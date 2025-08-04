package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public @Data class VPSResponse implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("RequestID")
  private String requestID;
  @JsonProperty("TransactionID")
  private String transactionID;
  @JsonProperty("ConfirmationNumber")
  private String confirmationNumber;
  @JsonProperty("Channel")
  private String channel;
  @JsonProperty("TotalPaidAmount")
  private String totalPaidAmount;
  @JsonProperty("TotalFeeCharged")
  private String totalFeeCharged;
  @JsonProperty("Token")
  private Object token;
  @JsonProperty("LineItems")
  private List<Object> lineItems;
  @JsonProperty("Splitpay")
  private String splitpay;
  @JsonProperty("Transactions")
  private List<Transaction> transactions;
  @JsonProperty("CustomFields")
  private List<Object> customFields;
  @JsonProperty("RepId")
  public Object repId;
  @JsonProperty("DisbursmentMode")
  public Object disbursmentMode;
  @JsonProperty("AccountNickName")
  public Object accountNickName;
}
