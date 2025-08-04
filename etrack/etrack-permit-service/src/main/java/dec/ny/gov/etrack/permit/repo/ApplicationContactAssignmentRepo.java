package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ApplicationContactAssignment;

@Repository
public interface ApplicationContactAssignmentRepo extends CrudRepository<ApplicationContactAssignment, Long> {

  @Modifying
  @Query(value="delete {h-schema}e_appln_contact_assign where application_id in (?1)", nativeQuery=true)
  void deleteAllByApplicationIds(List<Long> applicationIds);
}
