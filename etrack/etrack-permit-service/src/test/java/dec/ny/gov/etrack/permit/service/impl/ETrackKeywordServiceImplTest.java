package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

import dec.ny.gov.etrack.permit.entity.KeywordCategoryEntity;
import dec.ny.gov.etrack.permit.entity.KeywordTextEntity;
import dec.ny.gov.etrack.permit.entity.PermitKeywordEntity;
import dec.ny.gov.etrack.permit.entity.ProjectKeywordEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameResponse;
import dec.ny.gov.etrack.permit.model.KeywordCategory;
import dec.ny.gov.etrack.permit.model.KeywordText;
import dec.ny.gov.etrack.permit.model.PermitKeyword;
import dec.ny.gov.etrack.permit.model.ProjectKeyword;
import dec.ny.gov.etrack.permit.repo.KeywordCategoryRepo;
import dec.ny.gov.etrack.permit.repo.KeywordTextRepo;
import dec.ny.gov.etrack.permit.repo.PermitKeywordRepo;
import dec.ny.gov.etrack.permit.repo.ProjectKeywordRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackKeywordServiceImplTest {

  @InjectMocks
  private ETrackKeywordServiceImpl keyWordService;

  @Mock
  private KeywordTextRepo keywordTextRepo;

  @Mock
  private ProjectKeywordRepo projectKeywordRepo;

  @Mock
  private KeywordCategoryRepo keywordCategoryRepo;

  @Mock
  private PermitKeywordRepo permitKeywordRepo;
  
  @Mock
  private RestTemplate eTrackOtherServiceRestTemplate;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private String userId = "jxpuvoge";
  private Long projectId = 1002L;
  private String keywordText = "Text";
  private String contextId = "ContextID";
  private Long keywordCategoryId = 10L;
  private String keywordCategory = "Cat";
  private Long keywordId = 1l;
  private Long permitKeyword = 2L;

  // This is completed, 86% test coverage.


  @Test
  public void testStoreKeywordThrowsBREForInvalidKeywordCategory() {
    KeywordCategory category = getKeywordCategoryObj();
    category.setKeywordCategoryId(null);
    category.setKeywordCategory("");
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword Category is not available");
    keyWordService.storeKeywordCategory(userId, contextId, category);
  }


  // persistKeywordsAndAssociateToProject test cases

  @Test
  public void testPersistKeywordsAndAssociateToProjectMakesCallToSaveKeyword() {
    KeywordTextEntity entity = new KeywordTextEntity();
    entity.setKeywordId(10L);
    when(keywordTextRepo.save(Mockito.any())).thenReturn(entity);
    keyWordService.persistKeywordsAndAssociateToProject(userId, contextId, projectId,
        getKeywordTextList(), true);
    verify(projectKeywordRepo).save(Mockito.any());
  }



  // persistSystemDetecteKeywordTextToProject test cases
  @Test
  public void testPersistSystemDetecteKeywordTextToProjectSavesKeywordWithNoExistingKeywords() {
    when(projectKeywordRepo.findByProjectIdAndKeywordId(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(Arrays.asList());
    keyWordService.persistSystemDetecteKeywordTextToProject(userId, contextId, projectId,
        getProjectKeywordList());
    verify(projectKeywordRepo).save(Mockito.any());
  }

  @Test
  public void testPersistSystemDetecteKeywordTextToProjectSavesKeywordWithExistingKeywords() {
    when(projectKeywordRepo.findByProjectIdAndKeywordId(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(getProjectKeywordList());
    keyWordService.persistSystemDetecteKeywordTextToProject(userId, contextId, projectId,
        getProjectKeywordList());
    verify(projectKeywordRepo).save(Mockito.any());
  }


  // persistKeywordTextToProject test cases
  @Test
  public void testPersistKeywordTextToProjectThrowsBREForNoKeywordText() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword Text cannot be blank");
    ProjectKeyword keyword = getProjectKeywordObj();
    keyword.setKeywordText(null);
    keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", null,
        keyword);
  }

  @Test
  public void testPersistKeywordTextToProjectThrowsBREForExistingKeyword() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword text already exists.");
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList("test"));
    keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", null,
        getProjectKeywordObj());
  }

  @Test
  public void testPersistKeywordTextToProjectSavesKeywordSuccessfullyWithNoAssociatedInd() {
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
	ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>> typeRef =
	        new ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>>() {};
	String uri = UriComponentsBuilder.newInstance().pathSegment(
	        "/etrack-dart-db/keyword/project").build().toString();
	 HttpHeaders headers = new HttpHeaders();
	 headers.add("userId", userId);
	    headers.add("projectId", String.valueOf(projectId));
	    headers.add(HttpHeaders.AUTHORIZATION, "token");
	    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
	  	    Map<String, Map<String, List<ProjectKeyword>>> keywordsObj = new HashMap<>();
	    ResponseEntity<Map<String, Map<String, List<ProjectKeyword>>>> response = new  ResponseEntity(keywordsObj,HttpStatus.OK);
	    Mockito.lenient().when(eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.GET, requestEntity,typeRef)).thenReturn(response);
	   
	    java.util.Map<String, Map<String, List<ProjectKeyword>>> result =
        keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", null,
            getProjectKeywordObj());
    assertNotNull(result);
  }
 

  @Test
  public void testPersistKeywordTextToProjectThrowsBREForNewKeywordText() {
    expectedException.expectMessage("This Keyword Text doesn't look like exisitng one.");
    expectedException.expect(BadRequestException.class);
    ProjectKeyword projectKeyword = getProjectKeywordObj();
    projectKeyword.setKeywordId(null);
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
    keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", 1,
        projectKeyword);
  }

  @Test
  public void testPersistKeywordTextToProjectThrowsBREForNonExistentKeywordText() {
    expectedException.expectMessage("This Keyword Text is not available.");
    expectedException.expect(BadRequestException.class);
    ProjectKeyword projectKeyword = getProjectKeywordObj();
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
    keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", 1,
        projectKeyword);
  }

  @Test
  public void testPersistKeywordTextToProjectThrowsBREForAlreadyAssociatedKeyword() {
    expectedException.expectMessage("This Keyword Text is already associated with this project");
    expectedException.expect(BadRequestException.class);
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
    when(projectKeywordRepo.findByProjectIdAndKeywordId(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(getProjectKeywordList());
    when(keywordTextRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordTextEntityObj()));
    keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", 1,
        getProjectKeywordObj());

  }

  @Test
  public void testPersistKeywordTextToProjectSuccessfullyWithAssociatedIndicatorOfOne() {
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
    when(projectKeywordRepo.findByProjectIdAndKeywordId(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordTextEntityObj()));
    
    ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>> typeRef =
	        new ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>>() {};
	String uri = UriComponentsBuilder.newInstance().pathSegment(
	        "/etrack-dart-db/keyword/project").build().toString();
	 HttpHeaders headers = new HttpHeaders();
	 headers.add("userId", userId);
	    headers.add("projectId", String.valueOf(projectId));
	    headers.add(HttpHeaders.AUTHORIZATION, "token");
	    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
	  	    Map<String, Map<String, List<ProjectKeyword>>> keywordsObj = new HashMap<>();
	    ResponseEntity<Map<String, Map<String, List<ProjectKeyword>>>> response = new  ResponseEntity(keywordsObj,HttpStatus.OK);
	    Mockito.lenient().when(eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.GET, requestEntity,typeRef)).thenReturn(response);
    
    Map<String, Map<String, List<ProjectKeyword>>> result =
        keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", 1,
            getProjectKeywordObj());
    assertNotNull(result);
    verify(projectKeywordRepo).save(Mockito.any());
  }

  @Test
  public void testPersistKeywordTextToProject() {
    when(keywordTextRepo.findByKeywordTextFromApprovedList(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Arrays.asList());
    when(keywordTextRepo.save(Mockito.any())).thenReturn(getKeywordTextEntityObj());
    when(keywordTextRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordTextEntityObj()));
    ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>> typeRef =
	        new ParameterizedTypeReference<Map<String, Map<String, List<ProjectKeyword>>>>() {};
	String uri = UriComponentsBuilder.newInstance().pathSegment(
	        "/etrack-dart-db/keyword/project").build().toString();
	 HttpHeaders headers = new HttpHeaders();
	 headers.add("userId", userId);
	    headers.add("projectId", String.valueOf(projectId));
	    headers.add(HttpHeaders.AUTHORIZATION, "token");
	    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
	  	    Map<String, Map<String, List<ProjectKeyword>>> keywordsObj = new HashMap<>();
	    ResponseEntity<Map<String, Map<String, List<ProjectKeyword>>>> response = new  ResponseEntity(keywordsObj,HttpStatus.OK);
	    Mockito.lenient().when(eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.GET, requestEntity,typeRef)).thenReturn(response);
    Map<String, Map<String, List<ProjectKeyword>>> result =
        keyWordService.persistKeywordTextToProject(userId, contextId, projectId, "token", 1,
            getProjectKeywordObj());
    assertNotNull(result);
  }



  // storeKeywordText test cases

  @Test
  public void testStoreKeywordTextThrowsBREForNoKeywordText() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword Text is Required.");
    KeywordText keywordText = getKeywordTextObj();
    keywordText.setKeywordId(null);
    keywordText.setKeywordText("");
    keyWordService.storeKeywordText(userId, contextId, keywordText);
  }

  @Test
  public void testStoreKeywordTextThrowsBREForExistingKeywordText() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword Text already exists.");
    KeywordText keywordText = getKeywordTextObj();
    when(keywordTextRepo.findByKeywordText(Mockito.anyString()))
        .thenReturn(Arrays.asList(getKeywordTextEntityObj()));
    keyWordService.storeKeywordText(userId, contextId, keywordText);
  }

  @Test
  public void testStoreKeywordTextThrowsBREForInvalidKeywordText() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Invalid Keyword Text passed.");
    KeywordText keywordText = getKeywordTextObj();
    keywordText.setKeywordId(-3L);
    keyWordService.storeKeywordText(userId, contextId, keywordText);
  }

  @Test
  public void testStoreKeywordTextUpdatesKeywordTextEntity() {
    KeywordText keywordText = getKeywordTextObj();
    keywordText.setKeywordId(10L);
    when(keywordTextRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordTextEntityObj()));
    keyWordService.storeKeywordText(userId, contextId, keywordText);
    verify(keywordTextRepo).save(Mockito.any());
  }


  @Test
  public void testStoreKeywordTextThrowsBREForNonExistentKeywordWhenUpdating() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword Text doesn't exist to update.");
    KeywordText keywordText = getKeywordTextObj();
    keywordText.setKeywordId(10L);
    when(keywordTextRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    keyWordService.storeKeywordText(userId, contextId, keywordText);
  }


  @Test
  public void testStoreKeywordTextSavesNewKeywordTextEntity() {
    keyWordService.storeKeywordText(userId, contextId, getKeywordTextObj());
    verify(keywordTextRepo).save(Mockito.any());
  }

  // storeKeywordCategory test cases
  @Test
  public void testStoreKeywordCategoryUpdatesExistingKeywordCategory() {
    when(keywordCategoryRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordCategoryEntity()));
    keyWordService.storeKeywordCategory(userId, contextId, getKeywordCategoryObj());
    verify(keywordCategoryRepo).save(Mockito.any());
  }

  @Test
  public void testStoreKeywordCategorySavesNewKeywordCategory() {
    KeywordCategory category = getKeywordCategoryObj();
    category.setKeywordCategoryId(null);
    when(keywordCategoryRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getKeywordCategoryEntity()));
    keyWordService.storeKeywordCategory(userId, contextId, category);
    verify(keywordCategoryRepo).save(Mockito.any());
  }

  private KeywordCategoryEntity getKeywordCategoryEntity() {
    KeywordCategoryEntity entity = new KeywordCategoryEntity();
    entity.setActiveInd(1);
    entity.setKeywordCategory(keywordCategory);
    entity.setKeywordCategoryId(keywordCategoryId);
    return entity;
  }


  // replaceCandidateKeywords test cases


  @Test
  public void testReplaceCandidateKeywordsThrowsBREForInvalidKeywordCatId() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage(
        "Keyword text mandatory parameters either Start date or Keyword Category id is not available");
    PermitKeyword keyword = getPermitKeywordObj();
    keyword.setPermitKeywordId(null);
    keyWordService.storePermitKeyword(userId, contextId, keyword);
  }

  @Test
  public void testReplaceCandidateKeywordsThrowsBREForInvalidPermitTypeCode() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Permit Type code doesn't exists");
    PermitKeyword keyword = getPermitKeywordObj();
    keyword.setPermitKeywordId(null);
    keyword.setPermitTypeCode(null);
    keyWordService.storePermitKeyword(userId, contextId, keyword);
  }


  @Test
  public void testReplaceCandidateKeywordsThrowsBREForUnavailableKeywords() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("One or more candidate keywords are not available");
    when(projectKeywordRepo.findMatchedProjectKeywords(Mockito.anyList())).thenReturn(100);
    keyWordService.replaceCandidateKeywords(userId, contextId, getCandidateKeywordList(),
        permitKeyword);
  }

  @Test
  public void testReplaceCandidateKeywordsSavesNewKeywordAndDeletesOldKeywords() {
    when(projectKeywordRepo.findMatchedProjectKeywords(Mockito.anyList())).thenReturn(2);
    keyWordService.replaceCandidateKeywords(userId, contextId, getCandidateKeywordList(),
        permitKeyword);
    verify(projectKeywordRepo).updateCandidateKeywordsWithPermitKeyword(Mockito.anyList(),
        Mockito.anyLong());
    verify(keywordTextRepo).deleteReplacedCandidateKeywords(Mockito.anyList());
  }



  // storePermitKeyword test cases
  @Test
  public void testStorePermitKeywordUpdatesExistingPermitKeyword() {
    when(permitKeywordRepo.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(getPermitKeywordEntity()));
    keyWordService.storePermitKeyword(userId, contextId, getPermitKeywordObj());
    verify(permitKeywordRepo).save(Mockito.any());
  }


  @Test
  public void testStorePermitKeywordThrowsBREForUnavailablePermitTypeKeyword() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Permit Type Keyword id is not available");
    PermitKeyword keyword = getPermitKeywordObj();
    keyword.setPermitKeywordId(-10L);
    keyWordService.storePermitKeyword(userId, contextId, keyword);
  }

  @Test
  public void testStorePermitKeywordThrowsBREForInvalidKeywordTextId() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Keyword text id not an valid");
    PermitKeyword keyword = getPermitKeywordObj();
    when(permitKeywordRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    keyWordService.storePermitKeyword(userId, contextId, keyword);
  }

  // Not working as expected:
  // @Test
  // public void testStorePermitKeywordSavesNewPermitKeyword() {
  // //
  // when(permitKeywordRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPermitKeywordEntity()));
  // PermitKeyword keyword = getPermitKeywordObj();
  // keyword.setPermitKeywordId(null);
  // keyWordService.storePermitKeyword(userId, contextId, keyword);
  // verify(permitKeywordRepo).save(Mockito.any());
  // }



  // private methods
  private ProjectKeyword getProjectKeywordObj() {
    ProjectKeyword keyword = new ProjectKeyword();
    keyword.setKeywordText(keywordText);
    keyword.setKeywordCategoryId(keywordCategoryId);
    keyword.setKeywordCategory(keywordCategory);
    keyword.setKeywordId(keywordId);
    return keyword;
  }

  private List<Long> getCandidateKeywordList() {
    return Arrays.asList(1l, 2l);
  }

  private List<ProjectKeywordEntity> getProjectKeywordList() {
    List<ProjectKeywordEntity> entities = new ArrayList<>();
    ProjectKeywordEntity entity = new ProjectKeywordEntity();
    entity.setCreatedById(userId);
    entity.setProjectId(projectId);
    entity.setSystemDetected(1);
    entity.setKeywordId(10L);
    entities.add(entity);
    return entities;
  }


  private List<KeywordText> getKeywordTextList() {
    List<KeywordText> texts = new ArrayList<>();
    KeywordText text = new KeywordText();
    text.setActiveInd(1);
    text.setKeywordId(1L);
    text.setKeywordText(keywordText);
    texts.add(text);
    return texts;
  }

  private PermitKeywordEntity getPermitKeywordEntity() {
    PermitKeywordEntity entity = new PermitKeywordEntity();
    entity.setStartDate(new Date());
    entity.setKeywordId(keywordId);
    entity.setEndDate(new Date());
    return entity;
  }

  private PermitKeyword getPermitKeywordObj() {
    PermitKeyword keyword = new PermitKeyword();
    keyword.setPermitKeywordId(keywordId);
    keyword.setKeywordCategoryId(5L);
    keyword.setPermitTypeCode("CE");
    keyword.setStartDate("12/23/2021");
    keyword.setEndDate("03/14/2024");


    return keyword;
  }

  private KeywordTextEntity getKeywordTextEntityObj() {
    KeywordTextEntity entity = new KeywordTextEntity();
    entity.setKeywordId(keywordCategoryId);
    entity.setKeywordText(keywordText);
    return entity;
  }


  private KeywordText getKeywordTextObj() {
    KeywordText text = new KeywordText();
    text.setActiveInd(1);
    text.setEndDate("12/23/2023");
    text.setKeywordCategory(keywordCategory);
    text.setStartDate("12/20/2023");
    text.setKeywordText(keywordText);
    text.setKeywordCategoryId(keywordCategoryId);
    return text;
  }

  private KeywordCategory getKeywordCategoryObj() {
    KeywordCategory category = new KeywordCategory();
    category.setKeywordCategory(keywordCategory);
    category.setKeywordCategoryId(keywordId);
    return category;
  }


}
