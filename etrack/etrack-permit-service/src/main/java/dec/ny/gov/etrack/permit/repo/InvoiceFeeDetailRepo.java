package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.InvoiceFeeDetail;

@Repository
public interface InvoiceFeeDetailRepo extends CrudRepository<InvoiceFeeDetail, String>{
  @Query(value="select inv.invoice_fee, pt.permit_type_code, pt.permit_type_desc, inv.invoice_fee_type "
      + "from {h-schema}e_invoice_fee_type inv, {h-schema}e_permit_type_code pt where "
      + "inv.permit_type_code=pt.permit_type_code", nativeQuery = true)
  List<InvoiceFeeDetail> findFeeDetailsForFeeTypes();
}
