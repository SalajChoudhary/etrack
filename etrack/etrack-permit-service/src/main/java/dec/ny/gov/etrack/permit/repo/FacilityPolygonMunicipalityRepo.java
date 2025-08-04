package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityPolygonMunicipality;

@Repository
public interface FacilityPolygonMunicipalityRepo extends CrudRepository<FacilityPolygonMunicipality, Long> {
  @Query("SELECT m FROM FacilityPolygonMunicipality m WHERE facilityPolygonId= :facilityPolygonId")
  List<FacilityPolygonMunicipality> findAllMunicipalitiesByFacilityPolygonId(final Long facilityPolygonId);
}
