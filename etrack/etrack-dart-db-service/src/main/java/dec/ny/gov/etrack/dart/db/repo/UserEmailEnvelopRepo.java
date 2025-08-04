package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.UserEmailEnvelopDetail;

@Repository
public interface UserEmailEnvelopRepo extends CrudRepository<UserEmailEnvelopDetail, Long> {

  @Query(value="select correspondence_id, email_subject, cc_email_adr, "
      + "to_email_adr, ref_corresp_id, email_rqstd_user_id, email_rcvd_user_id, email_rcvd_user_name"
      + "from {h-schema}e_email_correspondence where project_id=?2 and email_rqstd_user_id=?1 "
      + "and email_status='S' and email_read is null", nativeQuery = true)
  List<UserEmailEnvelopDetail> findAllEmailCorrespondenceByUserIdAndProjectId(final String userId, final Long projectId);
}
