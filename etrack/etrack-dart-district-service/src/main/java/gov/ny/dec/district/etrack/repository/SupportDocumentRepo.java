package gov.ny.dec.district.etrack.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.district.etrack.entity.SupportDocument;

@Repository
public interface SupportDocumentRepo extends CrudRepository<SupportDocument, Long> {
  @Query("select sd from SupportDocument sd where sd.documentStateCode='A' and sd.projectId IN (:projectIds)")
  List<SupportDocument> findAllByProjectIds(Set<Long> projectIds);
//  @Query("select sd from SupportDocument sd where sd.documentStateCode='A' and sd.projectId= :projectId")
//  List<SupportDocument> findAllByProjectIds(Long projectId);
  @Query(value="select p.project_id from etrackowner.e_facility f, etrackowner.e_project p "
      + "where p.project_id=f.project_id and p.upload_to_dart_ind=1 and edb_district_id=?1", nativeQuery=true)
  Set<Long> findAllProjectsByDistrictId(long districtId);

  @Query(value="select distinct project_id from etrackowner.e_project_litigation_hold where "
      + "litigation_hold_ind=1 and litigation_hold_start_date <= sysdate "
      + "and (litigation_hold_end_date is null or litigation_hold_end_date >= sysdate) and project_id in (?1)", nativeQuery=true)
  Set<Long> findAllLitigationHoldEligibleProjectsByProjectIds(Set<Long> projectIds);
}
