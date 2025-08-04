package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.EmailCorrespondence;

@Repository
public interface EmailCorrespondenceRepo extends CrudRepository<EmailCorrespondence, Long> {

  List<EmailCorrespondence> findByProjectId(Long projectId);

  @Query(value="select e.* from {h-schema}e_email_correspondence e "
      + "where e.topic_id in(select a.topic_id from {h-schema}e_email_correspondence a where a.project_id=?2 and a.correspondence_id=?1) "
      + "and e.ref_correspondence_id is not null and (e.deleted_ind is null or e.deleted_ind=0)  and e.email_status='S' order by e.create_date desc", nativeQuery=true)
  List<EmailCorrespondence> findByCorrespondenceIdAndProjectId(Long correspondenceId, Long projectId);

  @Query(value="select a.* from {h-schema}e_email_correspondence a, "
      + "(select c.project_id, c.topic_id, max(c.create_date) request_date "
      + "from {h-schema}e_email_correspondence c where c.created_by_id=?1 or c.email_rcvd_user_id=?1 group by c.project_id, c.topic_id) b "
      + "where a.project_id=b.project_id and a.topic_id=b.topic_id "
      + "and a.create_date=b.request_date "
      + " and a.email_status='S' order by a.correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> findByCorrepondenceByUserId(String userId);
  
  @Query(value="select a.* from e_email_correspondence a, "
      + "(select c.project_id, c.topic_id, max(c.create_date) request_date "
      + "from e_email_correspondence c group by c.project_id, c.topic_id) b "
      + "where a.project_id=b.project_id and a.topic_id=b.topic_id "
      + "and a.create_date=b.request_date  and a.email_status='S' "
      + "and a.project_id=?2 and (a.email_rqstd_user_id=?1 or a.email_rcvd_user_id=?1) and a.email_read is null "
      + "order by a.correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> findByCorrepondenceByUserIdAndProjectId(final String userId, final Long projectId);
  
  @Query(value="select * from {h-schema}e_email_correspondence where (email_rqstd_user_id=?1 OR email_rcvd_user_id=?1) "
      + "and email_status='S' and (deleted_ind is null or deleted_ind=0) and project_id=?2 order by create_date desc", nativeQuery=true)
  List<EmailCorrespondence> findCorrespondenceByUserIdAndProjectId(final String userId, final Long projectId);
  
//  @Query(value="select e.project_id, e.email_read from {h-schema}e_email_correspondence e where e.email_rcvd_user_id=?1 "
//      + "and e.email_status='S' and e.deleted_ind is null order by 1 desc", nativeQuery=true)
  @Query(value="select e.* from {h-schema}e_email_correspondence e where e.email_rcvd_user_id=?1 "
      + "and e.email_status='S' and e.email_read is null and (e.deleted_ind is null or e.deleted_ind=0) order by e.correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> retrieveEmailUnreadMessageCountByUserId(final String userId);

  @Query(value="select project_id, facility_name from {h-schema}e_facility where project_id in (?1)", nativeQuery=true)
  List<String> retrieveFacilityNameByProjectIds(final Set<Long> projectIds);
  
  @Query(value="select * from {h-schema}e_email_correspondence where email_rcvd_user_id=?1 "
      + "and email_rqstd_user_id=?2 and project_id=?3 and email_status='S' "
      + "and (deleted_ind is null or deleted_ind=0) order by create_date desc", nativeQuery=true)
  List<EmailCorrespondence> retrieveEmailCorrespondencesBetweenUser(
      final String receivedUserId, final String senderUserId, final Long projectId);  
  
  @Query(value="select from_email_adr from {h-schema}e_email_correspondence where created_by_id=? fetch first 1 row only", nativeQuery=true)
  List<String> retriveEmailAddress(final String userId);

  @Query(value="select correspondence_id from {h-schema}e_document_review where document_id=?2 and doc_reviewer_id=?1", nativeQuery=true)
  List<Long> findCorrespondenceIdByUserIdAndDocumentId(final String userId, final Long documentId);

  @Query(value="select e.correspondence_id from {h-schema}e_document_review dr, {h-schema}e_email_correspondence e "
      + "where dr.correspondence_id=e.topic_id and e.project_id=?2 and dr.document_id is null and e.ref_correspondence_id is not null "
      + "and dr.doc_reviewer_id=?1 order by e.correspondence_id desc", nativeQuery=true)
  List<Long> findCorrespondenceIdByReviewerId(final String reviewerId, final Long projectId);
  
  @Query(value="select e.* from {h-schema}e_support_document sd, {h-schema}e_document_review dr, {h-schema}e_email_correspondence e "
      + "where sd.document_id=dr.document_id and dr.correspondence_id=e.topic_id and sd.project_id=?2 "
      + "and sd.document_id in (?3) and dr.doc_reviewer_id=?1 order by e.correspondence_id desc", nativeQuery=true)
  List<EmailCorrespondence> retrieveEmailCorrespondencesByReviewerIdAndDocumentIds(String userId,
      Long projectId, List<Long> documentIds);
}
