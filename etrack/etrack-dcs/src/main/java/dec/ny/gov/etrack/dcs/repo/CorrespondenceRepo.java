package dec.ny.gov.etrack.dcs.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.entity.EmailCorrespondence;

@Repository
public interface CorrespondenceRepo extends CrudRepository<EmailCorrespondence, Long>{
  @Query(value="select * from {h-schema}e_email_correspondence  where topic_id in ("
      + "select distinct topic_id from {h-schema}e_email_correspondence e, {h-schema}e_document_review r "
      + "where e.correspondence_id=r.correspondence_id and r.correspondence_id=?2 "
      + "and r.doc_reviewer_id=?1) and deleted_ind!=1  and ref_correspondence_id is not null order by correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> findAllCorrespondenceByReviewerIdAndDocReviewId(final String reviewerId, final Long topicId);
}
