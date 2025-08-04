package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_PUBLIC_TYPE_CODE")
public @Data class PublicType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String publicTypeCode;
  private String publicTypeDesc;
  private Integer displayOrder;
  private Integer activeInd;
}
