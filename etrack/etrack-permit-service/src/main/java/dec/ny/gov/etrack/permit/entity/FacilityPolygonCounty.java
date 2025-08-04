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
@Table(name = "E_FACILITY_POLYGON_COUNTY")
public @Data class FacilityPolygonCounty implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_FACILITY_POLYGON_COUNTY_S")
  @SequenceGenerator(name = "E_FACILITY_POLYGON_COUNTY_S", sequenceName = "E_FACILITY_POLYGON_COUNTY_S",
      allocationSize = 1)
  private Long facilityPolygonCountyId;
  private Long facilityPolygonId;
  private String countySwisCode;
  private String county;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
