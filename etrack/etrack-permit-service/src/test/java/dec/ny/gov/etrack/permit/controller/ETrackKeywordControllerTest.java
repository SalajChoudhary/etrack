package dec.ny.gov.etrack.permit.controller;

import static org.mockito.Mockito.verify;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.KeywordCategory;
import dec.ny.gov.etrack.permit.model.KeywordText;
import dec.ny.gov.etrack.permit.model.PermitKeyword;
import dec.ny.gov.etrack.permit.model.ProjectKeyword;
import dec.ny.gov.etrack.permit.service.ETrackKeywordService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackKeywordControllerTest {
	
	@InjectMocks
	private ETrackKeywordController keywordController;
	
	@Mock
	private ETrackKeywordService eTrackKeywordService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private String userId = "jxpuvoge";
	private Long projectId = 1002L;
	private String noteId = "123";
	private String contextId ="ContextID";
	
	@Test
	public void testStoreKeywordTextCallsServiceToStoreKeyword() {
		keywordController.storeKeywordText(userId, getKeywordTextObj());
		verify(eTrackKeywordService).storeKeywordText(Mockito.anyString(), Mockito.anyString(), Mockito.any());
	}
	
	
	@Test
	public void testStoreKeywordCategoryCallsServiceToStoreCategory() {
		keywordController.storeKeywordCategory(userId, getKeyWordCategoryObj());
		verify(eTrackKeywordService).storeKeywordCategory(Mockito.anyString(), Mockito.anyString(), Mockito.any());
	}
	
	@Test
	public void testStorePermitKeyword() {
		keywordController.storePermitKeyword(userId, getPermitKeywordObj());
		verify(eTrackKeywordService).storePermitKeyword(Mockito.anyString(), Mockito.anyString(), Mockito.any());
	}
	
	@Test
	public void testPersistKeywordsCallsServiceToSaveKeyword() {
		keywordController.persistKeywords(userId, projectId, "token", 1, getProjectKeywordObj());
		verify(eTrackKeywordService).persistKeywordTextToProject(
		    Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.any());
	}
	
	private ProjectKeyword getProjectKeywordObj() {
		ProjectKeyword keyword = new ProjectKeyword();
		return keyword;
	}
	
	@Test
	public void testReplaceCandidateKeywordsWithPermitKeywordThrowsBREForNoCandidateKeyword() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Candidate keywords passed");
		keywordController.replaceCandidateKeywordsWithPermitKeyword(userId, 1L, Arrays.asList());
	}
	
	@Test
	public void testReplaceCandidateKeywordsWithPermitKeywordThrowsBREForInvalidReplacementKeyword() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no replacement keywords passed");
		keywordController.replaceCandidateKeywordsWithPermitKeyword(userId, -1L, Arrays.asList(1L,2L));
	}
	
	private PermitKeyword getPermitKeywordObj() {
		PermitKeyword keyword = new PermitKeyword();
		return keyword;
	}
	
	private KeywordCategory getKeyWordCategoryObj() {
		KeywordCategory category = new KeywordCategory();
		category.setKeywordCategory("Cat");
		category.setKeywordCategoryId(10L);
		return category;
	}

	private KeywordText getKeywordTextObj() {
		KeywordText text = new KeywordText();
		text.setActiveInd(1);
		text.setEndDate("12/23/2023");
		text.setKeywordCategory("Cat");
		text.setStartDate("12/20/2023");
		text.setKeywordText("Text");
		return text;
	}
	
}
