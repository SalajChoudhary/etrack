package dec.ny.gov.etrack.fmis.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.fmis.model.BillingInvoiceRequest;
import dec.ny.gov.etrack.fmis.model.InvoiceResponseBody;

@Service
public interface FMISService {
  
  /**
   * Create new invoice by invoking FMIS system.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param invoice - Billing invoice request.
   * @param token - JWT token.
   * 
   * @return - Invoice details with newly created new invoice id.
   */
  Object createInvoice(final String userId, final String contextId,
      final Long projectId, BillingInvoiceRequest invoice, final String token);
  
  /**
   * Cancel the invoice by invoking FMIS system.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param cancelInvoiceRequest - Cancel Invoice request.
   * 
   * @return - Cancelled invoice details.
   */
  ResponseEntity<InvoiceResponseBody> cancelInvoice(final String userId, final String contextId,
      final Long projectId, final BillingInvoiceRequest cancelInvoiceRequest);
  
  /**
   * Update the Confirmation number received from VPS system.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param fmisInvoiceNum - FMIS Invoice number.
   * @param confirmationNumber - VPS Confirmation number.
   * 
   * @return - Invoice response with status.
   */
  ResponseEntity<InvoiceResponseBody> updateConfirmationNumber(final String userId, final String contextId,
      final Long projectId, final String fmisInvoiceNum, final String confirmationNumber);

  /**
   * Refresh the invoice status by calling FMIS at regular interval. This is scheduled process.
   */
  void getInvoiceStatus();
  
  /**
   * Update the invoice notes added by the user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param billingInvoiceRequest - Billing invoice request.
   * 
   * @return - Updated Invoice details.
   */
  Object updateInvoice(String userId, String contextId, Long projectId,
      BillingInvoiceRequest billingInvoiceRequest);
}
