package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.DartInCompleteMilestone;

@Repository
public interface DartInCompleteMilestoneRepo extends CrudRepository<DartInCompleteMilestone, Long> {
    List<DartInCompleteMilestone> findAllInCompleteMilestoneByProjectIdOrderByBatchIdAsc(final Long projectId);
}
