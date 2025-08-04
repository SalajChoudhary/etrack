package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.SpatialInquiryDetail;

@Repository
public interface SpatialInquiryRepo extends CrudRepository<SpatialInquiryDetail, Long> {
  @Query(value="select document_id from {h-schema}e_spatial_inq_document where inquiry_id=?1 "
      + "and document_nm like (?2) and ref_document_id is null and document_state_code='A'", nativeQuery = true)
  List<Long> findByDocumentNameAndInquiryId(Long inquiryId, String documentName);

  @Modifying
  @Query(value = "update {h-schema}e_spatial_inq_detail set analyst_assigned_id=?3, assigned_analyst_name=?4, modified_by_id=?1, "
      + "modified_date=sysdate, analyst_assigned_date=sysdate where inquiry_id=?2", nativeQuery = true)
  void updateGeographicalInquiryAssignmentDetails(String userId, Long inquiryId, String analystId,
      String analystName);

  @Modifying
  @Query(value = "update {h-schema}e_spatial_inq_detail set skip_document_ind=1, modified_by_id=?1, "
      + "modified_date=sysdate where inquiry_id=?2", nativeQuery = true)
  void updateSkipDocumentUploadProcess(String userId, Long inquiryId);
}
