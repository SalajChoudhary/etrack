package dec.ny.gov.etrack.gis.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.gis.exception.GISException;
import dec.ny.gov.etrack.gis.model.ProjectDetail;
import dec.ny.gov.etrack.gis.service.GISFacilityService;

@Service
public class GISFacilityServiceImpl implements GISFacilityService {

  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;

  private static final Logger LOGGER = LoggerFactory.getLogger(GISFacilityServiceImpl.class.getName());
  
  
  @Override
  public ProjectDetail saveFacilityDetail(String userId, String contextId, String jwtToken,
      ProjectDetail projectDetail) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    String url = UriComponentsBuilder.fromUriString("/etrack-permit/project").toUriString();
    HttpEntity<ProjectDetail> entity = new HttpEntity<>(projectDetail, httpHeaders);
    try {
      return eTrackOtherServiceRestTemplate.postForEntity(url, entity, ProjectDetail.class).getBody();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_FACILITY_SAVE_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public ProjectDetail updateFacilityDetail(String userId, String contextId, String jwtToken,
      ProjectDetail projectDetail) {
    LOGGER.info("Entering into updateFacilityDetail. User Id {}, Context Id {}", userId, contextId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    String url = UriComponentsBuilder.fromUriString("/etrack-permit/project").toUriString();
    HttpEntity<?> entity = new HttpEntity<>(projectDetail, httpHeaders);
    try {
      ResponseEntity<ProjectDetail> updateFacilityDetailResponse =
          eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.PUT, entity, ProjectDetail.class);
      LOGGER.info(
          "Exiting from updateFacilityDetail. User Id {}, Context Id {}, Response status {}, Response body {}",
          userId, contextId, updateFacilityDetailResponse.getStatusCode(),
          updateFacilityDetailResponse.getBody());
      return updateFacilityDetailResponse.getBody();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_FACILITY_UPD_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public ResponseEntity<ProjectDetail> retrieveFacilityInfo(String userId, String contextId,
      String jwtToken, Long projectId) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    httpHeaders.add("projectId", String.valueOf(projectId));
    String url = UriComponentsBuilder.fromUriString("/etrack-permit/project").toUriString();
    HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
    try {
      return eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.GET, entity,
          ProjectDetail.class);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("ETRACK_RET_FAC_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

  @Override
  public ResponseEntity<JsonNode> retrieveFacilityHistory(String userId, String contextId,
      String jwtToken, Long projectId) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", userId);
    httpHeaders.add("contextId", contextId);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, jwtToken);
    httpHeaders.add("projectId", String.valueOf(projectId));
    String url = UriComponentsBuilder.fromUriString("/etrack-dart-db/facility/view").toUriString();
    HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
    try {
      return eTrackOtherServiceRestTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new GISException("GIS_RET_FAC_HIST_ERROR", ex.getResponseBodyAsString(), ex.getStatusCode(), ex);
    }
  }

}
