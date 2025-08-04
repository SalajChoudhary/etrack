package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectSWFacilityType;

@Repository
public interface ProjectSWFacilityTypeRepo extends CrudRepository<ProjectSWFacilityType, Long> {
  List<ProjectSWFacilityType> findAllByProjectId(Long projectId);
}
