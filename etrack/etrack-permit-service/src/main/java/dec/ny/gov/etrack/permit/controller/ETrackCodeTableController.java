package dec.ny.gov.etrack.permit.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dec.ny.gov.etrack.permit.model.MaintanenceCodeTable;
import dec.ny.gov.etrack.permit.service.ETrackCodeTableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RestController
@RequestMapping("/code-table")
public class ETrackCodeTableController {
	
	 @Autowired
	 private ETrackCodeTableService eTrackCodeTableService;
	 
	 private static final Logger logger = LoggerFactory.getLogger(ETrackCodeTableController.class.getName());
	 

	 @PostMapping("/system-parameter")
	 @ApiOperation(value = "Store the System Paramter  and retruns 200 status successfully")
	  public void updateSystemParameter(
		      @RequestHeader @ApiParam(example = "shortname",
		          value = "User id of the logged in user") final String userId,
		      @RequestBody final MaintanenceCodeTable maintanenceCodeTable) {

		    final String contextId = UUID.randomUUID().toString();
		    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId, contextId);
		    eTrackCodeTableService.updateSystemParameter(maintanenceCodeTable);
		  }
}
