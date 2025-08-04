package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.ResidentialDevelopType;

@Repository
public interface ResidentialDevelopTypeRepo extends CrudRepository<ResidentialDevelopType, Integer> {
  public List<ResidentialDevelopType> findAllByActiveInd(Integer indicator);
}
