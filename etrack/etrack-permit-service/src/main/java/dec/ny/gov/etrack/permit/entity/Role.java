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
@Table(name = "E_ROLE")
public @Data class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_ROLE_S")
  @SequenceGenerator(name = "E_ROLE_S", sequenceName = "E_ROLE_S", allocationSize = 1)
  private Long roleId;
  private Integer roleTypeId;
  private String employeeRegionCode;
  private String title;
  private Integer legallyResponsibleTypeCode;
  private Long addressId;
  private Date beginDate;
  private Long edbRoleId;
  private Integer changeCtr;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Integer primaryLrpInd;
  private Integer originalSubmittalInd;
  private Date originalSubmittalDate;
  private Long publicId;
  private Integer selectedInEtrackInd;
//  @ManyToOne
//  @JoinColumn(name = "PUBLIC_ID")
//  private Public publicName;
}
