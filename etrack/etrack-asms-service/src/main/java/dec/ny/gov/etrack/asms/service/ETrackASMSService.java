package dec.ny.gov.etrack.asms.service;

import org.springframework.http.ResponseEntity;

public interface ETrackASMSService {
  
  /**
   * Returns the user authorization details.
   * 
   * @param userId - User Id who initiates this request.
   * @param guid - Unique GUID assigned for the user.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the User and Authorization details.
   */
  ResponseEntity<Object> getUserAuthDetails(final String userId, final String guid,
      final String contextId);
  
  /**
   * Returns the User role(s) and Permission(s) associated with this user.
   * 
   * @param userId - User Id who initiates this request.
   * @param guid - Unique GUID assigned for the user.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the Roles and Permission(s) details.
   */
  ResponseEntity<Object> getRoles(String userId, String guid, String contextId);
}
