package dec.ny.gov.etrack.dms.ecmaas.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dms.ecmaas.ECMaaSWrapper;
import dec.ny.gov.etrack.dms.exception.DMSException;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.DocumentResult;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.model.SdisRequestObject;
import dec.ny.gov.etrack.dms.model.SearchScope;
import dec.ny.gov.etrack.dms.request.MetadataProperty;
import dec.ny.gov.etrack.dms.response.ECMaaSMetaDataResponse;
import dec.ny.gov.etrack.dms.response.ECMaaSResponse;
import dec.ny.gov.etrack.dms.util.DeleteDocumentResponseHandler;
import dec.ny.gov.etrack.dms.util.DocumentResultResponseHandler;
import dec.ny.gov.etrack.dms.util.IngestionDocumentResponseHandler;

@Repository
public class ECMaaSWrapperImpl implements ECMaaSWrapper {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private DeleteDocumentResponseHandler deleteDocumentResponseHandler;

  @Autowired
  private DocumentResultResponseHandler documentResultResponseHandler;

  @Autowired
  private IngestionDocumentResponseHandler ingestionDocumentResponseHandler;

  @Value("${eCMaaS.delete.uri.path}")
  private String deleteDocumentPath;
  @Value("${eCMaaS.retrieve.document.uri.path}")
  private String searchDocumentPath;
  @Value("${eCMaaS.retrieve.content.uri.path}")
  private String retrieveContentPath;
  @Value("${eCMaaS.insert.content.uri.path}")
  private String uploadDocumentPath;
  @Value("${eCMaaS.update.metadata.uri.path}")
  private String updateMetatDataPath;

  private static final Logger logger = LoggerFactory.getLogger(ECMaaSWrapperImpl.class.getName());
  private Map<String, String> loggingMap = new HashMap<>();
  

