package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_INVOICE")
public @Data class Invoice implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long invoiceId;
  private Long projectId;
  @Column(name="INVOICE_FEE_TYPE_1")
  private String invoiceFeeType1;
  @Column(name="INVOICE_FEE_TYPE_FEE_1")
  private Integer invoiceFeeTypeFee1;
  @Column(name="INVOICE_FEE_TYPE_2")
  private String invoiceFeeType2;
  @Column(name="INVOICE_FEE_TYPE_FEE_2")
  private Integer invoiceFeeTypeFee2;
  @Column(name="INVOICE_FEE_TYPE_3")
  private String invoiceFeeType3;
  @Column(name="INVOICE_FEE_TYPE_FEE_3")
  private Integer invoiceFeeTypeFee3;
  private String fmisInvoiceNum;
  private Date createDate;
  private Date modifiedDate;
  private String vpsTxnId;
  private String paymentConfirmnId;
  private Integer invoiceStatusCode;
  private String checkNumber;
  private Date checkRcvdDate;
  private Long checkAmt;
  private Long paidAmt;
  private String decId;
  private String modifiedById;
}
