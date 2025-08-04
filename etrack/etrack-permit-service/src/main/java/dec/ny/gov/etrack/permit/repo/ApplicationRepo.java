package dec.ny.gov.etrack.permit.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Application;

@Repository
public interface ApplicationRepo extends CrudRepository<Application, Long> {
  public List<Application> findByProjectId(Long projectId);

//  @Query(name ="select a.role_id as role_id from {h-schema}e_application a where a.project_id=?1 and a.role_id is not null", nativeQuery=true)
//  public List<Long> findByProjectIdAndRoleId(final Long projectId);

  @Query(name ="delete from {h-schema}e_application a where a.project_id=?1 and a.role_id=?2", nativeQuery=true)
  public void deleteByProjectIdAndRoleId(final Long projectId, final Long roleId);
 
  @Modifying
  @Query(name ="delete from {h-schema}e_application a where a.project_id=?1", nativeQuery=true)
  public void deleteByProjectId(final Long projectId);
  
//  @Modifying
//  @Query(value="update Application set roleId= :roleId, edbApplId= :edbPublicId, modifiedDate= :currDate, "
//      + "modifiedById= :userId, transTypeCode= :transTypeCode "
//      + "where applicationId= :applicationId and projectId= :projectId and permitTypeCode= :permitTypeCode")
//  public void updatePermitContacts(final Long roleId, final Long edbPublicId, final Date currDate, 
//      final String userId, final Long applicationId, final Long projectId, final String permitTypeCode, final String transTypeCode);

  @Modifying
  @Query(value="update Application set roleId= :roleId, modifiedDate= :currDate, "
      + "modifiedById= :userId where applicationId= :applicationId and projectId= :projectId")
  public int updatePermitContacts(final Long roleId, final Date currDate, 
      final String userId, final Long applicationId, final Long projectId);
  
  public Optional<Application> findByApplicationIdAndProjectIdAndPermitTypeCode(Long applicationId,
      Long projectId, String permitTypeCode);
  
  @Query(value="select appl from Application appl where appl.edbApplId in (:edbApplId) "
      + "and appl.projectId= :projectId and appl.permitTypeCode in (:permitTypeCode)")
  public List<Application> findByEdbApplicationIdsAndProjectIdAndPermitTypeCode(List<Long> edbApplId,
      Long projectId, List<String> permitTypeCode);
  
  
  @Modifying
  @Query(value="update {h-schema}e_application set form_submitted_ind='Y', "
      + "permit_mod_reason=?1, permit_extended_date=?2, modified_by_id=?7, modified_date=sysdate where application_id=?3 "
      + "and edb_appl_id=?4 and permit_type_code=?5 and batch_id_edb=?6 ", nativeQuery=true)
  public void updatePermitformSubmissionDetails(final String reason, final Date permitExtendedDate, 
      final Long applicationId, final Long edbApplId, 
      final String permitType, final Long batchIdEdb, final String userId);
  
  List<Application> findAllByBatchIdEdbAndProjectId(Long batchIdEdb, Long projectId);
  
  @Modifying
  @Query(value="update {h-schema}e_application set trans_type_code=?2, modified_by_id=?1,"
      + " modified_date=sysdate where application_id in (?3)", nativeQuery=true)
  public int updateReviewedPermitsByApplicationIds(final String userId, 
      final String reviewedTransType, final Set<Long> applicationIds);
  
  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_application set upload_trans_type_code=?2, modified_by_id=?1,"
      + " modified_date=sysdate, tracking_ind=?4, batch_group_etrack=?5 where application_id=?3", nativeQuery=true)
  public int updateReviewedETrackPermitsByApplicationId(final String userId,  final String reviewedTransType, 
      final Long applicationId, final Integer trackingInd, final String eTrackBatchGroup);

  @Query(value="select application_id from {h-schema}e_application appl where appl.project_id= :projectId", nativeQuery=true)
  List<Long> findAllApplicationIdByProjectId(final Long projectId);

  @Query(value="select * from {h-schema}e_application appl where appl.application_id in (?1) and appl.project_id=?2", nativeQuery=true)
  public List<Application> findAllByIdAndProjectId(Set<Long> applicationIds, Long projectId);

  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_application set upload_trans_type_code=?2, modified_by_id=?1,"
      + " modified_date=sysdate, tracking_ind=?4 where application_id=?3", nativeQuery=true)
  public void updateReviewedDARTPermitsByApplicationId(String userId, String modifiedTransType,
      Long applicationId, Integer trackingInd);
  
  @Query(value="select permit_type_code from {h-schema}e_permit_type_code where permit_type_code in ("
      + "select related_permit_type_code from {h-schema}e_gp_related_permit where gp_permit_type_code in (?1))", nativeQuery=true)
  List<String> findAllRelatedRegularPermitsForGP(final Set<String> generalPermits);
  
  @Query(value="select distinct permit_type_code from {h-schema}e_application where project_id=?1 order by permit_type_code asc", nativeQuery=true)
  List<String> findAllPermitTypesByProjectId(final Long projectId);
  
  @Query(value="select * from {h-schema}e_application where project_id=?1 "
      + "and batch_id_edb=?2 and tracking_ind=1 fetch first rows only", nativeQuery=true)
  Application findExistingApplicationByProjectIdAndBatchId(final Long projectId, final Long batchId);

  @Transactional
  @Modifying
  @Query(value="delete {h-schema}e_application where application_id=?1", nativeQuery=true)
  void deleteAdditionalPermitByApplicationId(Long applicationId);
  
  @Query(value="select count(*) from {h-schema}e_application where project_id=?2 and upper(permit_type_code)=upper(?1)", nativeQuery=true)
  int findApplicantAlreadySubmitted(String permitTypeCode, final Long projectId);

  @Modifying
  @Query(value="delete {h-schema}e_proj_sw_facility_type where project_id=?1", nativeQuery = true)
  int deleteSwFacilityTypeCode(Long projectId);

  @Modifying
  @Query(value="update {h-schema}e_project set dam_type=null, "
      + "modified_by_id=?1, modified_date=sysdate where project_id=?2", nativeQuery = true)
  void resetDAMConstructionTypeDetails(String userId, Long projectId);

  @Modifying
  @Query(value="update {h-schema}e_project set constrn_type=null, proposed_start_date=null, "
      + "proposed_eff_date=null, modified_by_id=?1, modified_date=sysdate where project_id=?2", nativeQuery = true)
  void resetConstructionTypeDetails(String userId, Long projectId);

  @Query(value="select distinct permit_type_code from {h-schema}e_application where project_id=?1 "
      + "and permit_type_code in (?2) order by permit_type_code asc", nativeQuery=true)
  List<String> findAvailableConstructionPermitsByProjectId(Long projectId,
      List<String> constnPermitCodes);
}
