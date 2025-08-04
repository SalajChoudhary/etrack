package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;

@Repository
public interface ProjectActivityRepo extends CrudRepository<ProjectActivity, Long> {
  
  public List<ProjectActivity> findAllByProjectId(Long projectId);
  
  @Query("select p from ProjectActivity p where p.projectId= :projectId and p.activityStatusId <= :statusId order by p.activityStatusId asc")
  public List<ProjectActivity> findAllByProjectIdAndActivityStatusIdLTOREQ(Long projectId, Integer statusId);
  
  public List<ProjectActivity> findAllByProjectIdAndActivityStatusId(Long projectId, Integer statusId);
  
  @Query("select p from ProjectActivity p where p.projectId= :projectId and p.activityStatusId > :statusId order by p.activityStatusId asc")
  public List<ProjectActivity> findAllByProjectIdAndActivityStatusIdGT(Long projectId, Integer statusId);
  
  @Query("select p from ProjectActivity p where p.projectId= :projectId "
      + "and p.activityStatusId in (:activityStatusIds) and completionDate is not null  order by p.activityStatusId asc")
  public List<ProjectActivity> findProjectActivitiesByIds(List<Integer> activityStatusIds, Long projectId);

  @Modifying
  @Query(value="delete ProjectActivity where projectId= :projectId and activityStatusId > 5")
  public void deleteAllValidationRelatedActivities(Long projectId);
}
