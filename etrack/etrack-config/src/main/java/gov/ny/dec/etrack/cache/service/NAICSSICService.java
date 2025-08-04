package gov.ny.dec.etrack.cache.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import gov.ny.dec.etrack.cache.entity.SICCodes;

@Service
public interface NAICSSICService {
  
  /**
   * Retrieve NAICS Codes and descriptions for the input SIC Code.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param sicCode - SIC Code.
   * 
   * @return {@link Map}
   */
  Map<String, String> getNAICSCodes(final String userId, final String contextId, final String sicCode);
  
  /**
   * Returns the list of SIC codes.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return {@link List}
   */
  List<SICCodes> getSICCodes(final String userId, final String contextId);
}
