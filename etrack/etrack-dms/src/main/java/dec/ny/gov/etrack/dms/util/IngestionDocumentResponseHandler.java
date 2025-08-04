package dec.ny.gov.etrack.dms.util;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.response.DMSResponse;
import dec.ny.gov.etrack.dms.response.DimMetadataProperty;
import dec.ny.gov.etrack.dms.response.ECMaaSResponse;

@Component
public class IngestionDocumentResponseHandler {
  
  private static final Logger logger = LoggerFactory.getLogger(IngestionDocumentResponseHandler.class.getName());

  private static final String SUCCESS_RESULT_CODE = "0000";
  private static final String GUID_FIELD_NAME = "Id";
  private static final List<String> FILE_NET_ERROR =
      Arrays.asList("009_0003", "009_0004", "009_0005", "009_0006", "013_0002");

  /**
   * Handle response received from FileNet and share with the consumer accordingly.
   * 
   * @param responseEntity - Response Entity received from Akana/FileNet.
   * @param ingestionRequest - Ingestion request metadata.
   * @param updateMetaDataReqFlag - Updated Metadata indicator.
   * 
   * @return - Formalized response.
   */
  public ResponseEntity<IngestionResponse> handleResponse(ResponseEntity<?> responseEntity,
      IngestionRequest ingestionRequest, boolean updateMetaDataReqFlag) {
    String contextId = ingestionRequest.getContextId();
    String userId = ingestionRequest.getUserId();
    logger.info("Entering handleResponse(). User id:{}. Context id:{}", userId, contextId);
    IngestionResponse ingestionResponse = new IngestionResponse();
    DMSResponse dmsResponse = (DMSResponse) responseEntity.getBody();
    ingestionResponse.setIngestionRequest(ingestionRequest);
    
    if (dmsResponse == null) {
      logger.error("Response is null. Exiting handleResponse(). User id:{}. Context id:{}", userId, contextId);
      return new ResponseEntity<>(ingestionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    HttpStatus statusCode = responseEntity.getStatusCode();
    String resultCode = dmsResponse.getResultCode();
    String resultMessage = dmsResponse.getResultMessage();
    ingestionResponse.setResultCode(resultCode);
    ingestionResponse.setResultMessage(resultMessage);

    if (!HttpStatus.OK.equals(statusCode)) {
      logger.error("Ingestion unsuccessful. Status code:{}. Result message :{}. User id:{}. Context id:{}", statusCode, resultMessage, userId, contextId);
      return new ResponseEntity<>(ingestionResponse, responseEntity.getStatusCode());
    }
    
    if (SUCCESS_RESULT_CODE.equals(resultCode)) {
      if (updateMetaDataReqFlag) {
        logger.info("Ingestion successful. User id:{}. Context id:{}", userId, contextId);
        return new ResponseEntity<>(ingestionResponse, HttpStatus.OK);
      }
      ECMaaSResponse ecMaaSResponse = (ECMaaSResponse) responseEntity.getBody();
      List<DimMetadataProperty> dimMetadataProperties = ecMaaSResponse.getDimMetadataProperties();
      
      dimMetadataProperties.forEach(dimMetadataProperty -> {
        if (GUID_FIELD_NAME.equals(dimMetadataProperty.getPropertyDefinitionId())) {
          final String guid = dimMetadataProperty.getValue();
          ingestionResponse.setGuid(guid);
          return;
         }
      });
      
      if (StringUtils.hasText(ingestionResponse.getGuid())) {
        logger.info("Ingestion successful. Exiting handleResponse(). User id:{}. Context id:{}", userId, contextId);
        return new ResponseEntity<>(ingestionResponse, HttpStatus.CREATED);
      } else {
        logger.error("Ingestion unsuccessful. Result message:{}. User id:{}. Context id:{}", resultMessage, userId, contextId);
        return new ResponseEntity<>(ingestionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    
    for (String fileNetError : FILE_NET_ERROR) {
      if (resultMessage.contains(fileNetError)) {
        logger.error("Ingestion unsuccessful. Filenet error code:{}. Result message:{}. User id:{}. Context id:{}", fileNetError, resultMessage, userId, contextId);
        return new ResponseEntity<>(ingestionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    logger.error("Ingestion unsuccessful. Result message:{}. User id:{}. Context id:{}", resultMessage, userId, contextId);
    return new ResponseEntity<>(ingestionResponse, HttpStatus.BAD_REQUEST);
  }
  
}
