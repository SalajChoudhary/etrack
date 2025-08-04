package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentEntity;

@Repository
public interface SupportDocumentRepo extends CrudRepository<SupportDocumentEntity, Long> {

  @Query(value="select sd.document_id as document_uid, sd.document_id, sd.ecmaas_guid, sd.access_by_dep_only_ind, sd.doc_releasable_code, sd.tracked_application_id,"
      + "sd.document_type_id, sd.document_sub_type_id, sd.document_state_code, sd.document_desc, sd.document_sub_type_title_id,"
      + "sd.doc_sub_type_other_txt, sd.create_date, sd.created_by_id, sd.modified_by_id, sd.modified_date, sd.project_id,"
      + "sd.document_nm, sd.ref_document_id, sd.addl_doc_ind, sd.support_doc_category_code, sd.ref_document_desc, 0 file_count, "
      + "sd.doc_conf_ind,  sd.archive_reviewed_ind, sd.purge_reviewed_ind, sd.arc_prg_query_result_id "
      + "  from {h-schema}e_support_document sd where sd.project_id=?1 "
      + "and sd.document_state_code='A' order by sd.create_date desc", nativeQuery=true)
  List<SupportDocumentEntity> findAllUploadedSupportDocumentsByProjectId(final Long projectId);

  @Query(value="select nvl(sf.document_file_id+sd.document_id, sd.document_id) as document_uid, sd.document_id, sd.ecmaas_guid, sd.access_by_dep_only_ind, "
      + "sd.doc_releasable_code, sd.tracked_application_id,"
      + "sd.document_type_id, sd.document_sub_type_id, sd.document_state_code, sd.document_desc, sd.document_sub_type_title_id,"
      + "sd.doc_sub_type_other_txt, sd.create_date, sd.created_by_id, sd.modified_by_id, sd.modified_date, sd.project_id,"
      + "sd.document_nm, sd.ref_document_id, sd.addl_doc_ind, sd.support_doc_category_code, sd.ref_document_desc, 1 as file_count, "
      + "sd.doc_conf_ind, sd.archive_reviewed_ind, sd.purge_reviewed_ind, sd.arc_prg_query_result_id  "
      + "from {h-schema}e_support_document sd, {h-schema}e_support_document_file sf where sd.document_id=sf.document_id(+) "
      + "and sd.project_id=?1 and sd.document_state_code='A' order by sd.create_date desc", nativeQuery=true)
  List<SupportDocumentEntity> findAllUploadedSupportDocumentsByProjectIdWithFilesCount(final Long projectId);
  
  @Query(value="select distinct e.project_id from {h-schema}e_facility f, {h-schema}e_document_review r , "
      + "{h-schema}e_email_correspondence e where f.project_id=e.project_id and e.correspondence_id=r.correspondence_id "
      + "and r.review_due_date is not null and r.review_due_date > sysdate "
      + "and (r.doc_reviewed_ind is null or r.doc_reviewed_ind != 1) and "
      + "(?1 is null or lower(f.created_by_id)=lower(?1)) order by e.project_id desc ", nativeQuery=true)
  List<Long> findAllOutForReviewProjects(final String userId);
  
  @Query(value="select distinct p.project_id, dr.doc_reviewer_name from {h-schema}e_project p, {h-schema}e_facility f, "
      + "{h-schema}e_facility_address fa, {h-schema}e_application a, "
      + "{h-schema}e_support_document sd, {h-schema}e_document_review dr, {h-schema}e_permit_type_code pt, {h-schema}e_project_milestone pm "
      + "where p.project_id=f.project_id and f.project_id=pm.project_id and f.project_id = fa.project_id (+) "
      + "and p.project_id=a.project_id and a.project_id=sd.project_id and sd.document_id=dr.document_id "
      + "and a.permit_type_code=pt.permit_type_code and (dr.doc_reviewed_ind is null or dr.doc_reviewed_ind != 1) "
      + "and p.project_id in (?1) order by p.project_id desc", nativeQuery = true)
  List<String> findReviewerDetailsByProjectIds(final Set<Long> projectId);
}
