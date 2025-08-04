package dec.ny.gov.etrack.gis.controller;

import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.model.PolygonAction;
import dec.ny.gov.etrack.gis.service.SpatialInquiryService;

@RunWith(SpringRunner.class)
public class SpatialInquiryControllerTest {

  @InjectMocks
  private SpatialInquiryController spatialInquiryController;
  @Mock
  private SpatialInquiryService spatialInquiryService;
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @Test
  public void testSaveSpatialInquiryApplicantPolygonReturnsSuccess() {
    when(spatialInquiryService.spatialInquiryApplicantPolygon(
        Mockito.anyList(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String)spatialInquiryController.saveSpatialInquiryApplicantPolygon(null, "value", PolygonAction.S);
    Assert.assertEquals("Success", result);
  }
  
  @Test
  public void testGetSpatialPolygonByInquiryIdReturnsEmptyResult() {
   String result = spatialInquiryController.getSpatialPolygonByInquiryId(response,"");
   Assert.assertNull(result);
   Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }
  
  @Test
  public void testGetSpatialPolygonByInquiryIdRetursSuccessfully() {
    when(spatialInquiryService.getSpatialPolygonByApplicationId(Mockito.anyString(), Mockito.anyString())).thenReturn("Success"); 
    String result = spatialInquiryController.getSpatialPolygonByInquiryId(response,"spatialInquiryId");
    Assert.assertEquals("Success", result);
  }
  
  @Test
  public void testDeleteSpatialInquiryPolygonByObjIdReturnsSuccessfully() {
    when(spatialInquiryService.deleteSpatialInqPolygonByObjId(Mockito.anyString(), Mockito.anyString())).thenReturn("Success");
    String result = (String) spatialInquiryController.deleteSpatialInquiryPolygonByObjId(response, "objectId");
    Assert.assertEquals("Success", result);
  }
  
  @Test
  public void testSaveSpatialInquiryDetailsReturnsSuccessfully() {
    when(spatialInquiryService.saveSpatialInqDetails(Mockito.anyString(), 
        Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn("Success");
    String result = (String) spatialInquiryController.saveSpatialInquiryDetails("userId", "jwtToken", null);
    Assert.assertEquals("Success", result);
  }
  
  @Test
  public void testGetSpatialInquiryDetailsReturnsSuccessfully() {
    when(spatialInquiryService.getSpatialInquiryDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
        Mockito.anyString())).thenReturn("Success");
    String result = (String) spatialInquiryController.getSpatialInquiryDetails("userId", "requestor name", "jwtToken", 123L);
    Assert.assertEquals("Success", result);
  }
  

  @Test
  public void testSaveSpatialInquiryResponseDetailsReturnsSuccessfully() {
    when(spatialInquiryService.saveSpatialInquiryResponseDetails(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(
            new GISServiceResponse());
    spatialInquiryController.saveSpatialInquiryResponseDetails(
        response, "userId", "contextId", "jwtToken", null);
    Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

}
