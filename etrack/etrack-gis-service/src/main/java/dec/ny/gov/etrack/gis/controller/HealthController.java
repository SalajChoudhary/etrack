package dec.ny.gov.etrack.gis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller to check the status of the server.
 * 
 * @author mxmahali
 *
 */
@RestController
public class HealthController {

  /**
   * Health check end point.
   * 
   * @return  - status of the health.
   */
  @GetMapping("/healthCheck")
  public String healthCheck() {
    return "healthy";
  }
}
