package dec.ny.gov.etrack.dms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import dec.ny.gov.etrack.dms.exception.DMSException;
import dec.ny.gov.etrack.dms.model.DMSDocumentMetaData;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DocumentResult;
import dec.ny.gov.etrack.dms.model.ECMaaSDocumentMetaData;
import dec.ny.gov.etrack.dms.request.MetadataProperty;

@Component
public class DocumentResultResponseHandler {

  private static final String SUCCESS_STAT_CDE = "0000";
  private static final String DOC_NOT_FOUND_ERR = "009_0005";
  private static final List<String> FILE_NET_ERR =
      Arrays.asList("005_0016", "005_0017", "003_0017", "003_0018", "003_0019", "003_0020");

  private static final Logger logger =
      LoggerFactory.getLogger(DocumentResultResponseHandler.class.getName());
  private Map<String, String> loggingMap = new HashMap<>();



  /**
   * Handle the Document response data received from FileNet and transform as a consumable format.
   * 
   * @param responseEntity - Response Entity.
   * 
   * @return - Transformed Document Response.
   */
  public ResponseEntity<DMSDocumentResponse> handleResponse(
      ResponseEntity<DocumentResult> responseEntity, String contextId, String userId) {
    logger.info("Entering handleResponse(). User id:{}. Context id:{}", userId, contextId);

    DocumentResult responseBody = responseEntity.getBody();
   
    if (responseBody == null) {
      logger.error("Response is null. Exiting handleResponse(). User id:{}. Context id:{}",userId, contextId);
      return new ResponseEntity<>(responseEntity.getStatusCode());
    }
    
    String resultCode = responseBody.getResultCode();
    String statusMessage = responseBody.getResultMessage();
    HttpStatus statusCode = responseEntity.getStatusCode();

    if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      logger.error("Retrieval unsuccessful. Status code: {}. Result message: {}. User id: {}. Context id: {}", statusCode, statusMessage, userId, contextId);
      return convertECMaaSMetaDataToDMSMetaData(responseBody, statusCode, contextId, userId);
    }

    if (SUCCESS_STAT_CDE.equals(resultCode) && responseBody.getNumDocumentsReturned() > 0) {
      logger.info("Retrieval successful. Exiting handleResponse(). Context id:{}", contextId);
      return convertECMaaSMetaDataToDMSMetaData(responseBody, statusCode, contextId, userId);
    }

    if ((SUCCESS_STAT_CDE.equals(resultCode) && responseBody.getNumDocumentsReturned() == 0)
        || (!SUCCESS_STAT_CDE.equals(resultCode) && responseBody.getNumDocumentsReturned() == 0
            && statusMessage.contains(DOC_NOT_FOUND_ERR))) {
      logger.error("Retrieval unsuccessful. Status code: {}. Result message: {}. User id: {}. Context id: {}", statusCode, statusMessage, userId, contextId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    for (String errorCode : FILE_NET_ERR) {
      if (statusMessage.contains(errorCode)) {
        logger.error("Retrieval unsuccessful. Status code: {}. Result message: {}. User id: {}. Context id: {}", statusCode, statusMessage, userId, contextId);
        return convertECMaaSMetaDataToDMSMetaData(responseBody, HttpStatus.INTERNAL_SERVER_ERROR, contextId, userId);
      }
    }
    logger.error("Retrieval unsuccessful. Status code: {}. Result message: {}. User id: {}. Context id: {}", statusCode, statusMessage, userId, contextId);
    return convertECMaaSMetaDataToDMSMetaData(responseBody, HttpStatus.BAD_REQUEST, contextId, userId);
  }

  private ResponseEntity<DMSDocumentResponse> convertECMaaSMetaDataToDMSMetaData(
      DocumentResult documentResult, HttpStatus statusCode, String contextId, String userId) {

    logger.info("Entering into convertECMaaSMetaDataToDMSMetaData(). User id:{}. Context id:{}",userId, contextId);
    DMSDocumentResponse documentResponse = null;
    ResponseEntity<DMSDocumentResponse> dMResponseEntity = null;

    try {
      documentResponse = new DMSDocumentResponse();
      documentResponse.setResultCode(documentResult.getResultCode());
      documentResponse.setHasMoreDocuments(documentResult.getHasMoreDocuments());
      documentResponse.setNumCEAttached(documentResult.getNumCEAttached());
      documentResponse.setNumDocumentsReturned(documentResult.getNumDocumentsReturned());
      documentResponse.setResultMessage(documentResult.getResultMessage());
      documentResponse.setNumDocumentsMatching(documentResult.getNumDocumentsMatching());

      List<DMSDocumentMetaData> documentsMetaData = new ArrayList<>();
      Map<String, String> metaDataProperties = null;

      for (ECMaaSDocumentMetaData metaData : documentResult.getDocumentsMetaData()) {
        DMSDocumentMetaData dmsDocumentMetaData = new DMSDocumentMetaData();
        dmsDocumentMetaData.setDocumentId(metaData.getDocumentId());
        dmsDocumentMetaData.setAttachedCECount(metaData.getAttachedCECount());
        dmsDocumentMetaData.setAttachmentMetaDatas(metaData.getAttachmentMetaDatas());
        metaDataProperties = new HashMap<>();
        for (MetadataProperty metadataProperty : metaData.getReturnPropertyDefinitionIdList()) {
          metaDataProperties.put(metadataProperty.getPropertyDefinitionId(),
              metadataProperty.getValue());
        }
        if (!metaDataProperties.isEmpty()) {
          dmsDocumentMetaData.setMetaDataProperties(metaDataProperties);
        }
        documentsMetaData.add(dmsDocumentMetaData);
      }
      documentResponse.setDocumentsMetaData(documentsMetaData);
      dMResponseEntity = new ResponseEntity<>(documentResponse, statusCode);
      
    } catch (Exception e) {
      loggingMap = populateLoggerExeptionMap("convertECMaaSMetaDataToDMSMetaData()",
          "Converting EcmaaS metadata to DMS metadata.", contextId, e.getMessage(), contextId, userId);
      logger.error(loggingMap.toString());
      loggingMap.clear();
      throw new DMSException(e.getMessage());
    }
    logger.info("Exiting from convertECMaaSMetaDataToDMSMetaData(). Context id:{}", contextId);
    return dMResponseEntity;
  }
  
  private Map<String, String> populateLoggerExeptionMap(String methodName, String eventName,
      String documentId, String errorMessage, String contextId, String userId) {
    logger.debug("Entering populateLoggerExceptionMap(). User id: {}. Context id:{}", userId, contextId);
    loggingMap.put("Application name", "eTrack");
    loggingMap.put("Method name", methodName);
    loggingMap.put("Event name", eventName);
    loggingMap.put("Applicable Ids", documentId);
    loggingMap.put("Context Id", contextId);
    loggingMap.put("User id", userId);
    loggingMap.put("Error message", errorMessage);
    return loggingMap;
  }
}
