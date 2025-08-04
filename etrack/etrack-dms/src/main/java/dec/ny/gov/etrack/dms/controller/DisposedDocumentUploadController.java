package dec.ny.gov.etrack.dms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.processor.DocumentManagementProcessor;

@RestController
@RequestMapping("/system-doc")
public class DisposedDocumentUploadController {

  @Autowired
  private DocumentManagementProcessor documentManagementProcessor;
  
  private static final Logger logger = LoggerFactory.getLogger(DisposedDocumentUploadController.class.getName());
  
  /**
   * Upload the System Generated document(s) into FileNet as its doesn't need to authenticated..
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param ingestionMetaData - Metadata request.
   * @param uploadDocuments - Files to be uploaded.
   * 
   * @return - Uploaded document response.
   */
  @PostMapping(value = "/sdis", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  public ResponseEntity<IngestionResponse> uploadDocument(
      @RequestHeader String userId,
      @RequestHeader String clientId,
      @RequestHeader String contextId,
      @RequestPart(value = "ingestionMetaData") IngestionRequest ingestionMetaData,
      @RequestParam(value = "uploadDocuments") MultipartFile[] uploadDocuments) {
    
    logger.info("Entering uploadDocument(). Context Id: {}", contextId);
    if (!"system-authorized".equals(userId)) {
      return new ResponseEntity<IngestionResponse>(HttpStatus.UNAUTHORIZED);
    }
    ingestionMetaData.setClientId(clientId);
    ingestionMetaData.setUserId(userId);
    ingestionMetaData.setContextId(contextId);
    return documentManagementProcessor.uploadDocument(ingestionMetaData, uploadDocuments);
  }

  /**
   * Delete System Generated document(s) for the input requested GUID.
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param guid - GUID associated with file.
   * 
   * @return - Delete document response.
   */
  @DeleteMapping(value = "/sdds/{guid}", headers = {"Accept=application/json"},
      produces = "application/json")
  public ResponseEntity<Response> deleteDocument(@RequestHeader(value="userId") String userId,
      @RequestHeader(value="clientId") String clientId, @RequestHeader String contextId, @PathVariable String guid) {
    
    logger.info("Entering deleteDocument(). Context Id : {}", contextId);
    if (!"system-authorized".equals(userId)) {
      return new ResponseEntity<Response>(HttpStatus.UNAUTHORIZED);
    }
    DMSRequest deleteRequest = new DMSRequest();
    deleteRequest.setUserId(userId);
    deleteRequest.setClientId(clientId);
    deleteRequest.setDocumentId(guid);
    deleteRequest.setContextId(contextId);
    return documentManagementProcessor.deleteDocument(deleteRequest);
  }
}
