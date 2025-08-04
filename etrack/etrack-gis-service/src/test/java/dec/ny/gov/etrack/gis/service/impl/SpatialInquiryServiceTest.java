package dec.ny.gov.etrack.gis.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.model.GISErrorResponse;
import dec.ny.gov.etrack.gis.model.GISResponse;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.model.ProjectDetail;
import dec.ny.gov.etrack.gis.util.GISServiceUtil;


@RunWith(SpringJUnit4ClassRunner.class)
public class SpatialInquiryServiceTest {
  
  @InjectMocks
  private SpatialInquiryServiceImpl gisServiceImpl;

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
  private GISServiceUtil gisServiceUtil;
  @Mock
  private RestTemplate gisExternalServiceRestTemplate;
  
  private String contextId = "1245";
  private String userId = "userId";
  private String jwtToken = "TOKEN";


  @Test(expected = BadRequestException.class)
  public void testSpatialInquiryApplicantPolygonThrowsBadRequestWhenInvalidActionPassed() {
    gisServiceImpl.spatialInquiryApplicantPolygon(null, "userId", "X", "contextId");
  }

  @Test
  public void testSpatialInquiryApplicantPolygonSaveActionReturnsSuccessfully() {
    when(gisServiceUtil.submitPolygonRequest(Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("Success");
    String result = (String)gisServiceImpl.spatialInquiryApplicantPolygon(null, "userId", "S", "contextId");
    assertEquals("Success", result);
  }

  @Test
  public void testSpatialInquiryApplicantPolygonUpdateActionReturnsSuccessfully() {
    when(gisServiceUtil.submitPolygonRequest(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("Success");
    String result = (String)gisServiceImpl.spatialInquiryApplicantPolygon(null, "userId", "U", "contextId");
    assertEquals("Success", result);
  }

  @Test
  public void testDeleteSpatialInqPolygonByObjIdSuccessfully() {
    String objectId = "Id";
    ResponseEntity entity = new ResponseEntity<>("Success", HttpStatus.OK);
    when(gisInternalServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(entity);
    String result = (String)gisServiceImpl.deleteSpatialInqPolygonByObjId(objectId, contextId);
    assertEquals("Success", result);
  }

  @Test(expected = GISException.class)
  public void testDeleteSpatialInqPolygonByObjIdThrowsClientError() {
    String objectId = "Id";
    HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Client Error");
    doThrow(clientErrorException).when(gisInternalServiceRestTemplate).postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any());
    gisServiceImpl.deleteSpatialInqPolygonByObjId(objectId, contextId);
  }
  
  @Test(expected = GISException.class)
  public void testDeleteSpatialInqPolygonByObjIdThrowsServerError() {
    String objectId = "Id";
    HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Server Error");
    doThrow(serverErrorException).when(gisInternalServiceRestTemplate).postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any());
    gisServiceImpl.deleteSpatialInqPolygonByObjId(objectId, contextId);
  }
  
  
  @Test
  public void testSaveSpatialInqDetailsSuccessfully() {
    String jsonNode = "{"
        + "    \"result\": \"Success\""
        + "}";
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonModel = null;
    try {
      jsonModel = objectMapper.readValue(jsonNode, JsonNode.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    when(eTrackOtherServiceRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any())).thenReturn(new ResponseEntity<>(jsonModel, HttpStatus.OK));
    JsonNode result = (JsonNode)gisServiceImpl.saveSpatialInqDetails("userId", "contextId", "jwtToken", null);
    String resultCode = result.get("result").asText();
    assertEquals("Success", resultCode);
  }

  @Test(expected = GISException.class)
  public void testSaveSpatialInqDetailsThrowsClientError() {
    HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Client Error");
    doThrow(clientErrorException).when(eTrackOtherServiceRestTemplate).postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any());
    gisServiceImpl.saveSpatialInqDetails("userId", "contextId", "jwtToken", null);
  }
  
  @Test(expected = GISException.class)
  public void testSaveSpatialInqDetailsThrowsServerError() {
    HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Server Error");
    doThrow(serverErrorException).when(eTrackOtherServiceRestTemplate).postForEntity(Mockito.anyString(), Mockito.any(),
        Mockito.any());
    gisServiceImpl.saveSpatialInqDetails("userId", "contextId", "jwtToken", null);
  }
  
  @Test
  public void testgetSpatialPolygonByApplicationIdSuccessfully() {
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("Returned");
    String result = gisServiceImpl.getSpatialPolygonByApplicationId("id", contextId);
    assertEquals("Returned", result);
  }
  
  @Test(expected = GISException.class)
  public void testgetSpatialPolygonByApplicationIdThrowsClientError() {
    HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Client Error");
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenThrow(clientErrorException);
    gisServiceImpl.getSpatialPolygonByApplicationId("id", contextId);
  }

  @Test(expected = GISException.class)
  public void testgetSpatialPolygonByApplicationIdThrowsServerError() {
    HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Server Error");
    when(gisInternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
    .thenThrow(serverErrorException);
    gisServiceImpl.getSpatialPolygonByApplicationId("id", contextId);
  }

  @Test
  public void testGetSpatialInquiryDetailsSuccessfullyWithNoRequestor() {
    ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<JsonNode>>any())).thenReturn(response);
    Object result = gisServiceImpl.getSpatialInquiryDetails(userId, contextId, jwtToken, 10L, null);
    assertEquals(response.getBody(), result);
  }

  @Test
  public void testGetSpatialInquiryDetailsSuccessfullyWitRequestor() {
    ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<JsonNode>>any())).thenReturn(response);
    Object result = gisServiceImpl.getSpatialInquiryDetails(userId, contextId, jwtToken, 10L, "Requestor");
    assertEquals(response.getBody(), result);
  }

