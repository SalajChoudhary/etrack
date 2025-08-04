package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityAddr;

@Repository
public interface FacilityAddrRepo extends CrudRepository<FacilityAddr, Long> {
  FacilityAddr findByProjectId(Long projectId);
  
  @Modifying
  @Query(value="update {h-schema}e_facility_address set location_directions=?2, street1=?3, street2=?4, city=?5, zip=?6 where project_id=?1", nativeQuery=true)
  void updateFacilityAddressDetails(Long projectId, String locDirections, String stree1, String street2, String city, String zip);
}
