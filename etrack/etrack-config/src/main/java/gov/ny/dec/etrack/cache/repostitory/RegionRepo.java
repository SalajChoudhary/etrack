package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.Region;

@Repository
public interface RegionRepo extends CrudRepository<Region, Integer> {
  @Query("select r.depRegionId from Region r where activeInd= :activeInd")
  List<String> findAllRegionByActiveInd(final Integer activeInd);
}
