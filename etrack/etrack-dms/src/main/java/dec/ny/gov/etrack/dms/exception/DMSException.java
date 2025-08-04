package dec.ny.gov.etrack.dms.exception;

public class DMSException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 6638480847830116520L;

  public DMSException() {
    super();
  }

  public DMSException(String message) {
    super(message);
  }

  public DMSException(Throwable throwable) {
    super(throwable);
  }

  public DMSException(String message, Throwable t) {
    super(message, t);
  }

}
