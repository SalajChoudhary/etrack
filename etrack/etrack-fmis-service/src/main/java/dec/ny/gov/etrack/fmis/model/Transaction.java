package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Transaction implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("Last4OfPaymentMethod") 
  private String last4OfPaymentMethod;
  @JsonProperty("PaymentType") 
  private String paymentType;
  @JsonProperty("BillingName") 
  private String billingName;
  @JsonProperty("BillingAddress") 
  private String billingAddress;
  @JsonProperty("BillingFirstName") 
  private String billingFirstName;
  @JsonProperty("BillingLastName") 
  private String billingLastName;
  @JsonProperty("BillingCity") 
  private String billingCity;
  @JsonProperty("BillingState") 
  private String billingState;
  @JsonProperty("BillingZipcode") 
  private String billingZipcode;
  @JsonProperty("BillingCountry") 
  private String billingCountry;
  @JsonProperty("PhoneNumber") 
  private String phoneNumber;
  @JsonProperty("Email") 
  private Object email;
  @JsonProperty("PaymentMode") 
  private String paymentMode;
  @JsonProperty("ApprovalStatus") 
  private String approvalStatus;
  @JsonProperty("ApprovalText") 
  private String approvalText;
  @JsonProperty("PaidAmount") 
  private String paidAmount;
  @JsonProperty("FeeCharged") 
  private String feeCharged;
  @JsonProperty("TransactionTime") 
  private String transactionTime;
  @JsonProperty("PayPalCaptureId") 
  private String payPalCaptureId;
  @JsonProperty("GBTransactionId") 
  private String gBTransactionId;
  @JsonProperty("BillingAddressLine1") 
  private String billingAddressLine1;
  @JsonProperty("ProcessorResponseCode") 
  private Object processorResponseCode;
  @JsonProperty("ProcessorResponseCodeDescription") 
  private Object processorResponseCodeDescription;
  @JsonProperty("WalletType") 
  private Object walletType;
  @JsonProperty("CardExpiry") 
  private String cardExpiry;
}
