package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.ActionType;

@Repository
public interface ActionTypeRepo extends CrudRepository<ActionType, Integer> {
  List<ActionType> findBySystemNoteInd(Integer systemNoteInd);
}
