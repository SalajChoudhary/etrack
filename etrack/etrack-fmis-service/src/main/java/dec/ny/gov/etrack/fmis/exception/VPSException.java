package dec.ny.gov.etrack.fmis.exception;

public class VPSException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public VPSException() {
    super();
  }
  
  public VPSException(String errorMessage, Throwable t) {
    super (errorMessage, t);
  }
  public VPSException(Throwable t) {
    super(t);
  }
  public VPSException(String message) {
    super(message);
  }
  
}
