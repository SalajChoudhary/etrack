package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonRegion;

@Repository
public interface FacilityPolygonRegionRepo extends CrudRepository<FacilityPolygonRegion, Long> {
  @Query("SELECT fpr FROM FacilityPolygonRegion fpr WHERE fpr.facilityPolygonId= :facilityPolygonId")
  public List<FacilityPolygonRegion> findAllRegionsByFacilityPolygonId(Long facilityPolygonId);
}
