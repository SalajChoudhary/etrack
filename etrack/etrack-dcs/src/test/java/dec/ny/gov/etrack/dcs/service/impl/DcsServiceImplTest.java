package dec.ny.gov.etrack.dcs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.JDBCException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dcs.controller.DcsController;
import dec.ny.gov.etrack.dcs.dao.ETrackDartFacilityDAO;
import dec.ny.gov.etrack.dcs.dao.SubmittedDocumentDAO;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.DocType;
import dec.ny.gov.etrack.dcs.model.EtrackDartFacility;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;
import dec.ny.gov.etrack.dcs.model.SubmittedDocument;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;
import dec.ny.gov.etrack.dcs.util.DCSServiceConstants;
import dec.ny.gov.etrack.dcs.util.DCSServiceUtil;

@RunWith(SpringJUnit4ClassRunner.class)
public class DcsServiceImplTest {

  @Mock
  private DcsController dcsController;

  @InjectMocks
  private DcsServiceImpl serviceImpl;

  @Mock
  private SubmittedDocumentDAO submittedDocumentDao;

  @Mock
  private ETrackDartFacilityDAO eTrackDartFacilityDAO;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private UriComponentsBuilder uriComponentsBuilder;

  @Mock
  private SubmittedDocument document;

  @Mock
  private DCSServiceUtil dcsServiceUtil;

  @Mock
  private SupportDocumentService supportDocumentService;

