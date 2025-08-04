package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DocumentTypeEntity;

@Repository
public interface DocumentTypeRepo  extends CrudRepository<DocumentTypeEntity, Integer>{

    @Query(value="select * from {h-schema}e_document_type where lower(document_type_desc) = lower(?1)", nativeQuery = true)
	List<DocumentTypeEntity> findByDescription(String description);
    @Query(value="select * from {h-schema}e_document_type where document_type_id != ?1 and lower(document_type_desc) = lower(?2)", nativeQuery = true)
    List<DocumentTypeEntity> findByDescriptionAndDocumentTypeId(Integer documentTypeId, String description);
	List<DocumentTypeEntity> findAllByOrderByDescription();

}
