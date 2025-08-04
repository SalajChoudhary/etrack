package dec.ny.gov.etrack.gis.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class HealthControllerTest {

  @InjectMocks
  private HealthController healthController;
  
  @Test
  public void testHealthCheckSuccessfully() {
    String result = healthController.healthCheck();
    Assert.assertEquals("healthy", result);
  }
}
