package dec.ny.gov.etrack.dms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import dec.ny.gov.etrack.dms.exception.DMSException;

@ControllerAdvice
public class DMSExceptionController {

  private static final Logger logger =
      LoggerFactory.getLogger(DMSExceptionController.class.getName());

  @ExceptionHandler(value = DMSException.class)
  public ResponseEntity<String> dmsException(DMSException dmsException) {
    logger.error("Error Message {}", dmsException);
    return new ResponseEntity<>("Message", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
