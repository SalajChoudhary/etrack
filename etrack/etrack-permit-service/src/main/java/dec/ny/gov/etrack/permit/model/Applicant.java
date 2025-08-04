package dec.ny.gov.etrack.permit.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Applicant {
  private Long applicantId;
  private Long edbApplicantId;
  private String displayName;
  private String publicTypeCode;
  private String govtAgencyName;
//  private List<Relationship> propertyRelationships;
  private List<Integer> propertyRelationships;
  private Individual individual;
  private ApplicantAddress address;
  private Contact contact;
  private Organization organization;
  private String dba;
  private String validatedInd;
}
