package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class DocumentSubTypeTitleViewEntity {

  @Id
  private Long documentSubTypeTitleId;
  private Long documentTypeId;
  private String documentTypeDesc;
  private Long documentSubTypeId;
  private String documentSubTypeDesc;
  private Long documentTitleId;
  private String documentTitle;
  private Integer activeInd;
}
