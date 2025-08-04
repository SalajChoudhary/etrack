package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.County;

@Repository
public interface CountyRepo extends CrudRepository<County, Long> {
  @Query(value="select fpc.facility_polygon_county_id, fpc.county, fp.project_id "
      + "from {h-schema}e_facility_polygon_county fpc, {h-schema}e_facility_polygon fp "
      + "where fp.facility_polygon_id=fpc.facility_polygon_id and fp.project_id in (?1)", nativeQuery = true)
  List<County> findCountiesForProjectIds(final Set<Long> projectIds);
}
