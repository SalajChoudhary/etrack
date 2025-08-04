package dec.ny.gov.etrack.dms.controller;

import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.processor.DocumentManagementProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
public class DisposedDocumentUploadControllerTest {

  @Mock
  private DocumentManagementProcessor documentManagementProcessor;

  @InjectMocks
  private DisposedDocumentUploadController disposedDocumentUploadController;
  
  @Test
  public void testUploadDocumentReturnsUnauthorizedWhenInvalidUserIsPassed() {
    ResponseEntity<IngestionResponse> response = disposedDocumentUploadController.uploadDocument(
        "system", "clientId", "contextId", null, null);
    Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  public void testUploadDocumentReturnsSuccessResponse() {
    when(documentManagementProcessor.uploadDocument(
        Mockito.any(IngestionRequest.class), Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    ResponseEntity<IngestionResponse> response = disposedDocumentUploadController.uploadDocument(
        "system-authorized", "clientId", "contextId", new IngestionRequest(), null);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
