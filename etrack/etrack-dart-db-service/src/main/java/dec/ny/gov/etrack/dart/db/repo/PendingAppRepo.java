package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;

@Repository
public interface PendingAppRepo extends CrudRepository<PendingApplication, Long> {

  @Query(value = "select distinct f.project_id as project_id,  f.facility_name as facility_name, "
      + "a.street1 as location_directions, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, p.received_date, "
      + "a.street1 as street1, a.street2 as street2, "
      + "'' as display_name, '' as first_name, '' as last_name, '' as middle_name, '' as applicant_id, f.dec_id, f.edb_district_id, "
      + "'' as edb_public_id, '' as analyst_assigned_id, p.assigned_analyst_name , p.analyst_assigned_date, "
      + "p.ea_ind, p.rejected_ind, p.rejected_reason, to_char(p.rejected_date, 'mm/dd/yyyy') rejected_date "
      + "from {h-schema}e_facility f, {h-schema}e_facility_address a, {h-schema}e_facility_polygon fp, {h-schema}e_project p  "
      + "where (p.dimsr_ind is null or p.dimsr_ind=0) and f.created_by_id= ?1 and p.project_id=f.project_id "
      + "and f.project_id=a.project_id and f.project_id=fp.project_id and "
      + "(p.original_submittal_ind is null or p.original_submittal_ind=0) order by project_id desc", nativeQuery = true)
//      + "and not exists (select st.project_id from {h-schema}e_project_activity_task_status st "
//      + "where st.project_id=a.project_id and st.activity_status_id=4) order by project_id desc", nativeQuery = true)
  public List<PendingApplication> findAllUnSubmittedApplications(final String userId);
  
  @Query(value = "select distinct f.project_id as project_id,  f.facility_name as facility_name, "
      + "a.street1 as location_directions, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, p.received_date, "
      + "a.street1 as street1, a.street2 as street2, "
      + "pub.display_name as display_name, pub.public_id as applicant_id, f.dec_id, f.edb_district_id, "
      + " pub.first_name, pub.last_name, pub.middle_name, "
      + "'' as edb_public_id, '' as analyst_assigned_id, p.assigned_analyst_name , p.analyst_assigned_date, p.ea_ind, "
      + "p.rejected_ind, p.rejected_reason, to_char(p.rejected_date, 'mm/dd/yyyy') rejected_date "
      + "from {h-schema}e_facility f, {h-schema}e_facility_address a, {h-schema}e_project p, {h-schema}e_public pub  "
      + "where p.project_id=f.project_id and f.project_id=a.project_id and a.project_id=pub.project_id and "
      + "pub.public_id in (select r1.public_id from {h-schema}e_role r1 where r1.public_id=pub.public_id and r1.selected_in_etrack_ind=1 "
      + "order by pub.public_id asc fetch first rows only) "
      + "and p.project_id=?1 ", nativeQuery = true)
  public List<PendingApplication> findApplicationDetailForTheProjectId(final Long projectId);
  
  @Query(value="select distinct f.project_id as project_id, f.facility_name as facility_name,"
      + "p.display_name as display_name, p.first_name, p.last_name, p.middle_name, p.public_id as applicant_id, "
      + "a.street1 as location_directions, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, "
      + "a.street1 as street1, a.street2 as street2, prj.received_date received_date, "
      + "f.dec_id, f.edb_district_id, p.edb_public_id, prj.analyst_assigned_id, prj.assigned_analyst_name, "
      + "prj.analyst_assigned_date, prj.ea_ind, prj.rejected_ind, prj.rejected_reason, to_char(prj.rejected_date, 'mm/dd/yyyy') rejected_date  "
      + "from {h-schema}e_project prj, {h-schema}e_facility f, {h-schema}e_facility_address a, "
      + "{h-schema}e_facility_polygon fp, {h-schema}e_public p where prj.project_id=f.project_id "
      + "and prj.analyst_assigned_id=?1 and prj.upload_to_dart_ind=0 and (prj.dimsr_ind is null or prj.dimsr_ind=0) "
//      + "and prj.user_assigned is not null "
      + "and f.project_id=a.project_id and f.project_id=fp.project_id "
      + "and fp.project_id=p.project_id and p.public_id in (select min(pub.public_id) from {h-schema}e_public pub, {h-schema}e_role r1 "
      + "where pub.public_id=r1.public_id and (r1.role_type_id =1 or (r1.role_type_id=6 and r1.legally_responsible_type_code=1)) "
      + "and r1.selected_in_etrack_ind=1 and p.project_id=pub.project_id) and prj.original_submittal_date is not null "
      + "and exists (select st.project_id from {h-schema}e_project_activity_task_status st "
      + "where st.project_id=a.project_id and st.activity_status_id=5) order by create_date asc", nativeQuery = true)
  public List<PendingApplication> findAllValidationEligibleApplicationsByUserId(final String userId);
  
  
//  @Query(value="select distinct f.project_id as project_id, f.facility_name as facility_name,p.display_name as display_name, p.public_id as applicant_id, "
//      + "a.street1 as location_directions, a.city as city, "
//      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, prj.received_date received_date, "
//      + "a.street1 as street1, a.street2 as street2, "
//      + "f.dec_id, f.edb_district_id, p.edb_public_id, prj.analyst_assigned_id, prj.assigned_analyst_name, prj.analyst_assigned_date, prj.ea_ind "
//      + "from {h-schema}e_project prj, {h-schema}e_facility f, {h-schema}e_facility_address a, "
//      + "{h-schema}e_facility_polygon fp, {h-schema}e_public p where prj.project_id=f.project_id "
//      + "and prj.analyst_assigned_id=?1 and f.project_id=a.project_id and f.project_id=fp.project_id "
//      + "and fp.project_id=p.project_id and p.public_id in (select min(pub.public_id) from {h-schema}e_public pub, {h-schema}e_role r1 "
//      + "where pub.public_id=r1.public_id and (r1.role_type_id =1 or (r1.role_type_id=6 and r1.legally_responsible_type_code=1)) "
//      + "and pub.selected_in_etrack_ind=1 and p.project_id=pub.project_id) "
//      + "and exists (select st.project_id from {h-schema}e_project_activity_task_status st "
//      + "where st.project_id=a.project_id and st.activity_status_id=5) order by create_date asc", nativeQuery = true)
//  public List<PendingApplication> findAllValidationInprogressApplications(final String userId);

