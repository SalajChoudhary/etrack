package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;

@Repository
public interface DartMilestoneRepo extends CrudRepository<DartMilestone, Long> {
    List<DartMilestone> findAllMilestoneByProjectIdOrderByBatchIdAsc(final Long projectId);
}
