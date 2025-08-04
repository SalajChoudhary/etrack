package dec.ny.gov.etrack.permit.repo;

import java.util.Date;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Facility;

@Repository
public interface FacilityRepo extends CrudRepository<Facility, Long> {
  Facility findFacilityByEdbDistrictId(Long edbDistrictId);
  Facility findByProjectId(Long projectId);
  
  @Modifying
  @Query(value="update {h-schema}e_facility set facility_name=?2, modified_by_id=?3, modified_date=?4 where project_id=?1", nativeQuery=true)
  void updateFacilityDetails (Long projectId, String facilityName, final String userId, Date date);
}
