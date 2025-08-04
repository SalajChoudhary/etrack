package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ProjectNote;

@Repository
public interface ProjectNoteRepo extends CrudRepository<ProjectNote, Long> {
  List<ProjectNote> findAllByProjectId(Long projectId);
  List<ProjectNote> findAllByProjectNoteIdAndProjectId(Long projectNoteId, Long projectId);
  @Query(value="select dt.document_title from {h-schema}e_document_sub_type_title ds, {h-schema}e_document_title dt "
      + "where ds.document_title_id=dt.document_title_id and document_sub_type_title_id in (?1)", nativeQuery=true)
  List<String> findAllDocumentTitleByIds(Set<Integer> documentTitleIds);
  
  @Query(value="select action_type_code, action_type_desc from {h-schema}e_project_note_action_type", nativeQuery=true)
  List<String> findAllProjectNoteActionType();

}
