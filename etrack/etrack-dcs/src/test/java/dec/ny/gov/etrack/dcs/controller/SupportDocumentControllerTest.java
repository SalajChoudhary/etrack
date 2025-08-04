package dec.ny.gov.etrack.dcs.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocumentFileView;
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
public class SupportDocumentControllerTest {

  @InjectMocks
  private SupportDocumentController supportDocumentController;
  
  @Mock
  private SupportDocumentService supportDocumentService;

  @Test
  public void testUploadDocumentSuccessfully() {
    when(supportDocumentService.uploadSupportDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IngestionResponse());
    IngestionResponse ingestionResponse = supportDocumentController.uploadSupportDocument("userId", "authorization", 1, 123L, null, null);
    assertNotNull(ingestionResponse);
  }
  
  @Test
  public void testUpdateDocumentMetaDataSuccessfully() {
    when(supportDocumentService.updateSupportDocumentMetadata(
        Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<Response>(new Response(), HttpStatus.OK));
    ResponseEntity<Response> responseEntity = supportDocumentController.updateDocumentMetadata(12334L, "userId", "authorization",  null);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testUploadAdditionalDocumentSuccessfully() {
    when(supportDocumentService.uploadAdditionalSupportDocument(Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IngestionResponse());
    IngestionResponse responseEntity = supportDocumentController.uploadAdditionalSupportDocument(
        "userId", "authorization", 1232L, null,  null);
    assertNotNull(responseEntity);
  }

  @Test
  public void testReferenceDocumentReferenceSuccessfully() {
    doNothing().when(supportDocumentService).createReferenceForExistingDocument(Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any());
    supportDocumentController.referenceAlreadyUploadedDocument(
        "userId", 1232L,  null);
  }

  @Test
  public void testRetrieveAllDisplayNamesReturnsSuccessfully() {
    List<DocumentName> allDocumentNames = new ArrayList<>();
    when(supportDocumentService.retrieveAllDisplayNames(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allDocumentNames);
    List<DocumentName> responseEntity = supportDocumentController.retrieveDisplayNames(
        "userId", 1232L);
    assertNotNull(responseEntity);
    assertEquals(allDocumentNames, responseEntity);
  }

  @Test
  public void testGetSupportDocumentFilesReturnsSuccessfully() {
    List<DocumentFileView> allDocumentNames = new ArrayList<>();
    when(supportDocumentService.retrieveAllFilesAssociatedWithDocumentId(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allDocumentNames);
    List<DocumentFileView> responseEntity = supportDocumentController.getSupportDocumentFiles(
        "userId", 1232L, Arrays.asList(23424L));
    
    assertNotNull(responseEntity);
    assertEquals(allDocumentNames, responseEntity);
  }

  @Test
  public void testGetSupportDocumentDetailsReturnsSuccessfully() {
    List<DocumentNameView> allDocumentNames = new ArrayList<>();
    when(supportDocumentService.retrieveAllDocumentsAndFilesAssociatedWithDocumentId(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allDocumentNames);
    List<DocumentNameView> responseEntity = supportDocumentController.getSupportDocumentDetails(
        "userId", 1232L, 23424L);
    assertNotNull(responseEntity);
    assertEquals(allDocumentNames, responseEntity);
  }

  @Test
  public void testDeleteDocumentReturnsSuccessfully() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    when(supportDocumentService.deleteDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(deleteResponseEntity);
    ResponseEntity<Response> responseEntity = supportDocumentController.deleteDocument(
        Arrays.asList(23432L),  "userId", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }
  
  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoUserIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = supportDocumentController.deleteDocument(
        Arrays.asList(23432L),  "", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }
  
  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoDocumentIdsPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = supportDocumentController.deleteDocument(
        Arrays.asList(),  "userId", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoProjectIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = supportDocumentController.deleteDocument(
        Arrays.asList(12321L),  "userId", "authorization", null);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenInvalidProjectIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = supportDocumentController.deleteDocument(
        Arrays.asList(12321L),  "userId", "authorization", 0L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test
  public void testRetrieveFileContentReturnsSuccessfully() {
    ResponseEntity<byte[]> fileContentResponse = new ResponseEntity<>("filecontent".getBytes(), HttpStatus.OK);
    when(supportDocumentService.retrieveFileContent(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any())).thenReturn(fileContentResponse);
    ResponseEntity<byte[]> responseEntity = supportDocumentController.retrieveFileContent(
        "12321",  "userId", "authorization", 23424L, "fileName");
    assertNotNull(responseEntity);
    assertEquals(fileContentResponse.getStatusCode(), responseEntity.getStatusCode());
  }
}
