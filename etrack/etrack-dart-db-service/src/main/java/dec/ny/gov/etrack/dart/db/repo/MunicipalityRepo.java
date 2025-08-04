package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.Municipality;

@Repository
public interface MunicipalityRepo extends CrudRepository<Municipality, Long> {
  @Query(value="select fpm.facility_polygon_municip_id municipality_id, fpm.municipality municipality_name, "
      + "fp.project_id from {h-schema}e_facility_polygon fp, {h-schema}e_facility_polygon_municipality fpm "
      + "where fp.facility_polygon_id=fpm.facility_polygon_id and fp.project_id in (?1)", nativeQuery = true)
  List<Municipality> findMunicipalitiesForProjectIds(final Set<Long> projectIds);
}
