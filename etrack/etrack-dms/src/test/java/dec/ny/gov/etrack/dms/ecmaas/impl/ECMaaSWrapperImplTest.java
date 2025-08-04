package dec.ny.gov.etrack.dms.ecmaas.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import dec.ny.gov.etrack.dms.exception.DMSException;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.DocumentResult;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.response.ECMaaSMetaDataResponse;
import dec.ny.gov.etrack.dms.response.ECMaaSResponse;
import dec.ny.gov.etrack.dms.util.DeleteDocumentResponseHandler;
import dec.ny.gov.etrack.dms.util.DocumentResultResponseHandler;
import dec.ny.gov.etrack.dms.util.IngestionDocumentResponseHandler;

@RunWith(SpringJUnit4ClassRunner.class)
public class ECMaaSWrapperImplTest {

  @InjectMocks
  private ECMaaSWrapperImpl eCMaaSWrapperImpl;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ResponseEntity<Response> responseEntity;
  @Mock
  private ResponseEntity<DMSDocumentResponse> searchResponseEntity;
  @Mock
  private DeleteDocumentResponseHandler deleteDocumentErrorResponseHandler;
  @Mock
  private DocumentResultResponseHandler documentResultResponseHandler;
  @Mock
  private IngestionDocumentResponseHandler ingDocumentResponseHandler;
  @Mock
  private Logger logger;

  private DMSRequest dmsRequest = null;

  @Before
  public void setUp() {
    dmsRequest = new DMSRequest();
    dmsRequest.setClientId("TEST_CLIENT_ID");
    dmsRequest.setUserId("TEST_USER_ID");
    dmsRequest.setGuid("121-1231-32432-231");
    dmsRequest.setDocumentId("121-1231-32432-231");
    dmsRequest.setContextId("testContextId");
  }

