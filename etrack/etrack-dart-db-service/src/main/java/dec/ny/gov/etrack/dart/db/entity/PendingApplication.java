package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class PendingApplication implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long projectId;
  private String facilityName;
  private String locationDirections;
  private String city;
  private String state;
  private String country;
  private String zip;
  private Date createDate;
  private Date receivedDate;
  @Column(name="street1")
  private String street1;
  @Column(name="street2")
  private String street2;
//  private String municipality;
  private String displayName;
  private Long applicantId;
  private String decId;
//  private String county;
  private Long edbDistrictId;
  private Long edbPublicId;
//  private String createdById;
  private String analystAssignedId;
  private String assignedAnalystName;
  private String analystAssignedDate;
  private Integer eaInd;
  private String firstName;
  private String lastName;
  private String middleName;
  private Integer rejectedInd;
  private String rejectedReason;
  private String rejectedDate;
}
