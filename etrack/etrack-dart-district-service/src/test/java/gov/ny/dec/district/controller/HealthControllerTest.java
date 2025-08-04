package gov.ny.dec.district.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.district.controller.HealthController;

@RunWith(SpringJUnit4ClassRunner.class)
public class HealthControllerTest {

  @InjectMocks
  private HealthController healthController;
  
  @Test
  public void testHealthCheck() {
    String result = healthController.healthCheck();
    assertEquals("healthy", result);
  }
  
}
