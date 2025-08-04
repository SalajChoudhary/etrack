package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.DartSuspensionMilestone;

@Repository
public interface DartSuspendedMilestoneRepo extends CrudRepository<DartSuspensionMilestone, Long> {
    List<DartSuspensionMilestone> findAllSuspendedMilestoneByProjectIdOrderByBatchIdAsc(final Long projectId);
}
