package dec.ny.gov.etrack.permit.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityBIN;

@Repository
public interface FacilityBINRepo extends CrudRepository<FacilityBIN, Long>{
  public List<FacilityBIN> findByProjectId(final Long projectId);

  @Modifying
  @Query("update FacilityBIN f set f.deletedInd= :deleteInd, modifiedById= :userId, modifiedDate= :modifiedDate where f.facilityBinId= :facilityBinId")
  public void updateDeleteInd(Long facilityBinId, final int deleteInd, String userId, Date modifiedDate);
  
  @Modifying
  @Query("update FacilityBIN f set f.deletedInd= :deleteInd, modifiedById= :userId, modifiedDate= :modifiedDate where f.edbBin= :edbBin")
  public void updateEdbBinAndDeleteInd(String edbBin, final int deleteInd, String userId, Date modifiedDate);

}