  @Test(expected = DocumentNotFoundException.class)
  public void testDeleteDocumentThrowsDocumentNotFoundExceptionWhenNoDocumentAvail() {
    doReturn(null).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  @Test(expected = DcsException.class)
  public void testDeleteDocumentThrowsDcsExceptionWhenTryToFindDocumentExist() {
    doThrow(RuntimeException.class).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhenNoECMaaSGUID() {
    SubmittedDocument document = new SubmittedDocument();
    document.setDocumentStateCode("A");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  @Test(expected = ValidationException.class)
  public void testDeleteDocumentThrowsValidationExceptionWhileUpdatingStatusCode() {
    SubmittedDocument document = new SubmittedDocument();
    document.setDocumentStateCode("A");
    document.setEcmaasGUID("123456");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    when(dcsServiceUtil.deleteDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(submittedDocumentDao.updateStateCode(Mockito.any())).thenThrow(ValidationException.class);
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  @Test(expected = DcsException.class)
  public void testDeleteDocumentThrowsDCSExceptionWhileUpdatingStatusCode() {
    SubmittedDocument document = new SubmittedDocument();
    document.setDocumentStateCode("A");
    document.setEcmaasGUID("123456");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    when(dcsServiceUtil.deleteDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(submittedDocumentDao.updateStateCode(Mockito.any())).thenThrow(RuntimeException.class);
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  public void testDeleteDocumentReturnsSuccessfully() {
    SubmittedDocument document = new SubmittedDocument();
    document.setDocumentStateCode("A");
    document.setEcmaasGUID("123456");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    when(dcsServiceUtil.deleteDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    when(submittedDocumentDao.updateStateCode(Mockito.any())).thenReturn(1);
    serviceImpl.deleteDocument("1234", "testUserId", "token", "testClientId", "contextId");
  }

  @Test(expected = DocumentNotFoundException.class)
  public void testRetrieveFileContentReturnsNullDocument() {
    doReturn(null).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.retrieveFileContent("1234", "testUserId", "token", "testClientId", "fileName",
        "contextId");
  }

  @Test(expected = DcsException.class)
  public void testRetrieveFileContentThrowsException() {
    doThrow(RuntimeException.class).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.retrieveFileContent("1234", "testUserId", "token", "testClientId", "fileName",
        "contextId");
  }

  @Test(expected = DcsException.class)
  public void testRetrieveFileContentDMSServiceThrowsException() {
    SubmittedDocument document = new SubmittedDocument();
    document.setEdbDistrictId(1234L);
    document.setDocumentId(9893L);
    document.setEcmaasGUID("12343");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    doThrow(RuntimeException.class).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(), Mockito.<Class<byte[]>>any());
    serviceImpl.retrieveFileContent("1234", "testUserId", "token", "testClientId", "fileName",
        "contextId");
  }

  @Test
  public void testRetrieveFileContentDMSServiceSuccess() {
    SubmittedDocument document = new SubmittedDocument();
    document.setEdbDistrictId(1234L);
    document.setDocumentId(9893L);
    document.setEcmaasGUID("12343");
    doReturn(document).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    ResponseEntity<byte[]> responseEntity =
        new ResponseEntity<byte[]>("abdcd".getBytes(), HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.<HttpEntity<?>>any(), Mockito.<Class<byte[]>>any());
    responseEntity = serviceImpl.retrieveFileContent("1234", "testUserId", "token", "testClientId",
        "fileName", "contextId");
    assertTrue(responseEntity instanceof ResponseEntity);
  }

  @Test(expected = ValidationException.class)
  public void testUpdateDocumentMetadataThrowsWhenInvalidDocumentClassPassed() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(false);
    serviceImpl.updateDocumentMetadata("1234", "testUserId", new IngestionRequest(), "ContextId",
        "token", "CORRESPONDENCE", 234324L);
  }

  @Test(expected = ValidationException.class)
  public void testUpdateDocumentMetadataThrowsWhenInvalidFoilStatusPassed() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(true);
    serviceImpl.updateDocumentMetadata("1234", "testUserId", new IngestionRequest(), "ContextId",
        "token", "CORRESPONDENCE", null);
  }

  @Test
  public void testUpdateDocumentMetadataThrowsWhenProjectIdPassedForHistoricalDocument() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    when(supportDocumentService.updateSupportDocumentMetadata(Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(new ResponseEntity<Response>(HttpStatus.OK));
    ResponseEntity<Response> response = serviceImpl.updateDocumentMetadata("1234", "testUserId",
        new IngestionRequest(), "ContextId", "token", "CORRESPONDENCE", null);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testUpdateDocumentMetadataFindDocumentCallsSupportDocumentWhenThereIsNoHistoricalDocument() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    when(submittedDocumentDao.findByDocumentIdAndDocumentStateCode(Mockito.any(), Mockito.any()))
        .thenReturn(null);
    when(supportDocumentService.updateSupportDocumentMetadata(Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(new ResponseEntity<Response>(HttpStatus.OK));
    ResponseEntity<Response> response = serviceImpl.updateDocumentMetadata("1234", "testUserId",
        new IngestionRequest(), "ContextId", "token", "CORRESPONDENCE", null);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocumentThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.anyString())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    doThrow(RuntimeException.class).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", new IngestionRequest(), "ContextId",
        "token", "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocumentNamesByDistrictIdAndOtherDocIdsThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenThrow(RuntimeException.class);
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocumentNamesByDistrictIdThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentNmByDistrictId(Mockito.any()))
        .thenThrow(RuntimeException.class);
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = ValidationException.class)
  public void testUpdateDocumentMetadataFindDocumentNamesReturnsNamesResultsThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    List<String> documentNames = new ArrayList<>();
    documentNames.add("DOCTITLE");
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(documentNames);
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocClassRetrieveThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    doThrow(RestClientException.class).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocClassRetrieveEmptyResponse() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    docTypeMap.put("123213", null);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");

    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocClassRetrieveNoDocmentType() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    docTypeMap.put("123213", null);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = DcsException.class)
  public void testUpdateDocumentMetadataFindDocClassRetrieveNoDocmentClassThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = ValidationException.class)
  public void testUpdateDocumentMetadataFindDocClassRetrieveDocmentClassNotMatchingExistingClass() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("SUPPORTING_DOCUMENTS");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "CORRESPONDENCE", null);
  }

  @Test(expected = ValidationException.class)
  public void testUpdateDocumentMetadataReceivesInvalidDocumentClassThrowsException() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("SUPPORTING_DOCUMENTS");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    serviceImpl.updateDocumentMetadata("1234", "testUserId", ingestionRequest, "ContextId", "token",
        "SUPPORTING_DOCUMENTS", null);
  }

  @Test
  public void testUpdateDocumentMetadataPreparesPermitDocumentClass() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    metadataProperties.put(DCSServiceConstants.DOC_CATEGORY, "1");
    metadataProperties.put(DCSServiceConstants.DOC_SUB_CATEGORY, "5");
    metadataProperties.get(DCSServiceConstants.FOIL_STATUS);
    metadataProperties.put(DCSServiceConstants.HISTORIC, "0");
    metadataProperties.put(DCSServiceConstants.OTHER_SUB_CAT_TEXT, "Other");
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "Title");
    metadataProperties.put(DCSServiceConstants.DOC_LAST_MODIFIER, "Modifier");
    metadataProperties.put(DCSServiceConstants.DOC_DESCRIPTION, "Desc");
    metadataProperties.put(DCSServiceConstants.TRACKED_APP_ID, "AppId");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("PERMIT");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    ResponseEntity<Response> responseEntity = serviceImpl.updateDocumentMetadata("1234",
        "testUserId", ingestionRequest, "ContextId", "token", "PERMIT", null);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testUpdateDocumentMetadataPreparesCorrespondenceDocumentClass() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    metadataProperties.put(DCSServiceConstants.DOC_CATEGORY, "1");
    metadataProperties.put(DCSServiceConstants.DOC_SUB_CATEGORY, "5");
    metadataProperties.get(DCSServiceConstants.FOIL_STATUS);
    metadataProperties.put(DCSServiceConstants.HISTORIC, "0");
    metadataProperties.put(DCSServiceConstants.OTHER_SUB_CAT_TEXT, "Other");
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "Title");
    metadataProperties.put(DCSServiceConstants.DOC_LAST_MODIFIER, "Modifier");
    metadataProperties.put(DCSServiceConstants.DOC_DESCRIPTION, "Desc");
    metadataProperties.put(DCSServiceConstants.TRACKED_APP_ID, "AppId");
    metadataProperties.put(DCSServiceConstants.ACCESS, "0");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("CORRESPONDENCE");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    ResponseEntity<Response> responseEntity = serviceImpl.updateDocumentMetadata("1234",
        "testUserId", ingestionRequest, "ContextId", "token", "CORRESPONDENCE", null);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }


  @Test
  public void testUpdateDocumentMetadataPreparesApplicationDocumentClass() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    metadataProperties.put(DCSServiceConstants.DOC_CATEGORY, "1");
    metadataProperties.put(DCSServiceConstants.DOC_SUB_CATEGORY, "5");
    metadataProperties.get(DCSServiceConstants.FOIL_STATUS);
    metadataProperties.put(DCSServiceConstants.HISTORIC, "0");
    metadataProperties.put(DCSServiceConstants.OTHER_SUB_CAT_TEXT, "Other");
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "Title");
    metadataProperties.put(DCSServiceConstants.DOC_LAST_MODIFIER, "Modifier");
    metadataProperties.put(DCSServiceConstants.DOC_DESCRIPTION, "Desc");
    metadataProperties.put(DCSServiceConstants.TRACKED_APP_ID, "AppId");
    metadataProperties.put(DCSServiceConstants.ACCESS, "0");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("APPLICATION");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    ResponseEntity<Response> responseEntity = serviceImpl.updateDocumentMetadata("1234",
        "testUserId", ingestionRequest, "ContextId", "token", "APPLICATION", null);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }


  @Test
  public void testUpdateDocumentMetadataPreparesSupportDocumentDocumentClass() {
    when(dcsServiceUtil.isDocumentClassValid(Mockito.any())).thenReturn(true);
    when(dcsServiceUtil.isNotValidFoilStatus(Mockito.any(), Mockito.any())).thenReturn(false);
    when(dcsServiceUtil.retrieveClientId(Mockito.any())).thenReturn("clientId");
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(12131L);
    submittedDocument.setEdbDistrictId(12131L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .findByDocumentIdAndDocumentStateCode(Mockito.anyLong(), Mockito.anyString());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "docTitle");
    metadataProperties.put(DCSServiceConstants.DOC_CATEGORY, "1");
    metadataProperties.put(DCSServiceConstants.DOC_SUB_CATEGORY, "5");
    metadataProperties.get(DCSServiceConstants.FOIL_STATUS);
    metadataProperties.put(DCSServiceConstants.HISTORIC, "0");
    metadataProperties.put(DCSServiceConstants.OTHER_SUB_CAT_TEXT, "Other");
    metadataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "Title");
    metadataProperties.put(DCSServiceConstants.DOC_LAST_MODIFIER, "Modifier");
    metadataProperties.put(DCSServiceConstants.DOC_DESCRIPTION, "Desc");
    metadataProperties.put(DCSServiceConstants.TRACKED_APP_ID, "AppId");
    metadataProperties.put(DCSServiceConstants.ACCESS, "0");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    when(
        submittedDocumentDao.findDocumentNmByDistrictIdForOtherDocIds(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(
        submittedDocumentDao.findSupportDocumentNameExistByDistrictId(Mockito.any(), Mockito.any()))
            .thenReturn(new ArrayList<>());
    when(submittedDocumentDao.findDocumentTypeIdByDocumentId(Mockito.any())).thenReturn(123213);
    Map<String, Map<String, Object>> responseClass = new HashMap<>();
    Map<String, Object> docTypeMap = new HashMap<>();
    DocType docType = new DocType();
    docType.setDocTypeId(23423);
    docType.setDocClassName("SUPPORTINGDOCUMENTS");
    docTypeMap.put("123213", docType);
    responseClass.put("en-US", docTypeMap);
    ResponseEntity<Map> response = new ResponseEntity<>(responseClass, HttpStatus.OK);
    ReflectionTestUtils.setField(serviceImpl, "configPath", "configPath");
    ReflectionTestUtils.setField(serviceImpl, "cachedDocTypesPath", "cachedDocTypesPath");
    doReturn(response).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<Map>>any());
    ResponseEntity<Response> responseEntity = serviceImpl.updateDocumentMetadata("1234",
        "testUserId", ingestionRequest, "ContextId", "token", "SUPPORTINGDOCUMENTS", null);
    Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test(expected = DcsException.class)
  public void testUploadDocumentFailedToRetrieveDARTFacility() {
    doThrow(JDBCException.class).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", new IngestionRequest(),
        new MockMultipartFile[] {}, "contextId");
  }


  @Test(expected = ValidationException.class)
  public void testUploadDocumentNoMetaDataProperties() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", new IngestionRequest(),
        new MockMultipartFile[] {}, "contextId");
  }


  @Test(expected = ValidationException.class)
  public void testUploadDocumentUploadFilesEmpty() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metaDataProperties = new HashMap<>();
    metaDataProperties.put(DCSServiceConstants.FOIL_STATUS, "REL");
    ingestionRequest.setMetaDataProperties(metaDataProperties);
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest, null,
        "contextId");
  }


  @Test(expected = DcsException.class)
  public void testUploadDocumentFindDocumentNamesThrowsException() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    doThrow(JDBCException.class).when(submittedDocumentDao)
        .findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metaDataProperties = new HashMap<>();
    metaDataProperties.put(DCSServiceConstants.FOIL_STATUS, "REL");
    ingestionRequest.setMetaDataProperties(metaDataProperties);
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {}, "contextId");
  }

  @Test(expected = ValidationException.class)
  public void testUploadDocumentFindDocumentNamesExist() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAME_EXIST");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metaDataProperties = new HashMap<>();
    metaDataProperties.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metaDataProperties.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME_EXIST");
    ingestionRequest.setMetaDataProperties(metaDataProperties);
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {}, "contextId");
  }

  @Test(expected = ValidationException.class)
  public void testUploadDocumentFileDatesAreEmpty() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {}, "contextId");
  }

  @Test(expected = ValidationException.class)
  public void testUploadDocumentStoreDocumentThrowsExceptionWhenNoFileDatesPassed() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    doThrow(JDBCException.class).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }


  @Test(expected = DcsException.class)
  public void testUploadDocumentDMSThrowsException() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(432423L);
    submittedDocument.setEdbDistrictId(234234L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    doThrow(HttpServerErrorException.class).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<IngestionResponse>>any());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }


  @Test(expected = DcsException.class)
  public void testUploadDocumentStoreDocumentReturnsNull() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    doReturn(null).when(submittedDocumentDao).save(Mockito.any(SubmittedDocument.class));
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }

  @Test(expected = DcsException.class)
  public void testUploadDocumentDMSNotSuccessful() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(432423L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    IngestionResponse response = new IngestionResponse();
    response.setResultCode("0001");
    response.setResultMessage("Error Message");
    ResponseEntity<IngestionResponse> respnseEntity =
        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    doReturn(respnseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<IngestionResponse>>any());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }

  @Test(expected = DcsException.class)
  public void testUploadDocumentUpdateECMaasErrorStatusStoreThrowsException() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(432423L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    IngestionResponse response = new IngestionResponse();
    response.setResultCode("0001");
    response.setResultMessage("Error Message");
    ResponseEntity<IngestionResponse> respnseEntity =
        new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    doReturn(respnseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<IngestionResponse>>any());
    doThrow(JDBCException.class).when(submittedDocumentDao)
        .updateEcmaasGuidAndStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }

  @Test(expected = DcsException.class)
  public void testUploadDocumentUpdateECMaasSuccessStatusStoreThrowsException() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(432423L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    IngestionResponse response = new IngestionResponse();
    response.setResultCode("0001");
    response.setResultMessage("Error Message");
    ResponseEntity<IngestionResponse> respnseEntity =
        new ResponseEntity<>(response, HttpStatus.CREATED);
    doReturn(respnseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<IngestionResponse>>any());
    doThrow(JDBCException.class).when(submittedDocumentDao)
        .updateEcmaasGuidAndStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
  }

  @Test(expected = DcsException.class)
  public void testUploadDocumentUpdateECMaasSuccessStatus() {
    doReturn(null).when(eTrackDartFacilityDAO).findByDistrictId(Mockito.anyLong());
    doReturn(null).when(eTrackDartFacilityDAO).save(Mockito.any(EtrackDartFacility.class));
    List<String> docNames = Arrays.asList("DOC_NAMES");
    doReturn(docNames).when(submittedDocumentDao).findDocumentNmByDistrictId(Mockito.anyLong());
    IngestionRequest ingestionRequest = new IngestionRequest();
    Map<String, String> metadata = new HashMap<>();
    metadata.put(DCSServiceConstants.FOIL_STATUS, "REL");
    metadata.put(DCSServiceConstants.DOCUMENT_TITLE, "DOC_NAME");
    metadata.put(DCSServiceConstants.ACCESS, "0");
    metadata.put(DCSServiceConstants.DOC_CATEGORY, "12");
    metadata.put(DCSServiceConstants.DOC_SUB_CATEGORY, "10");
    metadata.put(DCSServiceConstants.HISTORIC, "0");
    ingestionRequest.setMetaDataProperties(metadata);
    Map<String, String> fileDates = new HashMap<>();
    fileDates.put("fileName", "02/09/2021 06:30:00");
    ingestionRequest.setFileDates(fileDates);
    MockMultipartFile file =
        new MockMultipartFile("fileName", "Original FileName", "pdf", "dfakjdkflaj".getBytes());
    SubmittedDocument submittedDocument = new SubmittedDocument();
    submittedDocument.setDocumentId(432423L);
    doReturn(submittedDocument).when(submittedDocumentDao)
        .save(Mockito.any(SubmittedDocument.class));
    IngestionResponse response = new IngestionResponse();
    response.setResultCode("0001");
    response.setResultMessage("Error Message");
    ResponseEntity<IngestionResponse> responseEntity =
        new ResponseEntity<>(response, HttpStatus.CREATED);
    doReturn(responseEntity).when(restTemplate).exchange(Mockito.anyString(),
        Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
        Mockito.<Class<IngestionResponse>>any());
    doReturn(1).when(submittedDocumentDao).updateEcmaasGuidAndStatus(Mockito.anyLong(),
        Mockito.anyString(), Mockito.anyString());
    serviceImpl.uploadDocument("userId", "token", "PERMIT", "32423", ingestionRequest,
        new MockMultipartFile[] {file}, "contextId");
    assertTrue(responseEntity instanceof ResponseEntity);
  }
}
