package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SupportDocumentMaintenance;

@Repository
public interface SupportDocumentMaintenanceRepo extends CrudRepository<SupportDocumentMaintenance, Integer>{

  @Query(value="select distinct f.reqd_doc_fac_type_id as unique_id, d.document_type_id,  d.document_type_desc, ds.document_sub_type_id, ds.document_sub_type_desc, "
      + "dt.document_title_id, dt.document_title, dst.document_sub_type_title_id, "
      + "f.reqd_new, f.reqd_mod, f.reqd_ext, f.reqd_mnm, f.reqd_mtn, f.reqd_ren, f.reqd_rtn, f.reqd_xfer, f.active_ind "
      + "from {h-schema}e_required_doc_for_fac_type f, {h-schema}e_document_type d, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst where "
      + "d.document_type_id=ds.document_type_id(+) and ds.document_sub_type_id=dst.document_sub_type_id "
      + "and dst.document_title_id=dt.document_title_id and dst.document_sub_type_title_id=f.document_sub_type_title_id "
      + " and f.sw_facility_type_id=?1 and f.sw_facility_sub_type_id is null order by dt.document_title", nativeQuery = true)
  List<SupportDocumentMaintenance> findAllFacTypeAndSubTypeAssociatedConfigurationRecords(final Integer swFacTypeId);
  
  @Query(value="select distinct f.reqd_doc_fac_type_id as unique_id, d.document_type_id, d.document_type_desc, "
      + "ds.document_sub_type_id, ds.document_sub_type_desc, "
      + "dt.document_title_id, dt.document_title, dst.document_sub_type_title_id, "
      + "f.reqd_new, f.reqd_mod, f.reqd_ext, f.reqd_mnm, f.reqd_mtn, f.reqd_ren, f.reqd_rtn, f.reqd_xfer, f.active_ind "
      + "from {h-schema}e_required_doc_for_fac_type f, {h-schema}e_document_type d, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst where "
      + "d.document_type_id=ds.document_type_id(+) and ds.document_sub_type_id=dst.document_sub_type_id "
      + "and dst.document_title_id=dt.document_title_id and dst.document_sub_type_title_id=f.document_sub_type_title_id "
      + " and f.sw_facility_type_id=?1 and f.sw_facility_sub_type_id=?2 order by dt.document_title", nativeQuery = true)
  List<SupportDocumentMaintenance> findAllFacTypeAndSubTypeAssociatedConfigurationRecordsBySubTypeId(
      final Integer swFacTypeId, final Integer swFacSubTypeId);

  @Query(value="select distinct f.required_doc_for_seqr_id as unique_id, d.document_type_id, d.document_type_desc, ds.document_sub_type_id, ds.document_sub_type_desc, "
      + "dt.document_title_id, dt.document_title, dst.document_sub_type_title_id, "
      + "f.reqd_new, f.reqd_mod, f.reqd_ext, f.reqd_mnm, f.reqd_mtn, f.reqd_ren, f.reqd_rtn, f.reqd_xfer, f.active_ind "
      + "from {h-schema}e_required_doc_for_seqr f, {h-schema}e_document_type d, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst where "
      + "d.document_type_id=ds.document_type_id(+) and ds.document_sub_type_id=dst.document_sub_type_id "
      + "and dst.document_title_id=dt.document_title_id and dst.document_sub_type_title_id=f.document_sub_type_title_id order by dt.document_title", nativeQuery = true)
  List<SupportDocumentMaintenance> findAllSEQRAssociatedDocumentConfigurationRecords();
  
  @Query(value="select distinct f.required_doc_for_nat_gp_id as unique_id, d.document_type_id, d.document_type_desc, ds.document_sub_type_id, ds.document_sub_type_desc, "
      + "dt.document_title_id, dt.document_title, dst.document_sub_type_title_id, "
      + "f.reqd_new, f.reqd_mod, f.reqd_ext, f.reqd_mnm, f.reqd_mtn, f.reqd_ren, f.reqd_rtn, f.reqd_xfer, f.active_ind "
      + "from {h-schema}e_required_doc_for_nat_gp f, {h-schema}e_document_type d, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst where "
      + "d.document_type_id=ds.document_type_id(+) and ds.document_sub_type_id=dst.document_sub_type_id "
      + "and dst.document_title_id=dt.document_title_id and dst.document_sub_type_title_id=f.document_sub_type_title_id order by dt.document_title", nativeQuery = true)
  List<SupportDocumentMaintenance> findAllNaturalResourceGPAssociatedDocumentConfigurationRecords();

  @Query(value="select distinct f.reqd_doc_permit_type_id as unique_id, d.document_type_id, d.document_type_desc, ds.document_sub_type_id, ds.document_sub_type_desc, "
      + "dt.document_title_id, dt.document_title, dst.document_sub_type_title_id, "
      + "f.reqd_new, f.reqd_mod, f.reqd_ext, f.reqd_mnm, f.reqd_mtn, f.reqd_ren, f.reqd_rtn, f.reqd_xfer, f.active_ind "
      + "from {h-schema}e_required_doc_for_permit_type f, {h-schema}e_document_type d, {h-schema}e_document_sub_type ds, "
      + "{h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst where "
      + "d.document_type_id=ds.document_type_id(+) and ds.document_sub_type_id=dst.document_sub_type_id "
      + "and dst.document_title_id=dt.document_title_id and dst.document_sub_type_title_id=f.document_sub_type_title_id and f.permit_type_code=?1 order by dt.document_title", nativeQuery = true)
  List<SupportDocumentMaintenance> findAllPermitTypeAssociatedDocumentConfigurationRecords(final String permitTypeCode);
  
  @Query(value="select document_sub_type_title_id from {h-schema}e_document_sub_type_title where document_title_id=?1", nativeQuery = true)
  List<Long> findDocumentSubTypeTitleIdByDocumentTitleId(final Long titleId);
}

