package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.model.AdditionalPermitDetail;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;

@Service
public interface DartPermitService {

  /**
   * Retrieve the Permit application assignment details.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Map of the Contact/Agent assign to the permit form(s).
   */
  Map<String, Object> retrievePermitsAssignment(String userId, String contextId, Long projectId);

  /**
   * Retrieve all the Permit applications applied so far for the the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param permitSummaryInd - indicates whether the permit needs to be bundled with Contact/Agent or not.
   * 
   * @return - Permit Applications group by category.
   */
  Map<String, Object> retrieveAllPermitApplications(
      final String userId, final String contextId, final Long projectId, final boolean permitSummaryInd);
  

  /**
   * Retrieve all the permit summary applied for this project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param token - JWT Token.
   * @param projectId - Project Id.
   * 
   * @return - Permit summary
   */
  Object retrieveAllPermitSummary(final String userId, final String contextId, final String token, Long projectId);

  /**
   * Retrieve the list of Modification requested Permits for the input Project Id.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - List of Modify requested applications.
   */
  List<PermitApplication> retrievePermitModificationSummary(String userId, String contextId, Long projectId);
  
  /**
   * Returns the list of permits not applied and can be added as an additional.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @return - Additional Permit details.
   */
  AdditionalPermitDetail retrieveAvailablePermitsAddAsAdditional(String userId, String contextId, Long projectId);
}
