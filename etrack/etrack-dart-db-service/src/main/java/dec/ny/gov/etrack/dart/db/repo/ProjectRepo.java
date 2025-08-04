package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.Project;

@Repository
public interface ProjectRepo extends CrudRepository<Project, Long> {
  @Query(value="select program_application_code, program_application_identifier, "
      + "edb_program_application_identifier from {h-schema}e_program_application where project_id=?1", nativeQuery=true)
  List<String> findAllProgramXTRAIDSByProjectId(final Long projectId);
  @Query(value="select program_district_code, program_district_identifier, "
      + "edb_program_district_identifier from {h-schema}e_program_district_identifier where project_id=?1", nativeQuery=true)
  List<String> findAllProgramProgramIdsByProjectId(final Long projectId);
  @Query(value="select special_attention_code from {h-schema}e_special_attention where project_id=?1", nativeQuery=true)
  List<String> findAllProjectSpecialAttnByProjectId(final Long projectId);
  @Query(value="select p.project_id, p.rejected_reason, f.facility_name from {h-schema}e_project p, "
      + "{h-schema}e_facility f where p.project_id=f.project_id and p.rejected_ind is not null and p.rejected_ind=1 and p.project_id=?1", nativeQuery = true)
  List<String> retrieveProjectRejectionDetails(final Long projectId);
  @Query(value="select inquiry_id from {h-schema}e_project_inq_associate where project_id=?1", nativeQuery = true)
  List<Long> findAllInquiriesByProjectId(Long projectId);
  
  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_email_correspondence set email_read=1 where project_id=?1", nativeQuery = true)
  void updateEmailCorrespondenceAsRead(Long projectId);
}
