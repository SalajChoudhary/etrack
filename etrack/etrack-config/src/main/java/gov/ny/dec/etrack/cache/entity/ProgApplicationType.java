package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public @Data class ProgApplicationType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String programApplicationTypeCode;
  private String programApplicationTypeDesc;
}
