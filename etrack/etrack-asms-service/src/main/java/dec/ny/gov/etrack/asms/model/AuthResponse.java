package dec.ny.gov.etrack.asms.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class AuthResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private String guid;
  private String userId;
  private List<String> roles;
  private Set<String> permissions;
}
