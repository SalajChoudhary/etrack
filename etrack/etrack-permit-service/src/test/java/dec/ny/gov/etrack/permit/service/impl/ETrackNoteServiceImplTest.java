package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import dec.ny.gov.etrack.permit.entity.ActionNoteEntity;
import dec.ny.gov.etrack.permit.entity.Invoice;
import dec.ny.gov.etrack.permit.entity.InvoiceFeeDetail;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;
import dec.ny.gov.etrack.permit.entity.ProjectNote;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.MissingDocument;
import dec.ny.gov.etrack.permit.model.ProjectNoteView;
import dec.ny.gov.etrack.permit.repo.ActionNoteRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceFeeDetailRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectNoteRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackNoteServiceImplTest {

	@InjectMocks
	private ETrackNoteServiceImpl noteServiceImpl;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Mock
	private ProjectNoteRepo projectNoteRepo;
	
	@Mock
	private ActionNoteRepo actionNoteRepo;
	
	@Mock
	private InvoiceFeeDetailRepo invoiceFeeDetailRepo;
	
	@Mock
	private InvoiceRepo invoiceRepo;
	
	@Mock
	private ProjectAlertRepo projectAlertRepo;
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;
	private Long noteId = 1L;
	private String fmisInvoiceNum = "12345";
	private int invoiceFee = 100;
	private String invoiceFeeType = "Type";
	private final String systemUserId = "SYSTEM";
	
	
	//**completed, coverage is 91%**
	
	//generateMissingReqDocumentNote tests
	@Test
	public void testGenerateMissingReqDocumentNoteIsSystemGenerated() {
		ProjectNote note = getProjectNoteList().get(0);
		note.setCreatedById(systemUserId);
		when(projectNoteRepo.save(Mockito.any())).thenReturn(note);
		noteServiceImpl.generateMissingReqDocumentNote(userId, contextId, projectId, getMissingDocumentObj());
		assertEquals(systemUserId, note.getCreatedById());
	}
	
	@Test
	public void testGenerateMissingReqDocumentNoteSavesProjectNote() {
		when(projectNoteRepo.save(Mockito.any())).thenReturn(getProjectNoteList().get(0));
		noteServiceImpl.generateMissingReqDocumentNote(userId, contextId, projectId, getMissingDocumentObj());
		verify(projectNoteRepo).save(Mockito.any());
	}
	
	
	//getNotes tests
	@Test
	public void testGetNotesReturnsEmptyListWhenNoNotesFound() {
		when(projectNoteRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(getProjectNoteList());
		when(actionNoteRepo.findActionNoteByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		List<ProjectNoteView> result = this.noteServiceImpl.getNotes(userId, contextId, projectId);
		assertTrue(CollectionUtils.isEmpty(result));
	}
	
	
	@Test
	public void testGetNotesWhenCreatingNonSystemGeneratedNote() {
		when(projectNoteRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(getProjectNoteList());
		when(actionNoteRepo.findActionNoteByProjectId(Mockito.anyLong())).thenReturn(getActionNoteList());
		List<ProjectNoteView> resultList = noteServiceImpl.getNotes(userId, contextId, projectId);
		ProjectNoteView result = resultList.get(0);
		assertEquals("Comment", result.getComments());
		assertEquals(1L, result.getProjectNoteId());
		assertEquals("N", result.getSystemGenerated());
		
	}
	
	@Test
	public void testGetNotesWithMissingRequiredDocuments() {
		when(projectNoteRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(getProjectNoteList());
		List<ActionNoteEntity> actionNoteList = getActionNoteList();
		actionNoteList.get(0).setActionNote("1,2,3");
		when(actionNoteRepo.findActionNoteByProjectId(Mockito.anyLong())).thenReturn(actionNoteList);
		List<String> docTitles = Arrays.asList("Doc Name", "Doc Name 2");
		when(projectNoteRepo.findAllDocumentTitleByIds(Mockito.anySet())).thenReturn(docTitles);
		List<ProjectNoteView> resultList = noteServiceImpl.getNotes(userId, contextId, projectId);
		ProjectNoteView result = resultList.get(0);
		assertEquals(2, result.getMissingReqdDoc().size());
		assertTrue(result.getActionNote().contains("Doc Name"));
	}
	
	@Test
	public void testGetNotesGeneratesSystemGeneratedNote() {
		String expectedResult = "Y";
		when(projectNoteRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(getProjectNoteList());
		List<ActionNoteEntity> actionNoteList = getActionNoteList();
		actionNoteList.get(0).setActionNote("1,2,3");
		actionNoteList.get(0).setCreatedById(systemUserId);
		when(actionNoteRepo.findActionNoteByProjectId(Mockito.anyLong())).thenReturn(actionNoteList);
		List<String> docTitles = Arrays.asList("Doc Name", "Doc Name 2");
		when(projectNoteRepo.findAllDocumentTitleByIds(Mockito.anySet())).thenReturn(docTitles);
		List<ProjectNoteView> resultList = noteServiceImpl.getNotes(userId, contextId, projectId);
		ProjectNoteView result = resultList.get(0);
		assertEquals(expectedResult, result.getSystemGenerated());
	}
	

	
	//getNote tests
	@Test
	public void testGetNoteThrowsBREForNoActionNoteFound() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no data available for the input note Id");
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		noteServiceImpl.getNote(userId, contextId, projectId, noteId);
	}
	
	@Test
	public void testGetNoteThrowsBREForNoInvoiceFound() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no invoice available for this project Id");
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getActionNoteEntityObj());
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		noteServiceImpl.getNote(userId, contextId, projectId, noteId);
	}
	
	@Test
	public void testGetNoteGeneratesSystemNoteWithNoInvoiceFeeType() {
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getActionNoteEntityObj());
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getInvoiceObj());
		when(invoiceFeeDetailRepo.findFeeDetailsForFeeTypes()).thenReturn(getInvoiceFeeDetailList());
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals("Y", result.getSystemGenerated());
		assertEquals("Action", result.getActionNote());
		assertNull(result.getUpdatedDate());
	}
	
	@Test
	public void testGetNoteCorrectlyMapsInvoiceFeeType1Data() {
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getActionNoteEntityObj());
		Invoice invoice = getInvoiceObj();
		invoice.setInvoiceFeeType1(invoiceFeeType);
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(invoice);
		when(invoiceFeeDetailRepo.findFeeDetailsForFeeTypes()).thenReturn(getInvoiceFeeDetailList());
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals(fmisInvoiceNum, result.getPaymentActionNote().getInvoiceNumber());
		assertEquals(invoiceFee, result.getPaymentActionNote().getProjectTypeFee1());
		assertNull(result.getMissingReqdDoc());
	}
	
	
	@Test
	public void testGetNoteCorrectlyMapsInvoiceFeeType2Data() {
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getActionNoteEntityObj());
		Invoice invoice = getInvoiceObj();
		invoice.setInvoiceFeeType2(invoiceFeeType);
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(invoice);
		when(invoiceFeeDetailRepo.findFeeDetailsForFeeTypes()).thenReturn(getInvoiceFeeDetailList());
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals("Desc", result.getPaymentActionNote().getProjectType2());
		assertEquals(invoiceFee, result.getPaymentActionNote().getProjectTypeFee2());
		assertNull(result.getMissingReqdDoc());
	}
	
	@Test
	public void testGetNoteCorrectlyMapsInvoiceFeeType3Data() {
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getActionNoteEntityObj());
		Invoice invoice = getInvoiceObj();
		invoice.setInvoiceFeeType3(invoiceFeeType);
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(invoice);
		when(invoiceFeeDetailRepo.findFeeDetailsForFeeTypes()).thenReturn(getInvoiceFeeDetailList());
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals("Desc", result.getPaymentActionNote().getProjectType3());
		assertEquals(invoiceFee, result.getPaymentActionNote().getProjectTypeFee3());
		assertNull(result.getMissingReqdDoc());
	}
	
	@Test
	public void testGetNoteReturnsNonSystemGeneratedNote() {
		ActionNoteEntity noteEntity = getActionNoteEntityObj();
		noteEntity.setCreatedById(userId);
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(noteEntity);
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals("N", result.getSystemGenerated());
		
	}
	
	@Test
	public void testGetNoteReturnsValidMissingReqdDocumentNote() {
		ActionNoteEntity noteEntity = getActionNoteEntityObj();
		noteEntity.setActionTypeCode(17);
		noteEntity.setActionNote("10");
		when(actionNoteRepo.findActionNoteByNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(noteEntity);
		when(invoiceRepo.findInvoiceByProjectIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getInvoiceObj());
		when(invoiceFeeDetailRepo.findFeeDetailsForFeeTypes()).thenReturn(getInvoiceFeeDetailList());
		when(projectNoteRepo.findAllDocumentTitleByIds(Mockito.anySet())).thenReturn(Arrays.asList("DOC"));
		ProjectNoteView result = noteServiceImpl.getNote(userId, contextId, projectId, noteId);
		assertEquals("Project ID " + projectId + " missing the following documents", result.getActionNote());
		assertEquals("DOC", result.getMissingReqdDoc().get(0));
	}
	
	//addNotes tests
	@Test 
	public void testAddNotesThrowsBREForInvalidActionNote() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid input parameter");
		ProjectNoteView projectNoteView = getProjectNoteViewObj();
		projectNoteView.setActionNote("");
		noteServiceImpl.addNotes(userId, contextId, projectId, projectNoteView);
	} 
	
	@Test
	public void testAddNotesThrowsBREForInvalidProjectNoteId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no note avaialble for the requested project note id");
		ProjectNoteView projectNoteView = getProjectNoteViewObj();
		when(projectNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		noteServiceImpl.addNotes(userId, contextId, projectId, projectNoteView);
	}
	
	@Test
	public void testAddNotesThrowsBREForInvalidActionDate() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Action date in the note request not the valid format i.e. MM/dd/yyyy");
		ProjectNote projectNote = getProjectNoteList().get(0);
		ProjectNoteView projectNoteView = getProjectNoteViewObj();
		projectNoteView.setActionDate("2023/03/09");
		when(projectNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(projectNote));
		noteServiceImpl.addNotes(userId, contextId, projectId, projectNoteView);
	}
	
	
	@Test
	public void testAddNotesUpdatesExistingNote() {
		ProjectNoteView projectNoteView = getProjectNoteViewObj();
		ProjectNote projectNote = getProjectNoteList().get(0);
		Long projectNoteId = 105L;
		when(projectNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(projectNote));
		ProjectNote returnedNote = new ProjectNote();
		returnedNote.setProjectNoteId(projectNoteId);
		when(projectNoteRepo.save(Mockito.any())).thenReturn(returnedNote);
		ProjectNoteView resultNote = noteServiceImpl.addNotes(systemUserId, contextId, projectId, projectNoteView);
		assertEquals(projectNoteId, resultNote.getProjectNoteId());
	}
	
	@Test
	public void testAddNotesSavesNewNote() {
		ProjectNoteView projectNoteView = getProjectNoteViewObj();
		projectNoteView.setProjectNoteId(null);
		ProjectNote projectNote = getProjectNoteList().get(0);
		Long projectNoteId = 105L;
		when(projectNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(projectNote));
		ProjectNote returnedNote = new ProjectNote();
		returnedNote.setProjectNoteId(projectNoteId);
		returnedNote.setCreatedById(userId);
		returnedNote.setComments("Comment");
		when(projectNoteRepo.save(Mockito.any())).thenReturn(returnedNote);
		noteServiceImpl.addNotes(systemUserId, contextId, projectId, projectNoteView);
		assertEquals(userId, returnedNote.getCreatedById());
		assertEquals("Comment", returnedNote.getComments());
	}
	
	//deleteNotes tests
	@Test
	public void testDeleteNoteThrowsBREForInvalidParameter() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid input parameter");
		when(projectAlertRepo.findByProjectIdAndProjectNoteId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectNoteRepo.findAllByProjectNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Arrays.asList());
		noteServiceImpl.deleteNote(systemUserId, contextId, projectId, noteId);
	}
	
	
	@Test
	public void testDeleteNoteDeletesAllProjectAlertsForNote() {
		when(projectAlertRepo.findByProjectIdAndProjectNoteId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getProjectAlertList());
		when(projectNoteRepo.findAllByProjectNoteIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getProjectNoteList());
		noteServiceImpl.deleteNote(userId, contextId, projectId, noteId);
		verify(projectAlertRepo).deleteAll(Mockito.anyIterable());
	}
	
	
	
	private ActionNoteEntity getActionNoteEntityObj() {
		ActionNoteEntity actionNoteEntity = new ActionNoteEntity();
		actionNoteEntity.setActionTypeCode(15);
		actionNoteEntity.setComments("Comment");
		actionNoteEntity.setActionNote("Action");
		actionNoteEntity.setProjectNoteId(10L);
		actionNoteEntity.setCreatedById(systemUserId);
		actionNoteEntity.setActionDate(new Date());
		return actionNoteEntity;
	}
	
	private Invoice getInvoiceObj() {
		Invoice invoice = new Invoice();
		invoice.setPaymentConfirmnId("10");
		invoice.setFmisInvoiceNum(fmisInvoiceNum);
		return invoice;
	}
	
	private List<InvoiceFeeDetail> getInvoiceFeeDetailList(){
		InvoiceFeeDetail feeDetail = new InvoiceFeeDetail();
		feeDetail.setInvoiceFee(invoiceFee);
		feeDetail.setPermitTypeCode("CE");
		feeDetail.setPermitTypeDesc("Desc");
		feeDetail.setInvoiceFeeType(invoiceFeeType);
		return Arrays.asList(feeDetail);
	}
	

	private ProjectNoteView getProjectNoteViewObj() {
		ProjectNoteView noteView = new ProjectNoteView();
		noteView.setActionDate("12/23/2021");
		noteView.setActionType(1);
		noteView.setActionNote("Note");
		//Value here is 1L
		noteView.setProjectNoteId(noteId);
		return noteView;
	}
	
	private MissingDocument getMissingDocumentObj() {
		MissingDocument document = new MissingDocument();
		return document;
	}	
	
	private List<ProjectAlert> getProjectAlertList(){
		ProjectAlert alert = new ProjectAlert();
		return Arrays.asList(alert);
		}
	
	private List<ProjectNote> getProjectNoteList(){
		ProjectNote projectNote = new ProjectNote();
		projectNote.setActionTypeCode(2);
		projectNote.setProjectNoteId(100L);
		return Arrays.asList(projectNote);
	}
	
	private List<ActionNoteEntity> getActionNoteList(){
		ActionNoteEntity actionNoteEntity = new ActionNoteEntity();
		actionNoteEntity.setActionTypeDesc("DESC");
		actionNoteEntity.setActionDate(new Date());
		actionNoteEntity.setActionTypeCode(17);
		actionNoteEntity.setComments("Comment");
		actionNoteEntity.setProjectNoteId(1L);
		return Arrays.asList(actionNoteEntity);
	}
	
}
