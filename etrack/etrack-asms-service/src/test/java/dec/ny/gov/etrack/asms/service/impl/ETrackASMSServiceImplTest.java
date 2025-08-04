package dec.ny.gov.etrack.asms.service.impl;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.asms.handler.ASMSResponseHandler;
import dec.ny.gov.etrack.asms.model.AuthResponse;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackASMSServiceImplTest {

  @InjectMocks
  private ETrackASMSServiceImpl eTrackASMSServiceImpl;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ObjectMapper mapper;
  @Mock
  private ASMSResponseHandler responseHandler;
  
  
  @Test
  public void testReturnsNoContentWhenResponseBodyIsEmpty() {
    ResponseEntity<SchemaSecurityObjects> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
  
  public void testReturnsValidResponseBodyWhenItReceivesSuccessResponseBody() {
    SchemaSecurityObjects objects = new SchemaSecurityObjects();
    objects.setApplication("application");
    ResponseEntity<SchemaSecurityObjects> responseEntity = new ResponseEntity<>(objects, HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    AuthResponse authResponse = new AuthResponse();
    authResponse.setUserId("TestuserId");
    doReturn(authResponse).when(responseHandler).handleASMSObjectAuthResponse(
        Mockito.anyString(), Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("TestuserId", "guid", "contextId");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testReturnsNotFoundContentWhenReceivesASMSNotFoundFaultResponse() {
    HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, 
        "System unavailable scenario");
    doThrow(ex).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  public void testReturnsNotFoundContentWhenReceivesEmptyErrResponseInASMSFaultResponse() {
    HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, 
        "Error Response body empty/blank", "".getBytes(), Charset.defaultCharset());
    doThrow(ex).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }
  
  @Test
  public void testReturnsNoContentWhenReceivesGUIDNotAvailableResponseInASMSFaultResponse() {
    HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, 
        "Status text", "<u>User GUID not found</u>".getBytes(), Charset.defaultCharset());
    doThrow(ex).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }
  
  @Test
  public void testReturnsErrorStatusWhenASMSCallReturnsFailure() {
    HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    doThrow(ex).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNull(response.getBody());
  }
  
  @Test
  public void testReturnsErrorStatusWhenASMSCallReturnsError() {
    doThrow(RuntimeException.class).when(restTemplate).getForEntity(Mockito.anyString(), Mockito.any());
    ResponseEntity<Object> response = eTrackASMSServiceImpl.getUserAuthDetails("userId", "guid", "contextId");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
}
