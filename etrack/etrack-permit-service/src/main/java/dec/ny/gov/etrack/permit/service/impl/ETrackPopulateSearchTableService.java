package dec.ny.gov.etrack.permit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.permit.dao.EtrackSearchToolDAO;
import dec.ny.gov.etrack.permit.entity.SearchLoad;
import dec.ny.gov.etrack.permit.repo.SearchLoadRepo;
import dec.ny.gov.etrack.permit.repo.SearchQueryRepo;
import dec.ny.gov.etrack.permit.util.SearchLoadRequest;

@Service
public class ETrackPopulateSearchTableService {

  @Autowired
  private EtrackSearchToolDAO etrackSearchToolDAO;
  @Autowired
  private SearchQueryRepo searchQueryRepo;
  @Autowired
  private SearchLoadRepo searchLoadRepo;

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackSearchServiceImpl.class.getName());
  
  @Scheduled(cron = "0 0 7-20/4 * * MON-FRI")
  public void cleanUpUnProcessedRecordsWhichBecameOrphan() {
    logger.info(
        "Entering into cleanUp existing records and re-load new Search records Or Incremental load to add additional. Requested time {}",
        new Date());
    Iterable<SearchLoad> searchLoadDatas = searchLoadRepo.findAll();
    List<SearchLoad> searchResultList = new ArrayList<>();
    searchLoadDatas.forEach(searchResultList::add);
    SearchLoad searchLoadRecord = null;
    if (CollectionUtils.isEmpty(searchResultList) 
        || (searchLoadRecord=searchResultList.get(0)).getLastLoadDate() == null) {
      
      logger.info("CleanUp existing records and re-load if exists or add new records "
          + "to prepare as pre-requisite for the User's Search. Requested time {}", new Date());
      try {
        String procResponse = etrackSearchToolDAO.retriveSearchToolsDetail();
        if (searchLoadRecord == null) {
          searchLoadRecord = new SearchLoad();
          searchLoadRecord.setSearchLoadId(1);
        }
        searchLoadRecord.setLastLoadDate(new Date());
        searchLoadRepo.save(searchLoadRecord);
        logger.info("CleanUp existing records and re-load if exists or add new records "
            + "to prepare as pre-requisite for the User's Search Completed. Requested time {}. Procedure response {}",
            new Date(), procResponse);
      } catch (Exception e) {
        logger.error("Error while refreshing the Search table data", e);
      }
    } else {
      logger.info("Incremental upload rocess has been initiated. Requested time {}", new Date());
      try {
        String procResponse = etrackSearchToolDAO.processIncrementalSearchTableDetailsLoad();
        logger.info(
            "Incremental upload rocess has been completed. Requested time {}. Process Response {}",
            new Date(), procResponse);
      } catch (Exception e) {
        logger.error("Error while refreshing the Search table data", e);
      }
    }
  }

  /**
   * Run the Search table data refresh on demand based on the input request raised by user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param requestedLoadRequest - RELOAD - Refresh the entire Search table. INCREMENTAL_LOAD -
   *        Update the Search table with modified records.
   */
  public void onDemandProcessToRefreshSearchResults(final String userId, final String contextId,
      SearchLoadRequest requestedLoadRequest) {

    logger.info(
        "Populate Search table ON DEMAND Process. User Id {}, "
            + "Context Id {}, On Demand Request {}",
        userId, contextId, requestedLoadRequest.name());
    if (SearchLoadRequest.RELOAD.equals(requestedLoadRequest)) {
      searchQueryRepo.deleteSearchLoadRecord();
    }
    cleanUpUnProcessedRecordsWhichBecameOrphan();
  }
}
