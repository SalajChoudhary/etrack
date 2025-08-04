package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class SupportDocumentMaintenance {

  @Id
  private Integer uniqueId;
  private Integer documentSubTypeTitleId;
  private Integer documentTypeId;
  private String documentTypeDesc;
  private Integer documentSubTypeId;
  private String documentSubTypeDesc;
  private Integer documentTitleId;
  private String documentTitle;
  private String reqdNew;
  private String reqdMod;
  private String reqdExt;
  private String reqdMnm;
  private String reqdMtn;
  private String reqdRen;
  private String reqdRtn;
  private String reqdXfer;
  private Integer activeInd;
}
