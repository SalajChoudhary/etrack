package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.NaturalResourceGeneralPermitDocument;

@Repository
public interface NaturalResourceGeneralPermitDocumentRepo
    extends CrudRepository<NaturalResourceGeneralPermitDocument, Long> {

  @Query(value="select nat.* from {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst, {h-schema}e_required_doc_for_nat_gp nat  "
      + "where dt.document_title_id=dst.document_title_id and dst.document_sub_type_title_id=nat.document_sub_type_title_id "
      + "and dst.document_title_id=?1", nativeQuery = true)
  List<NaturalResourceGeneralPermitDocument> findByDocumentTitleId(Long documentTitleId);
}
