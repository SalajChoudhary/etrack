package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectResidential;

@Repository
public interface ProjectResidentialRepo extends CrudRepository<ProjectResidential, Long> {
  public List<ProjectResidential> findByProjectId(Long projectId);
}
