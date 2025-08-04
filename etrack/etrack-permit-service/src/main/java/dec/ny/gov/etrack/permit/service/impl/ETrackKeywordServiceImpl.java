package dec.ny.gov.etrack.permit.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.permit.entity.KeywordCategoryEntity;
import dec.ny.gov.etrack.permit.entity.KeywordTextEntity;
import dec.ny.gov.etrack.permit.entity.PermitKeywordEntity;
import dec.ny.gov.etrack.permit.entity.ProjectKeywordEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.KeywordCategory;
import dec.ny.gov.etrack.permit.model.KeywordText;
import dec.ny.gov.etrack.permit.model.PermitKeyword;
import dec.ny.gov.etrack.permit.model.ProjectKeyword;
import dec.ny.gov.etrack.permit.repo.KeywordCategoryRepo;
import dec.ny.gov.etrack.permit.repo.KeywordTextRepo;
import dec.ny.gov.etrack.permit.repo.PermitKeywordRepo;
import dec.ny.gov.etrack.permit.repo.ProjectKeywordRepo;
import dec.ny.gov.etrack.permit.service.ETrackKeywordService;

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
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
  private static final Long CANDIDATE_KEYWORD_CATEGORY = -1L;
  private static final Logger logger = LoggerFactory.getLogger(ETrackKeywordServiceImpl.class.getName());
  
  @Transactional
  @Override
  public void storeKeywordText(final String userId, final String contextId, KeywordText keywordText) {
    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId, contextId);
    
    if (keywordText.getKeywordId() == null 
        && !StringUtils.hasLength(keywordText.getKeywordText())) {
      throw new BadRequestException("KEYWORD_TEXT_NA", "Keyword Text is Required.", keywordText);
    }
    
    List<KeywordTextEntity> keywordTexts = keywordTextRepo.findByKeywordText(keywordText.getKeywordText());
    if (!CollectionUtils.isEmpty(keywordTexts)) {
      if (keywordText.getKeywordId() == null || !keywordTexts.get(0).getKeywordId().equals(keywordText.getKeywordId())) {
        throw new BadRequestException("KEYWORD_TEXT_ALREADY_EXIST", "Keyword Text already exists.", keywordText);
      }
    }
    
    KeywordTextEntity keywordTextEntity = null;
    if (keywordText.getKeywordId() != null 
        && keywordText.getKeywordId() <= 0) {
      throw new BadRequestException("KEYWORD_TEXT_NA", "Invalid Keyword Text passed.", keywordText);
    } else if (keywordText.getKeywordId() != null) {
      Optional<KeywordTextEntity> keywordTextExists = keywordTextRepo.findById(keywordText.getKeywordId());
      if (!keywordTextExists.isPresent()) {
        throw new BadRequestException("KEYWORD_TEXT_NA", "Keyword Text doesn't exist to update.", keywordText);
      }
      keywordTextEntity = keywordTextExists.get();
    } else if (!StringUtils.hasLength(keywordText.getStartDate()) 
        || keywordText.getKeywordCategoryId() == null 
        || keywordText.getKeywordCategoryId() <= 0) {
      throw new BadRequestException("KEYWORD_TEXT_MANDATORY_PARAM_NA", 
          "One or more (Start date or Keyword Category id) is missing.", keywordText);
    }
    if (keywordTextEntity == null) {
      logger.info("Adding new Keyword Text requested by the user. User Id {}, Context Id {}", userId, contextId);
      keywordTextEntity = new KeywordTextEntity();
      keywordTextEntity.setActiveInd(1);
      keywordTextEntity.setCreateDate(new Date());
      keywordTextEntity.setCreatedById(userId);
    } else {
      keywordTextEntity.setStartDate(null);
      keywordTextEntity.setEndDate(null);
      keywordTextEntity.setModifiedDate(new Date());
      keywordTextEntity.setModifiedById(userId);
    }
    keywordTextEntity.setKeywordCategoryId(keywordText.getKeywordCategoryId());
    keywordTextEntity.setKeywordText(keywordText.getKeywordText());
    try {
      keywordTextEntity.setStartDate(sdf.parse(keywordText.getStartDate()));
      if (StringUtils.hasLength(keywordText.getEndDate())) {
        keywordTextEntity.setEndDate(sdf.parse(keywordText.getEndDate()));
      }
    } catch (ParseException e) {
      throw new BadRequestException("KEYWORD_TEXT_DATE_FMT_ERR", "Date value is not passed in MM/DD/YYYY format.", keywordTextEntity);
    }
    keywordTextRepo.save(keywordTextEntity);
    logger.info("Exiting from Store the Keyword Text. User Id {}, Context Id {}", userId, contextId);
  }

  @Transactional
  @Override
  public void storeKeywordCategory(String userId, String contextId,
      KeywordCategory keywordCategory) {
    
    logger.info("Entering into Store the Keyword Category. User Id {}, Context Id {}", userId, contextId);
    if (keywordCategory.getKeywordCategoryId() == null 
        && !StringUtils.hasLength(keywordCategory.getKeywordCategory())) {
      throw new BadRequestException("KEYWORD_CATG_NA", "Keyword Category is not available", keywordCategory);
    }
    
    List<KeywordCategoryEntity> keywordCategories = keywordCategoryRepo.findByCategoryText(
        keywordCategory.getKeywordCategory().toLowerCase());
    
    if (!CollectionUtils.isEmpty(keywordCategories)) {
      if (keywordCategory.getKeywordCategoryId() == null 
          || !keywordCategory.getKeywordCategoryId().equals(keywordCategories.get(0).getKeywordCategoryId())) {
        throw new BadRequestException("KEYWORD_CATG_ALREADY_EXIST", "Keyword category already exists.", keywordCategory);
      }
    }
    
    KeywordCategoryEntity keywordCategoryEntity = null;
    if (keywordCategory.getKeywordCategoryId() != null 
        && keywordCategory.getKeywordCategoryId() <= 0) {
      throw new BadRequestException("KEYWORD_CATG_NA", "Keyword Category id is not available", keywordCategory);
    } else if (keywordCategory.getKeywordCategoryId() != null) {
      Optional<KeywordCategoryEntity> keywordCategoryExists = keywordCategoryRepo.findById(keywordCategory.getKeywordCategoryId());
      if (!keywordCategoryExists.isPresent()) {
        throw new BadRequestException("KEYWORD_CATG_NA", "Keyword Category id is not an valid.", keywordCategory);
      }
      keywordCategoryEntity = keywordCategoryExists.get();
    }
    
    if (keywordCategoryEntity == null) {
      if (!CollectionUtils.isEmpty(keywordCategoryRepo.findByKeywordCategory(keywordCategory.getKeywordCategory()))) {
        throw new BadRequestException("KEYWORD_CATG_ALREADY_EXIST", 
            "Keyword category already exists.", keywordCategory);
      }
      logger.info("Adding new Keyword Category requested by the user. User Id {}, Context Id {}", userId, contextId);
      keywordCategoryEntity = new KeywordCategoryEntity();
      keywordCategoryEntity.setActiveInd(1);
      keywordCategoryEntity.setCreateDate(new Date());
      keywordCategoryEntity.setCreatedById(userId);
    } else {
      keywordCategoryEntity.setModifiedDate(new Date());
      keywordCategoryEntity.setModifiedById(userId);
    }
    keywordCategoryEntity.setKeywordCategory(keywordCategory.getKeywordCategory());
    keywordCategoryRepo.save(keywordCategoryEntity);
    logger.info("Existing from Store the Keyword Category. User Id {}, Context Id {}", userId, contextId);
  }

  @Transactional
  @Override
  public void storePermitKeyword(String userId, String contextId, PermitKeyword permitKeyword) {
    logger.info("Entering into Store the Permit Keyword. User Id {}, Context Id {}", userId, contextId);
    if (permitKeyword.getPermitKeywordId() == null 
        && !StringUtils.hasLength(permitKeyword.getPermitTypeCode())) {
      throw new BadRequestException("PERMIT_KEYWORD_NA", "Permit Type code doesn't exists", permitKeyword);
    }
    PermitKeywordEntity permitKeywordEntity = null;
    if (permitKeyword.getPermitKeywordId() != null 
        && permitKeyword.getPermitKeywordId() <= 0) {
      throw new BadRequestException("PERMIT_KEYWORD_NA", "Permit Type Keyword id is not available", permitKeyword);
    } else if (permitKeyword.getPermitKeywordId() != null) {
      Optional<PermitKeywordEntity> permitKeywordExists = permitKeywordRepo.findById(permitKeyword.getPermitKeywordId());
      if (!permitKeywordExists.isPresent()) {
        throw new BadRequestException("KEYWORD_TEXT_NA", "Keyword text id not an valid.", permitKeyword);
      }
      permitKeywordEntity = permitKeywordExists.get();
    } else if (!StringUtils.hasLength(permitKeyword.getStartDate()) 
        || permitKeyword.getKeywordId() == null 
        || permitKeyword.getKeywordId() <= 0) {
      throw new BadRequestException("PERMIT_KEYWORD_MANDATORY_PARAM_NA", 
          "Keyword text mandatory parameters either Start date or Keyword Category id is not available.", permitKeyword);
    }
    
    if (permitKeywordEntity == null) {
      if (!CollectionUtils.isEmpty(permitKeywordRepo.findByPermitTypeCodeAndKeywordId(
          permitKeyword.getPermitTypeCode(), permitKeyword.getKeywordId()))) {
        throw new BadRequestException("PERMIT_TYPE_ALREADY_EXIST", 
            "Permit Type already exists for this category and keyword text.", permitKeyword);        
      }
      permitKeywordEntity = new PermitKeywordEntity();
      permitKeywordEntity.setActiveInd(1);
      permitKeywordEntity.setCreateDate(new Date());
      permitKeywordEntity.setCreatedById(userId);
    } else {
      permitKeywordEntity.setStartDate(null);
      permitKeywordEntity.setEndDate(null);
      permitKeywordEntity.setModifiedDate(new Date());
      permitKeywordEntity.setModifiedById(userId);
    }
    try {
      permitKeywordEntity.setStartDate(sdf.parse(permitKeyword.getStartDate()));
      if (StringUtils.hasLength(permitKeyword.getEndDate())) {
        permitKeywordEntity.setEndDate(sdf.parse(permitKeyword.getEndDate()));
      }
    } catch (ParseException e) {
      throw new BadRequestException(
          "PERMIT_KEYWORD_TEXT_DATE_FMT_ERR", "Date value is not passed in MM/DD/YYYY format.", permitKeywordEntity);
    }
    permitKeywordEntity.setKeywordId(permitKeyword.getKeywordId());
    permitKeywordEntity.setPermitTypeCode(permitKeyword.getPermitTypeCode());
    permitKeywordRepo.save(permitKeywordEntity);
    logger.info("Exiting from Store the Permit Keyword. User Id {}, Context Id {}", userId, contextId);
  }

  @Transactional
  @Override
  public Map<String, Map<String, List<ProjectKeyword>>> persistKeywordTextToProject(
      final String userId, final String contextId, final Long projectId, final String token,
      final Integer associatedInd, ProjectKeyword projectKeyword) {

    logger.info("Entering into Persist Keyword text to the input project. User Id {}, Context Id {}", userId, contextId);
    ProjectKeywordEntity projectKeywordEntity = new ProjectKeywordEntity();
    projectKeywordEntity.setProjectId(projectId);
    projectKeywordEntity.setCreateDate(new Date());
    projectKeywordEntity.setCreatedById(userId);
    boolean isNewCandidateKeyword = false;
    if (associatedInd == null) {
      logger.info("Add candidate keyword for this project {}. User Id {}, Context Id {}", projectId, userId, contextId);
      if (!StringUtils.hasLength(projectKeyword.getKeywordText())) {
        throw new BadRequestException("KEYWORD_TEXT_EMPTY", "Keyword Text cannot be blank", projectKeyword);
      }
      if (!CollectionUtils.isEmpty(keywordTextRepo.findByKeywordTextFromApprovedList(
          projectId, projectKeyword.getKeywordText()))) {
        throw new BadRequestException("KEYWORD_TEXT_ALREADY_EXIST", 
            "Keyword text already exists.", projectKeyword);
      }
      KeywordTextEntity keywordTextEntity = new KeywordTextEntity();
      keywordTextEntity.setActiveInd(1);
      keywordTextEntity.setCreateDate(new Date());
      keywordTextEntity.setCreatedById(userId);
      keywordTextEntity.setStartDate(new Date());
      keywordTextEntity.setKeywordCategoryId(CANDIDATE_KEYWORD_CATEGORY);
      keywordTextEntity.setKeywordText(projectKeyword.getKeywordText());
      keywordTextEntity = keywordTextRepo.save(keywordTextEntity);
      projectKeywordEntity.setKeywordId(keywordTextEntity.getKeywordId());
      projectKeywordRepo.save(projectKeywordEntity);
      projectKeyword.setKeywordId(keywordTextEntity.getKeywordId());
      projectKeyword.setProjectSelected(1);
      isNewCandidateKeyword = true;
    } else {
      if (projectKeyword.getKeywordId() == null) {
        throw new BadRequestException("KEYWORD_TEXT_ID_NOT_PASSED", 
            "This Keyword Text doesn't look like exisitng one.", projectKeyword);
      }
      List<ProjectKeywordEntity> projectKeywordEntities = projectKeywordRepo.findByProjectIdAndKeywordId(
          projectId, projectKeyword.getKeywordId());
      if (associatedInd.equals(1)) {
        logger.info("Associate this keyword text to this project {}. User Id {}, Context Id {}", projectId, userId, contextId);
        Optional<KeywordTextEntity> keywordTextEntity = keywordTextRepo.findById(projectKeyword.getKeywordId());
        if (!keywordTextEntity.isPresent()) {
          throw new BadRequestException("KEYWORD_TEXT_ID_NA", 
              "This Keyword Text is not available.", projectKeyword);
        }
        if (!CollectionUtils.isEmpty(projectKeywordEntities)) {
          throw new BadRequestException("KEYWORD_TEXT_ALREADY_ASSOCIATED", 
              "This Keyword Text is already associated with this project.", projectKeyword);
        }
        projectKeywordEntity.setKeywordId(projectKeyword.getKeywordId());
        projectKeywordEntity.setSystemDetected(projectKeyword.getSystemDetected());
        projectKeywordRepo.save(projectKeywordEntity);
      } else if (associatedInd.equals(0)) {
        logger.info("Dissociate/Delete this keyword text to this project {}. User Id {}, Context Id {}", projectId, userId, contextId);
        if (CollectionUtils.isEmpty(projectKeywordEntities)) {
          throw new BadRequestException("KEYWORD_TEXT_NOT_ASSOCIATED", 
              "This Keyword Text is not associated with this project.", projectKeyword);
        }
        projectKeywordRepo.deleteById(projectKeywordEntities.get(0).getProjectKeywordId());
      } else {
        throw new BadRequestException("KEYWORD_TEXT_ASSOC_INVALID", 
            "Associated indicator is passed not an valid one to take some action for this project.", projectKeyword);
      }
    }
    
    logger.info("Retrieving the updated keywords from another service. User Id {}, Context Id {}", userId, contextId);
    ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>> typeRef =
        new ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>>() {};
    String uri = UriComponentsBuilder.newInstance().pathSegment(
        "/etrack-dart-db/keyword/project").build().toString();
    logger.info("Requesting DART DB Service to get the eTrack Active Authorizations. "
        + "User Id: {}, Context Id: {}", userId, contextId);
    HttpHeaders headers = new HttpHeaders();
    headers.add("userId", userId);
    headers.add("projectId", String.valueOf(projectId));
    headers.add(HttpHeaders.AUTHORIZATION, token);
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    try {
      Map<String, Map<String, List<ProjectKeyword>>> keywords = eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.GET, requestEntity, typeRef).getBody();
      if (isNewCandidateKeyword) {
        if (CollectionUtils.isEmpty(keywords)) {
          keywords = new HashMap<>();
          keywords.put("candidateKeyword", new HashMap<String, List<ProjectKeyword>>());
        }
        
        Map<String, List<ProjectKeyword>> projectKeywordCategoryMap = keywords.get("candidateKeyword");
        if (CollectionUtils.isEmpty(projectKeywordCategoryMap)) {
          projectKeywordCategoryMap = new HashMap<String, List<ProjectKeyword>>();
          projectKeywordCategoryMap.put("Candidate Keyword", new ArrayList<>());
        }
        
        if (CollectionUtils.isEmpty(projectKeywordCategoryMap.get("Candidate Keyword"))) {
          List<ProjectKeyword> projectKeywords = new ArrayList<>();
          projectKeywords.add(projectKeyword);
        } else {
          projectKeywordCategoryMap.get("Candidate Keyword").add(projectKeyword);
        }
        for (String categorizedKeyword : keywords.keySet()) {
          for (String category : keywords.get(categorizedKeyword).keySet()) {
            keywords.get(categorizedKeyword).put(category, keywords.get(categorizedKeyword).get(category).stream().sorted(
                Comparator.comparing(ProjectKeyword::getKeywordText)).collect(Collectors.toList())); 
          }
        }
      }
      logger.info("Exiting from reading the keyworkds. User Id {}, Context Id {}", userId, contextId);
      return keywords;
    } catch (HttpServerErrorException hse) {
      throw new ETrackPermitException(hse.getStatusCode(), "KEYWORD_RETRIEVAL_ERR",
          "Error while retrieving the Keywords "+ hse.getResponseBodyAsString());
    } catch (Exception ex) {
    	ex.printStackTrace();
      throw new ETrackPermitException("KEYWORD_RETRIEVAL_GENERAL_ERR",
          "General error while retrieving the Keywords.", ex);
    }
  }

  
  @Transactional
  @Override
  public void persistKeywordsAndAssociateToProject(final String userId, final String contextId, final Long projectId,
      final  List<KeywordText> keywordTexts, final boolean systemDetectedKeywordIndicator) {
    keywordTexts.forEach(keywordText -> {
      KeywordTextEntity keywordTextEntity = new KeywordTextEntity();
      keywordTextEntity.setActiveInd(1);
      keywordTextEntity.setCreateDate(new Date());
      keywordTextEntity.setCreatedById(userId);
      keywordTextEntity.setStartDate(new Date());
      keywordTextEntity.setKeywordText(keywordText.getKeywordText());
      keywordTextEntity.setKeywordCategoryId(keywordText.getKeywordCategoryId());
      keywordTextEntity = keywordTextRepo.save(keywordTextEntity);
      ProjectKeywordEntity projectKeyword = new ProjectKeywordEntity();
      projectKeyword.setCreateDate(new Date());
      projectKeyword.setCreatedById(userId);
      projectKeyword.setKeywordId(keywordTextEntity.getKeywordId());
      if (systemDetectedKeywordIndicator) {
        projectKeyword.setSystemDetected(1);
      }
      projectKeyword.setProjectId(projectId);
      projectKeywordRepo.save(projectKeyword);
    });
  }

  @Transactional
  @Override
  public void persistSystemDetecteKeywordTextToProject(String userId, String contextId,
      Long projectId, List<ProjectKeywordEntity> systemDetectedKeywords) {
    
    systemDetectedKeywords.forEach(systemDetectedKeyword -> {
      List<ProjectKeywordEntity> projectKeywordEntities = projectKeywordRepo.findByProjectIdAndKeywordId(
          projectId, systemDetectedKeyword.getKeywordId());
      
      ProjectKeywordEntity  projectKeywordEntity = null;
      if (CollectionUtils.isEmpty(projectKeywordEntities)) {
        projectKeywordEntity = new ProjectKeywordEntity();
        projectKeywordEntity.setCreateDate(new Date());
        projectKeywordEntity.setCreatedById(userId);
        projectKeywordEntity.setProjectId(projectId);
        projectKeywordEntity.setKeywordId(systemDetectedKeyword.getKeywordId());
      } else {
        projectKeywordEntity = projectKeywordEntities.get(0);
        projectKeywordEntity.setModifiedById(userId);
        projectKeywordEntity.setModifiedDate(new Date());
      }
      projectKeywordEntity.setSystemDetected(systemDetectedKeyword.getSystemDetected());
      projectKeywordRepo.save(projectKeywordEntity);
    });
  }

  @Transactional
  @Override
  public void replaceCandidateKeywords(final String userId, final String contextId,
      final List<Long> candidateKeywords, final Long permitKeyword) {
    logger.info("Entering into replaceCandidateKeywords. User Id {}, Context Id {}", userId, contextId);
    int matchedCandidateKeywordsCount = projectKeywordRepo.findMatchedProjectKeywords(candidateKeywords);
    if (candidateKeywords.size() != matchedCandidateKeywordsCount) {
      throw new BadRequestException("CANDIDATE_KEYWORD_MISMATCH", 
          "One or more candidate keywords are not available", candidateKeywords);
    }
    
    logger.info("Requesting to replace the candidate keywords. User Id {}, Context Id {}", userId, contextId);
    projectKeywordRepo.updateCandidateKeywordsWithPermitKeyword(candidateKeywords, permitKeyword);
    logger.info("Candidate Keywords are replaced successfully "
        + "with the permit keyword {}. User Id {}, Context Id {}", permitKeyword, userId, contextId);

    logger.info("Requesting to delete the candidate keywords. User Id {}, Context Id {}", userId, contextId);
    keywordTextRepo.deleteReplacedCandidateKeywords(candidateKeywords);
    logger.info("Replaced Candidate Keywords are deleted successfully. "
        + "{}. User Id {}, Context Id {}", candidateKeywords, userId, contextId);
    logger.info("Exiting from replaceCandidateKeywords. User Id {}, Context Id {}", userId, contextId);
  }
}
