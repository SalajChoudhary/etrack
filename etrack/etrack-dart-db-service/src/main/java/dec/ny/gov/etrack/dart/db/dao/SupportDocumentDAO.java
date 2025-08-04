package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_OUT_FOR_REVIEW_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_SUPPORT_DOC_CURSOR;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.OutForReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.SupportDocument;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Repository
public class SupportDocumentDAO {

  private static final Logger logger = LoggerFactory.getLogger(SupportDocumentDAO.class.getName());
  
  @Autowired
  @Qualifier("eTrackSupportDocumentProcCall")
  private SimpleJdbcCall eTrackSupportDocumentProcCall;

  @Autowired
  @Qualifier("eTrackOutForReviewAppsProcCall")
  private SimpleJdbcCall eTrackOutForReviewAppsProcCall;
  
  /**
   * Retrieve all the required documents list for the project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project id
   * 
   * @return - List of support documents for the input Project id.
   */
  @SuppressWarnings("unchecked")
  public List<SupportDocument> retrieveAllSupportDocumentsForTheProjectId(
      final String userId, final String contextId, final Long projectId) {
    
    try {
      logger.info("Retrieve {} the Support document details for the input UserId: {} Context Id: {} ", projectId,
          userId, contextId);
      Map<String, Object> input = new HashMap<>();
      input.put(DartDBConstants.PROJECT_ID, projectId);
      eTrackSupportDocumentProcCall.declareParameters(
          new SqlParameter(DartDBConstants.PROJECT_ID, Types.INTEGER),
          new SqlOutParameter(P_SUPPORT_DOC_CURSOR, Types.REF_CURSOR))
      .returningResultSet(P_SUPPORT_DOC_CURSOR, BeanPropertyRowMapper.newInstance(SupportDocument.class));

      Map<String, Object> result = eTrackSupportDocumentProcCall.execute(input);
      if (result != null) {
        return (List<SupportDocument>) result.get(P_SUPPORT_DOC_CURSOR);
      }
    } catch (Exception e) {
      throw new DartDBException("SUPPORT_DOC_RETRIEVAL_ERR",
          "Error while retrieving all the required documents for the input project id "+projectId,  e);
    }
    return null;
  }


  /**
   * This method is used to retrieve all the application which are Out for Review.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of projects which are Out for review.
   */
  @SuppressWarnings("unchecked")
  public List<OutForReviewEntity> retrieveOutForReviewApps(
      final String userId, final String contextId) {
    
    try {
      logger.info("Entering into Retrieve the Out for Review Apps details for the input UserId: {} Context Id: {} ", userId,
          userId, contextId);
      Map<String, Object> input = new HashMap<>();
      input.put(DartDBConstants.USER_ID, userId);
      eTrackOutForReviewAppsProcCall.declareParameters(
          new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
          new SqlOutParameter(P_OUT_FOR_REVIEW_CURSOR, Types.REF_CURSOR))
      .returningResultSet(P_OUT_FOR_REVIEW_CURSOR, BeanPropertyRowMapper.newInstance(OutForReviewEntity.class));

      Map<String, Object> result = eTrackOutForReviewAppsProcCall.execute(input);
      logger.info("Exiting from Retrieve the Out for Review Apps details for the input UserId: {} Context Id: {} ", userId,
          userId, contextId);
      if (result != null) {
        return (List<OutForReviewEntity>) result.get(P_OUT_FOR_REVIEW_CURSOR);
      }
    } catch (Exception e) {
      throw new DartDBException("OUT_FOR_REVIEW_APPS_ERR", 
          "Error while retrieving the Out for review projects from the eTrack", e);
    }
    return null;
  }
}
