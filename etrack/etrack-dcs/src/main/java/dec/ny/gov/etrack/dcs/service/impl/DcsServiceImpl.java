package dec.ny.gov.etrack.dcs.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.dcs.dao.ETrackDartFacilityDAO;
import dec.ny.gov.etrack.dcs.dao.SubmittedDocumentDAO;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocType;
import dec.ny.gov.etrack.dcs.model.DocumentFile;
import dec.ny.gov.etrack.dcs.model.EtrackDartFacility;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SubmittedDocNonRelReasonDetail;
import dec.ny.gov.etrack.dcs.model.SubmittedDocument;
import dec.ny.gov.etrack.dcs.service.DcsService;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import dec.ny.gov.etrack.dcs.util.DCSServiceUtil;
import dec.ny.gov.etrack.dcs.util.DateUtil;
import dec.ny.gov.etrack.dcs.util.TransformationService;

@Service
@Transactional
public class DcsServiceImpl implements DcsService {

  private static Logger logger = LoggerFactory.getLogger(DcsServiceImpl.class);
  @Autowired
  private SubmittedDocumentDAO submittedDocumentDao;
  @Autowired
  private DCSServiceUtil dcsServiceUtil;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ETrackDartFacilityDAO eTrackDartFacilityDAO;
  @Value("${dms.ingest.document.uri.path}")
  private String ingestDocumentPath;
  @Value("${dms.delete.document.uri.path}")
  private String deleteDocumentPath;
  @Value("${dms.retrieve.document.uri.path}")
  private String retrieveDocumentPath;
  @Value("${dms.update.document.metadata.uri.path}")
  private String updateMetadataPath;
  @Value("${config.messages.uri}")
  private String cachedMessagesPath;
  @Value("${config.doctypes.uri}")
  private String cachedDocTypesPath;
  @Value("${config.uri}")
  private String configPath;
  private String contextId;
  private String methodName;

  @Autowired
  private TransformationService transformationService;
  @Autowired
  private SupportDocumentService supportDocumentService;
  
