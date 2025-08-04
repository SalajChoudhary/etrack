package dec.ny.gov.etrack.dart.db.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.model.UserAssignment;

@Repository
public interface UserAssignmentRepo extends CrudRepository<UserAssignment, Long> {

  @Query(value="select p.project_id, p.analyst_assigned_id user_assigned, p.assigned_analyst_name as analyst_name, p.assigned_analyst_role_id as analyst_role_id, "
      + "n.comments from {h-schema}e_project_note n, {h-schema}e_project p where n.project_id=p.project_id "
      + "and n.action_type_code=12 and p.project_id=?1 order by n.project_note_id desc fetch first 1 rows only", nativeQuery = true)
  UserAssignment findUserAssignment(final Long projectId);
}
