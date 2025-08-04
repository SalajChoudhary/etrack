package dec.ny.gov.etrack.dcs.exception;

import org.springframework.http.HttpStatus;
import dec.ny.gov.etrack.dcs.model.Response;

public class DocumentNotFoundException extends RuntimeException {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private Response response;

  public DocumentNotFoundException(String resultCode, String resultMessage) {
    response = new Response();
    response.setResultCode(resultCode);
    response.setResultMessage(resultMessage);
  }

  public DocumentNotFoundException(String resultCode, String resultMessage, HttpStatus status) {
    response = new Response();
    response.setResultCode(resultCode);
    response.setResultMessage(resultMessage);
  }

  public Response getResponse() {
    return this.response;
  }
}
