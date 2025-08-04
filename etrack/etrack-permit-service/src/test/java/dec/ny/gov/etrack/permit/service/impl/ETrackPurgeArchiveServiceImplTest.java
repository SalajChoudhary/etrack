package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import dec.ny.gov.etrack.permit.dao.ETrackPurgeArchiveDao;
import dec.ny.gov.etrack.permit.entity.ArchivePurgeQueryResult;
import dec.ny.gov.etrack.permit.model.DocumentArchivePurge;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.PurgeArchive;
import dec.ny.gov.etrack.permit.repo.ArchivePurgeQueryResultRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackPurgeArchiveServiceImplTest {

	@InjectMocks
	private ETrackPurgeArchiveServiceImpl eTrackPurgeArchiveServiceImpl;

	@Mock
	SupportDocumentRepo supportDocumentRepo;

	@Mock
	ArchivePurgeQueryResultRepo archivePurgeQueryResultRepo;

	@Mock
	ETrackPurgeArchiveDao purgeArchiveDao;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private RestTemplate businessVerificationRestTemplate;
	
	@Mock
	private DocumentUploadService documentUploadService;

	private String userId = "userId";
	private String contextId = "contextId";

	// savePurgeArchiveReviewDetails Test cases:
	@Test
	public void testSavePurgeArchiveReviewDetails() {
		PurgeArchive purgeArchive = PurgeArchive.builder().build();
		when(purgeArchiveDao.savePurgeArchiveReviewDetails(userId, contextId, purgeArchive)).thenReturn(100l);
		Long resultId = eTrackPurgeArchiveServiceImpl.savePurgeArchiveReviewDetails(userId, contextId, purgeArchive);
		assertNotNull(resultId);

	}

	// UpdateCompletedIndicator Test cases:
	@Test
	public void testUpdateCompletedIndicator() {
		DocumentReview documentReview = new DocumentReview();
		documentReview.setResultId(234l);
		Integer archiveDocInd = 102;
		archivePurgeQueryResultRepo.updateAnalystIndicator(userId, documentReview.getResultId());
		eTrackPurgeArchiveServiceImpl.updateAnalystIndicator(userId, contextId, documentReview);
	}

	// DeleteArchivePurgeQueryResult Test cases:
	@Test
	public void testDeleteArchivePurgeQueryResult() {
		Long resultId = 102l;
		archivePurgeQueryResultRepo.updateCompleteIndicator(userId, resultId);
		eTrackPurgeArchiveServiceImpl.updateProcessCompletedIndicator(userId, contextId, "jwttoken", resultId);
	}

	// UpdatePurgeArchiveDocument Test cases:
	@Test
	public void testUpdatePurgeArchiveDocument() {
		Long documentId = 102l;
		purgeArchiveDao.deletePurgeArchiveDocument(userId, contextId, documentId);
		eTrackPurgeArchiveServiceImpl.deletePurgeArchiveDocument(userId, contextId, documentId);
	}
	
	@Test
	public void testUpdateArchivePurgeIndicatorY() {
		DocumentReview documentReview = new DocumentReview ();
		DocumentArchivePurge documentArchivePurge= new DocumentArchivePurge();
		documentArchivePurge.setDocId(123l);
		documentArchivePurge.setMarkForReview(true);
		documentReview.setDocuments(Arrays.asList(documentArchivePurge));
		documentReview.setDocumentIds(Arrays.asList(234l,456l));
		documentReview.setArchiveType("Y");
		Integer indicator=1;
		supportDocumentRepo.updateArchiveIndicator(userId, indicator, documentReview.getDocumentIds(),documentReview.getResultId());
		eTrackPurgeArchiveServiceImpl.updateArchivePurgeIndicator(userId, contextId, documentReview);
	}
	
	@Test
	public void testUpdateArchivePurgeIndicatorN() {
		DocumentReview documentReview = new DocumentReview ();
		DocumentArchivePurge documentArchivePurge = new DocumentArchivePurge();
		documentArchivePurge.setDocId(123l);
		documentArchivePurge.setMarkForReview(true);
		documentReview.setDocuments(Arrays.asList(documentArchivePurge));
		documentReview.setDocumentIds(Arrays.asList(234l,456l));
		documentReview.setArchiveType("N");
		Integer indicator=1;
		supportDocumentRepo.updateArchiveIndicator(userId, indicator, documentReview.getDocumentIds(),documentReview.getResultId());
		eTrackPurgeArchiveServiceImpl.updateArchivePurgeIndicator(userId, contextId, documentReview);
	}
	
	@Test
	public void testUpdateProcessCompletedIndicator_P() {
		ArchivePurgeQueryResult archivePurgeQueryResult = new ArchivePurgeQueryResult();
		when(archivePurgeQueryResultRepo.findById(anyLong())).thenReturn(Optional.of(archivePurgeQueryResult));
		doNothing().when(archivePurgeQueryResultRepo).updateCompleteIndicator(anyString(), anyLong());
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("P");
		when(supportDocumentRepo.findAllPurgeEligibleDocumentsByResultId(anyLong())).thenReturn(Arrays.asList("A,1"));
		doNothing().when(documentUploadService).deleteExistingDocumentFromDMS(anyString(), anyString(), anyLong(), anyString(), any(), anyBoolean());
		eTrackPurgeArchiveServiceImpl.updateProcessCompletedIndicator(userId, contextId, "token",  10L);
		verify(documentUploadService).deleteExistingDocumentFromDMS(anyString(), anyString(), anyLong(), anyString(), any(), anyBoolean());
		verify(archivePurgeQueryResultRepo, times(2)).findQueryActivityTypeByResultId(anyLong());
	}
	
	@Test
	public void testUpdateAnalystIndicator_A() {
		ArchivePurgeQueryResult archivePurgeQueryResult = new ArchivePurgeQueryResult();
		DocumentReview documentReview = new DocumentReview();
		documentReview.setResultId(10L);
		when(archivePurgeQueryResultRepo.findByResultId(anyLong())).thenReturn(archivePurgeQueryResult);
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("A");
		doNothing().when(archivePurgeQueryResultRepo).updateAnalystIndicator(anyString(), anyLong());
		doNothing().when(supportDocumentRepo).updateArchiveReviewCompletedInd(anyString(), anyLong());
		eTrackPurgeArchiveServiceImpl.updateAnalystIndicator(userId, contextId,  documentReview);
		verify(archivePurgeQueryResultRepo).findQueryActivityTypeByResultId(anyLong());
		verify(supportDocumentRepo).updateArchiveReviewCompletedInd(anyString(), anyLong());
	}
	
	@Test
	public void testUpdateAnalystIndicator_P() {
		ArchivePurgeQueryResult archivePurgeQueryResult = new ArchivePurgeQueryResult();
		DocumentReview documentReview = new DocumentReview();
		documentReview.setResultId(10L);
		when(archivePurgeQueryResultRepo.findByResultId(anyLong())).thenReturn(archivePurgeQueryResult);
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("P");
		doNothing().when(archivePurgeQueryResultRepo).updateAnalystIndicator(anyString(), anyLong());
		doNothing().when(supportDocumentRepo).updatePurgeReviewCompletedInd(anyString(), anyLong());
		eTrackPurgeArchiveServiceImpl.updateAnalystIndicator(userId, contextId,  documentReview);
		verify(archivePurgeQueryResultRepo).findQueryActivityTypeByResultId(anyLong());
		verify(supportDocumentRepo).updatePurgeReviewCompletedInd(anyString(), anyLong());
	}
	
	
	@Test
	public void testUpdateProcessCompletedIndicator_A() {
		doNothing().when(supportDocumentRepo).updateArchiveCompletedInd(anyString(), anyLong());
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("A");
		eTrackPurgeArchiveServiceImpl.updateProcessCompletedIndicator(userId, contextId, "token",  10L);
		verify(supportDocumentRepo).updateArchiveCompletedInd(anyString(), anyLong());
		verify(archivePurgeQueryResultRepo).findQueryActivityTypeByResultId(anyLong());
	}
	
	@Test
	public void testPurgeAllReviewedDocuments() {
		doNothing().when(documentUploadService).deleteExistingDocumentFromDMS(anyString(), anyString(), anyLong(), anyString(), any(), anyBoolean());
		eTrackPurgeArchiveServiceImpl.purgeAllReviewedDocuments(userId, contextId, "token",  10L, Arrays.asList("A,1", "B,2"));
		verify(documentUploadService, times(2)).deleteExistingDocumentFromDMS(anyString(), anyString(), anyLong(), anyString(), any(), anyBoolean());
	}
	
	@Test
	public void testPurgeAllReviewedDocuments_Exception() {
		doThrow(new RuntimeException()).when(documentUploadService).deleteExistingDocumentFromDMS(anyString(), anyString(), anyLong(), anyString(), any(), anyBoolean());
		eTrackPurgeArchiveServiceImpl.purgeAllReviewedDocuments(userId, contextId, "token",  10L, Arrays.asList("A,1", "B,1"));
	}
	
	@Test
	public void testMarkAllRequestedDocumentAsNotEligible_A() {
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("A");
		doNothing().when(supportDocumentRepo).updateAllUnReviewedArchiveEligibleDocumentsAsNotEligible(anyString(), anyLong(), any());
		eTrackPurgeArchiveServiceImpl.markAllRequestedDocumentAsNotEligible(userId, contextId, 10L, Arrays.asList(0L));
		verify(supportDocumentRepo).updateAllUnReviewedArchiveEligibleDocumentsAsNotEligible(anyString(), anyLong(), any());
	}
	
	@Test
	public void testMarkAllRequestedDocumentAsNotEligible_P() {
		when(archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(anyLong())).thenReturn("P");
		doNothing().when(supportDocumentRepo).updateAllUnReviewedPurgeEligibleDocumentsAsNotEligible(anyString(), anyLong(), any());
		eTrackPurgeArchiveServiceImpl.markAllRequestedDocumentAsNotEligible(userId, contextId, 10L, Arrays.asList(0L));
		verify(supportDocumentRepo).updateAllUnReviewedPurgeEligibleDocumentsAsNotEligible(anyString(), anyLong(), any());
	}
}
