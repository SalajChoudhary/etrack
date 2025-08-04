package dec.ny.gov.etrack.permit.exception;

import org.springframework.http.HttpStatus;
import lombok.Data;

public @Data class ETrackPermitException extends RuntimeException {

  private String errorCode;
  private String errorMessage;
  private HttpStatus httpStatus;
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ETrackPermitException() {
    super();
  }
  
  public ETrackPermitException(final String errorCode, final String errorMessage) {
//    super(errorMessage);
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }

  public ETrackPermitException(final String errorCode, final String errorMessage, Throwable e) {
    super(e);
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }
  
  public ETrackPermitException(final HttpStatus httpStatus, final String errorCode, final String errorMessage) {
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
  
}
