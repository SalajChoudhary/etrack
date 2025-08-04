package dec.ny.gov.etrack.permit.repo;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Project;

@Repository
public interface ProjectRepo extends CrudRepository<Project, Long> {
  
  @Modifying
  @Query("update Project set projectDesc= :projectDesc, proposedUseCode= :proposedUseCode, "
      + "modifiedById= :userId, modifiedDate= :currDate, constrnType= :constrnType, "
      + "proposedStartDate= :proposedStartDate, estmtdCompletionDate= :estmtdCompletionDate, "
      + "strWaterbodyName= :strWaterbodyName, wetlandIds= :wetlandIds, damType= :damType, seqrInd= :seqrInd"
      + " where projectId= :projectId")
  public void updateProjectInfo(String projectDesc, String proposedUseCode, Long projectId,
      String userId, Date currDate, Integer constrnType, Date proposedStartDate,
      Date estmtdCompletionDate, String strWaterbodyName, String wetlandIds, String damType, Integer seqrInd);
  
  @Modifying
  @Query(value = "update {h-schema}e_public set public_signed_ind=1, modified_by_id=?2, modified_date=?3  "
      + "where public_id in (?1) and project_id=?4", nativeQuery = true)
  public void updatePublicSignatureReceivedInd(List<Long> publicIdList, String userId, Date date, Long projectId);
  
  @Modifying
  @Query(value = "update {h-schema}e_project set validated_ind=?4, modified_by_id=?1, modified_date=?3  "
      + "where project_id=?2", nativeQuery = true)
  public void updateValidateInd(String userId, Long projectId, Date date, Integer validateInd);
  
  @Modifying
  @Query(value = "update {h-schema}e_project set analyst_assigned_id=?3, assigned_analyst_name=?4, modified_by_id=?1, "
      + "modified_date=sysdate, analyst_assigned_date=sysdate, assigned_analyst_role_id=?5 where project_id=?2", nativeQuery = true)
  public void updateProjectAssignment(final String userId, final Long projectId, 
      final String analystAssignedId, final String analystName, final Long analystRoleId);

  @Modifying
  @Query(value = "update {h-schema}e_project set ea_ind=?3, modified_by_id=?1, "
      + "modified_date=sysdate where project_id=?2", nativeQuery = true)
  public void updateEAIndicator(final String userId, final Long projectId, final Integer eaInd);

  @Modifying
  @Query(value = "update {h-schema}e_project set foil_req_ind=?3, modified_by_id=?1, "
      + "modified_date=sysdate where project_id=?2", nativeQuery = true)
  public void updateProjectFoilRequestIndicator(String userId, final Long projectId, Integer foilRequestInd);

  @Transactional
  @Modifying
  @Query(value = "update {h-schema}e_project set  modified_by_id=?1, modified_date=sysdate, received_date=?3 "
      + "where project_id=(select project_id from {h-schema}e_project_activity_task_status "
      + "where activity_status_id=14 and project_id=?2)", nativeQuery = true)
  int updateProjectUploadedDetail(String userId, Long projectId, Date receivedDate);

  @Transactional
  @Modifying
  @Query(value = "update {h-schema}e_project set upload_to_dart_ind=1, modified_by_id=?1, modified_date=sysdate "
      + "where project_id=(select project_id from {h-schema}e_project_activity_task_status "
      + "where activity_status_id=14 and project_id=?2)", nativeQuery = true)
  int updateUploadToDartAfterSuccessful(String userId, Long projectId);

  
  @Query(value="select * from {h-schema}e_project where upload_to_dart_ind=1 and project_id=?1", nativeQuery=true)
  Project findByProjectIdAndUploadToDart(final Long projectId);

  @Query(value="select * from {h-schema}e_project where project_id in(projectIds)", nativeQuery=true)
  public List<Project> findPolygonUploadEligibleProjects(Set<Long> projectIds);

  @Modifying
  @Query(value = "update {h-schema}e_project set approved_polygon_change_ind=?4, modified_by_id=?1, modified_date=?3  "
      + "where project_id=?2", nativeQuery = true)
  public void updateApprovedPolygonChangeInd(String userId, Long projectId, Date date,
      Integer approvedPolygonChangeInd);

//  @Modifying
//  @Transactional
//  @Query(value = "update {h-schema}e_project set online_appln_ind=1, modified_by_id=?1, "
//      + "modified_date=sysdate where project_id=?2", nativeQuery = true)
//  public void updateOnlineSubmissionInd(String userId, Long projectId);
  
  @Query(value="select count(*) from {h-schema}e_project p where p.analyst_assigned_id is not null "
      + "and p.original_submittal_ind=1 and p.project_id=?2 and lower(p.analyst_assigned_id)=lower(?1) ", nativeQuery=true)
  public int retrieveRetrieveEligibleProjectId(final String userId, final Long projectId);

  @Modifying
  @Query(value="update {h-schema}e_project set original_submittal_ind=0, rejected_ind=1, "
      + "rejected_reason=?3, analyst_assigned_id=null, assigned_analyst_role_id=null,"
      + " modified_by_id=?1, modified_date=sysdate, rejected_date=sysdate where project_id=?2", nativeQuery = true)
  public void revertProjectToDataEntry(String userId, Long projectId, final String rejectedReason);

  @Query(value="select project_id from {h-schema}e_project where project_id=?1 "
      + "and (original_submittal_ind is null or original_submittal_ind=0 or rejected_ind=1)", nativeQuery=true) 
  public Long findUnsubmittedProjectByProjectId(Long projectId);
}
