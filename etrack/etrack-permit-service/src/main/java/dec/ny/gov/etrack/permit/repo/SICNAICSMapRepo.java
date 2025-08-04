package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.SICNAICSMap;

@Repository
public interface SICNAICSMapRepo extends CrudRepository<SICNAICSMap, Long> {
}
