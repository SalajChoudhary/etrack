package dec.ny.gov.etrack.dcs.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.IngestionResponse;
import dec.ny.gov.etrack.dcs.model.Response;

@Component
public class DCSServiceUtil {

  @Value("${allowed.input.file.extn.in.zip}")
  private String fileExtns;
  private static final String ZIP_FILE_EXTN = ".zip";

  @Value("${dms.ingest.document.uri.path}")
  private String ingestDocumentPath;
  @Autowired
  private RestTemplate restTemplate;
  
  @Value("${dms.delete.document.uri.path}")
  private String deleteDocumentPath;


  private static Logger logger = LoggerFactory.getLogger(DCSServiceUtil.class);

  private enum DocumentClasses {
    APPLICATION, PERMIT, SUPPORTINGDOCUMENTS, CORRESPONDENCE, NOTICES
  }

  /**
   * Returns the Document Class is valid or not.
   *  
   * @param documentClass - Document Class name.
   * 
   * @return - True of False whether its valid or not.
   */
  public Boolean isDocumentClassValid(String documentClass) {
    logger.debug("Entering isDocumentClassValid()");
    String documentClassCapitalized = documentClass.toUpperCase();
    for (DocumentClasses documentClass1 : DocumentClasses.values()) {
      if (documentClassCapitalized.equals(documentClass1.toString())) {
        logger.debug("Exiting isDocumentClassValid()");
        return true;
      }
    }
    logger.debug("Exiting isDocumentClassValid()");
    return false;
  }

  /**
   * Remove the unwanted characters and append with comma.
   * 
   * @param str - Input String to be formatted.
   * 
   * @return - Formatted String.
   */
  public String removeUnwantedChars(String str) {
    if (!StringUtils.isEmpty(str)) {
      String[] strs = str.split(",");
      int length = strs.length;
      StringBuilder sb = new StringBuilder();
      for (int index = 0; index < length; index++) {
        String value = strs[index];
        if (!StringUtils.isEmpty(value)) {
          if (index < length - 1) {
            sb.append(value).append(",");
          } else {
            sb.append(strs[index]);
          }
        }
      }
      str = sb.toString();
    }
    return str;
  }