  @Transactional(rollbackOn = {DcsException.class})
  @Override
  public ResponseEntity<Response> deleteDocument(String documentId, String userId, String token,
      String clientId, String contextId) {
    this.methodName = "deleteDocument()";
    this.contextId = contextId;
    logger.info("Entering {}. user id: {} context Id : {} , document Id : {}", methodName, userId,
        this.contextId, documentId);
    SubmittedDocument document =
        retrieveDocumentByDocumentId(documentId, userId, contextId, this.methodName);
    
    if (StringUtils.isEmpty(document.getEcmaasGUID())) {
     logger.error("There is no document associated with document id {} to delete", documentId);
     throw new ValidationException(DCSServiceConstants.NO_DOCUMENT,
         DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
    }
    
    ResponseEntity<Response> deleteResponse = dcsServiceUtil.deleteDocuments(
        documentId, document.getEcmaasGUID(), userId, token,clientId, contextId);
    try {
      logger.info("Updating document: {} state code. user id: {} context id: {}", documentId,
          userId, this.contextId);
      submittedDocumentDao.updateStateCode(Long.valueOf(documentId));
      logger.info("Update successful for document Id {} . user id: {} context id: {}", documentId,
          userId, this.contextId);
      logger.info("Exiting {} . Response code is: {} user id: {} context Id: {}", this.methodName,
          deleteResponse.getStatusCode(), userId,  this.contextId);
      return deleteResponse;
    } catch (ValidationException e) {
      throw e;
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName, "Error updating document's state code",
          "Document id: ".concat(documentId), e.getMessage(), this.contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  @Transactional
  @Override
  public ResponseEntity<byte[]> retrieveFileContent(String documentId, String userId, String token,
      String clientId, String fileName, String contextId) {
    this.methodName = "retrieveFileContent()";
    this.contextId = contextId;
    logger.info("Entering {}. user id: {} context Id :{} , document Id :{}", methodName, userId,
        this.contextId, documentId);
    SubmittedDocument document = 
        retrieveDocumentByDocumentId(documentId, userId, contextId, this.methodName);

    String guid = dcsServiceUtil.removeBracketsFromGUID(document.getEcmaasGUID(), contextId);
    MultiValueMap<String, String> headerMap =
        populateRetrieveAndDeleteDmsHeadersMap(documentId, userId, clientId, guid);
    headerMap.add(HttpHeaders.AUTHORIZATION, token);
    HttpEntity<String> httpEntity = new HttpEntity<>(headerMap);
    String uri = UriComponentsBuilder.newInstance()
        .pathSegment(retrieveDocumentPath, guid, fileName).build().toUriString();
    logger.debug("URI Value with file name {} ", uri);;
    ResponseEntity<byte[]> retrieveResponseEntity = null;
    try {
      logger.info("Making call to DMS to retrieve file content {} from the document id:{}. user id: {}. Context Id: {}",
          fileName, documentId, userId, contextId);
      retrieveResponseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, byte[].class);
      logger.info("Exiting {}. Response code is: {}, user id: {} context Id: {}", this.methodName,
          retrieveResponseEntity.getStatusCode(), userId, this.contextId);;
      return retrieveResponseEntity;
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName,
          "Error making call to DMS to retrieve file content", "Document id: ".concat(documentId),
          e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }
  }

  @Transactional(rollbackOn = DcsException.class)
  @Override
  public ResponseEntity<Response> updateDocumentMetadata(String documentId, String userId, 
      IngestionRequest request, String contextId, String token, 
      final String documentClass, final Long projectId) {

    this.methodName = "updateDocumentMetadata()";
    this.contextId = contextId;
    String originalDocClass = null;// doc class of the document when it was originally uploaded
    Integer originalDocTypeId = null;

    logger.info("Entering {}. user id: {} context Id: {} , document Id: {}", methodName, userId,
        this.contextId, documentId);
    
    if (!(dcsServiceUtil.isDocumentClassValid(documentClass))) {
      logger.error(
          "The document class: {} is invalid. Exiting updateDocumentMetadata(). User id: {}  Context Id: {}",
          documentClass, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }

    if (dcsServiceUtil.isNotValidFoilStatus(request, contextId)) {
      logger.error("Received NonReleaseCode when FOIL status as REL. User id: {}  context Id: {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClass);
    logger.debug(
        "Making call to Service layer to update document:{}'s metadata. User id: {}  context id: {}",
        documentId, userId, contextId);
    List<String> documentNames = null;
    Response response = null;
    // Read the document from Database
    try {
      if (projectId != null && projectId > 0) {
        logger.info("Search this document in Support document. User Id {}, Context Id {} ", userId, contextId);
        throw new DocumentNotFoundException("NO_SUBMIT_DOC_AVAIL", "This document should be searched in Support document");
      }
      
      SubmittedDocument document =
          retrieveDocumentByDocumentId(documentId, userId, contextId, this.methodName);
 
      String districtId = document.getEdbDistrictId().toString();
      logger.debug("Document names for district Id: {} are: {}. User id: {} Context Id {} ",
          districtId, documentNames, userId, contextId);
      String documentTitle =
          request.getMetaDataProperties().get(DCSServiceConstants.DOCUMENT_TITLE).toUpperCase();
      logger.debug("Document title is:{}. User id: {} context id: {}", documentTitle, userId,
          contextId);
      documentNames = getAllDocNamesInProjectForOtherDocIds(districtId, documentId, userId, documentTitle);
      if (!CollectionUtils.isEmpty(documentNames) && documentNames.contains(documentTitle)) {
        logger.error(
            "The document name {}, is already taken for associated district Id: {} . Exiting updateMetaData. user id: {} context id: {}",
            documentTitle, districtId, userId, DCSServiceConstants.CONTEXT_ID);
        throw new ValidationException("DOC_NAME_DUPLICATE",
            "Error : Document Name already exists for this Project. You must rename the Document Name in order to Save.");
      }
      try {
        logger.info("Retrieving original Document type id. user id: {} context id: {}", userId,
            contextId);
        originalDocTypeId = submittedDocumentDao.findDocumentTypeIdByDocumentId(Long.valueOf(documentId));
        logger.info("Retrieval successful. User id: {} context id: {}", userId, contextId);
        logger.debug("originalDocTypeId== {}, user id: {}, context id: {}", originalDocTypeId, userId,
            contextId);
        if (originalDocTypeId != null) {
          originalDocClass = getDocumentClassNmfromDocType(originalDocTypeId, contextId,
              this.methodName, userId, token);
        }
        if (StringUtils.isEmpty(originalDocClass)) {
          logger.error(
              "Original Document Class is empty. Process cannot be continued. user id: {} context Id {}",
              userId, contextId);
          throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
              DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
        }
        logger.debug("OriginalDocClass== {} , user id: {} context Id: {} ", originalDocClass, userId,
            contextId);
        if (!documentClass.equals(originalDocClass)) {
          logger.error("During the update of docType Filenet document class is changing from "
              + originalDocClass + " to " + documentClass + " which is not permitted.");
          throw new ValidationException("DOC_CLASS_CHANGING",
              "Technically an existing Document Type cannot be changed, as a FileNet Document Class is changing. To work around this, "
                  + "download all of the files for this Document Name, create a new one with the correct Document Type, and delete the incorrect one.");
        }
        
        Date indexDate = java.util.Calendar.getInstance().getTime();
        String indexDateString = new SimpleDateFormat("yyyyMMddHHmmss").format(indexDate);
        logger.debug("Formatted index date is: {}. user id: {} context id: {}", indexDateString,
            userId, contextId);
        request.getMetaDataProperties().put(DCSServiceConstants.INDEX_DATE, indexDateString);

        if (DCSServiceConstants.PERMIT.equals(documentClass)) {
          mapSubmittedDocumentFromIngestionRequestForPermitClass(request.getMetaDataProperties(),
              document, userId);
        } else if (DCSServiceConstants.APPLICATION.equals(documentClass)) {
          request.getMetaDataProperties().remove(DCSServiceConstants.PERMIT_TYPE);
          mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass(
              request.getMetaDataProperties(), document, userId);
        } else if (DCSServiceConstants.SUPPORTINGDOCUMENTS.equals(documentClass)) {
          mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass(
              request.getMetaDataProperties(), document, userId);
        } else if (DCSServiceConstants.CORRESPONDENCE.equals(documentClass)) {
          
          Map<String, String> ingestionMetaData = request.getMetaDataProperties();
          ingestionMetaData.remove(DCSServiceConstants.APPLICATION_ID);
          ingestionMetaData.remove(DCSServiceConstants.PERMIT_TYPE);
          ingestionMetaData.remove(DCSServiceConstants.MODIFICATION_NUMBER);
          ingestionMetaData.remove(DCSServiceConstants.RENEWAL_NUMBER);
          String emailSubject = ingestionMetaData.get(DCSServiceConstants.DOC_DESCRIPTION);
          if (StringUtils.isEmpty(emailSubject)) {
            emailSubject = "NULL";
          }
          ingestionMetaData.put(DCSServiceConstants.EMAIL_SUBJECT, emailSubject);
          mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass(
              ingestionMetaData, document, userId);
        } else {
          logger.error(
              "Document class: {} received for the district ID {} is not valid. user id: {} context id: {}",
              documentClass, districtId, userId, contextId);
          throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
              DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
        }
        removeUneededFieldsFromMetadataMap(request.getMetaDataProperties(), userId);
      } catch (Exception e) {
        if (e instanceof ValidationException) {
          throw e;
        }
        populateLoggerExeptionMap(this.methodName, "Error mapping request to document.",
            "Document id: ".concat(documentId), e.getMessage(), contextId, userId);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.", e);
      }

      MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
      headersMap.add(DCSServiceConstants.CLIENT_ID, clientId);
      headersMap.add(DCSServiceConstants.USER_ID, userId);
      headersMap.add(DCSServiceConstants.CONTEXT_ID, contextId);
      headersMap.add(HttpHeaders.AUTHORIZATION, token);
      String guid = dcsServiceUtil.removeBracketsFromGUID(document.getEcmaasGUID(), contextId);
      HttpEntity<IngestionRequest> httpEntity = new HttpEntity<>(request, headersMap);
      String uri =
          UriComponentsBuilder.newInstance().pathSegment(updateMetadataPath, guid).build().toString();

      ResponseEntity<Response> updateResponseEntity = null;
      try {
        logger.info("Making call to DMS to update document id: {} . user id: {} context Id: {}",
            documentId, userId, contextId);
        updateResponseEntity = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Response.class);
      } catch (Exception e) {
        populateLoggerExeptionMap(this.methodName,
            "Error error making call to DMS to update metadata.", "Document id: ".concat(documentId),
            e.getMessage(), contextId, userId);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.", e);
      }

      if (HttpStatus.OK != updateResponseEntity.getStatusCode()) {
        logger.error(
            "Received unsuccessful response code {} from DMS update Metadata response call. "
                + "Document id: {}, user id: {} context Id: {}",
            updateResponseEntity.getStatusCode(), documentId, userId, contextId);
        response = updateResponseEntity.getBody();
        if (response != null) {
          logger.error(
              "Received response from DMS update metadata document service for "
                  + "document id {} . user id: {} context Id {} , result code {} , result message {}",
              documentId, contextId, response.getResultCode(), response.getResultMessage());
        }
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.");
      }
      try {
        logger.info("Updating document {} . user id: {} context Id: {}", documentId, userId,
            contextId);
        submittedDocumentDao.save(document);
        logger.info(
            "Update succuessful. The response status code is {}. Exiting updateDocumentsMetadata(). user id: {} context Id: {}",
            updateResponseEntity.getStatusCode(), userId, contextId);
        return updateResponseEntity;
      } catch (Exception e) {
        populateLoggerExeptionMap(this.methodName, "Error ingesting document.",
            "Document id: ".concat(documentId), e.getMessage(), contextId, userId);
        logger.error(
            "Exception while ingesting document after successful DMS call. user id: {} context id: {}",
            userId, contextId, e);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.", e);
      }
    } catch (DocumentNotFoundException dnfe) {
      logger.info("This document is not available in Submitted Document."
          + " Needs to check Support Document. User Id {}, Context Id {}", userId, contextId);
      return supportDocumentService.updateSupportDocumentMetadata(
          userId, contextId, token, request, Long.valueOf(documentId));
    }
  }

  private SubmittedDocument retrieveDocumentByDocumentId(String documentId, String userId,
      String contextId, String methodName) {
    try {
      logger.info("Retrieving document: {}. user id: {} context id: {}", documentId, userId,
          contextId);
      SubmittedDocument document =
          submittedDocumentDao.findByDocumentIdAndDocumentStateCode(Long.valueOf(documentId), "A");
      logger.info("Retrieval successful. user id: {} context id: {}", userId, contextId);
      if (document == null) {
        logger.info(
            "The document Id: {} has no Historical documents related to it. Exiting {}. user id: {} context Id : {}",
            documentId, methodName, userId, contextId);
        throw new DocumentNotFoundException(DCSServiceConstants.NO_DOCUMENT,
            DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
      }
      return document;
    } catch (DocumentNotFoundException dnfe) {
      throw dnfe;
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName, "Retrieving document by document Id.",
          "Document id: ".concat(documentId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }
  }

  /**
   * This method will return all document names in that district belonging to all document IDs
   * except the one in second argument. DocumentID can not be null here. *
   * 
   * @param districtId
   * @param documentId
   * @param userId
   * @return
   * @throws DcsException
   */
  private List<String> getAllDocNamesInProjectForOtherDocIds(String districtId, String documentId,
      String userId, final String documentName) throws DcsException {
    try {
      logger.info("Retrieving documents related to district id: {} . user id: {} context Id: {}",
          districtId, userId, contextId);
      List<String> documentNames = submittedDocumentDao
          .findDocumentNmByDistrictIdForOtherDocIds(Long.valueOf(districtId), new Long(documentId));
      logger.debug(
          "Document names associated with document {} and district id {}. DocumentNames: {} , user id: {} context Id {}",
          documentId, districtId, documentNames, userId, contextId);
      if (CollectionUtils.isEmpty(documentNames)) {
        documentNames = submittedDocumentDao.findSupportDocumentNameExistByDistrictId(
            Long.valueOf(districtId), documentName);
      }
      return documentNames;
    } catch (Exception e) {
      populateLoggerExeptionMap(methodName,
          "Error retrieving documents related to district id:".concat(districtId),
          "District id: ".concat(districtId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  /**
   * This method returns all document names for that district. documentID here can be null as it is
   * required for only logs.
   * 
   * @param districtId
   * @param documentId
   * @param userId
   * @return
   * @throws DcsException
   */
  private List<String> getAllDocNamesInProject(String districtId, String userId, final String documentName)
      throws DcsException {
    try {
      logger.info("Retrieving documents related to district id: {} . user id: {} context Id: {}",
          districtId, userId, contextId);
      List<String> documentNames =
          submittedDocumentDao.findDocumentNmByDistrictId(Long.valueOf(districtId));
      List<String> supportDocumentNames = submittedDocumentDao.findSupportDocumentNameExistByDistrictId(
          Long.valueOf(districtId), documentName);
      documentNames.addAll(supportDocumentNames);
      logger.debug(
          "Document names associated with the district ID {} are {}. user id: {} context Id: {}",
          districtId, documentNames, userId, contextId);
      return documentNames;
    } catch (Exception e) {
      populateLoggerExeptionMap(methodName,
          "Error retrieving documents related to district id:".concat(districtId),
          "District id: ".concat(districtId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  private void populateLoggerExeptionMap(String methodName, String eventName, String applicableId,
      String errorMessage, String contextId, String userId) {
    logger.debug("Entering populateLoggerExceptionMap(). User id: {} context id: {}", userId,
        contextId);
    Map<String, String> loggingMap = new HashMap<>();
    loggingMap.put(DCSServiceConstants.APPLICATION_NAME, "eTrack");
    loggingMap.put(DCSServiceConstants.METHOD_NAME, methodName);
    loggingMap.put(DCSServiceConstants.EVENT_NAME, eventName);
    loggingMap.put("Applicable ids", applicableId);
    loggingMap.put(DCSServiceConstants.CONTEXT_ID, contextId);
    loggingMap.put(DCSServiceConstants.USER_ID, userId);
    loggingMap.put(DCSServiceConstants.ERROR_MESSAGE, errorMessage);
    logger.error(loggingMap.toString());
    loggingMap = null;
  }

  private Map<String, String> removeUneededFieldsFromMetadataMap(Map<String, String> requestMap,
      String userId) {
    logger.debug("Entering removeUnneededFieldsFromMetadataMap(). User id: {} context id: {} ",
        userId, contextId);
    requestMap.remove("docCreator");
    requestMap.remove(DCSServiceConstants.TRACKED_APP_ID);
    requestMap.remove(DCSServiceConstants.OTHER_SUB_CAT_TEXT);
    // concatenate non releasable reason codes to foilStatus if it is non releasable
    String docRelCode = requestMap.get(DCSServiceConstants.FOIL_STATUS);
    String nonRelReasonCd = requestMap.get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
    if (docRelCode != null && docRelCode.trim().equals("NOREL"))
      requestMap.put(DCSServiceConstants.FOIL_STATUS, docRelCode + "-" + nonRelReasonCd);
    requestMap.remove(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
    logger.debug("Requests metadata map after removing fields: {}. User id: {} context id: {}.",
        requestMap.toString(), userId, contextId);
    logger.debug("Exiting removeUneededFieldsFromMetadataMap(). User id: {} context id: {}", userId,
        contextId);
    return requestMap;
  }

  private SubmittedDocument mapSubmittedDocumentFromIngestionRequestForPermitClass(
      Map<String, String> ingestionMetadata, SubmittedDocument document, String userId) {
    logger.debug(
        "Entering mapSubmittedDocumentFromIngestionRequestForPermitClass(). user id: {} context id: {}",
        userId, contextId);
    try {
      Date currentDate = java.util.Calendar.getInstance().getTime();
      document.setDocumentTypeId(
          Integer.valueOf(ingestionMetadata.get(DCSServiceConstants.DOC_CATEGORY)));
      String docSubCat = ingestionMetadata.get(DCSServiceConstants.DOC_SUB_CATEGORY);
      if (docSubCat != null && !docSubCat.trim().equals("")) {
        if (docSubCat.trim().equals("0")) {
          document.setDocumentSubTypeId(null);
        } else {
          document.setDocumentSubTypeId(Integer.parseInt(docSubCat));
        }
      }
      document.setDocReleasableCode(ingestionMetadata.get(DCSServiceConstants.FOIL_STATUS));
      document
          .setNonEtrackInd(Integer.valueOf(ingestionMetadata.get(DCSServiceConstants.HISTORIC)));
      document.setDocSubTypeOtherTxt(ingestionMetadata.get(DCSServiceConstants.OTHER_SUB_CAT_TEXT));
      document.setDocumentNm(ingestionMetadata.get(DCSServiceConstants.DOCUMENT_TITLE));
      document.setModifiedById(ingestionMetadata.get(DCSServiceConstants.DOC_LAST_MODIFIER));
      document.setDocumentDesc(ingestionMetadata.get(DCSServiceConstants.DOC_DESCRIPTION));
      document.setModifiedDate(currentDate);
      document.setTrackedApplicationId(ingestionMetadata.get(DCSServiceConstants.TRACKED_APP_ID));
      setNonRelReasonCodesToDocument(ingestionMetadata, document, userId);
      logger.debug(
          "Exiting mapSubmittedDocumentFromIngestionRequestForPermitClass(). User id: {} context id: {}",
          userId, contextId);
      return document;
    } catch (Exception e) {
      String documentId = document.getDocumentId().toString();
      populateLoggerExeptionMap("mapSubmittedDocumentFromIngestionRequestForPermitClass()",
          "Error mapping request to document", documentId, e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  private SubmittedDocument mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass(
      Map<String, String> ingestionMetadata, SubmittedDocument document, String userId) {
    logger.debug(
        "Entering mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass(). user id: {} context id: {}",
        userId, contextId);
    try {
      Date currentDate = java.util.Calendar.getInstance().getTime();
      document.setDocumentTypeId(
          Integer.valueOf(ingestionMetadata.get(DCSServiceConstants.DOC_CATEGORY)));
      String docSubCat = ingestionMetadata.get(DCSServiceConstants.DOC_SUB_CATEGORY);
      if (docSubCat != null && !docSubCat.trim().equals("")) {
        if (docSubCat.trim().equals("0")) {
          document.setDocumentSubTypeId(null);
        } else {
          document.setDocumentSubTypeId(Integer.parseInt(docSubCat));
        }
      }
      setNonRelReasonCodesToDocument(ingestionMetadata, document, userId);
      document.setDocumentNm(ingestionMetadata.get(DCSServiceConstants.DOCUMENT_TITLE));
      document.setModifiedById(ingestionMetadata.get(DCSServiceConstants.DOC_LAST_MODIFIER));
      document.setDocSubTypeOtherTxt(ingestionMetadata.get(DCSServiceConstants.OTHER_SUB_CAT_TEXT));
      document.setDocumentDesc(ingestionMetadata.get(DCSServiceConstants.DOC_DESCRIPTION));
      document.setModifiedDate(currentDate);
      document.setDocReleasableCode(ingestionMetadata.get(DCSServiceConstants.FOIL_STATUS));
      document
          .setNonEtrackInd(Integer.valueOf(ingestionMetadata.get(DCSServiceConstants.HISTORIC)));
      document.setAccessByDEPOnlyInd(
          Integer.valueOf(ingestionMetadata.get(DCSServiceConstants.ACCESS)));
      document.setTrackedApplicationId(ingestionMetadata.get(DCSServiceConstants.TRACKED_APP_ID));
      return document;
    } catch (Exception e) {
      String documentId = document.getDocumentId().toString();
      populateLoggerExeptionMap(
          "mapSubmittedDocumentFromIngestionRequestForApplicationAndSupportingDocumentsClass()",
          "Error mapping request to document", "Document id: ".concat(documentId), e.getMessage(),
          contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  private MultiValueMap<String, String> populateRetrieveAndDeleteDmsHeadersMap(String documentId,
      String userId, String clientId, String guid) {
    logger.debug("Entering populateRetrieveAndDeleteDmsHeadersMap(). User id: {} context id: {}",
        userId, contextId);
    MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
    headersMap.add(DCSServiceConstants.GUID, guid);
    headersMap.add(DCSServiceConstants.DOCUMENT_ID, documentId);
    headersMap.add(DCSServiceConstants.USER_ID, userId);
    headersMap.add(DCSServiceConstants.CLIENT_ID, clientId);
    headersMap.add(DCSServiceConstants.CONTEXT_ID, this.contextId);
    return headersMap;
  }

  @Transactional(rollbackOn = DcsException.class)
  @Override
  public ResponseEntity<IngestionResponse> uploadDocument(String userId, String token,
      String docClass, String districtId, IngestionRequest ingestionMetaData,
      MultipartFile[] uploadFiles, String contextId) {

    this.methodName = "uploadDocument()";
    this.contextId = contextId;
    ResponseEntity<IngestionResponse> ingestDocResponse = null;
//    IngestionResponse resp = new IngestionResponse();
    MultiValueMap<String, Object> filesAndMetadataMap = null;
    List<DocumentFile> filesInDoc = new ArrayList<>();
    SubmittedDocument doc = null;
    ingestionMetaData.setUserId(userId);
    // Long dupDocNameDocId=null;
    logger.info("Entering {}. User id: {}  context Id: {}", methodName, userId, this.contextId);
    List<String> docNames = null;

    // Insert District details if its not available.
    try {
      Long districId = Long.valueOf(districtId);
      Date currentDate = new Date();
      EtrackDartFacility eDartFacility = eTrackDartFacilityDAO.findByDistrictId(districId);
      if (eDartFacility == null) {
        eDartFacility = new EtrackDartFacility();
        eDartFacility.setDistrictId(Long.valueOf(districtId));
        eDartFacility.setCreateDate(currentDate);
        eDartFacility.setModifiedDate(currentDate);
        eDartFacility.setCreatedById(userId);
        logger.info("Ingesting Etrack Dart Facility, District ID {} . User id: {}  context Id: {} ",
            districtId, userId, this.contextId);
        eTrackDartFacilityDAO.save(eDartFacility);
        logger.info("Ingestion successful. User id: {} context id: {}", userId, contextId);
      }
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName, "Ingesting dart facility into E_DART_FACILITY.",
          "District id: ".concat(districtId), e.getMessage(), this.contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }

    try {
      if (CollectionUtils.isEmpty(ingestionMetaData.getMetaDataProperties())) {
        logger.error(
            "Ingestion MetaData property is empty for the district Id {} , user id: {} context Id: {}",
            districtId, userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }

      logger.debug("IngestionMetaData is {} ,  attachFileCount == {}. User id: {} context id: {}",
          ingestionMetaData.getMetaDataProperties().toString(),
          (ingestionMetaData.getAttachmentFilesCount() == null ? ""
              : ingestionMetaData.getAttachmentFilesCount().intValue()),
          userId, contextId);

      if (uploadFiles == null) {
        logger.error("UploadFiles is null. District Id {} , user id: {} context id: {}", districtId,
            userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      if (uploadFiles.length > 0) {
        logger.debug("UploadFiles file1 name = {}. User id: {}, context id: {}"
            + uploadFiles[0].getOriginalFilename(), userId, contextId);
      }

      docNames = getAllDocNamesInProject(districtId, contextId, ingestionMetaData.getMetaDataProperties()
          .get(DCSServiceConstants.DOCUMENT_TITLE).toUpperCase());
      if (docNames != null) {
        logger.debug("Got all documents associated to this facility {}. User id: {} context Id {} ",
            docNames.toString(), userId, contextId);
        if (docNames.contains(ingestionMetaData.getMetaDataProperties()
            .get(DCSServiceConstants.DOCUMENT_TITLE).toUpperCase())) {
          logger.error(
              "The document name is already taken for associated district Id: {} . User id: {}  context Id: {}.",
              districtId, userId, contextId);
          throw new ValidationException("DOC_NAME_DUP_REPLACE_MSG",
              "Error : This Document Name already exists for this DEC ID. Are you sure that you want to REPLACE it?");
        } else {
          logger.debug("No documents for the district ID {} , user id: {} context Id {}",
              districtId, userId, contextId);
        }
      }
    } catch (Exception e) {
      if (e instanceof ValidationException) {
        throw e;
      }
      populateLoggerExeptionMap(this.methodName, "Checking for duplicate document names",
          "District id: ".concat(districtId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
    try {
      doc = getSubmittedDocEntity(ingestionMetaData.getMetaDataProperties(), userId, districtId,
          contextId);

      Map<String, String> fileDates = ingestionMetaData.getFileDates();
      if (CollectionUtils.isEmpty(fileDates)) {
        logger.error(
            "File Dates are missing in metadata map. District Id:{} , user id: {} context Id: {}",
            districtId, userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      int fileDateIndex = 0;
      filesAndMetadataMap = new LinkedMultiValueMap<>();
      HttpHeaders attachFile = new HttpHeaders();

      if (uploadFiles != null && uploadFiles.length > 0) {
        for (MultipartFile file : uploadFiles) {
          attachFile.setContentType(MediaType.parseMediaType(file.getContentType()));
          ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
          HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(resource, attachFile);
          filesAndMetadataMap.add("uploadDocuments", attachment);
          logger.debug("FileDate == {}, user id: {} context Id : {} ",
              fileDates.get(fileDateIndex + " "), userId, contextId);
          filesInDoc.add(createDocumentFile(new Long(fileDateIndex + 1), file.getOriginalFilename(),
              userId, fileDates.get(fileDateIndex + ""), doc));
          fileDateIndex++;
        }
      }
      doc.setDocFiles(filesInDoc);
      logger.info("Ingesting document for first time. User id: {} context id: {}", userId,
          contextId);
      doc = submittedDocumentDao.save(doc);// save the document to database and get doc Id.
      logger.info("Ingestion successful. User id: {} context id: {}", userId, contextId);
    } catch (ValidationException ve) {
      throw ve;
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName, "Ingesting document for first time.",
          "User id: ".concat(userId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }

    if (doc != null) {
      logger.debug(
          "Modifying the metadata properties map depending on document class. Document Id {},  User id: {} context id: {}",
          doc.getDocumentId(), userId, contextId);
      ingestionMetaData =
          transformationService.getIngestReqForDocClass(ingestionMetaData, docClass, doc.getDocumentId(), userId, contextId);
      logger.debug("Foil status after adjusting foilstatus {}, user id: {} context Id {}",
          ingestionMetaData.getMetaDataProperties().get(DCSServiceConstants.FOIL_STATUS), userId,
          contextId);
      try {
        HttpHeaders fileMetadataHeaders = new HttpHeaders();
        fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (docClass != null && !docClass.trim().equals(""))
          ingestionMetaData.setClientId(dcsServiceUtil.retrieveClientId(docClass));
        logger.debug("GUID from ECMaas is not yet available. User id: {} context id: {}", userId,
            contextId);
        HttpEntity<IngestionRequest> metaDataEntity =
            new HttpEntity<>(ingestionMetaData, fileMetadataHeaders);
        filesAndMetadataMap.set("ingestionMetaData", metaDataEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.add(DCSServiceConstants.CLIENT_ID, ingestionMetaData.getClientId());
        httpHeaders.add(DCSServiceConstants.USER_ID, userId);
        httpHeaders.add(DCSServiceConstants.CONTEXT_ID, contextId);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
            new HttpEntity<>(filesAndMetadataMap, httpHeaders);
        String uri =
            UriComponentsBuilder.newInstance().path(ingestDocumentPath).build().toUriString();
        logger.info("Making call to DMS to upload document id: {} . User id: {} context Id: {}",
            doc.getDocumentId(), userId, contextId);
        ingestDocResponse =
            restTemplate.exchange(uri, HttpMethod.POST, requestEntity, IngestionResponse.class);
        logger.info("Upload successful. User id: {} context id: {}", userId, contextId);
      } catch (Exception e) {
        populateLoggerExeptionMap(this.methodName, "Error ingesting document to filenet",
            "Context id: ".concat(contextId), e.getMessage(), contextId, userId);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.", e);
      }
    } else // throw new DcsException("DOcument could not be persisted in database.");
    {
      logger.error(
          "Document could not be ingested in database. District Id {} , user id: {} context Id: {} ",
          districtId, userId, contextId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
    }
    int updateInd = 0;

    try {
      if (HttpStatus.CREATED != ingestDocResponse.getStatusCode()) {
        logger.error("File Upload failed. User id: {} context Id: {}", userId, contextId);
        submittedDocumentDao.updateEcmaasGuidAndStatus(doc.getDocumentId(), null, "E");
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
      }
      String guid = ingestDocResponse.getBody().getGuid();
      logger.info("Updating document {} . ECMaaS Guid {}, User id: {} context id: {}.", guid,
          doc.getDocumentId(), userId, contextId);
      updateInd = submittedDocumentDao.updateEcmaasGuidAndStatus(doc.getDocumentId(), guid, "A");
      logger.info("Exiting {}. Response code is: {}, updateInd {},  user Id: {} context Id: {}",
          this.methodName, ingestDocResponse.getStatusCode(), updateInd, userId, contextId);

      return ingestDocResponse;
    } catch (Exception e) {
      if (e instanceof DcsException) {
        throw e;
      }
      populateLoggerExeptionMap(this.methodName, "Updating documents EcmaaS guid and status code.",
          "EcmaaS guid:".concat(ingestDocResponse.getBody().getGuid()), e.getMessage(), contextId,
          userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }
  }

  private String getDocumentClassNmfromDocType(Integer docTypeId, String contextId,
      String methodName, String userId, String token) {

    UriComponentsBuilder.newInstance();
    String uri = UriComponentsBuilder.fromUriString(configPath).pathSegment(cachedDocTypesPath)
        .build().toString();
    logger.debug("DocType ID = {} , user id: {} context Id {} ", docTypeId, userId, contextId);
    ResponseEntity<Map> messageResponse;
    try {
      logger.info("Making call to cache to retrieve document class. User id: {} context id: {}",
          userId, contextId);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, token);
      headers.add(DCSServiceConstants.USER_ID, userId);
      headers.add(DCSServiceConstants.CONTEXT_ID, contextId);
      HttpEntity<MultiValueMap> entity = new HttpEntity<>(headers);
      messageResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
      // messageResponse = restTemplate.getForEntity(uri, Map.class);
      logger.info("Retrieval successful. User id: {} context id: {}.", userId, contextId);
    } catch (Exception e) {
      populateLoggerExeptionMap(methodName, "Error retrieving document class from cache.",
          docTypeId.toString(), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }

    Map<String, Map<String, Object>> docTypes = messageResponse.getBody();

    if (CollectionUtils.isEmpty(docTypes)) {
      logger.error("Cache returned an empty response. User id: {} context id: {}", userId,
          contextId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
    }
    
    Map<String, Object> languageBasedMap = docTypes.get("en-US");
    DocType docType = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = mapper.writeValueAsString(languageBasedMap.get(docTypeId.toString()));

      docType = mapper.readValue(jsonString, DocType.class);
      if (docType == null) {
        logger.error(
            "Error: There is no docType in the cache associated "
                + "for the input document type ID {}. User id: {} context Id: {}",
            docTypeId, userId, contextId);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
      }
    } catch (DcsException e) {
      throw e;
    } catch (JsonProcessingException e) {
      populateLoggerExeptionMap(methodName, "Converting Etrack Document Type map to Json string",
          "Context id: ".concat(contextId), e.getMessage(), contextId, userId);
      logger.error("Error in processing JSON ", e);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
    }
    logger.debug("Document class name: {}, user id: {} context Id {} ", docType.getDocClassName(),
        userId, contextId);
    return docType.getDocClassName();
  }

  @Transactional
  @Override
  public ResponseEntity<IngestionResponse> replaceWithNewDocument(String userId, String docClass,
      String districtId, IngestionRequest ingestionMetaData, MultipartFile[] uploadFiles,
      String contextId, String token) {
    String origClientId = null;
    String originalDocClass = null;
    Integer originalDocTypeId = null;
    Long docId = null;
    this.methodName = "replaceWithNewDocument()";
    this.contextId = contextId;

    try {
      if (ingestionMetaData == null || ingestionMetaData.getMetaDataProperties() == null) {
        logger.error("IngestionMetaData or metadata map is null. User id: {} context Id: {} ",
            userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      String documentTitle = ingestionMetaData.getMetaDataProperties()
          .get(DCSServiceConstants.DOCUMENT_TITLE).toUpperCase();
      logger.info(
          "Retrieving document id by district id and document name. User id: {} context id: {}", userId, 
          contextId);
      logger.debug("Document title == {}, district id == {} user id: {} context id: {}",
          documentTitle, districtId, userId, contextId);
      docId = submittedDocumentDao.findDocumentIdByDistrictIdAndDocumentNm(Long.valueOf(districtId),
          documentTitle);
      logger.info("Retrieval successful. Document id:{}, user id: {} context id: {}.", docId,
          userId, contextId);
      if (docId == null) {
        logger.error(
            "There are no documents with the title: {}. Replacing the document is not necessary. "
            + "User id: {} district Id: {}  context Id: {}",
            documentTitle, userId, districtId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      logger.debug(
          "The document ID for which the given name already exists in the project is: {}. User id: {} context id: {} ",
          docId, userId, contextId);
    } catch (Exception e) {
      if (e instanceof ValidationException) {
        throw e;
      }
      populateLoggerExeptionMap(this.methodName, "Error retrieving document id.",
          "District id: ".concat(districtId), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }

    try {
      logger.info("Retrieving original document type id. User id: {} context id: {}", userId,
          contextId);
      originalDocTypeId = submittedDocumentDao.findDocumentTypeIdByDocumentId(docId);
      logger.info("Retreival successful. originalDocTypeId {},  User id: {} context id: {}",
          originalDocTypeId, userId, contextId);
      if (originalDocTypeId != null) {
        originalDocClass =
            getDocumentClassNmfromDocType(originalDocTypeId, contextId, methodName, userId, token);
      }

      if (StringUtils.isEmpty(originalDocClass)) {
        logger.error(
            "Original Document Class is empty. Process cannot be continued. User id: {} context Id: {}",
            userId, contextId);
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
      }
      logger.debug("OriginalDocClass== {} , user id: {} context Id {} ", originalDocClass, userId,
          contextId);
      origClientId = dcsServiceUtil.retrieveClientId(originalDocClass);
      logger.info("Making call to delete document {}. user id: {} context id: {}", docId.toString(),
          userId, contextId);
      ResponseEntity<Response> resp =
          deleteDocument(docId.toString(), userId, token, origClientId, contextId);
      if (resp.getStatusCode() == HttpStatus.OK) {
        logger.info(
            "Document {}'s logical delete was successful. Trying to ingest new document. "
                + "User id: {} context id: {} Response status code: {}",
            docId, userId, contextId, resp.getStatusCode());
        return uploadDocument(userId, token, docClass, districtId, ingestionMetaData, uploadFiles,
            contextId);
      } else {
        logger.error(
            "Duplicate document could not be logically deleted. Exiting "
                + "replaceWithNewDocument(), user id: {} context id: {} , response status code: {}",
            userId, contextId, resp.getStatusCode());
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG);
      }
    } catch (DcsException e) {
      throw e;
    } catch (Exception e) {
      populateLoggerExeptionMap(this.methodName,
          "Error logically deleting/uploading of new document",
          "Document id: ".concat(docId.toString()), e.getMessage(), contextId, userId);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          DCSServiceConstants.UNABLE_TO_PROCESS_NOW_ERR_MSG, e);
    }
  }


  private SubmittedDocument getSubmittedDocEntity(Map<String, String> metadata, String userId,
      String distId, String contextId) {
    logger.debug("Entering getSubmittedDocEntity(). User id: {} context id: {}", userId, contextId);
    SubmittedDocument doc = new SubmittedDocument();
    Long districtId = Long.valueOf(distId);
    Date currentDate = new Date();
    doc.setEdbDistrictId(districtId);
    doc.setAccessByDEPOnlyInd(Integer.parseInt(metadata.get(DCSServiceConstants.ACCESS)));
    doc.setDocReleasableCode(metadata.get(DCSServiceConstants.FOIL_STATUS));
    doc.setDocumentTypeId(Integer.parseInt(metadata.get(DCSServiceConstants.DOC_CATEGORY)));
    String docSubCat = metadata.get(DCSServiceConstants.DOC_SUB_CATEGORY);
    if (docSubCat != null && !docSubCat.trim().equals("")) {
      if (docSubCat.trim().equals("0")) {
        doc.setDocumentSubTypeId(null);
      } else {
        doc.setDocumentSubTypeId(Integer.parseInt(docSubCat));
      }
    }
    doc.setDocumentStateCode("P");
    logger.debug(
        "When trying to upload initially, set document status code to pending status. User id: {} context id: {}",
        userId, contextId);
    doc.setDocumentDesc(metadata.get(DCSServiceConstants.DOC_DESCRIPTION));
    doc.setDocumentNm(metadata.get(DCSServiceConstants.DOCUMENT_TITLE));
    doc.setNonEtrackInd(Integer.parseInt(metadata.get(DCSServiceConstants.HISTORIC)));
    doc.setDocSubTypeOtherTxt(metadata.get(DCSServiceConstants.OTHER_SUB_CAT_TEXT));
    doc.setCreateDate(currentDate);
    doc.setModifiedDate(currentDate);
    doc.setCreatedById(userId);
    doc.setTrackedApplicationId(metadata.get(DCSServiceConstants.TRACKED_APP_ID));
    setNonRelReasonCodesToDocument(metadata, doc, userId);
    return doc;
  }

  private SubmittedDocument setNonRelReasonCodesToDocument(Map<String, String> metadata,
      SubmittedDocument doc, String userId) {

    logger.debug("Entering setNonRelReasonCodesToDocument(). User id: {} context id {}", userId,
        contextId);
    String nonRelReasonCodes = metadata.get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
    nonRelReasonCodes = dcsServiceUtil.removeUnwantedChars(nonRelReasonCodes);

    // String docReleasableCode = doc.getDocReleasableCode();
    String docReleasableCode = metadata.get(DCSServiceConstants.FOIL_STATUS);

    logger.debug("nonRelReasonCodes==== {}, Doc Releasable code is: {}. User id: {} context id: {}",
        nonRelReasonCodes, docReleasableCode, userId, contextId);

    if (!StringUtils.isEmpty(docReleasableCode)) {
      if (!StringUtils.isEmpty(nonRelReasonCodes) && docReleasableCode.trim().equals("NOREL")) {
        logger.debug(
            "Non Releasable foil status , clear the existing non rel reasons before add {}, user id: {} Context Id: {} ",
            doc.getDocNonRelReasons(), userId, contextId);

        if (doc.getDocNonRelReasons() == null) {
          doc.setDocNonRelReasons(
              createSubmittedDocNonRelReasonList(nonRelReasonCodes, userId, doc)); // insert
        } else {
          doc.getDocNonRelReasons().clear(); // update - clear the existing non releasable reasons.
          doc.getDocNonRelReasons()
              .addAll(createSubmittedDocNonRelReasonList(nonRelReasonCodes, userId, doc));
        }
      } else {
        logger.debug(
            "Releasable foil status , clear the existing non rel reasons if exist {}, user id: {} context Id ",
            doc.getDocNonRelReasons(), userId, contextId);
        if (!CollectionUtils.isEmpty(doc.getDocNonRelReasons())) {
          doc.getDocNonRelReasons().clear(); // update - clear the existing non releasable reasons.
        }
      }
    }
    logger.debug("NonRelReasonCodesList==== {}, user id: {} context Id {} ",
        (doc.getDocNonRelReasons() == null ? "0" : doc.getDocNonRelReasons().size()), userId,
        contextId);
    return doc;
  }

  private DocumentFile createDocumentFile(Long fileNo, String fileName, String userId,
      String fileDate, SubmittedDocument sDoc) {
    logger.debug("Entering createDocumentFile(). User id: {} context Id {}", userId, contextId);
    DocumentFile docFile = new DocumentFile();
    docFile.setCreatedById(userId);
    docFile.setFileName(fileName);
    docFile.setFileNumber(fileNo);
    docFile.setCreatedDate(new Date());
    docFile.setFileDate(DateUtil.formatStringToDate(fileDate));
    docFile.setSubmittedDoc(sDoc);
    return docFile;
  }

  private List<SubmittedDocNonRelReasonDetail> createSubmittedDocNonRelReasonList(
      String nonRelReasons, String userId, SubmittedDocument doc) {
    logger.debug("Entering createSubmittedDocNonRelReasonList(), user id: {} context Id {}", userId,
        contextId);
    List<SubmittedDocNonRelReasonDetail> nonRelReasonList = null;
    new ArrayList<SubmittedDocNonRelReasonDetail>();
    SubmittedDocNonRelReasonDetail nonRelReason = null;
    if (nonRelReasons != null && nonRelReasons.trim().length() > 0) {
      String[] reasons = nonRelReasons.split(",");
      if (reasons != null && reasons.length > 0) {
        nonRelReasonList = new ArrayList<SubmittedDocNonRelReasonDetail>();
        for (String reasonCode : reasons) {
          nonRelReason = new SubmittedDocNonRelReasonDetail();
          nonRelReason.setCreatedById(userId);
          nonRelReason.setDocNonRelReasonCode(reasonCode);
          // logger.info("non rel reason code being added to child table "+reasonCode);
          nonRelReason.setCreatedDate(new Date());
          nonRelReason.setSubmittedDoc(doc);
          nonRelReasonList.add(nonRelReason);
        }
      }
    }
    return nonRelReasonList;
  }
}
