package dec.ny.gov.etrack.dms.controller;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import io.cucumber.java.Before;

@RunWith(SpringJUnit4ClassRunner.class)
public class HealthControllerTest {

  @InjectMocks
  HealthController healthController;

//  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldReturnHealthCheck() {
    assertEquals("healthy", healthController.healthCheck());
  }
}
