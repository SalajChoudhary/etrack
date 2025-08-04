package dec.ny.gov.etrack.fmis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;

@RestController
public class HealthController {

  @GetMapping("/health")
  @ApiOperation(value="Health check end point.")
  public String healthCheck() {
    return "Healthy";
  }
}
