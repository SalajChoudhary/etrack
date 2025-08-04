package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DocumentTitleEntity;

@Repository
public interface DocumentTitleRepo  extends CrudRepository<DocumentTitleEntity, Integer>{
  @Query(
      value = "select * from {h-schema}e_document_title where lower(document_title) = lower(?1)", nativeQuery = true)
	List<DocumentTitleEntity> findByDescription(String title);
  @Query(
      value = "select * from {h-schema}e_document_title where document_title_id !=?1 and lower(document_title) = lower(?2)", nativeQuery = true)
    List<DocumentTitleEntity> findByDocumentTitleIdAndDescription(final Integer titleId, final String title);
	List<DocumentTitleEntity> findAllByOrderByDescription();
}
