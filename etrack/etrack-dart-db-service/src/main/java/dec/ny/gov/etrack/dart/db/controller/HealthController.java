package dec.ny.gov.etrack.dart.db.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;

@RestController
public class HealthController {

  @GetMapping("/healthCheck")
  @ApiOperation(value="Returns the healthy of this service.")
  public String healthCheck() {
    return "healthy";
  }
}
