package dec.ny.gov.etrack.fmis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import dec.ny.gov.etrack.fmis.exception.VPSException;

@ControllerAdvice
public class VPSExceptionController {

  private static final Logger logger = LoggerFactory.getLogger(VPSExceptionController.class.getName());
  
  @ExceptionHandler(VPSException.class)
  public ResponseEntity<Void> handleVPSException(VPSException fe) {
    logger.error("Error Message ", fe);
    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
