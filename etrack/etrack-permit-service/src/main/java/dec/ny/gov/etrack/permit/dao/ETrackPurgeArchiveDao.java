package dec.ny.gov.etrack.permit.dao;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PURGE_QUERY_NAME_CODE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PURGE_REGION_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PURGE_RESULT_SET_NAME;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.P_DOCUMENT_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_CODE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_MESSAGE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.USER_ID;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.PurgeArchive;

@Repository
public class ETrackPurgeArchiveDao {

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackPurgeArchiveDao.class.getName());

  @Autowired
  @Qualifier("purgeArchiveReviewProcCall")
  private SimpleJdbcCall purgeArchiveReviewProcCall;

  @Autowired
  @Qualifier("purgeArchiveDcoumentProcCall")
  private SimpleJdbcCall purgeArchiveDcoumentProcCall;

  /**
   * Used to create the result set for purge/archive.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id to track the request.
   * @param purgeArchive - Request body.
   * 
   * @return - Query Result id.
   */
  public Long savePurgeArchiveReviewDetails(String userId, String contextId,
      PurgeArchive purgeArchive) {
    
    logger.info("Entering into savePurgeArchiveReviewDetails. User Id {}, Context Id {}", userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(PURGE_REGION_ID, purgeArchive.getRegionId());
    inputParam.put(PURGE_QUERY_NAME_CODE, purgeArchive.getQueryNameCode());
    inputParam.put(PURGE_RESULT_SET_NAME, purgeArchive.getResultSetName());
    inputParam.put(USER_ID, userId);

    Map<String, Object> result = null;
    try {
      purgeArchiveReviewProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(PURGE_REGION_ID, Types.INTEGER),
          new SqlParameter(PURGE_QUERY_NAME_CODE, Types.INTEGER),
          new SqlParameter(PURGE_RESULT_SET_NAME, Types.VARCHAR),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT)).returningResultSet(contextId, null);
      logger.info("before executing purgeArchiveReviewProcCall. User Id {}, Context Id {}", userId, contextId);
      result = purgeArchiveReviewProcCall.execute(inputParam);
      logger.info("after executing purgeArchiveReviewProcCall. User Id {}, Context Id {}", userId, contextId);
      Long statusCode = (Long) result.get(STATUS_CODE);
      logger.info("Raw data received as procedure output after processed {}. User Id {}, Context Id {}", statusCode, userId, contextId);
      if (statusCode > 0) {
        throw new BadRequestException("PURGE_ARCHIVE_DB_ERROR",
            "Unable to process the request due to some data issue "
                + "while populating the project details. Error message "
                + result.get(STATUS_MESSAGE),
            purgeArchive.getRegionId());
      } else if (statusCode < 0) {
        throw new ETrackPermitException("PURGE_ARCHIVE_DB_ERROR",
            "Received Unsuccessful status code recieved from the DB "
                + "while populating the project details. DB Status message "
                + result.get(STATUS_MESSAGE));
      }
      Long resultId = ((BigDecimal) result.get("P_RESULT_SET_ID")).longValue();
      logger.info("Entering into savePurgeArchiveReviewDetails. "
          + "User Id {}, Context Id {}, resultId {}", userId, contextId, resultId);
      return resultId;
    } catch (Exception e) {
      throw new ETrackPermitException("PURGE_ARCHIVE_DB_ERROR",
          "Error from Database while populating the PurgeArchive Details. ", e);
    }
  }

  /**
   * To remove a document the review list requested by Analyst/Admin.
   * 
   * @param userId - User who initiates this request.
   * @param documentId - Document id.
   */
  public void deletePurgeArchiveDocument(final String userId, final String contextId,
      Long documentId) {

    logger.info("Entering into deletePurgeArchiveDocument. User Id {}, Context Id {}", userId,
        contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(P_DOCUMENT_ID, documentId);
    inputParam.put(USER_ID, userId);

    Map<String, Object> result = null;
    try {
      purgeArchiveDcoumentProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(P_DOCUMENT_ID, Types.INTEGER),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT));
      
      logger.info("Before executing purgeArchiveDcoumentProcCall. User Id {}, Context Id {}", userId, contextId);
      result = purgeArchiveDcoumentProcCall.execute(inputParam);
      logger.info("After executing purgeArchiveDcoumentProcCall. User Id {}, Context Id {}", userId, contextId);
      logger.debug("Raw data received as procedure output {}. User Id {}, Context Id{}", result, userId, contextId);
      Long statusCode = (Long) result.get(STATUS_CODE);
      logger.info("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode > 0) {
        throw new BadRequestException("PURGE_ARCHIVE_DB_ERROR",
            "Unable to process the request due to some data issue "
                + "while populating the project details. Error message "
                + result.get(STATUS_MESSAGE),
            documentId);
      } else if (statusCode < 0) {
        throw new ETrackPermitException("PURGE_ARCHIVE_DB_ERROR",
            "Received Unsuccessful status code recieved from the DB "
                + "while populating the project details. DB Status message "
                + result.get(STATUS_MESSAGE));
      }
    } catch (Exception e) {
      throw new ETrackPermitException("PURGE_ARCHIVE_DB_ERROR",
          "Error from Database while populating the PurgeArchive Details. ", e);
    }
    logger.info("Exiting from deletePurgeArchiveDocument. User Id {}, Context Id {}", userId,
        contextId);
  }

}
