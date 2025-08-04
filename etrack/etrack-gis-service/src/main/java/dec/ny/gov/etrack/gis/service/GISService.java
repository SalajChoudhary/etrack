package dec.ny.gov.etrack.gis.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.gis.model.ProjectDetail;

@Service
public interface GISService {
  public String getITSAddresses(final String address);  
  public String getEsriAddresses(final String address);
  public Object getCounties();
  public Object getTaxParcel(final String taxParcelID, final String countyName, final String muncipalName);
  public String getMuncipalities(final String countyName);
  public String getDECPolygonByTaxId(final String taxParcelID,final String countyName, final String municipalName);
  public String getDECPolygonByAddress(final String street, final String city);
  public String getDECPolygonByDecId(final String decId);
  public Object saveApplicantPolygon(final List<Object> featureMap, final String value);
  public Object saveAnalystPolygon(final List<Object> featureMap, final String value);
  public Object saveSubmitedPolygon(final List<Object> featureMap, final String value);
  public String getApplicantPolygon(final String applicationId);
  public String getAnalystPolygon(final String analystId) ; 
  public String getsubmitedPolygon(final String applSubId) ;
  public ProjectDetail saveFacilityDetail(final String userId, final String contextId, final String jwtToken,
      ProjectDetail projectDetail);
  public Map<String, Object> getDECIDyDetail(final String userId, final String contextId, String jwtToken, String programId,
      String programType);
}
