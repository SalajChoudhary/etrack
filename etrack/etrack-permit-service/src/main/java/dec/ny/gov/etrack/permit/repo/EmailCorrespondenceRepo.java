package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.EmailCorrespondence;

@Repository
public interface EmailCorrespondenceRepo extends CrudRepository<EmailCorrespondence, Long> {
  EmailCorrespondence findByCorrespondenceIdAndProjectId(Long emailCorrespondenceId,
      Long projectId);

  @Modifying
  @Query(value="update {h-schema}e_email_correspondence set email_read=1, "
      + "modified_by_id=?1, modified_date=sysdate where project_id=?2 "
      + "and topic_id=?3 and upper(email_rcvd_user_id)=upper(?1) and (email_read=0 or email_read is null)", nativeQuery=true)
  void updateEmailReadStatus(final String userId, final Long projectId, final Long correspondenceId);  

  @Modifying
  @Query(value="update {h-schema}e_email_correspondence set deleted_ind=1, "
      + "modified_by_id=?1, modified_date=sysdate where project_id=?2 "
      + "and (correspondence_id=?3 or topic_id=?3)", nativeQuery=true)
  void deleteEmailCorrespondenceId(final String userId, final Long projectId, final Long correspondenceId);
  
  @Query(value="select * from {h-schema}e_email_correspondence  where topic_id in ("
      + "select distinct topic_id from {h-schema}e_email_correspondence e, {h-schema}e_document_review r "
      + "where e.correspondence_id=r.correspondence_id and r.correspondence_id=?2 "
      + "and upper(r.doc_reviewer_id)=upper(?1)) and deleted_ind!=1  and ref_correspondence_id is not null order by correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> findAllCorrespondenceByReviewerIdAndDocReviewId(final String reviewerId, final Long topicId);
}
