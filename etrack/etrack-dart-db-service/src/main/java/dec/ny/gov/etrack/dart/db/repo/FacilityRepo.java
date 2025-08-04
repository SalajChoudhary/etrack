package dec.ny.gov.etrack.dart.db.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.Facility;

@Repository
public interface FacilityRepo extends CrudRepository<Facility, Long> {
  public Facility findByProjectId(Long projectId);
}
