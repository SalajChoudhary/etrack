package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dec.ny.gov.etrack.dart.db.service.ETrackSearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/search")
public class ETrackSearchController {
	
	 @Autowired
	 private ETrackSearchService etrackSearchService;
	 
	 private static final Logger logger = LoggerFactory.getLogger(ETrackSearchController.class.getName());
	  
	  /**
	   * Retrieve the Search By Attribute details for all the Search Entites
	   * 
	   * @param userId - User who initiates this request.
	   * 
	   * @return - Search Attribute Details with Operator type.
	   */
	  
	  @GetMapping("/search-by-attributes")
	  @ApiOperation(value="Retrieve All the Search Attributes.")
	  public Object retriveSearchByAttributes(
	      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
	    final String contextId = UUID.randomUUID().toString();
	    logger.info("Entering into search by attributes. User Id {}, Context Id {}", userId, contextId);
	    return etrackSearchService.retrieveSearchByAttribute(userId, contextId);
	  }
	
	  /**
	   * Retrieve List of all Available searches  saved by User/Global(System Admin)
	   * 
	   * @param userId - User who initiates this request.
	   * 
	   * @return - Available searches List.
	   */
	  @GetMapping("/available-searches")
	  @ApiOperation(value="Retrieve All the Available Searches.")
	  public Object retriveAllAvailableSearches(
	      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
	    final String contextId = UUID.randomUUID().toString();
	    logger.info("Entering into search by attributes. User Id {}, Context Id {}", userId, contextId);
	    return etrackSearchService.retrieveAvailableSearches(userId, contextId);
	  }
	  
	  
	  /**
	   * Retrieve Search by Query ID saved by User/Global(System Admin)
	   * 
	   * @param userId - User who initiates this request.
	   * 
	   * @return - List of Search Criteria for Query Name.
	   */
	  @GetMapping("/available-searches/{queryId}")
	  @ApiOperation(value="Retrieve by Search Query ID.")
	  public Object retrieveSearchConditionByQueryId(
	      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
	      @PathVariable @ApiParam(example = "1", value="Search Query id") final Long queryId) {
	    final String contextId = UUID.randomUUID().toString();
	    logger.info("Entering into retrive Search Condition  by Query ID. User Id {}, Context Id {}", userId, contextId, queryId);
	    return etrackSearchService.retrieveSearchConditionByQueryId(userId, contextId, queryId);
	  }
	  
	  
	  
	  /**
	   * Retrieve Project Details or Document Details  by Query ID saved by User/Global(System Admin)
	   * 
	   * @param userId - User who initiates this request.
	   * 
	   * @return - List of Project Details or Document Details.
	   */
	  
	 @GetMapping("/run-query/{queryId}")
	 @ApiOperation(value="Retrieve query run details")
	 public Object retriveRunQueryDetails(  @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
		      @PathVariable @ApiParam(example = "1", value="Search Query id") final Long queryId) {
		 final String contextId = UUID.randomUUID().toString();
		 logger.info("Entering into retrive Results Table Data by Query ID. User Id {}, Context Id {}", userId, contextId, queryId);
		 return etrackSearchService.retriveRunQueryDetails(userId, queryId);
	 }

	  

}
