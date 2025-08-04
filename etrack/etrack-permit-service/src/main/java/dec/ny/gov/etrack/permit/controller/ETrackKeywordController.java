package dec.ny.gov.etrack.permit.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.KeywordCategory;
import dec.ny.gov.etrack.permit.model.KeywordText;
import dec.ny.gov.etrack.permit.model.PermitKeyword;
import dec.ny.gov.etrack.permit.model.ProjectKeyword;
import dec.ny.gov.etrack.permit.service.ETrackKeywordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/keyword")
public class ETrackKeywordController {

  @Autowired
  private ETrackKeywordService keywordService;
  private static final Logger logger = LoggerFactory.getLogger(ETrackKeywordController.class.getName());
  
  /**
   * Store the Keyword text to the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param keywordText - Keyword text which needs to be stored/updated.
   */
  @ApiOperation(value = "Store/Update the Keyword text and retruns 200 status successfully")
  @PostMapping
  public void storeKeywordText(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final KeywordText keywordText) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId, contextId);
    keywordService.storeKeywordText(userId, contextId, keywordText);
  }

  /**
   * Store the Active Categories into the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param keywordCategory - Keyword category which needs to be stored/updated.
   */
  @ApiOperation(value = "Store/Update the Keyword Category and retruns 200 status successfully")
  @PostMapping("/category")
  public void storeKeywordCategory(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final KeywordCategory keywordCategory) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Keyword Category. User Id {}, Context Id {}", userId, contextId);
    keywordService.storeKeywordCategory(userId, contextId, keywordCategory);
  }


  /**
   * Store the Keyword for the Permit to the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param keywordCategory - Keyword category which needs to be stored/updated.
   */
  @ApiOperation(value = "Store/Update the Permit Keyword text and retruns 200 status successfully")
  @PostMapping("/permit")
  public void storePermitKeyword(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final PermitKeyword permitKeyword) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Permit Keyword. User Id {}, Context Id {}", userId, contextId);
    keywordService.storePermitKeyword(userId, contextId, permitKeyword);
  }
  
  /**
   * End point performs below Operations.
   *    1. Add the keyword and associated to the input project if its new keyword.
   *    2. Remove the keyword text from this associated project if the user wants remove.
   *    3. Remove the keyword text if its system detected from the input project if the user wants remove.
   *    4. Add the existing keyword text to the input project if the user wants to associate.
   *    
   * @param userId - User who initiates this request.
   * @param projectId - Project id.
   * @param associatedInd - Keyword text Indicator. 0 - not associate, 1- Associate.
   * @param projectKeyword - Object of {@link ProjectKeyword}
   * 
   * @return - Updated the Keyword text object of {@link ProjectKeyword}
   */
  @PostMapping({"/project/keyword", "/project/keyword/{associatedInd}"})
  @ApiOperation(value = "End point performs the below Operations."
      + "1. Add the keyword and associated to the input project if its new keyword.\n"
      + "2. Remove the keyword text from this associated project if the user wants remove.\n"
      + "3. Remove the keyword text if its system detected from the input project if the user wants remove.\n"
      + "4. Add the existing keyword text to the input project if the user wants to associate.")
  public Map<String, Map<String, List<ProjectKeyword>>> persistKeywords(
      @RequestHeader @ApiParam(example = "shortname", value = "User id of the logged in user") final String userId,  
      @RequestHeader @ApiParam(example = "1232", value = "Project Id.") final Long  projectId,
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken,
      @PathVariable(required = false) @ApiParam(example = "1", value = "Project associate Indicator.") final Integer associatedInd,
      @RequestBody ProjectKeyword projectKeyword) {
      
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into persist keywords. Associated Ind {}. User Id {}, Context Id {}", associatedInd,userId, contextId);
    return keywordService.persistKeywordTextToProject(userId, contextId, projectId, jwtToken, associatedInd, projectKeyword);
  }

  /**
   * End point to replace the list of Candidate keywords with the Permit related keyword.
   * 
   * @param userId - User who initiates this request.
   * @param replacementKeyword - Keyword which will be used as a replacement.
   * @param candidateKeywords - List of Candidate keywords to be replaced.
   */
  @PostMapping("replace-candidate")
  @ApiOperation(value = "Replace the Candidate keywords with the requested permit keyword.")
  public void replaceCandidateKeywordsWithPermitKeyword(
      @RequestHeader @ApiParam(example = "shortname", value = "User id of the logged in user") final String userId,  
      @RequestHeader @ApiParam(example = "1232", value = "Permit Keywords to be replaced") final Long  replacementKeyword,
      @RequestBody List<Long> candidateKeywords) {
      
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into replaceCandidateKeywordsWithPermitKeyword. User Id {}, Context Id {}", userId, contextId);
    if (CollectionUtils.isEmpty(candidateKeywords)) {
      throw new BadRequestException("NO_CANDIDATE_KEYWORDS_PASSED", "There is no Candidate keywords passed" , candidateKeywords);
    }
    if (replacementKeyword == null || replacementKeyword <= 0) {
      throw new BadRequestException("N__REPLACEMENTKEYWORD_PASSED", "There is no replacement keywords passed" , replacementKeyword);
    }
    keywordService.replaceCandidateKeywords(userId, contextId, candidateKeywords, replacementKeyword);
    logger.info("Exiting from replaceCandidateKeywordsWithPermitKeyword. User Id {}, Context Id {}", userId, contextId);
  }
}
