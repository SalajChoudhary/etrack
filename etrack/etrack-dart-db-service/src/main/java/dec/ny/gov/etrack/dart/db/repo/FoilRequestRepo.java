package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ProjectFoilStatusDetail;

@Repository
public interface FoilRequestRepo extends CrudRepository<ProjectFoilStatusDetail, Long> {
  List<ProjectFoilStatusDetail> findByProjectId(final Long projectId);
}
