package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.PermitType;

@Repository
public interface PermitTypeRepo extends CrudRepository<PermitType, String> {
  
//  @Query(value = "select p.permit_category_id as permit_category_id, c.permit_category_desc as permit_category_desc, "
//      + "p.permit_type_desc as permit_type_desc, p.permit_type_code as permit_type_code, p.general_permit_ind as general_permit_ind, "
//      + "p.edb_code as edb_code from {h-schema}e_permit_category c, "
//      + "{h-schema}e_permit_type_code p where c.permit_category_id=p.permit_category_id "
//      + "and c.active_ind=1 and p.active_ind=1 order by p.permit_category_id asc", nativeQuery = true)
//  LinkedList<PermitType> findAllPermitByPermitCategoryInd();
  
  @Query(value="select permit_type_code, permit_type_desc from {h-schema}e_permit_type_code p where p.permit_type_code  in ("
      + "select related_permit_type_code from {h-schema}e_gp_related_permit where gp_permit_type_code=?1)", nativeQuery = true)
  List<String> findRelatedRegularPermitsForTheGeneralPermit(final String generalPermit);

  @Query(value="select permit_type_code from {h-schema}e_permit_type_code "
      + "where (effective_start_date is null or effective_start_date <= sysdate) "
      + "and (effective_end_date is null or effective_end_date >=sysdate) and renewed_ind=0 "
      + "and permit_type_code not like 'GP-%' order by permit_type_code", nativeQuery = true)
  List<String> findAllConstructionPermits();

  @Query(value="select permit_type_code from {h-schema}e_permit_type_code "
      + "where (effective_start_date is null or effective_start_date <= sysdate) "
      + "and (effective_end_date is null or effective_end_date >=sysdate) and renewed_ind=1 "
      + "and permit_type_code not like 'GP-%' order by permit_type_code", nativeQuery = true)
  List<String> findAllOperatingPermits();

  @Query(value="select permit_type_code from {h-schema}e_permit_type_code "
      + "where (effective_start_date is null or effective_start_date <= sysdate) "
      + "and (effective_end_date is null or effective_end_date >=sysdate) "
      + "and permit_type_code like 'GP-%' order by permit_type_code", nativeQuery = true)
  List<String> findAllGeneralPermits();

}