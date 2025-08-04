package dec.ny.gov.etrack.dms.controller;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.processor.DocumentManagementProcessor;

@RestController
@RequestMapping("/dms")
public class DMSController {

  private static Logger logger = LoggerFactory.getLogger(DMSController.class);

  @Autowired
  private DocumentManagementProcessor documentManagementProcessor;

  /**
   * Upload the document into FileNet.
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
    
    logger.info("Entering uploadDocument. User id:{}. Context id:{}",userId, contextId);
    ingestionMetaData.setClientId(clientId);
    ingestionMetaData.setUserId(userId);
    ingestionMetaData.setContextId(contextId);
    return documentManagementProcessor.uploadDocument(ingestionMetaData, uploadDocuments);
  }

  /**
   * Update the existing document metadata for the input GUID in the FileNet.
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param guid - GUID associated with file.
   * @param updateMetaDataRequest - Metadata details.
   * 
   * @return - Updated the document metadata response.
   */
  @PutMapping(value = "/sdus/{guid}", consumes = "application/json", produces = "application/json",
      headers = {"Accept=application/json"})
  public ResponseEntity<IngestionResponse> updateDocumentMetaData(
      @RequestHeader String userId,
      @RequestHeader String clientId,
      @RequestHeader String contextId,
      @PathVariable String guid,
      @RequestBody IngestionRequest updateMetaDataRequest) {
    
    logger.info("Entering updateDocumentMetaData(). User id:{}. Context id:{}", userId, contextId);
    updateMetaDataRequest.setClientId(clientId);
    updateMetaDataRequest.setUserId(userId);
    updateMetaDataRequest.setGuid(guid);
    updateMetaDataRequest.setContextId(contextId);
    return documentManagementProcessor.updateMetadataDocument(updateMetaDataRequest);
  }

  /**
   * Retrieve allt he document details for the input GUID.
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param guid - GUID associated with file.
   * 
   * @return - Document Response.
   */
  @GetMapping(value = "qrs/{guid}", produces = "application/json")
  public ResponseEntity<DMSDocumentResponse> retrieveDocumentDetailsByGuid(
      @RequestHeader("userId") String userId, @RequestHeader("clientId") String clientId, @RequestHeader("contextId") String contextId,
      @PathVariable String guid) {
    
    logger.info("Entering retrieveDocumentDetailsByGuid(). User id:{}. Context id:{}", userId, contextId);
    DMSRequest searchRequest = new DMSRequest();
    searchRequest.setClientId(clientId);
    searchRequest.setUserId(userId);
    searchRequest.setDocumentId(guid);
    searchRequest.setContextId(contextId);
    return documentManagementProcessor.retrieveDocumentsByGuid(searchRequest);
  }

  /**
   * Retrieve the file content from FileNet for the input GUID.
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param guid - GUID associated with file.
   * @param fileName - File name content to be retrieved.
   * 
   * @return - File content.
   */
  @GetMapping(value = "qrs/{guid}/{fileName:.+}")
  public ResponseEntity<byte[]> retrieveFileContent(@RequestHeader("userId") String userId,
      @RequestHeader("clientId") String clientId, @RequestHeader("contextId") String contextId, @PathVariable String guid,
      @PathVariable String fileName) {
    
    logger.info("Entering retrieveFileContent() file name {} . User id:{}. Context id:{}", fileName, userId, contextId);
    DMSRequest searchRequest = new DMSRequest();
    searchRequest.setClientId(clientId);
    searchRequest.setUserId(userId);
    searchRequest.setDocumentId(guid);
    searchRequest.setContextId(contextId);
    return documentManagementProcessor.retrieveDocumentContent(searchRequest, fileName);
  }

  /**
   * Delete document for hte input requested GUID.
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
    logger.info("Entering deleteDocument(). User id:{}. Context id:{}", userId, contextId);
    DMSRequest deleteRequest = new DMSRequest();
    deleteRequest.setUserId(userId);
    deleteRequest.setClientId(clientId);
    deleteRequest.setDocumentId(guid);
    deleteRequest.setContextId(contextId);
    return documentManagementProcessor.deleteDocument(deleteRequest);
  }

  /**
   * Delete the document from FileNet for the list of input GUIDs.
   * 
   * @param userId - User who initiates this request.
   * @param clientId - Client Id which will be passed to FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param guids - List of GUID Strings.
   * 
   * @return - Deleted document response.
   */
  @PutMapping(value = "/sdds/delete-documents", headers = {"Accept=application/json"},
      produces = "application/json")
  public ResponseEntity<Response> deleteDocuments(@RequestHeader String userId,
      @RequestHeader String clientId, @RequestHeader String contextId, @RequestBody List<String> guids) {
    logger.info("Entering deleteDocument(). User id:{}. Context id:{}", userId, contextId);
    if (!CollectionUtils.isEmpty(guids)) {
      guids.forEach(guid -> {
        DMSRequest deleteRequest = new DMSRequest();
        deleteRequest.setUserId(userId);
        deleteRequest.setClientId(clientId);
        deleteRequest.setDocumentId(guid);
        deleteRequest.setContextId(contextId);
        documentManagementProcessor.deleteDocument(deleteRequest);
      });
    }    
    return new ResponseEntity<Response>(HttpStatus.OK);
  }
}
