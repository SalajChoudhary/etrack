package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;

@Repository
public interface ApplicantRepo extends CrudRepository<ApplicantDto, Long> {

  /**
   * This method/query will fetch all the Contacts/Agents associated with the input Project id or
   * Facility
   * 
   * @param projectId - Project id
   * @param associatedInd - 0- indicates user part of the facility but not added. 1 - part of the
   *        project
   * 
   * @return - list of applicants
   */
  @Query(
      value = "select distinct p.public_id as public_id, p.public_type_code as public_type_code, p.display_name as display_name, p.project_id,"
          + "p.edb_public_id as edb_public_id, p.public_signed_ind, '' as role, '' as modified, p.validated_ind, "
//          + "p.first_name, p.last_name, p.middle_name, p.online_submitter_ind from {h-schema}e_public p, {h-schema}e_role r "
          + "p.first_name, p.last_name, p.middle_name from {h-schema}e_public p, {h-schema}e_role r "
          + "where p.public_id=r.public_id and p.project_id = ?1 and r.selected_in_etrack_ind = ?2 and r.role_type_id in (2,3,4,5)",
      nativeQuery = true)
  public List<ApplicantDto> findAllContactsByAssociatedInd(final Long projectId,
      final Integer associatedInd);

  /**
   * This method/query will fetch all the Property Owners associated with the input Project
   * id/Facility
   * 
   * @param projectId - Project id
   * @param associatedInd - 0- indicates user part of the facility but not part of the project . 1 -
   *        Owners part of the project
   * 
   * @return - list of applicants
   */
  @Query(
      value = "select distinct p.public_id as public_id, p.public_type_code as public_type_code, p.display_name as display_name, p.project_id,"
          + "p.edb_public_id as edb_public_id, p.public_signed_ind , '' as role, '' as modified, p.validated_ind, "
//          + "p.first_name, p.last_name, p.middle_name, p.online_submitter_ind from {h-schema}e_public p, {h-schema}e_role r "
          + "p.first_name, p.last_name, p.middle_name from {h-schema}e_public p, {h-schema}e_role r "
          
          + "where p.public_id=r.public_id and p.project_id= ?1 and r.selected_in_etrack_ind= ?2 and r.role_type_id=6",
      nativeQuery = true)
  public List<ApplicantDto> findAllOwnersByAssociatedInd(final Long projectId,
      final Integer associatedInd);

  /**
   * This method/query will fetch all the Publics/LRPs associated with the input Project id
   * 
   * @param projectId - Project id
   * @param associatedInd - 0- indicates user part of the facility but not part of the project . 1 -
   *        Applicants part of the project
   * 
   * @return - list of applicants
   */
  @Query(
      value = "select distinct p.public_id as public_id, p.public_type_code as public_type_code, p.display_name as display_name, p.project_id, "
          + "p.edb_public_id as edb_public_id, p.public_signed_ind, '' as role, '' as modified, p.validated_ind, "
//          + "p.first_name, p.last_name, p.middle_name, p.online_submitter_ind from {h-schema}e_public p, {h-schema}e_role r "
          + "p.first_name, p.last_name, p.middle_name from {h-schema}e_public p, {h-schema}e_role r "
          
          + "where p.public_id=r.public_id and project_id=?1 and r.selected_in_etrack_ind= ?2 "
          + "and ((r.role_type_id =1 or (r.role_type_id=6 and r.legally_responsible_type_code=1)) or r.legally_responsible_type_code in (1,2,3))",
      nativeQuery = true)
  public List<ApplicantDto> findAllPublicsByAssociatedInd(final Long projectId,
      final Integer associatedInd);
  
  @Query(value = "select distinct p.public_id, p.public_type_code, p.display_name, p.first_name, "
//      + "p.last_name, p.middle_name, p.online_submitter_ind, p.edb_public_id, p.project_id, "
      + "p.last_name, p.middle_name, p.edb_public_id, p.project_id, "
      + "p.public_signed_ind, '' as role, '' as modified, p.validated_ind from {h-schema}e_project prj, {h-schema}e_public p, {h-schema}e_role r "
      + "where prj.project_id=p.project_id and p.public_id = r.public_id and (?1 is null or (?1 is not null and prj.created_by_id=?1))"
      + "and p.public_id in (select pub.public_id "
      + " from {h-schema}e_project prj, {h-schema}e_public pub, {h-schema}e_role r1 "
      + "where prj.project_id=pub.project_id and p.public_id=pub.public_id and pub.public_id=r1.public_id "
      + "and ((r1.role_type_id =1 or (r1.role_type_id=6 and r1.legally_responsible_type_code=1)) or r1.legally_responsible_type_code in (1,2,3)) "
      + "and r1.selected_in_etrack_ind=1 and pub.project_id is not null order by pub.public_id asc"
      + " fetch first 1 row only) order by p.project_id desc", nativeQuery = true)
  public List<ApplicantDto> findLRPDetailsByCreateById(final String userId);

  @Query(value = "select distinct p.public_id, p.public_type_code, p.display_name, p.first_name, "
//      + "p.last_name, p.middle_name, p.online_submitter_ind, p.edb_public_id, p.project_id, "
      + "p.last_name, p.middle_name, p.edb_public_id, p.project_id, "
      + "p.public_signed_ind, '' as role, '' as modified, p.validated_ind from {h-schema}e_project prj, {h-schema}e_public p, {h-schema}e_role r "
      + "where prj.project_id=p.project_id and p.public_id = r.public_id and prj.project_id=?1 "
      + "and p.public_id in (select pub.public_id "
      + " from {h-schema}e_project prj, {h-schema}e_public pub, {h-schema}e_role r1 "
      + "where prj.project_id=pub.project_id and p.public_id=pub.public_id and pub.public_id=r1.public_id "
      + "and ((r1.role_type_id =1 or (r1.role_type_id=6 and r1.legally_responsible_type_code=1)) or r1.legally_responsible_type_code in (1,2,3)) "
      + "and r1.selected_in_etrack_ind=1 and pub.project_id is not null order by pub.public_id asc"
      + " fetch first 1 row only) order by p.project_id desc", nativeQuery = true)
  public List<ApplicantDto> findLRPDetailsByProjectId(final Long projectId);

}
