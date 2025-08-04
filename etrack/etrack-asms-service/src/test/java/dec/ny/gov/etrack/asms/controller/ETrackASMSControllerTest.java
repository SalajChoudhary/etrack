package dec.ny.gov.etrack.asms.controller;

import static org.mockito.Mockito.when;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.google.common.net.HttpHeaders;
import dec.ny.gov.etrack.asms.service.ETrackASMSService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackASMSControllerTest {

  @InjectMocks
  private ETrackASMSController controller;

  @Mock
  private ETrackASMSService service;
  @Mock
  private JwtDecoder jwtDecoder;
  private Jwt jwt;

  @Before
  public void setUp() {
    Map<String, Object> headers = new HashMap<>();
    headers.put(HttpHeaders.AUTHORIZATION, "Bearer Token");
    headers.put("typ", "JWT");
    headers.put("alg", "none");
    Map<String, Object> claims = new HashMap<>();
    claims.put("custom:status", "active");
    jwt = new Jwt("fake-token", Instant.now(), Instant.now().plusSeconds(100), headers, claims);
  }
  
  @Test
  public void testGetuserAuthDetailsReturnsSuccessResponse() {
    ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
    when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
    when(service.getUserAuthDetails(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);
    ResponseEntity<Object> responseEntity = controller.getUserAuthDetails("userId", "token");
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testGetuserRolesDetailsReturnsSuccessResponse() {
    ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
    when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
    when(service.getRoles(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);
    ResponseEntity<Object> responseEntity = controller.getUserRoles("userId", "token");
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
}
