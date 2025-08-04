package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_SPATIAL_INQ_CATEGORY")
public @Data class SpatialInqCategory implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Integer spatialInqCategoryId;
  private String spatialInqCategoryCode;
  private String spatialInqCategoryDesc;
  private Integer activeInd;
  private Integer displayOrder;
  private String categoryAvailTo;
}
