package dec.ny.gov.etrack.dart.db.service;

import org.springframework.stereotype.Service;

@Service
public interface GenerateReportService {
  
  /**
   * Generate the Invoice report for the input invoice number.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id. - Project Id.
   * @param invoiceNum - FMIS Invoice number.
   * 
   * @return - array of bytes of the invoice report.
   */
  byte[] retrieveInvoiceReport(final String userId, final String contextId, final Long projectId, final String invoiceNum);

  /**
   * Generate and return the Permit Cover sheet report to show/send to the user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Permit Cover sheet report in array of bytes.
   */
  byte[] retrievePermitCoverSheetReport(String userId, String contextId, Long projectId);
  
  /**
   * Generate and return the uploaded documents report.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Uploaded document report in array of bytes.
   */
  byte[] generateDocumentsUploadedReport(final String userId, final String contextId, final Long projectId);
}
