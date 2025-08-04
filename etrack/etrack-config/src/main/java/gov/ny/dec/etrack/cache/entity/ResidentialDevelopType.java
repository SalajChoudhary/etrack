package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_RES_DEV_TYPE_CODE")
public @Data class ResidentialDevelopType {
  
  @Id
  private Integer resDevTypeCode;
  private String resDevTypeDesc;
  private Integer activeInd;
}
