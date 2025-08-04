package dec.ny.gov.etrack.permit.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.IngestionRequest;
import dec.ny.gov.etrack.permit.model.IngestionResponse;
import dec.ny.gov.etrack.permit.repo.SpatialInquiryRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;

@Component
public class DocumentUploadService {
  
  private static final Logger logger = LoggerFactory.getLogger(DocumentUploadService.class.getName());
  private static final DateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyyMMddHHmmss");

  @Autowired
  private SupportDocumentRepo supportDocumentRepo;
  @Autowired
  private SpatialInquiryRepo spatialInquiryRepo;
  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  /**
   * Method to request GIS server to share the Map and upload to DMS. Re-attempt (max 3 attempts includes 2 re-attempt) if any failure.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id/Inquiry Id will be passed.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token
   * @param printUrl - GIS Map URL to retrieve the content.
   * @param decId - Facility's DEC Id.
   * @param applicantSubmitted - Applicant submitted status or not.
   * @param giInqRequestInd - indicates whether Spatial Inquiry request or permit support document.
   * 
   * @return - Returns Uploaded document Id.
   */
  public String uploadGISPrintFormattedMapDocumentToDMS(final String userId, final String contextId,
      final String token, final Long projectId, String printUrl, final String decId,
      final boolean applicantSubmitted, final boolean giInqRequestInd) {
    
    int retryCount = 0;
    String responseAfterUpload = null;
    ETrackPermitException ex;
    do {
      try {
        logger.info("Project/Inquiry Id {}. Attempt {} to read and upload the file into DMS. "
            + "User Id {}, Context Id {}", projectId, retryCount, userId, contextId );
        responseAfterUpload = uploadPrintedFormatOfMapDocumentToDMS(userId, contextId,
            token, projectId, printUrl, decId, applicantSubmitted, giInqRequestInd);
        return responseAfterUpload;        
      } catch (ETrackPermitException e) {
        if (StringUtils.hasLength(e.getErrorCode()) 
            && (e.getErrorCode().equals("UNSUCCESSFUL_TO_RETRIEVE_MAP") 
                || e.getErrorCode().equals("UNABLE_TO_RETRIEVE_MAP"))) {
          ++retryCount;
          logger.info("Project/Inquiry Id {}. Re-Attempt {} to read and upload the file into DMS. "
              + "User Id {}, Context Id {}", projectId, retryCount, userId, contextId );

          try {
            Thread.sleep(5000l);
          } catch (InterruptedException e1) {
            logger.error("Error while holding the thread before go for re-attempt");
          }
          ex = e;
        } else {
          throw e;
        }
      }
    } while (retryCount < 2);
    throw ex;
  }
  
