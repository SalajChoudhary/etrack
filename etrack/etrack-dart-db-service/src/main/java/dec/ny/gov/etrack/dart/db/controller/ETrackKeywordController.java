package dec.ny.gov.etrack.dart.db.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.entity.KeywordCategory;
import dec.ny.gov.etrack.dart.db.entity.KeywordText;
import dec.ny.gov.etrack.dart.db.entity.PermitKeyword;
import dec.ny.gov.etrack.dart.db.entity.ProjectKeyword;
import dec.ny.gov.etrack.dart.db.model.PermitType;
import dec.ny.gov.etrack.dart.db.service.ETrackKeywordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/keyword")
public class ETrackKeywordController {

  @Autowired
  private ETrackKeywordService keywordService; 
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackKeywordController.class.getName());
  
  /**
   * Retrieve the Active Keyword texts from the configuration.
   *  
   * @param userId - User who initiates this request.
   * 
   * @return - List of {@link KeywordText}
   */
  @ApiOperation(value="Retrieve the Keyword texts from the configuration table.")
  @GetMapping
  public Map<String, List<KeywordText>> retrieveKeywordTexts(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve keyword texts. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveKeywordTexts(userId, contextId);
  }
  
  /**
   * Retrieve the Active Keyword texts from the configuration for the input category id.
   *  
   * @param userId - User who initiates this request.
   * 
   * @return - List of {@link KeywordText}
   */
  @ApiOperation(value="Retrieve the Keyword texts from the configuration table.")
  @GetMapping("/{categoryId}")
  public Map<String, List<KeywordText>> retrieveKeywordTextsByCategoryId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @PathVariable @ApiParam(example = "1", value="Keyword Category id") final Long categoryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve keyword texts. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveKeywordTextsById(userId, contextId, categoryId);
  }


  /**
   * Retrieve the Active Categories from the configuration.
   *  
   * @param userId - User who initiates this request.
   * 
   * @return - List of {@link KeywordCategory}
   */
  @ApiOperation(value="Retrieve the Keyword categories.")
  @GetMapping("/category")
  public List<KeywordCategory> retrieveKeywordCategories(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve keyword categories. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveKeywordCategories(userId, contextId);
  }

  /**
   * Retrieve the Active Permit Keyword texts from the configuration.
   *  
   * @param userId - User who initiates this request.
   * 
   * @return - List of {@link PermitKeyword}
   */
  @ApiOperation(value="Retrieve the Permit Keyword texts from the configuration table.")
  @GetMapping({"/permit", "/permit/{categoryId}"})
  public List<PermitKeyword> retrievePermitKeywordTexts(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @PathVariable(required = false) @ApiParam(example = "1", value="Keyword Category id") final Long categoryId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Permit keyword details. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrievePermitKeywordTexts(userId, contextId, categoryId);
  }
  
  /**
   * Retrieve the all the keywords associated with Project and also can be associated in future.
   *  
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - Key and value of categorized Keywords like Candidate, System and Permits of the list  of {@link ProjectKeyword}
   */
  @ApiOperation(value="Retrieve the Permit Keyword texts from the configuration table.")
  @GetMapping("/project")
  public Map<String, Map<String, List<ProjectKeyword>>> retrieveKeywordTextsForProject(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "2132", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve Project keywords associated and available keywords. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveKeywordTextsForProject(userId, contextId, projectId);
  }
  
  /**
   * Retrieve the all the Approved Candidate keywords to display in the maintenance for review and replace.
   *  
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - Key and value of categorized Keywords like Candidate, System and Permits of the list  of {@link ProjectKeyword}
   */
  @ApiOperation(value="Retrieve all the approved keywords to display in the Maintenance section for the staff to review and replace if any keywords can be.")
  @GetMapping("/candidate")
  public List<ProjectKeyword> retrieveCandidateKeywordTexts(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve all the approved Candidate keywords "
        + "to display in the maintenance section. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveCandidateKeywordTexts(userId, contextId);
  }

  /**
   * Retrieve the all the permit types for the maintenance table configuration.
   *  
   * @param userId - User who initiates this request.
   * 
   * @return - List of {@link PermitType}
   */
  @ApiOperation(value="Retrieve the Permit Keyword texts from the configuration table.")
  @GetMapping("/permitTypes")
  public List<PermitType> retrievePermitTypes(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve all the active Permit Types "
        + "for keyword maintenance configuration. User Id {}, Context Id {}", userId, contextId);
    return keywordService.retrieveAllActivePermitTypes(userId, contextId);
  }
}
