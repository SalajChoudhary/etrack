package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PROPOSED_USE_CODE")
public @Data class ProposedUseCode implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String proposedUseCode;
  private String proposedUseDesc;
  private Integer activeInd;
  
}
