package dec.ny.gov.etrack.dms.ecmaas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;

public interface ECMaaSWrapper {

  /**
   * Upload the document into FileNet via Akana API.
   * 
   * @param ingestionRequest - Metadata details for the 
   * @param uploadDocuments - Files to be uploaded.
   * 
   * @return
   */
  ResponseEntity<IngestionResponse> uploadDocument(IngestionRequest ingestionRequest,
      MultipartFile[] uploadDocuments);
  
  /**
   * Update the document metadata in the FileNet system.
   * 
   * @param ingestionRequest - Metadata details which needs to be updated.
   * 
   * @return - Updated metadata response.
   */
  ResponseEntity<IngestionResponse> updatedMetaData(IngestionRequest ingestionRequest);
  
  /**
   * Request FileNet system to delete the request.
   * 
   * @param deleteRequest - Delete request details.
   * 
   * @return - Delete document response.
   */
  ResponseEntity<Response> deleteDocument(DMSRequest deleteRequest);

  /**
   * Retrieve the document for the input request GUID.
   * 
   * @param searchRequest - Document Search request details.
   * 
   * @return - Response of the document retrieval.
   */
  ResponseEntity<DMSDocumentResponse> retrieveDocumentByGuid(DMSRequest searchRequest);

  /**
   * Retrieve the File content from FileNet.
   * 
   * @param retrieveContentRequest - Retrieve File Content request.
   * 
   * @return - Returns File content response.
   */
  ResponseEntity<byte[]> retrieveDocumentContent(DMSRequest retrieveContentRequest);

}
