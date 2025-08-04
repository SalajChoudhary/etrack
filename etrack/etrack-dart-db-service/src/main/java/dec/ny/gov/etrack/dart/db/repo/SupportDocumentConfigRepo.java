package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentConfig;

@Repository
public interface SupportDocumentConfigRepo extends CrudRepository<SupportDocumentConfig, Long> {

//  @Query(value="select pt.reqd_doc_permit_type_id as support_doc_id, ds.document_type_id, st.document_sub_type_id, "
//      + "dt.document_title as document_name, pt.permit_type_code, st.document_sub_type_title_id "
//      + "from {h-schema}e_required_doc_for_permit_type pt,{h-schema}e_document_sub_type_title st, "
//      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type ds where "
//      + "pt.document_sub_type_title_id=st.document_sub_type_title_id "
//      + "and st.document_title_id = dt.document_title_id "
//      + "and st.document_sub_type_id = ds.document_sub_type_id "
//      + "and upper(pt.reqd_new)=upper('YES') and permit_type_code in "
//      + "(select a.permit_type_code from {h-schema}e_application a where a.project_id=?1)", nativeQuery = true)
//  List<SupportDocumentConfig> findAllSupportDocuments(final Long projectId);

  @Query(value="select distinct dt.document_title_id support_doc_id, dt.document_title document_name, pt.document_sub_type_title_id document_sub_type_title_id, '' document_type_id, "
      + "st.document_sub_type_id, pt.permit_type_code from {h-schema}e_required_doc_for_permit_type pt, {h-schema}e_document_sub_type_title st, {h-schema}e_document_title dt"
      + "    where pt.document_sub_type_title_id=st.document_sub_type_title_id and st.document_title_id=dt.document_title_id and pt.reqd_new='Yes' and "
      + "    pt.permit_type_code in (?1) and upper(dt.document_title) like '%SUPPLE%'"
      + "", nativeQuery = true)
  List<SupportDocumentConfig> findAllSupplementalDocumentForms(final Set<String> permitTypeCodes);
}
