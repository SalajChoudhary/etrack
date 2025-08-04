package dec.ny.gov.etrack.dms.processor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;

public interface DocumentManagementProcessor {

  /**
   * Upload the document into FileNet.
   * 
   * @param ingestionRequest - Metadata properties.
   * @param uploadDocuments - Files to be uploaded.
   * 
   * @return - Uploaded document response.
   */
  ResponseEntity<IngestionResponse> uploadDocument(IngestionRequest ingestionRequest,
      MultipartFile[] uploadDocuments);
  
  /**
   * Update the Metadata properties.
   * 
   * @param updateMetaDataRequest - Metadata details.
   * 
   * @return - Updated Metadata response.
   */
  ResponseEntity<IngestionResponse> updateMetadataDocument(IngestionRequest updateMetaDataRequest);
  
  /**
   * Delete the document from FileNet for the input request.
   * 
   * @param deleteRequest - Delete request details.
   * 
   * @return - Deleted document details.
   */
  ResponseEntity<Response> deleteDocument(DMSRequest deleteRequest);

  /**
   * Retrieve the document details from GileNet for the input request.
   * 
   * @param searchRequest - Search details.
   * 
   * @return - Document details with response.
   */
  ResponseEntity<DMSDocumentResponse> retrieveDocumentsByGuid(DMSRequest searchRequest);
  
  /**
   * Retrieve the file content from FileNet for the input file name and search details.
   * 
   * @param searchRequest - Search request details.
   * @param fileName - File name.
   * 
   * @return - Retrieved Document contents.
   */
  ResponseEntity<byte[]> retrieveDocumentContent(DMSRequest searchRequest, String fileName);
}
