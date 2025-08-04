package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.PublicType;

@Repository
public interface PublicTypeRepo extends CrudRepository<PublicType, String> {
  List<PublicType> findByActiveInd(Integer indicator);
}
