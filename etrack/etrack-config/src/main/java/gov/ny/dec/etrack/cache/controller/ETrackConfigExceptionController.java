package gov.ny.dec.etrack.cache.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import gov.ny.dec.etrack.cache.exception.ETrackConfigDuplicateDataFoundException;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;
import gov.ny.dec.etrack.cache.exception.ETrackConfigNoDataFoundException;

@ControllerAdvice
public class ETrackConfigExceptionController {

  private static final Logger logger = LoggerFactory.getLogger(ETrackConfigExceptionController.class.getName());
  
  @ExceptionHandler(ETrackConfigException.class)
  public ResponseEntity<String> eTrackConfigException(ETrackConfigException eTrackConfigException) {
    logger.error("ETrackConfigException details {}",  eTrackConfigException);
    return new ResponseEntity<>(eTrackConfigException.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ETrackConfigNoDataFoundException.class)
  public ResponseEntity<String> eTrackConfigNoDataFoundException(
      ETrackConfigNoDataFoundException eTrackConfigDataFoundException) {
    logger.error("ETrackConfigNoDataFoundException details {}",  eTrackConfigDataFoundException);
    logger.error(eTrackConfigDataFoundException.getMessage());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler(ETrackConfigDuplicateDataFoundException.class)
  public ResponseEntity<String> duplicateDataFoundException(
      ETrackConfigDuplicateDataFoundException duplicateDataFoundException) {
    logger.error(duplicateDataFoundException.getMessage());
    return new ResponseEntity<>(duplicateDataFoundException.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
