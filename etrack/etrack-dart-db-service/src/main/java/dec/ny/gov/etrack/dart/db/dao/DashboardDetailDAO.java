package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.DISTRICT_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_DISPOSED_APPS_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_DUE_APPS_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_PENDING_APPS_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_SUSPENDED_APPS_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.REGION_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_CODE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_MESSAGE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.USER_ID;
import java.io.Serializable;
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
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.ReviewerDocumentDetail;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Repository
public class DashboardDetailDAO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Autowired
  @Qualifier("eTrackDartUnIssuedProcCall")
  private SimpleJdbcCall eTrackDartUnIssuedProcCall;

  @Autowired
  @Qualifier("eTrackDartDueAppsProcCall")
  private SimpleJdbcCall eTrackDartDueAppsProcCall;

  @Autowired
  @Qualifier("eTrackDartReviewAppsProcCall")
  private SimpleJdbcCall eTrackDartReviewAppsProcCall;

  @Autowired
  @Qualifier("eTrackGetReviewDetailsProcCall")
  private SimpleJdbcCall eTrackGetReviewDetailsProcCall;

  @Autowired
  @Qualifier("eTrackDartAplctDueAppsProcCall")
  private SimpleJdbcCall eTrackDartAplctDueAppsProcCall;

  @Autowired
  @Qualifier("eTrackDartSuspendedAppsProcCall")
  private SimpleJdbcCall eTrackDartSuspendedAppsProcCall;

  @Autowired
  @Qualifier("eTrackDartDisposedAppsProcCall")
  private SimpleJdbcCall eTrackDartDisposedAppsProcCall;

  @Autowired
  @Qualifier("eTrackDartEmergencyAppsProcCall")
  private SimpleJdbcCall eTrackDartEmergencyAppsProcCall;

  private static final Logger logger = LoggerFactory.getLogger(DashboardDetailDAO.class.getName());

  /**
   * Retrieve the Un-issued applications for the input district Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param districtId - District Id.
   * @param regionId - Region id.
   * 
   * @return - List of Un-issued Applications.
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveDARTPendingApplications(final String userId, final String contextId,
      final Long districtId, final Integer regionId) {

    logger.info("Entering into retrieveDARTUnissuedApps(). User Id {}, Context Id {}", userId,
        contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DISTRICT_ID, districtId);
      inputParam.put(USER_ID, userId);
      inputParam.put(REGION_ID, regionId);
      eTrackDartUnIssuedProcCall.declareParameters(new SqlParameter(DISTRICT_ID, Types.BIGINT),
          new SqlParameter(REGION_ID, Types.BIGINT), new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(P_PENDING_APPS_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackDartUnIssuedProcCall.addDeclaredRowMapper(P_PENDING_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartUnIssuedProcCall.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveDARTUnissuedApps User Id: {}, Context ID {}", userId,
            contextId);
        return (List<DartApplication>) result.get(P_PENDING_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("UNISSUED_APPS_RETRIEVAL_ERR",
            "Error while retrieving the Unissued applications from enterprise. " + statusMessage);
      } else {
        throw new DartDBException("UNISSUED_APPS_RETRIEVAL_INVALID_STATUS_ERR",
            "Received Unsuccessful error status while retrieving the Unissued applications from enterprise. "
                + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("UNISSUED_APPS_RETRIEVAL_GENERAL_ERR",
          "General error while retrieving the Unissued applications from enterprise. ", e);
    }
  }


  /**
   * Retrieve all the Tasks Due applications from enterprise database for the logged in user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @return - List of Tasks Due applications.
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveDARTDueApps(final String userId, final String contextId) {

    logger.info("Entering into retrieveDARTDueApps()  User  Id {} Context Id {}", userId,
        contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackDartDueAppsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(P_DUE_APPS_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackDartDueAppsProcCall.addDeclaredRowMapper(P_DUE_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartDueAppsProcCall.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.info(
          "Raw data received as procedure output Cursor Type is as updated as -10 . Status code{}",
          statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveDARTDueApps User Id: {}, Context ID {}", userId,
            contextId);
        return (List<DartApplication>) result.get(P_DUE_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("TASKS_DUE_APPS_RETRIEVAL_ERR",
            "Error occurred while retrieving the Tasks Due apps " + statusMessage);
      } else {
        throw new DartDBException("TASKS_DUE_APPS_RETRIEVAL_INVALID_STATUS_ERR",
            "Received unsuccessful error while retrieving the Tasks Due apps " + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("TASKS_DUE_APPS_RETRIEVAL_GENERAL_ERR",
          "General error occurred while retrieving the Tasks Due apps", e);
    }
  }

  /**
   * Retrieve the Applicant Response Due details from enterprise database for the logged in user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - LIst of Applicant Response Due applications.
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveDARTAplctResponseDueApps(final String userId,
      final String contextId) {

    logger.info("Entering into retrieveDARTAplctResponseDueApps()  User  Id {}, Context Id {}",
        userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackDartAplctDueAppsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(P_DUE_APPS_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackDartAplctDueAppsProcCall.addDeclaredRowMapper(P_DUE_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartAplctDueAppsProcCall.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveDARTAplctResponseDueApps User Id: {}, Context ID {}",
            userId, contextId);
        return (List<DartApplication>) result.get(P_DUE_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("APLCT_RESPONSE_DUE_RETRIEVAL_ERR",
            "Error while retrieving the Applicant Response Due Applications " + statusMessage);
      } else {
        throw new DartDBException("APLCT_RESPONSE_DUE_RETRIEVAL_ERR",
            "Received Unsuccessful status received while retrieving Applicant Response Due Applications "
                + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("APLCT_RESPONSE_DUE_RETRIEVAL_GENERAL_ERR",
          "General error occurred while retrieving Aplct Response Due apps", e);
    }
  }

  /**
   * Retrieve the Suspended Application details for the logged in user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique id to track this request.
   * 
   * @return - Suspended applications list
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveDARTSuspendedApps(final String userId,
      final String contextId) {

    logger.info("Entering into retrieveDARTSuspendedApps()  User  Id {}, Context Id {}", userId,
        contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackDartSuspendedAppsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(P_SUSPENDED_APPS_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackDartSuspendedAppsProcCall.addDeclaredRowMapper(P_SUSPENDED_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartSuspendedAppsProcCall.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from eTrackDartSuspendedAppsProcCall User Id: {}, Context ID {}",
            userId, contextId);
        return (List<DartApplication>) result.get(P_SUSPENDED_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("SUSPENDED_APPS_RETRIEVAL_GENERAL_ERR",
            "Error while retrieving the Suspended Apps " + statusMessage);
      } else {
        throw new DartDBException("SUSPENDED_APPS_RETRIEVAL_INVALID_STATUSL_ERR",
            "Received Unsuccesful status while retrieving the Suspended Apps " + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("SUSPENDED_APPS_RETRIEVAL_GENERAL_ERR",
          "General error occurred while retrieving suspended apps", e);
    }
  }



  /**
   * Retrieve all the Review projects for the logged in user id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique Id to track this request.
   * @param facilityRegionId - Facility Region Id.
   * 
   * @return - List of Review documents associated with user id.
   */
  @SuppressWarnings("unchecked")
  public List<ReviewerDocumentDetail> findAllReviewProjectDetailsByUserId(final String userId,
      final String contextId, Integer facilityRegionId) {

    logger.info("Entering into findAllReviewProjectDetailsByUserId(). User Id {} Context Id {}, Facility Region Id {}",
        userId, contextId, facilityRegionId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      if (facilityRegionId != null) {
        if (facilityRegionId == -1) {
          facilityRegionId = null;
        }
        inputParam.put(DartDBConstants.USER_ID, null);
      } else {
        inputParam.put(DartDBConstants.USER_ID, userId);
      }
      inputParam.put(DartDBConstants.REGION_ID, facilityRegionId);
      eTrackGetReviewDetailsProcCall
          .declareParameters(new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlParameter(DartDBConstants.REGION_ID, Types.INTEGER),
              new SqlOutParameter(DartDBConstants.P_REVIEW_PROJ_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.P_REVIEW_PROJ_CURSOR,
              BeanPropertyRowMapper.newInstance(ReviewerDocumentDetail.class));

      Map<String, Object> response = eTrackGetReviewDetailsProcCall.execute(inputParam);

      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        return (List<ReviewerDocumentDetail>) response.get(DartDBConstants.P_REVIEW_PROJ_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("REVIEW_DOC_RETRIEVAL_ERR",
            "Error while retrieving the Reviewer detials for the user id " + userId + " "
                + statusMessage);
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_USER_FOUND", statusMessage);
      } else {
        throw new DartDBException("REVIEW_DOC_RETRIEVAL_ERR",
            "Received Unsuccessful response while "
                + "receiving the Reviewer details for the user id " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("REVIEW_DOC_RETRIEVAL_GENERAL_ERR",
          "General error while retrieving the review eligible documents", e);
    }
  }

  /**
   * Retrieve the Disposed Applications details for the logged in user id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique id to track this request.
   * @param facilityRegionId - facility region id.
   * 
   * @return - Disposed applications list
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveDARTDisposedApps(final String userId, final String contextId,
      final Integer facilityRegionId) {
    logger.info("Entering into retrieveDARTDisposedApps()  User  Id {}, Context Id {}", userId,
        contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(REGION_ID, facilityRegionId);
      eTrackDartDisposedAppsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(REGION_ID, Types.INTEGER),
          new SqlOutParameter(P_DISPOSED_APPS_CURSOR, Types.REF_CURSOR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackDartDisposedAppsProcCall.addDeclaredRowMapper(P_DISPOSED_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartDisposedAppsProcCall.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from eTrackDartDisposedAppsProcCall User Id: {}, Context ID {}",
            userId, contextId);
        return (List<DartApplication>) result.get(P_DISPOSED_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("DISPOSED_APPS_ERR",
            "Error occurred while retrieving disposed application" + statusMessage);
      } else {
        throw new DartDBException("DISPOSED_APPS_INVALID_STATUS_ERR",
            "Received unsuccessful status while retrieving disposed application" + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("DISPOSED_APPS_GENERAL_ERR",
          "General error occurred while retrieving disposed application", e);
    }
  }

  /**
   * Retrieve the Emergency Authorization applications for the logged in user id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Emergency Authorization applications.
   */
  @SuppressWarnings("unchecked")
  public List<DartApplication> retrieveEmergencyAuthorizationApps(final String userId,
      final String contextId) {

    logger.info("Entering into retrieveEmergencyAuthorizationApps(). User Id {}, Context Id {}",
        userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackDartEmergencyAppsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(DartDBConstants.P_EMERGENCY_AUTH_APPS_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));
      eTrackDartEmergencyAppsProcCall.addDeclaredRowMapper(
          DartDBConstants.P_EMERGENCY_AUTH_APPS_CURSOR,
          BeanPropertyRowMapper.newInstance(DartApplication.class));
      Map<String, Object> result = eTrackDartEmergencyAppsProcCall.execute(inputParam);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveDARTUnissuedApps User Id: {}, Context ID {}", userId,
            contextId);
        return (List<DartApplication>) result.get(DartDBConstants.P_EMERGENCY_AUTH_APPS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("EMERGENCY_AUTH_APPS_RETRIEVAL_ERR",
            "Error while retrieving the Emergency Authorization applications from enterprise. "
                + statusMessage);
      } else {
        throw new DartDBException("EMERGENCY_AUTH_APPS_RETRIEVAL_INVALID_STATUS_ERR",
            "Received Unsuccessful error status while retrieving "
                + "the Emergency Authorization applications from enterprise. " + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("EMERGENCY_AUTH_APPS_RETRIEVAL_GENERAL_ERR",
          "General error while retrieving the Emergency Authorization applications from enterprise. ",
          e);
    }
  }
}

