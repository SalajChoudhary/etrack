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
@Table(name = "E_PUBLIC")
public @Data class Public {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PUBLIC_S")
  @SequenceGenerator(name = "E_PUBLIC_S", sequenceName = "E_PUBLIC_S", allocationSize = 1)
  private Long publicId;
  private String publicTypeCode;
  private String publicName;
  private String taxpayerId;
  private Long edbPublicId; 
  private String displayName;
  private String firstName;
  private String middleName;
  private String lastName;
  private String suffix;
  private String dbaName;
  private Integer incorpInd;
  private String incorpState;
  private String territoryOrCountry;
  private Integer changeCounter;
  private Date createDate;
  private Date modifiedDate;
  private String modifiedById;
  private String createdById;
  private Long projectId;
//  private Integer selectedInEtrackInd;
  private Integer businessValidatedInd;
  private Integer validatedInd;
//  private Integer onlineSubmitterInd;
//  @OneToMany(mappedBy = "publicName", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//  private List<Role> roles;
}
