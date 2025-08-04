package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import dec.ny.gov.etrack.dart.db.entity.GIInquiryAlert;

public interface GIInquiryAlertRepo extends CrudRepository<GIInquiryAlert, Long>{
  @Query(value="select a.inquiry_alert_id, a.inquiry_id, a.alert_date, a.alert_note, "
      + "a.created_by_id, a.create_date, a.msg_read_ind as read_ind, n.comments "
      + "from {h-schema}e_geo_inquiry_alert a, {h-schema}e_geo_inquiry_note n "
      + "where a.inquiry_note_id=n.inquiry_note_id and a.alert_rcvd_user_id=?1 ", nativeQuery = true)
  List<GIInquiryAlert> findAllAlertsByUserId(final String userId);

  @Transactional
  @Modifying
  @Query(value="delete {h-schema}e_geo_inquiry_alert where inquiry_id=?1 and inquiry_alert_id=?2", nativeQuery = true)
  void deleteByInquiryIdAndAlertId(Long inquiryId, Long inquiryAlertId);
}
