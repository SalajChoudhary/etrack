package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectInquiryAssociate;

@Repository
public interface ProjectInquiryAssociateRepo extends CrudRepository<ProjectInquiryAssociate, Long> {
  List<ProjectInquiryAssociate> findByProjectId(Long projectId);
  
  @Modifying
  @Query("delete ProjectInquiryAssociate p where p.projectId= :projectId")
  int deleteByProjectId(Long projectId);

  @Query(value="select count(*) from {h-schema}e_spatial_inq_detail inq "
      + "where inq.original_submittal_date is not null and inq.inquiry_id in (?1)", nativeQuery = true)
  int findByInquiriesList(Set<Long> inquiries);
}
