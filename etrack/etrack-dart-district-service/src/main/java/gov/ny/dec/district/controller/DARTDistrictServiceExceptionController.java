package gov.ny.dec.district.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import gov.ny.dec.dart.district.model.Response;
import gov.ny.dec.district.exception.DARTDistrictServiceException;
import gov.ny.dec.district.exception.ValidationException;

@ControllerAdvice
public class DARTDistrictServiceExceptionController {

  private static final Logger logger =
      LoggerFactory.getLogger(DARTDistrictServiceExceptionController.class.getName());

  @ExceptionHandler(DARTDistrictServiceException.class)
  public ResponseEntity<String> dartDistrictException(DARTDistrictServiceException exception) {
    logger.error(exception.getMessage() , exception);
    if (exception.getStatus() != null) {
      return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Response> dartDistrictException(ValidationException exception) {
    logger.error(exception.getErrorResponse().getResultCode(), exception.getErrorResponse().getResultMessage());
    return new ResponseEntity<Response>(exception.getErrorResponse(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  public ResponseEntity<String> handleException(MissingRequestHeaderException mrhe) {
    return new ResponseEntity<String>("Header field 'userId' is missing", HttpStatus.BAD_REQUEST);
  }
}
