package dec.ny.gov.etrack.asms.model;

import java.io.Serializable;
import lombok.Data;

public @Data class User implements Serializable {
  private Long id;
  private String guid;
  private String loginId;
  private String emailAddress;
  private Name name;
  private String userType;
  private String loginStatus;
  private String division;
  private String workLocation;
  private String trustLevel;
}
