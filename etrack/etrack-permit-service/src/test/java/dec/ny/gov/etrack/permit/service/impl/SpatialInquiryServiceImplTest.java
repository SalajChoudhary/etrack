//package dec.ny.gov.etrack.permit.service.impl;
//
//
//
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.web.client.RestTemplate;
//
//import dec.ny.gov.etrack.permit.entity.GeoInquiryDocument;
//import dec.ny.gov.etrack.permit.entity.GeoInquiryDocumentReview;
//import dec.ny.gov.etrack.permit.entity.GeographicalInquiryNote;
//import dec.ny.gov.etrack.permit.entity.GeographicalInquiryResponse;
//import dec.ny.gov.etrack.permit.entity.SpatialInquiryDetail;
//import dec.ny.gov.etrack.permit.exception.BadRequestException;
//import dec.ny.gov.etrack.permit.model.AssignmentNote;
//import dec.ny.gov.etrack.permit.model.DocumentReview;
//import dec.ny.gov.etrack.permit.model.GIReviewCompletionDetail;
//import dec.ny.gov.etrack.permit.model.GeographicalInquiryNoteView;
//import dec.ny.gov.etrack.permit.model.GeographicalInquirySubmittalResponse;
//import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;
//import dec.ny.gov.etrack.permit.repo.GIInquiryAlertRepo;
//import dec.ny.gov.etrack.permit.repo.GeoInquiryDocumentRepo;
//import dec.ny.gov.etrack.permit.repo.GeoInquiryDocumentReviewRepo;
//import dec.ny.gov.etrack.permit.repo.GeographicalInquiryNoteRepo;
//import dec.ny.gov.etrack.permit.repo.GeographicalInquiryResponseRepo;
//import dec.ny.gov.etrack.permit.repo.SpatialInquiryRepo;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class SpatialInquiryServiceImplTest {
//
//	@InjectMocks
//	private SpatialInquiryServiceImpl spatialInqService;
//	
//	@Mock
//	private RestTemplate eTrackOtherServiceRestTemplate;
//	
//	@Mock
//	private TransformationService transformationServive;
//	
//	@Mock
//	private SpatialInquiryRepo spatialInquiryRepo;
//	
//	@Mock
//	private DocumentUploadService docUploadService;
//	
//	@Mock
//	private GeographicalInquiryNoteRepo geographicalInquiryNoteRepo;
//	
//	@Mock
//	private GeographicalInquiryResponseRepo geographicalInquiryResponseRepo;
//	
//	@Mock
//	private GeoInquiryDocumentReviewRepo geoInquiryDocumentReviewRepo;
//	
//	
//	@Mock
//	private GeoInquiryDocumentRepo geoInquiryDocumentRepo;
//
//	
//	@Mock
//	private GIInquiryAlertRepo giInquiryAlertRepo;
//	
//	@Rule
//	public ExpectedException expectedException = ExpectedException.none();
//	
//	private String userId = "jxpuvoge";
//	private String contextId = "context1234";
//	private String name = "John Doe";
//	private String token = "34567";
//	private Long noteId = 1L;
//	private Long inquiryId = 4L;
//	private String dateString ="12/23/2023";
//	private String city = "Albany";
//	private String borough = "Queens";
//	private String comments = "Comment";
//	private String spatialInquiryMapUrl = "URL";
//	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//	
//	//saveSpatialInquiryDetail test cases
//	@Test
//	public void testSaveSpatialInquiryDetailSavesSpatialInqDetail() {
//		SpatialInquiryRequest request = getSpatialInquiryRequestObj();
//		when(transformationServive.transformSpatialInquiryRequestToEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(getSpatialInquiryDetailObj());
//		when(spatialInquiryRepo.save(Mockito.any())).thenReturn(getSpatialInquiryDetailObj());
//		spatialInqService.saveSpatialInquiryDetail(userId, contextId, request);
//		verify(spatialInquiryRepo).save(Mockito.any());
//	}
//	
//	
//	
//	//uploadSpatialInquiryMap test cases
//	@Test
//	public void testUploadSpatialInquiryMapCallsDocUploadServiceToUploadMapDoc() {
//		spatialInqService.uploadSpatialInquiryMap(userId, contextId, token, inquiryId, spatialInquiryMapUrl);
//		verify(docUploadService).uploadPrintedFormatOfMapDocumentToDMS(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean());
//	}
//	
//	//submitSpatialInquiry test cases
//	@Test
//	public void testsubmitSpatialInquiryThrowsBREForInvalidInquiryId() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("not available for the input requested Inquiry id");
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//		spatialInqService.submitSpatialInquiry(userId, contextId, token, inquiryId);
//		
//	}
//	
//	@Test
//	public void testsubmitSpatialInquirySavesSpatialInquiryAndGINote() {
//		SpatialInquiryDetail detail = getSpatialInquiryDetailObj();
//		detail.setPolygonId("123");
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(detail));
//		spatialInqService.submitSpatialInquiry(userId, contextId, token, inquiryId);
//		verify(spatialInquiryRepo).save(Mockito.any());
//		verify(geographicalInquiryNoteRepo).save(Mockito.any());
//		
//	}
//	
//	//submitSpatialInquiryResponse test cases	    
//	@Test
//	public void testSubmitSpatialInquiryResponseSavesNewGeographicalInquiryResponse() {
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getSpatialInquiryDetailObj()));
//		spatialInqService.submitSpatialInquiryResponse(userId, contextId, inquiryId, token, getGeographicalInquirySubmittalResponse());
//		verify(geographicalInquiryResponseRepo).save(Mockito.any());
//	}
//	
//	@Test
//	public void testSubmitSpatialInquiryResponseUpdatesExistingGeographicalInquiryResponse() {
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getSpatialInquiryDetailObj()));
//		GeographicalInquirySubmittalResponse response = getGeographicalInquirySubmittalResponse();
//		response.setResponseSentInd("Y");
//		//response.setInquiryCompletedInd("Y");
//		response.setResponseSentDate(dateString);
//		List<GeographicalInquiryResponse> responses = new ArrayList<>();
//		responses.add(getGeographicalInquiryResponse());
//		when(geographicalInquiryResponseRepo.findByInquiryId(Mockito.anyLong())).thenReturn(responses);
//		spatialInqService.submitSpatialInquiryResponse(userId, contextId, inquiryId, token, response);
//		verify(geographicalInquiryResponseRepo).save(Mockito.any());
//	}
//	
//	@Test
//	public void testSubmitSpatialInquiryResponseThrowsBREForInvalidDateFormat() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Geographical Inquiry submittal response date is not an valid format");
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getSpatialInquiryDetailObj()));
//		GeographicalInquirySubmittalResponse response = getGeographicalInquirySubmittalResponse();
//		response.setResponseSentDate("INVALID");
//		spatialInqService.submitSpatialInquiryResponse(userId, contextId, inquiryId, token, response);
//
//	}
//	
//	@Test
//	public void testSubmitSpatialInquiryResponseThrowsBREForInvalidInquiryId() {
//		expectedException.expect(BadRequestException.class);
//		when(spatialInquiryRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//		spatialInqService.submitSpatialInquiryResponse(userId, contextId, inquiryId, token, getGeographicalInquirySubmittalResponse());
//	}
//	
//
//	
//	//addNotes test cases
//	@Test
//	public void testAddNotesSavesAndReturnsForNewNote() {
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getGeographicalInquiryNote()));
//		when(geographicalInquiryNoteRepo.save(Mockito.any())).thenReturn(getGeographicalInquiryNote());
//		GeographicalInquiryNoteView result = spatialInqService.addNotes(userId, contextId, inquiryId, getGeographicalInquiryNoteView());
//		assertEquals(comments, result.getComments());
//		assertEquals("Note", result.getActionNote());
//		assertEquals(dateString, result.getActionDate());
//	}
//	
//	@Test
//	public void testAddNotesSavesAndReturnsForExistingNote() {
//		GeographicalInquiryNoteView note = getGeographicalInquiryNoteView();
//		note.setInquiryNoteId(null);
//		note.setInquiryId(100L);
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getGeographicalInquiryNote()));
//		when(geographicalInquiryNoteRepo.save(Mockito.any())).thenReturn(getGeographicalInquiryNote());
//		GeographicalInquiryNoteView result = spatialInqService.addNotes(userId, contextId, inquiryId, note);
//		assertEquals(comments, result.getComments());
//		assertEquals("Note", result.getActionNote());
//		assertEquals(dateString, result.getActionDate());
//		assertEquals(100L, result.getInquiryId());
//	}
//	
//	@Test
//	public void testAddNotesThrowsBREForNullActionDate() {
//		GeographicalInquiryNoteView note = getGeographicalInquiryNoteView();
//		note.setActionDate(null);
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("One or more required parameter missing");
//		spatialInqService.addNotes(userId, contextId, inquiryId, note);
//	}
//	
//	@Test
//	public void testAddNotesThrowsBREForUnavailableInquiryNote() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Geographical Inquiry note available to update");
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//		spatialInqService.addNotes(userId, contextId, inquiryId, getGeographicalInquiryNoteView());
//		
//	}
//	
//	@Test
//	public void testAddNotesThrowsBREForInvalidActionDate() {
//		GeographicalInquiryNoteView note = getGeographicalInquiryNoteView();
//		note.setActionDate("INVALID");
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Action Date passed not a valid one");
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getGeographicalInquiryNote()));
//		spatialInqService.addNotes(userId, contextId, inquiryId, note);
//		
//	}
//	
//	//deleteNote Test cases
//	@Test
//	public void testDeleteNoteDeletesNote() {
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getGeographicalInquiryNote()));
//		spatialInqService.deleteNote(userId, contextId, inquiryId, noteId);
//		verify(geographicalInquiryNoteRepo).delete(Mockito.any());
//	}
//	
//	@Test
//	public void testDeleteNoteThrowsBREForUnavailableNote() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Geographical Inquiry note available to delete");
//		when(geographicalInquiryNoteRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//		spatialInqService.deleteNote(userId, contextId, inquiryId, noteId);
//	}
//	
//	
//	//updateDocumentReviewerCompletionDetails test cases
//	@Test
//	public void testUpdateDocumentReviewerCompletionDetails() {
//		spatialInqService.updateDocumentReviewerCompletionDetails(userId, contextId, inquiryId, getGiReviewCompletionDetail());
////		verify(geoInquiryDocumentReviewRepo).updateDocumentReviewCompletionDetails(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
//		verify(geoInquiryDocumentReviewRepo).updateDocumentReviewCompletionDetails(userId, 4L, null, "123", 1);
//	}
//	
//	@Test(expected = BadRequestException.class)
//	public void testUpdateDocumentReviewerCompletionDetailswithEmptyDocId() {
//		DocumentReview review = new DocumentReview();
//		review.setDateAssigned("04/02/2026");
//		review.setDueDate("12/21/2029");
//		List<Long> docIds = new ArrayList<>();
//		//docIds.add(1L);
//		review.setDocumentIds(docIds);
//		List<GeoInquiryDocument> geoInqDocList = new ArrayList<>();
//		GeoInquiryDocument geoInqDoc = new GeoInquiryDocument();
//		geoInqDoc.setDocumentClassNm(borough);
//		Mockito.lenient().when(geoInquiryDocumentRepo
//				.findAllByInquiryIdDocumentIds(inquiryId, new ArrayList<>())).thenReturn(geoInqDocList);
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, review);
//	}
//	
//	@Test(expected = BadRequestException.class)
//	public void testUpdateDocumentReviewerCompletionDetailswithValidDocId() {
//		DocumentReview review = new DocumentReview();
//		review.setDateAssigned("04/02/2026");
//		review.setDueDate("12/21/2029");
//		List<Long> docIds = new ArrayList<>();
//		docIds.add(1L);
//		review.setDocumentIds(docIds);
//		List<GeoInquiryDocument> geoInqDocList = new ArrayList<>();
//		GeoInquiryDocument geoInqDoc = new GeoInquiryDocument();
//		geoInqDoc.setDocumentClassNm(borough);
//		Mockito.lenient().when(geoInquiryDocumentRepo
//				.findAllByInquiryIdDocumentIds(inquiryId, new ArrayList<>())).thenReturn(geoInqDocList);
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, review);
//	}
//	
//	
//	@Test(expected = BadRequestException.class)
//	public void testUpdateDocumentReviewerCompletionDetailswithDocMissing() {
//		DocumentReview review = new DocumentReview();
//		review.setReviewerId(userId);
//		review.setDateAssigned("04/02/2026");
//		review.setDueDate("12/21/2029");
//		List<Long> docIds = new ArrayList<>();
//		docIds.add(1L);
//		docIds.add(3L);
//		review.setDocumentIds(docIds);
//		review.setReviewerName("Steve");
//		review.setReviewerEmail("test@Tes.com");
//		List<GeoInquiryDocument> geoInqDocList = new ArrayList<>();
//		GeoInquiryDocument geoInqDoc = new GeoInquiryDocument();
//		geoInqDoc.setDocumentId(1L);
//		geoInqDoc.setDocumentId(2L);
//		geoInqDoc.setDocumentClassNm(borough);
//		geoInqDoc.setDocumentNm("");
//		geoInqDocList.add(geoInqDoc);
//		Mockito.lenient().when(geoInquiryDocumentRepo
//				.findAllByInquiryIdDocumentIds(inquiryId, review.getDocumentIds())).thenReturn(geoInqDocList);
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, review);
//	}
//	
//	@Test
//	public void testUpdateDocumentReviewerCompletionDetailsWithGeoInquiryDocumentReview() {
//		DocumentReview review = new DocumentReview();
//		review.setReviewerId("1L");
//		review.setDateAssigned("04/02/2026");
//		review.setDueDate("12/21/2029");
//		List<Long> docIds = new ArrayList<>();
//		docIds.add(1L);
//		review.setDocumentIds(docIds);
//		review.setReviewerName("Steve");
//		review.setReviewerEmail("test@Tes.com");
//		List<GeoInquiryDocument> geoInqDocList = new ArrayList<>();
//		GeoInquiryDocument geoInqDoc = new GeoInquiryDocument();
//		geoInqDoc.setDocumentId(1L);
//		geoInqDoc.setDocumentClassNm(borough);
//		geoInqDoc.setDocumentNm("");
//		geoInqDocList.add(geoInqDoc);
//		Mockito.lenient().when(geoInquiryDocumentRepo
//				.findAllByInquiryIdDocumentIds(inquiryId, review.getDocumentIds())).thenReturn(geoInqDocList);
//		GeoInquiryDocumentReview geoInquiryDocumentReview = new GeoInquiryDocumentReview();
//		geoInquiryDocumentReview.setCreateDate(new Date());
//		geoInquiryDocumentReview.setReviewGroupId(1L);
//		geoInquiryDocumentReview.setDocReviewedInd(0);
//		geoInquiryDocumentReview.setDocReviewerId(review.getReviewerId());
//		geoInquiryDocumentReview.setGiDocumentReviewId(1L);
//		geoInquiryDocumentReview.setCreatedById("dxdev");
//		geoInquiryDocumentReview.setDocReviewerName(review.getReviewerName());
//		when(geoInquiryDocumentReviewRepo.save(Mockito.any())).thenReturn(geoInquiryDocumentReview);
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, review);
//	}
//	
//	
//	@Test
//	public void testUpdateDocumentReviewerCompletionDetailsThrowsBREForNullDocReviewerName() {
//		GIReviewCompletionDetail detail = getGiReviewCompletionDetail();
//		detail.setDocReviewerName(null);
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("One of the field Document Reviewer Id, Reviewer name and Reviewer id is missing");
//		spatialInqService.updateDocumentReviewerCompletionDetails(userId, contextId, inquiryId, detail);
//		
//	}
//	
//	
//	//updateDocumentReviewerDetails test cases
//	//dateFormat Error
////	@Test
////	public void testUpdateDocumentReviewerDetailsUpdatesReviewerDetails() {
////		when(geoInquiryDocumentRepo.findAllByInquiryIdDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getGeoInquiryDocumentList());
////		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, getDocumentReviewObj());
////		verify(geoInquiryDocumentReviewRepo).save(Mockito.any());
////	}
//	
//	@Test
//	public void testUpdateDocumentReviewerDetailsThrowsBREForInvalidDate() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Invalid dates are passed");
//		DocumentReview docReview = getDocumentReviewObj();
//		docReview.setDateAssigned("12/21/2021");
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, docReview);
//	}
//	
//	//date Format error
////	@Test
////	public void testUpdateDocumentReviewerDetailsThrowsBREForNoDocIds() {
////		expectedException.expect(BadRequestException.class);
////		expectedException.expectMessage("No Document ids passed to assign Program Reviewer");
////		DocumentReview docReview = getDocumentReviewObj();
////		docReview.setDocumentIds(null);
////		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, docReview);
////	}
//	
//	@Test
//	public void testUpdateDocumentReviewerDetailsThrowsBREInvalidDateFormat() {
//		expectedException.expect(BadRequestException.class);
//		expectedException.expectMessage("Dates are not a valid format.");
//		DocumentReview docReview = getDocumentReviewObj();
//		docReview.setDateAssigned("INVALID");
//		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, docReview);
//	}
//	
//	//dateFormat error
////	@Test
////	public void testUpdateDocumentReviewerDetailsThrowsBREForUnavailableDocument() {
////		expectedException.expect(BadRequestException.class);
////		expectedException.expectMessage("There is no documents available to assign Program Reviewer");
////		DocumentReview docReview = getDocumentReviewObj();
////		when(geoInquiryDocumentRepo.findAllByInquiryIdDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList());
////		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, docReview);
////	}
//	
//	//dateFormat error
////	@Test
////	public void testUpdateDocumentReviewerDetailsThrowsBREForMissingDocs() {
////		expectedException.expect(BadRequestException.class);
////		expectedException.expectMessage("One or more documents not available to assign Program Reviewer");
////		DocumentReview docReview = getDocumentReviewObj();
////		when(geoInquiryDocumentRepo.findAllByInquiryIdDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(new GeoInquiryDocument(), new GeoInquiryDocument()));
////		spatialInqService.updateDocumentReviewerDetails(userId, contextId, inquiryId, docReview);
////	}
//	
//	//skipRequiredDocumentUploadForInquiries test cases
//	
//	@Test
//	public void testskipRequiredDocumentUploadForInquiriesUpdatesUploadProcess() {
//		spatialInqService.skipRequiredDocumentUploadForInquiries(userId, contextId, inquiryId);
//		verify(spatialInquiryRepo).updateSkipDocumentUploadProcess(userId, inquiryId);
//	}
//
//	
//	
//		
//	//updateGeographicalInquiryAssignment test cases
//	@Test
//	public void testUpdateGeographicalInquiryAssignment() {
//		GeographicalInquiryNote inquiryNote = new GeographicalInquiryNote();
//		inquiryNote.setInquiryNoteId(10L);
//		when(geographicalInquiryNoteRepo.save(Mockito.any())).thenReturn(inquiryNote);
//		spatialInqService.updateGeographicalInquiryAssignment(userId, contextId, inquiryId, getAssignmentNote());
//		verify(giInquiryAlertRepo).save(Mockito.any());
//	}
//
//	//private methods
//		
//	private DocumentReview getDocumentReviewObj() {
//		DocumentReview review = new DocumentReview();
//		review.setDateAssigned("4/2/2026");
//		review.setDueDate("12/21/2029");
//		List<Long> docIds = new ArrayList<>();
//		docIds.add(1L);
//		review.setDocumentIds(docIds);
//		return review;
//	}
//	
//	private List<GeoInquiryDocument> getGeoInquiryDocumentList(){
//		List<GeoInquiryDocument> docs = new ArrayList<>();
//		GeoInquiryDocument document = new GeoInquiryDocument();
//		document.setDocumentClassNm("Doc class");
//		document.setDocumentId(100L);
//		document.setDocumentNm("Doc name");
//		document.setEcmaasGuid("1234");
//		docs.add(document);
//		return docs;
//	}
//
//
//	
//	
//
//	private GIReviewCompletionDetail getGiReviewCompletionDetail() {
//		GIReviewCompletionDetail detail = new GIReviewCompletionDetail();
//		detail.setDocReviewedInd("Y");
//		detail.setDocReviewerName(name);
//		detail.setDocumentReviewId(inquiryId);
//		detail.setReviewerId("123");
//		return detail;
//	}
//	
//	private GeographicalInquiryNoteView getGeographicalInquiryNoteView() {
//		GeographicalInquiryNoteView noteView = new GeographicalInquiryNoteView();
//		noteView.setComments(comments);
//		noteView.setInquiryNoteId(inquiryId);
//		noteView.setActionDate(dateString);
//		noteView.setActionNote("Note");
//		noteView.setActionTypeCode(1);
//		return noteView;
//	}
//	
//
//	
//	private GeographicalInquiryNote getGeographicalInquiryNote() {
//		GeographicalInquiryNote note = new GeographicalInquiryNote();
//		note.setInquiryId(inquiryId);
//		note.setActionNote("Action");
//		note.setActionTypeCode(1);
//		note.setModifiedById(userId);
//		return note;
//	}
//	
//	private AssignmentNote getAssignmentNote() {
//		AssignmentNote note = new AssignmentNote();
//		note.setAnalystName(name);
//
//		return note;
//	}
//
//	private GeographicalInquirySubmittalResponse getGeographicalInquirySubmittalResponse() {
//		GeographicalInquirySubmittalResponse response = new GeographicalInquirySubmittalResponse();
//		return response;
//	}
//	
//	private GeographicalInquiryResponse getGeographicalInquiryResponse() {
//		GeographicalInquiryResponse response = new GeographicalInquiryResponse();
//		response.setResponseText("response");
//		return response;
//	}
//		
//	private SpatialInquiryRequest getSpatialInquiryRequestObj() {
//		SpatialInquiryRequest request = new SpatialInquiryRequest();
//		request.setCity(city);
//		request.setExtenderName(name);
//		request.setBorough(borough);
//		request.setComments(comments);
//		return request;
//	}
//	
//	private SpatialInquiryDetail getSpatialInquiryDetailObj() {
//		SpatialInquiryDetail detail = new SpatialInquiryDetail();
//		detail.setCity(city);
//		detail.setAssignedAnalystName(name);
//		detail.setInquiryId(inquiryId);
//		return detail;
//	}
//
//}
