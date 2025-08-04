package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.PermitTypeCodeEntity;

@Repository
public interface PermitTypeCodeRepo  extends JpaRepository<PermitTypeCodeEntity, String>{

  @Query(value="select permit_type_code from {h-schema}e_permit_type_code "
      + "where effective_start_date <= sysdate and effective_end_date <=sysdate and renewed_ind=0 "
      + "and permit_type_code not like 'GP-%' order by permit_type_code", nativeQuery = true)
  List<String> findAllConstructionPermits();
}
