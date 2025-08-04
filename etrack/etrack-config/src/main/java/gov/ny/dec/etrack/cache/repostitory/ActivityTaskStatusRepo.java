package gov.ny.dec.etrack.cache.repostitory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.ActivityTaskStatus;

@Repository
public interface ActivityTaskStatusRepo extends CrudRepository<ActivityTaskStatus, Integer> {
 
}
