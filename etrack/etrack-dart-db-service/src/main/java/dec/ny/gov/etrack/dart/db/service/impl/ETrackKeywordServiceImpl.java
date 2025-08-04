package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.dart.db.entity.KeywordCategory;
import dec.ny.gov.etrack.dart.db.entity.KeywordText;
import dec.ny.gov.etrack.dart.db.entity.PermitKeyword;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectKeyword;
import dec.ny.gov.etrack.dart.db.model.PermitType;
import dec.ny.gov.etrack.dart.db.repo.KeywordCategoryRepo;
import dec.ny.gov.etrack.dart.db.repo.KeywordTextRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitKeywordRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectKeywordRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.service.ETrackKeywordService;

@Service
public class ETrackKeywordServiceImpl implements ETrackKeywordService {

  @Autowired
  private KeywordCategoryRepo keywordCategoryRepo;
  @Autowired
  private PermitKeywordRepo permitKeywordRepo;
  @Autowired
  private KeywordTextRepo keywordTextRepo;
  @Autowired
  private ProjectKeywordRepo projectKeywordRepo;
  @Autowired
  private ProjectRepo projectRepo;
  
  private static final Logger logger =
      LoggerFactory.getLogger(ETrackKeywordServiceImpl.class.getName());
  private static final Integer CANDIDATE_KEYWORD = -1;
  private static final Integer SYSTEM_DETECTED_KEYWORD = 1;
  private static final String CANDIDATE_KEYWORD_NAME = "candidateKeyword";
  private static final String SYSTEM_DETECTED_KEYWORD_NAME = "systemDetectedKeyword";
  private static final String PERMIT_KEYWORD_NAME = "permitKeyword";

  @Override
  public Map<String, List<KeywordText>> retrieveKeywordTexts(final String userId,
      final String contextId) {
    logger.info("Entering into retrieve the keyword texts. User Id {}, Context Id {}", userId,
        contextId);
    
    Map<String, List<KeywordText>> categoryAndKeywordTextMap = new HashMap<>();
    List<KeywordText> keywordTexts = keywordTextRepo.findAllActiveKeywordTexts();
    if (!CollectionUtils.isEmpty(keywordTexts)) {
      keywordTexts.forEach(keywordText -> {
        if (!CANDIDATE_KEYWORD.equals(keywordText.getKeywordCategoryId().intValue())) {
          if (CollectionUtils.isEmpty(categoryAndKeywordTextMap.get(keywordText.getKeywordCategory()))) {
            List<KeywordText> keywordTextsList = new ArrayList<>();
            keywordTextsList.add(keywordText);
            categoryAndKeywordTextMap.put(keywordText.getKeywordCategory(), keywordTextsList);
          } else {
            categoryAndKeywordTextMap.get(keywordText.getKeywordCategory()).add(keywordText);
          }
        }
      });      
    }
    categoryAndKeywordTextMap.keySet().forEach(categoryAndKeywordText -> {
      categoryAndKeywordTextMap.put(categoryAndKeywordText, 
      categoryAndKeywordTextMap.get(categoryAndKeywordText).stream().sorted(
          Comparator.comparing(KeywordText::getKeywordText)).collect(Collectors.toList()));
    });
    return categoryAndKeywordTextMap;
  }


  @Override
  public List<KeywordCategory> retrieveKeywordCategories(final String userId,
      final String contextId) {
    logger.info("Entering into retrieve the keyword categories. User Id {}, Context Id {}", userId,
        contextId);
    return keywordCategoryRepo.findAllKeywordCategories().stream().sorted(
        Comparator.comparing(KeywordCategory::getKeywordCategory)).collect(Collectors.toList());
  }

  @Override
  public List<PermitKeyword> retrievePermitKeywordTexts(final String userId,
      final String contextId, final Long categoryId) {
    logger.info("Entering into retrieve the Permit keyword texts. User Id {}, Context Id {}",
        userId, contextId);
    if (categoryId == null || categoryId.equals(0L)) {
      return permitKeywordRepo.findAllPermitKeywords();
    } else {
      return permitKeywordRepo.findAllPermitKeywordsByCategoryid(categoryId);
    }
    
  }

