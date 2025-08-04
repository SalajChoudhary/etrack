package dec.ny.gov.etrack.permit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.DocumentArchivePurge;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.PurgeArchive;
import dec.ny.gov.etrack.permit.service.ETrackPurgeArchiveService;

@RunWith(SpringJUnit4ClassRunner.class)
public class EtrackPurgeArchiveControllerTest {


	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private ETrackPurgeArchiveController eTrackPurgeArchiveController;

	@Mock
	private ETrackPurgeArchiveService eTrackPurgeArchiveService;


	private String userId = "userId";
	private Long alertId = 12l;

	@Test
	public void testSavePurgeArchiveReviewDetails() {
		PurgeArchive purgeArchive= PurgeArchive.builder().queryNameCode(1).build();
		when(eTrackPurgeArchiveService.savePurgeArchiveReviewDetails(anyString(), anyString(), any())).thenReturn(1L);
		ResponseEntity<Long> result = eTrackPurgeArchiveController.savePurgeArchiveReviewDetails(userId,purgeArchive);
		assertNotNull(result);
		assertEquals(result.getStatusCodeValue(), 200);
		assertEquals(result.getBody(), 1L);
	}
	
	@Test
	public void testSavePurgeArchiveReviewDetailsNoContent() {
		PurgeArchive purgeArchive= PurgeArchive.builder().queryNameCode(1).build();
		when(eTrackPurgeArchiveService.savePurgeArchiveReviewDetails(anyString(), anyString(), any())).thenThrow(new ETrackPermitException());
		ResponseEntity<Long> result = eTrackPurgeArchiveController.savePurgeArchiveReviewDetails(userId,purgeArchive);
		assertNotNull(result);
		assertEquals(result.getStatusCodeValue(), 204);
	}

	@Test
	public void testUpdateReviewCompletedIndicator() {
		DocumentReview documentReview = new DocumentReview();
		documentReview.setResultId(1L);
		doNothing().when(eTrackPurgeArchiveService).updateAnalystIndicator(anyString(), anyString(), any());
		eTrackPurgeArchiveController.updateReviewCompletedIndicator(userId, documentReview);
		verify(eTrackPurgeArchiveService).updateAnalystIndicator(anyString(), anyString(), any());
	}

	@Test
	public void testUpdateReviewCompletedIndicatorException() {
		DocumentReview documentReview = new DocumentReview();
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.updateReviewCompletedIndicator(userId, documentReview));
	}

	@Test
	public void testUpdatePurgeArchiveReviewDocumentsStatus() {
		DocumentReview documentReview = new DocumentReview();
		DocumentArchivePurge documentArchivePurge= new DocumentArchivePurge();
		documentArchivePurge.setDocId(1l);
		List<DocumentArchivePurge> documentArchivePurges= new ArrayList<>();
		documentArchivePurges.add(documentArchivePurge);
		documentReview.setDocuments(documentArchivePurges);
		doReturn(new ArrayList<>()).when(eTrackPurgeArchiveService).updateArchivePurgeIndicator(anyString(), anyString(), any());
		List data = eTrackPurgeArchiveController.updatePurgeArchiveReviewDocumentsStatus(userId, documentReview);
		assertNotNull(data);
		verify(eTrackPurgeArchiveService).updateArchivePurgeIndicator(anyString(), anyString(), any());
	}

	@Test
	public void testUpdatePurgeArchiveReviewDocumentsStatusException() {
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.updatePurgeArchiveReviewDocumentsStatus(userId, null));
	}

	@Test
	public void testDeletePurgeArchiveDocument() {
		eTrackPurgeArchiveController.removeDocumentFromReviewList("testUserId", 123L);
		verify(eTrackPurgeArchiveService).deletePurgeArchiveDocument(anyString(), anyString(), anyLong());
	}

	@Test
	public void testDeletePurgeArchiveDocumentException() {
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.removeDocumentFromReviewList("testUserId", null));
	}
	
	@Test
	public void testDeletePurgeArchiveReview() {
		eTrackPurgeArchiveController.updatePurgeArchiveReviewProcessCompleted(userId, "jwttoken", 123L);
		verify(eTrackPurgeArchiveService).updateProcessCompletedIndicator(anyString(), anyString(), anyString(), anyLong());
	}

	@Test
	public void testDeletePurgeArchiveReviewException() {
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.updatePurgeArchiveReviewProcessCompleted(userId, "jwttoken", null));
	}
	
	@Test
	public void testMarkAllRequestedDocumentAsNotEligible() {
		eTrackPurgeArchiveController.markAllRequestedDocumentAsNotEligible(userId, 1L, new ArrayList<>());
		verify(eTrackPurgeArchiveService).markAllRequestedDocumentAsNotEligible(anyString(), anyString(), anyLong(), any());
	}

	@Test
	public void testMarkAllRequestedDocumentAsNotEligibleException() {
		doThrow(new BadRequestException()).when(eTrackPurgeArchiveService).markAllRequestedDocumentAsNotEligible(anyString(), anyString(), anyLong(), any());
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.markAllRequestedDocumentAsNotEligible(userId, 1L, new ArrayList<>()));
	}
	
	@Test
	public void testPurgeAllReviewedDocuments() {
		eTrackPurgeArchiveController.purgeAllReviewedDocuments(userId, "", 1L, new ArrayList<>());
		verify(eTrackPurgeArchiveService).purgeAllReviewedDocuments(anyString(), anyString(), anyString(), anyLong(), any());
	}

	@Test
	public void testPurgeAllReviewedDocumentsException() {
		assertThrows(BadRequestException.class,()-> eTrackPurgeArchiveController.purgeAllReviewedDocuments(userId, "",  0L, new ArrayList<>()));
	}

}
