package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;

@Repository
public interface ProjectAlertRepo extends CrudRepository<ProjectAlert, Long> {
  
  ProjectAlert findByProjectAlertIdAndProjectId(Long alertId, Long projectId);

  List<ProjectAlert> findByProjectIdAndProjectNoteId(Long projectId, Long projectNoteId);
}
