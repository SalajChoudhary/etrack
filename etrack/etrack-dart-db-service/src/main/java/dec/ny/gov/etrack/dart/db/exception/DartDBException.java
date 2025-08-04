package dec.ny.gov.etrack.dart.db.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DartDBException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String errorCode;
  private String errorMessage;
  private HttpStatus httpStatus;
  
  public DartDBException() {
    super();
  }
  
//  public DartDBException(String message) {
//    super(message);
//  }
//  
//  public DartDBException(String message, Throwable e) {
//    super(message, e);
//  }

  public DartDBException(final String errorCode, final String message) {
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

  public DartDBException(final String errorCode, final String message, Throwable e) {
    super(e);
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

  public DartDBException(final HttpStatus httpStatus,  final String errorCode, final String message, Throwable e) {
    super(e);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.errorMessage = message;
  }

}
