package dec.ny.gov.etrack.gis.exception;

import org.springframework.http.HttpStatus;

/**
 * GISException class.
 * 
 * @author mxmahali
 *
 */
public class GISException extends RuntimeException {

  private final String errorCode;
  private final String errorMessage;
  private final HttpStatus status;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default Constructor.
   */
  public GISException() {
    super();
    this.errorCode = "";
    this.errorMessage = "";
    this.status = null;
  }
  
  /**
   * Overloaded constructor with Error code and Message.
   * 
   * @param errorCode - Error Code.
   * @param errorMessage - Error Message.
   */
  public GISException(String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = null;
  }
  
  /**
   * Overloaded constructor with Error code, Message and Exception.
   * 
   * @param errorCode - Error code.
   * @param errorMessage - Error Message.
   * @param e - {@link Throwable}
   */
  public GISException(String errorCode, String errorMessage, Throwable e) {
    super(errorMessage, e);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = null;
  }

  /**
   * Overloaded constructor with Error code, Message and Exception.
   * 
   * @param errorCode - Error code.
   * @param errorMessage - Error Message.
   * @param status - {@link HttpStatus}
   */
  public GISException(String errorCode, String errorMessage, HttpStatus status) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = status;
  }

  /**
   * Overloaded constructor with Error code, Message and Exception.
   * 
   * @param errorCode - Error code.
   * @param errorMessage - Error Message.
   * @param status - HttpStatus.
   * @param e - {@link HttpStatus}
   */
  public GISException(String errorCode, String errorMessage, HttpStatus status, Throwable e) {
    super(errorMessage, e);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.status = status;
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

  /**
   * Retrieve the HttpStatus.
   *  
   * @return - Status code.
   */
  public HttpStatus getStatus() {
    return status;
  }
  
}
