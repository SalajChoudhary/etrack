package dec.ny.gov.etrack.dcs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
  @RestController
  @RequestMapping("/healthCheck")
  public class HealthController {

    @GetMapping
    @ApiOperation(value="Health Check of the service")
    public String healthCheck() {
      return "healthy";
    }
  }
