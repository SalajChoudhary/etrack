package gov.ny.dec.etrack.cache.dao;

import java.util.List;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeAndSubTypeEntity;

public interface SWFacilityTypeDAO {

  /**
   * Retrieve the Solid Waste Facility Type.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Solid Waste Facility Type.
   */
  List<SWFacilityTypeAndSubTypeEntity> getSWFacilityType(final String userId, final String contextId);
}
