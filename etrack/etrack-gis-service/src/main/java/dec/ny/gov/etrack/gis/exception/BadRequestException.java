package dec.ny.gov.etrack.gis.exception;

public class BadRequestException extends RuntimeException {

  private final String errorCode;
  private final String errorMessage;
  private final Object object;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public BadRequestException(String errorCode, String errorMessage, Object object) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.object = object;
  }

  public BadRequestException(String errorCode, String errorMessage, Throwable e) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.object = new Object();
  }

  public String getErrorCode() {
    return this.errorCode;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
  
  public Object getObject() {
    return this.object;
  }
}
