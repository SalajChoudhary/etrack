package dec.ny.gov.etrack.fmis.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data  class OAuthToken implements Serializable {/**
   * 
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private Long expiresIn;
}
