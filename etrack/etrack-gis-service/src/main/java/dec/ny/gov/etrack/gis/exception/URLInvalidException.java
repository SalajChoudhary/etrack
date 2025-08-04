package dec.ny.gov.etrack.gis.exception;

public class URLInvalidException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   */
  public URLInvalidException() {
    super();
  }

  /**
   * Constructor with error message.
   * 
   * @param message - Error message.
   */
  public URLInvalidException(String message) {
    super(message);
  }

  /**
   * Constructor with error message and error details.
   * 
   * @param message - Error message
   * @param e - {@link Throwable}
   */
  public URLInvalidException(String message, Throwable e) {
    super(message,e);
  }

}
