package dec.ny.gov.etrack.dart.db.model;

import java.util.List;
import lombok.Data;

public @Data class ApplicantHistory {
  private Long applicantId;
  private Long edbApplicantId;
  private String displayName;
  private String publicTypeCode;
  private String govtAgencyName;
  private List<String> propertyRelationships;
  private Individual individual;
  private Address address;
  private Contact contact;
  private Organization organization;
  private String dba;
}
