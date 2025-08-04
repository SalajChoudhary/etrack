package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackKeywordServiceImplTest {
	
	  @Mock
	  private KeywordCategoryRepo keywordCategoryRepo;
	  @Mock
	  private PermitKeywordRepo permitKeywordRepo;
	  @Mock
	  private KeywordTextRepo keywordTextRepo;
	  @Mock
	  private ProjectKeywordRepo projectKeywordRepo;
	  @Mock
	  private ProjectRepo projectRepo;
	  
	  @InjectMocks
	  private ETrackKeywordServiceImpl eTrackKeywordServiceImpl;

	@Test
	public void retrieveKeywordTextsTest() {
		Map<String, List<KeywordText>> categoryAndKeywordTextMap = new HashMap<>();
		List<KeywordText> keywordTexts = new ArrayList<>();
		KeywordText keywordText = new KeywordText();
		keywordText.setKeywordCategoryId(1L);
		keywordTexts.add(keywordText);
		when(keywordTextRepo.findAllActiveKeywordTexts()).thenReturn(keywordTexts);
		Map<String, List<KeywordText>> retriveKeywordText = eTrackKeywordServiceImpl.retrieveKeywordTexts("dxdevada", "");
		
	}
	
	@Test
	public void retrieveKeywordCategories() {
		List<KeywordCategory> list = eTrackKeywordServiceImpl.retrieveKeywordCategories("", "");
	    assertNotNull(list);
	}
	
	@Test
	public void retrievePermitKeywordTexts() {
		List<PermitKeyword> list =  eTrackKeywordServiceImpl.retrievePermitKeywordTexts("","",1L);
		 assertNotNull(list);
	}
	
	@Test
	public void retrievePermitKeywordNullTexts() {
		List<PermitKeyword> list =  eTrackKeywordServiceImpl.retrievePermitKeywordTexts("","",null);
		 assertNotNull(list);
	}
	
	@Test
	public void retrieveKeywordTextsForProject() {
		 String userId = "",  contextId="";  Long projectId = 1L;
		 List<ProjectKeyword> keywordsAndAllocatedToProject = new ArrayList();
		 ProjectKeyword pk = new ProjectKeyword();
		 keywordsAndAllocatedToProject.add(pk);
	     when(projectKeywordRepo.findAllKeywordsAndProjectAssociatedKeywords(projectId)).thenReturn(keywordsAndAllocatedToProject);
	     Project p = new Project();  
	     p.setOriginalSubmittalDate(new Date());
		 Optional<Project> projectAvail = Optional.of(p);
	     when(projectRepo.findById(projectId)).thenReturn(projectAvail);
		Map<String, Map<String, List<ProjectKeyword>>> map= eTrackKeywordServiceImpl.retrieveKeywordTextsForProject(
			      userId,contextId,projectId);
		 assertNotNull(map);
	}
	
	@Test
	public void retrieveKeywordTextsForProjectOneTest() {
		 String userId = "",  contextId="";  Long projectId = 1L;
		 List<ProjectKeyword> keywordsAndAllocatedToProject = new ArrayList();
		 ProjectKeyword pk = new ProjectKeyword();
		 pk.setKeywordCategoryId(-1L);
		 keywordsAndAllocatedToProject.add(pk);
	     when(projectKeywordRepo.findAllKeywordsAndProjectAssociatedKeywords(projectId)).thenReturn(keywordsAndAllocatedToProject);
	     Project p = new Project();  
	     p.setOriginalSubmittalInd(1);
	     p.setOriginalSubmittalDate(new Date());
		 Optional<Project> projectAvail = Optional.of(p);
	     when(projectRepo.findById(projectId)).thenReturn(projectAvail);
		Map<String, Map<String, List<ProjectKeyword>>> map= eTrackKeywordServiceImpl.retrieveKeywordTextsForProject(
			      userId,contextId,projectId);
		 assertNotNull(map);
	}
	
	@Test
	public void retrieveKeywordTextsForProjectTwoTest() {
		 String userId = "",  contextId="";  Long projectId = 1L;
		 List<ProjectKeyword> keywordsAndAllocatedToProject = new ArrayList();
		 ProjectKeyword pk = new ProjectKeyword();
		 pk.setSystemDetected(1);
		 keywordsAndAllocatedToProject.add(pk);
	     when(projectKeywordRepo.findAllKeywordsAndProjectAssociatedKeywords(projectId)).thenReturn(keywordsAndAllocatedToProject);
	     Project p = new Project();  
	     p.setOriginalSubmittalInd(1);
	     p.setOriginalSubmittalDate(new Date());
		 Optional<Project> projectAvail = Optional.of(p);
	     when(projectRepo.findById(projectId)).thenReturn(projectAvail);
		Map<String, Map<String, List<ProjectKeyword>>> map= eTrackKeywordServiceImpl.retrieveKeywordTextsForProject(
			      userId,contextId,projectId);
		 assertNotNull(map);
	}
	
		
	@Test
	public void retrieveKeywordTextsByIdTest() {
		String userId="",  contextId="";
	      Long categoryId=1L;
		 List<KeywordText> keywordTexts = new ArrayList();
		 KeywordText kt = new KeywordText();
		 kt.setKeywordCategory("1");
		 keywordTexts.add(kt);
		 KeywordText kt1 = new KeywordText();
//		 kt1.setKeywordCategory("1");
		 keywordTexts.add(kt1);
		 when(keywordTextRepo.findAllActiveKeywordTextsByCategoryId(categoryId)).thenReturn(keywordTexts);
		 Map<String, List<KeywordText>> list = eTrackKeywordServiceImpl.retrieveKeywordTextsById( userId,  contextId,
			       categoryId);
		 assertNotNull(list);
	}
	

	
	@Test
	public void retrieveAllActivePermitTypes() {
		String userId="", contextId="";
		List<String> permitTypeAndDescs = new ArrayList();
		permitTypeAndDescs.add("one,two,three");
		when(permitKeywordRepo.findAllPermitTypes()).thenReturn(permitTypeAndDescs);
		 List<PermitType> list = eTrackKeywordServiceImpl.retrieveAllActivePermitTypes( userId,  contextId);
		 assertNotNull(list);
	}
	
	@Test
	public void retrieveCandidateKeywordTexts() {
		List<ProjectKeyword> list = eTrackKeywordServiceImpl.retrieveCandidateKeywordTexts("","");
		 assertNotNull(list);
	}

}
