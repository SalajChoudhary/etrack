package dec.ny.gov.etrack.dart.db.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ProjectActivity;

@Repository
public interface ProjectActivityRepo extends CrudRepository<ProjectActivity, Long>{

  @Query(value="select project_activity_status_id from {h-schema}e_project_activity_task_status "
      + "where project_id=?1 and activity_status_id=?2 and completion_date is not null", nativeQuery=true)
  public List<Integer> findProjectActivityStatusId(final Long projectId, final Integer activityStatusId);

  @Query(value="select * from {h-schema}e_project_activity_task_status  "
      + "where project_id=?1 and activity_status_id=?2", nativeQuery=true)
  public List<ProjectActivity> findProjectActivityStatusByActivityStatusId(final Long projectId, final Integer activityStatusId);

  @Modifying
  @Query(value = "update {h-schema}e_project_activity_task_status set activity_status_id=?4, "
      + "modified_by_id=?1, modified_date=sysdate where project_id=?2 and project_activity_status_id=?3", nativeQuery=true)
  public void updateProjectActivityStatusIdAsIncomplete(String userId, Long projectId,
      Integer projectActivityStatusId, int activityStatusId);

  @Modifying
  @Query(value = "update {h-schema}e_project_activity_task_status set activity_status_id=?4, completion_date=?5, "
      + "modified_by_id=?1, modified_date=sysdate where project_id=?2 and project_activity_status_id=?3", nativeQuery=true)
  public void updateProjectActivityStatusId(String userId, Long projectId,
      Integer projectActivityStatusId, int activityStatusId, Date completionDate);

  @Query(value="select s.project_activity_status_id from {h-schema}e_project_activity_task_status s, {h-schema}e_project p  "
      + "where s.project_id=p.project_id and p.original_submittal_ind=1 and p.project_id=?1 "
      + "and s.activity_status_id=?2 and s.completion_date is not null", nativeQuery = true)
  public List<Integer> findProjectSignedAndSubmitted(final Long projectId, final Integer activityStatusId);
}
