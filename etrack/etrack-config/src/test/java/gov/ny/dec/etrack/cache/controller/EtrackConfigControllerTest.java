package gov.ny.dec.etrack.cache.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.etrack.cache.service.ETrackConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
public class EtrackConfigControllerTest {

  @InjectMocks
  ETrackConfigController eTrackConfigController;
  
  @Mock
  ETrackConfigService eTrackConfigService;
  
  ResponseEntity<?> responseEntity =  null;
  
  @Before
  public void setup() {
    responseEntity = new ResponseEntity<>(HttpStatus.OK);
  }
  
  
  @Test
  public void testGetDocTypeAndSubType() {
    doReturn(responseEntity).when(eTrackConfigService).getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
    assertTrue(eTrackConfigController.getDocTypeAndSubTypes() instanceof ResponseEntity);
  }
  
  
  @Test
  public void testGetMessages() {
    doReturn(responseEntity).when(eTrackConfigService).getMessages(Mockito.anyString(), Mockito.anyString());
    assertTrue(eTrackConfigController.getMessages() instanceof ResponseEntity);
  }
  
}
