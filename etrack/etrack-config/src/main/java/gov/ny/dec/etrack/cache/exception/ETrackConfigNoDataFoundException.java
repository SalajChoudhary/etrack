package gov.ny.dec.etrack.cache.exception;


public class ETrackConfigNoDataFoundException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ETrackConfigNoDataFoundException() {
    super();
  }
  
  public ETrackConfigNoDataFoundException(String errorMessage) {
    super(errorMessage);
  }

}
