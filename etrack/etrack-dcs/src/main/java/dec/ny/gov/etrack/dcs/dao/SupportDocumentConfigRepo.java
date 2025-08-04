package dec.ny.gov.etrack.dcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.SupportDocumentConfig;

@Repository
public interface SupportDocumentConfigRepo extends CrudRepository<SupportDocumentConfig, Integer> {

//  @Query(value="select rt.document_sub_type_title_id as document_sub_type_title_id, st.document_sub_type_id, doc.document_type_id, dc.document_class_nm  "
//      + "from {h-schema}e_required_doc_for_permit_type rt, {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title st, {h-schema}e_document_sub_type su, "
//      + "{h-schema}e_document_class dc, {h-schema}e_document_type doc "
//      + "where rt.document_sub_type_title_id = st.document_sub_type_title_id  "
//      + "and dt.document_title_id=st.document_title_id "
//      + "and st.document_sub_type_id=su.document_sub_type_id "
//      + "and su.document_type_id=doc.document_type_id and doc.document_class_id=dc.document_class_id "
//      + "and upper(dt.document_title)=?1 and upper(rt.reqd_new)=upper('YES') "
//      + "and rt.permit_type_code in (select a.permit_type_code from {h-schema}e_application a where a.project_id=?2)", nativeQuery = true)
//  List<SupportDocumentConfig> findSupportConfigByDisplayName(final String documentDisplayName, final Long projectId);
  
  @Query(value="select dst.document_sub_type_title_id, ds.document_sub_type_id, ds.document_type_id,"
      + "dc.document_class_nm from {h-schema}e_document_sub_type_title dst, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_type dt, {h-schema}e_document_class dc where dst.document_sub_type_id=ds.document_sub_type_id "
      + "and ds.document_type_id=dt.document_type_id and "
      + "dt.document_class_id=dc.document_class_id and dst.document_sub_type_title_id=?1", nativeQuery = true)
  List<SupportDocumentConfig> findSupportDocumentConfigBySubTypeTitleId(final Integer documentSubTypeTitleId);
  

//  @Query(value="select rt.document_sub_type_title_id as document_sub_type_title_id, st.document_sub_type_id, doc.document_type_id, "
//      + "dc.document_class_nm  from {h-schema}e_required_doc_for_permit_type rt, {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title st, "
//      + "{h-schema}e_document_sub_type su, {h-schema}e_document_class dc, {h-schema}e_document_type doc "
//      + "where rt.document_sub_type_title_id = st.document_sub_type_title_id  and dt.document_title_id=st.document_sub_type_title_id "
//      + "and st.document_sub_type_id=su.document_sub_type_id and su.document_type_id=doc.document_type_id "
//      + "and doc.document_class_id=dc.document_class_id and rt.reqd_doc_permit_type_id=?1", nativeQuery = true)
//  List<SupportDocumentConfig> findSupportConfigByConfigId(final Long supportDocConfigId);

  @Query(
      value = "select c.document_class_nm from {h-schema}e_document_type t, {h-schema}e_document_class "
          + "c where t.document_class_id=c.document_class_id and t.document_type_id=?1",
      nativeQuery = true)
  List<String> findDocumentClassByDocumentTypeId(final Integer documentTypeId);

}

