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

@Service
public interface SupportDocumentService {

  /**
   * Upload the requested Support document.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param authorization - JWT Token.
   * @param ingestionRequest - Document upload request.
   * @param uploadFiles - Files to be uploaded into FileNet.
   * @param docClassification - Document Classification code.
   * 
   * @return - Document uploaded response.
   */
  IngestionResponse uploadSupportDocument(
      final String userId, final String contextId, final Long projectId, final String authorization,
      IngestionRequest ingestionRequest, final MultipartFile[] uploadFiles, final Integer docClassification);
  
  /**
   * Reference the existing document.
   * i.e. User can reference the existing document instead of uploading another document. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param reference - Document reference details.
   */
  void createReferenceForExistingDocument(
      final String userId, final String contextId, final Long projectId,
      final DocumentNameView reference);
  
  /**
   * Retrieve all the Document display names for the requested Project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Document Display names.
   */
  List<DocumentName> retrieveAllDisplayNames(
      final String userId, final String contextId, final Long projectId);

  /**
   * Retrieve all the file details associated with the document id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * 
   * @return - Document File(s) details.
   */
  List<DocumentFileView> retrieveAllFilesAssociatedWithDocumentId(
      final String userId, final String contextId, final Long projectId, final List<Long> documentId);

  /**
   * Delete the requested document(s).
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param authorization - JWT Token.
   * @param documentIds - List of Document Ids to be deleted.
   * 
   * @return - Deleted Document response.
   */
  ResponseEntity<Response> deleteDocument(
      final String userId, final String contextId, final Long projectId,
      String authorization, List<Long> documentIds);
  
  /**
   * Retrieve all the document details and files associated with the document id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * 
   * @return - Returns the Document and file details.
   */
  List<DocumentNameView> retrieveAllDocumentsAndFilesAssociatedWithDocumentId(final String userId,
      final String contextId, final Long projectId, Long documentId);
  
  /**
   * Retrieve the file content.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param authorization - JWT Token.
   * @param projectId - Project Id.
   * @param documentId - Document Id.
   * @param fileName - File name.
   * 
   * @return - File Content with response {@link ResponseEntity}
   */
  ResponseEntity<byte[]> retrieveFileContent(
      final String userId, final String contextId, final String authorization,
      final Long projectId, Long documentId, String fileName);
  
  /**
   * Upload the additional document to be added.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param authorization - JWT Token.
   * @param ingestionRequest - Document upload request.
   * @param uploadFiles - Files to be uploaded into FileNet.
   * 
   * @return - Document additional document Response.
   */
  IngestionResponse uploadAdditionalSupportDocument(
      final String userId, final String contextId, final Long projectId,
      final String authorization, IngestionRequest ingestionRequest, MultipartFile[] uploadFiles);

  /**
   * Update the Support Document Metadata.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param authorization - JWT Token.
   * @param request - Metadata to be updated.
   * @param documentId - Document Id.
   * 
   * @return - Updated Support document Metadata.
   */
  ResponseEntity<Response> updateSupportDocumentMetadata(final String userId, final String contextId,
      final String authorization, IngestionRequest request, final Long documentId);
}