  @Transactional
  @Override
  public Map<String, Map<String, List<ProjectKeyword>>> retrieveKeywordTextsForProject(
      final String userId, final String contextId, final Long projectId) {
    
    List<ProjectKeyword> candidateKeywords = new ArrayList<>();
    Map<String, Map<String, List<ProjectKeyword>>> categorizedKeywords = new HashMap<>();
    List<ProjectKeyword> keywordsAndAllocatedToProject =
        projectKeywordRepo.findAllKeywordsAndProjectAssociatedKeywords(projectId);
    
    Optional<Project> projectAvail = projectRepo.findById(projectId);
    
    if (projectAvail.isPresent() 
        && (projectAvail.get().getOriginalSubmittalInd() == null 
        || projectAvail.get().getOriginalSubmittalInd().equals(1))) {
      projectKeywordRepo.deleteUnMappedKeywords(projectId);
    }
    
    if (!CollectionUtils.isEmpty(keywordsAndAllocatedToProject)) {
      Map<String, List<ProjectKeyword>> permitCategoryAndKeywordTexts = new HashMap<>();
      Map<String, List<ProjectKeyword>> systemDetecteCategoryAndKeywordTexts = new HashMap<>();
      keywordsAndAllocatedToProject.forEach(keyword -> {
        if (keyword.getKeywordCategoryId() != null 
            && keyword.getKeywordCategoryId().intValue() == CANDIDATE_KEYWORD) {
          candidateKeywords.add(keyword);
        } else {
          if (SYSTEM_DETECTED_KEYWORD.equals(keyword.getSystemDetected())) {
            prepareCategorizedKeywords(keyword, systemDetecteCategoryAndKeywordTexts);
          } else {
            prepareCategorizedKeywords(keyword, permitCategoryAndKeywordTexts);
          }
        }
      });
      categorizedKeywords.put(PERMIT_KEYWORD_NAME, permitCategoryAndKeywordTexts);
      if (projectAvail.get().getOriginalSubmittalDate() != null 
          && !CollectionUtils.isEmpty(systemDetecteCategoryAndKeywordTexts)) {
        permitCategoryAndKeywordTexts.keySet().forEach(category -> {
          if (!CollectionUtils.isEmpty(systemDetecteCategoryAndKeywordTexts.get(category))) {
            permitCategoryAndKeywordTexts.get(category).addAll(systemDetecteCategoryAndKeywordTexts.get(category));
          }
        });
        categorizedKeywords.put(SYSTEM_DETECTED_KEYWORD_NAME, new HashMap<>());
      } else {
        categorizedKeywords.put(SYSTEM_DETECTED_KEYWORD_NAME, systemDetecteCategoryAndKeywordTexts);
      }
      Map<String, List<ProjectKeyword>> candidateKeywordsMap = new HashMap<>();
      if (!CollectionUtils.isEmpty(candidateKeywords)) {
        candidateKeywordsMap.put(candidateKeywords.get(0).getKeywordCategory(), candidateKeywords);
      }
      categorizedKeywords.put(CANDIDATE_KEYWORD_NAME, candidateKeywordsMap);
    }
    categorizedKeywords.keySet().forEach(categorizedKeyword -> {
      categorizedKeywords.get(categorizedKeyword).keySet().forEach(category -> {
        categorizedKeywords.get(categorizedKeyword).put(category, categorizedKeywords.get(categorizedKeyword).get(category).stream().sorted(
            Comparator.comparing(ProjectKeyword::getKeywordText)).collect(Collectors.toList()));
      });
    });
    return categorizedKeywords;
  }


  private void prepareCategorizedKeywords(
      ProjectKeyword keyword, Map<String, List<ProjectKeyword>> keywordCategoryMap) {
    
    if (CollectionUtils.isEmpty(keywordCategoryMap.get(keyword.getKeywordCategory()))) {
      List<ProjectKeyword> keywordsAndProjectAssociated = new ArrayList<>();
      keywordsAndProjectAssociated.add(keyword);
      keywordCategoryMap.put(keyword.getKeywordCategory(), keywordsAndProjectAssociated);
    } else {
      keywordCategoryMap.get(keyword.getKeywordCategory()).add(keyword);
    }
  }
  
  @Override
  public Map<String, List<KeywordText>> retrieveKeywordTextsById(String userId, String contextId,
      Long categoryId) {
    logger.info("Entering into retrieve the keyword texts. User Id {}, Context Id {}", userId,
        contextId);
    Map<String, List<KeywordText>> categoryAndKeywordTextMap = new HashMap<>();
    List<KeywordText> keywordTexts = keywordTextRepo.findAllActiveKeywordTextsByCategoryId(categoryId);
    keywordTexts.forEach(keywordText -> {
      if (CollectionUtils
          .isEmpty(categoryAndKeywordTextMap.get(keywordText.getKeywordCategory()))) {
        List<KeywordText> keywordTextsList = new ArrayList<>();
        keywordTextsList.add(keywordText);
        categoryAndKeywordTextMap.put(keywordText.getKeywordCategory(), keywordTextsList);
      } else {
        categoryAndKeywordTextMap.get(keywordText.getKeywordCategory()).add(keywordText);
      }
    });
    categoryAndKeywordTextMap.keySet().forEach(categoryAndKeywordText -> {
      categoryAndKeywordTextMap.put(categoryAndKeywordText, 
      categoryAndKeywordTextMap.get(categoryAndKeywordText).stream().sorted(
          Comparator.comparing(KeywordText::getKeywordText)).collect(Collectors.toList()));
    });
    return categoryAndKeywordTextMap;
  }

  @Override
  public List<PermitType> retrieveAllActivePermitTypes(String userId, String contextId) {
    List<String> permitTypeAndDescs =  permitKeywordRepo.findAllPermitTypes();
    List<PermitType> allActivePermitTypes = new ArrayList<>();
    if (!CollectionUtils.isEmpty(permitTypeAndDescs)) {
      permitTypeAndDescs.forEach(permitTypeAndDesc -> {
        PermitType permitType = new PermitType();
        String[] permitTypeDetails = permitTypeAndDesc.split(",");
        permitType.setPermitType(permitTypeDetails[0]);
        permitType.setPermitTypeDesc(permitTypeDetails[1]);
        allActivePermitTypes.add(permitType);
      });
    }
    List<PermitType> sortedPermitTypes = allActivePermitTypes.stream().sorted(
        Comparator.comparing(PermitType::getPermitTypeDesc)).collect(Collectors.toList());
    return sortedPermitTypes;
  }

  @Override
  public List<ProjectKeyword> retrieveCandidateKeywordTexts(String userId, String contextId) {
    return projectKeywordRepo.findAllCandidateKeywords();
  }
}
