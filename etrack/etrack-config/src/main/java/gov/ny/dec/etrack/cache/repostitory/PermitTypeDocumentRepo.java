package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.PermitTypeDocumentEntity;

@Repository
public interface PermitTypeDocumentRepo extends CrudRepository<PermitTypeDocumentEntity, Long> {

  @Query(value="select rp.* from {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst, {h-schema}e_required_doc_for_permit_type rp  "
      + "where dt.document_title_id=dst.document_title_id and dst.document_sub_type_title_id=rp.document_sub_type_title_id "
      + "and dst.document_title_id=?1 and rp.permit_type_code=?2", nativeQuery = true)
  List<PermitTypeDocumentEntity> findByDocumentTitleIdAndPermitTypeCode(
      final Long documentTitleId, final String permitTypeCode);
}
