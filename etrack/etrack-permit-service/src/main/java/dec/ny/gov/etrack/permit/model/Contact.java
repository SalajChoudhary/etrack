package dec.ny.gov.etrack.permit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Contact {
  private String cellNumber;
  private String workPhoneNumber;
  private String workPhoneNumberExtn;
  private String homePhoneNumber;
  private String emailAddress;
}
