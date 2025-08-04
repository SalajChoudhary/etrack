package dec.ny.gov.etrack.gis.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.DataNotFoundException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.Response;

@RunWith(SpringRunner.class)
public class GISExceptionControllerTest {

  @InjectMocks
  private GISExceptionController gisExceptionController; 
  
  @Test
  public void testBadRequestExceptionHandledSuccessfully() {
    ResponseEntity<Object> results = gisExceptionController.handleBadRequestException(
        new BadRequestException("BR_CODE", "Bad request Message", "Input Value"));
    Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), results.getStatusCodeValue());
    Assert.assertEquals("BR_CODE", ((Response)results.getBody()).getResultCode());
    Assert.assertEquals("Bad request Message", ((Response)results.getBody()).getResultResponse());
  }
 
  @Test
  public void testDataNotFoundExceptionHandledSuccessfully() {
    ResponseEntity<Object> results = gisExceptionController.handleDataNotFoundException(
        new DataNotFoundException());
    Assert.assertEquals(HttpStatus.NO_CONTENT.value(), results.getStatusCodeValue());
  }

  @Test
  public void testGISExceptionHandledSuccessfullyWhenNoStatusPassed() {
    ResponseEntity<Object> results = gisExceptionController.handleGISException(
        new GISException("GIS_ERR", "GIS Error details"));
    Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), results.getStatusCodeValue());
    Assert.assertEquals("GIS_ERR", ((Response)results.getBody()).getResultCode());
    Assert.assertEquals("GIS Error details", ((Response)results.getBody()).getResultResponse());
  }

  @Test
  public void testGISExceptionHandledSuccessfullyWhenStatusPassed() {
    ResponseEntity<Object> results = gisExceptionController.handleGISException(
        new GISException("GIS_ERR", "GIS Error details", HttpStatus.CONFLICT));
    Assert.assertEquals(HttpStatus.CONFLICT.value(), results.getStatusCodeValue());
    Assert.assertEquals("GIS Error details", ((String)results.getBody()));
  }
  
  @Test
  public void testURLInvalidExceptionHandledSuccessfully() {
    ResponseEntity<Object> results = gisExceptionController.handleURLInvalidException(
        new URLInvalidException());
    Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), results.getStatusCodeValue());
  }

  @Test
  public void testHttpServerExceptionHandledSuccessfully() {
    ResponseEntity<Object> results = gisExceptionController.handleHttpServerErrorException(
        new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "HttpServerException"));
    Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), results.getStatusCodeValue());
  }
  
  @Test
  public void testHttpClientExceptionHandledSuccessfully() {
    HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found Error");
    ResponseEntity<Object> results = gisExceptionController.handleHttpClientErrorException(clientErrorException);
    Assert.assertEquals(HttpStatus.NOT_FOUND.value(), results.getStatusCodeValue());
  }
}
