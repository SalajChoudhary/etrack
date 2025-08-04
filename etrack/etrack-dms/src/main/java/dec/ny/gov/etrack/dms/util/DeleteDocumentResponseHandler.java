package dec.ny.gov.etrack.dms.util;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import dec.ny.gov.etrack.dms.model.Response;

@Component
public class DeleteDocumentResponseHandler {

  private static final String SUCCESS_STAT_CDE = "0000";
  private static final String DOC_NOT_FOUND_ERR = "009_0005";
  private static final List<String> FILE_NET_ERR =
      Arrays.asList("009_0006", "009_0003", "009_0004");
  
  private static final Logger logger = LoggerFactory.getLogger(DeleteDocumentResponseHandler.class.getName());


  /**
   * Handle the Delete document response received from FileNet.
   * 
   * @param responseEntity - Delete Document response from FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param userId - User who initiates this request.
   * 
   * @return - Transformed response.
   */
  public ResponseEntity<Response> handleResponse(ResponseEntity<Response> responseEntity, String contextId, String userId) {
    logger.info("Entering handleResponse(). User id:{}. Context id:{}", userId, contextId);
    if (responseEntity != null && responseEntity.getBody() != null) {
      Response responseBody = responseEntity.getBody();
      String resultCode = responseBody.getResultCode();
      String statusMessage = responseBody.getResultMessage();
      String documentId = responseBody.getDocumentId();

      if (HttpStatus.OK.equals(responseEntity.getStatusCode())
          && !resultCode.equals(SUCCESS_STAT_CDE)) {

        if (statusMessage.contains(DOC_NOT_FOUND_ERR)) {
          logger.error("No document found for document {}. Result message:{}. User id:{}. Context id:{}", documentId, statusMessage, userId, contextId);
          return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
        }

        for (String errorCode : FILE_NET_ERR) {
          if (statusMessage.contains(errorCode)) {
            logger.error("Error deleting document {}. Result message:{}. User id:{}. Context id:{}", documentId, statusMessage, userId, contextId);
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
        logger.error("Error deleting document {}. Result message:{}. User id:{}. Context id:{}", documentId, statusMessage, userId, contextId);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.BAD_REQUEST);
      }
    }
    logger.info("Deletion successful. User id:{}. Context id:{}", userId, contextId);
    return responseEntity;
  }
}
