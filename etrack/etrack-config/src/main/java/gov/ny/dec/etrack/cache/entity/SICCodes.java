package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_SIC_CODE")
public @Data class SICCodes implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String sicCode;
  private String sicDesc;
  private Integer activeInd;
}
