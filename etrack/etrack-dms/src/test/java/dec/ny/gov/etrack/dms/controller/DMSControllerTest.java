package dec.ny.gov.etrack.dms.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.processor.DocumentManagementProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
public class DMSControllerTest {

  @InjectMocks
  private DMSController dmsController;

  @Mock
  private DocumentManagementProcessor documentManagementProcessor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void testUploadDocumentReturnsOk() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    MockMultipartFile[] mockMultipartFiles = null;
    IngestionResponse ingestionResponse = new IngestionResponse();
    ingestionResponse.setDocumentId("12321321");
    ResponseEntity<IngestionResponse> ingestionResponseEntity =
        new ResponseEntity<>(ingestionResponse, HttpStatus.OK);
    Mockito.doReturn(ingestionResponseEntity).when(documentManagementProcessor)
        .uploadDocument(Mockito.any(), Mockito.any());
    assertEquals(ingestionResponseEntity, dmsController.uploadDocument("TEST_USER_ID",
        "TEST_CLIENT_ID", "context Id", ingestionRequest, mockMultipartFiles));
  }

  @Test
  public void testUploadDocumentMetaData() {
    IngestionRequest updateMetadataRequest = new IngestionRequest();
    IngestionResponse updatedMetadataResponse = new IngestionResponse();
    updatedMetadataResponse.setResultCode("0000");
    ResponseEntity<IngestionResponse> ingestionResponseEntity =
        new ResponseEntity<>(updatedMetadataResponse, HttpStatus.OK);
    Mockito.doReturn(ingestionResponseEntity).when(documentManagementProcessor)
        .updateMetadataDocument(Mockito.any());
    assertNotNull(dmsController.updateDocumentMetaData("TEST_USER_ID", "TEST_CLIENT_ID", "contextId",
        "8028D379-0000-C114-8555-51D3F0FBEEB2", updateMetadataRequest));
  }

  @Test
  public void testDeleteDocumentEndPointReturnsOk() {
    Response response = new Response();
    response.setResultCode("0000");
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    Mockito.doReturn(responseEntity).when(documentManagementProcessor)
        .deleteDocument(Mockito.any());
    assertEquals(responseEntity,
        dmsController.deleteDocument("TestUserId", "TEST_CLIENT_ID_P8", "contextId", "121-1231-32432-231"));
  }

  @Test
  public void testRetrieveDocumentReturnsOK() {
    ResponseEntity<DMSDocumentResponse> responseEntity =
        new ResponseEntity<DMSDocumentResponse>(new DMSDocumentResponse(), HttpStatus.OK);
    doReturn(responseEntity).when(documentManagementProcessor)
        .retrieveDocumentsByGuid(Mockito.any());
    assertEquals(responseEntity, dmsController.retrieveDocumentDetailsByGuid("TestUserID",
        "TEST_CLIENT_ID", "context Id","{2342-1213-2323}"));
  }

  @Test
  public void testRetrieveDocumentContentReturnsOk() {
    ResponseEntity<DMSDocumentResponse> responseEntity =
        new ResponseEntity<DMSDocumentResponse>(new DMSDocumentResponse(), HttpStatus.OK);
    doReturn(responseEntity).when(documentManagementProcessor)
        .retrieveDocumentContent(Mockito.any(), Mockito.any());
    assertEquals(responseEntity, dmsController.retrieveFileContent("TestUserID", "TEST_CLIENT_ID", "ContextId",
        "{2342-1213-2323}", "testFileName.txt"));
  }

}
