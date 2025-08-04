package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SICCodes;

@Repository
public interface SICCodeRepo extends CrudRepository<SICCodes, String>{
  @Cacheable(value = "sicCodesCache")
  @Query("SELECT s FROM SICCodes s where s.activeInd=1 and SUBSTRING(s.sicCode, 4) <> 0")
  public List<SICCodes> findAllSICCodes();
}
