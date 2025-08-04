package dec.ny.gov.etrack.gis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class ProjectDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long projectId;
  private String projectDesc;
  private Integer mailInInd;
  private Integer applicantTypeCode;
  private String proposedUseCode;
  private Long facilityId;
  private String polygonId;
  private String locDirections;
  private FacilityDetail facility;
  private String regions;
  private String primaryRegion;
  private String counties;
  private String countySwis;
  private String municipalities;
  private String municipalitySwis;
  private String primaryMunicipality;
  private String taxmaps;
  private String reason;
  private String boundaryChangeReason;
  @JsonProperty("lat")
  private String latitude;
  @JsonProperty("long")
  private String longitude;
  private String polygonStatus;
  private String validatedInd;
  private String receivedDate;
  private String classifiedUnderSeqr;
  private String workAreaId;
  private BigDecimal nytmx;
  private BigDecimal nytmy;
  private String decIdNotMatchInd;
  private Integer mode;
  private Integer ignoreDecIdMismatch;
  private String printUrl;
  private Integer hasSameGeometry;
  private LoginUser loggedInUser;
  private String onlineSubmissionInd;
  private List<Long> inquiries;
}
