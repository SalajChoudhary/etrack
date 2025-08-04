package gov.ny.dec.etrack.cache.dao;

import java.util.Map;

public interface EnterpriseConfigurationDAO {

  /**
   * Retrieve all the XTRA IDs, Program IDs and Special Attention codes configuration from enterprise.
   * 
   * @return - Result Set of these configurations.
   */
  Map<String, Object> retriveXTRAProgIdsAndSpecialAttnCodes();
}
