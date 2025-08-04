package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class InvoiceOutput implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private List<InvoiceStatus> invoiceStatus;

}
