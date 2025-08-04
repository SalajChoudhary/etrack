package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.PermitKeywordEntity;

@Repository
public interface PermitKeywordRepo extends CrudRepository<PermitKeywordEntity, Long> {
  List<PermitKeywordEntity> findByPermitTypeCodeAndKeywordId(String permitTypeCode, final Long keywordId);
}
