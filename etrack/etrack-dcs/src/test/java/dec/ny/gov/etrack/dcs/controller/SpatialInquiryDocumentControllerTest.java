package dec.ny.gov.etrack.dcs.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import dec.ny.gov.etrack.dcs.model.DocumentName;
import dec.ny.gov.etrack.dcs.model.DocumentNameView;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SpatialInqDocument;
import dec.ny.gov.etrack.dcs.service.SpatialInquiryDocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
public class SpatialInquiryDocumentControllerTest {
  
  @Mock
  private SpatialInquiryDocumentService spatialInquiryDocumentService;
  
  @InjectMocks
  private SpatialInquiryDocumentController spatialInquiryDocumentController;
  
  @Test
  public void testUploadDocumentSuccessfully() {
    when(spatialInquiryDocumentService.uploadSpatialInquiryDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(new IngestionResponse());
    IngestionResponse ingestionResponse = spatialInquiryDocumentController.uploadSupportDocument(
        "userId", "authorization", 212321L, null, null);
    assertNotNull(ingestionResponse);
  }

  @Test
  public void testUploadAdditionalDocumentSuccessfully() {
    when(spatialInquiryDocumentService.uploadAdditionalSpatialDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new IngestionResponse());
    IngestionResponse responseEntity = spatialInquiryDocumentController.uploadAdditionalSupportDocument(
        "userId", "authorization", 1232L, null,  null);
    assertNotNull(responseEntity);
  }

  @Test
  public void testReferenceDocumentReferenceSuccessfully() {
    Mockito.doNothing().when(spatialInquiryDocumentService).createReferenceForExistingDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any());
    spatialInquiryDocumentController.referenceAlreadyUploadedDocument(
        "userId", 1232L, null);
  }

  @Test
  public void testRetrieveAllDisplayNamesReturnsSuccessfully() {
    List<DocumentName> allDocumentNames = new ArrayList<>();
    when(spatialInquiryDocumentService.retrieveAllDisplayNames(
        Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allDocumentNames);
    List<DocumentName> responseEntity = spatialInquiryDocumentController.getAllDocumentDisplayNames(
        "userId", 1232L);
    assertNotNull(responseEntity);
    assertEquals(allDocumentNames, responseEntity);
  }

  @Test
  public void testGetSupportDocumentDetailsReturnsSuccessfully() {
    List<DocumentNameView> allDocumentNames = new ArrayList<>();
    when(spatialInquiryDocumentService.retrieveAllDocumentsAndFilesAssociatedWithDocumentId(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(allDocumentNames);
    List<DocumentNameView> responseEntity = spatialInquiryDocumentController.getSpatialInquiryDocumentDetails(
        "userId", 1232L, 23424L);
    assertNotNull(responseEntity);
    assertEquals(allDocumentNames, responseEntity);
  }

  @Test
  public void testDeleteDocumentReturnsSuccessfully() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    when(spatialInquiryDocumentService.deleteDocument(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(deleteResponseEntity);
    ResponseEntity<Response> responseEntity = spatialInquiryDocumentController.deleteDocument(
        Arrays.asList(23432L),  "userId", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }
  
  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoUserIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = spatialInquiryDocumentController.deleteDocument(
        Arrays.asList(23432L),  "", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }
  
  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoDocumentIdsPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = spatialInquiryDocumentController.deleteDocument(
        Arrays.asList(),  "userId", "authorization", 23424L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoProjectIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = spatialInquiryDocumentController.deleteDocument(
        Arrays.asList(12321L),  "userId", "authorization", null);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenInvalidProjectIdPassed() {
    ResponseEntity<Response> deleteResponseEntity = new ResponseEntity<>(new Response(), HttpStatus.OK);
    ResponseEntity<Response> responseEntity = spatialInquiryDocumentController.deleteDocument(
        Arrays.asList(12321L),  "userId", "authorization", 0L);
    assertNotNull(responseEntity);
    assertEquals(deleteResponseEntity.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test
  public void testRetrieveFileContentReturnsSuccessfully() {
    ResponseEntity<byte[]> fileContentResponse = new ResponseEntity<>("filecontent".getBytes(), HttpStatus.OK);
    when(spatialInquiryDocumentService.retrieveFileContent(
        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), 
        Mockito.any(), Mockito.any())).thenReturn(fileContentResponse);
    ResponseEntity<byte[]> responseEntity = spatialInquiryDocumentController.retrieveFileContent(
        "12321",  "userId", "authorization", 23424L, "fileName");
    assertNotNull(responseEntity);
    assertEquals(fileContentResponse.getStatusCode(), responseEntity.getStatusCode());
  }

  @Test
  public void testRetrieveRequiredSpatialInquiryDocumentListSuccessfully() {
    List<SpatialInqDocument> spatialInquiryReqdDoc = new ArrayList<>();
    when(spatialInquiryDocumentService.retrieveRequiredSpatialDocument(
        Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(spatialInquiryReqdDoc);
    List<SpatialInqDocument> responseEntity = spatialInquiryDocumentController.retrieveRequiredSpatialDocument(
        "userId", "authorization", 23424L);
    assertEquals(spatialInquiryReqdDoc, responseEntity);
  }
  
}
