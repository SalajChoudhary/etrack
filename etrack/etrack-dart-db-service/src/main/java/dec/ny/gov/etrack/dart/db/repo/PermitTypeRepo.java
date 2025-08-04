package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.PermitType;

@Repository
public interface PermitTypeRepo extends CrudRepository<PermitType, String> {

  @Query(value="select p.permit_type_code, p.permit_type_desc, p.permit_category_id, p.effective_start_date, p.effective_end_date "
      + "from {h-schema}e_permit_type_code p where permit_type_code in (?1)", nativeQuery=true)
  List<PermitType> findPermitTypeDetails(final Set<String> permitTypes);
  
//  @Query(value="select pt.permit_type_code, pt.permit_type_desc, pt.permit_category_id, "
//      + "pt.effective_start_date, pt.effective_end_date from {h-schema}e_permit_type_code pt where pt.general_permit_ind=0 and "
//      + "pt.permit_type_code not in(select permit_type_code from {h-schema}e_application where project_id=?1)"
//      + "and pt.permit_type_code not in(select gp.related_permit_type_code from {h-schema}e_application a, {h-schema}e_gp_related_permit gp "
//      + "where a.permit_type_code=gp.gp_permit_type_code and project_id=?1) order by pt.permit_type_code asc", nativeQuery = true)
//  List<PermitType> findEligiblePermitTypesToAddAdditioanl(final Long projectId);

  @Query(value="select pt.permit_type_code, pt.permit_type_desc, pt.permit_category_id, "
      + "pt.effective_start_date, pt.effective_end_date from {h-schema}e_permit_type_code pt where pt.general_permit_ind=0 and "
      + "pt.permit_type_code not in (?1) and (pt.effective_start_date is null or pt.effective_start_date <= sysdate) "
      + "and (pt.effective_end_date is null or pt.effective_end_date >= sysdate) "
      + "and pt.permit_type_code not in (?1) order by pt.permit_type_code asc", nativeQuery = true)
  List<PermitType> findEligiblePermitTypesToAddAdditional(Set<String> appliedPermits);
}
