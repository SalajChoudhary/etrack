package dec.ny.gov.etrack.dms.controller;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.exception.DMSException;

@RunWith(SpringJUnit4ClassRunner.class)
public class DMSExceptionControllerTest {

  @InjectMocks
  private DMSExceptionController dmsExceptionController;

  @Test
  public void testDMSExceptionHandler() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
        dmsExceptionController.dmsException(new DMSException("Exception Test")).getStatusCode());
  }
}
