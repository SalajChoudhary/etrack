package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ContactAgent;

@Repository
public interface ContactRepo extends CrudRepository<ContactAgent, Long> {

//  @Query(value = "select p.public_id as public_id, p.public_type_code as public_type_code, p.display_name as display_name,"
//      + "p.edb_public_id as edb_public_id, f.edb_district_id as edb_district_id"
//      + " from {h-schema}e_public p, {h-schema}e_facility f"
//      + " where p.project_id=f.project_id and p.project_id = ?1 and p.selected_in_etrack_ind = ?2", nativeQuery = true)
//  List<ContactAgent> findAllContactAgentsByProjectId(final Long projectId, final Integer selectedInEtrackInd);
//  
  @Query(
      value = "select distinct p.public_id as public_id, p.public_type_code as public_type_code, p.display_name as display_name,"
          + "p.edb_public_id as edb_public_id, r.role_id from {h-schema}e_public p, {h-schema}e_role r "
          + "where p.public_id=r.public_id and p.project_id = ?1 and r.selected_in_etrack_ind = ?2 and r.role_type_id in (2,3,4,5)",
      nativeQuery = true)
  List<ContactAgent> findAllContactsByAssociatedInd(final Long projectId,
      final Integer selectedInEtrackInd);
}
