package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;
import dec.ny.gov.etrack.dart.db.service.ETrackQueryReportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/query")
public class ETrackQueryReportController {

  @Autowired
  private ETrackQueryReportService eTrackQueryReportService;
  
  /**
   * Retrieve the Submitted Projects report for the input request parameters.
   * 
   * @param userId - User who initiates this request.
   * @param projectSubmittalDataRetrieval - Values will be used to retrieve the matched criteria.
   * 
   * @return - Data results.
   */
  @PostMapping("/project-submittal")
  @ApiOperation(value="Retrieve the Project Submittal details retrieved based on the input parameters passed.")
  public Object generateProjectSubmittalReport(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestBody ProjectSubmittedRetrievalCriteria projectSubmittalDataRetrieval) {
    final String contextId = UUID.randomUUID().toString();
    return eTrackQueryReportService.retrieveProjectSubmittalDetails(userId, contextId, projectSubmittalDataRetrieval);
  }
  
  /**
   * Retrieve the Candidate Keyword details for the input query parameters
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - Candidate Keywords per region reports.
   */
  @GetMapping("/candidate-keywords")
  @ApiOperation(value="Retrieve the Candidate Keyword for the input region.")
  public Object retrieveCandidateKeywordDetailsReport(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    return eTrackQueryReportService.retrieveCandidateKeywordDetailsReport(userId, contextId);
  }
}
