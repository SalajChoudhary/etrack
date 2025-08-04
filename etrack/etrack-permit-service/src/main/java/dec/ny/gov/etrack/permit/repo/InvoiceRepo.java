package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import dec.ny.gov.etrack.permit.entity.Invoice;

public interface InvoiceRepo extends CrudRepository<Invoice, Long> {
  
//  @Query("select i from Invoice i where i.vps_confirmn_id is not null and project_id= :projectId and i.invoice_status=2")
  @Query("select i from Invoice i where i.paymentConfirmnId is not null and projectId=?1 and i.invoiceStatusCode=?2")
  Invoice findInvoiceByProjectIdAndStatus(final Long projectId, final Integer status);
}
