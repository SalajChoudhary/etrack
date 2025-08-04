package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_PROJECT")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Project implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long projectId;
  private String projectDesc;
  private Integer mailInInd;
  private Integer applicantTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String proposedUseCode;
  private Date receivedDate;
//  private String bridgeIdNumber;
  private Integer constrnType;
  private Date proposedStartDate;
  private Date estmtdCompletionDate;
  private String strWaterbodyName;
  private String wetlandIds;
  private Integer eaInd;
  private String damType;
  private String analystAssignedId;
  private String assignedAnalystName;
  private Date analystAssignedDate;
  private Integer seqrInd;
  private Integer foilReqInd;
  private Integer dimsrInd;
  private Integer originalSubmittalInd;
  private Date originalSubmittalDate;
//  private Integer onlineApplnInd;
  
  @JsonIgnore
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProjectDevelopment> projectDevelopments;

  @JsonIgnore
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProjectResidential> projectResidentials;
  
  @JsonIgnore
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProjectSICNAICSCode> projectSicNaicsCodes;
  
  @JsonIgnore
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProjectSWFacilityType> projectSWFacilityType;

}
