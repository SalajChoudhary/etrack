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
public @Data class ProgDistrictType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String progDistrictTypeCode;
  private String progDistrictTypeDesc;
  private String editMask;
  private String errorText;
  private String formatMask;
}
