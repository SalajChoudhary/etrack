package gov.ny.dec.etrack.cache.dao;

import java.util.List;
import gov.ny.dec.etrack.cache.entity.PermitType;

public interface PermitTypeDAO {
  
  /**
   * Retrieve all the Permit Types for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Returns the list of Permit Types.
   */
  List<PermitType> findAllPermitTypesByProjectId(
      final String userId, final String contextId, final Long projectId);


  List<PermitType> findAllActivePermitTypesByProjectId(final String userId, final String contextId,
                                                       final Long projectId);


}
