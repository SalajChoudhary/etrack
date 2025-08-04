package dec.ny.gov.etrack.permit.controller;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.GIReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.GeographicalInquiryNoteView;
import dec.ny.gov.etrack.permit.model.GeographicalInquirySubmittalResponse;
import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.permit.service.impl.SpatialInquiryServiceImpl;
import io.swagger.annotations.ApiParam;

@RunWith(SpringJUnit4ClassRunner.class)
public class SpatialInquiryControllerTest {

	@InjectMocks
	private SpatialInquiryController inquiryController;
	
	@Mock
	private SpatialInquiryServiceImpl inquiryService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private String userId = "jxpuvoge";
	private String token = "123456";
	private String contextId = "Context";
	private Long inquiryId = 2L;
	
	
	//saveSpatialPolygonInquiryDetails tests
	
	@Test
	public void testSaveSpatialPolygonInquiryDetailsCallsService() {
		inquiryController.saveSpatialPolygonInquiryDetails(userId, contextId, new SpatialInquiryRequest());
		verify(inquiryService).saveSpatialInquiryDetail(Mockito.anyString(), Mockito.anyString(), Mockito.any(SpatialInquiryRequest.class));
	}
	
	
	//uploadSpatialInquiryMapDetails test cases
	@Test
	public void testUploadSpatialInquiryMapDetailsCallsInquiryService() {
		inquiryController.uploadSpatialInquiryMapDetails(userId, "URL", token, inquiryId);
		verify(inquiryService).uploadSpatialInquiryMap(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString());
	}
  
	
	//submitSpatialInquiry test cases	
	@Test
	public void testsubmitSpatialInquiryCallsService() {
		inquiryController.submitSpatialInquiry(token, userId, inquiryId);
		verify(inquiryService).submitSpatialInquiry(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
	}

	//submitSpatialInquiryResponse test cases	
	@Test
	public void testSubmitSpatialInquiryResponseCallsService() {
		inquiryController.submitSpatialInquiryResponse(token, userId, inquiryId, new GeographicalInquirySubmittalResponse());
		verify(inquiryService).submitSpatialInquiryResponse(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.any());
	}

	
	//assignGeographicalInquiryToAnalyst test cases
	@Test
	public void testAssignGeographicalInquiryToAnalystThrowsBREForInvalidInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Inquiry Id is not passed.");
		inquiryController.assignGeographicalInquiryToAnalyst(userId, null, getAssignmentNote());
	}
	
	@Test
	public void testAssignGeographicalInquiryToAnalystCallsServiceToUpdateAssignment() {
		inquiryController.assignGeographicalInquiryToAnalyst(userId, inquiryId, getAssignmentNote());
		verify(inquiryService).updateGeographicalInquiryAssignment(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}
	
	@Test
	public void testAssignGeographicalInquiryToAnalystThrowsBREForNoAnalystId() {
		AssignmentNote note = getAssignmentNote();
		note.setAnalystId(null);
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("User Assigned details cannot be empty");
		inquiryController.assignGeographicalInquiryToAnalyst(userId, inquiryId,note );
	}
	
	//addOrUpdateNote test cases
	@Test
	public void testAddOrUpdateNoteThrowsBREForInvalidInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Inquiry id  and/or User id is blank or invalid passed");
		inquiryController.addOrUpdateNote(userId, null, new GeographicalInquiryNoteView());
	}
	
	@Test
	public void testAddOrUpdateNoteCallsServiceToAddNote() {
		inquiryController.addOrUpdateNote(userId, inquiryId, new GeographicalInquiryNoteView());
		verify(inquiryService).addNotes(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}
	
	//deleteGeographicalInquiryNote test cases
	@Test
	public void testDeleteGeographicalInquiryNoteThrowsBREForInvalidInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Inquiry ID is Required");
		inquiryController.deleteGeographicalInquiryNote(userId, null, 1L);
	}
	
	@Test
	public void testDeleteGeographicalInquiryNoteCallsServiceToDeleteNote() {
		inquiryController.deleteGeographicalInquiryNote(userId, inquiryId, 1L);
		verify(inquiryService).deleteNote(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
	}

	
	//assignProgramReviewerToDocument  test cases
	@Test
	public void testAssignProgramReviewerToDocumentThrowsBREForInvalidInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Inquiry id is Required");
		inquiryController.assignProgramReviewerToDocument(userId, null, new DocumentReview());
	}
	
	@Test
	public void testAssignProgramReviewerToDocumentThrowsBREForInvalidReviewDetail() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Reviewer details details cannot be empty");
		inquiryController.assignProgramReviewerToDocument(userId, inquiryId, new DocumentReview());
	}
	
	@Test
	public void testAssignProgramReviewerToDocumentCallsServiceToUpdateReviewDetails() {
		DocumentReview doc = new DocumentReview();
		doc.setDateAssigned("12/23/2023");
		doc.setDueDate("12/12/2024");
		doc.setReviewerName("John doe");
		inquiryController.assignProgramReviewerToDocument(userId, inquiryId, doc);
		verify(inquiryService).updateDocumentReviewerDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}
	
	//updateGeoInquiryDocReviewCompletion test cases
	@Test
	public void testUpdateGeoInquiryDocReviewCompletionThrowsBREForInvalidInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Inquiry id is Required.");
		inquiryController.updateGeoInquiryDocReviewCompletion(userId, null, new GIReviewCompletionDetail());
		
	}
	
	@Test
	public void testUpdateGeoInquiryDocReviewCompletionCallsServiceToUpdate() {
		
		inquiryController.updateGeoInquiryDocReviewCompletion(userId, inquiryId, new GIReviewCompletionDetail());
		verify(inquiryService).updateDocumentReviewerCompletionDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}

	
	//skipRequiredDocumentUploadForInquiries test cases
	@Test
	public void testSkipRequiredDocumentUploadForInquiriesCallsService() {
		inquiryController.skipRequiredDocumentUploadForInquiries(userId, inquiryId);
		verify(inquiryService).skipRequiredDocumentUploadForInquiries(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
	}


	
	//private methods
	private AssignmentNote getAssignmentNote() {
		AssignmentNote note = new AssignmentNote();
		note.setAnalystId("Analyst1");
		note.setAnalystName("John Doe");
		note.setComments("comment");
		note.setAnalystId("2");
		return note;
	}
}
