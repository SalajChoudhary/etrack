package dec.ny.gov.etrack.gis.controller;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.PolygonAction;
import dec.ny.gov.etrack.gis.model.ProjectPolygon;
import dec.ny.gov.etrack.gis.service.GISPolygonService;

@RunWith(SpringJUnit4ClassRunner.class)
public class GISPolygonServiceControllerTest {
  
  @InjectMocks
  private GISPolygonServiceController gisController;

  @Mock
  private GISPolygonService gisService;

  private String county = "Albany";
  private String city = "Seattle";
  private String municipalName = "Municipal!";
  private String taxParcelId = "TAX123";
  private String street = "152 S Pearl St";
  private String decId = "123456";
  private String jwtToken = "JWT TOKEN";
  private String userId = "userid";
  private MockHttpServletResponse response = new MockHttpServletResponse();
  private Object featureMap = new Object();
  private String value = "Val";


  @Test
  public void testGetDECPolygonByTaxIdWithNoCounty() {
    String result =
        this.gisController.getDECPolygonByTaxId(response, taxParcelId, null, municipalName);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void testGetDECPolygonByTaxIdWithNoMunicipality() {
    String result = this.gisController.getDECPolygonByTaxId(response, taxParcelId, county, null);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void testGetDECPolygonByTaxIdWithNoTaxParcelId() {
    String result = this.gisController.getDECPolygonByTaxId(response, null, county, municipalName);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }


  @Test
  public void testGetDECPolygonByTaxIdSuccessfully() {
    when(gisService.getDECPolygonByTaxId(Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString())).thenReturn("DEC Polygon by Tax Id");
    String result =
        gisController.getDECPolygonByTaxId(response, taxParcelId, county, municipalName);
    Assert.assertEquals("DEC Polygon by Tax Id", result);
  }

  @Test
  public void testGetDECPolygonByAddressWithInvalidStreet() {
    String result = this.gisController.getDECPolygonByAddress(response, null, city);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void testGetDECPolygonByAddressWithInvalidCity() {
    String result = this.gisController.getDECPolygonByAddress(response, street, null);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void testGetDECPolygonByAddressSuccessfully() {
    when(gisService.getDECPolygonByAddress(Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("DEC Polygon by Address");
    String result = gisController.getDECPolygonByAddress(response, street, city);
    Assert.assertEquals("DEC Polygon by Address", result);
  }

  @Test
  public void testGetDECPolygonByDecIdSuccessfully() {
    when(gisService.getDECPolygonByDecId(Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("DEC ID");
    String result = gisController.getDECPolygonByDecId(response, decId, "token");
    Assert.assertEquals("DEC ID", result);
  }

  @Test
  public void testGetDECPolygonByDecIdWithNoDECId() {
    String result = gisController.getDECPolygonByDecId(response, "token", null);
    Assert.assertNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void testUploadFinalApprovedPolygonToEFindWithNoPolygons() {
    Object response = this.gisController.uploadFinalApprovedPolygonToEFind(null);
    assertNull(response);
  }

  @Test
  public void testUploadFinalApprovedPolygonToEFindSuccessfully() {
    Map<Long, String> status = new HashMap<>();
    status.put(1231L, "Success");
    when(gisService.uploadApprovedPolygonToEFind(Mockito.anyString(), Mockito.anyList()))
        .thenReturn(status);
    Map<Long, String> result = gisController.uploadFinalApprovedPolygonToEFind(
        new ArrayList<>(Arrays.asList(this.getProjectPolygon())));
    Assert.assertEquals("Success", result.get(1231L));
  }

  @Test
  public void testSaveApplicantPolygonSuccessfully() {
    String value = "Value";
    PolygonAction action = PolygonAction.valueOf("S");
    Object featureMap = new Object();
    when(gisService.applicantPolygon(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("Applicant Polygon");
    String result = (String) gisController.saveApplicantPolygon(featureMap, value, action);
    Assert.assertEquals("Applicant Polygon", result);
  }

  @Test
  public void testSaveAnalystPolygonSuccessfully() {
    PolygonAction action = PolygonAction.valueOf("S");
    when(gisService.analystPolygon(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("Analyst Polygon");
    String result = (String) gisController.saveAnalystPolygon(response, featureMap, value, action);
    Assert.assertEquals("Analyst Polygon", result);
  }

  @Test
  public void testSaveSubmitedPolygonSuccessfully() {
    PolygonAction action = PolygonAction.valueOf("S");
    when(gisService.submittedPolygon(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("Submitted Polygon");
    String result = (String) gisController.saveSubmitedPolygon(response, featureMap, value, action);
    Assert.assertEquals("Submitted Polygon", result);
  }

  @Test
  public void testgetApplicantPolygonWithInvalidApplId() {
    String result = gisController.getApplicantPolygon(response, null);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    assertNull(result);
  }

  @Test
  public void testgetApplicantPolygonWithvalidApplId() {
    String applId = "123";
    when(gisService.getApplicantPolygon(Mockito.anyString(), Mockito.anyString())).thenReturn("Get Applicant Polygon");
    String result = gisController.getApplicantPolygon(response, applId);
    Assert.assertEquals("Get Applicant Polygon", result);
  }

  @Test
  public void testGetAnalystPolygonSuccessfully() {
    String analystId = "Analyst";
    when(gisService.getAnalystPolygon(Mockito.anyString(), Mockito.anyString())).thenReturn("Get Analyst Polygon");
    String result = gisController.getAnalystPolygon(response, analystId);
    Assert.assertEquals("Get Analyst Polygon", result);
  }

  @Test
  public void testGetAnalystPolygonWithNoAnalystId() {
    String result = this.gisController.getAnalystPolygon(response, null);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    assertNull(result);
  }

  @Test
  public void testGetsubmitedPolygon() {
    when(gisService.getsubmitedPolygon(Mockito.anyString(), Mockito.anyString())).thenReturn("Get Submitted Polygon");
    String result = this.gisController.getsubmitedPolygon(response, "123");
    Assert.assertEquals("Get Submitted Polygon", result);
  }

  @Test
  public void testGetsubmitedPolygonWithNoSubId() {
    String result = this.gisController.getsubmitedPolygon(response, null);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    assertNull(result);
  }

  @Test
  public void testGetDECIdByProgramTypeSuccessfully() {
    String programType = "Type";
    String programId = "Id";
    Map<String, Object> programTypeDECIdMap = new HashMap<>();
    programTypeDECIdMap.put("ProgramType", "DECID");
    when(gisService.getDECIdByProgramType(Mockito.anyString(), Mockito.anyString(), 
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(programTypeDECIdMap);
    Map<String, Object> result = this.gisController.getDECIdByProgramType(userId, programId, programType, jwtToken);
    Assert.assertEquals("DECID", result.get("ProgramType"));
  }

  @Test(expected = BadRequestException.class)
  public void testGetDECIdByProgramTypeThrowBadRequestForEmptyProgramId() {
    String programType = "Type";
    String programId = null;
    this.gisController.getDECIdByProgramType(userId, programId, programType, jwtToken);
  }

  @Test(expected = BadRequestException.class)
  public void testGetDECIdByProgramTypeThrowBadRequestForEmptyProgramType() {
    String programType = null;
    String programId = "Id";
    this.gisController.getDECIdByProgramType(userId, programId, programType, jwtToken);
  }

  @Test
  public void testGetDECIdByTxmapSuccessfully() {
    String taxMap = "Map";
    when(gisService.getDECIdByTxmap(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(), 
        Mockito.anyString(),Mockito.anyString())).thenReturn(new ResponseEntity<String>("DECID", HttpStatus.OK));
    ResponseEntity<String> result = this.gisController.getDECIdByTxmap(userId, taxMap, county, municipalName, jwtToken);
    Assert.assertEquals("DECID", result.getBody());
  }

  @Test(expected = BadRequestException.class)
  public void testGetDECIdByTxmapThrowsBadRequestForEmptyTaxMap() {
    String taxMap = null;
    this.gisController.getDECIdByTxmap(userId, taxMap, county, municipalName, jwtToken);
  }

  @Test(expected = BadRequestException.class)
  public void testGetDECIdByTxmapThrowsBadRequestForEmptyCounty() {
    String taxMap = "Map";
    this.gisController.getDECIdByTxmap(userId, taxMap, null, municipalName, jwtToken);
  }

  @Test(expected = BadRequestException.class)
  public void testGetDECIdByTxmapThrowsBadRequestForEmptyUserId() {
    String taxMap = "Map";
    this.gisController.getDECIdByTxmap(null, taxMap, county, municipalName, jwtToken);
  }

  @Test
  public void testDeletePolygonByObjIdSuccessfully() {
    when(gisService.deletePolygonByObjId(Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String)this.gisController.deletePolygonByObjId(response, "123");
    Assert.assertEquals("Success", result);
  }

  @Test
  public void testDeleteAnalystPolygonByObjIdSuccessfully() {
    when(gisService.deleteAnalystPolygonByObjId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String)this.gisController.deleteAnalystPolygonByObjId(response, "userId", "123");
    Assert.assertEquals("Success", result);
  }

  @Test
  public void testDeleteApplicantSubmittalPolygonByObjIdSuccessfully() {
    when(gisService.deleteApplicantSubmittalPolygonByObjId(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String)this.gisController.deleteApplicantSubmittalPolygonByObjId(response, "userId", "123");
    Assert.assertEquals("Success", result);
  }

  @Test
  public void testUploadShapeFileSuccessfully() {
    when(gisService.uploadShapefile(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any())).thenReturn("Success");
    Object result = gisController.uploadShapeFile(
        "userId", "filetype", "publishParameters", "value", null);
    Assert.assertEquals("Success", result);
  }

  @Test
  public void testSaveWorkAreaPolygonSuccessfully() {
    when(gisService.saveOrUpdateWorkAreaPolygon(Mockito.anyList(),
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String)this.gisController.saveWorkAreaPolygon((Object) featureMap, value, PolygonAction.S);
    Assert.assertEquals("Success", result);
  }

  @Test
  public void testDeleteWorkAreaPolygonSuccessfully() {
    when(gisService.deleteWorkAreaPolygonByObjId(Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString())).thenReturn(Arrays.asList("Success"));
    Object result = this.gisController.deleteWorkAreaPolygonByObjId("userId", "objectId");
    Assert.assertEquals(Arrays.asList("Success"), result);
  }
  
  @Test
  public void testGetWorkAreaPolygonSuccessfully() {
    when(gisService.getWorkAreaPolygon(Mockito.anyString(),
        Mockito.anyString())).thenReturn("Success");
    String result = (String)this.gisController.getWorkAreaPolygon(response, "workAreaId");
    Assert.assertEquals("Success", result);
  }
  
  @Test
  public void testGetWorkAreaPolygonReturnsEmptyResponseWhenWorkAreaIdIsNotPassed() {
    String result = (String)this.gisController.getWorkAreaPolygon(response, "");
    Assert.assertNull(result);
    Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }
  
  private ProjectPolygon getProjectPolygon() {
    ProjectPolygon polygon = new ProjectPolygon();
    polygon.setApprovedPolygonChangeInd(1234);
    polygon.setEdbDistrictId(30303L);
    polygon.setProjectId(10L);
    return polygon;
  }
}


