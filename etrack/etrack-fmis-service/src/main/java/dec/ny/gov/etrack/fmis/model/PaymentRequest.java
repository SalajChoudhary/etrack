package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class PaymentRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("CDF1")
  private String programIdentification;
  @JsonProperty("CDF2")
  private String customerUniqueIdentifier;
  @JsonProperty("PartnerName")
  private String customerName;
  @JsonProperty("CDF3")
  private String customerType;
  @JsonProperty("CDF4")
  private String billingSiteUniqueId;
  @JsonProperty("CDF5")
  private String transactionType;
  @JsonProperty("Addressline")
  private String billingAddressline;
  @JsonProperty("City")
  private String billingCity;
  @JsonProperty("Statecode")
  private String billingState;
  @JsonProperty("Zipcode")
  private String billingZip;
  @JsonProperty("Country")
  private String billingCountrycode;
  private String billingContact;
  private String billingEmail;
  @JsonProperty("TotalPaymentAmount")
  private Long totalCharges;

}
