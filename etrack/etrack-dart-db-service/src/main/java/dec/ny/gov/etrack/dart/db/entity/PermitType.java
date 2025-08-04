package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class PermitType implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private String permitTypeCode;
  private String permitTypeDesc;
  private Integer permitCategoryId;
  private Date effectiveStartDate;
  private Date effectiveEndDate;
}
