package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocumentReview;

@Repository
public interface SpatialInquiryDocumentReviewRepo extends CrudRepository<SpatialInquiryDocumentReview, Long> {
  @Query(value="select gr.gi_document_review_id, si.document_id document_id, si.inquiry_id, si.document_desc document_desc, si.document_nm document_nm, "
      + "gr.doc_reviewer_id, gr.doc_reviewer_name, gr.doc_reviewed_ind, gr.review_assigned_date, gr.review_due_date, gr.create_date, gr.review_group_id "
      + "from {h-schema}e_spatial_inq_document si, {h-schema}e_gi_document_review gr where si.document_id=gr.document_id and si.document_state_code='A' "
      + "and si.inquiry_id=?1 order by create_date desc", nativeQuery = true)
  List<SpatialInquiryDocumentReview> findAllReviewDocumentsByInquiryId(Long inquiryId);
}
