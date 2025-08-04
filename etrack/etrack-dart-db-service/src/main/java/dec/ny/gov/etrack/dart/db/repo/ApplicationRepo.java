package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.Application;

@Repository
public interface ApplicationRepo extends CrudRepository<Application, Long> {
  @Query(value="select a.application_id, a.permit_type_code, p.permit_type_desc, a.project_id, a.trans_type_code, a.prog_id, '' as ea_ind, '' as related_reg_permit, "
      + "a.batch_id_edb, p.general_permit_ind gp_ind from {h-schema}e_application a, {h-schema}e_permit_type_code "
      + "p where a.permit_type_code=p.permit_type_code and a.project_id=?1 ", nativeQuery = true)
  List<Application> findAllByProjectId(Long projectId);

  @Query(value="select nvl(a.application_id+gp.gp_appl_id,a.application_id) as application_id, p.permit_type_code, "
      + "gp.permit_type_code related_reg_permit, p.permit_type_desc, a.project_id, a.trans_type_code, "
  + "decode(nvl(a.prog_id, nvl(gp.prog_id, 'xxx')),'xxx',null,"
  + "substr(nvl(a.prog_id, gp.prog_id), 1,1)||'-'||substr(nvl(a.prog_id, gp.prog_id),2,4)||'-'||substr(nvl(a.prog_id, gp.prog_id),6,5)||'/'||substr(nvl(a.prog_id, gp.prog_id),11)) prog_id, '' as ea_ind, a.batch_id_edb, p.general_permit_ind gp_ind "
  + "from {h-schema}e_application a, {h-schema}e_permit_type_code p, {h-schema}e_gp_application gp "
  + "where a.application_id=gp.application_id(+) and a.permit_type_code=p.permit_type_code "
  + "and a.project_id=?1 order by a.application_id desc", nativeQuery = true)
  List<Application> findAllUploadedApplnByProjectId(Long projectId);
  
//  @Query(value="select a.application_id, a.permit_type_code, p.permit_type_desc, a.project_id, a.trans_type_code, pr.ea_ind, "
//      + "decode(nvl(a.prog_id,'xxx'),'xxx',null,substr(a.prog_id,1,1)||'-'||substr(a.prog_id,2,4)||'-'||substr(a.prog_id,6,5)||'/'||substr(a.prog_id,11)) prog_id, "
//      + "a.batch_id_edb, p.general_permit_ind from {h-schema}e_application a, {h-schema}e_permit_type_code p, "
//      + "{h-schema}e_project pr where pr.project_id=a.project_id "
//      + "and a.permit_type_code=p.permit_type_code and a.project_id=?1 "
//      + "and a.application_id not in (select appl_id from {h-schema}e_project_milestone where project_id=?1)", nativeQuery=true)
//  List<Application> findUnTrackedApplicationsByProjectId(Long projectId); 
  
//  @Query(value="select decode(nvl(a1.prog_id,'xxx'),'xxx',null,substr(a1.prog_id,1,1)||'-'||substr(a1.prog_id,2,4)||'-'||substr(a1.prog_id,6,5)||'/'||substr(a1.prog_id,11)) tracked_id_formatted, "
//      + "permit_type_code from {h-schema}e_application a1 where a1.application_id=?1", nativeQuery = true)
//  List<String> findProgramIdAndPermitByApplicationId(final Long applicationId);
  
  @Query(value="select distinct p.permit_type_desc, a.trans_type_code, a.batch_id_edb, p.permit_type_code from {h-schema}e_application a, {h-schema}e_permit_type_code p "
      + "where a.permit_type_code=p.permit_type_code and project_id=?1", nativeQuery=true)
  List<String> findPermitTypesAndTransTypesByProjectId(final Long projectId);
  
//  @Query(value="select distinct batch_id_edb, trans_type_code "
//      + "from {h-schema}e_application where project_id=?1 and tracking_ind=1", nativeQuery=true)
  @Query(value="select distinct batch_id, edb_trans_type_code "
      + "from {h-schema}e_project_milestone where project_id=?1", nativeQuery = true)
  List<String> findBatchIdAndTransType(final Long projectId);

  @Query(value="select edb_appl_id from {h-schema}e_application where project_id=?1 and edb_appl_id is not null", nativeQuery=true)
  List<Long> findAllEnterpriseApplicationsAppliedInETrack(Long projectId);
}
