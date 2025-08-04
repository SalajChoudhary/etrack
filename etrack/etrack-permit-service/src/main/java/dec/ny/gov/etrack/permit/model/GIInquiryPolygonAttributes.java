package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class GIInquiryPolygonAttributes implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("OBJECTID") 
  private Long objectId;
  @JsonProperty("RESPONSE_DATE")
  private String responseDate;
  @JsonProperty("RECEIVED_DATE")
  private String receivedDate;
}
