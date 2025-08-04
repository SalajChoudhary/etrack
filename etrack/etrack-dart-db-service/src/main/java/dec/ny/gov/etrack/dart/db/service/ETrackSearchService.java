package dec.ny.gov.etrack.dart.db.service;

import org.springframework.stereotype.Service;

@Service
public interface ETrackSearchService {
	
	  /**
	   * Retrieve all the Attribute for Search Tool .
	   * 
	   * @param userId - User who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * 
	   * @return - the list of {@link SearchByAttribute}
	   */
	Object retrieveSearchByAttribute(String userId, String contextId);
	
	/**
	   * Retrieve all the Available Searches .
	   * 
	   * @param userId - User who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * 
	   * @return - the list of {@link SearchAttribute}
	   */
	Object retrieveAvailableSearches(String userId, String contextId);
	
	/**
	   * Retrieve List of Search Criteria for theQuery Name by Query ID.
	   * 
	   * @param userId - User who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * 
	   * @return - the list of {@link SearchAttribute}
	   */
	Object retrieveSearchConditionByQueryId(String userId, String contextId, Long queryId);

	/**
	   * Retrieve Result for the Search Criteria for the Query Name by Query ID.
	   * 
	   * @param userId - User who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * 
	   * @return - the list of {@link QueryResults}
	   */
	Object retriveRunQueryDetails(String userId, Long queryId);
}

