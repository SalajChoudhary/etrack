package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.InvoiceFeeType;

@Repository
public interface InvoiceFeeTypeRepo extends CrudRepository<InvoiceFeeType, String> {
  @Query(value="select distinct permit_type_code, invoice_fee, invoice_fee_desc, invoice_fee_type "
      + "from {h-schema}e_invoice_fee_type where permit_type_code in ("
      + "select DISTINCT permit_type_code from {h-schema}e_application where project_id=?1 "
      + "and permit_type_code not like 'GP%')", nativeQuery = true)
  List<InvoiceFeeType> findAllInvoiceFeeTypesByProjectId(final Long projectId);
}

