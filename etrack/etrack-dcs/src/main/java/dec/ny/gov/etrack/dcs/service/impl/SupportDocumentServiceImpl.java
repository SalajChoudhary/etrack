/**
 * 
 */
package dec.ny.gov.etrack.dcs.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dcs.dao.DocumentNameRepo;
import dec.ny.gov.etrack.dcs.dao.SupportDocumentConfigRepo;
import dec.ny.gov.etrack.dcs.dao.SupportDocumentDAO;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocumentFileView;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SupportDocument;
import dec.ny.gov.etrack.dcs.model.SupportDocumentConfig;
import dec.ny.gov.etrack.dcs.model.SupportDocumentFile;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import dec.ny.gov.etrack.dcs.util.DCSServiceUtil;
import dec.ny.gov.etrack.dcs.util.DateUtil;
import dec.ny.gov.etrack.dcs.util.TransformationService;

/**
 * @author mxmahali
 *
 */
@Service
public class SupportDocumentServiceImpl implements SupportDocumentService {

  @Autowired
  private DCSServiceUtil dcsServiceUtil;
  @Autowired
  private SupportDocumentDAO supportDocumentDAO;
  @Autowired
  private SupportDocumentConfigRepo supportDocumentConfigRepo;

  @Value("${dms.retrieve.document.uri.path}")
  private String retrieveDocumentPath;
  @Value("${dms.update.document.metadata.uri.path}")
  private String updateMetadataPath;
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private TransformationService transformationService;

  private static final Logger logger =
      LoggerFactory.getLogger(SupportDocumentService.class.getName());

  private final SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");


