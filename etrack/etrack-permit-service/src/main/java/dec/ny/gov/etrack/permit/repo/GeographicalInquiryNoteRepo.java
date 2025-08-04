package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.GeographicalInquiryNote;

@Repository
public interface GeographicalInquiryNoteRepo extends CrudRepository<GeographicalInquiryNote, Long> {

}
