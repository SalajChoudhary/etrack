package dec.ny.gov.etrack.permit.exception;

public class DataNotFoundException extends RuntimeException {

  private String errorCode;
  private String errorMessage;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DataNotFoundException() {
    super();
  }
  
  public DataNotFoundException(String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
 
  public String getErrorCode() {
    return this.errorCode;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
}
