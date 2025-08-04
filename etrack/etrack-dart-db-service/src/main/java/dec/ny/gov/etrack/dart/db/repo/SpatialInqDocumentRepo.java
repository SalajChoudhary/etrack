package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SpatialInqDocumentEntity;

@Repository
public interface SpatialInqDocumentRepo extends CrudRepository<SpatialInqDocumentEntity, Long> {
  
  @Query(value="select nvl(sf.inquiry_file_id+sd.document_id, sd.document_id) as document_uid, "
      + "sd.document_id, sd.ecmaas_guid, sd.access_by_dep_only_ind, sd.doc_releasable_code, sd.tracked_application_id,"
      + "sd.document_type_id, sd.document_sub_type_id, sd.document_state_code, sd.document_desc, sd.document_sub_type_title_id,"
      + "sd.doc_sub_type_other_txt, sd.create_date, sd.created_by_id, sd.modified_by_id, sd.modified_date, sd.inquiry_id,"
      + "sd.document_nm, sd.ref_document_id, sd.addl_doc_ind, sd.ref_document_desc, 1 as file_count "
      + "from {h-schema}e_spatial_inq_document sd, {h-schema}e_spatial_inq_file sf where sd.document_id=sf.document_id(+) "
      + "and sd.inquiry_id=?1 and sd.document_state_code='A' order by sd.create_date desc", nativeQuery=true)
  List<SpatialInqDocumentEntity> findAllUploadedSupportDocumentsByInquiryIdWithFilesCount(final Long inquiryId);
}

