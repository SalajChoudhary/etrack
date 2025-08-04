package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectSpecialAttention;

@Repository
public interface ProjectSpecialAttentionRepo extends CrudRepository<ProjectSpecialAttention, Long> {
  List<ProjectSpecialAttention> findByProjectId(Long projectId);
}