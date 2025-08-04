package dec.ny.gov.etrack.dcs.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.service.DcsService;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceUtil;

@RunWith(SpringJUnit4ClassRunner.class)
public class DcsControllerTest {

  @Mock
  private DcsService service;

  @Mock
  private SupportDocumentService supportDocumentService;

  @Mock
  private DCSServiceUtil dcsServiceUtil;
  
  @InjectMocks
  private DcsController dcsController;

  @Test(expected = ValidationException.class)
  public void testInvalidDocumentClassInDeleteDocument() {
    doReturn(false).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    dcsController.deleteDocument("1213", "userid", "token", "INVALID");
  }

  @Test
  public void testDeleteDocumentSuccessfully() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("The document was sucessfully deleted.");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(responseEntity).when(service).deleteDocument(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString());
    dcsController.deleteDocument("1213", "userid", "token", "APPLICATION");
//    assertTrue(
//        dcsController.deleteDocument("1213", "userid", "token", "APPLICATION") instanceof ResponseEntity);
  }

  @Test(expected = ValidationException.class)
  public void testInvalidDocumentClassInRetrieveFileContent() {
    doReturn(false).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    dcsController.retrieveFileContent("1213", "userid","token", null, "File Name", "INVALID");
  }

  @Test
  public void testRetrieveFileContentSuccessfully() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("Success");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(responseEntity).when(service).retrieveFileContent(Mockito.anyString(),Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dcsController.retrieveFileContent("1213", "userid", "token", 0L, "File Name",
        "APPLICATION");
//    assertTrue(dcsController.retrieveFileContent("1213", "userid", "token", 0L, "File Name",
//        "APPLICATION") instanceof ResponseEntity);
  }

  @Test
  public void testUpdateDocumentMetadataSuccessfully() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("Success");
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(false).when(dcsServiceUtil).isNotValidFoilStatus(Mockito.any(), Mockito.anyString());
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(responseEntity).when(service).updateDocumentMetadata(Mockito.anyString(),
        Mockito.anyString(), Mockito.any(), Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setMetaDataProperties(new HashMap<>());
    assertTrue(dcsController.updateDocumentMetadata("1213", "userid", "token", "APPLICATION",
        ingestionRequest, 1L) instanceof ResponseEntity);
  }
  
  @Test(expected = ValidationException.class)
  public void testInvalidFilesInUploadDocument() {
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(false).when(dcsServiceUtil).isContainsValidFiles(Mockito.any(), Mockito.anyString());
    dcsController.uploadDocument("1213", "userid","token", "INVALID", new IngestionRequest(), new MockMultipartFile[] {});
  }
  
  @Test(expected = ValidationException.class)
  public void testInvalidFoilStatusDatasInUploadDocument() {
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(true).when(dcsServiceUtil).isContainsValidFiles(Mockito.any(), Mockito.anyString());
    doReturn(true).when(dcsServiceUtil).isNotValidFoilStatus(Mockito.any(), Mockito.anyString());    
    dcsController.uploadDocument("1213", "userid", "token", "PERMIT",new IngestionRequest(), new MockMultipartFile[] {});
  }
  
  @Test
  public void testUploadDocumentSuccessfully() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("Success");
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(true).when(dcsServiceUtil).isContainsValidFiles(Mockito.any(), Mockito.anyString());
    doReturn(false).when(dcsServiceUtil).isNotValidFoilStatus(Mockito.any(), Mockito.anyString());
    
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(responseEntity).when(service).uploadDocument(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString());
    MockMultipartFile multipartFile = new MockMultipartFile("fileName1", "FileContent".getBytes());
    MockMultipartFile[] files = {multipartFile};
    assertTrue(dcsController.uploadDocument("userid", "token", "APPLICATION", "1213", new IngestionRequest(),
        files) instanceof ResponseEntity);
  }


  @Test(expected = ValidationException.class)
  public void testInvalidDocumentClassInReplaceWithNewDocument() {
    doReturn(false).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    dcsController.replaceWithNewDocument("userid", "INVALID", "1213","token", new IngestionRequest(), null);
  }


  @Test(expected = ValidationException.class)
  public void testInvalidFilesInReplaceWithNewDocument() {
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(false).when(dcsServiceUtil).isContainsValidFiles(Mockito.any(), Mockito.anyString());
    dcsController.replaceWithNewDocument("userid", "VALID", "1213","token", new IngestionRequest(), null);
  }
  
  @Test
  public void testReplaceWithNewDocumentSuccessfully() {
    doReturn(true).when(dcsServiceUtil).isDocumentClassValid(Mockito.anyString());
    doReturn(true).when(dcsServiceUtil).isContainsValidFiles(Mockito.any(), Mockito.anyString());
    IngestionResponse response = new IngestionResponse();
    response.setResultCode("0000");
    response.setResultMessage("Success");
    ResponseEntity<IngestionResponse> responseEntity =
        new ResponseEntity<>(response, HttpStatus.OK);
    doReturn(responseEntity).when(service).replaceWithNewDocument(Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(),
        Mockito.anyString(), Mockito.anyString());
    MockMultipartFile multipartFile = new MockMultipartFile("fileName1", "FileContent".getBytes());
    MockMultipartFile[] files = {multipartFile};
    assertTrue(dcsController.replaceWithNewDocument("userid","APPLICATION", "1213","token",
        new IngestionRequest(), files) instanceof ResponseEntity);
  }  
}
