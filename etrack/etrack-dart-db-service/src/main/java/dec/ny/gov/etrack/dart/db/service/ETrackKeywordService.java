package dec.ny.gov.etrack.dart.db.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.entity.KeywordCategory;
import dec.ny.gov.etrack.dart.db.entity.KeywordText;
import dec.ny.gov.etrack.dart.db.entity.PermitKeyword;
import dec.ny.gov.etrack.dart.db.entity.ProjectKeyword;
import dec.ny.gov.etrack.dart.db.model.PermitType;

@Service
public interface ETrackKeywordService {

  /**
   * Retrieve the Active Keyword texts from the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Key and Value of Category code and list of {@link KeywordText}
   */
  Map<String, List<KeywordText>> retrieveKeywordTexts(final String userId, final String contextId);

  /**
   * Retrieve the Active Keyword categories from the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return = list of {@link KeywordCategory}
   */
  List<KeywordCategory> retrieveKeywordCategories(final String userId, final String contextId);
  
  /**
   * Retrieve the Active Permit Keywords from the configuration.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param categoryId - Keyword Category id.
   * 
   * @return - list of {@link PermitKeyword}
   */
  List<PermitKeyword> retrievePermitKeywordTexts(final String userId, final String contextId, final Long categoryId);

  /**
   * Retrieve the keywords list and list of keywords selected for the project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project id.
   * 
   * @return - Returns the key and value of the list {@link ProjectKeyword}
   */
  Map<String, Map<String, List<ProjectKeyword>>> retrieveKeywordTextsForProject(final String userId, final String contextId,
      final Long projectId);

  /**
   * Retrieve the Active Keyword texts from the configuration for the input category id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param categoryId = Keyword Category id.
   * 
   * @return - Key and Value of Category code and list of {@link KeywordText}
   */

  Map<String, List<KeywordText>> retrieveKeywordTextsById(String userId, String contextId,
      Long categoryId);

  /**
   * Retrieve all the Active Permit types.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - the list of {@link PermitType}
   */
  List<PermitType> retrieveAllActivePermitTypes(String userId, String contextId);

  /**
   * Retrieve all the approved candidate keywords list.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the list of all the approved candidate keywords {@link ProjectKeyword}
   */
  List<ProjectKeyword> retrieveCandidateKeywordTexts(String userId, String contextId);
}
