package dec.ny.gov.etrack.permit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.Result;

@ControllerAdvice
public class ETrackPermitExceptionController {

  private Logger logger = LoggerFactory.getLogger(ETrackPermitExceptionController.class.getName());
  
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequestException(BadRequestException bre) {
    logger.error("Bad request exception error details {}" , bre);
    Object request = bre.getObject();
    if (request != null) {
      if (request instanceof String) {
        logger.error("Error details Error Code: {}, Error Message: {}, "
            + "Request Body {} ", bre.getErrorCode(), bre.getErrorMessage(),  request);
      } else {
        try {
          logger.error("Error details Error Code: {}, Error Message: {}, "
              + "Request Body {} ", bre.getErrorCode(), bre.getErrorMessage(),  new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }
    Result result = new Result(bre.getErrorCode(), bre.getErrorMessage());
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException epe) {
    logger.error("Error details {}" , epe);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler(ETrackPermitException.class)
  public ResponseEntity<Object> handleETrackPermitException(ETrackPermitException epe) {
    logger.error("Error details. ", epe);
    Result result = new Result(epe.getErrorCode(), epe.getErrorMessage());
    if (epe.getHttpStatus() != null) {
      return new ResponseEntity<>(result, epe.getHttpStatus());
    } else {
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ExceptionHandler(DataExistException.class)
  public ResponseEntity<Object> handleDataExistException(DataExistException epe) {
    logger.error("Error details {}" , epe);
    return new ResponseEntity<>(epe.getErrorMessage(), HttpStatus.CONFLICT);
  }
  
  @ExceptionHandler(UnexpectedRollbackException.class)
  public ResponseEntity<Object> handleDBDataRollbackException(UnexpectedRollbackException ure) {
    logger.error("Error details {}" , ure);
    Result result = new Result("INCORRECT_DATA_PASSED", 
        "Data mismatch or missing value while persisting into DB " + ure.getMessage());
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }
}
