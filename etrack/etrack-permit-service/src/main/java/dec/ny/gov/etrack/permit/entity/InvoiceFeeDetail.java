package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
//@Table(name="E_INVOICE_FEE_TYPE")
public @Data class InvoiceFeeDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String permitTypeCode;
  private String permitTypeDesc;
  @Id
  private String invoiceFeeType;
  private Integer invoiceFee;
  
}