  /**
   * Returns the Foil Status is valid or not.
   * 
   * @param ingestionRequest - Request Metadata.
   * @param contextId  - Unique UUID to track this request.
   * 
   * @return  - Returns True or False.
   */
  public boolean isNotValidFoilStatus(IngestionRequest ingestionRequest, String contextId) {
    String foilStatus =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.FOIL_STATUS);
    String nonReleaseCode =
        ingestionRequest.getMetaDataProperties().get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
    if (StringUtils.hasLength(foilStatus) && "REL".equals(foilStatus)) {
      if (StringUtils.hasLength(nonReleaseCode)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether input files are valid or not.
   * 
   * @param uploadFiles - Files to be uploaded.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - True or False.
   */
  public boolean isContainsValidFiles(MultipartFile[] uploadFiles, String contextId) {

    String fileExtn = null;
    List<String> validFileExtns = returnsValidExtnsList();
    for (int fileIndex = 0; fileIndex < uploadFiles.length; fileIndex++) {
      MultipartFile file = uploadFiles[fileIndex];
      try {
        logger.debug("Finding the extension for the file name {}, context Id {}",
            file.getOriginalFilename(), contextId);
        fileExtn = returnFileExtn(file.getOriginalFilename());
      } catch (Exception e) {
        return false;
      }

      if (StringUtils.isEmpty(fileExtn)) {
        logger.error("Input file name {} is not having a valid extension. context Id {}",
            file.getName(), contextId);
        return false;
      }

      if (ZIP_FILE_EXTN.equals(fileExtn)
          && !isZipContainsValidFiles(file, validFileExtns, contextId)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the indicator whether the files inside the compressed folder(.zip) is valid or not.
   * 
   * @param zipfile - Compressed file.
   * @param validFileExtns - List of valid extensions allowed in teh compressed files.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns True of False.
   */
  private boolean isZipContainsValidFiles(MultipartFile zipfile, List<String> validFileExtns,
      String contextId) {
    try {
      logger.debug("Finding the extension inside the zip the file name {}, " + "context Id {}",
          zipfile.getOriginalFilename(), contextId);
      ZipInputStream inputStream = new ZipInputStream(zipfile.getInputStream());
      ZipEntry entry = null;
      while ((entry = inputStream.getNextEntry()) != null) {
        String fileName = entry.getName();
        if (StringUtils.isEmpty(fileName) || !validFileExtns.contains(returnFileExtn(fileName))) {
          logger.error(
              "Context Id {}. This file {} present inside the zip file {} contains invalid extension.",
              contextId, fileName, zipfile.getName());
          return false;
        }
      }
    } catch (Exception e) {
      logger.error("Error while processing the input zip file {}. Context Id {} ",
          zipfile.getOriginalFilename(), contextId);
      logger.error(contextId, e);
      return false;
    }
    return true;
  }

  private String returnFileExtn(String fileName) {
    return fileName.substring(fileName.indexOf("."));
  }

  private List<String> returnsValidExtnsList() {
    if (StringUtils.isEmpty(fileExtns)) {
      throw new DcsException("File extension attribute is not available");
    }
    return Arrays.asList(fileExtns.split(","));
  }


  /**
   * Request DMS service to upload the document.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param docClass - Document Class Name.
   * @param documentId - Document Id.
   * @param ingestionMetaData - Metadata details.
   * @param filesAndMetadataMap - Files and Metadata.
   * 
   * @return - Uploaded document response.
   */
  public ResponseEntity<IngestionResponse> requestDMSServiceToUploadDocument(final String userId,
      final String contextId, final String token, final String docClass, final Long documentId,
      IngestionRequest ingestionMetaData, MultiValueMap<String, Object> filesAndMetadataMap) {

    logger.info("Entering into requestDMSServiceToUploadDocument User Id {}, Context Id {} ",
        userId, contextId);
    try {
      HttpHeaders fileMetadataHeaders = new HttpHeaders();
      fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
      ingestionMetaData.setClientId(retrieveClientId(docClass));
      logger.debug("GUID from ECMaas is not yet available. User id: {} context id: {}", userId,
          contextId);
      HttpEntity<IngestionRequest> metaDataEntity =
          new HttpEntity<>(ingestionMetaData, fileMetadataHeaders);
      filesAndMetadataMap.set("ingestionMetaData", metaDataEntity);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      httpHeaders.add(DCSServiceConstants.CLIENT_ID, ingestionMetaData.getClientId());
      httpHeaders.add(DCSServiceConstants.USER_ID, userId);
      httpHeaders.add(DCSServiceConstants.CONTEXT_ID, contextId);
      String uri = null;
      if (StringUtils.hasLength(token)) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        uri = UriComponentsBuilder.newInstance().path(ingestDocumentPath).build().toUriString();
      } else {
        uri = UriComponentsBuilder.newInstance().path("/system-doc/sdis").build().toUriString();
      }
      HttpEntity<MultiValueMap<String, Object>> requestEntity = 
          new HttpEntity<>(filesAndMetadataMap, httpHeaders);
      logger.info("Making call to DMS to upload document id: {} . User id: {} context Id: {}",
          documentId, userId, contextId);
      ResponseEntity<IngestionResponse> ingestionResponseEntity 
        = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, IngestionResponse.class);
      ingestionResponseEntity.getBody().setDocumentId(String.valueOf(documentId));
      return ingestionResponseEntity;
    } catch (Exception e) {
      logger.error(
          "Error while requesting DMS service to upload the document User Id {}, Context Id {} ",
          userId, contextId, e);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  /**
   * Remove the curly braces from the GUID value.
   * 
   * @param guid - GUID assigned by the FileNet System.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - GUID without curly braces.
   */
  public String removeBracketsFromGUID(String guid, String contextId) {
    logger.debug("Entering removeBracketsFromGUID(). context id: {}", contextId);
    if (guid != null) {
      if (guid.startsWith("{")) {
        Matcher matcher = null;
        String guidPattern = "\\{([0-9A-Z-]*)\\}";
        Pattern pattern = Pattern.compile(guidPattern);
        matcher = pattern.matcher(guid);
        if (matcher.find()) {
          guid = matcher.group(1);
        }
      }
      if (StringUtils.isEmpty(guid)) {
        throw new DocumentNotFoundException(DCSServiceConstants.NO_DOCUMENT,
            DCSServiceConstants.NO_DOCUMENT_ERR_MSG);
      }
    }
    return guid;
  }
  
  /**
   * Request DMS service to delete the requested document.
   *  
   * @param documentId - Document Id.
   * @param ecMaaSGuid - GUID assigned by FileNet for the file.
   * @param userId - User who initiates this request.
   * @param token - JWT Token.
   * @param clientId - Client Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Deleted the document response.
   */
  public ResponseEntity<Response> deleteDocuments(String documentId, String ecMaaSGuid, String userId, String token,
      String clientId, String contextId) {

    logger.info("Entering deleteDocuments. user id: {} context Id : {} , document Id : {}", userId,
        contextId, documentId);
    String guid = removeBracketsFromGUID(ecMaaSGuid, contextId);

    MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
    headersMap.add(DCSServiceConstants.GUID, guid);
    headersMap.add(DCSServiceConstants.DOCUMENT_ID, documentId);
    headersMap.add(DCSServiceConstants.USER_ID, userId);
    headersMap.add(DCSServiceConstants.CLIENT_ID, clientId);
    headersMap.add(DCSServiceConstants.CONTEXT_ID, contextId);
    String uri = null;
    if (StringUtils.hasLength(token)) {
      headersMap.add(HttpHeaders.AUTHORIZATION, token);
      uri = UriComponentsBuilder.newInstance().path(deleteDocumentPath).path("/").path(guid)
          .build().toString();
    } else {
      uri = UriComponentsBuilder.newInstance().path("/system-doc/sdds").path("/").path(guid)
          .build().toString();
    }
    HttpEntity<String> httpEntity = new HttpEntity<>(headersMap);
//    ResponseEntity<Response> deleteResponse = null;
    try {
      logger.info("Making call to DMS to delete document id:{} . user id: {} context Id: {}",
          documentId, userId, contextId);
      ResponseEntity<Response> deleteResponse = restTemplate.exchange(
          uri, HttpMethod.DELETE, httpEntity, Response.class);
      if (HttpStatus.NO_CONTENT.equals(deleteResponse.getStatusCode())) {
        logger.error(
            "There is no document associated with this in the FileNet. "
            + "Document id {} , user id: {} context Id {}", documentId, userId, contextId);
      } else if (HttpStatus.OK != deleteResponse.getStatusCode()) {
        logger.error(
            "Received unsuccessful response code {} from DMS delete response call. Document id {} , user id: {} context Id {}",
            deleteResponse.getStatusCode(), documentId, userId, contextId);
        Response response = deleteResponse.getBody();
        if (response != null) {
          logger.error(
              "Received response from DMS delete document service for "
                  + "document id {}. user id: {}. context Id {}, Result code {} , Result message {}",
              documentId, userId, contextId, response.getResultCode(), response.getResultMessage());
        }
        throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
            "Error : Unable to process your request at the time.");
      }
      return deleteResponse;
    } catch (DcsException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error while making a call to DMS to delete the document {}", documentId, e);
      throw new DcsException(DCSServiceConstants.UNABLE_TO_PROCESS_NOW,
          "Error : Unable to process your request at the time.", e);
    }
  }

  /**
   * Prepare the Client Id using the Document Class Name.
   * 
   * @param documentClassName - Document Class Name.
   * 
   * @return - Client Id String.
   */
  public String retrieveClientId(String documentClassName) {
    StringBuilder sb = new StringBuilder();
    sb.append("DEC_").append(documentClassName).append("_P8");
    return sb.toString();
  }
}
