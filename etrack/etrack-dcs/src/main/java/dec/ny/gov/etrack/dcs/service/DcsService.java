package dec.ny.gov.etrack.dcs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;

public interface DcsService {

  /**
   * Delete the requested document from FileNet.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param token - JWT Token.
   * @param clientId - Client Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Response of the deleted document request.
   */
  ResponseEntity<Response> deleteDocument(String documentId, String userId, String token,
      String clientId, String contextId);

  /**
   * Retrieve the File content for the requested document id and file name.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param token - JWT Token.
   * @param clientId - Client Id.
   * @param fileName - File name.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - File content.
   */
  ResponseEntity<byte[]> retrieveFileContent(String documentId, String userId, String token,
      String clientId, String fileName, String contextId);
  
  /**
   * Update the document metadata.
   * 
   * @param documentId - Document Id.
   * @param userId - User who initiates this request.
   * @param request - Metadata details.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param documentClass - Document CLass name.
   * @param projectId - Project Id.
   * 
   * @return - Document Updated response.
   */
  ResponseEntity<Response> updateDocumentMetadata(String documentId, String userId, 
      IngestionRequest request, String contextId, String token, final String documentClass, final Long projectId);

  /**
   * Upload the document into FileNet using another service etrack-dms.
   * 
   * @param userId - User who initiates this request.
   * @param token - JWT Token.
   * @param docClass - Document Class name.
   * @param districtId - Enterprise District Id.
   * @param ingestionMetaData  - Metadata details.
   * @param uploadFiles - Files to be uploaded into FileNet.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Uploaded document response.
   */
  ResponseEntity<IngestionResponse> uploadDocument(String userId, String token,
      String docClass, String districtId, IngestionRequest ingestionMetaData,
      MultipartFile[] uploadFiles, String contextId);

  /**
   * Replace the existing document with new document.
   * 
   * @param userId - User who initiates this request.
   * @param docClass - Document Class name.
   * @param districtId - Enterprise District Id.
   * @param ingestionMetaData  - Metadata details.
   * @param uploadFiles - Files to be uploaded into FileNet.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * 
   * @return - Document replacement response.
   */
  ResponseEntity<IngestionResponse> replaceWithNewDocument(String userId, String docClass,
      String districtId, IngestionRequest ingestionMetaData, MultipartFile[] uploadFiles,
      String contextId, String token);
}
