package dec.ny.gov.etrack.dcs.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.EtrackDartFacility;

@Repository
public interface ETrackDartFacilityDAO extends CrudRepository<EtrackDartFacility, Long> {
  /**
   * Retrieve the DART Facility details for the input district id.
   * 
   * @param districtId - Enterprise district id.
   * 
   * @return - Facility details.
   */
  EtrackDartFacility findByDistrictId(Long districtId);
}
