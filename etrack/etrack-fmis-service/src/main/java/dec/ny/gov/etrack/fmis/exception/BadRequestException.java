package dec.ny.gov.etrack.fmis.exception;

import lombok.Data;

public @Data class BadRequestException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String errorCode;
  private String errorMessage;

  public BadRequestException() {
    super();
  }
  
  public BadRequestException(final String errorCode, final String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
