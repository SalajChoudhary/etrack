package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;

@Repository
public interface PermitRepo extends CrudRepository<ETrackPermit, Long> {

//  @Query(value="select f.edb_district_id, p.public_id, p.display_name, r.role_id, pt.permit_category_id, a.permit_type_code, pt.permit_type_desc, "
//      + "r.role_id as contact_role_id, a.application_id, a.edb_appl_id, a.role_id as contact_assigned_id "
//      + "from {h-schema}e_facility f, {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_application a, "
//      + "{h-schema}e_permit_type_code pt where f.project_id=p.project_id and p.public_id=r.public_id and p.project_id=a.project_id "
//      + "and a.permit_type_code=pt.permit_type_code and r.role_type_id in (2,3,4,5) and p.project_id=?1", nativeQuery =  true)

  @Query(value = "select f.edb_district_id, a.permit_type_code, a.trans_type_code, pt.permit_type_desc, pt.permit_category_id, "
      + "a.tracking_ind, a.edb_tracking_ind, a.user_sel_new_ind, a.user_sel_mod_ind, a.user_sel_ext_ind, a.user_sel_transfer_ind, a.user_sel_ren_ind,pending_ind,"
      + "a.chg_original_project_ind, a.edb_trans_type_code, to_char(a.edb_auth_exp_date, 'mm/dd/yyyy') effective_end_date, to_char(a.edb_auth_eff_date, 'mm/dd/yyyy') effective_start_date , "
      + "a.application_id, a.edb_appl_id, a.role_id as contact_assigned_id, pt.ref_link as ref_link, a.batch_id_edb, a.prog_id, a.form_submitted_ind, a.edb_auth_template_auth_id edb_auth_id, "
      + "decode(nvl(a.prog_id,'xxx'),'xxx',null,substr(a.prog_id,1,1)||'-'||substr(a.prog_id,2,4)||'-'||substr(a.prog_id,6,5) ||'/'||substr(a.prog_id,11)) prog_id_formatted, a.batch_group_etrack "
      + "from {h-schema}e_application a, {h-schema}e_facility f, {h-schema}e_permit_type_code pt "
      + "where a.project_id=f.project_id and a.permit_type_code=pt.permit_type_code and a.project_id=?1", nativeQuery=true)
  List<ETrackPermit> findETrackPermits(final Long projectId);
  
  @Query(value="select a.application_id, a.edb_appl_id, p.permit_type_code, a.trans_type_code, p.permit_type_desc, "
      + "a.tracking_ind, a.edb_tracking_ind, a.user_sel_new_ind, a.user_sel_mod_ind, a.user_sel_ext_ind, a.user_sel_transfer_ind, a.user_sel_ren_ind, pending_ind,"
      + "a.chg_original_project_ind, a.edb_trans_type_code, to_char(a.edb_auth_exp_date, 'mm/dd/yyyy') effective_end_date, to_char(a.edb_auth_eff_date, 'mm/dd/yyyy') effective_start_date,"
      + "p.permit_category_id, '' as edb_district_id, '' as contact_assigned_id, '' as ref_link, a.batch_id_edb, a.prog_id, a.form_submitted_ind, a.edb_auth_template_auth_id edb_auth_id, "
      + "decode(nvl(a.prog_id,'xxx'),'xxx',null,substr(a.prog_id,1,1)||'-'||substr(a.prog_id,2,4)||'-'||substr(a.prog_id,6,5) ||'/'||substr(a.prog_id,11)) prog_id_formatted, a.batch_group_etrack "
      + " from {h-schema}e_application a, {h-schema}e_permit_type_code p "
      + "where p.permit_type_code=a.permit_type_code and edb_appl_id is not null and project_id=?1 "
      + "and a.permit_type_code in (?2) and a.edb_appl_id in (?3)", nativeQuery=true)
  List<ETrackPermit> findDartAssignedPermits(final Long projectId, Set<String> permitTypes, Set<Long> edbApplId);

  @Query(value = "select f.edb_district_id, a.permit_type_code, a.trans_type_code, pt.permit_type_desc, pt.permit_category_id, "
      + "a.tracking_ind, a.edb_tracking_ind, a.user_sel_new_ind, a.user_sel_mod_ind, a.user_sel_ext_ind, a.user_sel_transfer_ind, a.user_sel_ren_ind,pending_ind,"
      + "a.chg_original_project_ind, a.edb_trans_type_code,to_char(a.edb_auth_exp_date, 'mm/dd/yyyy') effective_end_date, to_char(a.edb_auth_eff_date, 'mm/dd/yyyy') effective_start_date, "
      + "a.application_id, a.edb_appl_id, a.role_id as contact_assigned_id, pt.ref_link as ref_link, a.batch_id_edb, a.prog_id, a.form_submitted_ind, a.edb_auth_template_auth_id edb_auth_id,  "
      + "decode(nvl(a.prog_id,'xxx'),'xxx',null,substr(a.prog_id,1,1)||'-'||substr(a.prog_id,2,4)||'-'||substr(a.prog_id,6,5) ||'/'||substr(a.prog_id,11)) prog_id_formatted, a.batch_group_etrack "
      + "from {h-schema}e_application a, {h-schema}e_facility f, {h-schema}e_permit_type_code pt "
      + "where a.project_id=f.project_id and a.permit_type_code=pt.permit_type_code and a.project_id=?1 and a.user_sel_mod_ind=1", nativeQuery=true)
  List<ETrackPermit> findETrackModifiedPermits(Long projectId);
  
  
}
