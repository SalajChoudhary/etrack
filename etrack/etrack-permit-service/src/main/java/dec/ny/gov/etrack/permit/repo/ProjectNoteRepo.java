package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectNote;

@Repository
public interface ProjectNoteRepo extends CrudRepository<ProjectNote, Long> {
  List<ProjectNote> findAllByProjectId(Long projectId);
  List<ProjectNote> findAllByProjectNoteIdAndProjectId(Long projectNoteId, Long projectId);
  List<ProjectNote> findByProjectIdAndActionTypeCode(Long projectId, Integer i);
  @Query(value="select dt.document_title from {h-schema}e_document_sub_type_title ds, {h-schema}e_document_title dt "
      + "where ds.document_title_id=dt.document_title_id and document_sub_type_title_id in (?1)", nativeQuery=true)
  List<String> findAllDocumentTitleByIds(Set<Integer> documentTitleIds);
}