  @Query(value="select distinct f.project_id as project_id, f.facility_name as facility_name,"
      + "p.display_name as display_name, p.first_name, p.last_name, p.middle_name, p.public_id as applicant_id, "
      + "a.street1 as location_directions, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, prj.received_date received_date, "
      + "a.street1 as street1, a.street2 as street2, "
      + "f.dec_id, f.edb_district_id, p.edb_public_id, prj.analyst_assigned_id, "
      + "prj.assigned_analyst_name, prj.analyst_assigned_date, prj.ea_ind, prj.rejected_ind, "
      + "prj.rejected_reason, to_char(prj.rejected_date, 'mm/dd/yyyy') rejected_date  "
      + "from {h-schema}e_project prj, {h-schema}e_facility f, {h-schema}e_facility_address a, "
      + "{h-schema}e_facility_polygon fp, {h-schema}e_public p where prj.project_id=f.project_id "
      + "and f.project_id=a.project_id and f.project_id=fp.project_id "
      + "and fp.project_id=p.project_id and prj.upload_to_dart_ind=0 and (prj.dimsr_ind is null or prj.dimsr_ind=0) and p.public_id in "
      + "(select min(pub.public_id) from {h-schema}e_public pub, {h-schema}e_role r "
      + "where pub.public_id=r.public_id and (r.role_type_id =1 or (r.role_type_id=6 and r.legally_responsible_type_code=1)) "
      + "and r.selected_in_etrack_ind=1 and p.project_id=pub.project_id) and prj.original_submittal_date is not null and (prj.rejected_ind is null or prj.rejected_ind = 0) "
      + "and exists (select st.project_id from {h-schema}e_project_activity_task_status st "
      + "where st.project_id=a.project_id and st.activity_status_id=5) order by create_date asc", nativeQuery = true)
  public List<PendingApplication> findAllUnValidatedApplications();


  @Query(value="select distinct f.project_id as project_id, f.facility_name as facility_name,"
      + "p.display_name as display_name, p.first_name, p.last_name, p.middle_name, p.public_id as applicant_id, "
      + "a.street1 as location_directions, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, f.create_date as create_date, prj.received_date received_date, "
      + "a.street1 as street1, a.street2 as street2, "
      + "f.dec_id, f.edb_district_id, p.edb_public_id, prj.analyst_assigned_id, prj.assigned_analyst_name, "
      + "prj.analyst_assigned_date, prj.ea_ind, prj.rejected_ind, prj.rejected_reason, to_char(prj.rejected_date, 'mm/dd/yyyy') rejected_date "
      + "from {h-schema}e_project prj, {h-schema}e_facility f, {h-schema}e_facility_address a, "
      + "{h-schema}e_facility_polygon fp, {h-schema}e_public p, {h-schema}e_facility_polygon_region reg "
      + "where prj.project_id=f.project_id "
      + "and f.project_id=a.project_id and f.project_id=fp.project_id "
      + "and fp.facility_polygon_id=reg.facility_polygon_id and reg.dep_region_id=?1 "
      + "and fp.project_id=p.project_id and prj.upload_to_dart_ind=0 and (prj.dimsr_ind is null or prj.dimsr_ind=0) and p.public_id in "
      + "(select min(pub.public_id) from {h-schema}e_public pub, {h-schema}e_role r "
      + "where pub.public_id=r.public_id and (r.role_type_id =1 or (r.role_type_id=6 and r.legally_responsible_type_code=1))"
      + " and r.selected_in_etrack_ind=1 and p.project_id=pub.project_id) and prj.original_submittal_date is not null and (prj.rejected_ind is null or prj.rejected_ind = 0)  "
      + "and exists (select st.project_id from {h-schema}e_project_activity_task_status st "
      + "where st.project_id=a.project_id and st.activity_status_id=5) order by create_date asc", nativeQuery = true)
  public List<PendingApplication> findAllUnValidatedApplicationsByRegionId(final Integer regionId);
  
}
