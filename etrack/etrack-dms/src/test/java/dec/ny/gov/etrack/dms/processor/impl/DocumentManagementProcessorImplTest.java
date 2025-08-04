package dec.ny.gov.etrack.dms.processor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.ecmaas.ECMaaSWrapper;
import dec.ny.gov.etrack.dms.model.DMSDocumentMetaData;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.request.AttachmentMetaData;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class DocumentManagementProcessorImplTest {

  @InjectMocks
  private DocumentManagementProcessorImpl documentManagementProcessorImpl;

  @Mock
  private ECMaaSWrapper eCMaaSWrapper;

  @Test
  public void testUploadDocumentServiceReturnsResponseOk() {
    ResponseEntity<IngestionResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    IngestionRequest ingestionRequest = new IngestionRequest();
    MockMultipartFile[] multipartFiles = null;
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    doReturn(responseEntity).when(eCMaaSWrapper).uploadDocument(Mockito.any(), Mockito.any());
    ResponseEntity<IngestionResponse> actualResponseEntity =
        documentManagementProcessorImpl.uploadDocument(ingestionRequest, multipartFiles);
    assertNotNull(actualResponseEntity);
    assertTrue(actualResponseEntity instanceof ResponseEntity);

  }

  @Test
  public void testUploadDocumentServiceReturnsNoResponse() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    MockMultipartFile[] multipartFiles = null;
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    doReturn(null).when(eCMaaSWrapper).uploadDocument(Mockito.any(), Mockito.any());
    ResponseEntity<IngestionResponse> actualResponseEntity =
        documentManagementProcessorImpl.uploadDocument(ingestionRequest, multipartFiles);
    assertNull(actualResponseEntity);
  }

  @Test
  public void testUpdateMetadataServiceReturnsResponseOk() {
    ResponseEntity<IngestionResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    IngestionRequest updateMetadataRequest = new IngestionRequest();
    updateMetadataRequest.setClientId("TEST_CLIENT_ID");
    doReturn(responseEntity).when(eCMaaSWrapper).updatedMetaData(Mockito.any());
    ResponseEntity<IngestionResponse> actualResponseEntity =
        documentManagementProcessorImpl.updateMetadataDocument(updateMetadataRequest);
    assertNotNull(actualResponseEntity);
    assertTrue(actualResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testUpdateMetadataServiceReturnsNoResponseBody() {
    IngestionRequest updateMetaDataRequest = new IngestionRequest();
    updateMetaDataRequest.setClientId("TEST_CLIENT_ID");
    doReturn(null).when(eCMaaSWrapper).uploadDocument(Mockito.any(), Mockito.any());
    ResponseEntity<IngestionResponse> actualResponseEntity =
        documentManagementProcessorImpl.updateMetadataDocument(updateMetaDataRequest);
    assertNull(actualResponseEntity);
  }

  @Test
  public void testDeleteDocumentService() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    DMSRequest dmsRequest = new DMSRequest();
    dmsRequest.setClientId("TEST_CLIENT_ID");
    doReturn(responseEntity).when(eCMaaSWrapper).deleteDocument(Mockito.any());
    ResponseEntity<Response> actualResponseEntity =
        documentManagementProcessorImpl.deleteDocument(dmsRequest);
    assertNotNull(actualResponseEntity);
    assertTrue(actualResponseEntity instanceof ResponseEntity);
  }


  @Test
  public void testDeleteDocumentServiceReturnsNull() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(null).when(eCMaaSWrapper).deleteDocument(Mockito.any());
    responseEntity = documentManagementProcessorImpl.deleteDocument(new DMSRequest());
    assertNull(responseEntity);
  }



  @Test
  public void testRetrieveDocumentByGuid() {
    ResponseEntity<DMSDocumentResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    responseEntity = documentManagementProcessorImpl.retrieveDocumentsByGuid(new DMSRequest());
    assertNotNull(responseEntity);
    assertTrue(responseEntity instanceof ResponseEntity);

  }

  @Test
  public void testSearchReturnsStatusCodeAsNotOKInRetrieveDocumentContent() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(HttpStatus.BAD_REQUEST);
    DMSRequest searchRequest = new DMSRequest();
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    ResponseEntity<byte[]> response =
        documentManagementProcessorImpl.retrieveDocumentContent(searchRequest, "test.txt");
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }


  @Test
  public void testSearchReturnsReturnsEmptyResponseBodyInRetrieveDocumentContent() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(HttpStatus.OK);
    DMSRequest searchRequest = new DMSRequest();
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    assertEquals(HttpStatus.NO_CONTENT, documentManagementProcessorImpl
        .retrieveDocumentContent(searchRequest, "test.txt").getStatusCode());
  }

  @Test
  public void testSearchReturnsNoDocumentMetaDataInRetrieveDocumentContent() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(HttpStatus.OK);
    DMSRequest retrieveContentRequest = new DMSRequest();
    retrieveContentRequest.setUserId("TESTUserID");
    retrieveContentRequest.setClientId("TEST_CLIENT_ID");
    retrieveContentRequest.setGuid("1231-1231-123123-1231");
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    List<DMSDocumentMetaData> documentsMetaDatas = new ArrayList<DMSDocumentMetaData>();
    DMSDocumentResponse response = new DMSDocumentResponse();
    response.setDocumentsMetaData(documentsMetaDatas);
    ResponseEntity<DMSDocumentResponse> documentResponseEntity =
        new ResponseEntity<DMSDocumentResponse>(response, HttpStatus.OK);
    doReturn(documentResponseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    assertEquals(HttpStatus.NO_CONTENT, documentManagementProcessorImpl
        .retrieveDocumentContent(retrieveContentRequest, "test.txt").getStatusCode());
  }

  @Test
  public void testSearchReturnsNoAttachmentMetaDataInRetrieveDocumentContent() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(HttpStatus.OK);
    DMSRequest retrieveContentRequest = new DMSRequest();
    retrieveContentRequest.setUserId("TESTUserID");
    retrieveContentRequest.setClientId("TEST_CLIENT_ID");
    retrieveContentRequest.setGuid("1231-1231-123123-1231");
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    List<DMSDocumentMetaData> documentsMetaDatas = new ArrayList<DMSDocumentMetaData>();
    DMSDocumentMetaData documentsMetaData = new DMSDocumentMetaData();
    List<AttachmentMetaData> attachmentMetaDatas = new ArrayList<>();
    documentsMetaData.setAttachmentMetaDatas(attachmentMetaDatas);
    documentsMetaDatas.add(documentsMetaData);
    DMSDocumentResponse response = new DMSDocumentResponse();
    response.setDocumentsMetaData(documentsMetaDatas);
    ResponseEntity<DMSDocumentResponse> documentResponseEntity =
        new ResponseEntity<DMSDocumentResponse>(response, HttpStatus.OK);
    doReturn(documentResponseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    assertEquals(HttpStatus.NO_CONTENT, documentManagementProcessorImpl
        .retrieveDocumentContent(retrieveContentRequest, "test.txt").getStatusCode());
  }

  @Test
  public void testFileIsNotAvailableInSearchDocumentResponseInRetrieveDocumentContent() {
    ResponseEntity<Response> responseEntity = new ResponseEntity<Response>(HttpStatus.OK);
    DMSRequest retrieveContentRequest = new DMSRequest();
    retrieveContentRequest.setUserId("TESTUserID");
    retrieveContentRequest.setClientId("TEST_CLIENT_ID");
    retrieveContentRequest.setGuid("1231-1231-123123-1231");
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    List<DMSDocumentMetaData> documentsMetaDatas = new ArrayList<DMSDocumentMetaData>();
    DMSDocumentMetaData documentsMetaData = new DMSDocumentMetaData();
    List<AttachmentMetaData> attachmentMetaDatas = new ArrayList<>();
    AttachmentMetaData attachmentMetaData = new AttachmentMetaData();
    attachmentMetaData.setRetrievalName("searchResults.txt");
    attachmentMetaDatas.add(attachmentMetaData);
    documentsMetaData.setAttachmentMetaDatas(attachmentMetaDatas);
    documentsMetaDatas.add(documentsMetaData);
    DMSDocumentResponse response = new DMSDocumentResponse();
    response.setDocumentsMetaData(documentsMetaDatas);
    ResponseEntity<DMSDocumentResponse> documentResponseEntity =
        new ResponseEntity<DMSDocumentResponse>(response, HttpStatus.OK);
    doReturn(documentResponseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    assertEquals(HttpStatus.NO_CONTENT, documentManagementProcessorImpl
        .retrieveDocumentContent(retrieveContentRequest, "test.txt").getStatusCode());
  }

  @Test
  public void testRetrieveDocumentContentSuccessfully() {
    byte[] bytes = new byte[] {'a', 'b'};
    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, HttpStatus.OK);
    DMSRequest retrieveContentRequest = new DMSRequest();
    ResponseEntity<DMSDocumentResponse> documentResponseEntity = null;
    List<DMSDocumentMetaData> documentsMetaDatas = new ArrayList<DMSDocumentMetaData>();
    DMSDocumentMetaData documentsMetaData = new DMSDocumentMetaData();
    List<AttachmentMetaData> attachmentMetaDatas = new ArrayList<>();
    AttachmentMetaData attachmentMetaData = new AttachmentMetaData();
    attachmentMetaData.setElementSequenceNumber(0);
    attachmentMetaData.setRetrievalName("test.txt");
    attachmentMetaDatas.add(attachmentMetaData);
    documentsMetaData.setAttachmentMetaDatas(attachmentMetaDatas);
    documentsMetaDatas.add(documentsMetaData);
    DMSDocumentResponse response = new DMSDocumentResponse();
    response.setDocumentsMetaData(documentsMetaDatas);
    documentResponseEntity = new ResponseEntity<DMSDocumentResponse>(response, HttpStatus.OK);
    doReturn(documentResponseEntity).when(eCMaaSWrapper).retrieveDocumentByGuid(Mockito.any());
    doReturn(responseEntity).when(eCMaaSWrapper).retrieveDocumentContent(Mockito.any());
    responseEntity =
        documentManagementProcessorImpl.retrieveDocumentContent(retrieveContentRequest, "test.txt");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

}
