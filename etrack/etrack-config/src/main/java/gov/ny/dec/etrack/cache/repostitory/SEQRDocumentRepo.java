package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SEQRDocumentEntity;

@Repository
public interface SEQRDocumentRepo extends CrudRepository<SEQRDocumentEntity, Long> {
 @Query(value="select rs.* from etrackowner.e_document_title dt, {h-schema}e_document_sub_type_title dst, {h-schema}e_required_doc_for_seqr rs "
     + "where dt.document_title_id=dst.document_title_id and dst.document_sub_type_title_id=rs.document_sub_type_title_id "
     + "and dst.document_title_id=?1", nativeQuery = true) 
 List<SEQRDocumentEntity> findByDocumentTitleId(Long documentTitleId); 
}
