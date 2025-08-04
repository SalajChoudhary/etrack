package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DevelopmentType;

@Repository
public interface DevelopmentTypeRepo extends CrudRepository<DevelopmentType, Integer> {
  public List<DevelopmentType> findByActiveInd(Integer activeInd);
}
