package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import dec.ny.gov.etrack.dart.db.entity.FacilityBIN;

public interface FacilityBINRepo extends CrudRepository<FacilityBIN, Long> {
  public List<FacilityBIN> findByProjectId(final Long projectId);
}
