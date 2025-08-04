package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Invoice implements Serializable {
  
  private static final long serialVersionUID = 1L;
  @JsonProperty("PROGRAMIDENTIFICATION")
  private String programIdentification;
  @JsonProperty("CUSTOMERUNIQUEIDENTIFIER")
  private String customerUniqueIdentifier;
  @JsonProperty("CUSTOMERTYPE")
  private String customerType;
  @JsonProperty("TRANSACTIONTYPE")
  private String transactionType;
  @JsonProperty("CUSTOMERNAME")
  private String customerName;
  @JsonProperty("CUSTOMERADDRESSLINE1")
  private String customerAddressline1;
  @JsonProperty("CUSTOMERADDRESSLINE2")
  private String customerAddressline2;
  @JsonProperty("CITY")
  private String city;
  @JsonProperty("STATE")
  private String state;
  @JsonProperty("ZIP")
  private String zip;
  @JsonProperty("COUNTRYCODE")
  private String countryCode;
  @JsonProperty("BILLINGSITEUNIQUEID")
  private String billingSiteUniqueId;
  @JsonProperty("BILLINGADDRESSLINE1")
  private String billingAddressline1;
  @JsonProperty("BILLINGADDRESSLINE2")
  private String billingAddressline2;
  @JsonProperty("BILLINGCITY")
  private String billingCity;
  @JsonProperty("BILLINGSTATE")
  private String billingState;
  @JsonProperty("BILLINGZIP")
  private String billingZip;
  @JsonProperty("PHONENUMBER")
  private String phoneNumber;
  @JsonProperty("BILLINGCOUNTRYCODE")
  private String billingCountrycode;
  @JsonProperty("BILLINGFIRSTNAME")
  private String billingFirstname;
  @JsonProperty("BILLINGLASTNAME")
  private String billingLastname;
  @JsonProperty("BILLINGEMAIL")
  private String billingEmail;
  @JsonProperty("TOTALCHARGES")
  private Long totalCharges;
  @JsonProperty("REVACCT1")
  private String revAcct1;
  @JsonProperty("REVACCT1AMT")
  private Long revAcct1Amt;
  @JsonProperty("REVACCT2")
  private String revAcct2;
  @JsonProperty("REVACCT2AMT")
  private Long revAcct2Amt;
  @JsonProperty("REVACCT3")
  private String revAcct3;
  @JsonProperty("REVACCT3AMT")
  private Long revAcct3Amt;
  @JsonProperty("ORIGINALFMISINVNUM")
  private String originalFmisInvnum;
  @JsonProperty("ATRRIBUTE1")
  private String atrribute1;
  @JsonProperty("ATRRIBUTE2")
  private String atrribute2;
  @JsonProperty("ATRRIBUTE3")
  private String atrribute3;
  @JsonProperty("ATRRIBUTE4")
  private String atrribute4;
  @JsonProperty("ATRRIBUTE5")
  private String atrribute5;
  @JsonProperty("INVOICENUM")
  private String invoiceNum;
  @JsonProperty("CONFNUM")
  private String confNum;
  @JsonProperty("CHECKRECEIVED")
  private String checkReceived;
  @JsonProperty("DECID")
  private String decId;
  @JsonProperty("CHECKNUMBER")
  private String checkNumber;
  @JsonProperty("CHECKAMOUNT")
  private String checkAmount;
  @JsonProperty("CHECKRECEIVEDDATE")
  private String checkReceivedDate;
}
