package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectSICNAICSCode;

@Repository
public interface ProjectSICNAICSCodeRepo extends CrudRepository<ProjectSICNAICSCode, Long> {
  List<ProjectSICNAICSCode> findByProjectId(Long projectId);
}
