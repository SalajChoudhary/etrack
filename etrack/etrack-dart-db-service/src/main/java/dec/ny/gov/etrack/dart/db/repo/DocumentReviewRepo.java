package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.DocumentReviewEntity;

@Repository
public interface DocumentReviewRepo extends CrudRepository<DocumentReviewEntity, Long> {
  
  @Query(value="select d.document_review_id, s.document_id document_id, e.project_id, s.document_desc document_desc, s.document_nm document_nm, "
      + "d.doc_reviewer_id, d.doc_reviewer_name, d.doc_reviewed_ind, d.review_assigned_date, d.review_due_date, d.create_date, d.correspondence_id "
      + "from {h-schema}e_support_document s, {h-schema}e_document_review d, {h-schema}e_email_correspondence e "
      + "where s.document_id=d.document_id "
      + "and d.correspondence_id=e.correspondence_id and s.document_state_code='A' "
      + "and e.email_status ='S' and e.project_id=?1 "
      + "union "
      + "select d.document_review_id, null, e.project_id, null,  null, "
      + "d.doc_reviewer_id, d.doc_reviewer_name, d.doc_reviewed_ind, d.review_assigned_date, d.review_due_date, d.create_date, d.correspondence_id "
      + "from {h-schema}e_document_review d, {h-schema}e_email_correspondence e "
      + "where d.correspondence_id=e.correspondence_id and e.email_status ='S' "
      + "and e.project_id=?1 and d.document_id is null order by create_date desc", nativeQuery=true)
  List<DocumentReviewEntity> findAllByProjectId(Long projectId);
  
  @Query(value="select to_char(review_due_date, 'mm/dd/yyyy') from {h-schema}e_support_document sd, "
      + "{h-schema}e_document_review r where sd.document_id=r.document_id "
//      + "and r.doc_reviewer_id=?1 "
      + "and sd.project_id=?1 and r.doc_reviewed_ind is null "
      + "order by r.review_due_date desc fetch first 1 row only", nativeQuery=true)
  String findReviewDateByProjectId(final Long projectId);
}
