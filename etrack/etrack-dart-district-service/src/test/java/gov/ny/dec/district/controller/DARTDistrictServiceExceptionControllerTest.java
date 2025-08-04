package gov.ny.dec.district.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.MissingRequestHeaderException;
import gov.ny.dec.district.exception.DARTDistrictServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
public class DARTDistrictServiceExceptionControllerTest {

  @InjectMocks
  private DARTDistrictServiceExceptionController dARTDistrictServiceExceptionController;

  @Test
  public void testRunsInternalErrorWhenReceivesDARTDistrictServiceExcecption() {
    assertTrue((dARTDistrictServiceExceptionController.dartDistrictException(
        new DARTDistrictServiceException("Test")) instanceof ResponseEntity));
  }
  
  @Test
  public void testRunsBadRequestWhenReceivesMissingHeaderException() {
    ResponseEntity response = dARTDistrictServiceExceptionController.handleException(
        new MissingRequestHeaderException("Test", null));
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
  }
}
