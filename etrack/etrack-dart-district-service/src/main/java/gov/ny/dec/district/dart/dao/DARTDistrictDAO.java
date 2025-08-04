package gov.ny.dec.district.dart.dao;

import java.util.List;
import gov.ny.dec.district.dart.entity.ApplicationNarrativeDetail;
import gov.ny.dec.district.dart.entity.District;

public interface DARTDistrictDAO {
  
  /**
   * Search the district(s) by DEC Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param decId - DEC ID.
   * 
   * @return - List of Matched District(s).
   */
  List<District> searchDistrictDetailByDecId(final String userId, final String contextId, final String decId);
  
  /**
   * Search the district(s) by Facility name and Search Type.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param facilityName - Facility name.
   * @param searchType - Search Type.
   * 
   * @return - List of Matched District(s).
   */
  List<District> searchDistrictDetailByFacilityName(final String userId, final String contextId, 
      final String facilityName, final String searchType);
  
  /**
   * Retrieve the Application Permit Description narrative for the input district id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param districtId - District Id.
   * 
   * @return - List of Application Narrative Description.
   */
  List<ApplicationNarrativeDetail> retrieveApplicationPermitDescNarrative(final String userId, final String contextId,
      final Long districtId);
  
  /**
   * Upload the DIMSR application into DART(enterprise).
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param guid - GUID associated with the requested user.
   */
  void uploadDIMSRApplicationDetailsToDart(final String userId, final String contextId, final Long projectId,
      final String guid);
  
  /**
   * Upload the eTrack applications into DART.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param guid - GUID associated with the requested user.
   */
  void uploadETrackDataToDart(final String userId, final String contextId, final Long projectId, final String guid);
  
  /**
   * Retrieve the Milestone related details from DART and upload into eTrack for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   */
  void refreshMilestone(final String userId, final String contextId, final Long projectId);
  
  /**
   * Add additional permits into DART. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applId - eTrack Application Id.
   * @param guid - GUID associated with the requested user.
   */
  void addAdditionalPermitToDart(final String userId, final String contextId, final Long projectId, final Long applId, final String guid);
}
