package dec.ny.gov.etrack.dart.db.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoDataFoundException extends RuntimeException {

  private String errorCode;
  private String errorMessage;
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public NoDataFoundException() {
    super();
  }

  public NoDataFoundException(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
  
  public NoDataFoundException(String errorCode, String errorMessage, Throwable e) {
    super(e);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
  
}
