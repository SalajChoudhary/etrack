package gov.ny.dec.district.exception;

import gov.ny.dec.dart.district.model.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException {
  
  private Response errorResponse;
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public ValidationException() {
    super();
  }
  
  public ValidationException(String message) {
    super(message);
  }
  
  public ValidationException(String errorCode, String errorMessage) {
    errorResponse = new Response();
    errorResponse.setResultCode(errorCode);
    errorResponse.setResultMessage(errorMessage);
  }
}
