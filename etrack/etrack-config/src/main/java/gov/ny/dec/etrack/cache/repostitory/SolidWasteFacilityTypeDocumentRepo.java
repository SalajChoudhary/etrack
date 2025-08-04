package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SolidWasteFacilityTypeDocument;

@Repository
public interface SolidWasteFacilityTypeDocumentRepo
    extends CrudRepository<SolidWasteFacilityTypeDocument, Long> {

  @Query(value="select fac.* from {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst, {h-schema}e_required_doc_for_fac_type fac  "
      + "where dt.document_title_id=dst.document_title_id and dst.document_sub_type_title_id=fac.document_sub_type_title_id "
      + "and dst.document_title_id=?1 and fac.sw_facility_type_id=?2 and fac.sw_facility_sub_type_id is null", nativeQuery = true)
  List<SolidWasteFacilityTypeDocument> findByDocumentTitleIdAndSWFacTypeId(final Long documentTitleId, final Integer swFacTypeId);
  
  @Query(value="select fac.* from {h-schema}e_document_title dt, {h-schema}e_document_sub_type_title dst, {h-schema}e_required_doc_for_fac_type fac  "
      + "where dt.document_title_id=dst.document_title_id and dst.document_sub_type_title_id=fac.document_sub_type_title_id "
      + "and dst.document_title_id=?1 and fac.sw_facility_type_id=?2 and fac.sw_facility_sub_type_id=?3", nativeQuery = true)
  List<SolidWasteFacilityTypeDocument> findByDocumentTitleIdAndSWFacTypeIds(final Long documentTitleId, final Integer swFacTypeId, final Integer swFacSubTypeId);
}
