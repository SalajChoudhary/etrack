package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ProjectAlert;

@Repository
public interface ProjectAlertRepo extends CrudRepository<ProjectAlert, Long> {
  
//  @Query(value="select a.project_alert_id, a.project_id, a.alert_date, a.alert_note, a.created_by_id, "
//      + "a.create_date, f.facility_name, a.msg_read_ind as read_ind "
//      + "from {h-schema}e_project_alert a, {h-schema}e_facility f where f.project_id=a.project_id", nativeQuery = true)
//  List<ProjectAlert> findAllAlerts();
  
  @Query(value="select a.project_alert_id, a.project_id, a.alert_date, a.alert_note, "
      + "a.created_by_id, a.create_date, f.facility_name, a.msg_read_ind as read_ind, n.comments "
      + "from {h-schema}e_project_alert a, {h-schema}e_project_note n, {h-schema}e_facility f "
      + "where a.project_note_id=n.project_note_id and f.project_id=a.project_id and a.alert_rcvd_user_id=?1 ", nativeQuery = true)
  List<ProjectAlert> findAllAlertsByUserId(final String userId);
  
//  @Query(value="select a.project_alert_id, a.project_id, a.alert_date, a.alert_note, pr.dep_region_id, "
//      + "a.create_date, a.created_by_id, a.msg_read_ind as read_ind, f.facility_name "
//      + "from {h-schema}e_project_alert a, {h-schema}e_facility f, {h-schema}e_facility_polygon p, {h-schema}e_facility_polygon_region pr "
//      + "where a.project_id=p.project_id and a.project_id=f.project_id "
//      + "and p.facility_polygon_id=pr.facility_polygon_id and pr.dep_region_id=?1 order by a.create_date desc ", nativeQuery = true)
//  List<ProjectAlert> findAllAlertsByRegionId(final Long regionId);
  
  @Query(value="select a.project_alert_id, a.project_id, a.alert_date, a.alert_note, a.created_by_id, "
      + "a.create_date, f.facility_name, a.msg_read_ind as read_ind, n.comments "
      + "from {h-schema}e_project_alert a, {h-schema}e_project_note n, {h-schema}e_facility f "
      + "where a.project_note_id=n.project_note_id and f.project_id=a.project_id "
      + "and a.project_id=?2 and a.alert_rcvd_user_id=?1", nativeQuery = true)
  List<ProjectAlert> findAllAlertsByUserIdAndProjectId(final String userId, final Long projectId);

  @Transactional
  @Modifying
  @Query(value="delete {h-schema}e_project_alert where project_id=?1 and project_alert_id=?2", nativeQuery = true)
  void deleteByAlertId(Long projectId, Long projectAlertId);
}
