package gov.ny.dec.district.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.dart.entity.ApplicationNarrativeDetail;
import gov.ny.dec.district.dart.entity.District;

public interface DARTDistrictService {
  
  /**
   * Retrieve the matched District details for the requested district Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param districtId - District Id.
   * 
   * @return - Return the Matched District details with response.
   */
  ResponseEntity<DistrictDetail> getDistrictDetails(final String userId, String contextId, final Long districtId);
  
  /**
   * Retrieve the matched District details for the requested DEC Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param decId - DEC ID.
   * @return - Return the List of Matched District details with response.
   */
  ResponseEntity<List<District>> getDistrictDetailsByDecId(final String userId, String contextId, final String decId);
  
  /**
   * Retrieve the District details by the facility name and search type.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityName - Facility Name.
   * @param searchType - Search Type.
   * 
   * @return - Return the List of Matched District details with response.
   */
  ResponseEntity<List<District>> getDistrictDetailsByFacilityName(final String userId, String contextId, final String facilityName,
      final String searchType);
  
  /**
   * Retrieve the Application Narrative Description details associated with the District Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param districtId - District Id.
   * 
   * @return - List of Application Narrative Descriptions.
   */
  List<ApplicationNarrativeDetail> getApplicationNarrativeDescription(final String userId,
      final String contextId, final Long districtId);
  
  /**
   * Upload the DIMSR application into DART for the requested Project Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param guid - GUID of the requested user.
   */
  void uploadDIMSRApplication(final String userId, final String contextId, final Long projectId, final String guid);
  
  /**
   * Upload the eTrack application into DART for the requested Project Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param guid - GUID of the requested user.
   */
  void uploadETrackApplicationDetailsToDart(final String userId, final String contextId, final Long projectId, final String guid);
  
  /**
   * Refresh the milestone details by retrieving from enterprise and update into eTrack.
   */
  void refreshMilestoneStatus();
  
  /**
   * Refresh the milestone details by retrieving from enterprise and update into eTrack for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  void refreshMilestoneStatusByProjectId(final String userId, final String contextId, final Long projectId);
  
  /**
   * Adding additional Permit applications into DART.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applId - Additional Application Id.
   * @param guid - GUID of the requested user.
   */
  void addAdditionalPermitToDart(final String userId, final String contextId, final Long projectId, final Long applId, final String guid);
}
