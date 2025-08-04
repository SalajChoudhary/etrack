package dec.ny.gov.etrack.permit.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.ProjectNoteView;
import dec.ny.gov.etrack.permit.service.ETrackFoilLigitationService;
import dec.ny.gov.etrack.permit.service.ETrackNoteService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackNoteServiceControllerTest {

	
	@InjectMocks
	private ETrackNoteServiceController eTrackNoteServiceController;
	
	@Mock
    private ETrackNoteService eTrackNoteService;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	
	private String userId = "jxpuvoge";
	private Long projectId = 1002L;
	private String noteId = "123";

	
	//getNotesTests
	
	@Test
	public void testGetNotesReturnsList() {
		assertTrue(this.eTrackNoteServiceController.getNotes(userId, projectId) instanceof List);
	}
	
	
	@Test
	public void testGetNotesThrowsBREForNullUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.getNotes(null, projectId);
	}
	
	@Test
	public void testGetNotesThrowsBREForNullProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.getNotes(userId, null);
	}
	
	//getNoteByNoteId tests
	
	@Test
	public void testGetNoteByNoteIdThrowsBREForNullProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.getNoteByNoteId(userId, null, 10L);
	}
	
	@Test
	public void testGetNoteByNoteIdThrowsBREForNullUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.getNoteByNoteId(null, projectId, 10L);
	}
	
	@Test
	public void testGetNoteByNoteIdThrowsBREForNullNoteId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.getNoteByNoteId(userId, projectId, null);
	}
	
	@Test
	public void testGetNoteByNoteIdCallsService() {
		this.eTrackNoteServiceController.getNoteByNoteId(userId, projectId, 10L);	
		verify(eTrackNoteService).getNote(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
		}
	
	
	
	@Test
	public void testAddNoteThrowsBREForNoProjectId() {
		ProjectNoteView projNote = new ProjectNoteView();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.addNote(userId, null, projNote);
	}
	
	@Test
	public void testAddNoteThrowsBREForNoUserId() {
		ProjectNoteView projNote = new ProjectNoteView();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.addNote(null, projectId, projNote);
	}
	
	@Test
	public void testAddNoteCallsProjectService() {
		ProjectNoteView projNote = new ProjectNoteView();
		this.eTrackNoteServiceController.addNote(userId, projectId, projNote);
		verify(eTrackNoteService, times(1)).addNotes(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ProjectNoteView.class));
	}

	
	//updateNote tests
	
	@Test
	public void testUpdateNoteThrowsBREForInvalidProjId() {
		ProjectNoteView projectNote = new ProjectNoteView();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.updateNote(userId, null, projectNote);
	}
	
	@Test
	public void testUpdateNoteThrowsBREForInvalidUserId() {
		ProjectNoteView projectNote = new ProjectNoteView();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.updateNote(null, projectId, projectNote);
	}
	
	@Test
	public void testUpdateNoteCallsProjectService() {
		ProjectNoteView projectNote = new ProjectNoteView();
		this.eTrackNoteServiceController.updateNote(userId, projectId, projectNote);
		verify(eTrackNoteService, times(1)).addNotes(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ProjectNoteView.class));
	}
	
	//deleteNote tests
	@Test
	public void testDeleteThrowsBREForInvalidUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.deleteNote(null, projectId, noteId);
	}
	
	@Test
	public void testDeleteThrowsBREForInvalidProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.deleteNote(userId, null, noteId);
	}
	
	@Test
	public void testDeleteThrowsBREForInvalidNoteId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.eTrackNoteServiceController.deleteNote(userId, projectId, null);
	}
	
	
}
