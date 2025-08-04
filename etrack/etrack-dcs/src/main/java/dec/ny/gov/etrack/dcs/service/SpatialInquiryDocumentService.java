package dec.ny.gov.etrack.dcs.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dcs.model.DocumentFileView;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SpatialInqDocument;

@Service
public interface SpatialInquiryDocumentService {

  /**
   * Upload the Spatial Inquiry document.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param authorization - JWT Token.
   * @param ingestionRequest - Metadata details to be uploaded.
   * @param uploadFiles - File to be uploaded.
   * @param docReplaceInd - Document replacement indicator.
   * 
   * @return - Uploaded response.
   */
  IngestionResponse uploadSpatialInquiryDocument(final String userId, final String contextId, Long inquiryId, final String authorization,
      IngestionRequest ingestionRequest, final MultipartFile[] uploadFiles, final boolean docReplaceInd);

  /**
   * Add the reference details for requested document. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param reference - Reference text like Page #4.
   */
  void createReferenceForExistingDocument(final String userId, final String contextId, final Long inquiryId,
      final DocumentNameView reference);

  /**
   * Retrieve all the document display name(s) for the requested inquiry id. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - List of Display names.
   */
  List<DocumentName> retrieveAllDisplayNames(final String userId, final String contextId, final Long inquiryId);

  /**
   * Retrieve all the all files associated with the inquiry and document id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param documentId - Document Id.
   * 
   * @return - Document Files details.
   */
  List<DocumentFileView> retrieveAllFilesAssociatedWithDocumentId(
      final String userId, final String contextId, final Long inquiryId,
      final List<Long> documentId);
  
  /**
   * Delete the document for the requested document ids.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param authorization - JWT Token.
   * @param documentIds - Document Ids.
   * 
   * @return - Delete document response.
   */
  ResponseEntity<Response> deleteDocument(String userId, String contextId, Long inquiryId,
      String authorization, List<Long> documentIds);
  
  /**
   * Retrieve all the documents and files associated with this document and inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param documentId - Document Id.
   * 
   * @return - List of documents and files for the document.
   */
  List<DocumentNameView> retrieveAllDocumentsAndFilesAssociatedWithDocumentId(String userId,
      String contextId, Long inquiryId, Long documentId);

  /**
   * Retrieve the file content for the input document and file name.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param authorization - JWT Token.
   * @param inquiryId - Spatial Inquiry Id.
   * @param documentId - Document Id.
   * @param fileName - File name to be retrieved.
   * 
   * @return - File content.
   */
  ResponseEntity<byte[]> retrieveFileContent(String userId, String contextId, String authorization,
      Long inquiryId, Long documentId, String fileName);
  
  /**
   * Upload the additional document for the Spatial Inquiry id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * @param authorization - JWT Token.
   * @param ingestionRequest - Metadata details to be uploaded.
   * @param uploadFiles - File to be uploaded.
   * 
   * @return - Spatial inquiry upload response.
   */
  IngestionResponse uploadAdditionalSpatialDocument(String userId, String contextId, Long inquiryId,
      String authorization, IngestionRequest ingestionRequest, MultipartFile[] uploadFiles);

  /**
   * Retrieve all the list of required documents for this Spatial Inquiry Category code.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Spatial Inquiry Id.
   * 
   * @return - List of documents required details for this category code.
   */
  List<SpatialInqDocument> retrieveRequiredSpatialDocument(final String userId, final String contextId, final Long inquiryId);
}
