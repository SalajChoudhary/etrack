package dec.ny.gov.etrack.gis.service.impl;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.GISResponse;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.model.PolygonFeature;
import dec.ny.gov.etrack.gis.model.PolygonObject;
import dec.ny.gov.etrack.gis.model.ProjectPolygon;
import dec.ny.gov.etrack.gis.service.GISPolygonService;

/**
 * This class to interact with GIS internal, External and Other eTrack servers to support Facility
 * Step 1.
 * 
 * @author mxmahali
 */
@Service
public class GISPolygonServiceImpl implements GISPolygonService {

  private static final String POLYGON_URL_TEMPLATE = "/%1$s/FeatureServer/0/%2$s";
  private static final String DEC_POLYGON =
      "/%1$s/FeatureServer/0/%2$s?outFields=*&returnGeometry=true&returnDistinctValues=true&f=pjson&outSR=4326&returnCentroid=true&";


  @Autowired
  @Qualifier("gisInternalServiceRestTemplate")
  private RestTemplate gisInternalServiceRestTemplate;

  @Autowired
  @Qualifier("gisExternalServiceRestTemplate")
  private RestTemplate gisExternalServiceRestTemplate;

  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;

  @Autowired
  @Qualifier("eTrackGISServiceRestTemplate")
  private RestTemplate eTrackGISServiceRestTemplate;

  @Autowired
  @Qualifier("gisEFindPolygonReadRestTemplate")
  private RestTemplate gisEFindPolygonReadRestTemplate;

  @Autowired
  @Qualifier("giseFindPolygonUploadServiceRestTemplate")
  private RestTemplate giseFindPolygonUploadServiceRestTemplate;

  private static final String SAVE_ACTION = "S";
  private static final String UPDATE_ACTION = "U";
  private static final String INVALID_URL_LOG_INFO =
      "URL {} contains invalid encoded value. Error Message {}";
  private static final String PJSON_FIELD = "pjson";
  private static final String OUTFIELDS_TEXT = "outFields";
  private static Logger LOGGER = LoggerFactory.getLogger(GISPolygonService.class.getName());

