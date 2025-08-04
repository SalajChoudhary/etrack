package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import dec.ny.gov.etrack.dart.db.entity.PermitType;
import dec.ny.gov.etrack.dart.db.entity.ProjectKeyword;
import dec.ny.gov.etrack.dart.db.service.ETrackKeywordService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackKeywordControllerTest {
	
	@Mock
	private ETrackKeywordService keywordService; 
	
	@InjectMocks
	private ETrackKeywordController eTrackKeywordController;

	@Test
	public void retrieveKeywordTextsTest() {
		Map<String, List<KeywordText>> keywordData = new HashMap<>();
		when(keywordService.retrieveKeywordTexts(Mockito.anyString(), Mockito.anyString())).thenReturn(keywordData);
		Object object = eTrackKeywordController.retrieveKeywordTexts("dxdev");
		assertNotNull(object);
	}
	
	@Test
	public void retrieveKeywordTextsByIdTest() {
		Map<String, List<KeywordText>> keywordData = new HashMap<>();
		when(keywordService.retrieveKeywordTextsById(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong())).thenReturn(keywordData);
		Object object = eTrackKeywordController.retrieveKeywordTextsByCategoryId("dxdev",1L);
		assertNotNull(object);
	}
	
	@Test
	public void retrieveKeywordCategoriesTest() {
		List<KeywordCategory> keywordCategories = new ArrayList<>();
		when(keywordService.retrieveKeywordCategories(Mockito.anyString(), Mockito.anyString()
				)).thenReturn(keywordCategories);
		Object object = eTrackKeywordController.retrieveKeywordCategories("dxdev");
		assertNotNull(object);
	}
	
	@Test
	public void retrievePermitKeywordTextsTest() {
		List<PermitKeyword> permitKeywords = new ArrayList<>();
		when(keywordService.retrievePermitKeywordTexts(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong())).thenReturn(permitKeywords);
		Object object = eTrackKeywordController.retrievePermitKeywordTexts("dxdev",2L);
		assertNotNull(object);
	}
	

	@Test
	public void retrieveKeywordTextsForProjectTest() {
		Map<String,Map<String, List<ProjectKeyword>>> keywordData = new HashMap<>();
		when(keywordService.retrieveKeywordTextsForProject(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong())).thenReturn(keywordData);
		Object object = eTrackKeywordController.retrieveKeywordTextsForProject("dxdev",1L);
		assertNotNull(object);
	}
	
	@Test
	public void retrieveCandidateKeywordTextsTest() {
		List<ProjectKeyword> candidateKeywordText = new ArrayList<>();
		when(keywordService.retrieveCandidateKeywordTexts(Mockito.anyString(), Mockito.anyString()
				)).thenReturn(candidateKeywordText);
		Object object = eTrackKeywordController.retrieveCandidateKeywordTexts("dxdev");
		assertNotNull(object);
	}
	
	@Test
	public void retrievePermitTypesTest() {
		List<dec.ny.gov.etrack.dart.db.model.PermitType> permitTypes = new ArrayList<>();
		when(keywordService.retrieveAllActivePermitTypes(Mockito.anyString(), Mockito.anyString()
				)).thenReturn(permitTypes);
		Object object = eTrackKeywordController.retrievePermitTypes("dxdev");
		assertNotNull(object);
	}

}
