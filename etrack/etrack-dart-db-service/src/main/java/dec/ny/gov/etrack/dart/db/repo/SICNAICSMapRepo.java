package dec.ny.gov.etrack.dart.db.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SICNAICSMap;

@Repository
public interface SICNAICSMapRepo extends CrudRepository<SICNAICSMap, Long> {
}
