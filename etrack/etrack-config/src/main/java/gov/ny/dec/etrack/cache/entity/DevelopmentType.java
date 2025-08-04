package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_DEVELOPMENT_TYPE_CODE")
public @Data class DevelopmentType {
  @Id
  private Integer developmentTypeCode;
  private String developmentTypeDesc;
  private Integer activeInd;
}
