package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.Public;

@Repository
public interface DartDbRepo extends CrudRepository<Public, Long> {
  
  @Query("select p from Public p where p.projectId= :projectId and (p.publicId= :publicId or p.edbPublicId= :publicId)")
  public List<Public> findAllPublicsAssociatedProject(Long projectId, Long publicId);
  
  @Query(value="select distinct p.* from {h-schema}e_public p, {h-schema}e_role r where p.public_id=r.public_id "
      + "and r.selected_in_etrack_ind=?2 and p.project_id=?1", nativeQuery = true)
  public List<Public> findAllApplicantsByProjectIdAndSelectedInEtrackInd(Long projectId,
      Integer selectedETrackInd);
  
  @Transactional
  @Modifying
  @Query("update Public p set p.publicTypeCode= :publicTypeCode where p.publicId= :publicId")
  public void updatePublicTypeCode(String publicTypeCode, Long publicId);
  
//  @Query(value = "select f.project_id, f.dec_id, (substr(f.dec_id,1,1)||'-'||substr(f.dec_id,2,4)||'-'||substr(f.dec_id,6)) dec_id_formatted, f.facility_name, "
//      + "a.location_directions, a.state, a.zip, a.zip_extension, a.street1, a.street2, a.city, a.country, '' as change_counter, a.phone_number "
//      + "from {h-schema}e_facility f, {h-schema}e_facility_address a where f.project_id=a.project_id and f.project_id=?1", nativeQuery = true)
//  public List<FacilityDetail> findAllFacilityDetail(Long projectId);
  
}
