package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.ProposedUseCode;

@Repository
public interface ProposedUseCodeRepo extends CrudRepository<ProposedUseCode, String> {
  public List<ProposedUseCode> findByActiveInd(Integer indicator);
}
