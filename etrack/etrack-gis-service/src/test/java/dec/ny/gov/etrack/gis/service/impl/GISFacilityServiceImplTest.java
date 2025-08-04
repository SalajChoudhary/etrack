package dec.ny.gov.etrack.gis.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.model.FacilityAddress;
import dec.ny.gov.etrack.gis.model.FacilityDetail;
import dec.ny.gov.etrack.gis.model.ProjectDetail;

@RunWith(SpringJUnit4ClassRunner.class)
public class GISFacilityServiceImplTest {
  @InjectMocks
  private GISFacilityServiceImpl gisServiceImpl;

  @Mock
  private RestTemplate gisITSServiceRestTemplate;

  @Mock
  private RestTemplate geoCodeServiceRestTemplate;

  @Mock
  private RestTemplate eTrackOtherServiceRestTemplate;

  @Mock
  private RestTemplate gisInternalServiceRestTemplate;

  @Mock
  private RestTemplate eTrackGISServiceRestTemplate;

  @Mock
  private ObjectMapper ObjectMapper;



  @Mock
  private RestTemplate gisExternalServiceRestTemplate;
  private String taxParcelId = "Parcel id";
  private String municipalName = "Gyeongi-do";
  private String contextId = "1245";
  private String postal = "12207";
  private String city = "Seoul";
  private String address = "890 Seoul Drive";
  private String county = "Albany";
  private String street = "Penny Lane";
  private String userId = "Jxpuvoge";
  private String jwtToken = "TOKEN";

  @Test
  public void testRetrieveFacilityInfoSuccessfully() {
    ResponseEntity<ProjectDetail> entity = new ResponseEntity<>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(),
        Matchers.<Class<ProjectDetail>>any())).thenReturn(entity);
    this.gisServiceImpl.retrieveFacilityInfo(userId, contextId, jwtToken, 9L);
  }

  // retrieveFacilityHistory tests
  @Test
  public void testRetrieveFacilityHistorySuccessfully() {
    ResponseEntity<JsonNode> entity = new ResponseEntity<>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(),
        Matchers.<Class<JsonNode>>any())).thenReturn(entity);
    this.gisServiceImpl.retrieveFacilityHistory(userId, contextId, jwtToken, 9L);
  }

  // saveFacilityDetail tests
  @Test
  public void testSaveFacilityDetailSuccessfully() {
    ProjectDetail projectDetail = new ProjectDetail();
    ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.postForEntity(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(responseEntity);
    this.gisServiceImpl.saveFacilityDetail(userId, contextId, jwtToken, projectDetail);
  }

  // updateFacilityDetail tests
  @Test
  public void testupdateFacilityDetailSuccessfully() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    ResponseEntity<ProjectDetail> responseEntity =
        new ResponseEntity<ProjectDetail>(projectDetail, HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(),
        Matchers.<Class<ProjectDetail>>any())).thenReturn(responseEntity);
    ProjectDetail detail =
        this.gisServiceImpl.updateFacilityDetail(userId, contextId, jwtToken, projectDetail);
    assertEquals(projectDetail.getApplicantTypeCode(), detail.getApplicantTypeCode());

  }

  // analystPolygon tests
  private JsonNode getJsonNode() {
    String jsonString =
        "[{\"score\":80,\"attributes\":{\"City\":\"CityName\",\"RegionAbbr\":\"state\"}}]";
    // Object obj = new Object();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = null;
    try {
      node = mapper.readTree(jsonString);
    } catch (Exception e) {
    }
    return node;
  }

  private ProjectDetail getValidProjectDetailObj() {
    ProjectDetail projectDetail = new ProjectDetail();
    projectDetail.setMailInInd(10);
    projectDetail.setApplicantTypeCode(100);
    projectDetail.setFacility(getFacilityObj());
    projectDetail.setProjectId(300L);
    return projectDetail;
  }

  private FacilityDetail getFacilityObj() {
    FacilityDetail facilityDetail = new FacilityDetail();
    facilityDetail.setAddress(getFacilityAddressObj());
    return facilityDetail;
  }

  private FacilityAddress getFacilityAddressObj() {
    FacilityAddress facilityAddress = new FacilityAddress();
    facilityAddress.setCity("Albany");
    facilityAddress.setState("New York");
    return facilityAddress;

  }

}
