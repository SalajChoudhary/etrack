package dec.ny.gov.etrack.permit.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Role;

@Repository
public interface RoleRepo extends CrudRepository<Role, Long> {

  @Modifying
  @Query(
      value = "INSERT INTO {h-schema}E_ROLE(ROLE_ID, PUBLIC_ID, ROLE_TYPE_ID, EMPLOYEE_REGION_CODE, "
          + "LEGALLY_RESPONSIBLE_TYPE_CODE, ADDRESS_ID, EDB_ROLE_ID, CREATED_BY_ID, CREATE_DATE, PRIMARY_LRP_IND, CHANGE_CTR, SELECTED_IN_ETRACK_IND) "
          + "VALUES ({h-schema}E_ROLE_S.NEXTVAL, :publicId, :roleTypeId, to_number(:emplRegionCode), "
          + "to_number(:legallyResponseTypeCode), :addressId, to_number(:edbRoleId), :userId, :createDate, "
          + "to_number(:primaryLRPInd), :changeCounter, to_number(:selectedInEtrackInd))",nativeQuery = true)
  public void addRole(final Long publicId, final Integer roleTypeId, final String emplRegionCode,
      final Integer legallyResponseTypeCode, final Long addressId, final Long edbRoleId,
      final String userId, final Date createDate, final Integer primaryLRPInd,
      final Integer changeCounter, final Integer selectedInEtrackInd);

  @Modifying
  @Query(value= "delete {h-schema}e_role where role_id=?1", nativeQuery=true)
  public void deleteRoleById(final Long roleId);
  
  @Query(value="select * from {h-schema}e_role where public_id=?1 and role_type_id in (?2) order by role_type_id desc", nativeQuery = true)
  public List<Role> findByRoleTypeId(final Long applicantId, List<Integer> roleTypeIds);
  
  // @Query(
  // value = "INSERT INTO {h-schema}E_ROLE(ROLE_ID, PUBLIC_ID, ROLE_TYPE_ID, EMPLOYEE_REGION_CODE, "
  // + "LEGALLY_RESPONSIBLE_TYPE_CODE, ADDRESS_ID, EDB_ROLE_ID, CREATED_BY_ID, CREATE_DATE,
  // PRIMARY_LRP_IND, CHANGE_CTR) "
  // + "VALUES ({h-schema}E_ROLE_S.NEXTVAL, :publicId, :#{#r.roleTypeId}, :#{#r.employeeRegionCode},
  // "
  // + ":#{#r.legallyResponsibleTypeCode}, :#{#r.addressId}, :#{#r.edbRoleId}, :#{#r.createdById}, "
  // + ":#{#r.createDate}, :#{#r.primaryLrpInd}, :#{#r.changeCtr})",
  // nativeQuery = true)
  // public void addRole(final Long publicId, @Param("r") Role role);
}
