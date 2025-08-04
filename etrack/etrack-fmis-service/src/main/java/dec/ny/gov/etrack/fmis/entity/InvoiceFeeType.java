package dec.ny.gov.etrack.fmis.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_INVOICE_FEE_TYPE")
public @Data class InvoiceFeeType implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String invoiceFeeType;
  private Long invoiceFee;
}
