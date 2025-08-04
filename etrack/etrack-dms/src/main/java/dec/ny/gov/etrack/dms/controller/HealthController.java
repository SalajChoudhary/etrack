package dec.ny.gov.etrack.dms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthCheck")
public class HealthController {

  @GetMapping
  public String healthCheck() {
    return "healthy";
  }
}