  /**
   * This method is used to retrieve the Printed Format of Map content from GIS server and upload to
   * DMS via etrack-dcs service.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id/Inquiry Id will be passed.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token
   * @param printUrl - GIS Map URL to retrieve the content.
   * @param decId - Facility's DEC Id.
   * @param applicantSubmitted - Applicant submitted status or not.
   * @param giInqRequestInd - indicates whether Spatial Inquiry request or permit support document.
   * 
   * @return - Returns document Id.
   */
  private String uploadPrintedFormatOfMapDocumentToDMS(final String userId, final String contextId,
      final String token, final Long projectId, String printUrl, final String decId,
      final boolean applicantSubmitted, final boolean giInqRequestInd) {

    logger.info("Entering into uploadPrintedFormatOfMapDocumentToDMS. "
        + "User Id {}, Context Id {}. Project Id {}", userId, contextId, projectId);

    byte[] printedMapContent = null;
    try {
      RestTemplate retrievePrintableMapRestTemplate = new RestTemplate();
      HttpHeaders httpHeaders = new HttpHeaders();
      HttpEntity<?> mapEntity = new HttpEntity<>(httpHeaders);
      logger.info("Polygon Map URL which needs to be used to retrieve the Polygon Map {}",
          printUrl);
      printUrl = printUrl.replace("http://", "https://");
      logger.info(
          "Polygon Map URL appended https which needs to be used to retrieve the Polygon Map {}",
          printUrl);
      ResponseEntity<byte[]> printedFormatMapResponse = retrievePrintableMapRestTemplate
          .exchange(printUrl, HttpMethod.GET, mapEntity, byte[].class);
      if (!printedFormatMapResponse.getStatusCode().equals(HttpStatus.OK)) {
        logger.error(
            "Error status code received from Print url  {} for the project/Inquiry Id {}",
            printUrl, projectId);
        throw new ETrackPermitException(printedFormatMapResponse.getStatusCode(),
            "UNSUCCESSFUL_TO_RETRIEVE_MAP",
            "Received Unsuccessful status from GIS Server " + projectId);
      }
      printedMapContent = printedFormatMapResponse.getBody();
    } catch (ETrackPermitException e) {
      throw e;
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error(
          "Received invalid status {} while retrieving Printed format of Map from this path {}. "
              + "User Id {}, Context Id {}",
          e.getStatusCode(), printUrl, userId, contextId, e);
      throw new ETrackPermitException(e.getStatusCode(), "UNSUCCESSFUL_TO_RETRIEVE_MAP",
          "Unable to retrieve the Printed format Map from GIS Server. Message  " + e.getMessage());
    } catch (Exception e) {
      logger
          .error("Received general error while retrieving Printed format of Map from this path {}. "
              + "User Id {}, Context Id {}", printUrl, userId, contextId, e);
      throw new ETrackPermitException("UNABLE_TO_RETRIEVE_MAP",
          "Unable to retrieve the Printed format Map from GIS Server. Id : " + projectId, e);
    }
    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> filesDate = new HashMap<>();
    String currentDate = YYYY_MM_DD_HH_MM_SS.format(new Date());
    filesDate.put("0", currentDate);
    ingestionRequest.setFileDates(filesDate);
    Map<String, Object> metadataProperties = new HashMap<>();
    if (giInqRequestInd) {
      metadataProperties.put("Description", projectId + " inquiry map url");
    } else {
      metadataProperties.put("Description", projectId + " project printed format of Map");
    }
    metadataProperties.put("docCategory", "31");
    metadataProperties.put("docSubCategory", "278");
    metadataProperties.put("docCreationType", "PDF");
    StringBuilder sb = new StringBuilder();
    if (giInqRequestInd) {
      sb.append("GI_MAP_INQUIRYID_").append(projectId);
    } else if (applicantSubmitted) {
      sb.append("Location_Submitted_PID").append(projectId);
    } else {
      sb.append("Location_Validated_PID").append(projectId);
    }
    
    List<Long> documentIds = null;
    if (giInqRequestInd) {
      documentIds = spatialInquiryRepo.findByDocumentNameAndInquiryId(projectId, sb.toString()); 
    } else {
      documentIds = supportDocumentRepo.findByDocumentNameAndProjectId(projectId, sb.toString());
    }
    if (!CollectionUtils.isEmpty(documentIds)) {
      List<String> documentIdStr = new ArrayList<>();
      documentIds.forEach(documentId -> {
        documentIdStr.add(String.valueOf(documentId));      
      });
      deleteExistingDocumentFromDMS(userId, contextId, projectId, token, documentIdStr, giInqRequestInd);
    }
    metadataProperties.put("DocumentTitle", sb.toString());
    metadataProperties.put("historic", "0");
    metadataProperties.put("docCreator", userId);
    metadataProperties.put("indexDate", currentDate);
    metadataProperties.put("docLastModifier", userId);
    metadataProperties.put("source", "ETRACK");
    metadataProperties.put("projectID", projectId);
    metadataProperties.put("applicationID", decId);
    metadataProperties.put("foilStatus", "NODET");
    metadataProperties.put("deleteFlag", "F");
    metadataProperties.put("renewalNumber", "0");
    metadataProperties.put("modificationNumber", "0");
    metadataProperties.put("trackedAppId", "");
    metadataProperties.put("access", "0");
    metadataProperties.put("nonRelReasonCodes", "");
    metadataProperties.put("receivedDate", currentDate);
    metadataProperties.put("permitType", "");
    ingestionRequest.setMetadataProperties(metadataProperties);
    String[] printURLStringArray = printUrl.split("/");

    ByteArrayResource byteArrayResource = new ByteArrayResource(printedMapContent) {
      @Override
      public String getFilename() {
        return printURLStringArray[printURLStringArray.length - 1];
      }
    };

    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    try {
      HttpHeaders attachFile = new HttpHeaders();
      attachFile.setContentType(MediaType.TEXT_PLAIN);
      HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(byteArrayResource, attachFile);
      filesAndMetadataMap.add("uploadFiles", attachment);
      HttpHeaders fileMetadataHeaders = new HttpHeaders();
      fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IngestionRequest> metaDataEntity =
          new HttpEntity<>(ingestionRequest, fileMetadataHeaders);
      filesAndMetadataMap.add("ingestionMetaData", metaDataEntity);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      httpHeaders.add("userId", userId);
      StringBuilder clientId = new StringBuilder();
      clientId.append("DEC_").append("SUPPORTINGDOCUMENTS").append("_P8");
      if (giInqRequestInd) {
        httpHeaders.add("inquiryId", String.valueOf(projectId));
      } else {
        httpHeaders.add("projectId", String.valueOf(projectId));
      }
      httpHeaders.add("clientId", clientId.toString());
      httpHeaders.add("contextId", contextId);
      httpHeaders.add("docClassification", String.valueOf(2));;
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);

      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(filesAndMetadataMap, httpHeaders);
      String uri = null;
      if (giInqRequestInd) {
        uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/spatial-inquiry/upload")
            .build().toUriString();
      } else {
        uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/support-document/upload")
            .build().toUriString();
      }
      logger.info(
          "Making call to DMS to upload Printed Map document for the DEC id: {} . User id: {} context Id: {}",
          decId, userId, contextId);
      ResponseEntity<IngestionResponse> ingestionResponse = eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.POST, requestEntity, IngestionResponse.class);
      logger.info("Uploaded the Printed Format Map to DMS successfully. User Id: {} Context Id: {}",
          userId, contextId);
      return ingestionResponse.getBody().getDocumentId();
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error(
          "Error while requesting etrack-dcs service to upload the Printed format Map document. "
              + "User Id {}, Context Id {}. Error details ",
          userId, contextId, e);
      throw new ETrackPermitException(e.getStatusCode(), "PRINTED_MAP_DOC_UPLOAD_ERROR",
          e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new ETrackPermitException("PRINTED_MAP_DOC_UPLOAD_ERROR",
          "Error while preparing the Printed format Map document and upload into DMS for the DEC Id "
              + decId,
          e);
    }
  }

  public void deleteExistingDocumentFromDMS(String userId, String contextId,
      Long projectId, String token, List<String> documentIds, final boolean giInqRequestInd) {
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      httpHeaders.add("userId", userId);
      if (giInqRequestInd) {
        httpHeaders.add("inquiryId", String.valueOf(projectId));
      } else {
        httpHeaders.add("projectId", String.valueOf(projectId));
      }
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
      String uri = null;
      if (giInqRequestInd) {
        uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/spatial-inquiry/document/")
            .path(String.join(",", documentIds)).build().toUriString();
      } else {
        uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/support-document/document/")
            .path(String.join(",", documentIds)).build().toUriString();
      }
      HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
      logger.info("Making call to DMS to Delete the Existing Document. "
          + "document id: {} . User id: {} context Id: {}",
          documentIds, userId, contextId);
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, JsonNode.class);
      logger.info("Deleted the existing document successful. User id: {} context id: {}", userId,
          contextId);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error("Error while requesting etrack-dcs service to delete the existing document . "
          + "User Id {}, Context Id {}. Error details ", userId, contextId, e);
      throw new ETrackPermitException(e.getStatusCode(), "DELETE_EXISTING_DOC_ERROR",
          e.getResponseBodyAsString());
    } catch (Exception e) {
      throw new ETrackPermitException("DELETE_EXISTING_DOC_GEN_ERROR",
          "Error while requesting to delete from DMS for the document Id "
              + documentIds, e);
    }
  }
}
