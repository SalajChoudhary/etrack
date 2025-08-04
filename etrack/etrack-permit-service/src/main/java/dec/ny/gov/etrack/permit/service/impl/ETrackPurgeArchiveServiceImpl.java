package dec.ny.gov.etrack.permit.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.google.common.collect.Lists;
import dec.ny.gov.etrack.permit.dao.ETrackPurgeArchiveDao;
import dec.ny.gov.etrack.permit.entity.ArchivePurgeQueryResult;
import dec.ny.gov.etrack.permit.model.DeletedPurgeArchiveDocument;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.PurgeArchive;
import dec.ny.gov.etrack.permit.repo.ArchivePurgeQueryResultRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.permit.service.ETrackPurgeArchiveService;

@Service
public class ETrackPurgeArchiveServiceImpl implements ETrackPurgeArchiveService {

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackPurgeArchiveServiceImpl.class.getName());


  @Autowired
  ETrackPurgeArchiveDao purgeArchiveDao;
  @Autowired
  SupportDocumentRepo supportDocumentRepo;
  @Autowired
  ArchivePurgeQueryResultRepo archivePurgeQueryResultRepo;
  @Autowired
  private DocumentUploadService documentUploadService;

  private static final Integer DB_RECORD_BATCH_SIZE = 100;
  private static final String PURGE_ACTIVITY = "P";
  private static final String ARCHIVE_ACTIVITY = "A";

  @Transactional
  @Override
  public Long savePurgeArchiveReviewDetails(final String userId, final String contextId,
      PurgeArchive purgeArchive) {
    logger.info("Entering into savePurgeArchiveReviewDetails. User Id {}, Context Id {}", userId,
        contextId);

    return purgeArchiveDao.savePurgeArchiveReviewDetails(userId, contextId, purgeArchive);
  }

  @Transactional
  @Override
  public void updateAnalystIndicator(final String userId, final String contextId,
      DocumentReview documentReview) {
    ArchivePurgeQueryResult archivePurgeQueryResult =
        archivePurgeQueryResultRepo.findByResultId(documentReview.getResultId());
    String activityType = archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(documentReview.getResultId());
    if (archivePurgeQueryResult != null) {
      archivePurgeQueryResultRepo.updateAnalystIndicator(userId, documentReview.getResultId());
      if (PURGE_ACTIVITY.equalsIgnoreCase(activityType)) {
        supportDocumentRepo.updatePurgeReviewCompletedInd(userId, documentReview.getResultId());
      } else if (ARCHIVE_ACTIVITY.equalsIgnoreCase(activityType)) {
        supportDocumentRepo.updateArchiveReviewCompletedInd(userId, documentReview.getResultId());
      }
    }
  }
  
  @Override
  public List<DeletedPurgeArchiveDocument> updateArchivePurgeIndicator(final String userId,
      final String contextId, DocumentReview documentReview) {
    logger.info("Entering into updateArchivePurgeIndicator as Reviewed. User Id {}, Context Id {}",
        userId, contextId);
    List<Long> documentIds = updateIndicator(userId, contextId, documentReview);
    List<DeletedPurgeArchiveDocument> result = new ArrayList<>();
    Lists.partition(documentIds, DB_RECORD_BATCH_SIZE).forEach(docIdSubList -> result
        .addAll(archivePurgeQueryResultRepo.getDeletedArchivePurgeDocument(docIdSubList)));
    logger.info("Existing into updateArchivePurgeIndicator as Reviewed. User Id {}, Context Id {}",
        userId, contextId);
    return result;
  }

  @Transactional
  public List<Long> updateIndicator(final String userId, final String contextId,
      DocumentReview documentReview) {
    final Map<Integer, List<Long>> result = new HashMap<>();
    documentReview.getDocuments().forEach(document -> {
      Integer indicator = document.getMarkForReview() ? 1 : 0;
      List<Long> documentIds = result.get(indicator);
      if (CollectionUtils.isEmpty(documentIds)) {
        documentIds = new ArrayList<>();
        result.put(indicator, documentIds);
      }
      documentIds.add(document.getDocId());
    });
    if ("y".equalsIgnoreCase(documentReview.getArchiveType())) {
      result.forEach((indicator, docIds) -> {
        Lists.partition(docIds, DB_RECORD_BATCH_SIZE).forEach(docIdSubList -> supportDocumentRepo
            .updateArchiveIndicator(userId, indicator, docIdSubList, documentReview.getResultId()));
      });
    } else {
      result.forEach((indicator, docIds) -> {
        Lists.partition(docIds, DB_RECORD_BATCH_SIZE).forEach(docIdSubList -> supportDocumentRepo
            .updatePurgeIndicator(userId, indicator, docIdSubList, documentReview.getResultId()));
      });
    }
    return result.values().stream().flatMap(List::stream).collect(Collectors.toList());
  }

  @Transactional
  @Override
  public void updateProcessCompletedIndicator(final String userId, final String contextId,
      final String token, Long resultId) {
    logger.info("Entering into updateProcessCompletedIndicator(). User Id {}, Context Id {}", userId,
        contextId);
    Optional<ArchivePurgeQueryResult> archivePurgeQueryResultAvail = 
        archivePurgeQueryResultRepo.findById(resultId);
    String queryActivityType = 
        archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(resultId);
    if (ARCHIVE_ACTIVITY.equals(queryActivityType)) {
      supportDocumentRepo.updateArchiveCompletedInd(userId, resultId);
    } else if (PURGE_ACTIVITY.equals(queryActivityType)) {
      if (archivePurgeQueryResultAvail.isPresent()
          && (archivePurgeQueryResultAvail.get().getCompletedInd() == null 
              || archivePurgeQueryResultAvail.get().getCompletedInd() == 0)) {
        archivePurgeQueryResultRepo.updateCompleteIndicator(userId, resultId);
        purgeRequestedReviewedReportDocuments(userId, contextId, token, resultId);
      }
    }
    logger.info("Exiting from updateProcessCompletedIndicator(). User Id {}, Context Id {}", userId,
        contextId);
  }

  private void purgeRequestedReviewedReportDocuments(final String userId, final String contextId,
      final String token, final Long resultId) {
    String queryActivityType = 
        archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(resultId);
    if (PURGE_ACTIVITY.equals(queryActivityType)) {
      List<String> documentIdAndProjectIds =
          supportDocumentRepo.findAllPurgeEligibleDocumentsByResultId(resultId);
      if (!CollectionUtils.isEmpty(documentIdAndProjectIds)) {
        requestDMSServiceToDelete(userId, contextId, token, documentIdAndProjectIds);
      }
    }
  }

  private void requestDMSServiceToDelete(final String userId, final String contextId,
      final String token, List<String> documentIdAndProjectIds) {
    Map<Long, List<String>> projectIdAndDocumentIds = new HashMap<>();
    documentIdAndProjectIds.forEach(documentIdAndProjectId -> {
      String[] splitted = documentIdAndProjectId.split(",");
      Long projectId = Long.valueOf(splitted[1]);
      if (projectIdAndDocumentIds.get(projectId) == null) {
        List<String> documentIds = new ArrayList<>();
        documentIds.add(splitted[0]);
        projectIdAndDocumentIds.put(projectId, documentIds);
      } else {
        projectIdAndDocumentIds.get(projectId).add(splitted[0]);
      }
    });
    projectIdAndDocumentIds.keySet().forEach(projectId -> {
      try {
        documentUploadService.deleteExistingDocumentFromDMS(userId, contextId, projectId, token,
            projectIdAndDocumentIds.get(projectId), false);
      } catch (Exception e) {
        logger.error("Error while deleting the Documents associated with the project id {}",
            projectId, e);
      }
    });

  }
  
  
  @Override
  public void deletePurgeArchiveDocument(final String userId, final String contextId,
      Long documentId) {
    purgeArchiveDao.deletePurgeArchiveDocument(userId, contextId, documentId);
  }


  @Override
  public void purgeAllReviewedDocuments(String userId, String contextId, String jwtToken,
      Long resultId, List<String> documentIdAndProjectIds) {
    logger.info("Entering into purgeAllReviewedDocuments. User Id {}, Context id{}", userId,
        contextId);
    requestDMSServiceToDelete(userId, contextId, jwtToken, documentIdAndProjectIds);
    logger.info("Exiting from purgeAllReviewedDocuments. User Id {}, Context id{}", userId,
        contextId);
  }


  @Override
  public void markAllRequestedDocumentAsNotEligible(final String userId, final String contextId,
      final Long resultId, List<Long> documentIds) {
    logger.info("Entering into markAllRequestedDocumentAsNotEligible. User Id {}, Context Id {}", userId,
        contextId);
    String queryActivityType = 
        archivePurgeQueryResultRepo.findQueryActivityTypeByResultId(resultId);
    if (ARCHIVE_ACTIVITY.equalsIgnoreCase(queryActivityType)) {
      Lists.partition(documentIds, DB_RECORD_BATCH_SIZE).forEach(documentIdList -> {
        supportDocumentRepo.updateAllUnReviewedArchiveEligibleDocumentsAsNotEligible(userId,resultId, documentIdList);
      });
    } else if (PURGE_ACTIVITY.equalsIgnoreCase(queryActivityType)) {
      Lists.partition(documentIds, DB_RECORD_BATCH_SIZE).forEach(documentIdList -> {
        supportDocumentRepo.updateAllUnReviewedPurgeEligibleDocumentsAsNotEligible(userId,resultId, documentIdList);
      });
    }
    logger.info("Exiting from markAllRequestedDocumentAsNotEligible. User Id {}, Context id{}", userId,
        contextId);
  }
}
