package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.SWFacTypeSubTypeDocument;

@Repository
public interface SWFacTypeSubTypeDocumentRepo extends CrudRepository<SWFacTypeSubTypeDocument, String> {
 
  @Query(value="select  distinct f.sw_facility_type_id ||'-'||nvl(f.sw_facility_sub_type_id, 0) as unique_id, f.sw_facility_type_id, sf.facility_type_desc, "
      + "sfs.sw_facility_sub_type_id, sfs.sub_type_description from {h-schema}e_required_doc_for_fac_type f, "
      + "{h-schema}e_sw_facility_type sf, {h-schema}e_sw_facility_sub_type sfs "
      + "where f.sw_facility_type_id = sf.sw_facility_type_id and f.sw_facility_sub_type_id=sfs.sw_facility_sub_type_id(+) "
      + "and f.active_ind=1 and sf.active_ind=1 and sfs.active_ind=1 order by 2", nativeQuery = true)
  List<SWFacTypeSubTypeDocument> findAllDocumentAssociatedSWFacTypeAndSubType();
}
