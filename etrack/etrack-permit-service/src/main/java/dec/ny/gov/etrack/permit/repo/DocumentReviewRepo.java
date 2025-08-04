package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.DocumentReviewEntity;

@Repository
public interface DocumentReviewRepo extends CrudRepository<DocumentReviewEntity, Long>{

  @Modifying
  @Query(value = "update {h-schema}e_document_review set doc_reviewed_ind=?4, "
      + "modified_by_id=?1, modified_date=sysdate where correspondence_id=?2 and doc_reviewer_id=?3", nativeQuery = true)
  void updateDocumentReviewCompletionDetails(
      String userId, Long correspondenceId, String reviewerId, final Integer docReviewedInd);

  /**
   * Retrieve the list of document reviews requested.
   * 
   * @param correspondenceId - Correspondence id.
   * 
   * @return - List of Document Review eligible reviews.
   */
  List<DocumentReviewEntity> findByCorrespondenceId(Long correspondenceId);
}
