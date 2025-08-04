package dec.ny.gov.etrack.gis.util;

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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.GISResponse;
import dec.ny.gov.etrack.gis.model.GISServiceResponse;

@Component
public class GISServiceUtil {

  @Autowired
  @Qualifier("gisInternalServiceRestTemplate")
  private RestTemplate gisInternalServiceRestTemplate;

  @Autowired
  @Qualifier("gisExternalServiceRestTemplate")
  private RestTemplate gisExternalServiceRestTemplate;

  private static final String PJSON_FIELD = "pjson";
  private static final String INVALID_URL_LOG_INFO = 
      "URL {} contains invalid encoded value. Error Message {}";

  private static final Logger LOGGER = LoggerFactory.getLogger(GISServiceUtil.class.getName());
  
  /**
   * Request GIS service to store the Polygon in GIS.
   *  
   * @param url - GIS Path to be used to store the polygon.
   * @param featureMap - Feature Map.
   * @param value - Value (pson)
   * @param gisEnvInd - GIS Environment Indicator. EXTERNAL/INTERNAL.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return  - GIS response after submission.
   */
  public Object submitPolygonRequest(String url, List<Object> featureMap, String value,
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

}
