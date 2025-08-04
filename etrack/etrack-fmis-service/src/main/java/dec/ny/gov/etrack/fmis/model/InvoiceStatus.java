package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class InvoiceStatus implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("INVOICE_NUMBER")
  private String invoiceNumber;
  @JsonProperty("INVOICE_STATUS")
  private String invoiceStatus;
  @JsonProperty("RECEIPT_NUMBER")
  private String receiptNumber;
  @JsonProperty("RECEIPT_DATE")
  private String receiptDate;
  @JsonProperty("PAID_AMOUNT")
  private String paidAmount;
  @JsonProperty("PAYMENT_TYPE")
  private String paymentType;
}
