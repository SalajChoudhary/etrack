package gov.ny.dec.dart.district.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Response implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String resultCode;
  private String resultMessage;
}
