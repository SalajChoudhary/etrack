package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;

@Repository
public interface InvoiceRepo extends CrudRepository<InvoiceEntity, Long> {
  List<InvoiceEntity> findAllByProjectId(Long projectId);
  List<InvoiceEntity> findAllByProjectIdOrderByCreateDateDesc(Long projectId);
//  List<String> findStatusDescByStatusId(Long statusId);
  List<InvoiceEntity> findAllByProjectIdAndFmisInvoiceNum(Long projectId, String fmisInvoice);
  
  @Query(value="select i.invoice_fee_type fee_type, p.permit_type_desc permit_type "
      + "from {h-schema}e_invoice_fee_type i, {h-schema}e_permit_type_code p where i.permit_type_code=p.permit_type_code "
      + "and i.invoice_fee_type in (?1)", nativeQuery=true)
  List<String> findAllFeeTypeDesc(Set<String> feeTypes);
  
  @Query(value="select a.permit_type_code from {h-schema}e_application a, {h-schema}e_invoice_fee_type ft "
      + "where a.permit_type_code=ft.permit_type_code and a.project_id=?1", nativeQuery=true)
  List<String> findInvoiceFeeEligiblePermits(final Long projectId);

  @Query(value="select invoice_status_code, invoice_status_desc from {h-schema}e_invoice_status_code", nativeQuery = true)
  List<String> findAllInvoiceStatus();
}
