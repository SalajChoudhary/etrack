package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.LitigationHold;

@Repository
public interface LitigationHoldRequestRepo extends CrudRepository<LitigationHold, Long> {
  LitigationHold findByProjectId(Long projectId);
}
