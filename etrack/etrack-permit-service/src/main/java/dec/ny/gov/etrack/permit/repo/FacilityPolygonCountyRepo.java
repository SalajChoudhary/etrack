package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonCounty;

@Repository
public interface FacilityPolygonCountyRepo extends CrudRepository<FacilityPolygonCounty, Long> {
  
  @Query("SELECT c FROM FacilityPolygonCounty c WHERE facilityPolygonId= :facilityPolygonId")
  List<FacilityPolygonCounty> findAllCountiesByFacilityPolygonId(final Long facilityPolygonId);

}
