package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import lombok.Data;

public @Data class PaymentActionNote implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String projectType1;
  private Integer projectTypeFee1;
  private String projectType2;
  private Integer projectTypeFee2;
  private String projectType3;
  private Integer projectTypeFee3;
  private String invoiceNumber; 
  private String paymentReference;
  private Integer totalAmount;
}
