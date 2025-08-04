package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.PaymentReceivedNoteEntity;

@Repository
public interface PaymentReceivedNoteRepo extends CrudRepository<PaymentReceivedNoteEntity, String>{

  @Query(value="select ift.permit_type_code, ptc.permit_type_desc, n.action_date, "
      + "n.action_note, n.action_type_code, n.project_note_id,  "
      + "n.comments, n.create_date, nat.action_type_desc, "
      + "inv.invoice_fee_type_1, inv.invoice_fee_type_2, inv.invoice_fee_type_3, n.created_by_id, n.modified_by_id, "
      + "n.modified_date, inv.fmis_invoice_num, inv.payment_confirmn_id, inv.vps_txn_id, "
      + "inv.invoice_fee_type_fee_1, inv.invoice_fee_type_fee_2, inv.invoice_fee_type_fee_3 "
      + "from {h-schema}e_project_note n, {h-schema}e_invoice inv, {h-schema}e_project_note_action_type nat, "
      + "{h-schema}e_invoice_fee_type ift, {h-schema}e_permit_type_code ptc  "
      + "where n.project_id=inv.project_id and n.action_type_code=nat.action_type_code and ift.permit_type_code=ptc.permit_type_code  "
      + "and (inv.invoice_fee_type_1=ift.invoice_fee_type or inv.invoice_fee_type_2=ift.invoice_fee_type "
      + "or inv.invoice_fee_type_3=ift.invoice_fee_type) and n.project_id=?1 and n.action_type_code=15 and inv.invoice_status_code=?2", nativeQuery = true)
  List<PaymentReceivedNoteEntity> findPaymentReceivedNoteDetails(final Long projectId, Integer invoiceStatusCode);
}
