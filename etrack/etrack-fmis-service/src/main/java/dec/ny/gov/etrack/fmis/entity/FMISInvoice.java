package dec.ny.gov.etrack.fmis.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_INVOICE")
public @Data class FMISInvoice implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_INVOICE_S")
  @SequenceGenerator(name = "E_INVOICE_S", sequenceName = "E_INVOICE_S", allocationSize = 1)
  private Long invoiceId;
  private Long publicId;
  private Long projectId;
  @Column(name="INVOICE_FEE_TYPE_1")
  private String invoiceFeeType1;
  @Column(name="INVOICE_FEE_TYPE_FEE_1")
  private Long invoiceFeeTypeFee1;
  @Column(name="INVOICE_FEE_TYPE_2")
  private String invoiceFeeType2;
  @Column(name="INVOICE_FEE_TYPE_FEE_2")
  private Long invoiceFeeTypeFee2;
  @Column(name="INVOICE_FEE_TYPE_3")
  private String invoiceFeeType3;
  @Column(name="INVOICE_FEE_TYPE_FEE_3")
  private Long invoiceFeeTypeFee3;
  private String fmisInvoiceNum;
  private String createdById;
  private Date createDate;
  private Date modifiedDate;
  private String modifiedById;
  private Integer deleteInd;
  private String vpsTxnId;
  private String paymentConfirmnId;
  private Integer invoiceStatusCode;
  private String checkNumber;
  private Date checkRcvdDate;
  private Long checkAmt;
  private Long paidAmt;
  private String decId;
  private String notes;
  private String cancelReason;
  private String cancelUserId;
}
