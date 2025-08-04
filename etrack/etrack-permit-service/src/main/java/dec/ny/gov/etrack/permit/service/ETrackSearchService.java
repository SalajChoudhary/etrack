package dec.ny.gov.etrack.permit.service;

import org.springframework.stereotype.Service;

import dec.ny.gov.etrack.permit.model.SearchQueryDetail;

@Service
public interface ETrackSearchService {
	

	  /**
	   * This method to stores/update the Search Query Condition.
	   * 
	   * @param userId - User who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * @body search Query Condition - Attributes and it's details.
	   */
	void storeSearchQuery(String userId, String contextId, SearchQueryDetail searchQueryDTO);
	
	  /**
	   * Remove the search Query ID and it's attributes stored.
	   * @param userId - User Id who initiates this request.
	   * @param contextId - Unique UUID to track this request.
	   * @param searchQueryId - Requested query Id to be deleted.
	   */
	void deleteSearchQuery(Long searchQueryConditonId);

	SearchQueryDetail getSearchQueryDetail(String queryName);

}
