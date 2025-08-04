
package dec.ny.gov.etrack.dcs.exception;

import dec.ny.gov.etrack.dcs.model.Response;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionControllerHandler {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerHandler.class);

  @ExceptionHandler(DcsException.class)
  public ResponseEntity<Response> handleDCSException(DcsException e) {
    logger.error("Internal server error inside ExceptionControllerHandler ", e);
    return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(JDBCException.class)
  public ResponseEntity<Void> handleJDBCException(JDBCException e) {
    logger.error("Error in JDBC : ", e);
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DocumentNotFoundException.class)
  public ResponseEntity<Response> handleDocumentNotFoundException(DocumentNotFoundException dnfe) {
    logger.error("internal server error inside DocumentNotFoundException ", dnfe);
    return new ResponseEntity<>(dnfe.getResponse(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Response> handleValidationException(ValidationException ve) {
    logger.error("internal server error inside handleValidationException ", ve);
    return new ResponseEntity<>(ve.getErrorResponse(), HttpStatus.BAD_REQUEST);
  }
}
