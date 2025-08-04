package gov.ny.dec.etrack.cache.exception;


public class ETrackConfigDuplicateDataFoundException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ETrackConfigDuplicateDataFoundException() {
    super();
  }
  
  public ETrackConfigDuplicateDataFoundException(String errorMessage) {
    super(errorMessage);
  }

}
