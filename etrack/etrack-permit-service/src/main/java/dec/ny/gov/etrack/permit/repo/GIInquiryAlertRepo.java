package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.GIInquiryAlert;

@Repository
public interface GIInquiryAlertRepo extends CrudRepository<GIInquiryAlert, Long> {

  GIInquiryAlert findByInquiryAlertIdAndInquiryId(Long alertId, Long inquiryId);
  
}
