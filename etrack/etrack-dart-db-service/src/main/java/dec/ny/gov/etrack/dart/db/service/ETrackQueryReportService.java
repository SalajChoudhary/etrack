package dec.ny.gov.etrack.dart.db.service;

import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittalReport;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;

@Service
public interface ETrackQueryReportService {

  /**
   * Retrieve the criteria matched submitted Projects submitted via different channel.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param queryCriteria - Search Criteria parameters. Object of {@link ProjectSubmittedRetrievalCriteria}
   * 
   * @return - Data result of {@link ProjectSubmittalReport} 
   */
  ProjectSubmittalReport retrieveProjectSubmittalDetails(
      final String userId, final String contextId, final ProjectSubmittedRetrievalCriteria queryCriteria);

  /**
   * Retrieve the Candidate Keyword details report.
   * 
   * @param userId - User who initiates this request.
   * @param regionId - Region Id.
   * 
   * @return - Candidate Keyword details.
   */
  Object retrieveCandidateKeywordDetailsReport(String userId, String contextId);
}
