package dec.ny.gov.etrack.dart.db.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;

@Service
public interface DartFacilityService {

  /**
   * Retrieve the DEC Id for the input Program Id and Program Type from DART System.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param programId - Program Id.
   * @param programType - Program Type.
   * 
   * @return - Returns the DEC Id and associate Facility details.
   */
  FacilityAddress getDECIDByProgramType(final String userId, final String contextId,
      final String programId, final String programType);

  /**
   * Retrieve the matching DEC Id for the input parameters Tax Map, County and Municipality. 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param txmap - Tax Map number.
   * @param county - County
   * @param municipality - Municipality.
   * 
   * @return - DEC ID.
   */
  Object getDECIDByTaxMap(String userId, String contextId, String txmap, final String county, final String municipality);

  /**
   * Returns the Facility details for the input Project Id from eTrack.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Returns the Facility details.
   */
  FacilityDetail getEtrackFacilityDetails(final String userId, final String contextId,
      final Long projectId);

  /**
   * Returns all the matched facilities from DART.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param addrLine1 - Facility Address line 1.
   * @param city - Facility City.
   * 
   * @return - List of Facility details.
   */
  Object getAllMatchedFacilities(final String userId, final String contextId, String addrLine1,
      String city);

  /**
   * Retrieve the existing facility details from before the user do any amendment which helps to compare the changes.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @return - Initial Facility details to compare the 
   */
  ResponseEntity<Object> retrieveFacilityHistory(final String userId, String contextId, Long projectId);
  

}
