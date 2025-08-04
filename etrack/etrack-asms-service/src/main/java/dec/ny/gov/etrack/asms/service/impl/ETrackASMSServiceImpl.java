package dec.ny.gov.etrack.asms.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.asms.handler.ASMSResponseHandler;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects;
import dec.ny.gov.etrack.asms.schema.SchemaUser;
import dec.ny.gov.etrack.asms.service.ETrackASMSService;

@Service
public class ETrackASMSServiceImpl implements ETrackASMSService {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ASMSResponseHandler handler;

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackASMSServiceImpl.class.getName());
  
  @Value("${asms.interface.url}")
  private String asmsURI;

  @Override
  public ResponseEntity<Object> getUserAuthDetails(final String userId, final String guid,
      final String contextId) {

    try {
      logger.info("Entering into getSecurityObjects Context Id : {}", contextId);
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
//      String urlString = UriComponentsBuilder.newInstance().pathSegment("ETR", guid).build().toString();
      String urlString = UriComponentsBuilder.newInstance().pathSegment("user", guid).build().toString();
//          .buildAndExpand("ETR", guid).toUriString();
      ResponseEntity<SchemaUser> userResponse = restTemplate.getForEntity(urlString, SchemaUser.class);
      
      if (userResponse.getBody() == null) {
        logger.info("Response body is empty . Context Id {} ", contextId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      Object object = handler.transformASMSUserResponse(contextId, userId, userResponse.getBody());
      return new ResponseEntity<Object>(object, userResponse.getStatusCode());
    } catch (HttpClientErrorException e) {
      HttpStatus errorStatusCode = e.getStatusCode();
      String errroResponse = e.getResponseBodyAsString();
      logger.error("Response status code is not success. code {} . Error response body {}",
          errorStatusCode, errroResponse);

      if (HttpStatus.NOT_FOUND.equals(errorStatusCode) && !StringUtils.isEmpty(errroResponse)
          && errroResponse.contains("User GUID not found")) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        return new ResponseEntity<>(e.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("General error while requesting ASMS Service {}", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
 
  /**
   * URL encode the GUID.
   * 
   * @param guid the GUID to encode
   * @return the URL encoded GUID
   * @throws IllegalArgumentException if the character encoding is not supported
   */
  private String encodeGuid(final String guid, final String charEncoding) throws IllegalArgumentException {
      try {
          return URLEncoder.encode(guid, charEncoding);
      } catch (UnsupportedEncodingException e) {
          logger.error("Error URL encoding the GUID. Error: {}", e);
          throw new IllegalArgumentException("Error URL encoding the GUID, unsupported character encoding.", e);
      }
  }
  
  
  @Override
  public ResponseEntity<Object> getRoles(final String userId, final String guid,
      final String contextId) {
    
    logger.info("Entering into getRoles Context Id : {}", contextId);
    try {
      Map<String, String> parameters = new HashMap<>();
      String valueEncoded = encodeGuid(guid, "UTF-8");
      parameters.put("guid", valueEncoded);
      String uri = asmsURI + "object/eTrack?guid={guid}";
//      RestTemplate authInfoRestTemplate = new RestTemplate();
//      DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
//      defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
//      authInfoRestTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
      ResponseEntity<SchemaSecurityObjects> responseEntity = restTemplate.getForEntity(
          uri, SchemaSecurityObjects.class, parameters);
      if (responseEntity.getBody() == null) {
        logger.info("Response body is empty . Context Id {} ", contextId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      logger.debug("Security Schema Objects {}", new ObjectMapper().writeValueAsString(responseEntity.getBody()));
      Object authResponse = handler.handleASMSObjectAuthResponse(contextId, userId, responseEntity.getBody());
      return new ResponseEntity<Object>(authResponse, HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      HttpStatus errorStatusCode = e.getStatusCode();
      String errroResponse = e.getResponseBodyAsString();
      logger.error("Response status code is not success. code {} . Error response body {}",
          errorStatusCode, errroResponse);

      if (HttpStatus.NOT_FOUND.equals(errorStatusCode) && !StringUtils.isEmpty(errroResponse)
          && errroResponse.contains("User GUID not found")) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        return new ResponseEntity<>(e.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("General error while requesting ASMS Service {}", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
