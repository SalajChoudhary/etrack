package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import lombok.Data;

public @Data class DartApplication implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
//  @Column(name = "prog_id")
  private Long applId;
  private String trackedId;
  private String trackedIdFormatted;
//  @Column(name = "appl_time_frame_code")
  private String emergencyInd;
//  @Column(name="general_permit_ind")
  private Integer gpInd;
  private Integer renOrderNum;
  private Integer modOrderNum;
//  @Column(name="appl_trans_type_code")
  private String transType;
//  @Column(name="auth_type_code")
  private String permitType;
  private String permitDesc;
  private Date startDate;
  private Date expiryDate;
  private Date receivedDate;
  private String displayName;
  private String publicName;
  private Integer trackingInd;
  private Long batchId;
//  @Column(name="final_disposition_due_date")
  private Date dueDate;
  private Integer region;
  private Long districtId;
//  @Column(name="district_name")
  private String facilityName;
//  @Column(name="auth_status_code")
  private String appStatus;
  private String street;
  private String city;
  private String state;
  private String zip;
  private String sapaInd;
  private Date sapaDate;
  private String municipality;
  private String county;
  private String projectDesc;
  private Long projectId;
  private String dispositionDate;
  private Long publicId;
  private Long analystRoleId;
  private String programManager;
}