  public String getDECPolygonByTaxId(final String taxParcelID, final String countyName,
      final String municipalName, final String contextId) {

    String query = "COUNTIES='" + countyName + "' and MUNICIPALITIES='" + municipalName
        + "' and PRIMARY_ID like '" + taxParcelID + "%'";
    String url = String.format(DEC_POLYGON, "eTrack_facility_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query).toUriString();

    try {
      LOGGER.info("getDECPolygonByTaxId URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_POLYGON_BY_TAXID_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  public String getDECPolygonByAddress(final String street, final String city,
      final String contextId) {
    String query = "UPPER(LOCATION_DIRECTIONS_1) like '%" + street.toUpperCase()
        + "%' and UPPER(CITY)='" + city.toUpperCase() + "'";
    String url = String.format(DEC_POLYGON, "eTrack_facility_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query).toUriString();

    try {
      LOGGER.info("getDECPolygonByAddress URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_POLYGON_BY_ADR_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
  * 
  */
  public String getDECPolygonByDecId(final String decId, final String contextId,
      final String jwtToken) {

    Map<String, Object> facilityDetails =
        getDECIdByProgramType("system", contextId, jwtToken, decId, "DEC");
    LOGGER.info("DEC ID Program Type {}", facilityDetails);
    String edbDistrictId = (String) facilityDetails.get("districtId");

    // String query = "PRIMARY_ID Like '" + decId + "%'";
    String url = String.format(DEC_POLYGON, "eTrack_facility_poly", "query");
    String query = "SITE_ID='" + edbDistrictId + "'";
    // String url = String.format(DEC_POLYGON, "eFind_facility_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query).toUriString();
    try {
      LOGGER.info("getDECPolygonByDecId URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_POLYGON_BY_DECID_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * This method is used to perform Save, update and Delete Applicant polygon based on the action
   * indicator
   */
  public Object applicantPolygon(final List<Object> featureMap, final String value,
      final String actionInd, final String contextId) {
    String url = null;
    switch (actionInd) {
      case SAVE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_scratch_poly", "addFeatures");
        break;
      case UPDATE_ACTION:
        url =
            String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_scratch_poly", "updateFeatures");
        break;
      default:
        throw new BadRequestException("NOT_VALID_ACTION",
            "Not a valid an action indicator passed to the Applicant Polygon ", actionInd);
    }
    return submitPolygonRequest(url, featureMap, value, "INTERNAL", contextId);
  }

  /**
   * This method is used to perform Save, update and Delete Analyst polygon based on the action
   * indicator
   */
  public Object analystPolygon(List<Object> featureMap, String value, final String actionInd,
      final String contextId) {
    String url = null;
    switch (actionInd) {
      case SAVE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_analyst_poly", "addFeatures");
        break;
      case UPDATE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_analyst_poly", "updateFeatures");
        break;
      default:
        throw new BadRequestException("NOT_VALID_ACTION",
            "Not a valid an action indicator passed to the Analyst Polygon", actionInd);

    }
    return submitPolygonRequest(url, featureMap, value, "INTERNAL", contextId);
  }

  /**
   * This method is used to perform Save and Delete Analyst polygon based on the action indicator
   */
  public Object submittedPolygon(List<Object> featureMap, String value, final String actionInd,
      final String contextId) {
    String url = null;
    switch (actionInd) {
      case SAVE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_submittal_poly", "addFeatures");
        break;
      case UPDATE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_submittal_poly",
            "updateFeatures");
        break;
      default:
        throw new BadRequestException("NOT_VALID_ACTION",
            "Not a valid an action indicator passed to the Submitted Polygon", actionInd);
    }
    return submitPolygonRequest(url, featureMap, value, "INTERNAL", contextId);
  }

  /**
   * 
   * @param url
   * @param featureMap
   * @param value
   * @return
   */
  private Object submitPolygonRequest(String url, List<Object> featureMap, String value,
      final String gisEnvInd, final String contextId) {

    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<List<Object>> formJson = new HttpEntity<>(featureMap, formheaders);
    featureMapDetails.add("features", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    GISServiceResponse response = null;
    try {
      LOGGER.info("URL for save Polygon {}", URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      if ("EXTERNAL".equals(gisEnvInd)) {
        LOGGER.info("GIS External service is getting executed");
        response = gisExternalServiceRestTemplate.postForObject(
            URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity,
            GISServiceResponse.class);
      } else {
        LOGGER.info("GIS Internal service is getting executed");
        response = gisInternalServiceRestTemplate.postForObject(
            URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity,
            GISServiceResponse.class);
      }
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_SUBMIT_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
    if (response != null) {
      List<GISResponse> gisResponse =
          !CollectionUtils.isEmpty(response.getAddResults()) ? response.getAddResults()
              : response.getUpdateResults();
      if (!CollectionUtils.isEmpty(gisResponse)) {
        if (!gisResponse.get(0).isSuccess()) {
          throw new BadRequestException("GIS Error Code " + gisResponse.get(0).getError().getCode(),
              gisResponse.get(0).getError().getDescription(), featureMap);
        }
      }
      return response;
    } else {
      throw new GISException("GIS_SERVICE_ERROR", "Error while interacting with GIS Service");
    }
  }

  /**
   * 
   */
  public String getApplicantPolygon(final String applicationId, final String contextId) {

    String query = "OBJECTID=" + applicationId;
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_scratch_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
        .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*").queryParam("outSR", "3857")
        .queryParam("returnCentroid", true).toUriString();

    try {
      LOGGER.info("getApplicantPolygon URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_APLCT_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  public Object deletePolygonByObjId(String objectIdInput, final String contextId) {
    LOGGER.info("Entering into Delete polygon by Id {}", objectIdInput);
    String url =
        String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_scratch_poly", "deleteFeatures");

    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> formJson = new HttpEntity<>("objectId=" + objectIdInput, formheaders);
    featureMapDetails.add("where", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    try {
      LOGGER.info("URL for delete the Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEL_APLCT_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  
  public Object deleteSpatialInquiryPolygonByObjId(String spatialInquiryObjectIdInput,
      final String contextId) {
    LOGGER.info("Entering into Delete polygon by Id {}", spatialInquiryObjectIdInput);
    String url =
        String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_scratch_poly", "deleteFeatures");

    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> formJson =
        new HttpEntity<>("objectId=" + spatialInquiryObjectIdInput, formheaders);
    featureMapDetails.add("where", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    try {
      LOGGER.info("URL for delete the Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_SPATIAL_INQ_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  public Object deleteAnalystPolygonByObjId(final String userId, final String contextId,
      final String objectIdInput) {
    LOGGER.info("Entering into Delete Analyst polygon by Id {}", objectIdInput);
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_analyst_poly", "deleteFeatures");
    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> formJson = new HttpEntity<>("objectId=" + objectIdInput, formheaders);
    featureMapDetails.add("where", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    try {
      LOGGER.info("URL for delete the Analyst Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEL_ANALYST_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  public Object deleteApplicantSubmittalPolygonByObjId(final String userId, final String contextId,
      final String objectIdInput) {
    LOGGER.info("Entering into Delete Submittal polygon by Id {}", objectIdInput);
    String url =
        String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_submittal_poly", "deleteFeatures");
    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> formJson = new HttpEntity<>("objectId=" + objectIdInput, formheaders);
    featureMapDetails.add("where", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    try {
      LOGGER.info("URL for delete the submit Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    }  catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEL_APLCT_SUBMITTED_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }


  public String getAnalystPolygon(final String polygonId, final String contextId) {

    String query = "OBJECTID=" + polygonId;
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_analyst_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
        .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*").queryParam("outSR", "3857")
        .queryParam("returnCentroid", true).toUriString();

    try {
      LOGGER.info("getAnalystPolygon URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_ANALYST_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  public String getsubmitedPolygon(final String applSubId, final String contextId) {

    String query = "OBJECTID=" + applSubId;
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_applicant_submittal_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
        .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*").queryParam("outSR", "3857")
        .queryParam("returnCentroid", true).toUriString();

    try {
      LOGGER.info("getsubmitedPolygon URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_SUBMITTED_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }


  /**
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> getDECIdByProgramType(final String userId, final String contextId,
      final String jwtToken, final String programId, final String programType) {
    LOGGER.info("Entering into getDECIDDetail. User Id {} Context Id{}", userId, contextId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add("programId", programId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    String url = UriComponentsBuilder.fromUriString("/etrack-dart-db/decid/programType/")
        .path(programType).toUriString();
    HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
    try {
      return eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.GET, entity, Map.class)
          .getBody();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_DECID_BY_PROG_TYPE_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  /**
   * 
   */
  @Override
  public ResponseEntity<String> getDECIdByTxmap(final String userId, final String contextId,
      final String jwtToken, final String txmap, final String county, final String municipality) {
    LOGGER.info("Entering into getDECIDDetail. User Id {} Context Id{}", userId, contextId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    String url = UriComponentsBuilder.fromUriString("/etrack-dart-db/decid/txmap/")
        .queryParam("txmap", txmap).queryParam("county", county)
        .queryParam("municipality", municipality).toUriString();
    HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
    
    try {
      ResponseEntity<String> responseEntity =
          eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.GET, entity, String.class);
      if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        return responseEntity;
      } else {
        return new ResponseEntity<String>(responseEntity.getStatusCode());
      }
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_DECID_BY_TAXMAP_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }


  @Override
  public Object uploadShapefile(String userId, String contextId, String filetype,
      String publishParameters, String value, MultipartFile file) {

    LOGGER.info("Entering into upload the shape file. User Id {}, Context Id {}", userId,
        contextId);
    MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
    HttpHeaders multiPartHeaders = new HttpHeaders();
    multiPartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    requestBody.add("filetype", filetype);
    requestBody.add("publishParameters", publishParameters);
    requestBody.add("f", value);
    try {
      ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() {
          return file.getOriginalFilename();
        }
      };
      requestBody.add("file", resource);
    } catch (Exception e) {
      LOGGER.error("File is not available or corrupted . User Id {}, Context Id {} ", userId,
          contextId);
      throw new GISException("FILE_NOT_FOUND", "File is not available or corrupted", e);
    }
    try {
      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(requestBody, multiPartHeaders);
      return eTrackGISServiceRestTemplate.postForEntity("/", requestEntity, String.class);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_RET_ADR_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    } 
  }


  @Override
  public Object saveOrUpdateWorkAreaPolygon(List<Object> featureMap, String value, String action,
      String contextId) {

    String url = null;
    switch (action) {
      case SAVE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_work_area_poly", "addFeatures");
        break;
      case UPDATE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_work_area_poly", "updateFeatures");
        break;
      default:
        throw new BadRequestException("NOT_VALID_ACTION",
            "Not a valid an action indicator passed for the Work Area Polygon ", action);

    }
    return submitPolygonRequest(url, featureMap, value, "INTERNAL", contextId);
  }

  @Override
  public Object deleteWorkAreaPolygonByObjId(String userId, String contextId, String objectId) {
    LOGGER.info("Entering into WorkArea Polygon by Id {}", objectId);
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_work_area_poly", "deleteFeatures");
    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> formJson = new HttpEntity<>("objectId=" + objectId, formheaders);
    featureMapDetails.add("where", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    try {
      LOGGER.info("URL for delete the submit Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEL_WA_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public String getWorkAreaPolygon(String workareaId, String contextId) {
    LOGGER.info("Entering into getWorkAreaPolygon. Context Id {}", contextId);
    String query = "OBJECTID=" + workareaId;
    // String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_appl_scratch", "query");
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_work_area_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
        .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*").queryParam("outSR", "3857")
        .queryParam("returnCentroid", true).toUriString();
    try {
      LOGGER.info("getWorkAreaPolygon URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_GET_WA_POLYGON_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }


  /**
   * 
   */
  private ResponseEntity<PolygonObject> getDECPolygonByDistrictId(final Long edbDistrictId,
      final String contextId) {
    String query = "SITE_ID ='" + edbDistrictId + "'";
    String url = String.format(DEC_POLYGON, "eFind_facility_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query).toUriString();
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add(HttpHeaders.ACCEPT_LANGUAGE, "text/plain");
      HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);
      LOGGER.info("getDECPolygonByDistrictId URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisEFindPolygonReadRestTemplate.exchange(
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), HttpMethod.GET, requestEntity,
          PolygonObject.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("URL {} contains invalid encoded value. Error Message ", uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEC_POLYGON_DIST_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public Map<Long, String> uploadApprovedPolygonToEFind(final String contextId,
      final List<ProjectPolygon> uploadPolygonProjects) {

    Map<Long, String> projectIdsWithUploadedStatus = new HashMap<>();
    uploadPolygonProjects.forEach(uploadPolygonProject -> {
      List<Object> uploadApprovedPolygon = new ArrayList<>();
      PolygonFeature polygonFeature = new PolygonFeature();

      try {
        String query = "OBJECTID=" + uploadPolygonProject.getPolygonGisId();
        String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_analyst_poly", "query");
        String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
            .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*")
            .queryParam("outSR", "26918")
            // .queryParam("returnCentroid", true)
            .toUriString();
        LOGGER.info("Query Parameter for this Geometry call {}",
            URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
        String geoMetricDetails = gisInternalServiceRestTemplate
            .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
        LOGGER.debug("Geo Metric details . Context Id {}. Geo metry details {} ", contextId,
            geoMetricDetails);
        PolygonObject polygongeoMetryObject =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .readValue(geoMetricDetails, PolygonObject.class);
        polygonFeature.setGeometry(polygongeoMetryObject.getFeatures().get(0).getGeometry());
        LOGGER.debug("Geo Polygon Metric details . Context Id {}. Geo metry details {} ", contextId,
            new ObjectMapper().writeValueAsString(polygongeoMetryObject));

        PolygonObject polygonObject =
            getDECPolygonByDistrictId(uploadPolygonProject.getEdbDistrictId(), contextId).getBody();
        LOGGER.debug("Geo Polygon Object details . Context Id {}. Geo metry details {} ", contextId,
            new ObjectMapper().writeValueAsString(polygonObject));

        if (polygonObject.getError() != null) {
          throw new GISException("EFIND_POLYGON_RETRIEVAL_ERR",
              "Unable to retrieve the Polygon " + polygonObject.getError());
        }
        polygonFeature.setAttributes(polygonObject.getFeatures().get(0).getAttributes());
        polygonFeature.getAttributes()
            .setValidatedLocation(uploadPolygonProject.getApprovedPolygonChangeInd());
        LOGGER.debug("Polygon Object details ",
            new ObjectMapper().writeValueAsString(polygonObject));
        uploadApprovedPolygon.add(polygonFeature);
        LOGGER.debug("Ready to upload polygon Object details {}",
            new ObjectMapper().writeValueAsString(uploadApprovedPolygon));
        uploadPolygonToEFind(uploadApprovedPolygon, PJSON_FIELD, contextId);
        LOGGER.debug("Ready to upload polygon Object details {}",
            new ObjectMapper().writeValueAsString(uploadApprovedPolygon));
        projectIdsWithUploadedStatus.put(uploadPolygonProject.getProjectId(), "S");
      } catch (Exception e) {
        LOGGER.error("Error while uploading the polygon into eFind. Context Id {}", contextId, e);
        projectIdsWithUploadedStatus.put(uploadPolygonProject.getProjectId(), "E");
      }
    });
    return projectIdsWithUploadedStatus;
  }

  /**
   * This method is used to perform Update Approved Polygon details into eFind.
   * 
   */
  private Object uploadPolygonToEFind(List<Object> featureMap, String value,
      final String contextId) throws UnsupportedEncodingException {

    MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
    HttpHeaders formheaders = new HttpHeaders();
    formheaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<List<Object>> formJson = new HttpEntity<>(featureMap, formheaders);
    featureMapDetails.add("features", formJson);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
    featureMapDetails.set("f", form);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> entity =
        new HttpEntity<>(featureMapDetails, httpHeaders);
    String url = "/eFind_facility_poly/FeatureServer/0/updateFeatures";
    
    GISServiceResponse response = giseFindPolygonUploadServiceRestTemplate.postForObject(
        URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, GISServiceResponse.class);
    if (response != null) {
      List<GISResponse> gisResponse =
          !CollectionUtils.isEmpty(response.getAddResults()) ? response.getAddResults()
              : response.getUpdateResults();
      if (!CollectionUtils.isEmpty(gisResponse) && !gisResponse.get(0).isSuccess()) {
        throw new BadRequestException(" GIS Error Code " + gisResponse.get(0).getError().getCode(),
            gisResponse.get(0).getError().getDescription(), featureMap);
      }
      return response;
    } else {
      throw new GISException("GIS_EFIND_SERVICE_ERROR",
          "Error while interacting with GIS eFind Service");
    }
  }
}
