package dec.ny.gov.etrack.permit.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public @Data class BadRequestException extends RuntimeException {

  private String errorCode;
  private String errorMessage;
  private Object object;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public BadRequestException() {
    super();
  }
  
  public BadRequestException(String errorCode, String errorMessage, Object object) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.object = object;
  }
  
  
  public BadRequestException(String errorCode, String errorMessage, Throwable e) {
    super(errorMessage, e);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

}
