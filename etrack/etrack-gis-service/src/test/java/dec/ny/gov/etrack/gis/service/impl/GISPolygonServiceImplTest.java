package dec.ny.gov.etrack.gis.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.model.PolygonAttributes;
import dec.ny.gov.etrack.gis.model.PolygonFeature;
import dec.ny.gov.etrack.gis.model.PolygonGeoMetry;
import dec.ny.gov.etrack.gis.model.PolygonObject;
import dec.ny.gov.etrack.gis.model.ProjectPolygon;

@RunWith(SpringJUnit4ClassRunner.class)
public class GISPolygonServiceImplTest {
  @InjectMocks
  private GISPolygonServiceImpl gisServiceImpl;
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
  private String city = "Seoul";
  private String county = "Albany";
  private String street = "Penny Lane";
  private String userId = "Jxpuvoge";
  private String jwtToken = "TOKEN";


  // getDECPolygonByTaxId tests
  @Test
  public void testGetDECPolygonByTaxId() {
    when(gisITSServiceRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn("Hi");
    this.gisServiceImpl.getDECPolygonByTaxId(taxParcelId, county, municipalName, contextId);

  }

  // getDECPolygonByAddress tests
  @Test
  public void testGetDECPolygonByAddress() {
    when(gisITSServiceRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn("Hi");
    this.gisServiceImpl.getDECPolygonByAddress(street, city, contextId);
  }

  // DeletePolygonByObjId tests\
  @Test
  public void testDeletePolygonByObjIdSuccessfully() {
    String objectIdInput = "Obj";
    ResponseEntity entity = new ResponseEntity(HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    this.gisServiceImpl.deletePolygonByObjId(objectIdInput, contextId);
  }

  // deleteSpatialInquiryPolygonByObjId tests

  @Test
  public void testDeleteSpatialInquiryPolygonByObjIdSuccessfully() {
    String spatialInquiryObjectIdInput = "Obj";
    ResponseEntity entity = new ResponseEntity(HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    this.gisServiceImpl.deleteSpatialInquiryPolygonByObjId(spatialInquiryObjectIdInput, contextId);
  }

  //// deleteAnalystPolygonByObjId tests

  @Test
  public void testDeleteAnalystPolygonByObjIdSuccessfully() {
    String objectIdInput = "Obj";
    ResponseEntity entity = new ResponseEntity(HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    this.gisServiceImpl.deleteAnalystPolygonByObjId(userId, objectIdInput, contextId);
  }


  // deleteApplicantSubmittalPolygonByObjId tests
  @Test
  public void testDeleteApplicantSubmittalPolygonByObjIdSuccessfully() {
    String objectIdInput = "Obj";
    ResponseEntity entity = new ResponseEntity(HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    this.gisServiceImpl.deleteApplicantSubmittalPolygonByObjId(userId, contextId, objectIdInput);
  }


  // getAnalystPolygon tests
  @Test
  public void testGetAnalystPolygonSuccessfully() {
    String projectId = "123";
    when(gisInternalServiceRestTemplate.getForObject(Mockito.any(), Mockito.any()))
        .thenReturn("Returned");
    this.gisServiceImpl.getAnalystPolygon(projectId, contextId);
  }


  // deleteWorkAreaPolygonByObjId tests
  @Test
  public void testDeleteWorkAreaPolygonByObjId() {
    String objectId = "Obj";
    ResponseEntity entity = new ResponseEntity(HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    this.gisServiceImpl.deleteWorkAreaPolygonByObjId(userId, contextId, objectId);
  }



  // getWorkAreaPolygon tests

  @Test
  public void testGetWorkAreaPolygonSuccessfully() {
    when(gisInternalServiceRestTemplate.getForObject(Mockito.any(), Mockito.any()))
        .thenReturn("Returned");
    this.gisServiceImpl.getWorkAreaPolygon("WorkArea", contextId);
  }

  // uploadApprovedPolygonToEFind tests
  @Test
  public void testUploadApprovedPolygonToEFindFailsDueToObjectMapping()
      throws JsonMappingException, JsonProcessingException {
    List<ProjectPolygon> polygons = new ArrayList<>(Arrays.asList(getProjectPolygon()));
    String geoMetricDetails = "Details";
    PolygonObject polygonObject = new PolygonObject();
    List<PolygonFeature> polygonFeatures = new ArrayList<>();
    PolygonGeoMetry polygonGeoMetry = new PolygonGeoMetry();


    PolygonAttributes polygonAttributes = new PolygonAttributes();
    polygonAttributes.setObjectId(900L);
    polygonAttributes.setValidatedLocation(900);

    PolygonFeature polygonFeature = new PolygonFeature();
    polygonFeatures.add(polygonFeature);
    polygonFeature.setGeometry(polygonGeoMetry);
    polygonObject.setFeatures(polygonFeatures);

    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("");
    when(ObjectMapper.readValue(Matchers.anyString(), Matchers.<Class<PolygonObject>>any()))
        .thenReturn(polygonObject);
    this.gisServiceImpl.uploadApprovedPolygonToEFind(contextId, polygons);
  }



  // getApplicantPolygon tests
  @Test
  public void testGetApplicantPolygonSuccessfully() {
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("Returned");
    this.gisServiceImpl.getApplicantPolygon("ApplID", contextId);
  }


  // applicantPolygon tests

  @Test
  public void testApplicantPolygon() {
    List<Object> featureMap = new ArrayList<Object>(Arrays.asList(new Object()));
    GISServiceResponse response = new GISServiceResponse();
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.applicantPolygon(featureMap, "VAl", "S", contextId);
  }



  // getsubmitedPolygon tests

  @Test
  public void testGetSubmitedPolygonSuccessfully() {
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("Returned");
    this.gisServiceImpl.getsubmitedPolygon("App1id", contextId);
  }

  // saveOrUpdateWorkAreaPolygon tests
  @Test
  public void testSaveOrUpdateWorkAreaPolygonWithSaveAction() {
    List<Object> list = new ArrayList<>();
    list.add(new Object());

    GISServiceResponse response = new GISServiceResponse();
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.saveOrUpdateWorkAreaPolygon(list, "VAL", "S", contextId);

  }

  @Test
  public void testSaveOrUpdateWorkAreaPolygonWithUpdateAction() {
    List<Object> list = new ArrayList<>();
    list.add(new Object());

    GISServiceResponse response = new GISServiceResponse();
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.saveOrUpdateWorkAreaPolygon(list, "VAL", "U", contextId);

  }


  // getSpatialInquiryDetails tests


  // getDECPolygonByDecId tests
  @Test
  public void testGetDECPolygonByDecIdSuccessfully() {
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("Response");
    String response = this.gisServiceImpl.getDECPolygonByDecId("123", contextId, "token");
    assertEquals("Response", response);
    this.gisServiceImpl.getDECPolygonByDecId("123", contextId, "token");
  }


  // getDECIdByTxmap tests
  @Test
  public void testGetDECIdByTxmapSuccessfullyWith200() {
    ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(),
        Matchers.<Class<String>>any())).thenReturn(response);
    this.gisServiceImpl.getDECIdByTxmap(userId, contextId, jwtToken, "Map", county, municipalName);
  }

  @Test
  public void testGetDECIdByTxmapWith204() {
    ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(),
        Matchers.<Class<String>>any())).thenReturn(response);
    this.gisServiceImpl.getDECIdByTxmap(userId, contextId, jwtToken, "Map", county, municipalName);

  }


  // submittedPolygon tests
  @Test
  public void testSubmittedPolygonWithSaveAction() {
    List<Object> featureMap = new ArrayList<Object>(Arrays.asList(new Object()));
    GISServiceResponse response = new GISServiceResponse();
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.submittedPolygon(featureMap, "Val", "S", contextId);
  }

  @Test
  public void testSubmittedPolygonWithUpdateAction() {
    List<Object> featureMap = new ArrayList<Object>(Arrays.asList(new Object()));
    GISServiceResponse response = new GISServiceResponse();
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.submittedPolygon(featureMap, "Val", "U", contextId);
  }

  // getDECIdByProgramType tests
  @Test
  public void testgetDECIdByProgramType() {
    ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Matchers.anyString(),
        Matchers.any(HttpMethod.class), Matchers.<HttpEntity<?>>any(), Matchers.<Class<Map>>any()))
            .thenReturn(responseEntity);
    this.gisServiceImpl.getDECIdByProgramType(userId, contextId, jwtToken, "ID", "TYPE");

  }

  // analystPolygon tests
  @Test
  public void testAnalystPolygonWithUpdateAction() {
    List<Object> featureMap = new ArrayList<Object>(Arrays.asList(new Object()));

    GISServiceResponse response = new GISServiceResponse();;
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);
    this.gisServiceImpl.analystPolygon(featureMap, "Val", "U", contextId);
  }

  @Test
  public void testAnalystPolygonWithSaveAction() {
    List<Object> featureMap = new ArrayList<Object>(Arrays.asList(new Object()));

    GISServiceResponse response = new GISServiceResponse();;
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.any(HttpEntity.class), Mockito.any())).thenReturn(response);

    this.gisServiceImpl.analystPolygon(featureMap, "Val", "S", contextId);
  }

  @Test
  public void testuploadShapefileSuccessfully() {
    MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes());
    this.gisServiceImpl.uploadShapefile(userId, contextId, "Txt", "Jxpuvoge", "val", file);
  }

  @Test(expected = Exception.class)
  public void testuploadShapefileUnsuccessfully() {
    this.gisServiceImpl.uploadShapefile(userId, contextId, "Txt", "Jxpuvoge", "val", null);
  }

  private ProjectPolygon getProjectPolygon() {
    ProjectPolygon polygon = new ProjectPolygon();
    polygon.setApprovedPolygonChangeInd(1234);
    polygon.setEdbDistrictId(300L);
    polygon.setProjectId(12L);
    return polygon;
  }

}
