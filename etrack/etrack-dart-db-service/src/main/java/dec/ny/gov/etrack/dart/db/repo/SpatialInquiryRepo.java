package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDetail;

@Repository
public interface SpatialInquiryRepo extends CrudRepository<SpatialInquiryDetail, Long> {
  @Query(value="select * from {h-schema}e_spatial_inq_detail where lower(requestor_name)=?1", nativeQuery=true)
  List<SpatialInquiryDetail> findAllByRequestorName(final String requestorName);
  List<SpatialInquiryDetail> findByCreatedById(final String userId);
  
  @Query(value="select * from {h-schema}e_spatial_inq_detail  where analyst_assigned_id=?1 "
      + "and spatial_inq_category_id=?2 and original_submittal_ind=1 and "
      + "original_submittal_date is not null and inquiry_id not in ( "
      + "select inquiry_id from {h-schema}e_spatial_inq_response where inquiry_completed_ind=1)", nativeQuery = true)
  List<SpatialInquiryDetail> findByAssignedAnalystIdAndSpatialInqCategoryId(
      final String userId, final Integer inquiryType);
  
  @Query(value="select * from {h-schema}e_spatial_inq_detail where spatial_inq_category_id=?1 "
      + "and original_submittal_ind=1 and original_submittal_date is not null", nativeQuery = true)
  List<SpatialInquiryDetail> findBySpatialInqCategoryId(final Integer category);
  
  @Query(value="select * from {h-schema}e_spatial_inq_detail where spatial_inq_category_id=?1 and region=?2 "
      + "and original_submittal_ind=1 and original_submittal_date is not null", nativeQuery = true)
  List<SpatialInquiryDetail> findBySpatialInqCategoryIdAndRegion(final Integer category,
      final Integer region);

  @Query(value="select * from {h-schema}e_spatial_inq_detail where "
      + "original_submittal_ind=1 and original_submittal_date is not null", nativeQuery = true)
  List<SpatialInquiryDetail> findAllSpatialInquiries();

  @Query(value="select * from {h-schema}e_spatial_inq_detail where region=?1 "
      + "and original_submittal_ind=1 and original_submittal_date is not null", nativeQuery = true)
  List<SpatialInquiryDetail> findBySpatialInquiriesByRegion(final Integer region);

  @Query(value="select c.spatial_inq_category_id, c.spatial_inq_category_desc "
      + "from {h-schema}e_spatial_inq_detail e, {h-schema}e_spatial_inq_category c "
      + "where e.spatial_inq_category_id=c.spatial_inq_category_id and e.inquiry_id=?1", nativeQuery=true)
  List<String> findInquiryCategoryByInquiryId(final Long inquiryId);
}
