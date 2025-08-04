package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeTitle;

@Repository
public interface DocumentSubTypeTitleRepo extends CrudRepository<DocumentSubTypeTitle, Long> {

  @Query(value="select * from etrackowner.e_document_sub_type_title where document_sub_type_title_id !=?1 "
      + "and document_sub_type_id=?2 and document_title_id=?3", nativeQuery = true)
  List<DocumentSubTypeTitle> findAllDocumentSubTypeTitle(final Long subTypeTitleId, final Long subTypeId, final Long titleId);
}
