package gov.ny.dec.etrack.cache.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETrackConfigException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackConfigException.class.getName());

  public ETrackConfigException() {
    super();
  }
  
  public ETrackConfigException(String errorMessage) {
    super(errorMessage);
    logger.error(errorMessage);
  }
  
  public ETrackConfigException(String errorMessage, Exception e) {
    super(errorMessage, e);
    logger.error(errorMessage, e);
  }
  
  public ETrackConfigException(String errorMessage, Throwable e) {
    super(errorMessage, e);
    logger.error(errorMessage, e);
  }
}
