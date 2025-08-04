package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ReviewDocument;

@Repository
public interface ReviewDocumentRepo extends CrudRepository<ReviewDocument, Long>{

  @Query(value="select distinct e.document_id, e.document_nm, e.document_desc, dt.document_type_desc, st.document_sub_type_desc, "
      + "(select count(document_id) from  {h-schema}e_support_document_file sf where sf.document_id=f.document_id) as file_count, "
      + "to_char(e.create_date, 'mm/dd/yyyy hh12:mi am') upload_date "
      + "from {h-schema}e_support_document e, {h-schema}e_support_document_file f, {h-schema}e_document_sub_type st, {h-schema}e_document_type dt "
      + "where f.document_id=e.document_id  and (e.document_sub_type_id = st.document_sub_type_id (+)) "
      + "and e.document_type_id = dt.document_type_id and e.document_state_code='A' "
      + "and e.project_id=?1", nativeQuery = true)
  List<ReviewDocument> findAllReviewEligibleDocuments(Long projectId);
  
  @Query(value="select distinct e.document_id, e.document_nm, e.document_desc, dt.document_type_desc, st.document_sub_type_desc, "
      + "(select count(document_id) from {h-schema}e_spatial_inq_file sf where sf.document_id=f.document_id) as file_count, "
      + "to_char(e.create_date, 'mm/dd/yyyy hh12:mi am') upload_date "
      + "from {h-schema}e_spatial_inq_document e, {h-schema}e_spatial_inq_file f,{h-schema}e_document_sub_type st, {h-schema}e_document_type dt "
      + "where f.document_id=e.document_id  and (e.document_sub_type_id = st.document_sub_type_id (+)) "
      + "and e.document_type_id = dt.document_type_id and e.document_state_code='A' and e.inquiry_id=?1", nativeQuery = true)
  List<ReviewDocument> findAllGIReviewEligibleDocuments(Long inquiryId);
}
