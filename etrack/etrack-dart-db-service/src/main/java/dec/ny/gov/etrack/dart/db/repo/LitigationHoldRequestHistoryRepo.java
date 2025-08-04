package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.LitigationHoldHistory;

@Repository
public interface LitigationHoldRequestHistoryRepo extends CrudRepository<LitigationHoldHistory, Long> {
//  @Query(value="select litigation_hold_h_id, litigation_hold_id, project_id, to_char(litigation_hold_start_date, 'mm/dd/yyyy'), "
//      + "to_char(litigation_hold_end_date, 'mm/dd/yyyy') from {h-schema}e_project_litigation_hold_h "
//      + "where project_id=?1 and litigation_hold_ind is not null order by litigation_hold_h_id desc", nativeQuery = true)
  @Query(value="select * from {h-schema}e_project_litigation_hold_h where project_id=?1 "
      + "and litigation_hold_ind is not null and litigation_hold_end_date is not null order by litigation_hold_h_id desc", nativeQuery = true)
  List<LitigationHoldHistory> findByProjectIdOrderByLitigationHoldHIdDesc(Long projectId);
}
