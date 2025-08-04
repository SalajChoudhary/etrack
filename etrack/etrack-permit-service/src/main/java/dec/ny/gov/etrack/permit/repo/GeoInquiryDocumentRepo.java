package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.GeoInquiryDocument;

@Repository
public interface GeoInquiryDocumentRepo extends CrudRepository<GeoInquiryDocument, Long> {

  @Query(value="select sd.document_id, sd.ecmaas_guid, dc.document_class_nm, sd.document_nm from {h-schema}e_spatial_inq_document sd, "
      + "{h-schema}e_document_class dc, {h-schema}e_document_type dt where sd.inquiry_id=?1 and sd.ref_document_id is null "
      + "and sd.document_state_code='A' and sd.document_type_id=dt.document_type_id "
      + "and dt.document_class_id=dc.document_class_id and sd.document_id in (?2)", nativeQuery = true)
  List<GeoInquiryDocument> findAllByInquiryIdDocumentIds(final Long inquiryId, final List<Long> documentIds);
}
