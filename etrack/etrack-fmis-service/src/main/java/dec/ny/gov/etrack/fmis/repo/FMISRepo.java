package dec.ny.gov.etrack.fmis.repo;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.fmis.entity.Applicant;

@Repository
public interface FMISRepo extends CrudRepository<Applicant, String> {

  @Query(value = "select distinct p.public_id as public_id from {h-schema}e_public p "
      + "where p.project_id= ?1 and p.public_id= ?2", nativeQuery = true)
  public String findByProjectIdAndApplicantId(final Long projectId, final Long applicantId);

  @Query(
      value = "select distinct p.public_id as public_id, p.display_name as display_name, p.first_name "
          + "as first_name, p.last_name as last_name, ad.street1 as street1, ad.street2 as street2, "
          + "ad.city as city, ad.state as state, ad.country as country, ad.zip as zip, ad.email_address email, "
          + "ad.home_phone_number as home_phone_number, ad.business_phone_number as business_phone_number, "
          + "ad.cell_phone_number as cell_phone_number from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address ad "
          + "where p.public_id=r.public_id and r.address_id=ad.address_id and "
          + "(r.role_type_id =1 or r.legally_responsible_type_code in (1,2,3)) and p.selected_in_etrack_ind = 1 "
          + "and p.project_id=?1 order by p.public_id fetch first 1 row only",
      nativeQuery = true)
  public List<Applicant> findApplicantDetails(final Long projectId);

  @Transactional
  @Modifying
  @Query(
      value = "update {h-schema}e_invoice set vps_txn_id=?3, invoice_status_code=1, "
          + "modified_by_id=?1, modified_date=?4 where fmis_invoice_num=?2", nativeQuery = true)
  public void updateTransactionNumber(final String userId, final String invoiceNumber,
      final String transactionId, final Date modifiedDate);

  @Transactional
  @Modifying
  @Query(
      value = "update {h-schema}e_invoice set payment_confirmn_id=?3, invoice_status_code=?5, modified_by_id='vps', paid_amt=?6 "
          + "modified_date=?4 where fmis_invoice_num=?1 and vps_txn_id=?2", nativeQuery = true)
  public void updateConfirmationNumber(final String invoiceNumber,
      final String transactionId, final String confirmationNumber, final Date modifiedDate, final Integer status, final Long paidAmount);

  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_invoice set paid_amt=?3, invoice_status_code=?4, payment_confirmn_id=?2, modified_by_id='fmis', "
      + "modified_date=sysdate where fmis_invoice_num=?1 and invoice_status_code=1", nativeQuery=true)
  public int updateFMISReceiptForPaidInvoice(final String invoiceNumber, final String receiptNumber, 
      final Integer paidAmount, final Integer statusCode);
  
  @Query(value="select dec_id from {h-schema}e_facility where project_id=?1", nativeQuery=true)
  String findDecIdByProjectId(final Long projectId);
}