  @Transactional
  @Override
  public IngestionResponse uploadAdditionalSupportDocument(String userId, String contextId,
      Long projectId, String authorization, IngestionRequest ingestionRequest,
      MultipartFile[] uploadFiles) {

    logger.info("Entering into uploadAdditionalSupportDocument. User id {}, Context Id {}", userId,
        contextId);

    Map<String, String> metadata = ingestionRequest.getMetaDataProperties();
    if (CollectionUtils.isEmpty(metadata) || uploadFiles == null || uploadFiles.length == 0) {
      logger.error(
          "Ingestion MetaData property is empty for the project Id {}, User id: {} Context Id: {}",
          projectId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    String documentName = metadata.get(DCSServiceConstants.DOCUMENT_TITLE);
    if (StringUtils.hasLength(documentName)) {
      List<Long> documentIds = supportDocumentDAO
          .findDocumentNameExistByProjectIdAndDocumentName(documentName, projectId);
      if (!CollectionUtils.isEmpty(documentIds)) {
        logger.error(
            "The document name is already taken for associated project Id: {} . User id: {}  context Id: {}.",
            projectId, userId, contextId);
        throw new ValidationException("DUP_DOC_NAME_NOT_ALLOWED",
            "Error : This Document Name already exists for this Project Id. Are you sure that you want to REPLACE it?");
      }
    }

    Long existingDocumentId = null;
    // String documentName = metadata.get(DCSServiceConstants.DOCUMENT_TITLE);
    String documentTitleIdStr = metadata.get(DCSServiceConstants.DOCUMENT_TITLE_ID);
    Integer documentTitleId = null;
    if (StringUtils.hasLength(documentTitleIdStr)) {
      documentTitleId = Integer.parseInt(documentTitleIdStr);
    }
    String docCategory = metadata.get(DCSServiceConstants.DOC_CATEGORY);
    String displayName = metadata.get(DCSServiceConstants.DISPLAY_NAME);

    if (StringUtils.hasLength(metadata.get(DCSServiceConstants.EXISTING_REF_DOCUMENT_ID))) {
      existingDocumentId = Long.valueOf(metadata.get(DCSServiceConstants.EXISTING_REF_DOCUMENT_ID));
    } else {
      logger.error("There is no reference Document id sent for additional document "
          + "User Id {} Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }


    if (StringUtils.isEmpty(documentTitleIdStr) && StringUtils.isEmpty(docCategory)) {
      logger.error("Both Document Title Id and Doc Type can not be blank  User Id {} Context Id {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    if (!dcsServiceUtil.isContainsValidFiles(uploadFiles, contextId)) {
      logger.error("Upload file(s) contains invalid extension User Id {} Context Id {} ", userId,
          contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_ZIP_FILE,
          DCSServiceConstants.INVALID_ZIP_FILE_MSG);
    }

    if (dcsServiceUtil.isNotValidFoilStatus(ingestionRequest, contextId)) {
      logger.error("Received NonReleaseCode when FOIL status as REL. User id: {}  context Id: {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    List<Long> documentIds = new ArrayList<>();
    documentIds.add(existingDocumentId);
    List<SupportDocument> supportDocuments =
        supportDocumentDAO.findAllByDocumentIdsAndProjectId(documentIds, projectId);

    if (CollectionUtils.isEmpty(supportDocuments)) {
      logger.error(
          "Additional Document is not allowed as it looks like new document. "
              + "No Documents found {} User id: {}  context Id: {}",
          existingDocumentId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    String supportDocumentClass = null;
    List<SupportDocumentConfig> supportConfigList = null;

    if (documentTitleId != null) {
      supportConfigList =
          supportDocumentConfigRepo.findSupportDocumentConfigBySubTypeTitleId(documentTitleId);
      if (CollectionUtils.isEmpty(supportConfigList)) {
        logger.error("There is no configuratuion associated data for this display name {}",
            displayName);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      SupportDocumentConfig config = supportConfigList.get(0);
      supportDocumentClass = config.getDocumentClassNm();
    } else if (StringUtils.hasLength(docCategory)) {
      List<String> documentClassList =
          supportDocumentConfigRepo.findDocumentClassByDocumentTypeId(Integer.valueOf(docCategory));

      if (CollectionUtils.isEmpty(documentClassList)) {
        logger.error(
            "There is no document class associated for this document type id {}, User Id {}, Context Id {} ",
            docCategory, userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      supportDocumentClass = documentClassList.get(0);
    } else {
      logger.error(
          "There is Document Sub Type Title Id or Document Category in the input. So, request cannot be "
              + "processed at this time, User Id {}, Context Id {} ",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    if (StringUtils.isEmpty(supportDocumentClass)
        || !(dcsServiceUtil.isDocumentClassValid(supportDocumentClass))) {
      logger.error(
          "The document class: {} is invalid. Exiting uploadDocument(). User id: {}  contextId: {}",
          supportDocumentClass, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    SupportDocument supportDocument = transformationService.getSupportDocument(ingestionRequest,
        userId, projectId, contextId, supportConfigList, new SupportDocument());

    if (StringUtils.isEmpty(docCategory)) {
      metadata.put(DCSServiceConstants.DOC_CATEGORY,
          String.valueOf(supportDocument.getDocumentTypeId()));
    }

    metadata.put(DCSServiceConstants.PROJECT_ID, projectId.toString());

    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY,
        String.valueOf(supportDocument.getDocumentSubTypeId()));
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    if (StringUtils.isEmpty(metadata.get(DCSServiceConstants.DOC_CREATION_TYPE))) {
      metadata.put(DCSServiceConstants.DOC_CREATION_TYPE, "Text");
    }
    Map<String, String> fileDates = ingestionRequest.getFileDates();
    if (CollectionUtils.isEmpty(fileDates)) {
      logger.error(
          "File Dates are missing in metadata map. Project Id:{} , user id: {} context Id: {}",
          projectId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    int fileDateIndex = 0;
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    Set<SupportDocumentFile> supportDocumentFiles = new HashSet<>();
    HttpHeaders attachFile = new HttpHeaders();
    if (uploadFiles != null && uploadFiles.length > 0) {
      ByteArrayResource resource = null;
      for (MultipartFile file : uploadFiles) {
        attachFile.setContentType(MediaType.parseMediaType(file.getContentType()));
        try {
          resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
        } catch (IOException e) {
          logger.error("Error while processing the file {}. File might have corrupted ",
              file.getOriginalFilename());
          throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
              DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
        }
        HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(resource, attachFile);
        filesAndMetadataMap.add("uploadDocuments", attachment);
        logger.debug("FileDate == {}, user id: {} context Id : {} ",
            fileDates.get(fileDateIndex + " "), userId, contextId);
        SupportDocumentFile docFile = new SupportDocumentFile();
        docFile.setCreatedById(userId);
        docFile.setFileName(file.getOriginalFilename());
        docFile
            .setFileDate(DateUtil.formatStringToDate(fileDates.get(String.valueOf(fileDateIndex))));
        docFile.setFileNumber(++fileDateIndex);
        docFile.setCreatedDate(new Date());
        docFile.setSupportDocument(supportDocument);
        supportDocumentFiles.add(docFile);
      }
    }
    supportDocument.setDocFiles(supportDocumentFiles);
    logger.info("Ingesting document for first time. User id: {} context id: {}", userId, contextId);
    supportDocument.setRefDocumentId(existingDocumentId);
    supportDocument.setAddlDocInd(1);
    supportDocument.setDocumentId(null);
    supportDocumentDAO.save(supportDocument);
    logger.info("Document initial request submitted successfully. User Id: {} context Id: {}",
        userId, contextId);
    Long documentId = supportDocument.getDocumentId();

    if (documentId == null || documentId == 0) {
      logger.error("Document Id is not generated correctly while persisting "
          + "initial data. User Id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    transformationService.getIngestReqForDocClass(ingestionRequest, supportDocumentClass,
        supportDocument.getDocumentId(), userId, contextId);
    // Get the document id
    try {
      metadata.remove(DCSServiceConstants.DISPLAY_NAME);
      metadata.remove(DCSServiceConstants.EXISTING_REF_DOCUMENT_ID);
      metadata.remove(DCSServiceConstants.CONFIDENTIAL);
      metadata.remove(DCSServiceConstants.DOCUMENT_TITLE_ID);
      ResponseEntity<IngestionResponse> ingestionResponseEntity =
          dcsServiceUtil.requestDMSServiceToUploadDocument(userId, contextId, authorization,
              supportDocumentClass, documentId, ingestionRequest, filesAndMetadataMap);

      if (!HttpStatus.CREATED.equals(ingestionResponseEntity.getStatusCode())) {
        logger.error("Received invalid status code from DMS . User Id {}, Context Id {}, Status {}",
            userId, contextId, ingestionResponseEntity.getStatusCode());
        throw new DcsException();
      }
      supportDocumentDAO.updateEcmaasGuidAndStatus(documentId,
          ingestionResponseEntity.getBody().getGuid(), DCSServiceConstants.ACTIVE);
      return ingestionResponseEntity.getBody();
    } catch (DcsException e) {
      logger.error("Update the error status for the document id {}. userId {}, Context Id {}",
          documentId, userId, contextId);
      supportDocumentDAO.updateEcmaasGuidAndStatus(documentId, null,
          DCSServiceConstants.ERROR_WHILE_UPLOADING);
      throw e;
    }
  }

  @Transactional(rollbackOn = {DcsException.class})
  @Override
  public IngestionResponse uploadSupportDocument(String userId, String contextId, Long projectId,
      String authorization, IngestionRequest ingestionRequest, 
      MultipartFile[] uploadFiles, final Integer docClassification) {

    logger.info("Entering into uploadSupportDocument. User id {}, Context Id {}", userId,
        contextId);

    if (CollectionUtils.isEmpty(ingestionRequest.getMetaDataProperties())) {
      logger.error(
          "Ingestion MetaData property is empty for the project Id {} , user id: {} context Id: {}",
          projectId, userId, contextId);
      throw new ValidationException("METADATA_PROPERTY_EMPTY",
          "Ingestion MetaData property is empty for the project Id " + projectId);
    }

    if (uploadFiles == null || uploadFiles.length == 0) {
      logger.error("Upload file is empty for the project Id {} , user id: {} context Id: {}",
          projectId, userId, contextId);
      throw new ValidationException("UPLOAD_FILE_EMPTY",
          "Upload file is empty for the project Id " + projectId);
    }

    String documentDisplayName =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DISPLAY_NAME);
    String documentTitleIdStr =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOCUMENT_TITLE_ID);
    String documentName =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOCUMENT_TITLE);
    String docCategory =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOC_CATEGORY);

    if (StringUtils.hasLength(documentName)) {
      logger.info("Checking whether this document Name {} is already exists in the Support Document. "
          + "Project Id {}, User Id {}, Context Id {}", documentName, projectId, userId, contextId);
      List<Long> documentIds = supportDocumentDAO
          .findDocumentNameExistByProjectIdAndDocumentName(documentName.toUpperCase(), projectId);
      
      if (CollectionUtils.isEmpty(documentIds)) {
        Long edbDistrictId = supportDocumentDAO.findDistrictIdByProjectId(projectId);
        if (edbDistrictId != null && edbDistrictId > 0) {
          logger.info("Checking whether this document Name {} is already exists in the Historical Document upload. "
              + "Project Id {}, User Id {}, Context Id {}", documentName, projectId, userId, contextId);
          documentIds = supportDocumentDAO.findDocumentNameExistByDistrictIdIdAndDocumentName(
              documentName.toUpperCase(), edbDistrictId);
        }
      }
      
      if (!CollectionUtils.isEmpty(documentIds)) {
        logger.error(
            "The document name {} is already taken for associated project Id: {} . User id: {}  context Id: {}.",
            documentName, projectId, userId, contextId);
        throw new ValidationException("DUP_DOC_NAME_NOT_ALLOWED",
            "Error : This Document Name " + documentName + " already exists for this DEC ID. "
                + "Are you sure that you want to REPLACE it?");
      }
    }
    if (StringUtils.isEmpty(docCategory) && StringUtils.isEmpty(documentTitleIdStr)) {
      logger.error("This request doesn't have either doc category {} " + "or document title id {}",
          docCategory, documentTitleIdStr);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    Integer documentTitleId =
        StringUtils.hasLength(documentTitleIdStr) ? Integer.parseInt(documentTitleIdStr) : null;
    Long documentId = null;
    // if (StringUtils.hasLength(documentTitleIdStr)) {
    // documentTitleId = Integer.valueOf(documentTitleIdStr);
    // documentId = supportDocumentDAO.findDocumentIdByProjectIdAndDocumentSubTypeTitleId(projectId,
    // documentTitleId);
    // }

    if (!(StringUtils.hasLength(documentDisplayName) || StringUtils.hasLength(documentName))) {
      logger.error("Document name or display name is blank  User Id {} Context Id {}", userId,
          contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    if (!dcsServiceUtil.isContainsValidFiles(uploadFiles, contextId)) {
      logger.error("Upload file(s) contains invalid extension User Id {} Context Id {} ", userId,
          contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_ZIP_FILE,
          DCSServiceConstants.INVALID_ZIP_FILE_MSG);
    }

    if (dcsServiceUtil.isNotValidFoilStatus(ingestionRequest, contextId)) {
      logger.error("Received NonReleaseCode when FOIL status as REL. User id: {}  context Id: {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    /*
     * This is commented to remove the restriction of adding the additional documents upload. if
     * (documentId != null) {
     * logger.error("Document Display name is already exists. User id: {}  context Id: {}", userId,
     * contextId); throw new ValidationException("DUP_DOC_NAME_NOT_ALLOWED",
     * "Error : This Document Name already exists for this DEC ID. Are you sure that you want to REPLACE it?"
     * ); }
     */

    List<SupportDocumentConfig> supportConfigList = null;
    String supportDocumentClass = null;
    // String documentCategoryId =
    // ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOC_CATEGORY);
    if (StringUtils.hasLength(docCategory)) {
      List<String> documentClassList =
          supportDocumentConfigRepo.findDocumentClassByDocumentTypeId(Integer.valueOf(docCategory));
      if (CollectionUtils.isEmpty(documentClassList)) {
        logger.error(
            "There is no document class associated for this document type id {}, User Id {}, Context Id {} ",
            docCategory, userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      supportDocumentClass = documentClassList.get(0);
    } else {
      supportConfigList =
          supportDocumentConfigRepo.findSupportDocumentConfigBySubTypeTitleId(documentTitleId);

      if (CollectionUtils.isEmpty(supportConfigList)) {
        logger.error(
            "There is no configuratuion associated data for this document sub type title id {}",
            documentTitleId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      SupportDocumentConfig config = supportConfigList.get(0);
      supportDocumentClass = config.getDocumentClassNm();
    }

    if (StringUtils.isEmpty(supportDocumentClass)
        || !(dcsServiceUtil.isDocumentClassValid(supportDocumentClass))) {
      logger.error(
          "The document class: {} is invalid. Exiting uploadDocument(). User id: {}  contextId: {}",
          supportDocumentClass, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    SupportDocument supportDocument = transformationService.getSupportDocument(ingestionRequest,
        userId, projectId, contextId, supportConfigList, new SupportDocument());
    supportDocument.setSupportDocCategoryCode(docClassification);
    if (documentTitleId != null) {
      supportDocument.setDocumentSubTypeTitleId(documentTitleId);
    }

    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_CATEGORY,
        String.valueOf(supportDocument.getDocumentTypeId()));
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.PROJECT_ID,
        projectId.toString());

    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_SUB_CATEGORY,
        String.valueOf(supportDocument.getDocumentSubTypeId()));
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_CREATION_TYPE, "Text");
    Map<String, String> fileDates = ingestionRequest.getFileDates();
    if (CollectionUtils.isEmpty(fileDates)) {
      logger.error(
          "File Dates are missing in metadata map. Project Id:{} , user id: {} context Id: {}",
          projectId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    int fileDateIndex = 0;
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    Set<SupportDocumentFile> supportDocumentFiles = new HashSet<>();
    HttpHeaders attachFile = new HttpHeaders();
    if (uploadFiles != null && uploadFiles.length > 0) {
      ByteArrayResource resource = null;
      for (MultipartFile file : uploadFiles) {
        attachFile.setContentType(MediaType.parseMediaType(file.getContentType()));
        try {
          resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
        } catch (IOException e) {
          logger.error("Error while processing the file {}. File might have corrupted ",
              file.getOriginalFilename());
          throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
              DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
        }
        HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(resource, attachFile);
        filesAndMetadataMap.add("uploadDocuments", attachment);
        logger.debug("FileDate == {}, user id: {} context Id : {} ",
            fileDates.get(fileDateIndex + " "), userId, contextId);
        SupportDocumentFile docFile = new SupportDocumentFile();
        docFile.setCreatedById(userId);
        docFile.setFileName(file.getOriginalFilename());
        docFile
            .setFileDate(DateUtil.formatStringToDate(fileDates.get(String.valueOf(fileDateIndex))));
        docFile.setFileNumber(++fileDateIndex);
        docFile.setCreatedDate(new Date());
        docFile.setSupportDocument(supportDocument);
        supportDocumentFiles.add(docFile);
      }
    }
    supportDocument.setDocFiles(supportDocumentFiles);
    logger.info("Ingesting document for first time. User id: {} context id: {}", userId, contextId);

    supportDocumentDAO.save(supportDocument);
    logger.info("Document initial request submitted successfully. User Id: {} context Id: {}",
        userId, contextId);
    documentId = supportDocument.getDocumentId();

    if (documentId == null || documentId == 0) {
      logger.error("Document Id is not generated correctly while persisting "
          + "initial data. User Id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    transformationService.getIngestReqForDocClass(ingestionRequest, supportDocumentClass,
        supportDocument.getDocumentId(), userId, contextId);
    // Get the document id
    try {
      ingestionRequest.getMetaDataProperties().remove(DCSServiceConstants.DISPLAY_NAME);
      ingestionRequest.getMetaDataProperties().remove(DCSServiceConstants.EXISTING_REF_DOCUMENT_ID);
      ingestionRequest.getMetaDataProperties().remove(DCSServiceConstants.CONFIDENTIAL);
      ingestionRequest.getMetaDataProperties().remove(DCSServiceConstants.DOCUMENT_TITLE_ID);
      ResponseEntity<IngestionResponse> ingestionResponseEntity =
          dcsServiceUtil.requestDMSServiceToUploadDocument(userId, contextId, authorization,
              supportDocumentClass, documentId, ingestionRequest, filesAndMetadataMap);

      if (!HttpStatus.CREATED.equals(ingestionResponseEntity.getStatusCode())) {
        logger.error("Received invalid status code from DMS . User Id {}, Context Id {}, Status {}",
            userId, contextId, ingestionResponseEntity.getStatusCode());
        throw new DcsException();
      }
      supportDocumentDAO.updateEcmaasGuidAndStatus(documentId,
          ingestionResponseEntity.getBody().getGuid(), DCSServiceConstants.ACTIVE);
      return ingestionResponseEntity.getBody();
    } catch (DcsException e) {
      logger.error("Update the error status for the document id {}. userId {}, Context Id {}",
          documentId, userId, contextId);
      supportDocumentDAO.updateEcmaasGuidAndStatus(documentId, null,
          DCSServiceConstants.ERROR_WHILE_UPLOADING);
      throw e;
    }
  }

  @Transactional
  @Override
  public ResponseEntity<Response> updateSupportDocumentMetadata(String userId, String contextId,
      String authorization, IngestionRequest request, final Long documentId) {

    logger.info("Entering into updateSupportDocumentMetadata. User Id {}, Context id {}", userId,
        contextId);
    SupportDocument supportDocument =
        supportDocumentDAO.findByDocumentIdAndDocumentStateCode(documentId, "A");
    if (supportDocument == null) {
      logger.error(
          "The document Id: {} has no Support documents related to it. user id: {} context Id : {}",
          documentId, userId, contextId);
      throw new DocumentNotFoundException(DCSServiceConstants.NO_DOCUMENT,
          DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    if (supportDocument.getRefDocumentId() != null && supportDocument.getRefDocumentId() > 0) {
      logger.info("This is reference document Metadata update. So, there is no update required "
          + "in the DMS level. User Id {}, Context Id{}", userId, contextId);

      supportDocumentDAO.updateDocumentDesc(supportDocument.getProjectId(),
          supportDocument.getDocumentId(), supportDocument.getDocumentDesc());
      return new ResponseEntity<Response>(HttpStatus.OK);
    }
    String docCategory = request.getMetaDataProperties().get(DCSServiceConstants.DOC_CATEGORY);
    if (!StringUtils.hasLength(docCategory)) {
      throw new ValidationException("NO_DOC_CATEGORY_FOUND",
          "There is no document category passed");
    }

    List<String> requestedDocumentClassList =
        supportDocumentConfigRepo.findDocumentClassByDocumentTypeId(Integer.valueOf(docCategory));
    if (CollectionUtils.isEmpty(requestedDocumentClassList)) {
      logger.error(
          "There is no document class associated for this document type id {}, User Id {}, Context Id {} ",
          docCategory, userId, contextId);
      throw new ValidationException("NO_DOCUMENT_CLASS",
          "There is no document class associated with this name " + docCategory);
    }

    List<String> originalDocumentClassList = supportDocumentConfigRepo
        .findDocumentClassByDocumentTypeId(supportDocument.getDocumentTypeId());

    String supportDocumentClass = requestedDocumentClassList.get(0);
    if (CollectionUtils.isEmpty(originalDocumentClassList)
        || !supportDocumentClass.equals(originalDocumentClassList.get(0))) {
      throw new ValidationException("DOC_CLASS_CHANGING",
          "Technically an existing Document Type cannot be changed, as a FileNet Document Class is changing. To work around this, "
              + "download all of the files for this Document Name, "
              + "create a new one with the correct Document Type, and delete the incorrect one.");
    }

    String documentTitle = request.getMetaDataProperties().get(DCSServiceConstants.DOCUMENT_TITLE);
    List<Long> documentIds =
        supportDocumentDAO.findDocumentNameExistByProjectIdAndDocumentNameAndDocumentId(
            documentTitle.toUpperCase(), supportDocument.getProjectId(), supportDocument.getDocumentId());

    if (CollectionUtils.isEmpty(documentIds)) {
      Long edbDistrictId = supportDocumentDAO.findDistrictIdByProjectId(supportDocument.getProjectId());
      if (edbDistrictId != null && edbDistrictId > 0) {
        documentIds = supportDocumentDAO.findDocumentNameExistByDistrictIdIdAndDocumentName(
            documentTitle.toUpperCase(), edbDistrictId);
      }
    }
    if (!CollectionUtils.isEmpty(documentIds)) {
      logger.error("Document name is already exists with this requested amended name "
          + "{} User Id {}, Context Id {} ", documentTitle, userId, contextId);
      throw new ValidationException("DOCUMENT_NAME_EXIST",
          "Document name is already associated with this project. Cannot be amended");
    }
    if (dcsServiceUtil.isNotValidFoilStatus(request, contextId)) {
      logger.error("Received NonReleaseCode when FOIL status as REL. User id: {}  context Id: {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    String clientId = dcsServiceUtil.retrieveClientId(supportDocumentClass);
    logger.info("Making call to DMS Service layer to update the Support document {}'s metadata. "
        + "User id: {}  context id: {}", documentId, userId, contextId);
    try {
      Integer documentTitleId = null;
      Date indexDate = java.util.Calendar.getInstance().getTime();
      String indexDateString = new SimpleDateFormat("yyyyMMddHHmmss").format(indexDate);
      logger.debug("Formatted index date is: {}. user id: {} context id: {}", indexDateString,
          userId, contextId);
      request.getMetaDataProperties().put(DCSServiceConstants.INDEX_DATE, indexDateString);
      List<SupportDocumentConfig> supportConfigList = null;
      // String documentCategoryId =
      // ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOC_CATEGORY);
      if (StringUtils.hasLength(docCategory)) {
        List<String> documentClassList = supportDocumentConfigRepo
            .findDocumentClassByDocumentTypeId(Integer.valueOf(docCategory));
        if (CollectionUtils.isEmpty(documentClassList)) {
          logger.error(
              "There is no document class associated for this document type id {}, User Id {}, Context Id {} ",
              docCategory, userId, contextId);
          throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
              DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
        }
        supportDocumentClass = documentClassList.get(0);
      } else {
        String documentTitleIdStr =
            request.getMetaDataProperties().get(DCSServiceConstants.DOCUMENT_TITLE_ID);
        if (!StringUtils.hasLength(documentTitleIdStr)) {
          throw new ValidationException("NO_DOC_CATG_OR_DOC_TITLE",
              "Neither Document Category nor Document title is passed");
        }
        documentTitleId = Integer.parseInt(documentTitleIdStr);
        supportConfigList =
            supportDocumentConfigRepo.findSupportDocumentConfigBySubTypeTitleId(documentTitleId);

        if (CollectionUtils.isEmpty(supportConfigList)) {
          logger.error(
              "There is no configuratuion associated data for this document sub type title id {}",
              documentTitleId);
          throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
              DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
        }
        SupportDocumentConfig config = supportConfigList.get(0);
        supportDocumentClass = config.getDocumentClassNm();
      }

      transformationService.getSupportDocument(request, userId, supportDocument.getProjectId(),
          contextId, supportConfigList, supportDocument);

      request.getMetaDataProperties().put(DCSServiceConstants.PROJECT_ID,
          supportDocument.getProjectId().toString());
      request.getMetaDataProperties().put(DCSServiceConstants.HISTORIC, "0");
      request.getMetaDataProperties().put(DCSServiceConstants.DOC_CREATION_TYPE, "Text");
      supportDocumentDAO.save(supportDocument);
    } catch (ValidationException ve) {
      throw ve;
    } catch (Exception e) {
      logger.error("Error while updating the metadata details "
          + "into Support Document before request DMS ", userId, contextId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }

    Map<String, String> ingestionMetaData = request.getMetaDataProperties();
    ingestionMetaData.remove(DCSServiceConstants.APPLICATION_ID);
    ingestionMetaData.remove(DCSServiceConstants.PERMIT_TYPE);
    ingestionMetaData.remove(DCSServiceConstants.MODIFICATION_NUMBER);
    ingestionMetaData.remove(DCSServiceConstants.RENEWAL_NUMBER);
    ingestionMetaData.remove(DCSServiceConstants.TRACKED_APP_ID);
    ingestionMetaData.remove(DCSServiceConstants.OTHER_SUB_CAT_TEXT);
    ingestionMetaData.remove(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
    
    MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
    headersMap.add(DCSServiceConstants.CLIENT_ID, clientId);
    headersMap.add(DCSServiceConstants.USER_ID, userId);
    headersMap.add(DCSServiceConstants.CONTEXT_ID, contextId);
    headersMap.add(HttpHeaders.AUTHORIZATION, authorization);
    String guid = dcsServiceUtil.removeBracketsFromGUID(supportDocument.getEcmaasGuid(), contextId);
    HttpEntity<IngestionRequest> httpEntity = new HttpEntity<>(request, headersMap);
    String uri =
        UriComponentsBuilder.newInstance().pathSegment(updateMetadataPath, guid).build().toString();

    ResponseEntity<Response> updateResponseEntity = null;
    try {
      logger.info("Making call to DMS to update document id: {} . user id: {} context Id: {}",
          documentId, userId, contextId);
      updateResponseEntity = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Response.class);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error("Received unsuccessful {} response from DMS while updating the "
          + "MetaData. User Id {}, Context Id {}", e.getStatusCode(), userId, contextId);
      Response response = new Response();
      response.setErrorCode("UPLOAD_DMS_METADTA_ERROR");
      response.setErrorCode(e.getResponseBodyAsString());
      return new ResponseEntity<Response>(response, e.getStatusCode());
    } catch (Exception e) {
      logger.error("General Error occurred while updating the Metadata. User Id {}, Context Id {}",
          userId, contextId, e);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
    return updateResponseEntity;
  }


  @Transactional
  @Override
  public void createReferenceForExistingDocument(final String userId, final String contextId,
      final Long projectId, final DocumentNameView reference) {
    logger.info("Entering into createReferenceForExistingDocument. User Id {}, Context Id {}",
        userId, contextId);
    Long existingDocumentId = reference.getDocumentId();
    String displayName = reference.getDisplayName();
    String referenceDescription = reference.getReferenceText();

    logger.debug("{} - {} - {} - {}", existingDocumentId, displayName, referenceDescription,
        reference.getRefDisplayName());

    if (existingDocumentId == null || existingDocumentId <= 0 || StringUtils.isEmpty(displayName)
        || StringUtils.isEmpty(referenceDescription)) {

      logger.error("Invalid request is passed . User Id {} , context Id {}, Object {}", userId,
          contextId, reference);
      throw new ValidationException("DOC_MISSING",
          "One or more mandatory value is missing in the reference document");
    }

    List<SupportDocument> supportDocumentList = supportDocumentDAO
        .findByDocumentIdAndProjectIdAndRefDocumentId(projectId, existingDocumentId);

    if (CollectionUtils.isEmpty(supportDocumentList)) {
      logger.error(
          "There is no document associated with this existing document reference. "
              + "Project id {}, Existing docuemnt Id {}, User id {}, Context Id {}",
          projectId, existingDocumentId, userId, contextId);
      throw new ValidationException("NO_REF_DOCUMENT",
          "There is no existing reference document for the document id " + existingDocumentId);
    }
    SupportDocument existingDocument = supportDocumentList.get(0);
    SupportDocument newSupportDocument = new SupportDocument();
    if (StringUtils.hasLength(displayName)) {
      List<String> documentTypeAndSubTypes =
          supportDocumentDAO.findDocumentTypeAndSubTypeByDisplayName(displayName);
      if (!CollectionUtils.isEmpty(documentTypeAndSubTypes)) {
        String[] documentTypeAndSubType = documentTypeAndSubTypes.get(0).split(",");;
        newSupportDocument.setDocumentTypeId(Integer.parseInt(documentTypeAndSubType[0]));
        newSupportDocument.setDocumentSubTypeId(Integer.parseInt(documentTypeAndSubType[1]));
      }
    } else {
      newSupportDocument.setDocumentTypeId(existingDocument.getDocumentTypeId());
      newSupportDocument.setDocumentSubTypeId(existingDocument.getDocumentSubTypeId());
    }
    newSupportDocument.setEcmaasGuid(existingDocument.getEcmaasGuid());
    newSupportDocument.setAccessByDepOnlyInd(existingDocument.getAccessByDepOnlyInd());
    newSupportDocument.setDocReleasableCode(existingDocument.getDocReleasableCode());
    newSupportDocument.setDocumentStateCode(existingDocument.getDocumentStateCode());
    newSupportDocument
        .setDocumentDesc(existingDocument.getDocumentNm() + ": " + referenceDescription);
    newSupportDocument
        .setRefDocumentDesc(existingDocument.getDocumentNm() + ": " + referenceDescription);
    if (reference.getDocumentTitleId() != null && reference.getDocumentTitleId() > 0) {
      newSupportDocument.setDocumentSubTypeTitleId(reference.getDocumentTitleId());
    } else {
      newSupportDocument.setDocumentSubTypeTitleId(existingDocument.getDocumentSubTypeTitleId());
    }
    newSupportDocument.setProjectId(projectId);
    newSupportDocument.setRefDocumentId(existingDocumentId);
    newSupportDocument.setCreatedById(userId);
    newSupportDocument.setCreateDate(new Date());
    newSupportDocument.setDocumentNm(displayName);
    supportDocumentDAO.save(newSupportDocument);
  }

  @Autowired
  private DocumentNameRepo documentNameRepo;

  @Override
  public List<DocumentName> retrieveAllDisplayNames(final String userId, final String contextId,
      final Long projectId) {
    List<DocumentName> displayNamesList =
        documentNameRepo.findAllByProjectIdAndRefDocumentIdIsNotNull(projectId);
    if (CollectionUtils.isEmpty(displayNamesList)) {
      displayNamesList = new ArrayList<>();
    }
    return displayNamesList;
  }


  @Transactional
  @Override
  public List<DocumentFileView> retrieveAllFilesAssociatedWithDocumentId(final String userId,
      final String contextId, final Long projectId, final List<Long> documentId) {
    List<Long> documentIds = new ArrayList<>();
    // documentIds.add(documentId);
    List<SupportDocument> supportDocumentList =
        supportDocumentDAO.findAllByDocumentIdsAndProjectId(documentIds, projectId);
    List<DocumentFileView> documentFileList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(supportDocumentList)
        && !CollectionUtils.isEmpty(supportDocumentList.get(0).getDocFiles())) {
      supportDocumentList.get(0).getDocFiles().forEach(supportDocumentFile -> {
        DocumentFileView documentFileView = new DocumentFileView();
        documentFileView.setDocumentFileId(supportDocumentFile.getDocumentFileId());
        documentFileView.setDocumentId(supportDocumentFile.getDocumentId());
        documentFileView.setFileName(supportDocumentFile.getFileName());
        documentFileView.setFileDate(formatDate.format(supportDocumentFile.getFileDate()));
        documentFileList.add(documentFileView);
      });
    }
    return documentFileList;
  }


  @Transactional(rollbackOn = {DcsException.class})
  @Override
  public ResponseEntity<Response> deleteDocument(String userId, String contextId, Long projectId,
      String authorization, List<Long> documentIds) {

    logger.info("Entering into deleteSupportDocument. User Id {}, Context Id {}", userId,
        contextId);
    List<String> documentClassList =
        supportDocumentDAO.findDocumentClassByDocumentIdAndProjectId(documentIds.get(0), projectId);

    if (CollectionUtils.isEmpty(documentClassList)) {
      logger.error("There is no document class associated for the input document Ids {}",
          documentIds);
      throw new ValidationException(DCSServiceConstants.NO_DOCUMENT,
          DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClassList.get(0));

    List<SupportDocument> supportDocuments =
        supportDocumentDAO.findAllDocumentsAndRefByDocumentIdsAndProjectId(documentIds, projectId);

    if (CollectionUtils.isEmpty(supportDocuments)) {
      logger.error("There is no document associated with this document Id {}", documentIds);
      throw new ValidationException(DCSServiceConstants.NO_DOCUMENT,
          DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    ResponseEntity<Response> deleteResponse = null;
    for (SupportDocument document : supportDocuments) {
      Long documentId = document.getDocumentId();
      try {
        if (CollectionUtils.isEmpty(document.getDocFiles())) {
          logger.info(
              "This document Id {} has the reference {}. "
                  + "Original document {} cannot be delete. so, removing the reference ",
              documentId, document.getRefDocumentId());
          supportDocumentDAO.updateStateCode(documentId, userId, new Date());
          deleteResponse = new ResponseEntity<Response>(HttpStatus.OK);
        } else {
          deleteResponse = dcsServiceUtil.deleteDocuments(String.valueOf(document.getDocumentId()),
              document.getEcmaasGuid(), userId, authorization, clientId, contextId);
          if (HttpStatus.OK.equals(deleteResponse.getStatusCode())) {
            logger.info(
                "Updating document: {} state code after delete from ECMaaS. user id: {} context id: {}",
                documentId, userId, contextId);
            supportDocumentDAO.updateStateCode(documentId, userId, new Date());
            logger.info("Update successful for document Id {} . user id: {} context id: {}",
                documentId, userId, contextId);
            logger.info(
                "Exiting from  deleteDocument Response code is: {} user id: {} context Id: {}",
                deleteResponse.getStatusCode(), userId, contextId);
            logger.info("Existing from deleteSupportDocument. User Id {}, Context Id {}", userId,
                contextId);
          } else {
            logger.error("Error while deleting the document Id {}, Received Invalid Status code {}",
                documentId, deleteResponse.getStatusCode());
            throw new ValidationException("FAILED_TO_DELETE",
                "Unable to delete the document from ECMaas. Document Id " + documentId);
          }
        }
      } catch (ValidationException ve) {
        throw ve;
      } catch (Exception e) {
        logger.error("Error while updating document's state code Document id: {}, user "
            + "Id {}, context Id {} ", documentId, userId, contextId, e);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.", e);
      }
    }
    return deleteResponse;
  }

  @Transactional
  @Override
  public List<DocumentNameView> retrieveAllDocumentsAndFilesAssociatedWithDocumentId(
      final String userId, final String contextId, final Long projectId, final Long documentId) {

    // List<Long> documentIds = new ArrayList<>();
    // documentIds.add(documentId);
    logger.info("Retrieve all the Documents and References associated with this "
        + "document Id {}, User Id {}, Context Id {}", documentId, userId, contextId);
    List<SupportDocument> supportDocumentList =
        supportDocumentDAO.findAllDocumentsByDocumentIdsAndProjectId(documentId, projectId);

    if (CollectionUtils.isEmpty(supportDocumentList)) {
      logger.error(
          "There is no document associated with this document Id {}, " + "User Id, Context Id {}",
          documentId, userId, contextId);
      throw new DocumentNotFoundException("NO_DOCUMENT_EXIST", "There is no document exist");
    }

    List<DocumentNameView> documentNamesList = new ArrayList<>();

    if (!CollectionUtils.isEmpty(supportDocumentList.get(0).getDocFiles())) {
      logger.info(
          "Process the Original document Id passed as an input parameter. User Id {} , Context Id {}",
          userId, contextId);
    } else if (CollectionUtils.isEmpty(supportDocumentList.get(0).getDocFiles())
        && supportDocumentList.get(0).getRefDocumentId() != null) {
      logger.info("This document details would have "
          + "referenced in another document. User Id {}, Context Id {}", userId, contextId);
      Optional<SupportDocument> supportDocumentAvailability = supportDocumentDAO
          .findByIdAndProjectId(supportDocumentList.get(0).getRefDocumentId(), projectId);

      if (supportDocumentAvailability.isPresent()) {
        SupportDocument refSupportDocument = supportDocumentAvailability.get();
        // refSupportDocument.setDocumentId(documentId);
        supportDocumentList.remove(0);
        supportDocumentList.add(0, refSupportDocument);
      } else {
        logger.error("There is no refernece document also associated with this document Id {}, "
            + "User Id, Context Id {}", documentId, userId, contextId);
        throw new DocumentNotFoundException("NO_DOCUMENT_EXIST", "There is no document exist");
      }
    } else {
      logger.error(
          "There is no document associated with this document Id {}, " + "User Id, Context Id {}",
          documentId, userId, contextId);
      throw new DocumentNotFoundException("NO_DOCUMENT_EXIST", "There is no document exist");
    }
    supportDocumentList.forEach(supportDocument -> {
      DocumentNameView documentDetail = new DocumentNameView();
      List<DocumentFileView> documentFileList = new ArrayList<>();
      documentDetail.setDisplayName(supportDocument.getDocumentNm());
      documentDetail.setDocumentId(supportDocument.getDocumentId());
      documentDetail.setDocCategory(supportDocument.getDocumentTypeId());
      documentDetail.setDocSubCategory(supportDocument.getDocumentSubTypeId());
      for (SupportDocumentFile supportDocumentFile : supportDocument.getDocFiles()) {
        DocumentFileView documentFileView = new DocumentFileView();
        documentFileView.setDocumentFileId(supportDocumentFile.getDocumentFileId());
        documentFileView.setDocumentId(supportDocument.getDocumentId());
        documentFileView.setFileName(supportDocumentFile.getFileName());
        documentFileView.setFileDate(formatDate.format(supportDocumentFile.getFileDate()));
        documentFileList.add(documentFileView);
      }
      documentDetail.setFiles(documentFileList);
      documentNamesList.add(documentDetail);
    });
    documentNamesList.get(0).setDocumentId(documentId);
    return documentNamesList;
  }


  @Transactional
  @Override
  public ResponseEntity<byte[]> retrieveFileContent(String userId, String contextId,
      String authorization, Long projectId, Long documentId, String fileName) {

    List<String> documentClassName =
        supportDocumentDAO.findDocumentClassByDocumentIdAndProjectId(documentId, projectId);
    if (CollectionUtils.isEmpty(documentClassName)) {
      throw new ValidationException("DOC_CLASS_MAPPING_MISSING",
          "There is no Document Category mapoing for this document Id " +documentId + " Project Id "+ projectId);
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClassName.get(0));
    SupportDocument supportDocument =
        supportDocumentDAO.findByDocumentIdAndDocumentStateCode(documentId, "A");

    if (supportDocument == null) {
      logger.error("There is no support document associated with this document id {}", documentId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          "Error : Invalid Request.");
    }

    String guid = dcsServiceUtil.removeBracketsFromGUID(supportDocument.getEcmaasGuid(), contextId);

    logger.debug(
        "Making call to service layer to retrieve document:{} . User id: {} Context id: {}",
        documentId, userId, contextId);

    MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
    headersMap.add(DCSServiceConstants.GUID, guid);
    headersMap.add(DCSServiceConstants.DOCUMENT_ID, String.valueOf(documentId));
    headersMap.add(DCSServiceConstants.USER_ID, userId);
    headersMap.add(DCSServiceConstants.CLIENT_ID, clientId);
    headersMap.add(DCSServiceConstants.CONTEXT_ID, contextId);

    headersMap.add(HttpHeaders.AUTHORIZATION, authorization);
    HttpEntity<String> httpEntity = new HttpEntity<>(headersMap);
    String uri = UriComponentsBuilder.newInstance()
        .pathSegment(retrieveDocumentPath, guid, fileName).build().toUriString();
    logger.debug("URI Value with file name {} ", uri);;
    ResponseEntity<byte[]> retrieveResponseEntity = null;
    try {
      logger.info(
          "Making call to DMS to retrieve file content {} from the document id:{}. user id: {}. Context Id: {}",
          fileName, documentId, userId, contextId);
      retrieveResponseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, byte[].class);
      logger.info(
          "Exiting from retrieve file content . Response code is: {}, user id: {} context Id: {}",
          retrieveResponseEntity.getStatusCode(), userId, contextId);;
      return retrieveResponseEntity;
    } catch (Exception e) {
      logger.error("Error making call to DMS to retrieve file content "
          + "Document id: {}. User Id {}, Context Id {}", documentId, userId, contextId, e);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }
  }
}
