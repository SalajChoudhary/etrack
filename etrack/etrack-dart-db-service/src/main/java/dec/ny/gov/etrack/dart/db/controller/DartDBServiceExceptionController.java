package dec.ny.gov.etrack.dart.db.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Result;

@ControllerAdvice
public class DartDBServiceExceptionController {
  
  private static final Logger logger = LoggerFactory.getLogger(
      DartDBServiceExceptionController.class.getName());
  
  @ExceptionHandler(NoDataFoundException.class)
  public ResponseEntity<Object> handleDataNotFoundException(NoDataFoundException epe) {
    logger.error("No Data found Error details {}" , epe);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequestException(BadRequestException bre) {
    logger.error("Bad Request Error details {}" , bre);
    Result result = new Result(bre.getErrorCode(), bre.getErrorMessage());
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DartDBException.class)
  public ResponseEntity<Object> handleDartDBException(DartDBException dbe) {
    logger.error("DB Error details {}" , dbe);
    Result result = new Result(dbe.getErrorCode(), dbe.getErrorMessage());
    if (dbe.getHttpStatus() != null) {
      return new ResponseEntity<Object>(result, dbe.getHttpStatus());
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
