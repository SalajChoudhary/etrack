package dec.ny.gov.etrack.fmis.exception;

import org.springframework.http.HttpStatus;
import lombok.Data;

public @Data class FMISException extends RuntimeException {

  private final String errorCode;
  private final String errorMessage;
  private final HttpStatus status;
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public FMISException(String errorCode, String errorMessage, Throwable t) {
    super (errorMessage, t);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = null;
  }
  
  public FMISException(String errorCode, String errorMessage, HttpStatus status) {
    super (errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = status;
  }
  
  public FMISException(Throwable t) {
    super(t);
    this.errorCode = null;
    this.errorMessage = null;
    this.status = null;

  }
}
