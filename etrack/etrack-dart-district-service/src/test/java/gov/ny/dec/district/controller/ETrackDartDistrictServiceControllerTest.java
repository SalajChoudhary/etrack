package gov.ny.dec.district.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.service.DARTDistrictService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackDartDistrictServiceControllerTest {

  @InjectMocks
  private ETrackDartDistrictServiceController controller;

  @Mock
  private DARTDistrictService dartDistrictService;
  
  @Test
  public void testRetrieveDistrictsByDecId() {
    ResponseEntity<DistrictDetail> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(responseEntity).when(dartDistrictService).getDistrictDetailsByDecId(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    assertTrue(controller.retrieveDistrictsByDecId("userID", "7083527A-0000-C21B-90B2-4D44BDF4929B") instanceof ResponseEntity);
  }

  @Test
  public void testRetrieveDistrictsByFacilityName() {
    ResponseEntity<DistrictDetail> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(responseEntity).when(dartDistrictService).getDistrictDetailsByFacilityName(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    assertTrue(controller.retrieveDistrictsByFacilityName("userID", "FACILY", "S") instanceof ResponseEntity);
  }
  
  @Test
  public void testGetDartDistrictDetailsByDistrictId() {
    ResponseEntity<DistrictDetail> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(responseEntity).when(dartDistrictService).getDistrictDetails(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    assertTrue(controller.getDistrictDetailsByDistrictId("userId", 23424L) instanceof ResponseEntity);
  }
}
