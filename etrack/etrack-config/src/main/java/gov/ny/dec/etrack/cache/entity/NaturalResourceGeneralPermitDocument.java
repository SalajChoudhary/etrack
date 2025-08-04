package gov.ny.dec.etrack.cache.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_REQUIRED_DOC_FOR_NAT_GP")
public @Data class NaturalResourceGeneralPermitDocument {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_REQUIRED_DOC_FOR_NAT_GP_S")
  @SequenceGenerator(name = "E_REQUIRED_DOC_FOR_NAT_GP_S", sequenceName = "E_REQUIRED_DOC_FOR_NAT_GP_S", allocationSize = 1)
  private Long requiredDocForNatGpId;
  private Long documentSubTypeTitleId;
  private String reqdNew;
  private String reqdMod;
  private String reqdExt;
  private String reqdMnm;
  private String reqdMtn;
  private String reqdRen;
  private String reqdRtn;
  private String reqdXfer;
  private Integer activeInd;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
