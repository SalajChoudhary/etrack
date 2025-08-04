package dec.ny.gov.etrack.asms.exception;

public class ETrackASMSException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ETrackASMSException() {
    super();
  }

  public ETrackASMSException(String errorMessage) {
    super(errorMessage);
  }

  public ETrackASMSException(String errorMessage, Throwable t) {
    super(errorMessage, t);
  }
}
