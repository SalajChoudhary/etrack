package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Public;

@Repository
public interface PublicRepo extends CrudRepository<Public, Long> {

  @Modifying
  @Query(value="update {h-schema}e_role set selected_in_etrack_ind=0 where public_id=?1 and role_type_id in (?2)", nativeQuery = true)
  void updateSeletedInETrackInd(Long publicId, List<Integer> roleTypeIds);

  List<Public> findByPublicIdAndEdbPublicIdAndProjectId(final Long publicId,
      final Long edbPublicId, final Long projectId);

  @Query(value="select p.* from {h-schema}e_public p, {h-schema}e_role r  where p.public_id=r.public_id "
      + "and p.edb_public_id=?1 and p.project_id=?2 and r.selected_in_etrack_ind=1", nativeQuery=true)
  List<Public> findByEdbPublicIdAndProjectId(final Long edbPublicId, final Long projectId);

  @Query(value="select * from {h-schema}e_public where edb_public_id=?1 and project_id=?2", nativeQuery=true)
  List<Public> findByEdbPublicIdAndProjectId1(final Long edbPublicId, final Long projectId);

  List<Public> findByPublicIdAndProjectId(final Long publicId, final Long projectId);

  List<Public> findByDisplayNameAndProjectId(final String displayName, final Long ProjectId);

  List<Public> findByDisplayNameAndProjectIdAndPublicTypeCode(final String displayName,
      final Long ProjectId, final String publicTypeCode);

  @Query(value = "select distinct p.public_id as public_id from {h-schema}e_public p, {h-schema}e_role r "
      + "where p.public_id=r.public_id and p.project_id=?2 and p.display_name=?1 and p.public_type_code=?3 "
      + "and r.role_type_id in(2, 3, 4, 5)", nativeQuery = true)
  List<Long> findExistingContacts(final String displayName, final Long ProjectId,
      final String publicTypeCode);
  
  @Query(value = "select distinct p.public_id as public_id from {h-schema}e_public p, {h-schema}e_role r "
      + "where p.public_id=r.public_id and p.project_id=?1 "
      + "and r.role_type_id in(2, 3, 4, 5) and r.selected_in_etrack_ind=1 ", nativeQuery = true)
  List<Long> findAllContactsByProjectId(final Long ProjectId);

  @Query(value = "select distinct p.public_id as public_id from {h-schema}e_public p, {h-schema}e_role r "
      + "where p.public_id=r.public_id and p.project_id=?2 and p.display_name=?1 and p.public_type_code=?3 "
      + "and r.role_type_id=6", nativeQuery = true)
  List<Long> findExistingOwner(final String displayName, final Long ProjectId,
      final String publicTypeCode);

  @Query(value = "select distinct p.public_id as public_id from {h-schema}e_public p, {h-schema}e_role r "
      + "where p.public_id=r.public_id and p.project_id=?2 and p.display_name=?1 and p.public_type_code=?3 "
      + "and (r.role_type_id=1 or r.legally_responsible_type_code in (1,2,3))", nativeQuery = true)
  List<Long> findExistingPublic(final String displayName, final Long ProjectId,
      final String publicTypeCode);

  @Query(value = "select p.* from {h-schema}e_public p, {h-schema}e_role r where p.public_id=r.public_id and p.project_id=?1 and r.selected_in_etrack_ind=1 and p.public_id in (?2)", nativeQuery = true)
  List<Public> findAllPublicByIds(final Long projectId, final List<Long> publicIds);

  @Query(value = "select p.public_id from {h-schema}e_public p, {h-schema}e_role r where p.public_id=r.public_id and p.project_id=?1 and r.selected_in_etrack_ind=1", nativeQuery=true)
  List<Long> findAllPublicByProjectId(final Long projectId);

  @Query(value = "select p.public_id from {h-schema}e_public p, {h-schema}e_role r where p.public_id=r.public_id and p.project_id=?1 and r.selected_in_etrack_ind=1 and p.public_id not in (?2)", nativeQuery = true)
  List<Long> findAllExcludedPublicIds(final Long projectId, final List<Long> publicIds);

  @Modifying
  @Query(value = "update {h-schema}e_public p set public_signed_ind=?4, modified_by_id=?1, modified_date=sysdate "
      + "where p.project_id=?2 and p.public_id in (?3) ", nativeQuery=true)
  int updateAllAcknowledgedApplicants(final String userId, final Long projectId, final List<Long> publicIds, Integer signeddIndicator);

  @Modifying
  @Query(value = "update {h-schema}e_public p set p.online_submitter_ind=0 where p.project_id=?1", nativeQuery=true)
  void resetTheExistingOnlineSubmitter(Long projectId);
  
  @Modifying
  @Query(value = "update {h-schema}e_public p set p.online_submitter_ind=1, p.modified_by_id=?1, p.modified_date=sysdate "
      + "where p.project_id=?1 and p.public_id=?2 ", nativeQuery=true)
  void updatePublicAsOnlineSubmitter(Long projectId, Long publicId);  
}
