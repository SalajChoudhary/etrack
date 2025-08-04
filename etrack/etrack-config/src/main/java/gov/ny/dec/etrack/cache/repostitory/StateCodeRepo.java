package gov.ny.dec.etrack.cache.repostitory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.StateCode;

@Repository
public interface StateCodeRepo extends CrudRepository<StateCode, String> {
}
