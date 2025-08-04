package dec.ny.gov.etrack.gis.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import dec.ny.gov.etrack.gis.model.ProjectDetail;

@Service
public interface GISFacilityService {

  /**
   * Store the request facility details into eTrack.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param projectDetail - Project/Facility details.
   * 
   * @return - Updated Project details with newly created Project Id.
   */
  ProjectDetail saveFacilityDetail(final String userId, final String contextId,
      final String jwtToken, ProjectDetail projectDetail);

  /**
   * Retrieve the Project and Facility information for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Project details.
   */
  ResponseEntity<ProjectDetail> retrieveFacilityInfo(final String userId, final String contextId, final String jwtToken,
      final Long projectId);

  /**
   * Update the Project facility details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param projectDetail - Project details.
   * 
   * @return - Updated Project details informations.
   */
  ProjectDetail updateFacilityDetail(String userId, String contextId, String jwtToken,
      ProjectDetail projectDetail);

  /**
   * Retrieve the Facility history for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Facility History details.
   */
  ResponseEntity<JsonNode> retrieveFacilityHistory(String userId, String contextId, String jwtToken, Long projectId);
  

}
