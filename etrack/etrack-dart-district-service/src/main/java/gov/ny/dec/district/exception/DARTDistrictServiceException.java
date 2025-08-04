package gov.ny.dec.district.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author mxmahali
 *
 */
public class DARTDistrictServiceException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private HttpStatus httpStatus;

  public DARTDistrictServiceException() {
    super();
  }
  
  public DARTDistrictServiceException(String message) {
    super(message);
  }
  
  public DARTDistrictServiceException(String message, Throwable e) {
    super(message, e);
  }

  public DARTDistrictServiceException(HttpStatus status, String message, Throwable e) {
    super(message, e);
    this.httpStatus = status;
  }

  public DARTDistrictServiceException(HttpStatus status, String message) {
    super(message);
    this.httpStatus = status;
  }
  
  public HttpStatus getStatus() {
    return httpStatus;
  }
}
