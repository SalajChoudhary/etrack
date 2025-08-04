package dec.ny.gov.etrack.permit.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.FoilRequest;
import dec.ny.gov.etrack.permit.model.LitigationRequest;

@Service
public interface ETrackFoilLigitationService {

  /**
   * Save or Update the FOIL request for the input project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param foilRequest - FOIL Request details.
   * 
   * @return - List of updated FOIL request number.
   */
  List<String> saveOrUpdateFoilRequest(String userId, String contextId, Long projectId,
      FoilRequest foilRequest);
  
  /**
   * Save or update the litigation hold request for the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param litigationRequest - Litigation request to save or update.
   * 
   * @return  - Litigation and History details. 
   */
  Map<String, Object> saveOrUpdateLitigationRequest(String userId, String contextId, Long projectId,
      LitigationRequest litigationRequest);
}
