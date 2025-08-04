package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class BillingInvoiceRequest implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String invoiceNum;
  private Long totalCharge;
  private List<ProjectType> types;
  private String checkNumber;
  private String checkRcvdDate;
  private Long checkAmt;
  private String notes;
  private String reason;
  private String cancelledUserName;
}
