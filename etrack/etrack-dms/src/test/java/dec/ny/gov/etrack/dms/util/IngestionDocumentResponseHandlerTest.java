package dec.ny.gov.etrack.dms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.response.DimMetadataProperty;
import dec.ny.gov.etrack.dms.response.ECMaaSMetaDataResponse;
import dec.ny.gov.etrack.dms.response.ECMaaSResponse;

@RunWith(SpringJUnit4ClassRunner.class)
public class IngestionDocumentResponseHandlerTest {

  @InjectMocks
  private IngestionDocumentResponseHandler ingestionHandler;

  @Test
  public void testIngestionServiceReturnsInternalErrorWhenNoResponseBodyReturns() {
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(HttpStatus.OK);
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsSameStatusCodeWhenStatusCodeIsNotOk() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0001");
    ecMaaSResponse.setResultMessage("003_0003 - Initialization Error – User ID is Required");
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.NOT_FOUND);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
  
  @Test
  public void testIngestionServiceReturnsInternalServerErrorWhenReturnsNoGuid() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0000");
    ecMaaSResponse.setResultMessage("000_0000 Document Created Successfully");
    DimMetadataProperty dimMetadataProperty = new DimMetadataProperty();
    dimMetadataProperty.setPropertyDefinitionId("Id");
    List<DimMetadataProperty> dimMetadataProperties = new ArrayList<>();
    dimMetadataProperties.add(dimMetadataProperty);
    ecMaaSResponse.setDimMetadataProperties(dimMetadataProperties);

    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsInternalServerErrorWhenReturnsBlankSpaceGuid() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0000");
    ecMaaSResponse.setResultMessage("000_0000 Document Created Successfully");
    DimMetadataProperty dimMetadataProperty = new DimMetadataProperty();
    dimMetadataProperty.setPropertyDefinitionId("Id");
    dimMetadataProperty.setValue("    ");
    List<DimMetadataProperty> dimMetadataProperties = new ArrayList<>();
    dimMetadataProperties.add(dimMetadataProperty);
    ecMaaSResponse.setDimMetadataProperties(dimMetadataProperties);

    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsSuccessAndReturnsValidGuid() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0000");
    ecMaaSResponse.setResultMessage("000_0000 Document Created Successfully");
    DimMetadataProperty dimMetadataProperty = new DimMetadataProperty();
    dimMetadataProperty.setPropertyDefinitionId("Id");
    dimMetadataProperty.setValue("{A013397A-0000-CA11-972D-4853EBA522BE}");
    List<DimMetadataProperty> dimMetadataProperties = new ArrayList<>();
    dimMetadataProperties.add(dimMetadataProperty);
    ecMaaSResponse.setDimMetadataProperties(dimMetadataProperties);

    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("{A013397A-0000-CA11-972D-4853EBA522BE}", response.getBody().getGuid());
  }



  @Test
  public void testIngestionServiceReturnsInternalServerErrorWhenGuidNotPassedInMetaData() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0000");
    ecMaaSResponse.setResultMessage("000_0000 Document Created Successfully");
    DimMetadataProperty dimMetadataProperty = new DimMetadataProperty();
    List<DimMetadataProperty> dimMetadataProperties = new ArrayList<>();
    dimMetadataProperties.add(dimMetadataProperty);
    ecMaaSResponse.setDimMetadataProperties(dimMetadataProperties);

    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsInternalErrorWhenReceivesFileNetErrorCode_03() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0002");
    ecMaaSResponse
        .setResultMessage("009_0003 - Repository Create error – FileNet Error: Connection Error");
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsInternalErrorWhenReceivesFileNetErrorCode_04() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0002");
    ecMaaSResponse.setResultMessage(
        "009_0004 - Repository Create error – FileNet Error: Error in saving document");
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsInternalErrorWhenReceivesFileNetErrorCode_05() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0002");
    ecMaaSResponse.setResultMessage(
        "009_0005 - Repository Create error - FileNet Error: Credentials not found in config DB");
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void testIngestionServiceReturnsBadRequestWhenFileNetSendsOtherErrorCode() {
    ECMaaSResponse ecMaaSResponse = new ECMaaSResponse();
    ecMaaSResponse.setResultCode("0001");
    ecMaaSResponse
        .setResultMessage("014_0175 - Validation Error - Mandatory Metadata Property Missing");
    ResponseEntity<ECMaaSResponse> inputResponse =
        new ResponseEntity<>(ecMaaSResponse, HttpStatus.OK);
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, false);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
  @Test
  public void testUpdateMetaDataServiceWhenUpdateMetadataFlasIsTrue() {
    ECMaaSMetaDataResponse ecMaaSMetaDataResponse = new ECMaaSMetaDataResponse();
    ecMaaSMetaDataResponse.setResultCode("0000");
    ecMaaSMetaDataResponse.setResultMessage("000_0000 Document Created Successfully");

    ResponseEntity<ECMaaSMetaDataResponse> inputResponse =
        new ResponseEntity<>(ecMaaSMetaDataResponse, HttpStatus.OK);

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setClientId("TEST_CLIENT_ID");
    ingestionRequest.setUserId("TEST_USER_ID");
    ingestionRequest.setAttachmentFilesCount(1);
    ResponseEntity<IngestionResponse> response =
        ingestionHandler.handleResponse(inputResponse, ingestionRequest, true);
    assertEquals(HttpStatus.OK, response.getStatusCode());

  }
  
}
