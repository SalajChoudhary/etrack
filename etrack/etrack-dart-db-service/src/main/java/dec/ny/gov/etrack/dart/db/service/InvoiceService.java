package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.InvoiceFeeType;

@Service
public interface InvoiceService {
  
  /**
   * Retrieve the invoice details for the input invoice number.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param fmisInvoice - FMIS Invoice number.
   * 
   * @return - Invoice details.
   */
  Object retrieveInvoiceDetails(final String userId, final String contextId, final Long projectId,
      final String fmisInvoice);

  /**
   * Returns the Fee Type details for the permit user has applied so far.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Invoice Fee details.
   */
  List<InvoiceFeeType> retrieveInvoiceFeeDetails(final String userId, final String contextId, final Long projectId);

}
