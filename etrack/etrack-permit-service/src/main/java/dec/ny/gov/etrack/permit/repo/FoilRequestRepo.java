package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectFoilStatusDetail;

@Repository
public interface FoilRequestRepo extends CrudRepository<ProjectFoilStatusDetail, Long> {
  List<ProjectFoilStatusDetail> findByProjectId(final Long projectId);
}
