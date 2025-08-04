package dec.ny.gov.etrack.dcs.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocumentFileView;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/support-document")
public class SupportDocumentController {

  @Autowired
  private SupportDocumentService supportDocumentService;

  private static Logger logger = LoggerFactory.getLogger(SupportDocumentController.class);

  /**
   * Upload the support document for the input project.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param docClassification - Document Classification.
   * @param projectId - Project Id.
   * @param ingestionMetaData - Metadata for the upload request.
   * @param uploadFiles - Files to be uploaded.
   * 
   * @return Uploaded document response.
   */
  @PostMapping(value = "/upload", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  public IngestionResponse uploadSupportDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader final Integer docClassification,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestPart IngestionRequest ingestionMetaData,
      @RequestParam MultipartFile[] uploadFiles) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into upload Support document. User Id {}, Context Id {}", userId,
        contextId);
    return supportDocumentService.uploadSupportDocument(
        userId, contextId, projectId, authorization,
        ingestionMetaData, uploadFiles, docClassification);
  }

  /**
   * Update the document metadata for the document id.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param request - Metadata details for the request.
   * 
   * @return - Document Metadata response.
   */
  @PutMapping("/updateMetadata/{documentId}")
  public ResponseEntity<Response> updateDocumentMetadata(
      @PathVariable final Long documentId, 
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
      @RequestBody IngestionRequest request) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into upload Support document. User Id {}, Context Id {}", userId,
        contextId);
    return supportDocumentService.updateSupportDocumentMetadata(
        userId, contextId, authorization, request, documentId);
    
  }

  /**
   * Request to upload the additional document for the input request document id.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param projectId - Project Id.
   * @param ingestionMetaData - Metadata for the upload request.
   * @param uploadFiles - Files to be uploaded.
   * 
   * @return - Response of the additional document upload.
   */
  @PostMapping(value = "/additional-doc", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  public IngestionResponse uploadAdditionalSupportDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestPart IngestionRequest ingestionMetaData,
      @RequestParam MultipartFile[] uploadFiles) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into upload additional Support document. User Id {}, Context Id {}", userId,
        contextId);
    return supportDocumentService.uploadAdditionalSupportDocument(userId, contextId, projectId, authorization,
        ingestionMetaData, uploadFiles);
  }

  
  /**
   * Add the reference of the existing document for the new document request. 
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param reference - Reference text.
   */
  @PostMapping(value = "/reference-doc")
  public void referenceAlreadyUploadedDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody DocumentNameView reference) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into reference the alraedy support document. User Id {}, Context Id {}", userId,
        contextId);
    supportDocumentService.createReferenceForExistingDocument(userId, contextId, projectId, reference);
    logger.info("Exiting from reference the alraedy support document. User Id {}, Context Id {}", userId,
        contextId);
  }
  
  /**
   * Retrieve all the display names for the input project id. 
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - List of display names.
   */
  @GetMapping(value="/documents")
  public List<DocumentName> retrieveDisplayNames(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into reference the alraedy support document. User Id {}, Context Id {}", userId,
        contextId);
    return supportDocumentService.retrieveAllDisplayNames(userId, contextId, projectId);
  }
  
  /**
   * Retrieve all the files details for the input project id and document id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * 
   * @return - List of document files.
   */
  @GetMapping(value="/document/files/{documentId}")
  public List<DocumentFileView> getSupportDocumentFiles(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "23432, 243242", value="List of Document ids.") final List<Long> documentId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieving file details associated with the input document id {}."
        + " User Id {}, Context Id {}", documentId, userId, contextId);
    return supportDocumentService.retrieveAllFilesAssociatedWithDocumentId(userId, contextId, projectId, documentId);
  }
  
  /**
   * Retrieve the Support document and file details for the input project id and document id.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * 
   * @return - List of Document and file details.
   */
  @GetMapping(value="/document/{documentId}")
  @ApiOperation(value="Retrieve all the support document details and list of files uploaded for each document.")
  public List<DocumentNameView> getSupportDocumentDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "123112", value="Document id.") final Long documentId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieving all the documents and details associated with the input document id {}."
        + " User Id {}, Context Id {}", documentId, userId, contextId);
    return supportDocumentService.retrieveAllDocumentsAndFilesAssociatedWithDocumentId(
        userId, contextId, projectId, documentId);
  }
  
  /**
   * Delete all the documents for the requested document Id.
   * 
   * @param documentIds - Document Ids
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Deleted document response.
   */
  @DeleteMapping("/document/{documentIds}")
  @ApiOperation(value="Delete the requested documents from FileNet.")
  public ResponseEntity<Response> deleteDocument(
      @PathVariable @ApiParam(example = "23432, 243242", value="List of Document ids.") List<Long> documentIds,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering delete support Document(). User id: {}  Context Id: {}", userId, contextId);
    if (CollectionUtils.isEmpty(documentIds) 
        || !StringUtils.hasLength(userId) 
        || projectId == null 
        || projectId == 0) {
      
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST, 
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    logger.debug("Making call to service layer to delete support document:{} User id: {}  context id: {}",
        documentIds, userId, contextId);
    return supportDocumentService.deleteDocument(userId, contextId, projectId, authorization, documentIds);
  }
  
  /**
   * Retrieve the File content for the input document id and file name.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param projectId - Project Id.
   * @param fileName - File Name.
   * 
   * @return - File content.
   */
  @GetMapping("/retrieveFileContent/{documentId}")
  @ApiOperation(value="Retrieve the file content for the requested file name.")
  public ResponseEntity<byte[]> retrieveFileContent(
      @PathVariable @ApiParam(example = "123112", value="Document id.") final String documentId,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestHeader @ApiParam(example = "abdc.pdf", value="File name with extension") final String fileName) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering retrieveFileContent() file name {}. User id: {}  context id: {}", fileName, userId, contextId);
    return supportDocumentService.retrieveFileContent
        (userId, contextId, authorization, projectId, Long.valueOf(documentId), fileName);
  }
}
