package dec.ny.gov.etrack.dart.db.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
public @Data class FacilityHistory {
  @Id
  private Long hFacilityId;
  private Long hProjectId;
  private Long hEdbDistrictId;
  private String hFacilityName;
  private String hDecId;
  private String createdById;
  private Date createDate;
  private String hModifiedById;
  private Date hModifiedDate;
  private String hOpCode;
  private String hCreatedById;
  private Date hCreateDate;
  private String hComments;
}
