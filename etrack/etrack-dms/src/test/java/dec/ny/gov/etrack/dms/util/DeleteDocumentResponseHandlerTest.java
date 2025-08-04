package dec.ny.gov.etrack.dms.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.model.Response;

@RunWith(SpringJUnit4ClassRunner.class)
public class DeleteDocumentResponseHandlerTest {

  @InjectMocks
  private DeleteDocumentResponseHandler deleteDocumentResponseHandler;

  @Test
  public void testResponseEntityIsNull() {
    ResponseEntity<Response> responseEntity = null;
    assertNull(deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id"));
  }

  @Test
  public void testResponseBodyIsNull() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
    assertNotNull(deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id"));
    assertNull(responseEntity.getBody());
  }

  @Test
  public void TestReponseEntityAndBodyIsNotNull() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
    deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
  }

  @Test
  public void testReturnsSameResponseCodeWhenResponseCodeNotOk() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity =
        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    deleteDocumentResponseHandler.handleResponse(responseEntity,"contextId", "test user id");
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void testReturnsSameResponseWhenResponseCodeAndSuccessStatusCodeOk() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
    deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("0000", responseEntity.getBody().getResultCode());
  }

  @Test
  public void testReturnsInputResponseAsIsIfHttpStatusIsNotOk() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity =
        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("0000", responseEntity.getBody().getResultCode());
  }

  @Test
  public void testReturnsInputResponseAsIsIfHttpStatusIsOkAndStatCodeIsSuccess() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
    deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("0000", responseEntity.getBody().getResultCode());
  }

  @Test
  public void testResponseEntityReturnsNoContentResponse() {
    Response response = new Response();
    response.setResultCode("0001");
    response
        .setResultMessage("009_0005 - Repository Delete error - FileNet Error: Document not found");
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
    responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertEquals(response.getResultCode(), responseEntity.getBody().getResultCode());
  }

  @Test
  public void testResponseEntityReturnsInternalErrorResponseFor009_0003() {
    Response response = new Response();
    response.setResultCode("0002");
    response
        .setResultMessage("009_0006 - Repository Delete error – FileNet Error: Connection Error");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals(response.getResultCode(), responseEntity.getBody().getResultCode());
  }

  @Test
  public void testResponseEntityReturnsInternalErrorResponseFor009_0004() {
    Response response = new Response();
    response.setResultCode("0002");
    response.setResultMessage(
        "009_0006 - Repository Delete error - Error occured while deleting the document");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals(response.getResultCode(), responseEntity.getBody().getResultCode());
  }

  @Test
  public void testResponseEntityReturnsInternalErrorResponseFor009_0006() {
    Response response = new Response();
    response.setResultCode("0002");
    response.setResultMessage(
        "009_0006 - Repository Delete error - FileNet Error: Credentials not found in config DB");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals(response.getResultCode(), responseEntity.getBody().getResultCode());
  }

  @Test
  public void testResponseEntityReturnsBadRequestResponse() {
    Response response = new Response();
    response.setResultCode("0002");
    response.setResultMessage(
        "013_0002 - Response Error – Fatal Error: could not process your request.");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    responseEntity = deleteDocumentResponseHandler.handleResponse(responseEntity, "contextId", "test user id");
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(response.getResultCode(), responseEntity.getBody().getResultCode());
  }

}
