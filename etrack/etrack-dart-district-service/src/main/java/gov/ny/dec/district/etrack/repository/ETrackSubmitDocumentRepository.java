package gov.ny.dec.district.etrack.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import gov.ny.dec.district.etrack.entity.SubmitDocument;

@Repository
@Transactional(readOnly = true)
public interface ETrackSubmitDocumentRepository extends CrudRepository<SubmitDocument, Long> {
  public List<SubmitDocument> findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(@Param("edbDistrictId") Long districtId,
      @Param("documentStateCode") String documentStateCode);
}
