package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY_POLYGON_TAX_MAP")
public @Data class FacilityPolygonTaxMap implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_FACILITY_POLYGON_TAX_MAP_S")
  @SequenceGenerator(name = "E_FACILITY_POLYGON_TAX_MAP_S", sequenceName = "E_FACILITY_POLYGON_TAX_MAP_S",
      allocationSize = 1)
  private Long facilityPolygonTaxMapId;
  private Long facilityPolygonId;
  private String taxmapNumber;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
