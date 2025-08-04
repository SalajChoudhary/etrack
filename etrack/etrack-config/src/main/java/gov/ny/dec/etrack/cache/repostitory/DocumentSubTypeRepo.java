package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeEntity;

@Repository
public interface DocumentSubTypeRepo extends CrudRepository<DocumentSubTypeEntity, Integer> {
  @Query(
      value = "select * from {h-schema}e_document_sub_type where lower(document_sub_type_desc) = lower(?1)",
      nativeQuery = true)
  List<DocumentSubTypeEntity> findByDescription(String description);

  @Query(
      value = "select * from {h-schema}e_document_sub_type where document_sub_type_id !=?1 "
          + "and document_type_id !=?2 and lower(document_sub_type_desc) = lower(?3)", nativeQuery = true)
  List<DocumentSubTypeEntity> findByDocumentSubTypeIdAndDescription(final Integer docSubTypeId,final Integer docTypeId,
      final String description);

  List<DocumentSubTypeEntity> findAllByOrderByDescription();

  @Query(value = "select * from {h-schema}e_document_sub_type where document_type_id=?1", nativeQuery = true)
  List<DocumentSubTypeEntity> findByDocumentTypeId(Long documentTypeId);
}
