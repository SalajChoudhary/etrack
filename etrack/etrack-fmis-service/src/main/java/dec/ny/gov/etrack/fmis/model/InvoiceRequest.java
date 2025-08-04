package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class InvoiceRequest implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String publicName;
  private Long applicantId;
  private String invoiceNum;
  private Address address;
  private BillingInvoiceRequest billing;
}
