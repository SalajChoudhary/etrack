package dec.ny.gov.etrack.fmis.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.fmis.entity.ProjectNote;

@Repository
public interface ProjectNoteRepo extends CrudRepository<ProjectNote, Long> {
}
