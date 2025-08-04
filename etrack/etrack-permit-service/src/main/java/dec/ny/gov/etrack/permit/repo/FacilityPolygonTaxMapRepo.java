package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonTaxMap;

@Repository
public interface FacilityPolygonTaxMapRepo extends CrudRepository<FacilityPolygonTaxMap, Long> {
  @Query("SELECT taxmapNumber FROM FacilityPolygonTaxMap WHERE facilityPolygonId= :facilityPolygonId")
  String findTaxmapNumberByFacilityPolygonId(final Long facilityPolygonId);
}
