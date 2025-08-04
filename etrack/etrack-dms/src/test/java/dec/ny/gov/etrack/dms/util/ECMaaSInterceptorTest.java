package dec.ny.gov.etrack.dms.util;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class ECMaaSInterceptorTest {

  @InjectMocks
  private EcMaaSInterceptor ecMaaSInterceptor;

  @Mock
  ClientHttpRequestExecution clientHttpRequestExecution;

  @Test
  public void testHttpRequestInterceptInvocation() throws Exception {
    ClientHttpResponse clientHttpResponse =
        new MockClientHttpResponse("This is test".getBytes(), HttpStatus.OK);
    doReturn(clientHttpResponse).when(clientHttpRequestExecution)
        .execute(Mockito.any(HttpRequest.class), Mockito.any());
    ecMaaSInterceptor = new EcMaaSInterceptor("TestAuth");
    clientHttpResponse = ecMaaSInterceptor.intercept(new MockClientHttpRequest(),
        "Test String".getBytes(), clientHttpRequestExecution);
    assertTrue(clientHttpResponse instanceof ClientHttpResponse);
  }
}
