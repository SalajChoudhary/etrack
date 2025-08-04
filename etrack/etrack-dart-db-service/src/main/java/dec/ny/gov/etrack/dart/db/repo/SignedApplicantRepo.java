package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SignedApplicant;

@Repository
public interface SignedApplicantRepo extends CrudRepository<SignedApplicant, String> {
  @Query(value="select CONCAT(CONCAT(p.public_id,','), r.role_id) as public_id, p.public_type_code, "
      + "p.display_name, p.edb_public_id, p.public_signed_ind, p.project_id, decode (rt.role_type_desc, "
      + "'LRP', 'Applicant',"
      + "'Owner', 'Owner',"
      + "'Application Contact', 'Contact/Agent'"
      + ") as role, r.legally_responsible_type_code "
      + "from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_role_type rt "
      + "where p.public_id=r.public_id and r.role_type_id=rt.role_type_id and r.selected_in_etrack_ind=1 "
      + "and r.role_type_id in (1,6,5) and p.project_id=?1 order by p.public_id desc", nativeQuery = true)
  List<SignedApplicant> findOwnerAndApplicantDetails(final Long projectId);
}
