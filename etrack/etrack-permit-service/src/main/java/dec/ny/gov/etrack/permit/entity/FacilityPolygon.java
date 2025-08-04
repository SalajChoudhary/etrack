package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY_POLYGON")
public @Data class FacilityPolygon implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_FACILITY_POLYGON_S")
  @SequenceGenerator(name = "E_FACILITY_POLYGON_S", sequenceName = "E_FACILITY_POLYGON_S",
      allocationSize = 1)
  private Long facilityPolygonId;
  private Long projectId;
  private String polygonGisId;
  private Integer polygonTypeCode;
  private String latitude;
  private String longitude;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String workAreaPolygonId;
  private BigDecimal nytmnCoordinate;
  private BigDecimal nytmeCoordinate;
  
//  @OneToMany(mappedBy = "facilityPolygon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//  private Set<FacilityPolygonCounty> counties;
//  @OneToMany(mappedBy = "facilityPolygon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//  private Set<FacilityPolygonMunicipality> municipalities;
//  @OneToMany(mappedBy = "facilityPolygon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//  private Set<FacilityPolygonRegion> regions;
//  @OneToMany(mappedBy = "facilityPolygon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//  private Set<FacilityPolygonTaxMap> taxMap;

}
