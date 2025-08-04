package dec.ny.gov.etrack.dcs.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class HealthControllerTest {

  @InjectMocks
  private HealthController healthController;
  
  @Test
  public void testHealthControllerReturnsSuccess() {
    String status = healthController.healthCheck();
    Assert.assertEquals("healthy", status);
  }
}
