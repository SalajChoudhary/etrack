package dec.ny.gov.etrack.gis.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.GISResponse;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;
import dec.ny.gov.etrack.gis.service.SpatialInquiryService;
import dec.ny.gov.etrack.gis.util.GISServiceUtil;

@Service
public class SpatialInquiryServiceImpl implements SpatialInquiryService {

  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  @Autowired
  @Qualifier("gisInternalServiceRestTemplate")
  private RestTemplate gisInternalServiceRestTemplate;
  @Autowired
  private GISServiceUtil gisServiceUtil;

  private static final String POLYGON_URL_TEMPLATE = "/%1$s/FeatureServer/0/%2$s";
  private static final String SAVE_ACTION = "S";
  private static final String UPDATE_ACTION = "U";
  private static final String PJSON_FIELD = "pjson";
  private static final String OUTFIELDS_TEXT = "outFields";
  private static final String INVALID_URL_LOG_INFO =
      "URL {} contains invalid encoded value. Error Message {}";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(SpatialInquiryServiceImpl.class.getName());

  @Override
  public Object spatialInquiryApplicantPolygon(List<Object> featureMap, String value, String action,
      final String contextId) {
    String url = null;
    switch (action) {
      case SAVE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_spatial_inquiry_poly", "addFeatures");
        break;
      case UPDATE_ACTION:
        url = String.format(POLYGON_URL_TEMPLATE, "eTrack_spatial_inquiry_poly", "updateFeatures");
        break;
      default:
        throw new BadRequestException("NOT_VALID_ACTION",
            "Not a valid an action indicator passed to the Spatial Inquiry ", action);

    }
    LOGGER.info("URL for Spatial Inquiry Polygon {}", url);
    return gisServiceUtil.submitPolygonRequest(url, featureMap, value, "INTERNAL", contextId);
  }

  @Override
  public Object deleteSpatialInqPolygonByObjId(String objectId, final String contextId) {

    LOGGER.info("Entering into deleteSpatialInqPolygonByObjId {}, Context Id {}", objectId,
        contextId);

    String url =
        String.format(POLYGON_URL_TEMPLATE, "eTrack_spatial_inquiry_poly", "deleteFeatures");

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
      LOGGER.info("URL for delete the Spatial Inquiry Polygon ById {}",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate.postForEntity(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, Object.class).getBody();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_DEL_SPATIAL_POLYGON_ERROR", ex.getResponseBodyAsString(),
          ex.getStatusCode(), ex);
    }
  }

  @Override
  public Object saveSpatialInqDetails(String userId, String contextId, String jwtToken,
      JsonNode spatialInqDetails) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    String url = UriComponentsBuilder.fromUriString("/etrack-permit/spatial-inquiry").toUriString();
    HttpEntity<JsonNode> entity = new HttpEntity<>(spatialInqDetails, httpHeaders);
    try {
      return eTrackOtherServiceRestTemplate.postForEntity(url, entity, JsonNode.class).getBody();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_SAVE_SPATIAL_ERROR", ex.getResponseBodyAsString(),
          ex.getStatusCode(), ex);
    }
  }

  @Override
  public String getSpatialPolygonByApplicationId(String spatialInquiryId, String contextId) {
    String query = "SI_ID='" + spatialInquiryId + "'";
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_spatial_inquiry_poly", "query");
    String uri = UriComponentsBuilder.fromUriString(url).queryParam("where", query)
        .queryParam("f", PJSON_FIELD).queryParam(OUTFIELDS_TEXT, "*").queryParam("outSR", "3857")
        .queryParam("returnCentroid", true).toUriString();

    try {
      LOGGER.info("getSpatialPolygonByApplicationId URL {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisInternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_SPATIAL_POLYGON_ERROR", ex.getResponseBodyAsString(),
          ex.getStatusCode(), ex);
    }
  }

  @Override
  public Object getSpatialInquiryDetails(String userId, String contextId, String jwtToken,
      Long inquiryId, final String requestorName) {
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("userId", userId);
      httpHeaders.add("contextId", contextId);
      if (StringUtils.hasLength(requestorName)) {
        httpHeaders.add("requestorName", requestorName);
      }
      httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
      String path = "/etrack-dart-db/spatial-inq/spatial-inquiry";
      String url = null;
      if (inquiryId != null && inquiryId > 0) {
        url = UriComponentsBuilder.fromUriString(path + "/" + inquiryId).toUriString();
      } else {
        url = UriComponentsBuilder.fromUriString(path).toUriString();
      }
      HttpEntity<JsonNode> entity = new HttpEntity<>(httpHeaders);
      return eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class)
          .getBody();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      return new GISException("GIS_GET_SPATIAL_INQ_ERROR", ex.getResponseBodyAsString(),
          ex.getStatusCode(), ex);
    } catch (Exception e) {
      return new GISException("GENERAL_ERROR", "General Error Occurred", e);
    }
  }

  @Override
  public GISServiceResponse saveSpatialInquiryResponseDetails(String userId, String contextId, String jwtToken,
      JsonNode spatialInqPolygonResponse) {

    LOGGER.info(
        "Entering into requesting GIS Service details to update the Inquiry response details. User Id {}, Context Id {}",
        userId, contextId);
    String url = String.format(POLYGON_URL_TEMPLATE, "eTrack_spatial_inquiry_poly", "applyEdits");
    try {
      LOGGER.info("saveSpatialInquiryResponseDetails URL {}, User Id {}, Context Id {} ",
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), userId, contextId);

      LOGGER.debug("GIS inquiry request to upload. User Id {}, Context Id {}, "
          + "Request {}", new ObjectMapper().writeValueAsString(spatialInqPolygonResponse));

      MultiValueMap<String, Object> featureMapDetails = new LinkedMultiValueMap<>();
      HttpHeaders formheaders = new HttpHeaders();
      formheaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<JsonNode> formJson = new HttpEntity<>(spatialInqPolygonResponse, formheaders);
      featureMapDetails.add("updates", formJson);
      
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<String> form = new HttpEntity<>(PJSON_FIELD, headers);
      featureMapDetails.set("f", form);
      
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      HttpEntity<MultiValueMap<String, Object>> entity =
          new HttpEntity<>(featureMapDetails, httpHeaders);
      GISServiceResponse response = gisInternalServiceRestTemplate.postForObject(
          URLDecoder.decode(url, StandardCharsets.UTF_8.name()), entity, GISServiceResponse.class);
      LOGGER.debug("Response from GIS after upload the inquiry response. User Id {}, Context Id {}, "
          + "Response {}", new ObjectMapper().writeValueAsString(response));
      if (response != null) {
        List<GISResponse> gisResponse =
            !CollectionUtils.isEmpty(response.getAddResults()) ? response.getAddResults()
                : response.getUpdateResults();
        if (!CollectionUtils.isEmpty(gisResponse)) {
          if (!gisResponse.get(0).isSuccess()) {
            throw new BadRequestException("GIS Error Code " + gisResponse.get(0).getError().getCode(),
                gisResponse.get(0).getError().getDescription(), spatialInqPolygonResponse);
          }
        }
      }
      return response;
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, url, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_INQ_RESP_UPLOAD_ERR", ex.getResponseBodyAsString(),
          ex.getStatusCode(), ex);
    } catch (Exception e) {
      throw new GISException("GIS_INQ_RESP_UPLOAD_GEN_ERR", "General error occurred while uploading the Inquiry response", e);
    }
  }
}
