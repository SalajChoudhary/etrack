package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_APPLICANT_TYPE_CODE")
public @Data class ApplicantType {
  @Id
  private Integer applicantTypeCode;
  private String applicantTypeDesc;
}
