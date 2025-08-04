package dec.ny.gov.etrack.permit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/healthCheck")
  public String healthCheck() {
    return "healthy";
  }
}
