package dec.ny.gov.etrack.dcs.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.entity.DocumentReviewEntity;

@Repository
public interface DocumentReviewRepo extends CrudRepository<DocumentReviewEntity, Long> {
  
  @Query(value="select d.document_review_id, s.document_id document_id, s.document_desc document_desc, s.document_nm document_nm, "
      + "d.doc_reviewer_id, d.doc_reviewer_name, d.doc_reviewed_ind, d.review_assigned_date, d.review_due_date, d.create_date, d.correspondence_id "
      + "from {h-schema}e_support_document s, {h-schema}e_document_review d, {h-schema}e_email_correspondence e "
      + "where s.document_id=d.document_id "
      + "and d.correspondence_id=e.correspondence_id and s.document_state_code='A' "
      + "and e.email_status ='S' and e.project_id=?1 "
      + "union "
      + "select d.document_review_id, null, null,  null, "
      + "d.doc_reviewer_id, d.doc_reviewer_name, d.doc_reviewed_ind, d.review_assigned_date, d.review_due_date, d.create_date, d.correspondence_id "
      + "from {h-schema}e_document_review d, {h-schema}e_email_correspondence e "
      + "where d.correspondence_id=e.correspondence_id and e.email_status ='S' "
      + "and e.project_id=?1 and d.document_id is null order by create_date desc", nativeQuery=true)
  List<DocumentReviewEntity> findAllCorrespondenceDetailsByProjectId(Long projectId);
  
  /**
   * Retrieve the list of document reviews requested.
   * 
   * @param correspondenceId - Correspondence id.
   * 
   * @return - List of Document Review eligible reviews.
   */
  @Query(value="select d.document_review_id, d.document_id document_id, '' document_desc, '' document_nm, "
      + "d.doc_reviewer_id, d.doc_reviewer_name, d.doc_reviewed_ind, d.review_assigned_date, d.review_due_date, d.create_date, d.correspondence_id "
      + "from {h-schema}e_document_review d where d.correspondence_id=?1", nativeQuery=true)
  List<DocumentReviewEntity> findByCorrespondenceId(Long correspondenceId);
  
  @Transactional
  @Modifying
  @Query(value = "update {h-schema}e_document_review set doc_reviewed_ind=?4, "
      + "modified_by_id=?1, modified_date=sysdate where correspondence_id=?2 and doc_reviewer_id=?3", nativeQuery = true)
  void updateDocumentReviewCompletionDetails(
      String userId, Long correspondenceId, String reviewerId, final Integer docReviewedInd);
  
  @Query(value="select document_id from {h-schema}e_support_document where project_id=?1 "
      + "and document_nm like (?2) and ref_document_id is null and document_state_code='A'", nativeQuery = true)
  List<Long> findByDocumentNameAndProjectId(Long projectId, String documentName);
}
