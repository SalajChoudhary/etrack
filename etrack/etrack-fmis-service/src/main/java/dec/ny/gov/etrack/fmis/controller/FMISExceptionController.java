package dec.ny.gov.etrack.fmis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.exception.FMISException;
import dec.ny.gov.etrack.fmis.model.ErrorResponse;

@ControllerAdvice
public class FMISExceptionController {

  private static final Logger logger = LoggerFactory.getLogger(FMISExceptionController.class.getName());
  
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException bre) {
    logger.error("Error code : {} Error Message : {}", bre.getErrorCode(), bre.getErrorMessage());
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse(bre.getErrorCode(), bre.getErrorMessage()), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(FMISException.class)
  public ResponseEntity<ErrorResponse> handleFMISException(FMISException fe) {
    logger.error("Error Message ", fe.getCause());
    return new ResponseEntity<ErrorResponse>(
        new ErrorResponse(fe.getErrorCode(), fe.getErrorMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
