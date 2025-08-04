package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Individual {
  private String firstName;
  private String middleName;
  private String lastName;
  private String suffix;
}
