package dec.ny.gov.etrack.dart.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public @Data class RESTHeader {

  @JsonProperty("xmlns")
  private String xmlns;
  @JsonProperty("Username")
  private String username;
  @JsonProperty("Password")  
  private String password;
  @JsonProperty("Responsibility")
  private String responsibility;
  @JsonProperty("RespApplication")
  private String respApplication;
  @JsonProperty("SecurityGroup")
  private String securityGroup;
}
