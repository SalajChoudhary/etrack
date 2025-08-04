package dec.ny.gov.etrack.dcs.exception;

import dec.ny.gov.etrack.dcs.model.Response;

public class ValidationException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -482735303079486413L;

  private Response errorResponse;

  public ValidationException(String resultCode, String resultMessage) {
    errorResponse = new Response();
    errorResponse.setResultCode(resultCode);
    errorResponse.setResultMessage(resultMessage);
  }

  public Response getErrorResponse() {
    return this.errorResponse;
  }
}
