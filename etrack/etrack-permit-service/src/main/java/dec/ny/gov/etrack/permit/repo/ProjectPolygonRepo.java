package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectPolygon;

@Repository
public interface ProjectPolygonRepo extends CrudRepository<ProjectPolygon, Long> {

  @Query(value="select prj.project_id, pol.edb_district_id, prj.approved_polygon_change_ind, fac.polygon_gis_id  "
      + "from {h-schema}e_polygon_for_upload pol, {h-schema}e_project prj, "
      + "{h-schema}e_facility_polygon fac  where prj.project_id=pol.project_id and prj.project_id=fac.project_id "
      + "and fac.polygon_type_code=4 and (retry_counter is null or retry_counter <= 10) order by prj.project_id desc", nativeQuery = true)
  List<ProjectPolygon> findPolygonUploadEligibleProjects();
}
