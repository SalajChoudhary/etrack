package dec.ny.gov.etrack.dcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.DocumentName;

@Repository
public interface DocumentNameRepo extends CrudRepository<DocumentName, Long> {

  @Query(value="select document_id, document_nm display_name, "
      + "document_type_id, document_sub_type_id, document_sub_type_title_id support_doc_ref_id "
      + "from {h-schema}e_support_document where project_id=?1 and document_state_code='A' "
      + "and ref_document_id is null and addl_doc_ind is null order by document_nm asc", nativeQuery = true)
  public List<DocumentName> findAllByProjectIdAndRefDocumentIdIsNotNull(final Long projectId);

  @Query(value="select document_id, document_nm display_name, "
      + "document_type_id, document_sub_type_id, document_sub_type_title_id support_doc_ref_id "
      + "from {h-schema}e_spatial_inq_document where inquiry_id=?1 and document_state_code='A' "
      + "and ref_document_id is null and addl_doc_ind is null order by document_nm asc", nativeQuery = true)
  public List<DocumentName> findAllByInquiryIdAndRefDocumentIdIsNotNull(final Long inquiryId);

}
