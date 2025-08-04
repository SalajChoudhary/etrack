package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class VPSTransactionRequest implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;
  @JsonProperty("PartnerID")
  private String partnerId;
  @JsonProperty("RequestID")
  private String requestId;
  @JsonProperty("Username")
  private String userName;
  @JsonProperty("Password")
  private String password;
  @JsonProperty("TotalPaymentAmount")
  private String amount;
  @JsonProperty("PartnerName")
  private String partnerName;
  @JsonProperty("CustomFields")
  private List<PaymentCustomField> customFields;
  @JsonProperty("RedirectURL")
  private String redirectURL;
  @JsonProperty("ConfirmationPostURL")
  private String confirmationPostURL;
  @JsonProperty("EchobackCustomData")
  private String echobackCustomData;
}
