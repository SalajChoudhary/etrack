package dec.ny.gov.etrack.dcs.controller;


import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.google.common.net.HttpHeaders;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.service.DcsService;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import dec.ny.gov.etrack.dcs.util.DCSServiceUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping("/dcs")
public class DcsController {

  @Autowired
  private DcsService service;
  
  @Autowired
  private DCSServiceUtil dcsServiceUtil;
  
  @Autowired
  private SupportDocumentService supportDocumentService;

  private static Logger logger = LoggerFactory.getLogger(DcsController.class);
  
  /**
   * Delete the requested document for the input request document Id.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param documentClass - Document Class name.
   * 
   * @return - Delete the document.
   */
  @DeleteMapping("/deleteDocument/{documentId}")
  @ApiOperation(value="Delete the requested historical document from FileNet system.")
  public ResponseEntity<Response> deleteDocument(
      @PathVariable @ApiParam(example = "112321", value="Document Id to be deleted.") String documentId,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "CORRESPONDENCE", value="Document Class name.") String documentClass) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering deleteDocument(). User id: {}  Context Id: {}", userId, contextId);
    if (!(dcsServiceUtil.isDocumentClassValid(documentClass))) {
      logger.error(
          "The document class: {} is invalid. Exiting deleteDocument(). User id: {}  contextId: {}",
          documentClass, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          "Error : Invalid Request.");
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClass);
    logger.debug("Making call to service layer to delete document:{} User id: {}  context id: {}",
        documentId, userId, contextId);
    return service.deleteDocument(documentId, userId, authorization, clientId, contextId);
  }

  /**
   * Request to receive the File content.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param projectId - Project Id.
   * @param fileName - File Name.
   * @param documentClass - Document Class name.
   * 
   * @return - File Content.
   */
  @GetMapping("/retrieveFileContent/{documentId}")
  @ApiOperation(value="Retrieve the File content from FileNet for the input document id.")
  public ResponseEntity<byte[]> retrieveFileContent(
      @PathVariable @ApiParam(example = "112321", value="Document Id to be deleted.") String documentId,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader(required = false) @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestHeader @ApiParam(example = "abdc.pdf", value="File name with extension.") String fileName, 
      @RequestHeader @ApiParam(example = "CORRESPONDENCE", value="Document Classification name. SUPPORTDOCUMENT, NOTICE, CORRESPONDENCE etc...") String documentClass) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering retrieveFileContent() file name {}. User id: {}  context id: {}", fileName, userId, contextId);
    if (!(dcsServiceUtil.isDocumentClassValid(documentClass))) {
      logger.error(
          "The document class: {} is invalid. Exiting retrieveFileContent(). User id: {}  contextId: {}",
          documentClass, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          "Error : Invalid Request.");
    }
    String clientId = dcsServiceUtil.retrieveClientId(documentClass);
    logger.debug(
        "Making call to service layer to retrieve document:{} . User id: {} Context id: {}",
        documentId, userId, contextId);
    if (projectId != null && projectId > 0) {
      return supportDocumentService.retrieveFileContent(userId, contextId, authorization, 
          projectId, Long.valueOf(documentId), fileName);
    } else {
      return service.retrieveFileContent(documentId, userId, authorization, clientId, fileName,
          contextId);
    }
  }


  /**
   * Request to update the Document Metadata.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param documentClass - Document Class name.
   * @param request - Metadata.
   * @param projectId - Project Id.
   * 
   * @return - Update Document Metadata response.
   */
  @PutMapping("/updateMetadata/{documentId}")
  @ApiOperation(value="Update the document metadata which is already uploaded.")
  public ResponseEntity<Response> updateDocumentMetadata(
      @PathVariable @ApiParam(example = "112321", value="Document Id to be deleted.") String documentId, 
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
      @RequestHeader @ApiParam(example = "CORRESPONDENCE", value="Document Classification name. SUPPORTDOCUMENT, NOTICE, CORRESPONDENCE etc...") String documentClass,
      @RequestBody IngestionRequest request, 
      @RequestHeader(required=false) @ApiParam(example = "123213", value="Project Id") Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering updateDocumentMetadata(). User id: {}  context id: {}", userId,
        contextId);
    String projectIStr = request.getMetaDataProperties().get("projectID");
    if (StringUtils.hasLength(projectIStr)) {
      projectId = Long.valueOf(projectIStr);
    } else {
      request.getMetaDataProperties().put("projectID", "0");
    }
    ResponseEntity<Response> resp = service.updateDocumentMetadata(documentId, userId, 
        request, contextId, authorization, documentClass, projectId);
    logger.info(
        "DCS Controller return value updateDocumentMetadata(), Status code value {} , user id: {}  context Id {} ",
        resp.getStatusCodeValue(), userId, contextId);
    return resp;
  }

  /**
   * Request to upload the document.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param docClassification - Document Class name.
   * @param districtId - Enterprise District Id.
   * @param ingestionMetaData - Metadata for the upload document.
   * @param uploadFiles - Files to be uploaded.
   * 
   * @return - Upload document response.
   */
  @PostMapping(value = "/uploadDocument", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  @ApiOperation(value="Upload the requested document into FileNet system.")
  public ResponseEntity<IngestionResponse> uploadDocument(
      @RequestHeader(required = true) @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader(required = true) @ApiParam(example = "CORRESPONDENCE", value="Document Classification name. SUPPORTDOCUMENT, NOTICE, CORRESPONDENCE etc...") String docClassification, 
      @RequestHeader @ApiParam(example = "7878324", value="Enterprise District Id") String districtId,
      @RequestPart(value = "ingestionMetaData", required = true) IngestionRequest ingestionMetaData,
      @RequestParam(value = "uploadFiles", required = true) MultipartFile[] uploadFiles) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering uploadDocument(). User id: {}  context id: {}", userId, contextId);
    ResponseEntity<IngestionResponse> ingestDocResponse = null;
    if (!(dcsServiceUtil.isDocumentClassValid(docClassification))) {
      logger.error(
          "The document class: {} is invalid. Exiting uploadDocument(). User id: {}  contextId: {}",
          docClassification, userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    
    if (!dcsServiceUtil.isContainsValidFiles(uploadFiles, contextId)) {
      throw new ValidationException(DCSServiceConstants.INVALID_ZIP_FILE,
          DCSServiceConstants.INVALID_ZIP_FILE_MSG);
    }
    
    if (dcsServiceUtil.isNotValidFoilStatus(ingestionMetaData, contextId)) {
      logger.error("Received NonReleaseCode when FOIL status as REL. User id: {}  context Id: {}",
          userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    logger.debug(
        "Making call to Service layer to uploadDocument for districtId:{} . User id: {}  context id: {}",
        districtId, userId, contextId);
    ingestDocResponse = service.uploadDocument(userId, authorization, docClassification, districtId,
        ingestionMetaData, uploadFiles, contextId);
    return ingestDocResponse;
  }

  /**
   * Request to replace the existing document with new one.
   * 
   * @param userId - User who initiates this request.
   * @param docClassification - Document Class name.
   * @param districtId - Enterprise District Id.
   * @param authorization - JWT Token.
   * @param ingestionMetaData - Metadata for the upload document.
   * @param uploadFiles - Files to be uploaded.
   * 
   * @return - Returns the replace document response.
   */
  @PostMapping(value = "/replaceDocument", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  @ApiOperation(value="Replace the existing document with new one. FileNet system doesn't have replaced option. "
      + "So, eTrack will delete the existing one from FileNet adn create new one.")
  public ResponseEntity<IngestionResponse> replaceWithNewDocument(
      @RequestHeader(required = true) @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(required = true) @ApiParam(example = "CORRESPONDENCE", value="Document Classification name. SUPPORTDOCUMENT, NOTICE, CORRESPONDENCE etc...") String docClassification, 
      @RequestHeader @ApiParam(example = "7878324", value="Enterprise District Id") String districtId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestPart(value = "ingestionMetaData", required = true) IngestionRequest ingestionMetaData,
      @RequestParam(value = "uploadFiles", required = true) MultipartFile[] uploadFiles) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering replaceDocument(). User id: {}  context id: {}", userId, contextId);
    ResponseEntity<IngestionResponse> ingestDocResponse = null;
    
    if (!(dcsServiceUtil.isDocumentClassValid(docClassification))) {
      logger.error(
          "The document class: {} is invalid. Exiting replaceWithNewDocument(). User id: {}  contextId: {}",
          docClassification, userId, contextId);
      throw new ValidationException("INVALID_DOC_CLASS_REQ",
          "Document Classification is not a valid one.");
    }
    
    if (!dcsServiceUtil.isContainsValidFiles(uploadFiles, contextId)) {
      throw new ValidationException(DCSServiceConstants.INVALID_ZIP_FILE,
          DCSServiceConstants.INVALID_ZIP_FILE_MSG);
    }
    
    logger.debug(
        "Making call to Service layer to replaceDocument for districtId:{} . User id: {}  context id: {}",
        districtId, userId, contextId);
    ingestDocResponse = service.replaceWithNewDocument(userId, docClassification, districtId,
        ingestionMetaData, uploadFiles, contextId, authorization);
    return ingestDocResponse;
  }
}