  @Override
  public ResponseEntity<Response> deleteDocument(DMSRequest deleteRequest) {
    String contextId = deleteRequest.getContextId();
    String userId = deleteRequest.getUserId();
    deleteRequest.setContextId(null);
    logger.info("Entering deleteDocument(). User id:{}. Context id:{}", userId, contextId);
    ResponseEntity<Response> responseEntity = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<DMSRequest> requestEntity = new HttpEntity<>(deleteRequest, headers);
      logger.info("Deleting document:{}. User id:{}. Context id:{}", deleteRequest.getDocumentId(), userId, contextId);
      responseEntity = restTemplate.exchange(getURI(deleteDocumentPath), HttpMethod.POST,
          requestEntity, Response.class);
      responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, contextId, userId);
    } catch (Exception e) {
      writeSystemUnavailableInfo(e);
      loggingMap = populateLoggerExeptionMap("deleteDocument()",
          "Deleting document.", deleteRequest.getDocumentId(), e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
    logger.info("Exiting deleteDocument. Response code is: {}. User id:{}. Context id:{}", responseEntity.getStatusCode(), userId, contextId);
    return responseEntity;
  }

  @Override
  public ResponseEntity<DMSDocumentResponse> retrieveDocumentByGuid(DMSRequest searchRequest) {
    String contextId = searchRequest.getContextId();
    String userId = searchRequest.getUserId();
    String documentId = searchRequest.getDocumentId();
    searchRequest.setContextId(null);
    logger.info("Entering retrieveDocumentByGuid(). User id:{}. Context id:{}", userId, contextId);
    ResponseEntity<DocumentResult> responseEntity = null;
    ResponseEntity<DMSDocumentResponse> dmsDocumentResponseEntity = null;
    String guid = null;
    try {
      searchRequest.setIncludeContentMetaData(true);
      SearchScope searchScope = new SearchScope();
      searchScope.setMaximumNumberOfDocs(1);
      searchScope.setSearchAllVersions(false);
      searchRequest.setSearchScope(searchScope);
      guid = searchRequest.getDocumentId();
      searchRequest.setSearchQueryCondition("ID='".concat(guid).concat("'"));
      searchRequest.setDocumentId(null);
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<DMSRequest> requestEntity = new HttpEntity<>(searchRequest, headers);
      logger.info("Retrieving document:{}. User id:{}. Context id:{}", documentId, userId, contextId);
      responseEntity = restTemplate.exchange(getURI(searchDocumentPath), HttpMethod.POST,
          requestEntity, DocumentResult.class);
      dmsDocumentResponseEntity = documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
      searchRequest.setGuid(guid);
    } catch (Exception e) {
      writeSystemUnavailableInfo(e);
      loggingMap = populateLoggerExeptionMap("retrieveDocumentByGuid()",
          "Retrieving document by guid.", documentId, e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
    logger.info("Exiting retrieveDocumentByGuid(). Response code is: {}. User id:{}. Context id:{}", dmsDocumentResponseEntity.getStatusCode(), userId, contextId);
    return dmsDocumentResponseEntity;
  }


  @Override
  public ResponseEntity<byte[]> retrieveDocumentContent(DMSRequest retrieveContentRequest) {
    String contextId = retrieveContentRequest.getContextId();
    String userId = retrieveContentRequest.getUserId();
    retrieveContentRequest.setContextId(null);
    logger.info("Entering retrieveDocumentContent(). User id:{}. Context id:{}", userId, contextId);
    ResponseEntity<byte[]> responseEntity = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<DMSRequest> requestEntity = new HttpEntity<>(retrieveContentRequest, headers);
      logger.info("Retrieving document. User id:{}. Context id:{}", userId, contextId);
      responseEntity = restTemplate.exchange(getURI(retrieveContentPath), HttpMethod.POST,
          requestEntity, byte[].class);
    } catch (Exception e) {
      writeSystemUnavailableInfo(e);
      loggingMap = populateLoggerExeptionMap("retrieveDocumentContent()",
          "Retrieving document content.", "Context id: ".concat(contextId), e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
    logger.info("Exiting retrieveDocumentContent(). Response code is: {}. User id: {} Context id: {}", responseEntity.getStatusCode(),  userId, contextId);
    return responseEntity;
  }

  @Override
  public ResponseEntity<IngestionResponse> uploadDocument(IngestionRequest ingestionRequest,
      MultipartFile[] uploadDocuments) {
    String contextId = ingestionRequest.getContextId();
    String userId = ingestionRequest.getUserId();
    ingestionRequest.setContextId(null);
    logger.info("Entering uploadDocument(). User id:{}. Context id:{}", userId, contextId);
    ResponseEntity<IngestionResponse> response = null;
    try {
      MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
      HttpHeaders attachFile = new HttpHeaders();
      if (uploadDocuments != null && uploadDocuments.length > 0) {
        for (MultipartFile file : uploadDocuments) {
          attachFile.setContentType(MediaType.parseMediaType(file.getContentType()));
          ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
          HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(resource, attachFile);
          multipartRequest.add("AttachmentFiles", attachment);
        }
      }
      HttpHeaders jsonRequestHeaders = new HttpHeaders();
      jsonRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<SdisRequestObject> metaDataEntity =
          new HttpEntity<>(getMetaDataProperties(ingestionRequest, contextId), jsonRequestHeaders);
      multipartRequest.set("SdisRequestObject", metaDataEntity);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(multipartRequest, httpHeaders);

      logger.info("Ingesting document. User id:{}. Context id:{}", userId, contextId);
      ResponseEntity<ECMaaSResponse> fileNetResponse = restTemplate.exchange(
          getURI(uploadDocumentPath), HttpMethod.POST, requestEntity, ECMaaSResponse.class);
      ingestionRequest.setContextId(contextId);
      response =
          ingestionDocumentResponseHandler.handleResponse(fileNetResponse, ingestionRequest, false);
    } catch (Exception e) {
      writeSystemUnavailableInfo(e);
      loggingMap = populateLoggerExeptionMap("uploadDocument()",
          "Uploading document.", contextId, e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
    logger.info("Exiting uploadDocument(). Response code is: {}. User id:{}. Context id:{}", response.getStatusCode(), userId, contextId);
    return response;
  }

  private void writeSystemUnavailableInfo(Exception e) {
    if (e instanceof HttpClientErrorException) {
      HttpClientErrorException exception = (HttpClientErrorException) e;
      if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        logger.error("AKANA_SYSTEM_UNAVAILABLE - Downstream system Akana is unavailable");
      }
    }
  }

  @Override
  public ResponseEntity<IngestionResponse> updatedMetaData(IngestionRequest updateMetaDataRequest) {
    String contextId = updateMetaDataRequest.getContextId();
    String userId = updateMetaDataRequest.getUserId();
    String guid = updateMetaDataRequest.getGuid();
    updateMetaDataRequest.setContextId(null);
    logger.info("Entering updatedMetaData(). User id:{}. Context id:{}", userId, contextId);
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      SdisRequestObject sdisRequestObject = getMetaDataProperties(updateMetaDataRequest, contextId);
      sdisRequestObject.setDocumentId(updateMetaDataRequest.getGuid());
      HttpEntity<SdisRequestObject> metaDataEntity =
          new HttpEntity<>(sdisRequestObject, httpHeaders);
      logger.info("Updating document {}'s metadata. User id:{} context id:{}",  guid, userId, contextId);
      ResponseEntity<ECMaaSMetaDataResponse> ecMaaSResponseEntity =
          restTemplate.exchange(getURI(updateMetatDataPath), HttpMethod.POST, metaDataEntity,
              ECMaaSMetaDataResponse.class);
      updateMetaDataRequest.setContextId(contextId);
      return ingestionDocumentResponseHandler.handleResponse(ecMaaSResponseEntity,
          updateMetaDataRequest, true);
    } catch (Exception e) {
      writeSystemUnavailableInfo(e);
      loggingMap = populateLoggerExeptionMap("updatedMetaData()",
          "Updating documents metadata.", guid, e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
  }

  
  private SdisRequestObject getMetaDataProperties(IngestionRequest ingestionRequest, String contextId) {
    String userId = ingestionRequest.getUserId();
    logger.debug("Entering getMetaDataProperties(). User id:{}. Context id:{}", userId, contextId);
    SdisRequestObject sdisRequestObject = new SdisRequestObject();
    sdisRequestObject.setClientId(ingestionRequest.getClientId());
    sdisRequestObject.setUserId(ingestionRequest.getUserId());
    sdisRequestObject.setAttachmentFilesCount(ingestionRequest.getAttachmentFilesCount());
    Map<String, String> inputMetadataProperties = ingestionRequest.getMetaDataProperties();
    List<MetadataProperty> metadataProperties = new ArrayList<>();
    if (inputMetadataProperties != null && !inputMetadataProperties.isEmpty()) {
      for (Map.Entry<String, String> inputMetaDataProperty : inputMetadataProperties.entrySet()) {
        MetadataProperty metadataProperty = new MetadataProperty();
        metadataProperty.setPropertyDefinitionId(inputMetaDataProperty.getKey());
        metadataProperty.setValue(inputMetaDataProperty.getValue());
        metadataProperties.add(metadataProperty);
      }
    }
    sdisRequestObject.setMetadataProperties(metadataProperties);
    logger.debug("Exiting getMetaDataProperties(). User id:{}. Context id:{}", userId, contextId);
    return sdisRequestObject;
  }

  private String getURI(String path) {
    return UriComponentsBuilder.newInstance().path(path).build().toUriString();
  }
  
  private Map<String, String> populateLoggerExeptionMap(String methodName, String eventName,
      String applicableIds, String errorMessage, String contextId, String userId) {
    logger.debug("Entering populateLoggerExceptionMap(). Context id:{}", contextId);
    loggingMap.put("Application name", "eTrack");
    loggingMap.put("Method name", methodName);
    loggingMap.put("Event name", eventName);
    loggingMap.put("Applicable ids", applicableIds);
    loggingMap.put("Context id", contextId);
    loggingMap.put("User id", userId);
    loggingMap.put("Error message", errorMessage);
    return loggingMap;
  }
}
