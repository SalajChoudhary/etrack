package dec.ny.gov.etrack.permit.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.model.SearchQueryDetail;
import dec.ny.gov.etrack.permit.service.ETrackSearchService;
import dec.ny.gov.etrack.permit.service.impl.ETrackPopulateSearchTableService;
import dec.ny.gov.etrack.permit.util.SearchLoadRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RestController
@RequestMapping("/search")
public class ETrackSearchController {
	
	 @Autowired
	 private ETrackSearchService etrackSearchService;
	 @Autowired
	 private ETrackPopulateSearchTableService tablePopulateService;
	 
	 private static final Logger logger = LoggerFactory.getLogger(ETrackSearchController.class.getName());
	 
	  /**
	   * End point performs below Operations.
	   *    1. Add the Search Query and associated to the input params Search Query and it's attributes to search.
	   *    2. Update the existing search query id by individual owner or Global Query Owner only.
	   *    3. Individual User = User Created by Other than System Admin 
	   *    4. Global User = System Admin 
	   *    
	   * @param userId - User who initiates this request.
	   * 
	   * @return - Updated the Search Query Condition  object of {@link searchQueryCondition}
	   */
	 
	 @PostMapping("/save-query")
	 @ApiOperation(value = "Store the Search Query  and retruns 200 status successfully")
	  public SearchQueryDetail storeSearchQuery(
		      @RequestHeader @ApiParam(example = "shortname",
		          value = "User id of the logged in user") final String userId,
		      @RequestBody final SearchQueryDetail searchQueryDto) {

		    final String contextId = UUID.randomUUID().toString();
		    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId, contextId);
		    etrackSearchService.storeSearchQuery(userId, contextId, searchQueryDto);
		    return etrackSearchService.getSearchQueryDetail(searchQueryDto.getQueryName());
		  }
	
	  /**
	   * End point performs below Operations.
	   *    Delete the Search Query and associated to the input params Search Query and it's attributes to search.
	   *    ->Individual User = User Created by Other than System Admin 
	   *    ->Global User = System Admin
	   *    
	   * @param userId - User who initiates this request.
	   *
	   */
	 
	 @DeleteMapping("/delete-search-query")
	 public void deleteSearchQuery( @RequestHeader @ApiParam(example = "shortname",
	          value = "User id of the logged in user") final String userId, 
			 @RequestParam(name = "searchQueryId") Long searchQueryConditonId) {
		 
		 @SuppressWarnings("unused")
		final String contextId = UUID.randomUUID().toString();
		    etrackSearchService.deleteSearchQuery(searchQueryConditonId);
	 }
	 
	  /**
	   * Retrieve Project Details or Document Details  by Query ID saved by User/Global(System Admin)
	   * 
	   * @param userId - User who initiates this request.
	   * 
	   * @return - List of Project Details or Document Details.
	   */

//	 @GetMapping("/search-table/details")
//	 @ApiOperation(value="Retrieve query run details")
//	 public void populateSearchTableDetails(  @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
//		 final String contextId = UUID.randomUUID().toString();
//		 logger.info("Entering into retrive search Results Table data. User Id {}, Context Id {}", userId, contextId);
//		 etrackSearchService.populateSearchToolsDetails(userId,contextId);
//	 }
	 
	 @PostMapping("/populate-search-table/{category}")
	 @ApiOperation(value="This is the On Demand request to refresh the Search Table details.")
	 public void populateSearchTableDetailsBasedOnCategory(
	     @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
	     @PathVariable @ApiParam(example = "DemandRequest", value="On Demand request") final SearchLoadRequest category) {
	   
	   String contextId = UUID.randomUUID().toString();
	   logger.info("Populate the Search Table details based on the On "
	       + "Demand request. User Id {}, Context Id {}", userId, contextId);
	   tablePopulateService.onDemandProcessToRefreshSearchResults(userId, contextId, category);
	 }
}
