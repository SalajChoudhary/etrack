package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectDevelopment;

@Repository
public interface ProjectDevelopRepo extends CrudRepository<ProjectDevelopment, Long>{
  public List<ProjectDevelopment> findByProjectId(Long projectId);
}
