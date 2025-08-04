package dec.ny.gov.etrack.dms.util;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class EcMaaSInterceptor implements ClientHttpRequestInterceptor {

  private final String basicAuth;

  public EcMaaSInterceptor(String basicAuth) {
    this.basicAuth = basicAuth;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
//    headers.setBasicAuth(getValue(basicAuth));
    headers.setBasicAuth(basicAuth);
    return execution.execute(request, body);
  }
}
