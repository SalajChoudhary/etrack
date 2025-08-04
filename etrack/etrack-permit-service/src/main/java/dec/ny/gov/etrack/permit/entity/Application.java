package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_APPLICATION")
public @Data class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_APPLICATION_S")
  @SequenceGenerator(name = "E_APPLICATION_S", sequenceName = "E_APPLICATION_S", allocationSize = 1)
  private Long applicationId;
  private Long projectId;
  private String permitTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Long roleId;
  private Long edbApplId;
  private String transTypeCode;
  private String edbTransTypeCode;
  private Integer userSelNewInd;
  private Integer userSelModInd;
  private Integer userSelExtInd;
  private Integer userSelTransferInd;
  private Integer userSelRenInd;
  private Integer pendingInd;
  private Integer chgOriginalProjectInd;
  private Long batchIdEdb;
  private String progId;
  private Integer polEmissionInd;
  private Integer formSubmittedInd;
  private String permitExtendedDate;
  private Integer trackingInd;
  private Integer edbTrackingInd;
  private Date edbAuthEffDate;
  private Date edbAuthExpDate;
  private String batchGroupEtrack;
  private Integer transactionTypeRuleId;
  private Long edbAuthTemplateAuthId;
  private String uploadTransTypeCode;
}
