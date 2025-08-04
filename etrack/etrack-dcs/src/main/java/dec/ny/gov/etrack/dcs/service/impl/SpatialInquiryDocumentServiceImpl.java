package dec.ny.gov.etrack.dcs.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dcs.dao.DocumentNameRepo;
import dec.ny.gov.etrack.dcs.dao.SpatialInquiryDocumentDAO;
import dec.ny.gov.etrack.dcs.dao.SpatialInquiryDocumentRepo;
import dec.ny.gov.etrack.dcs.dao.SupportDocumentConfigRepo;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocumentFileView;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SpatialInqDocument;
import dec.ny.gov.etrack.dcs.model.SpatialInquiryDocument;
import dec.ny.gov.etrack.dcs.model.SpatialInquiryFile;
import dec.ny.gov.etrack.dcs.model.SpatialInquirySupportDocument;
import dec.ny.gov.etrack.dcs.model.SupportDocumentConfig;
import dec.ny.gov.etrack.dcs.service.SpatialInquiryDocumentService;
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
public class SpatialInquiryDocumentServiceImpl implements SpatialInquiryDocumentService {

  @Autowired
  private DCSServiceUtil dcsServiceUtil;

  @Autowired
  private SpatialInquiryDocumentRepo spatialInquiryDocumentRepo;

  @Autowired
  private SpatialInquiryDocumentDAO spatialInquiryDocumentDAO;

  @Autowired
  private SupportDocumentConfigRepo supportDocumentConfigRepo;

  @Value("${dms.retrieve.document.uri.path}")
  private String retrieveDocumentPath;
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private TransformationService transformationService;

  private static final Logger logger =
      LoggerFactory.getLogger(SupportDocumentService.class.getName());

  private final SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");


