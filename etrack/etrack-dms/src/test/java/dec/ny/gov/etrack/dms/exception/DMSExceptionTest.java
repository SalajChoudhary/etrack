package dec.ny.gov.etrack.dms.exception;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DMSExceptionTest {

  private DMSException dmsException;
  
  @Test
  public void testDMSExceptionCreatedSuccessfully() {
    dmsException = new DMSException();
    assertNotNull(dmsException);
  }
}
