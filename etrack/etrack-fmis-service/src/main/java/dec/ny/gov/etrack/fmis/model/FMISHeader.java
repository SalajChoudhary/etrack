package dec.ny.gov.etrack.fmis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class FMISHeader {
  private String xmlns;
  @JsonProperty("Username")
  private String userName;
  @JsonProperty("Password")
  private String password;
  @JsonProperty("Responsibility")
  private String responsibility;
  @JsonProperty("RespApplication")
  private String respApplication;
  @JsonProperty("SecurityGroup")
  private String securityGroup;
}