  @Transactional
  @Override
  public IngestionResponse uploadAdditionalSpatialDocument(String userId, String contextId,
      Long inquiryId, String authorization, IngestionRequest ingestionRequest,
      MultipartFile[] uploadFiles) {

    logger.info("Entering into uploadAdditionalSpatialDocument. User id {}, Context Id {}", userId,
        contextId);

    Map<String, String> metadata = ingestionRequest.getMetaDataProperties();
    if (CollectionUtils.isEmpty(metadata) || uploadFiles == null || uploadFiles.length == 0) {
      logger.error(
          "Ingestion MetaData property is empty for the Inquiry Id {} , user id: {} context Id: {}",
          inquiryId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    String documentName = metadata.get(DCSServiceConstants.DOCUMENT_TITLE);
    if (StringUtils.hasLength(documentName)) {
      List<Long> documentIds = spatialInquiryDocumentRepo
          .findDocumentNameExistByInquiryIdAndDocumentName(documentName, inquiryId);
      if (!CollectionUtils.isEmpty(documentIds)) {
        logger.error(
            "The document name is already taken for associated Inquiry Id: {} . User id: {}  context Id: {}.",
            inquiryId, userId, contextId);
        throw new ValidationException("DOC_NAME_DUP_REPLACE_MSG",
            "Error : This Document Name already exists for this DEC ID. Are you sure that you want to REPLACE it?");
      }
    }

    Long existingDocumentId = null;
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
    List<SpatialInquiryDocument> spatialInquiryDocuments =
        spatialInquiryDocumentRepo.findAllByDocumentIdsAndInquiryId(documentIds, inquiryId);

    if (CollectionUtils.isEmpty(spatialInquiryDocuments)) {
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

    SpatialInquiryDocument spatialInquiryDocument =
        transformationService.transformMetadataToSpatialInquiryDocument(ingestionRequest, userId,
            inquiryId, contextId, supportConfigList);

    if (StringUtils.isEmpty(docCategory)) {
      metadata.put(DCSServiceConstants.DOC_CATEGORY,
          String.valueOf(spatialInquiryDocument.getDocumentTypeId()));
    }

    metadata.put(DCSServiceConstants.PROJECT_ID, inquiryId.toString());

    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY,
        String.valueOf(spatialInquiryDocument.getDocumentSubTypeId()));
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    if (StringUtils.isEmpty(metadata.get(DCSServiceConstants.DOC_CREATION_TYPE))) {
      metadata.put(DCSServiceConstants.DOC_CREATION_TYPE, "Text");
    }
    Map<String, String> fileDates = ingestionRequest.getFileDates();
    if (CollectionUtils.isEmpty(fileDates)) {
      logger.error(
          "File Dates are missing in metadata map. Inquiry Id:{} , user id: {} context Id: {}",
          inquiryId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    int fileDateIndex = 0;
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    Set<SpatialInquiryFile> spatialInquiryFiles = new HashSet<>();
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
        SpatialInquiryFile docFile = new SpatialInquiryFile();
        docFile.setCreatedById(userId);
        docFile.setFileName(file.getOriginalFilename());
        docFile
            .setFileDate(DateUtil.formatStringToDate(fileDates.get(String.valueOf(fileDateIndex))));
        docFile.setFileNumber(++fileDateIndex);
        docFile.setCreatedDate(new Date());
        docFile.setSpatialInqDocument(spatialInquiryDocument);
        spatialInquiryFiles.add(docFile);
      }
    }
    spatialInquiryDocument.setSpatialInquiryFiles(spatialInquiryFiles);
    logger.info("Ingesting document for first time. User id: {} context id: {}", userId, contextId);
    spatialInquiryDocument.setRefDocumentId(existingDocumentId);
    spatialInquiryDocument.setAddlDocInd(1);
    spatialInquiryDocument.setDocumentId(null);
    spatialInquiryDocumentRepo.save(spatialInquiryDocument);
    logger.info("Document initial request submitted successfully. User Id: {} context Id: {}",
        userId, contextId);
    Long documentId = spatialInquiryDocument.getDocumentId();

    if (documentId == null || documentId == 0) {
      logger.error("Document Id is not generated correctly while persisting "
          + "initial data. User Id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    transformationService.getIngestReqForDocClass(ingestionRequest, supportDocumentClass,
        spatialInquiryDocument.getDocumentId(), userId, contextId);
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
      spatialInquiryDocumentRepo.updateEcmaasGuidAndStatus(documentId,
          ingestionResponseEntity.getBody().getGuid(), DCSServiceConstants.ACTIVE);
      ingestionResponseEntity.getBody().getIngestionRequest().getMetaDataProperties()
          .remove(DCSServiceConstants.PROJECT_ID);
      ingestionResponseEntity.getBody().getIngestionRequest().getMetaDataProperties()
          .put(DCSServiceConstants.INQUIRY_ID, String.valueOf(inquiryId));
      return ingestionResponseEntity.getBody();
    } catch (DcsException e) {
      logger.error("Update the error status for the document id {}. userId {}, Context Id {}",
          documentId, userId, contextId);
      spatialInquiryDocumentRepo.updateEcmaasGuidAndStatus(documentId, null,
          DCSServiceConstants.ERROR_WHILE_UPLOADING);
      throw e;
    }
  }



  @Transactional(rollbackOn = {DcsException.class})
  @Override
  public IngestionResponse uploadSpatialInquiryDocument(String userId, String contextId,
      Long inquiryId, String authorization, IngestionRequest ingestionRequest,
      MultipartFile[] uploadFiles, final boolean docReplaceInd) {

    logger.info("Entering into uploadSpatialInquiryDocument. User id {}, Context Id {}", userId,
        contextId);

    if (CollectionUtils.isEmpty(ingestionRequest.getMetaDataProperties()) || uploadFiles == null
        || uploadFiles.length == 0) {
      logger.error(
          "Ingestion MetaData property is empty for the Inquiry Id {} , user id: {} context Id: {}",
          inquiryId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
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
      List<Long> documentIds = spatialInquiryDocumentRepo
          .findDocumentNameExistByInquiryIdAndDocumentName(documentName, inquiryId);
      if (!CollectionUtils.isEmpty(documentIds)) {
        logger.error(
            "The document name is already taken for associated Inquiry Id: {} . User id: {}  context Id: {}.",
            inquiryId, userId, contextId);
        throw new ValidationException("DOC_NAME_DUP_REPLACE_MSG",
            "Error : This Document Name already exists for this DEC ID. Are you sure that you want to REPLACE it?");
      }
    }

    if (StringUtils.isEmpty(docCategory) && StringUtils.isEmpty(documentTitleIdStr)) {
      logger.error("This request doesn't have either doc category {} " + "or document title id {}",
          docCategory, documentTitleIdStr);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    Integer documentTitleId = null;
    Long documentId = null;
    if (StringUtils.hasLength(documentTitleIdStr)) {
      documentTitleId = Integer.valueOf(documentTitleIdStr);
      documentId = spatialInquiryDocumentRepo
          .findDocumentIdByInquiryIdAndDocumentSubTypeTitleId(inquiryId, documentTitleId);
    }

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

    // if (documentId == null) {
    // documentId = spatialInquiryDocumentDAO.findDocumentIdByinquiryIdAndDocumentNm(inquiryId,
    // documentName.toUpperCase());
    // if (documentId == null && StringUtils.hasLength(documentDisplayName)) {
    // documentId = spatialInquiryDocumentDAO.findDocumentIdByinquiryIdAndDocumentNm(inquiryId,
    // documentDisplayName.toUpperCase());
    // }
    // }

    if (docReplaceInd) {
      if (documentId == null) {
        logger.error(
            "There is no existing document. So, document cannot be replaced. User id: {}  context Id: {}",
            userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      } else {
        logger.info("Requesting to delete the existing document {} , User Id {} Context Id ",
            documentId, userId, contextId);
        List<Long> documentIds = new ArrayList<>();
        documentIds.add(documentId);
        deleteDocument(userId, contextId, inquiryId, authorization, documentIds);
      }
    } else if (documentId != null) {
      logger.error("Document Display name is already exists. User id: {}  context Id: {}", userId,
          contextId);
      throw new ValidationException("DOC_NAME_DUP_REPLACE_MSG",
          "Error : This Document Name already exists for this DEC ID. Are you sure that you want to REPLACE it?");
    }


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

    SpatialInquiryDocument spatialInquiryDocument =
        transformationService.transformMetadataToSpatialInquiryDocument(ingestionRequest, userId,
            inquiryId, contextId, supportConfigList);

    if (documentTitleId != null) {
      spatialInquiryDocument.setDocumentSubTypeTitleId(documentTitleId);
    }

    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_CATEGORY,
        String.valueOf(spatialInquiryDocument.getDocumentTypeId()));
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.PROJECT_ID,
        inquiryId.toString());

    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_SUB_CATEGORY,
        String.valueOf(spatialInquiryDocument.getDocumentSubTypeId()));
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.getMetaDataProperties().put(DCSServiceConstants.DOC_CREATION_TYPE, "Text");
    Map<String, String> fileDates = ingestionRequest.getFileDates();
    if (CollectionUtils.isEmpty(fileDates)) {
      logger.error(
          "File Dates are missing in metadata map. Inquiry Id:{} , user id: {} context Id: {}",
          inquiryId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    int fileDateIndex = 0;
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    Set<SpatialInquiryFile> spatialInquiryFiles = new HashSet<>();
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
        SpatialInquiryFile docFile = new SpatialInquiryFile();
        docFile.setCreatedById(userId);
        docFile.setFileName(file.getOriginalFilename());
        docFile
            .setFileDate(DateUtil.formatStringToDate(fileDates.get(String.valueOf(fileDateIndex))));
        docFile.setFileNumber(++fileDateIndex);
        docFile.setCreatedDate(new Date());
        docFile.setSpatialInqDocument(spatialInquiryDocument);
        spatialInquiryFiles.add(docFile);
      }
    }
    spatialInquiryDocument.setSpatialInquiryFiles(spatialInquiryFiles);
    logger.info("Ingesting document for first time. User id: {} context id: {}", userId, contextId);

    spatialInquiryDocumentRepo.save(spatialInquiryDocument);
    logger.info("Document initial request submitted successfully. User Id: {} context Id: {}",
        userId, contextId);
    documentId = spatialInquiryDocument.getDocumentId();

    if (documentId == null || documentId == 0) {
      logger.error("Document Id is not generated correctly while persisting "
          + "initial data. User Id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    transformationService.getIngestReqForDocClass(ingestionRequest, supportDocumentClass,
        spatialInquiryDocument.getDocumentId(), userId, contextId);
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
      spatialInquiryDocumentRepo.updateEcmaasGuidAndStatus(documentId,
          ingestionResponseEntity.getBody().getGuid(), DCSServiceConstants.ACTIVE);
      ingestionResponseEntity.getBody().getIngestionRequest().getMetaDataProperties()
          .remove(DCSServiceConstants.PROJECT_ID);
      ingestionResponseEntity.getBody().getIngestionRequest().getMetaDataProperties()
          .put(DCSServiceConstants.INQUIRY_ID, String.valueOf(inquiryId));
      return ingestionResponseEntity.getBody();
    } catch (DcsException e) {
      logger.error("Update the error status for the document id {}. userId {}, Context Id {}",
          documentId, userId, contextId);
      spatialInquiryDocumentRepo.updateEcmaasGuidAndStatus(documentId, null,
          DCSServiceConstants.ERROR_WHILE_UPLOADING);
      throw e;
    }
  }


  @Transactional
  @Override
  public void createReferenceForExistingDocument(final String userId, final String contextId,
      final Long inquiryId, final DocumentNameView reference) {
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
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    /*
     * if (existingDocumentId == null || existingDocumentId <= 0 || StringUtils.isEmpty(displayName)
     * || StringUtils.isEmpty(referenceDescription) ||
     * StringUtils.isEmpty(reference.getRefDisplayName()) || reference.getRefSupportDocRefId() ==
     * null || reference.getRefSupportDocRefId() <= 0) {
     * 
     * logger.error("Invalid request is passed . User Id {} , context Id {}, Object {}", userId,
     * contextId, reference); throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
     * DCSServiceConstants.INVALID_REQUEST_ERR_MSG); }
     */

    List<SpatialInquiryDocument> spatialInquiryDocuments = spatialInquiryDocumentRepo
        .findByDocumentIdAndInquiryIdAndRefDocumentId(inquiryId, existingDocumentId);

    if (CollectionUtils.isEmpty(spatialInquiryDocuments)) {
      logger.error(
          "There is no document associated with this existing document reference. Inquiry id {}, Existing docuemnt Id {}, User id {}, Context Id {}",
          inquiryId, existingDocumentId, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    SpatialInquiryDocument existingSpatialInquiryDocument = spatialInquiryDocuments.get(0);

    // List<String> documentClassNameList =
    // supportDocumentConfigRepo.findDocumentClassByDocumentTypeId(existingDocument.getDocumentTypeId());;
    // if (CollectionUtils.isEmpty(documentClassNameList)) {
    // logger.error(
    // "There is no document configuration associated with this document reference. Inquiry id {}, "
    // + "Existing document Id {}, User id {}, Context Id {}",
    // inquiryId, existingDocumentId, userId, contextId);
    // throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
    // DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    // }

    // SupportDocument supportDocument = supportDocumentList.get(0);
    // SupportDocumentConfig documentConfig = documentNamesList.get(0);

    SpatialInquiryDocument newSpatialInquiryDocument = new SpatialInquiryDocument();
    newSpatialInquiryDocument.setEcmaasGuid(existingSpatialInquiryDocument.getEcmaasGuid());
    newSpatialInquiryDocument
        .setAccessByDepOnlyInd(existingSpatialInquiryDocument.getAccessByDepOnlyInd());
    newSpatialInquiryDocument
        .setDocReleasableCode(existingSpatialInquiryDocument.getDocReleasableCode());
    newSpatialInquiryDocument.setDocumentTypeId(existingSpatialInquiryDocument.getDocumentTypeId());
    newSpatialInquiryDocument
        .setDocumentSubTypeId(existingSpatialInquiryDocument.getDocumentSubTypeId());
    newSpatialInquiryDocument
        .setDocumentStateCode(existingSpatialInquiryDocument.getDocumentStateCode());
    newSpatialInquiryDocument.setRefDocumentDesc(referenceDescription);
    if (reference.getDocumentTitleId() != null && reference.getDocumentTitleId() > 0) {
      newSpatialInquiryDocument.setDocumentSubTypeTitleId(reference.getDocumentTitleId());
    } else {
      newSpatialInquiryDocument
          .setDocumentSubTypeTitleId(existingSpatialInquiryDocument.getDocumentSubTypeTitleId());
    }
    newSpatialInquiryDocument.setInquiryId(inquiryId);
    newSpatialInquiryDocument.setRefDocumentId(existingDocumentId);
    newSpatialInquiryDocument.setCreatedById(userId);
    newSpatialInquiryDocument.setCreateDate(new Date());
    newSpatialInquiryDocument.setDocumentNm(displayName);
    spatialInquiryDocumentRepo.save(newSpatialInquiryDocument);
  }

  @Autowired
  private DocumentNameRepo documentNameRepo;

  @Override
  public List<DocumentName> retrieveAllDisplayNames(final String userId, final String contextId,
      final Long inquiryId) {
    List<DocumentName> displayNamesList =
        documentNameRepo.findAllByInquiryIdAndRefDocumentIdIsNotNull(inquiryId);
    if (CollectionUtils.isEmpty(displayNamesList)) {
      displayNamesList = new ArrayList<>();
    }
    return displayNamesList;
  }


  @Transactional
  @Override
  public List<DocumentFileView> retrieveAllFilesAssociatedWithDocumentId(final String userId,
      final String contextId, final Long inquiryId, final List<Long> documentId) {
    List<Long> documentIds = new ArrayList<>();
    // documentIds.add(documentId);
    List<SpatialInquiryDocument> spatialInquiryDocumentList =
        spatialInquiryDocumentRepo.findAllByDocumentIdsAndInquiryId(documentIds, inquiryId);
    List<DocumentFileView> documentFileList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDocumentList)
        && !CollectionUtils.isEmpty(spatialInquiryDocumentList.get(0).getSpatialInquiryFiles())) {
      spatialInquiryDocumentList.get(0).getSpatialInquiryFiles().forEach(spatialInquiryFile -> {
        DocumentFileView documentFileView = new DocumentFileView();
        documentFileView.setDocumentFileId(spatialInquiryFile.getInquiryFileId());
        documentFileView.setDocumentId(spatialInquiryFile.getDocumentId());
        documentFileView.setFileName(spatialInquiryFile.getFileName());
        documentFileView.setFileDate(formatDate.format(spatialInquiryFile.getFileDate()));
        documentFileList.add(documentFileView);
      });
    }
    return documentFileList;
  }


  @Transactional(rollbackOn = {DcsException.class})
  @Override
  public ResponseEntity<Response> deleteDocument(String userId, String contextId, Long inquiryId,
      String authorization, List<Long> documentIds) {

    logger.info("Entering into deleteSupportDocument. User Id {}, Context Id {}", userId,
        contextId);
    List<String> documentClassList = spatialInquiryDocumentRepo
        .findDocumentClassByDocumentIdAndInquiryId(documentIds.get(0), inquiryId);

    if (CollectionUtils.isEmpty(documentClassList)) {
      logger.error("There is no document class associated for the input document Ids {}",
          documentIds);
      throw new ValidationException(DCSServiceConstants.NO_DOCUMENT,
          DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClassList.get(0));

    List<SpatialInquiryDocument> spatialInquiryDocuments = spatialInquiryDocumentRepo
        .findAllDocumentsByDocumentIdsAndInquiryId(documentIds, inquiryId);
    if (CollectionUtils.isEmpty(spatialInquiryDocuments)) {
      logger.error("There is no document associated with this document Id {}", documentIds);
      throw new ValidationException(DCSServiceConstants.NO_DOCUMENT,
          DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    ResponseEntity<Response> deleteResponse = null;
    for (SpatialInquiryDocument document : spatialInquiryDocuments) {
      Long documentId = document.getDocumentId();
      try {
        if (CollectionUtils.isEmpty(document.getSpatialInquiryFiles())) {
          logger.info(
              "This document Id {} has the reference {}. "
                  + "Original document {} cannot be delete. so, removing the reference ",
              documentId, document.getRefDocumentId());
          spatialInquiryDocumentRepo.updateStateCode(documentId, userId, new Date());
          deleteResponse = new ResponseEntity<Response>(HttpStatus.OK);
        } else {
          deleteResponse = dcsServiceUtil.deleteDocuments(String.valueOf(document.getDocumentId()),
              document.getEcmaasGuid(), userId, authorization, clientId, contextId);
          if (HttpStatus.OK.equals(deleteResponse.getStatusCode())) {
            logger.info(
                "Updating document: {} state code after delete from ECMaaS. user id: {} context id: {}",
                documentId, userId, contextId);
            spatialInquiryDocumentRepo.updateStateCode(documentId, userId, new Date());
            logger.info("Update successful for document Id {} . user id: {} context id: {}",
                documentId, userId, contextId);
            logger.info(
                "Exiting from  deleteDocument Response code is: {} user id: {} context Id: {}",
                deleteResponse.getStatusCode(), userId, contextId);
            logger.info("Existing from deleteSupportDocument. User Id {}, Context Id {}", userId,
                contextId);
          } else {
            throw new ValidationException("FAILED_TO_DELETE",
                "Unable to delete the document from ECMaas. Document Id " + documentId);
          }
        }
      } catch (ValidationException ve) {
        logger.error("Error while deleting the document Id {}, Received Invalid Status code {}",
            documentId, deleteResponse.getStatusCode(), ve);
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

  // @Transactional
  // @Override
  // public List<DocumentNameView> retrieveAllDocumentsAndFilesAssociatedWithDocumentId(String
  // userId,
  // String contextId, Long inquiryId, Long documentId) {
  // List<Long> documentIds = new ArrayList<>();
  // documentIds.add(documentId);
  // logger.info("Retrieve all the Documents and References associated with this "
  // + "document Id {}, User Id {}, Context Id {}", documentId, userId, contextId);
  //
  // Optional<SupportDocument> supportDocumentAvailability =
  // spatialInquiryDocumentDAO.findByIdAndinquiryId(documentId, inquiryId);
  // if (!supportDocumentAvailability.isPresent()) {
  // logger.error("There is no document associated with this document Id {}, "
  // + "User Id, Context Id {}", documentId, userId, contextId);
  // throw new DocumentNotFoundException("NO_DOCUMENT_EXIST", "There is no document exist");
  // }
  //
  // SupportDocument supportDocument = supportDocumentAvailability.get();
  // if (CollectionUtils.isEmpty(supportDocument.getDocFiles()) && ) {
  // logger.info("This document has an reference of another document. so, have to pull the Original
  // document. User Id {}, Context Id {} ", );
  // }
  //
  // List<SupportDocument> supportDocumentList =
  // spatialInquiryDocumentDAO.findAllByDocumentIdsAndinquiryId(documentIds, inquiryId);
  //
  // List<DocumentNameView> documentNamesList = new ArrayList<>();
  //
  // if (!CollectionUtils.isEmpty(supportDocumentList)) {
  // for (SupportDocument supportDocument : supportDocumentList) {
  //
  // if (CollectionUtils.isEmpty(supportDocument.getDocFiles())
  // && supportDocument.getRefDocumentId() != null) {
  // logger.info("Check whether this document has only reference detail. "
  // + "not actual file. User Id {}, Context id {}", userId, contextId);
  // supportDOcumentAavailability =
  // spatialInquiryDocumentDAO.findById(supportDocument.getRefDocumentId());
  // logger.info("Find the list of files mapped with Original Document {} , User Id {}, Context Id
  // {}",
  // supportDocument.getRefDocumentId(), userId, contextId);
  //
  // if (supportDOcumentAavailability.isPresent()) {
  // supportDocument = supportDOcumentAavailability.get();
  // }
  // }
  //
  // if (!CollectionUtils.isEmpty(supportDocument.getDocFiles())) {
  // List<DocumentFileView> documentFileList = new ArrayList<>();
  // DocumentNameView documentDetail = new DocumentNameView();
  // documentDetail.setDisplayName(supportDocument.getDocumentNm());
  // documentDetail.setDocumentId(supportDocument.getDocumentId());
  // documentDetail.setDocCategory(supportDocument.getDocumentTypeId());
  // documentDetail.setDocSubCategory(supportDocument.getDocumentSubTypeId());
  // for (SupportDocumentFile supportDocumentFile : supportDocument.getDocFiles()) {
  // DocumentFileView documentFileView = new DocumentFileView();
  // documentFileView.setDocumentFileId(supportDocumentFile.getDocumentFileId());
  // documentFileView.setDocumentId(supportDocument.getDocumentId());
  // documentFileView.setFileName(supportDocumentFile.getFileName());
  // documentFileView.setFileDate(formatDate.format(supportDocumentFile.getFileDate()));
  // documentFileList.add(documentFileView);
  // }
  // documentDetail.setFiles(documentFileList);
  // documentNamesList.add(documentDetail);
  // }
  // }
  // }
  // return documentNamesList;
  // }

  @Transactional
  @Override
  public List<DocumentNameView> retrieveAllDocumentsAndFilesAssociatedWithDocumentId(String userId,
      String contextId, Long inquiryId, Long documentId) {

    List<Long> documentIds = new ArrayList<>();
    documentIds.add(documentId);
    logger.info("Retrieve all the Documents and References associated with this "
        + "document Id {}, User Id {}, Context Id {}", documentId, userId, contextId);
    List<SpatialInquiryDocument> spatialInquiryDocuments = spatialInquiryDocumentRepo
        .findAllDocumentsByDocumentIdsAndInquiryId(documentIds, inquiryId);

    if (CollectionUtils.isEmpty(spatialInquiryDocuments)) {
      logger.error(
          "There is no document associated with this document Id {}, " + "User Id, Context Id {}",
          documentId, userId, contextId);
      throw new DocumentNotFoundException("NO_DOCUMENT_EXIST", "There is no document exist");
    }
    List<DocumentNameView> documentNamesList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDocuments.get(0).getSpatialInquiryFiles())) {
      logger.info(
          "Process the Original document Id passed as an input parameter. User Id {} , Context Id {}",
          userId, contextId);
    } else if (CollectionUtils.isEmpty(spatialInquiryDocuments.get(0).getSpatialInquiryFiles())
        && spatialInquiryDocuments.get(0).getRefDocumentId() != null) {
      logger.info("This document details would have "
          + "referenced in another document. User Id {}, Context Id {}", userId, contextId);
      Optional<SpatialInquiryDocument> spatialInquiryDocumentAvailability =
          spatialInquiryDocumentRepo
              .findByIdAndInquiryId(spatialInquiryDocuments.get(0).getRefDocumentId(), inquiryId);

      if (spatialInquiryDocumentAvailability.isPresent()) {
        SpatialInquiryDocument refSpatialInquiryDocument = spatialInquiryDocumentAvailability.get();
        // refSupportDocument.setDocumentId(documentId);
        spatialInquiryDocuments.remove(0);
        spatialInquiryDocuments.add(0, refSpatialInquiryDocument);
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
    spatialInquiryDocuments.forEach(spatialInquiryDoc -> {
      DocumentNameView documentDetail = new DocumentNameView();
      List<DocumentFileView> documentFileList = new ArrayList<>();
      documentDetail.setDisplayName(spatialInquiryDoc.getDocumentNm());
      documentDetail.setDocumentId(spatialInquiryDoc.getDocumentId());
      documentDetail.setDocCategory(spatialInquiryDoc.getDocumentTypeId());
      documentDetail.setDocSubCategory(spatialInquiryDoc.getDocumentSubTypeId());
      for (SpatialInquiryFile spatialInquiryFile : spatialInquiryDoc.getSpatialInquiryFiles()) {
        DocumentFileView documentFileView = new DocumentFileView();
        documentFileView.setDocumentFileId(spatialInquiryFile.getInquiryFileId());
        documentFileView.setDocumentId(spatialInquiryDoc.getDocumentId());
        documentFileView.setFileName(spatialInquiryFile.getFileName());
        documentFileView.setFileDate(formatDate.format(spatialInquiryFile.getFileDate()));
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
      String authorization, Long inquiryId, Long documentId, String fileName) {

    List<String> documentClassName =
        spatialInquiryDocumentRepo.findDocumentClassByDocumentIdAndInquiryId(documentId, inquiryId);
    if (CollectionUtils.isEmpty(documentClassName)) {
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          "Error : Invalid Request.");
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClassName.get(0));
    SpatialInquiryDocument spatialInquiryDocument =
        spatialInquiryDocumentRepo.findByDocumentIdAndDocumentStateCode(documentId, "A");

    if (spatialInquiryDocument == null) {
      logger.error("There is no support document associated with this document id {}", documentId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          "Error : Invalid Request.");
    }

    String guid =
        dcsServiceUtil.removeBracketsFromGUID(spatialInquiryDocument.getEcmaasGuid(), contextId);

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


  @Override
  public List<SpatialInqDocument> retrieveRequiredSpatialDocument(final String userId,
      final String contextId, final Long inquiryId) {
    
    List<SpatialInqDocument> spatialInquiryDocumentsList = new ArrayList<>();
    List<SpatialInquirySupportDocument> spatialInquirySupportDocuments = spatialInquiryDocumentDAO
        .retrieveSISupportDocuments(userId, contextId, inquiryId);
    
    Map<Integer, SpatialInquiryDocument> uploadedSpatialInquiryDocumentMappedByTitleId = new HashMap<>();
    List<SpatialInquiryDocument> uploadedDocuments = spatialInquiryDocumentRepo.findAllDocumentsByInquiryId(inquiryId);
    if (!CollectionUtils.isEmpty(uploadedDocuments)) {
      uploadedDocuments.forEach(uploadedDocument -> {
        if (uploadedDocument.getDocumentSubTypeTitleId() != null 
            && uploadedDocument.getDocumentSubTypeTitleId() > 0) {
          uploadedSpatialInquiryDocumentMappedByTitleId.put(
              uploadedDocument.getDocumentSubTypeTitleId(), uploadedDocument);
        } else {
          SpatialInqDocument uploadedSpatialDocument = new SpatialInqDocument();
          uploadedSpatialDocument.setDocumentId(uploadedDocument.getDocumentId());
          uploadedSpatialDocument.setDocumentTitle(uploadedDocument.getDocumentNm());
          uploadedSpatialDocument.setDocumentType(uploadedDocument.getDocumentTypeId());
          uploadedSpatialDocument.setDocumentSubType(uploadedDocument.getDocumentSubTypeId());
          uploadedSpatialDocument.setReqdDocumentInd(0);
          uploadedSpatialDocument.setUploadInd("Y");
          spatialInquiryDocumentsList.add(uploadedSpatialDocument);
        }
      });
    }
    
    if (!CollectionUtils.isEmpty(spatialInquirySupportDocuments)) {
      spatialInquirySupportDocuments.forEach(spatialInquirySupportDocument -> {
        SpatialInqDocument uploadedSpatialDocument = new SpatialInqDocument();
        uploadedSpatialDocument.setDocumentTitle(spatialInquirySupportDocument.getDocumentTitle());
        uploadedSpatialDocument.setDocumentTitleId(spatialInquirySupportDocument.getDocumentSubTypeTitleId());
        uploadedSpatialDocument.setReqdDocumentInd(spatialInquirySupportDocument.getReqdDocForSpatialInqId());
        SpatialInquiryDocument uploadedDocument = uploadedSpatialInquiryDocumentMappedByTitleId.get(
            spatialInquirySupportDocument.getDocumentSubTypeTitleId());
        if (uploadedDocument != null) {
          uploadedSpatialDocument.setUploadInd("Y");
          uploadedSpatialDocument.setDocumentId(uploadedDocument.getDocumentId());
        } else {
          uploadedSpatialDocument.setUploadInd("N");
        }
        spatialInquiryDocumentsList.add(uploadedSpatialDocument);
      });
    }
    return spatialInquiryDocumentsList;
  }
}
