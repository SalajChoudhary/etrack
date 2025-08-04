package dec.ny.gov.etrack.dcs.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.entity.ProjectNote;

@Repository
public interface ProjectNoteRepo extends CrudRepository<ProjectNote, Long> {
  
  List<ProjectNote> findAllByProjectId(Long projectId);
  
  @Query(value="select project_id from {h-schema}e_project_disposed", nativeQuery = true)
  List<Long> findAllDisposedProjectIds();
  
  @Transactional
  @Modifying
  @Query(value="delete from {h-schema}e_project_disposed where project_id=?1", nativeQuery = true)
  int deleteProcessedDisposedProjectByProjectId(final Long projectId);
}