  @Test
  public void testDeleteDocumentInvocationSuccess() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("000_0000 - Document Deleted Successfully");
    responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doReturn(responseEntity).when(deleteDocumentErrorResponseHandler).handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    assertEquals(responseEntity, eCMaaSWrapperImpl.deleteDocument(dmsRequest));
  }

  @Test
  public void testDeleteDocumentInvocationSuccessDebugEnabled() {
    Response response = new Response();
    response.setResultCode("0000");
    response.setResultMessage("000_0000 - Document Deleted Successfully");
    responseEntity = new ResponseEntity<Response>(response, HttpStatus.OK);
    doReturn(true).when(logger).isDebugEnabled();
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doReturn(responseEntity).when(deleteDocumentErrorResponseHandler).handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    assertEquals(responseEntity, eCMaaSWrapperImpl.deleteDocument(dmsRequest));
  }

  @Test(expected = DMSException.class)
  public void testDeleteDocumentThrowsExceptionWhileInvokingRestTemplate() {
    Response response = new Response();
    response.setResultCode("0001");
    response.setResultMessage("003_0003 - Initialization Error - User ID is Required");
    doThrow(DMSException.class).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    eCMaaSWrapperImpl.deleteDocument(dmsRequest);
  }

  @Test(expected = DMSException.class)
  public void testDeleteDocumentThrowsExceptionWhileInvokingResponseHandler() {
    Response response = new Response();
    response.setResultCode("0001");
    response.setResultMessage("003_0003 - Initialization Error - User ID is Required");
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doThrow(new RuntimeException()).when(deleteDocumentErrorResponseHandler)
        .handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    eCMaaSWrapperImpl.deleteDocument(dmsRequest);
  }

  @Test
  public void testDocumentSearchByGuidReturnsSuccessResponse() {
    DocumentResult result = new DocumentResult();
    result.setResultCode("0000");
    result.setResultMessage("000_0000 - Query executed Successfully");
    DMSDocumentResponse documentResponse = new DMSDocumentResponse();
    documentResponse.setResultCode(result.getResultCode());
    documentResponse.setResultMessage(result.getResultMessage());
    ResponseEntity<DocumentResult> documentResulEntity =
        new ResponseEntity<DocumentResult>(result, HttpStatus.OK);
    searchResponseEntity = new ResponseEntity<DMSDocumentResponse>(documentResponse, HttpStatus.OK);
    doReturn(documentResulEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doReturn(searchResponseEntity).when(documentResultResponseHandler)
        .handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    assertEquals(searchResponseEntity, eCMaaSWrapperImpl.retrieveDocumentByGuid(dmsRequest));
  }

  @Test(expected = DMSException.class)
  public void testDocumentSearchByGuidThrowsExceptionWhenRestTemplateInvokes() {
    Response response = new Response();
    response.setResultCode("0001");
    response.setResultMessage("003_0003 - Initialization Error - User ID is Required");
    doThrow(new RuntimeException()).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doReturn(searchResponseEntity).when(documentResultResponseHandler)
        .handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    eCMaaSWrapperImpl.retrieveDocumentByGuid(dmsRequest);
  }

  @Test(expected = DMSException.class)
  public void testDocumentSearchByGuidThrowsExceptionWhenResponseHandlerInvokes() {
    DocumentResult result = new DocumentResult();
    result.setResultCode("0000");
    result.setResultMessage("000_0000 - Query executed Successfully");
    DMSDocumentResponse documentResponse = new DMSDocumentResponse();
    documentResponse.setResultCode(result.getResultCode());
    documentResponse.setResultMessage(result.getResultMessage());
    ResponseEntity<DocumentResult> documentResulEntity =
        new ResponseEntity<DocumentResult>(result, HttpStatus.OK);
    searchResponseEntity = new ResponseEntity<DMSDocumentResponse>(documentResponse, HttpStatus.OK);
    doReturn(documentResulEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<Response>>any());
    doThrow(new RuntimeException()).when(documentResultResponseHandler)
        .handleResponse(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    eCMaaSWrapperImpl.retrieveDocumentByGuid(dmsRequest);
  }

  @Test
  public void testSuccessfulRetrieveDocumentContent() {
    byte[] bytes = new byte[] {'a', 'b'};
    ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.<HttpMethod>any(), Mockito.<HttpEntity<?>>any(), Mockito.<Class<byte[]>>any());
    assertEquals(responseEntity, eCMaaSWrapperImpl.retrieveDocumentContent(dmsRequest));
  }

  @Test(expected = DMSException.class)
  public void testRetrieveDocumentContentThrowsException() {
    byte[] bytes = new byte[] {'a', 'b'};
    ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
    doThrow(new RuntimeException()).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.<HttpMethod>any(), Mockito.<HttpEntity<?>>any(), Mockito.<Class<byte[]>>any());
    eCMaaSWrapperImpl.retrieveDocumentContent(dmsRequest);
  }

  @Test
  public void testUploadDocumentMetaDataPropertyIsNull() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);
    MockMultipartFile[] mockMultipartFiles = null;
    ingeResponseEntity = eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, ingestionRequest, false);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testUploadDocumentWhenMetaDataPropertyIsEmpty() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    ingestionRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);
    MockMultipartFile[] mockMultipartFiles = null;
    ingeResponseEntity = eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, ingestionRequest, false);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testUploadDocumentWhenNoFileInput() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("Id", "{A013397A-0000-CA11-972D-4853EBA522BE}");
    ingestionRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);
    MockMultipartFile[] mockMultipartFiles = null;
    ingeResponseEntity = eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, ingestionRequest, false);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testUploadDocumentWhenNoFilePassed() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("Id", "{A013397A-0000-CA11-972D-4853EBA522BE}");
    ingestionRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);
    MockMultipartFile[] mockMultipartFiles = {};
    ingeResponseEntity = eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, ingestionRequest, false);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test
  public void testUploadDocumentSuccessfully() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("Id", "{A013397A-0000-CA11-972D-4853EBA522BE}");
    ingestionRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.CREATED);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);

    MockMultipartFile multiPartFile1 = new MockMultipartFile("testfile1", "testfile1", "text/plain",
        "Testing Multipart file 1".getBytes());
    MockMultipartFile multiPartFile2 = new MockMultipartFile("testfile2", "testfile1", "text/plain",
        "Testing Multipart file 2".getBytes());

    MockMultipartFile[] mockMultipartFiles = {multiPartFile1, multiPartFile2};
    ingeResponseEntity = eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, ingestionRequest, false);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test(expected = DMSException.class)
  public void testUploadDocumentThrowsException() {
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("Id", "{A013397A-0000-CA11-972D-4853EBA522BE}");
    ingestionRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSResponse>>any());
    doThrow(DMSException.class).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        ingestionRequest, false);

    MockMultipartFile multiPartFile1 = new MockMultipartFile("testfile1", "testfile1", "text/plain",
        "Testing Multipart file 1".getBytes());
    MockMultipartFile multiPartFile2 = new MockMultipartFile("testfile2", "testfile1", "text/plain",
        "Testing Multipart file 2".getBytes());

    MockMultipartFile[] mockMultipartFiles = {multiPartFile1, multiPartFile2};
    eCMaaSWrapperImpl.uploadDocument(ingestionRequest, mockMultipartFiles);
  }

  @Test
  public void testUpdateMetaDataSuccessfully() {
    IngestionRequest updateMetaDataRequest = new IngestionRequest();
    updateMetaDataRequest.setClientId("TEST_CLIENT_ID");
    updateMetaDataRequest.setUserId("TEST_USER_ID");
    updateMetaDataRequest.setAttachmentFilesCount(1);
    updateMetaDataRequest.setGuid("{A013397A-0000-CA11-972D-4853EBA522BE}");
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("eTrackDocumentID", "23424234");
    updateMetaDataRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSMetaDataResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSMetaDataResponse>>any());
    ResponseEntity<IngestionResponse> ingeResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(ingeResponseEntity).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        updateMetaDataRequest, true);
    ingeResponseEntity = eCMaaSWrapperImpl.updatedMetaData(updateMetaDataRequest);
    verify(ingDocumentResponseHandler).handleResponse(eCMResponseEntity, updateMetaDataRequest,
        true);
    verify(restTemplate).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
        Mockito.any(HttpEntity.class), Mockito.<Class<ECMaaSMetaDataResponse>>any());
    assertTrue(ingeResponseEntity instanceof ResponseEntity);
  }

  @Test(expected = DMSException.class)
  public void testUpdateMetaDataPropertyThrowsException() {
    IngestionRequest updateMetaDataRequest = new IngestionRequest();
    updateMetaDataRequest.setClientId("TEST_CLIENT_ID");
    updateMetaDataRequest.setUserId("TEST_USER_ID");
    updateMetaDataRequest.setAttachmentFilesCount(1);
    updateMetaDataRequest.setGuid("{A013397A-0000-CA11-972D-4853EBA522BE}");
    Map<String, String> metatDataPropertyMap = new HashMap<>();
    metatDataPropertyMap.put("eTrackDocumentID", "23424234");
    updateMetaDataRequest.setMetaDataProperties(metatDataPropertyMap);
    ResponseEntity<ECMaaSMetaDataResponse> eCMResponseEntity = new ResponseEntity<>(HttpStatus.OK);
    doReturn(eCMResponseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<ECMaaSMetaDataResponse>>any());
    doThrow(DMSException.class).when(ingDocumentResponseHandler).handleResponse(eCMResponseEntity,
        updateMetaDataRequest, true);
    eCMaaSWrapperImpl.updatedMetaData(updateMetaDataRequest);
  }
}