  @Test
  public void testGetSpatialInquiryDetailsSuccessfullyWithNegativeInquiryId() {
    ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(HttpStatus.OK);
    when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<JsonNode>>any())).thenReturn(response);
    Object result = gisServiceImpl.getSpatialInquiryDetails(userId, contextId, jwtToken, -10L, "Requestor");
    assertEquals(response.getBody(), result);
  }

  @Test(expected = GISException.class)
  public void testGetSpatialInquiryDetailsThrowsClientErrorException() {
    when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<JsonNode>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Client Error"));
    gisServiceImpl.getSpatialInquiryDetails(userId, contextId, jwtToken, 10L, "Requestor");
  }

  @Test(expected = GISException.class)
  public void testGetSpatialInquiryDetailsThrowsServerErrorException() {
    when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<JsonNode>>any()))
            .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Server Error"));
    gisServiceImpl.getSpatialInquiryDetails(userId, contextId, jwtToken, 10L, "Requestor");
  }

  @Test(expected = GISException.class)
  public void testSaveSpatialInquiryDetailsThrowsClientError() {
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.<HttpEntity<?>>any(),Mockito.<Class<GISServiceResponse>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Client Error"));
    gisServiceImpl.saveSpatialInquiryResponseDetails(userId, contextId, jwtToken, null);
  }

  @Test(expected = GISException.class)
  public void testSaveSpatialInquiryDetailsThrowsServerError() {
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.<HttpEntity<?>>any(),Mockito.<Class<GISServiceResponse>>any()))
            .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Server Error"));
    gisServiceImpl.saveSpatialInquiryResponseDetails(userId, contextId, jwtToken, null);
  }
  
  @Test(expected = GISException.class)
  public void testSaveSpatialInquiryDetailsThrowsBadRequesWhenReceivedErrorResponse() {
    GISServiceResponse gisServiceResponse = new GISServiceResponse();
    GISResponse gisResponse = new GISResponse();
    GISErrorResponse errorResponse = new GISErrorResponse();
    errorResponse.setCode("E-001");
    errorResponse.setDescription("Error");
    gisResponse.setError(errorResponse);
    List<GISResponse> gisResponses = new ArrayList<>();
    gisResponses.add(gisResponse);
    gisServiceResponse.setAddResults(gisResponses);
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<GISServiceResponse>>any()))
            .thenReturn(gisServiceResponse);
    gisServiceImpl.saveSpatialInquiryResponseDetails(userId, contextId, jwtToken, null);
  }

  @Test
  public void testSaveSpatialInquiryDetailsReturnsSuccess() {
    GISServiceResponse gisServiceResponse = new GISServiceResponse();
    GISResponse gisResponse = new GISResponse();
    gisResponse.setSuccess(true);
    List<GISResponse> gisResponses = new ArrayList<>();
    gisResponses.add(gisResponse);
    gisServiceResponse.setAddResults(gisResponses);
    when(gisInternalServiceRestTemplate.postForObject(Mockito.anyString(),
        Mockito.<HttpEntity<?>>any(),
        Mockito.<Class<GISServiceResponse>>any()))
            .thenReturn(gisServiceResponse);
   GISServiceResponse gisServiceResponse2 = gisServiceImpl.saveSpatialInquiryResponseDetails(
       userId, contextId, jwtToken, null);
   assertEquals(gisServiceResponse, gisServiceResponse2);
  }
}
