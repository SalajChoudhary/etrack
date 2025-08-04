package dec.ny.gov.etrack.permit.entity;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class PaymentReceivedNoteEntity {

  @Id
  private String permitTypeCode;
  private String permitTypeDesc;
  private String actionDate;
  private String actionNote;
  private String actionTypeCode;
  private String projectNoteId;
  private String comments;
  private String createDate;
  private String actionTypeDesc;
  @Column(name="invoice_fee_type_fee_1")
  private Integer invoiceFeeTypeFee1;
  @Column(name="invoice_fee_type_fee_2")
  private Integer invoiceFeeTypeFee2;
  @Column(name="invoice_fee_type_fee_3")
  private Integer invoiceFeeTypeFee3;
  private String createdById;
  private String modifiedById;
  private Date modifiedDate;
  private String fmisInvoiceNum;
  private String paymentConfirmnId;
  private String vpsTxnId;
}
