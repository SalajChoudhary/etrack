package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectProgramDistrict;

@Repository
public interface ProjectProgramDistrictRepo extends CrudRepository<ProjectProgramDistrict, Long> {
  @Query(value="select * from {h-schema}e_program_district_identifier where project_id = ?1 "
      + "and edb_program_district_identifier is null", nativeQuery = true)
  List<ProjectProgramDistrict> findByProjectId(Long projectId);
}