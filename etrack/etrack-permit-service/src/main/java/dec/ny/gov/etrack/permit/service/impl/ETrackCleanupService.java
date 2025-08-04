package dec.ny.gov.etrack.permit.service.impl;

import javax.management.timer.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dec.ny.gov.etrack.permit.dao.ETrackCleanUpDAO;

@Service
public class ETrackCleanupService {
  
  @Autowired
  private ETrackCleanUpDAO eTrackCleanUpDAO;

  @Scheduled(fixedDelay = Timer.ONE_HOUR)
  @Transactional
  public void cleanUpUnProcessedRecordsWhichBecameOrphan() {
    eTrackCleanUpDAO.cleanUpOrphanRecords();
  }
}
