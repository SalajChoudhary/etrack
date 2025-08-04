package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectProgramAppln;

@Repository
public interface ProjectProgramApplnRepo extends CrudRepository<ProjectProgramAppln, Long> {
  @Query(value="select * from {h-schema}e_program_application where project_id= ?1 and edb_program_application_identifier is null ", nativeQuery = true)
  List<ProjectProgramAppln> findByProjectId(Long projectId);
}
