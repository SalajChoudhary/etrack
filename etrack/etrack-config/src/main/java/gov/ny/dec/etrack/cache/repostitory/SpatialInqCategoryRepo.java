package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SpatialInqCategory;

@Repository
public interface SpatialInqCategoryRepo extends CrudRepository<SpatialInqCategory, Integer> {
  List<SpatialInqCategory> findByActiveInd(int i);
}
