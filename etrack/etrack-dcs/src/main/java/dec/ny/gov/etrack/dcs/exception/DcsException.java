package dec.ny.gov.etrack.dcs.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dec.ny.gov.etrack.dcs.model.Response;

public class DcsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -482735303079486413L;
  private Logger logger = LoggerFactory.getLogger(DcsException.class.getName());

  private Response errorResponse;

  public DcsException() {
    super();
  }

  public DcsException(String message) {
    super(message);
  }

  public DcsException(Throwable throwable) {
    super(throwable);
  }

  public DcsException(String message, Throwable t) {
    super(message, t);
  }

  public DcsException(String resultCode, String resultMessage) {
    errorResponse = new Response();
    errorResponse.setResultCode(resultCode);
    errorResponse.setResultMessage(resultMessage);
  }

  public DcsException(String resultCode, String resultMessage, Throwable e) {
    errorResponse = new Response();
    errorResponse.setResultCode(resultCode);
    errorResponse.setResultMessage(resultMessage);
    logger.error("Error" , e);
  }

  public Response getErrorResponse() {
    return this.errorResponse;
  }
}
