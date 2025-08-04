package dec.ny.gov.etrack.gis.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.DataNotFoundException;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.exception.URLInvalidException;
import dec.ny.gov.etrack.gis.model.AddressCandidate;
import dec.ny.gov.etrack.gis.service.GISAddressRetrieveService;

@Service
public class GISAddressRetrieveServiceImpl implements GISAddressRetrieveService {

  @Autowired
  @Qualifier("gisITSServiceRestTemplate")
  private RestTemplate gisITSServiceRestTemplate;
  @Autowired
  @Qualifier("geoCodeServiceRestTemplate")
  private RestTemplate geoCodeServiceRestTemplate;
  @Autowired
  @Qualifier("gisExternalServiceRestTemplate")
  private RestTemplate gisExternalServiceRestTemplate;

  private static final String ESRI_ADDRESS_API_SERVER =
      "/World/GeocodeServer/findAddressCandidates?outFields=*&maxLocations=10&f=pjson&outSR=4326&countryCode=USA&";
  private static final String ITS_ADDRESS_API_SERVER = 
      "/Locators/Street_and_Address_Composite/GeocodeServer/findAddressCandidates?f=json&inSR=26918&outSR=4326&";
  private static final String TAX_REST_API_URL =
      "/EAF/EAF_Mapper/MapServer/1/query?f=json&outFields=*&outSR=4326&";
  private static final String COUNTIES_REST_API_URL =
      "/dil/dil_reference/MapServer/2/query?f=json&outFields=NAME&returnGeometry=false&where=1%3D1&orderByFields=NAME";
  private static final String MUNICIPALITIES_REST_API_URL =
      "/NYS_Civil_Boundaries/FeatureServer/6/query?f=pjson&resultRecordCount=100&orderByFields=NAME&returnGeometry=false&";
  private static final String INVALID_URL_LOG_INFO =
      "URL {} contains invalid encoded value. Error Message {}";
  private static final String OUTFIELDS_TEXT = "outFields";
  private static final Logger LOGGER = LoggerFactory.getLogger(GISAddressRetrieveServiceImpl.class.getName());
  
  @Override
  public String getITSAddresses(final String address, final String contextId) {
    String uri = UriComponentsBuilder.fromUriString(ITS_ADDRESS_API_SERVER)
        .queryParam("SingleLine", address).toUriString();
    try {
      LOGGER.debug("ITS address URL ::  {}", URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisITSServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_ITS_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public String getEsriAddresses(final String address, final String postal, final String city,
      final String contextId) {
    String uri = UriComponentsBuilder.fromUriString(ESRI_ADDRESS_API_SERVER)
        .queryParam("Region", "NY").queryParam("Address", address)
        .queryParam(OUTFIELDS_TEXT, "Addr_type").queryParam("City", city)
        .queryParam("forStorage", false).queryParam("postal", postal).toUriString();
    try {
      LOGGER.info("ESRI address URL ::  {}", URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return geoCodeServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_ESRI_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public Object getCounties(final String contextId) {
    LOGGER.info("Entering into getCounties");
    try {
      return gisExternalServiceRestTemplate.getForObject(
          URLDecoder.decode(COUNTIES_REST_API_URL, StandardCharsets.UTF_8.name()), Object.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, COUNTIES_REST_API_URL, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_COUNTIES_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public Object getTaxParcel(final String taxParcelID, final String countyName,
      final String municipalName, final String contextId) {
    String query = null;
    if (StringUtils.hasLength(municipalName)) {
      query = "PRINT_KEY ='" + taxParcelID + "' AND COUNTY_NAME='" + countyName
          + "' AND MUNI_NAME='" + municipalName + "'";
    } else {
      query = "SBL ='" + taxParcelID + "' AND COUNTY_NAME='" + countyName + "'";
    }
    String uri = UriComponentsBuilder.fromUriString(TAX_REST_API_URL).queryParam("where", query)
        .queryParam(OUTFIELDS_TEXT, "*").queryParam("returnCentroid", true).toUriString();
    try {
      LOGGER.info("Tax parcel URL {}", URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisExternalServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), Object.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_TAXPARCEL_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public String getMuncipalities(final String countyName, final String contextId) {
    String query = "COUNTY='" + countyName + "'";
    String uri = UriComponentsBuilder.fromUriString(MUNICIPALITIES_REST_API_URL)
        .queryParam("where", query).queryParam(OUTFIELDS_TEXT, "NAME,MUNITYCODE").toUriString();

    try {
      LOGGER.info("Municipalities URL {}", URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      return gisITSServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_MUNICIPALITIES_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public Object getAddressDetails(final String userId, final String contextId,
      final Map<String, String> addressParam) {

    LOGGER.info("Entering into getAddressDetails. User Id {} Context Id{}", userId, contextId);
    String uri = null;
    try {
      String address =
          addressParam.get("streetAddress1").trim() + " " + addressParam.get("zipCode").trim();

      uri = UriComponentsBuilder.fromUriString(ESRI_ADDRESS_API_SERVER)
          .queryParam("SingleLine", address).toUriString();

      LOGGER.debug("ITS address lookup URL ::  {}",
          URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
      String response = geoCodeServiceRestTemplate
          .getForObject(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), String.class);
      AddressCandidate addressCandidate =
          new ObjectMapper().readValue(response, AddressCandidate.class);
      LOGGER.debug("Response from address look up service {}", response);

      if (!CollectionUtils.isEmpty(addressCandidate.getCandidates())) {
        JsonNode jsonNode = addressCandidate.getCandidates().get(0);
        JsonNode score = jsonNode.get("score");
        if (score != null && score.asDouble() >= 70) {
          jsonNode = jsonNode.get("attributes");
          Map<String, Object> result = new HashMap<>();
          if (jsonNode.get("City") != null) {
            result.put("city", jsonNode.get("City").asText());
          }

          if (jsonNode.get("RegionAbbr") != null) {
            result.put("state", jsonNode.get("RegionAbbr").asText());
          }
          result.put("streetAddress1", addressParam.get("streetAddress1").trim());
          result.put("zip", addressParam.get("zipCode").trim());
          return result;
        } else {
          LOGGER.error("Address Look up score is lower than 70. "
              + "So, need to return not found {} . Address details {}", score, addressParam);
          throw new DataNotFoundException("ADR_MATCH_SCORE_LOW", 
              "Address match score is very low. Result won't be returned");
        }
      } else {
        throw new DataNotFoundException("NO_ADR_MATCH_FOUND", 
            "There is no matching address found for the input address.");
      }
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new URLInvalidException(e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_RET_ADR_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    } catch (DataNotFoundException e) {
      throw e;
    }  catch (Exception e) {
      LOGGER.error(INVALID_URL_LOG_INFO, uri, e);
      throw new GISException("GIS_RET_ADR_GEN_ERROR", "General error while retrieving the address details", e);
    }
  }

}
