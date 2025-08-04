package dec.ny.gov.etrack.dms.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DocumentResult;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentResultResponseHandlerTest {

  @InjectMocks
  private DocumentResultResponseHandler documentResultResponseHandler;

  @Test
  public void testNoResponseBodyIsAvailableInRequest() {
    ResponseEntity<DocumentResult> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
    String contextId = "contextID";
    String userId = "Test User Id";
    ResponseEntity<DMSDocumentResponse> dmsDocumentResponseEntity =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertNull(dmsDocumentResponseEntity.getBody());
    assertTrue(dmsDocumentResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testConvertedDocumentResponseWhenStatusCodeIsNotOk() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage("005_0016 Error occured executing query in the FileNet");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.BAD_REQUEST);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, documentResponse.getStatusCode());
  }

  @Test
  public void testDocumentResponseResultCodeIsOkAndDocumentReturnedSuccessfully() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0000");
    documentResult.setResultMessage("0000 000_0000 Query executed Successfully");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(1);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.OK, documentResponse.getStatusCode());
    assertNotNull(documentResponse.getBody());
    assertEquals("0000", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage005_0016() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage("005_0016 Error occured executing query in the FileNet");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage005_0017() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage("005_0017 Error occured executing query in the FileNet");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage003_0017() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult
        .setResultMessage("003_0017 Error occured obtaining Functional Route from ConfigDB");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage003_0018() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage(
        "003_0018 Error occurred obtaining ObjectStore Credentials from ConfigDB");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage003_0019() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult
        .setResultMessage("003_0019 Error occured obtaining Functional Route from ConfigDB");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testInternalServerErrorResponseWhenFileNetIsNotAbleToRespondAndSendsResultMessage003_0020() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult
        .setResultMessage("003_0020 Error occured obtaining Functional Route from ConfigDB");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testDocumentResponseResultCodeIsOkAndDocumentReturnedValueIsNegative() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0000");
    documentResult.setResultMessage("0000 000_0000 Query executed Successfully");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(-1);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, documentResponse.getStatusCode());
    assertEquals("0000", documentResponse.getBody().getResultCode());
  }

  @Test
  public void testDocumentResponseResultCodeIsOkAndNoDocumentReturned() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0000");
    documentResult.setResultMessage("0000 000_0000 Query executed Successfully");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.NO_CONTENT, documentResponse.getStatusCode());
  }

  @Test
  public void testDocumentResponseResultCodeIsOkAndNoDocumentReturnedAlsoDocError() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage("009_0005 RCI Error – Unable to Retrieve Documents");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(0);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.NO_CONTENT, documentResponse.getStatusCode());
  }

  @Test
  public void testDocumentResponseResultCodeIsOkAndAlsoDocErrorDocumentNegativeValue() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult.setResultMessage("009_0005 RCI Error – Unable to Retrieve Documents");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    documentResult.setNumDocumentsReturned(-1);
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }


  @Test
  public void testDocumentResponseHandlerResponseBadRequestError() {
    String contextId = "contextID";
    String userId = "Test User Id";
    DocumentResult documentResult = new DocumentResult();
    documentResult.setResultCode("0001");
    documentResult
        .setResultMessage("004_0005 Invalid Query condition or Condition is empty or null");
    documentResult.setDocumentsMetaData(new ArrayList<>());
    ResponseEntity<DocumentResult> responseEntity =
        new ResponseEntity<>(documentResult, HttpStatus.OK);
    ResponseEntity<DMSDocumentResponse> documentResponse =
        documentResultResponseHandler.handleResponse(responseEntity, contextId, userId);
    assertTrue(documentResponse instanceof ResponseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, documentResponse.getStatusCode());
    assertEquals("0001", documentResponse.getBody().getResultCode());
  }

}
