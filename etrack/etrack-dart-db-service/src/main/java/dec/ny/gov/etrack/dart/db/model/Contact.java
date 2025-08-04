package dec.ny.gov.etrack.dart.db.model;

import lombok.Data;

public @Data class Contact {
  private String cellNumber;
  private String workPhoneNumber;
  private String workPhoneNumberExtn;
  private String homePhoneNumber;
  private String emailAddress;
}
