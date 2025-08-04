package dec.ny.gov.etrack.gis.controller;

import static org.mockito.Mockito.when;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.FacilityAddress;
import dec.ny.gov.etrack.gis.model.FacilityDetail;
import dec.ny.gov.etrack.gis.model.ProjectDetail;
import dec.ny.gov.etrack.gis.service.GISFacilityService;

@RunWith(SpringJUnit4ClassRunner.class)
public class GISFacilityControllerTest {
  
  @InjectMocks
  private GISFacilityController gisFacilityController;

  @Mock
  private GISFacilityService gisFacilityService;
  private MockHttpServletResponse response = new MockHttpServletResponse();
  
  private String jwtToken = "JWT TOKEN";
  private String userId = "userid";

  @Test
  public void testSaveFacilityDetailSuccessfully() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    when(gisFacilityService.saveFacilityDetail(Mockito.anyString(), Mockito.anyString(), 
        Mockito.anyString(), Mockito.any(ProjectDetail.class))).thenReturn(projectDetail);
    ProjectDetail projectDetailResult = gisFacilityController.saveFacilityDetail(userId, jwtToken, projectDetail);
    Assert.assertEquals("300L", projectDetailResult.getProjectId());
  }

  @Test
  public void testUpdateFacilictyDetailsSuccessfully() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    when(gisFacilityService.updateFacilityDetail(Mockito.anyString(), Mockito.anyString(), 
        Mockito.anyString(), Mockito.any(ProjectDetail.class))).thenReturn(projectDetail);
    ProjectDetail projectDetailResult = gisFacilityController.updateFacilityDetails(userId, jwtToken, projectDetail);
    Assert.assertEquals("300L", projectDetailResult.getProjectId());
  }

  @Test(expected = BadRequestException.class)
  public void testUpdateFacilictyDetailsThrowsBadRequestForEmptyProjectID() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    projectDetail.setProjectId(null);
    this.gisFacilityController.updateFacilityDetails(userId, jwtToken, projectDetail);
  }

  @Test(expected = BadRequestException.class)
  public void testUpdateFacilictyDetailsThrowsBadRequestForProjectIDZero() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    projectDetail.setProjectId(0L);
    this.gisFacilityController.updateFacilityDetails(userId, jwtToken, projectDetail);
  }

  @Test(expected = BadRequestException.class)
  public void testUpdateFacilictyDetailsThrowsBadRequestForNegativeProjectID() {
    ProjectDetail projectDetail = getValidProjectDetailObj();
    projectDetail.setProjectId(-1L);
    this.gisFacilityController.updateFacilityDetails(userId, jwtToken, projectDetail);
  }
  
  @Test(expected = BadRequestException.class)
  public void testGetFacilityDetailThrowsBadRequestForNegativeProjId() {
    Long projectId = -20L;
    this.gisFacilityController.getFacilityDetail(userId, jwtToken, projectId);
  }

  @Test(expected = BadRequestException.class)
  public void testGetFacilityDetailThrowsBadRequestForProjectIdZero() {
    Long projectId = 0L;
    this.gisFacilityController.getFacilityDetail(userId, jwtToken, projectId);
  }

  @Test(expected = BadRequestException.class)
  public void testGetFacilityDetailThrowsBadRequestForEmptyProjId() {
    Long projectId = null;
    this.gisFacilityController.getFacilityDetail(userId, jwtToken, projectId);
  }

  @Test
  public void testGetFacilityDetailSuccessfully() {
    Long projectId = 20L;
    ProjectDetail projectDetail = getValidProjectDetailObj();
    projectDetail.setProjectId(20L);
    when(gisFacilityService.retrieveFacilityInfo(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(
            new ResponseEntity<>(projectDetail, HttpStatus.OK));
    ResponseEntity<ProjectDetail> projectDetailResponse = gisFacilityController.getFacilityDetail(userId, jwtToken, projectId);
    Assert.assertEquals(HttpStatus.OK, projectDetailResponse.getStatusCode());
    Assert.assertEquals("20L", projectDetailResponse.getBody().getProjectId());
  }

  @Test
  public void testGetFacilityViewDetailSuccessfully() throws JsonProcessingException {
    Long projectId = 20L;
    ProjectDetail projectDetail = getValidProjectDetailObj();
    projectDetail.setProjectId(20L);
    ObjectMapper objectMapper = new ObjectMapper();
    String value = objectMapper.writeValueAsString(projectDetail);
    JsonNode jsonNode = objectMapper.readTree(value);
    when(gisFacilityService.retrieveFacilityHistory(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(
            new ResponseEntity<>(jsonNode, HttpStatus.OK));
    ResponseEntity<JsonNode> projectDetailResponse = gisFacilityController.retrieveFacilityHistory(response, userId, jwtToken, projectId);
    Assert.assertEquals(HttpStatus.OK, projectDetailResponse.getStatusCode());
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
