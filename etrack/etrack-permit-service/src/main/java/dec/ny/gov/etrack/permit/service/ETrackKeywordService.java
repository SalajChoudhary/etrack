package dec.ny.gov.etrack.permit.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.entity.ProjectKeywordEntity;
import dec.ny.gov.etrack.permit.model.KeywordCategory;
import dec.ny.gov.etrack.permit.model.KeywordText;
import dec.ny.gov.etrack.permit.model.PermitKeyword;
import dec.ny.gov.etrack.permit.model.ProjectKeyword;

@Service
public interface ETrackKeywordService {

  /**
   * This method to stores/update the keyword text.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param keywordText - Keyword text details.
   */
  void storeKeywordText(final String userId, final String contextId, KeywordText keywordText);
  
  /**
   * This method to stores/update the keyword category into configuration table.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param keywordCategory - Keyword category details.
   */
  void storeKeywordCategory(final String userId, final String contextId, KeywordCategory keywordCategory);
  
  /**
   * This method to stores/update the permit keyword into configuration table.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param permitKeyword - Permit Keyword details.
   */
  void storePermitKeyword(final String userId, final String contextId, PermitKeyword permitKeyword);

  /**
   * Persist (Add, remove, Update) the Keyword text reference to the project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param associatedInd - Indicates whether this keyword text should be associated to this input project or not.
   * @param projectKeyword - Project Keyword Text request. Object of {@link ProjectKeyword}
   * 
   * @return - Updated Object of {@link ProjectKeyword}
   */
  /**
   * Persist (Add, remove, Update) the Keyword text reference to the project.
   *  
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param token - JWT Token.
   * @param associatedInd - Indicates whether this keyword text should be associated to this input project or not.
   * @param projectKeyword - Project Keyword Text request. Object of {@link ProjectKeyword}
   * 
   * @return - Returns the list of updated keywords.
   */
  Map<String, Map<String, List<ProjectKeyword>>> persistKeywordTextToProject(String userId, String contextId, Long projectId, final String token,
      Integer associatedInd, ProjectKeyword projectKeyword);

  /**
   * Persist (Add, remove, Update) the System Keyword text reference to the project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param systemDetectedKeywords - List of System Detected Keywords. Object of {@link ProjectKeywordEntity}
   * 
   * @return - Updated Object of {@link ProjectKeyword}
   */
  void persistSystemDetecteKeywordTextToProject(String userId, String contextId, Long projectId,
      List<ProjectKeywordEntity> systemDetectedKeywords);

  /**
   * Persist the Keywords into Keyword text and associate to the Project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param keywordTexts - Keyword texts.
   * @param systemDetectedKeywordIndicator - Indicates whether the keyword is System Detected or not.
   */
  void persistKeywordsAndAssociateToProject(final  String userId, final String contextId, 
      final Long projectId, List<KeywordText> keywordTexts, final boolean systemDetectedKeywordIndicator);

  /**
   * Replace the candidate keywords with Permit Keyword.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param candidateKeywords - List of Candidate Keywords which needs to be replaced.
   * @param permitKeyword - replacement id of the Permit Keyword.
   */
  void replaceCandidateKeywords(String userId, String contextId,
      List<Long> candidateKeywords, Long permitKeyword);
}
