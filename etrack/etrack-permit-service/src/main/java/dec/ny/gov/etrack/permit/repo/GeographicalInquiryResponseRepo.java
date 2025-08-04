package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.GeographicalInquiryResponse;

@Repository
public interface GeographicalInquiryResponseRepo extends CrudRepository<GeographicalInquiryResponse, Long>{
  List<GeographicalInquiryResponse> findByInquiryId(Long inquiryId);
}
