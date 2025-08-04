package dec.ny.gov.etrack.permit.repo;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.SupportDocument;

@Repository
public interface SupportDocumentRepo extends CrudRepository<SupportDocument, Long> {

  @Query(value = "select sd.document_id, sd.ecmaas_guid, dc.document_class_nm, sd.document_nm from {h-schema}e_support_document sd, {h-schema}e_document_class dc, "
      + " {h-schema}e_document_type dt where sd.project_id=?1 and sd.ref_document_id is null and sd.document_state_code='A' "
      + "and sd.document_type_id=dt.document_type_id and dt.document_class_id=dc.document_class_id", nativeQuery = true)
  List<SupportDocument> findAllDocumentsByProjectId(Long projectId);

  @Query(value = "select sd.document_id, sd.ecmaas_guid, dc.document_class_nm, sd.document_nm from {h-schema}e_support_document sd, {h-schema}e_document_class dc, "
      + " {h-schema}e_document_type dt where sd.project_id=?1 and sd.ref_document_id is null and sd.document_state_code='A' "
      + "and sd.document_type_id=dt.document_type_id and dt.document_class_id=dc.document_class_id "
      + "and sd.document_id in (?2) ", nativeQuery = true)
  List<SupportDocument> findAllByDocumentIds(final Long projectId, List<Long> documentIds);

  @Query(value="select document_id from {h-schema}e_support_document where project_id=?1 "
      + "and document_nm like (?2) and ref_document_id is null and document_state_code='A'", nativeQuery = true)
  List<Long> findByDocumentNameAndProjectId(Long projectId, String documentName);  
  
//  @Transactional
//  @Modifying
//  @Query(value="update {h-schema}e_support_document set archive_reviewed_ind= ?2, modified_by_id= ?1, modified_date=sysdate where document_id= ?3 "
//      + "and arc_prg_query_result_id= ?4", nativeQuery = true)
//  public void updateArchiveDocumentById(final String userId, final Integer archiveReviewdInd, final Long documentId, final Long archPrgQueryResultId);   
//  
//  @Transactional
//  @Modifying
//  @Query(value = "update {h-schema}e_support_document set archive_reviewed_ind= ?2, modified_by_id= ?1, modified_date=sysdate"
//      + " where document_id IN (?3) and arc_prg_query_result_id= ?4", nativeQuery = true)
//  public void updateArchiveDocumentsByIds(final String userId, final Integer archiveReviewdInd, 
//      final List<Long> documentIds, final Long archPrgQueryResultId);
  
  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_support_document set purge_reviewed_ind = ?2, modified_by_id= ?1, modified_date=sysdate "
      + "where document_id IN (?3) and arc_prg_query_result_id= ?4", nativeQuery = true)
  public void updatePurgeIndicator(final String userId, final Integer indicator, final List<Long> documentIds, final Long archPrgQueryResultId);
  
  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_support_document set archive_reviewed_ind= ?2, modified_by_id= ?1, modified_date=sysdate "
      + "where document_id IN (?3) and arc_prg_query_result_id= ?4", nativeQuery = true)
  public void updateArchiveIndicator(final String userId, final Integer indicator, final List<Long> documentIds, final Long archPrgQueryResultId);

  @Modifying
  @Query(value="update {h-schema}e_support_document set archive_reviewed_ind=1, modified_by_id= ?1, "
      + "modified_date=sysdate where arc_prg_query_result_id= ?2", nativeQuery = true)
  void updateArchiveReviewCompletedInd(final String userId, final Long resultId);

  @Modifying
  @Query(value="update {h-schema}e_support_document set archive_completed_ind=1, modified_by_id= ?1, "
      + "modified_date=sysdate where arc_prg_query_result_id= ?2", nativeQuery = true)
  void updateArchiveCompletedInd(final String userId, final Long resultId);

  @Modifying
  @Query(value="update {h-schema}e_support_document set purge_reviewed_ind=1, modified_by_id=?1, "
      + "modified_date=sysdate where arc_prg_query_result_id= ?2", nativeQuery = true)
  void updatePurgeReviewCompletedInd(final String userId, final Long resultId);

 
  @Query(value="select document_id, project_id from {h-schema}e_support_document where arc_prg_query_result_id= ?1 "
      + "and document_state_code='A' and purge_reviewed_ind=1 order by project_id asc", nativeQuery = true)
  List<String> findAllPurgeEligibleDocumentsByResultId(final Long resultId);
  
  @Query(value="select count(*) from {h-schema}e_support_document "
      + "where arc_prg_query_result_id=?1 and (purge_reviewed_ind is null or purge_reviewed_ind=0)", nativeQuery = true)
  Integer findUnReviewedDocumentsByResultId(final Long resultId);

  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_support_document set arc_prg_query_result_id=null, archive_reviewed_ind=null, modified_by_id= ?1, modified_date=sysdate "
      + "where arc_prg_query_result_id=?2 and document_id in (?3)", nativeQuery = true)
  void updateAllUnReviewedArchiveEligibleDocumentsAsNotEligible(String userId, Long resultId, List<Long> documentIds);
  
  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_support_document set arc_prg_query_result_id=null, purge_reviewed_ind=null, modified_by_id= ?1, modified_date=sysdate "
      + "where arc_prg_query_result_id=?2 and document_id in (?3)", nativeQuery = true)
  void updateAllUnReviewedPurgeEligibleDocumentsAsNotEligible(String userId, Long resultId, List<Long> documentIds);
 
  @Query(value = "select document_id from {h-schema}e_support_document where upper(document_nm)=?1 "
      + "and project_id=?2 and document_state_code='A' ", nativeQuery = true)
  List<Long> findDocumentNameExistByProjectIdAndDocumentName(final String documentName,
      final Long projectId);
}

