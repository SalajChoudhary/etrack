package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class Invoice  implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String invoiceId;
  private Long dueAmount;
  private Long paidAmount;
  private String status;
  private String invoiceDate;
  private String payReference;
  private String checkNumber;
  private String checkRcvdDate;
  private Long checkAmt;
  private List<InvoiceFeeType> types;
  private String notes;
  private String reason;
}
