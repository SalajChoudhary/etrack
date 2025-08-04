package dec.ny.gov.etrack.gis.exception;

/**
 * DataNotFoundException class.
 * 
 * @author mxmahali
 *
 */
public class DataNotFoundException extends RuntimeException {

  private final String errorCode;
  private final String errorMessage;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default Constructor.
   */
  public DataNotFoundException() {
    super();
    this.errorCode = "";
    this.errorMessage = "";
  }

  /**
   * Overloaded constructor with Error code and Message.
   * 
   * @param errorCode - Error Code.
   * @param errorMessage - Error Message.
   */
  public DataNotFoundException(String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
 
  /**
   * Retrieve Error code.
   * 
   * @return - Error code.
   */
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * Retrieve Error Message.
   * 
   * @return - Error Message.
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

}
