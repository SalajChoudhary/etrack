package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_DEP_REGION")
public @Data class Region implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Integer depRegionId;
  private Integer activeInd;
}
