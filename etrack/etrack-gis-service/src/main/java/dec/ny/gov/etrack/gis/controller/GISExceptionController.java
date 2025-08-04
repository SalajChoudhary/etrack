package dec.ny.gov.etrack.gis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.DataNotFoundException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.Response;

@ControllerAdvice
public class GISExceptionController {

  /**
   * Logging
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GISExceptionController.class.getName());
  
  @ExceptionHandler(value = BadRequestException.class)
  public ResponseEntity<Object> handleBadRequestException(BadRequestException bre) {
    LOGGER.error("Bad request error message details ", bre);
    return new ResponseEntity<>(new Response(bre.getErrorCode(), bre.getErrorMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = DataNotFoundException.class)
  public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException dnfe) {
    LOGGER.error("Data not found error message details ", dnfe);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler(value= GISException.class)
  public ResponseEntity<Object> handleGISException(GISException ge) {
    LOGGER.error("General error occurred while procesing the request ", ge);
    Response errorResponse = new Response();
    errorResponse.setResultCode(ge.getErrorCode());
    errorResponse.setResultResponse(ge.getMessage());
    if (ge.getStatus() == null) {
      return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      return new ResponseEntity<>(ge.getMessage(), ge.getStatus());
    }
  }
 
  @ExceptionHandler(value= URLInvalidException.class)
  public ResponseEntity<Object> handleURLInvalidException(URLInvalidException uie) {
    LOGGER.error("Unsupported encoded details are available in the URL ", uie);
    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
  }
  
  @ExceptionHandler(value= HttpServerErrorException.class)
  public ResponseEntity<Object> handleHttpServerErrorException(HttpServerErrorException hse) {
    LOGGER.error("Unsuccessful response received while making a call to GIS service or other eTrack service ", hse);
    return new ResponseEntity<>(hse.getResponseBodyAsString(), hse.getStatusCode());
  }

  @ExceptionHandler(value= HttpClientErrorException.class)
  public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException hse) {
    LOGGER.error("Unsuccessful response reported while making a call to GIS service or other eTrack service ", hse);
    return new ResponseEntity<>(hse.getResponseBodyAsString(), hse.getStatusCode());
  }

}
