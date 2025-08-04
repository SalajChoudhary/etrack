package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ActionNoteEntity;

@Repository
public interface ActionNoteRepo extends CrudRepository<ActionNoteEntity, Long>{

  @Query(value="select n.project_note_id, n.project_id, n.action_type_code, n.action_date, n.action_note, "
      + "n.comments, n.created_by_id, n.cancel_user_id, n.cancel_user_nm, "
      + "n.create_date, n.modified_by_id, n.modified_date, c.action_type_desc "
      + "from {h-schema}e_project_note n, {h-schema}e_project_note_action_type c "
      + "where n.action_type_code=c.action_type_code and n.project_id=?1 and n.project_note_id=?2", nativeQuery = true)
  public ActionNoteEntity findActionNoteByNoteIdAndProjectId(final Long projectId, final Long projectNoteId);

  @Query(value="select n.project_note_id, n.project_id, n.action_type_code, n.action_date, n.action_note, "
      + "n.comments, n.created_by_id, n.cancel_user_id, n.cancel_user_nm, "
      + "n.create_date, n.modified_by_id, n.modified_date, c.action_type_desc "
      + "from {h-schema}e_project_note n, {h-schema}e_project_note_action_type c "
      + "where n.action_type_code=c.action_type_code and n.project_id=?1", nativeQuery = true)
  public List<ActionNoteEntity> findActionNoteByProjectId(Long projectId);
}
