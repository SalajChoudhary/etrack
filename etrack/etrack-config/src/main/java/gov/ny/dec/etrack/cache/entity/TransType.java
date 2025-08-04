package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_TRANS_TYPE_CODE")
public @Data class TransType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private String transTypeCode;
  private String transTypeDesc;
  private String edbTransTypeCode;
}
