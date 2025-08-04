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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SpatialInqDocument;
import dec.ny.gov.etrack.dcs.service.SpatialInquiryDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/spatial-inquiry")
public class SpatialInquiryDocumentController {

  @Autowired
  private SpatialInquiryDocumentService spatialInquiryDocumentService;

  private static Logger logger = LoggerFactory.getLogger(SpatialInquiryDocumentController.class);

  
  /**
   * Upload the Support document related to Spatial Inquiry.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * @param ingestionMetaData - Metadata for this request.
   * @param uploadFiles - files to be uploaded.
   * 
   * @return - Response with updated Metadata.
   */
  @PostMapping(value = "/upload", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  @ApiOperation(value="Upload the Spatial Inquiry Document into FileNt.")
  public IngestionResponse uploadSupportDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId, 
      @RequestPart IngestionRequest ingestionMetaData,
      @RequestParam MultipartFile[] uploadFiles) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into upload Spatial Inquiry document. User Id {}, Context Id {}", userId,
        contextId);
    return spatialInquiryDocumentService.uploadSpatialInquiryDocument(userId, contextId, inquiryId, authorization,
        ingestionMetaData, uploadFiles, false);
  }
  
  /**
   * Adding additional document to be uploaded.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * @param ingestionMetaData - Metadata for this request.
   * @param uploadFiles - files to be uploaded.
   * 
   * @return - Response after upload as an additional.
   */
  @PostMapping(value = "/additional-doc", consumes = "multipart/form-data",
      headers = {"Accept=application/json, application/multipart"})
  @ApiOperation(value="Uplaod the additional document for the uploaded documents "
      + "if its missing during initial upload  or user wanted to do it later.")
  public IngestionResponse uploadAdditionalSupportDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId, 
      @RequestPart IngestionRequest ingestionMetaData,
      @RequestParam MultipartFile[] uploadFiles) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into upload additional Spatial Inquiry document. User Id {}, Context Id {}", userId,
        contextId);
    return spatialInquiryDocumentService.uploadAdditionalSpatialDocument(userId, contextId, inquiryId, authorization,
        ingestionMetaData, uploadFiles);
  }

  /**
   * Reference the existing document if the user says the document is already uploaded.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param reference - Reference text.
   */
  @PostMapping(value = "/reference-doc")
  @ApiOperation(value="Store the existing document reference details for this request. "
      + "e.g. if Map needs to be uploaded but this is already in the upload Photos document then give the reference details."
      + " Mostly Page number which helps to avoid duplicate files getting uploaded..")
  public void referenceAlreadyUploadedDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId, 
      @RequestBody DocumentNameView reference) {
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into reference the already uploaded Spatial Inquiry document. User Id {}, Context Id {}", userId,
        contextId);
    spatialInquiryDocumentService.createReferenceForExistingDocument(userId, contextId, inquiryId, reference);
    logger.info("Exiting from reference the alraedy support document. User Id {}, Context Id {}", userId,
        contextId);
  }
  
  /**
   * Retrieve all the document names for the input inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - List of Document names.
   */
  @GetMapping(value="/documents")
  @ApiOperation(value="Retrieve all the document names for the requested inquiry id.")
  public List<DocumentName> getAllDocumentDisplayNames(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId) {
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into reference the already Spatial Inquiry document. User Id {}, Context Id {}", userId,
        contextId);
    return spatialInquiryDocumentService.retrieveAllDisplayNames(userId, contextId, inquiryId);
  }
  
  /**
   * Retrieve the document details associated with the requested document Id.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param documentId - Document Id.
   * 
   * @return - List of Document details.
   */
  @GetMapping(value="/document/{documentId}")
  @ApiOperation(value="Retrieve all the files uploaded for this document.")
  public List<DocumentNameView> getSpatialInquiryDocumentDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId, 
      @PathVariable @ApiParam(example = "123112", value="Document id.") final Long documentId) {
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieving all the documents and details associated with the spatial input document id {}."
        + " User Id {}, Context Id {}", documentId, userId, contextId);
    return spatialInquiryDocumentService.retrieveAllDocumentsAndFilesAssociatedWithDocumentId(
        userId, contextId, inquiryId, documentId);
  }
  
  /**
   * Delete the document uploaded for this requested Spatial Inquiry id.
   * 
   * @param documentIds - Document Ids.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - Response of this activity.
   */
  @DeleteMapping("/document/{documentIds}")
  @ApiOperation(value="Delete the list of document from FileNet for the requested document ids.")
  public ResponseEntity<Response> deleteDocument(
      @PathVariable @ApiParam(example = "23432, 243242", value="List of Document ids.") List<Long> documentIds,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") Long inquiryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering delete support Document(). User id: {}  Context Id: {}", userId, contextId);
    if (CollectionUtils.isEmpty(documentIds) 
        || !StringUtils.hasLength(userId) 
        || inquiryId == null 
        || inquiryId == 0) {
      
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST, 
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    logger.debug("Making call to service layer to delete support document:{} User id: {}  context id: {}",
        documentIds, userId, contextId);
    return spatialInquiryDocumentService.deleteDocument(userId, contextId, inquiryId, authorization, documentIds);
  }
  
  /**
   * Retrieve the File Content for the input document Id and file name.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * @param fileName - File name to be retrieved.
   * 
   * @return - Content of the file.
   */
  @GetMapping("/retrieveFileContent/{documentId}")
  @ApiOperation(value="Retrieve the content of the requested file name.")
  public ResponseEntity<byte[]> retrieveFileContent(
      @PathVariable @ApiParam(example = "", value="") String documentId,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "234872", value="Spatial Inquiry Id.") final Long inquiryId, 
      @RequestHeader @ApiParam(example = "adbc.pdf", value="File name of the content to be retrieved.") String fileName) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering retrieveFileContent() file name {}. User id: {}  context id: {}", fileName, userId, contextId);
    return spatialInquiryDocumentService.retrieveFileContent(userId, contextId, 
        authorization, inquiryId, Long.valueOf(documentId), fileName);
  }

  /**
   * Retrieve the list of required document for this inquiry id and category code.
   * 
   * @param userId - User who initiates this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * @param categoryCode - Spatial Inquiry Category code.
   * 
   * @return - List of Spatial Inquiry document.
   */
  @GetMapping(value = "/retrieve-reqd-si-document")
  @ApiOperation(value="Retrieve the list of Spatial Inquiry document for the input inquiry category.")
  public List<SpatialInqDocument> retrieveRequiredSpatialDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestHeader @ApiParam(example = "142131", value="Spatial Inquiry Id.") final Long inquiryId) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveRequiredSpatialDocument. User Id {}, Context Id {}", userId,
        contextId);
    return spatialInquiryDocumentService.retrieveRequiredSpatialDocument(userId, contextId, inquiryId);
  }

}
