package gov.ny.dec.district.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {

  private static Logger logger = LoggerFactory.getLogger(HealthController.class.getName());
  
  @GetMapping("/health")
  public String healthCheck() {
    logger.info("Health Controller is invoked");
    return "healthy";
  }
  
}
