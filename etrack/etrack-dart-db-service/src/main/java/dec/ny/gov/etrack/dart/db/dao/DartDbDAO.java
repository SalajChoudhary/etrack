package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.ADDR_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.COUNTY;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.DEC_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.FACILITY_ADDR_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.FACILITY_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.FACILITY_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.MUNICIPALITY;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROGRAM_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROGRAM_TYPE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROJECT_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLICS_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_HIST_CURSOR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_CODE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_MESSAGE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.TX_MAP;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.USER_ID;
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
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.ApplicationPermitForm;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddressHistory;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.FacilityHistory;
import dec.ny.gov.etrack.dart.db.entity.FacilityLRPDetail;
import dec.ny.gov.etrack.dart.db.entity.PublicAndFacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.PublicAssociatedFacility;
import dec.ny.gov.etrack.dart.db.entity.PublicDetail;
import dec.ny.gov.etrack.dart.db.entity.history.AddressHistory;
import dec.ny.gov.etrack.dart.db.entity.history.PublicHistoryDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Repository
public class DartDbDAO {

  @Autowired
  @Qualifier("eTrackFacilityInfoProc")
  private SimpleJdbcCall eTrackFacilityInfoProc;

  @Autowired
  @Qualifier("eTrackGetPublicInfoProc")
  private SimpleJdbcCall eTrackGetPublicInfoProc;

  @Autowired
  @Qualifier("eTrackGetFacilityInfoProc")
  private SimpleJdbcCall eTrackGetFacilityInfoProc;

  @Autowired
  @Qualifier("eTrackGetDECIdProc")
  private SimpleJdbcCall eTrackGetDECIdProc;

  @Autowired
  @Qualifier("eTrackGetDECIdByTxMapCall")
  private SimpleJdbcCall eTrackGetDECIdByTxMapCall;

  @Autowired
  @Qualifier("eTrackApplicantSearchProc")
  private SimpleJdbcCall eTrackApplicantSearchProc;

  @Autowired
  @Qualifier("eTrackOrgSearchProc")
  private SimpleJdbcCall eTrackOrgSearchProc;

  @Autowired
  @Qualifier("matchedFacilityAddress")
  private SimpleJdbcCall matchedFacilityAddress;

  @Autowired
  @Qualifier("getPublicInfoFromDart")
  private SimpleJdbcCall getPublicInfoFromDart;

  @Autowired
  @Qualifier("getExistingPermitsFromDart")
  private SimpleJdbcCall getExistingPermitsFromDart;

  @Autowired
  @Qualifier("getExpiredPermitsFromDart")
  private SimpleJdbcCall getExpiredPermitsFromDart;

  @Autowired
  @Qualifier("getRegionIdByUserIdProcCall")
  private SimpleJdbcCall getRegionIdByUserIdProcCall;

  @Autowired
  @Qualifier("getUsersByRoleTypeIdProcCall")
  private SimpleJdbcCall getUsersByRoleTypeIdProcCall;

  @Autowired
  @Qualifier("getUsersWithValidEmailProcCall")
  private SimpleJdbcCall getUsersWithValidEmailProcCall;

  @Autowired
  @Qualifier("eTrackGetReviewDetailsProcCall")
  private SimpleJdbcCall eTrackGetReviewDetailsProcCall;

  @Autowired
  @Qualifier("eTrackStaffDetailsProcCall")
  private SimpleJdbcCall eTrackStaffDetailsProcCall;

  @Autowired
  @Qualifier("eTrackDartDIMSRSupportDetailProcCall")
  private SimpleJdbcCall eTrackDartDIMSRSupportDetailProcCall;
  
  @Autowired
  @Qualifier("eTrackDartPermitNarrativeDescProcCall")
  private SimpleJdbcCall eTrackDartPermitNarrativeDescProcCall;

  @Autowired
  @Qualifier("eTrackPermitFormsProcCall")
  private SimpleJdbcCall eTrackPermitFormsProcCall;

  @Autowired
  @Qualifier("enterpriseSupportDetailsProcCall")
  private SimpleJdbcCall enterpriseSupportDetailsProcCall;
  
  /**
   * Logging
   */
  private static final Logger logger = LoggerFactory.getLogger(DartDbDAO.class.getName());

  
  /**
   * Retrieve the facility details from eTrack database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id to retrieve the facility details
   * @return - Response received for the input details
   * 
   */
  public Map<String, Object> geETrackFacilityDetails(String userId, String contextId,
      Long projectId) {
    logger.info("Entering into geETrackDetails User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(PROJECT_ID, projectId);
      inputParam.put(USER_ID, userId);
      eTrackFacilityInfoProc
          .declareParameters(new SqlParameter(PROJECT_ID, Types.BIGINT),
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(FACILITY_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(PUBLICS_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(PUBLICS_CURSOR, BeanPropertyRowMapper.newInstance(PublicDetail.class))
          .returningResultSet(FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityDetail.class));

      result = eTrackFacilityInfoProc.execute(inputParam);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      logger.debug("Raw data received as procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));
      if (statusCode == 0) {
        logger.info("Exiting from geETrackDetails User Id: {}, Context ID {}", userId, contextId);
        return result;
      } else if (statusCode == -100) {
        throw new DartDBException("FACILITY_RETRIEVE_DB_ERROR", 
            "Received DB error while retrieving the facility details " + statusMessage);
      } else if (statusCode >= 1 && statusCode <= 3) {
        if (statusCode == 1) {
          throw new NoDataFoundException("NO_FACILITY_FOUND", "There is no facility found for the input project Id " + projectId);
        }
        if (statusCode == 2) {
          throw new NoDataFoundException("NO_FACILITY_ADDR_FOUND", "There is no facility address found for the input project Id " + projectId);
        }
        if (statusCode == 3) {
          throw new NoDataFoundException("NO_PROJECT_FOUND", "There is no Project found for the input project Id " + projectId);
        }
      } else {
        throw new DartDBException("UNEXPECTED_DB_ERROR", 
            "Unexpected DB Error while retrieving the facility summary details " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      logger.error(
          "Error while retrieving the Facility data user Id: {}, context Id: {}, Error :", userId, contextId, e);
      throw new DartDBException("GENERAL_FACILITY_RETRIEVAL_ERR", "Error while retrieving Facility summary data ", e);
    }
    return result;
  }

  /**
   * Retrieve the applicant details for the input parameter(s)
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project id associated with the input applicant
   * @param publicId - Applicant id
   * 
   * @return - Applicant details if any
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> getApplicantDetails(String userId, String contextId, Long projectId,
      Long publicId) {
    logger.info("Entering into getApplicantDetails() Context Id {} ", contextId);
    Map<String, Object> publicDetails = null;
    List<PublicDetail> applicantInfo = null;
    try {
      publicDetails = retrieveApplicantInfo(userId, contextId, projectId, publicId);
      applicantInfo = (List<PublicDetail>) publicDetails.get(PUBLIC_CURSOR);
      if (CollectionUtils.isEmpty(applicantInfo)) {
        logger.info("No data is available in eTrack. Trying to get it from DART User Id {}, Context Id {} ",
            userId, contextId);
        publicDetails = getPublicInfoFromDart(userId, contextId, publicId, null);
      }
    } catch (NoDataFoundException | DartDBException | BadRequestException e) {
      throw e;
    } catch (Exception nfe) {
      logger.error("Error while retrieving the applicant details ", nfe);
      throw new DartDBException("APPLICANT_RETRIEVAL_ERR", "Error while retrieving the public details", nfe);
    }
    return publicDetails;
  }

  /**
   * This method is used to retrieve the applicant detail
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id
   * @param publicId - Applicant Id to retrieve the details.
   * 
   * @return - Returns the Applicant details.
   */
  private Map<String, Object> retrieveApplicantInfo(String userId, String contextId, Long projectId,
      Long publicId) {
    logger.info("Entering into retrieveApplicantInfo()  public Id {}. User Id {}, Context Id {}", publicId,
        userId, contextId);
    Map<String, Object> result = null;
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(PUBLIC_ID, publicId);
    try {
      eTrackGetPublicInfoProc.declareParameters(new SqlParameter(PUBLIC_ID, Types.BIGINT),
          new SqlOutParameter(PUBLIC_CURSOR, -10),
          new SqlOutParameter(PUBLIC_HIST_CURSOR, -10), new SqlOutParameter(ADDR_HIST_CURSOR, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      eTrackGetPublicInfoProc.addDeclaredRowMapper(PUBLIC_CURSOR,
          BeanPropertyRowMapper.newInstance(PublicDetail.class));
      eTrackGetPublicInfoProc.addDeclaredRowMapper(PUBLIC_HIST_CURSOR,
          BeanPropertyRowMapper.newInstance(PublicHistoryDetail.class));
      eTrackGetPublicInfoProc.addDeclaredRowMapper(ADDR_HIST_CURSOR,
          BeanPropertyRowMapper.newInstance(AddressHistory.class));

      result = eTrackGetPublicInfoProc.execute(inputParam);
      logger.debug(
          "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
          result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.info(
          "Raw data received as procedure output Cursor Type is as updated as -10 . Status code{}",
          statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveApplicantInfo User Id: {}, Context ID {}", userId,
            contextId);
        return result;
      } else if (statusCode == -100) {
        throw new DartDBException("UNEXPECTED_ETRACK_DB_ERROR", "Error while retrieving the applicant details " + statusMessage);
      } else if (statusCode == 1) {
        return result;
      } else {
        throw new DartDBException("GENERAL_APPLICANT_DB_ERROR", "Unexpected DB error received " + statusMessage);
      }
    } catch (DartDBException dbe) {
      throw dbe;
    } catch (Exception e) {
      throw new DartDBException("GENEARL_APLCT_RETRIEVAL_ERR", 
          "Unexpected error while retrieving the applicant details", e);
    }
  }

  /**
   * This method is used to retrieve the Facility and history detail.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id
   * 
   * @return  - Facility and History details.
   */
  private Map<String, Object> retrieveFacilityInfoWithHistory(final String userId,
      final String contextId, final Long projectId) {
    
    logger.info("Entering into retrieveFacilityInfoWithHistory()  User  Id {} Context Id {}",
        userId, contextId);
    
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(PROJECT_ID, projectId);
    eTrackGetFacilityInfoProc.declareParameters(new SqlParameter(PROJECT_ID, Types.BIGINT),
        new SqlOutParameter(FACILITY_CURSOR, -10), new SqlOutParameter(FACILITY_HIST_CURSOR, -10),
        new SqlOutParameter(FACILITY_ADDR_HIST_CURSOR, -10),
        new SqlOutParameter(STATUS_CODE, Types.BIGINT),
        new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

    eTrackGetFacilityInfoProc.addDeclaredRowMapper(FACILITY_CURSOR,
        BeanPropertyRowMapper.newInstance(FacilityDetail.class));
    eTrackGetFacilityInfoProc.addDeclaredRowMapper(FACILITY_HIST_CURSOR,
        BeanPropertyRowMapper.newInstance(FacilityHistory.class));
    eTrackGetFacilityInfoProc.addDeclaredRowMapper(FACILITY_ADDR_HIST_CURSOR,
        BeanPropertyRowMapper.newInstance(FacilityAddressHistory.class));
    Map<String, Object> result = eTrackGetFacilityInfoProc.execute(inputParam);
    logger.debug(
        "Raw data received as procedure output Cursor Type is as updated as -10 .  Result {}",
        result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);
    logger.info(
        "Raw data received as procedure output Cursor Type is as updated as -10 . Status code{}",
        statusCode);
    if (statusCode == 0) {
      logger.info("Exiting from retrieveFacilityInfoWithHistory User Id: {}, Context ID {}", userId,
          contextId);
      return result;
    } else if (statusCode == -100) {
      throw new DartDBException("FACILITY_AND_HIST_RETRIEVAL_ERROR",
          "Error while retrieving the facility along with history details " + statusMessage);
    } else if (statusCode == 1) {
      return result;
    } else {
      throw new DartDBException("FACILITY_AND_HIST_RETRIEVAL_ERROR", 
          "Received Unsuccessful status received while retrieving the facility along with history details. " + statusMessage);
    }
  }


  /**
   * Retrieve the Facility details for the input Program Id and the Program Type. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param programId - Program Id
   * @param programType - Program Type
   * @return - Returns the matched Facility details.
   */
  @SuppressWarnings("unchecked")
  public List<FacilityAddress> findDECIdByProgramType(String userId, String contextId,
      String programId, String programType) {
    logger.info("Entering into findDECIdByProgramType() Context Id {}", contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(PROGRAM_TYPE, programType);
      inputParam.put(PROGRAM_ID, programId);
      inputParam.put(USER_ID, userId);

      eTrackGetDECIdProc
          .declareParameters(new SqlParameter(PROGRAM_TYPE, Types.VARCHAR),
              new SqlParameter(PROGRAM_ID, Types.VARCHAR), new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.FACILITY_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityAddress.class));

      Map<String, Object> response = eTrackGetDECIdProc.execute(inputParam);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from findDECIdByProgramType User Id: {}, Context ID {}", userId,
            contextId);
        return (List<FacilityAddress>) response.get(DartDBConstants.FACILITY_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("DEC_ID_RETRIEVAL_ERR",
            "Error while retrieving the DEC ID by Program type details " + statusMessage);
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_FAC_FOUND", statusMessage);
      } else {
        throw new DartDBException("DEC_ID_RETRIEVAL_STATUS_ERR", 
            "Received unsuccessful status error while retrieving the DEC ID " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("DEC_ID_RETRIEVAL_GENERAL_ERR", 
          "General error while retrieving the DEC ID by programType", e);
    }
  }

  /**
   * Returns all the Publics/Applicants for the input search parameter.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param firstName - First name search text.
   * @param fType - First name search pattern.
   * @param lastName - Last name search text.
   * @param lType - Last name search pattern.
   * 
   * @return - List of Publics.
   */
  @SuppressWarnings("unchecked")
  public List<PublicAssociatedFacility> searchAllMatchedApplicants(String userId, String contextId,
      String firstName, String fType, String lastName, String lType) {

    logger.info("Entering into searchAllMatchedApplicants() Context Id {}", contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, firstName);
      inputParam.put(DartDBConstants.LAST_NAME_SEARCH, lastName);
      inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, fType);
      inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, lType);
      inputParam.put(USER_ID, userId);

      eTrackApplicantSearchProc
          .declareParameters(new SqlParameter(DartDBConstants.FIRST_NAME_SEARCH, Types.VARCHAR),
              new SqlParameter(DartDBConstants.LAST_NAME_SEARCH, Types.VARCHAR),
              new SqlParameter(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, Types.VARCHAR),
              new SqlParameter(DartDBConstants.LAST_NAME_SEARCH_PATTERN, Types.VARCHAR),
              new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.P_RESULT_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.P_RESULT_CURSOR,
              BeanPropertyRowMapper.newInstance(PublicAssociatedFacility.class));

      Map<String, Object> response = eTrackApplicantSearchProc.execute(inputParam);
      logger.debug("Raw data received as procedure output {}", response);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from searchAllMatchedApplicants User Id: {}, Context ID {}", userId,
            contextId);
        return (List<PublicAssociatedFacility>) response.get(DartDBConstants.P_RESULT_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("MATCH_PUBLIC_RETRIEVAL_ERR",
            "Error while retrieving the matched applicant details " + statusMessage);
      } else if (statusCode == 1) {
        throw new BadRequestException("INVALID_SEARCH", statusMessage, fType + " " + lType);
      } else {
        throw new DartDBException("MATCH_PUBLIC_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received unsuccessful status error while retrieving the matched applicants. " + statusMessage);
      }
    } catch (DartDBException | BadRequestException e) {
      throw e;
    } catch (Exception e) {
      logger.error(
          "Error while getting all the matched applicant details user Id: {}, context Id: {}, Error {}:",
          userId, contextId, e);
      throw new DartDBException("MATCH_PUBLIC_RETRIEVAL_GENERAL_ERR", 
          "General error occurred while retrieving all the matched applicants", e);
    }
  }

  /**
   * Retrieve the list of matched Public Organization(s) based on the search
   * criteria passed as a search parameter.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param firstName - Organization first name/search content
   * @param fType - Search Type
   * @param searchPublicType - Public Type
   * 
   * @return - Matched Organizations results
   */
  @SuppressWarnings("unchecked")
  public List<PublicAssociatedFacility> searchAllMatchedPublicOrganizations(final String userId,
      final String contextId, final String firstName, final String fType,
      final String searchPublicType) {

    logger.info("Entering into searchAllMatchedPublicOrganizations() User Id: {} Context Id {}",
        userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, firstName);
      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, fType);
      inputParam.put(DartDBConstants.APPLICANT_TYPE, searchPublicType);
      inputParam.put(USER_ID, userId);
      eTrackOrgSearchProc
          .declareParameters(new SqlParameter(DartDBConstants.PUBLIC_NAME_SEARCH, Types.VARCHAR),
              new SqlParameter(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, Types.VARCHAR),
              new SqlParameter(DartDBConstants.APPLICANT_TYPE, Types.VARCHAR),
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.P_RESULT_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.P_RESULT_CURSOR,
              BeanPropertyRowMapper.newInstance(PublicAssociatedFacility.class));

      Map<String, Object> response = eTrackOrgSearchProc.execute(inputParam);
      logger.debug("Raw data received as searchAllMatchedPublicOrganizations output {}", response);

      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from searchAllMatchedPublicOrganizations User Id: {}, Context ID {}",
            userId, contextId);
        return (List<PublicAssociatedFacility>) response.get(DartDBConstants.P_RESULT_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("ORG_MATCH_RETRIEVAL_ERR",
            "Error while retrieving the matched Organization/Agency details " + statusMessage);
      } else if (statusCode == 1) {
        throw new BadRequestException("ORG_INVALID_SEARCH", statusMessage, fType);
      } else {
        throw new DartDBException("MATCH_ORG_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received Unsuccessful status code while retrieving matched Organization " + statusMessage);
      }
    } catch (Exception e) {
      logger.error(
          "Error while getting all the matched Organization/Agency details user Id: {}, context Id: {}, Error {}:",
          userId, contextId, e);
      throw new DartDBException("MATCH_ORG_RETRIEVAL_GENERAL_ERR", 
          "General error while retrieving matched organization for the search type", e);
    }
  }

  /**
   * Returns all the Matched facilities for the input search parameter Facility Address Line 1 and City.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param addressLine1 - Facility' Address Line 1
   * @param city - Facility's City.
   * 
   * @return - Returns the matched facilities.
   */
  @SuppressWarnings("unchecked")
  public Object getMatchedFacilityAddress(final String userId, final String contextId,
      final String addressLine1, final String city) {
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.ADDRESS_LINE, addressLine1);
      inputParam.put(DartDBConstants.CITY, city);
      inputParam.put(USER_ID, userId);
      matchedFacilityAddress
          .declareParameters(new SqlParameter(DartDBConstants.ADDRESS_LINE, Types.VARCHAR),
              new SqlParameter(DartDBConstants.CITY, Types.VARCHAR),
              new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.MATCHING_FACILITY_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(DartDBConstants.STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(DartDBConstants.STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.MATCHING_FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityAddress.class));
      Map<String, Object> response = matchedFacilityAddress.execute(inputParam);
      logger.debug("Matched facility address details {}", response);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from matchedFacilityAddress User Id: {}, Context ID {}", userId,
            contextId);
        return (List<FacilityAddress>) response.get(DartDBConstants.MATCHING_FACILITY_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("MATCH_FAC_RETRIEVAL_ERR",
            "Error while retrieving the matched Facility Address details " + statusMessage);
      } else {
        throw new DartDBException("MATCH_FAC_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received unsuccessful status error while retrieving the matched applications " + statusMessage);
      }
    } catch (DartDBException dbe) {
      throw dbe;
    } catch (Exception e) {
      throw new DartDBException("MATCH_FAC_RETRIEVAL_GENERAL_ERR", 
          "General error while retrieving the matched facility address", e);
    }
  }

  /**
   * Retrieve the public information enterprise database for the input enterprise public Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param edbPublicId - public id available in enterprise database.
   * @param publicTypeCode - public Type code.
   * 
   * @return - Public Information from enterprise.
   */
  public Map<String, Object> getPublicInfoFromDart(final String userId,
      final String contextId,final Long edbPublicId, final String publicTypeCode) {
    try {
      logger.info("Entering into getPublicInfoFromDart  method. User id {}, context id {}", userId,
          contextId);
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.PUBLIC_ID, edbPublicId);
      inputParam.put(USER_ID, userId);
      inputParam.put(DartDBConstants.APPLICANT_TYPE, publicTypeCode);
      getPublicInfoFromDart
          .declareParameters(new SqlParameter(DartDBConstants.PUBLIC_ID, Types.BIGINT),
              new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlParameter(DartDBConstants.APPLICANT_TYPE, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.PUBLIC_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(DartDBConstants.STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(DartDBConstants.STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.PUBLIC_CURSOR,
              new BeanPropertyRowMapper<>(PublicDetail.class));

      Map<String, Object> response = getPublicInfoFromDart.execute(inputParam);
      logger.debug("Retrieve the public information from DART {}", response);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info("Exiting from getPublicInfoFromDart method. User id {}, context Id {}", userId,
            contextId);
        return response;
      } else if (statusCode == -100) {
        throw new DartDBException("DART_PUBLIC_RETRIEVAL_DB_ERR",
            "Error while retrieving the public information from DART " + statusMessage);
      } else if (statusCode == 1) {
        throw new BadRequestException("NO_PUBLIC_AVAILABLE",
            "There is no Existing Public available for the enterprise Public id " + edbPublicId, edbPublicId);
      } else {
        throw new DartDBException("UNEXPECTED_PUBLIC_RETRIEVAL_ERR", 
            "Unexpected DB error received while retrieving the public detail " + statusMessage);
      }
    } catch (BadRequestException | DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("GENERAL_PUBLIC_RETRIEVAL_ERR", 
          "General Error while retrieving the public information from DART", e);
    }
  }

  /**
   * Retrieve all the expired applications from Enterprise to extend if the user wants to.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param edbDistrictId - Enterprise District Id.
   * 
   * @return - Returns the list of expired applications.
   */
  public Map<String, Object> retrieveExpiredApplicationsToExtendFromEnterprise(
      final String userId, final String contextId, final Long edbDistrictId) {
    
    logger.info(
        "Entering into retrieveExpiredApplicationsToExtendFromEnterprise method. User Id {}, Context Id {}",
        userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
    inputParam.put(USER_ID, userId);
    try {
      getExpiredPermitsFromDart
          .declareParameters(new SqlParameter(DartDBConstants.DISTRICT_ID, Types.BIGINT),
              new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.EXPIRED_APPS_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(DartDBConstants.STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.EXPIRED_APPS_CURSOR,
              new BeanPropertyRowMapper<>(DartPermit.class));

      Map<String, Object> response = getExpiredPermitsFromDart.execute(inputParam);
      logger.debug("Retrieve the public information from DART {}", response);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info(
            "Exiting from retrieveExpiredApplicationsToExtendFromEnterprise method. User Id {}, Context Id {}",
            userId, contextId);
        return response;
      } else if (statusCode == -100) {
        throw new DartDBException("EXPIRED_PERMITS_RETRIEVAL_DB_ERR",
            "Error while retrieving the Expired Permits from the DART " + statusMessage);
      } else {
        throw new DartDBException("EXPIRED_PERMITS_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received unsuccessful status code while retrieving Expired Permits " + statusMessage);
      }
    } catch (NoDataFoundException | DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("EXPIRED_PERMITS_RETRIEVAL_GENERAL_DB_ERROR", 
          "Error while retrieving the Expired Permits from the DART", e);
    }
  }
  
  /**
   * Retrieve all the Active Authorization permits associated with the enterprise district id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param edbDistrictId - Enterprise District Id.
   * 
   * @return - Retrieve the Active Authorization Permits if any exists.
   */
  public Map<String, Object> retrieveModExtendEligiblePermitsFromEnterprise(final String userId,
      final String contextId, final Long edbDistrictId) {

    logger.info(
        "Entering into retrieveModExtendEligiblePermitsFromEnterprise method. User Id {}, Context Id {}",
        userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
    inputParam.put(USER_ID, userId);
    try {
      getExistingPermitsFromDart
          .declareParameters(new SqlParameter(DartDBConstants.DISTRICT_ID, Types.BIGINT),
              new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.EXISTING_APPS_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(DartDBConstants.STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.EXISTING_APPS_CURSOR,
              new BeanPropertyRowMapper<>(DartPermit.class));

      Map<String, Object> response = getExistingPermitsFromDart.execute(inputParam);
      logger.debug("Retrieve the public information from DART {}", response);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      if (statusCode == 0) {
        logger.info(
            "Exiting from retrieveModExtendEligiblePermitsFromEnterprise method. User Id {}, Context Id {}",
            userId, contextId);
        return response;
      } else if (statusCode == -100) {
        throw new DartDBException("EXISTING_PERMITS_RETRIEVAL_DB_ERR",
            "Error while retrieving the Active Authorization detials from the DART " + statusMessage);
      } else {
        throw new DartDBException("EXISTING_PERMITS_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received unsuccessful status code while retrieving Active Authorization Permits " + statusMessage);
      }
    } catch (NoDataFoundException | DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("EXISTING_PERMITS_RETRIEVAL_GENERAL_DB_ERROR", 
          "Error while retrieving the Active Authorization from the DART", e);
    }
  }
  

  /**
   * Retrieve the DEC ID for the input tax map number, County and Municipality.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param txMap - Tax Map number.
   * @param county - Country details.
   * @param municipality - Municipality details.
   * 
   * @return - Matched DEC ID details
   */
  @SuppressWarnings("unchecked")
  public List<FacilityAddress> findDECIdByTaxMap(String userId, String contextId, String txMap,
      String county, String municipality) {
    logger.info("Entering into findDECIdByTaxMap() Context Id {}", contextId);
    Map<String, Object> inputParam = new HashMap<>();
    
    try {
      inputParam.put(TX_MAP, txMap);
      inputParam.put(COUNTY, county);
      inputParam.put(MUNICIPALITY, municipality);
      inputParam.put(USER_ID, userId);

      eTrackGetDECIdByTxMapCall.declareParameters(new SqlParameter(TX_MAP, Types.VARCHAR),
          new SqlParameter(COUNTY, Types.VARCHAR), new SqlParameter(MUNICIPALITY, Types.VARCHAR),
          new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(DartDBConstants.FACILITY_CURSOR, Types.REF_CURSOR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR)).returningResultSet(DartDBConstants.FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityAddress.class));

      Map<String, Object> response = eTrackGetDECIdByTxMapCall.execute(inputParam);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from findDECIdByTaxMap User Id: {}, Context ID {}", userId, contextId);
        return (List<FacilityAddress>) response.get(DartDBConstants.FACILITY_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("DEC_ID_RETRIEVAL_ERR",
            "Error while retrieving the DEC ID by Txmap type details " + statusMessage);
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_FACILITY_DATA_FOUND", 
            "No facility found for the given Taxmap, County and Municipality");
      } else {
        throw new DartDBException("DEC_ID_RETRIEVAL_GENERAL_ERR", 
            "Received Unsuccessful status code from DB while retrieving DEC ID " + statusMessage);
      }
    } catch (NoDataFoundException | DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("GENERAL_ERR_RETRIEVE_DEC_ID", 
          "General error while retrieving the DEC ID for the input Tax Map, Municipality and County", e);
    }
  }

  /**
   * Retrieve the Facility and history details for the project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track the request.
   * @param projectId - Project Id.
   * 
   * @return - Facility and History details if any for the input project.
   */
  public Map<String, Object> findFacilityHistoryDetail(final String userId, final String contextId,
      final Long projectId) {
    logger.info("Entering into getApplicantDetails() Context Id {} ", contextId);
    try {
      return retrieveFacilityInfoWithHistory(userId, contextId, projectId);
    } catch (NoDataFoundException | DartDBException e) {
      throw e;
    } catch (Exception nfe) {
      throw new DartDBException("RETRIEVE_FAC_AND_HIST_ERR", 
          "General error while retreving the Facility and History details", nfe);
    }
  }

  /**
   * Retrieve the region for the input user id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Region Id.
   */
  public Long getUserRegionId(final String userId, final String contextId) {
    logger.info("Entering into getUserRegionId(). User Id {}, Context Id {}", userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);

      getRegionIdByUserIdProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(DartDBConstants.REGION_ID, Types.BIGINT),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      Map<String, Object> response = getRegionIdByUserIdProcCall.execute(inputParam);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {} {} {} {}", statusCode,
          statusMessage, response.get(STATUS_CODE), response.get(DartDBConstants.REGION_ID));
      if (statusCode == 0) {
        logger.info("Exiting from getUserRegionId User Id: {}, Context Id {}", userId, contextId);
        return (Long) response.get(DartDBConstants.REGION_ID);
      } else if (statusCode == -100) {
        throw new DartDBException("USER_REGION_RETRIEVAL_ERR", "Error while retrieving the Region ID by User Id " + statusMessage);
      } else {
        throw new DartDBException("USER_REGION_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received Unsuccessful error while retrieving the Region Id for the input user " + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("USER_REGION_RETRIEVAL_GENERAL_ERR", 
          "General error occurred while retriving the User's region id ", e);
    }
  }

  /**
   * Retrieve all the DEC Staffs for the input Role type.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param regionId - region Id.
   * @param roleTypeId - Role type id. Valid values are 50 - DEC Analyst and 55 - Program Area Reviewer.
   * 
   * @return - List of DEC Staffs if any.
   */
  @SuppressWarnings("unchecked")
  public List<RegionUserEntity> findAllTheUsersByRoleTypeId(final String userId,
      final String contextId, final Integer regionId, final Integer roleTypeId) {

    logger.info("Entering into findAllTheUsersByRegionId(). User Id {} Context Id {}", userId,
        contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.REGION_ID, regionId);
      inputParam.put(DartDBConstants.ROLE_TYPE_ID, roleTypeId);

      getUsersByRoleTypeIdProcCall
          .declareParameters(
              new SqlParameter(DartDBConstants.REGION_ID, Types.INTEGER),
              new SqlParameter(DartDBConstants.ROLE_TYPE_ID, Types.INTEGER),
              new SqlOutParameter(DartDBConstants.P_REGION_USER_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.P_REGION_USER_CURSOR,
              BeanPropertyRowMapper.newInstance(RegionUserEntity.class));

      Map<String, Object> response = getUsersByRoleTypeIdProcCall.execute(inputParam);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);

      if (statusCode == 0) {
        return (List<RegionUserEntity>) response.get(DartDBConstants.P_REGION_USER_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("DEC_STAFF_RETRIEVAL_ERR", 
            "Error while retrieving the User detials for the roleType id "+ roleTypeId + " " + statusMessage);
      } else {
        throw new DartDBException("DEC_STAFF_RETRIEVAL_INVALID_STATUS_ERR", "Unexpected DB error received while "
            + "retrieving the User details for the Role Type id " + roleTypeId);
      }      
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("DEC_STAFF_RETRIEVAL_GENERAL_ERR", 
          "General error while retrieving the DEC users for the input role ", e);
    }
  }

  /**
   * Retrieve all the DEC Staffs(DEP Analyst and Program Area Reviewer) who has valid user Id and email address.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of all the valid DEC Staffs.
   */
  @SuppressWarnings("unchecked")
  public List<RegionUserEntity> findAllTheUsersWithValidEmailAddress(final String userId,
      final String contextId) {

    logger.info("Entering into findAllTheUsersWithValidEmailAddress(). User Id {} Context Id {}",
        userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      getUsersWithValidEmailProcCall
          .declareParameters(
              new SqlOutParameter(DartDBConstants.P_EMAIL_USER_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.P_EMAIL_USER_CURSOR,
              BeanPropertyRowMapper.newInstance(RegionUserEntity.class));

      Map<String, Object> response = getUsersWithValidEmailProcCall.execute(inputParam);
      Long statusCode = (Long) response.get(STATUS_CODE);
      String statusMessage = (String) response.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);

      if (statusCode == 0) {
        return (List<RegionUserEntity>) response.get(DartDBConstants.P_EMAIL_USER_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("STAFF_EMAIL_ERR",
            "Error while retrieving the valid Users with email address " + statusMessage);
      } else {
        throw new DartDBException("STAFF_EMAIL_INVALID_STATUS_ERR", "Received Unsuccessful status received while "
            + "receiving the User details with the valid email address " + statusMessage);
      }
    } catch (DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("STAFF_EMAIL_GENERAL_ERR", "Unexpected DB error received while "
          + "receiving the User details with the valid email address ", e);
    }
  }

  /**
   * Retrieve the staff details from enterprise database for the requested user id..
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @return - Returns the staff details for the input user id.
   */
  @SuppressWarnings("unchecked")
  public List<RegionUserEntity> retrieveStaffDetailsByUserId(String userId, String contextId) {
    logger.info("Entering into retrieveStaffDetailsByUserId() User Id {},  Context Id: {}", userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackStaffDetailsProcCall
          .declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.STAFF_DETAILS_CURSOR,
              BeanPropertyRowMapper.newInstance(RegionUserEntity.class));

      Map<String, Object> result = eTrackStaffDetailsProcCall.execute(inputParam);
      logger.debug("Raw data received as procedure output {}", result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from retrieve Get Staff details for the User Id: {}, Context ID {}",
            userId, contextId);
        return (List<RegionUserEntity>) result.get(DartDBConstants.STAFF_DETAILS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("STAFF_DETAIL_RETRIEVAL_ERR", "Error while retrieving the staff details " + statusMessage);
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_USER_FOUND", statusMessage);
      } else {
        throw new DartDBException("STAFF_DETAIL_RETRIEVAL_INVALID_STATUS_ERR", 
            "Received Unsuccessful status received while retrieving the staff details " + statusMessage);
      }
    } catch (NoDataFoundException | DartDBException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("STAFF_DETAIL_RETRIEVAL_GENERAL_ERR", 
          "General error Occurred while retrieving the Staff details for the user Id" + userId, e);
    }
  }

  /**
   * Retrieve all the support data (Pending applications, Facility, LRP and Active authorization applications to display to the user for DIMSR.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param decId - DEC ID
   * 
   * @return - Returns all the details to support DIMSR activity. 
   */
  public Map<String, Object> retrieveSupportDetailsForDIMSR(String userId, String contextId,
      String decId) {
    
    logger.info("Entering into retrieveSupportDetailsForDIMSR. User Id {}, Context Id {}", userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(DEC_ID, decId);
      
      eTrackDartDIMSRSupportDetailProcCall
          .declareParameters(
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlParameter(DEC_ID, Types.VARCHAR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.FACILITY_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.EXISTING_APPS_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.P_PENDING_APPS_CURSOR, -10))
          .returningResultSet(DartDBConstants.FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityLRPDetail.class))
              .returningResultSet(DartDBConstants.EXISTING_APPS_CURSOR,
                  BeanPropertyRowMapper.newInstance(DartPermit.class))
                  .returningResultSet(DartDBConstants.P_PENDING_APPS_CURSOR,
                      BeanPropertyRowMapper.newInstance(DartApplication.class));

      Map<String, Object> result = eTrackDartDIMSRSupportDetailProcCall.execute(inputParam);
      logger.debug("Raw data received as procedure output {}", result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from retrieveSupportDetailsForDIMSR for the User Id: {}, Context Id: {}",
            userId, contextId);
        return result;
      } else if (statusCode == -100) {
        throw new DartDBException("DIMSR_PERSIST_ERR", 
            "Error while retrieving the Active Authorization for DIMSR application " + statusMessage);
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_FACILITY_FOUND" , statusMessage);
      } else {
        throw new DartDBException("DIMSR_PERSIST_INVALID_STATUS_ERR", 
            "Received Unsucecesful status retrieving the Active Authorization for DIMSR application " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("DIMSR_PERSIST_GENERAL_ERR", 
          "General error occurred while retrieving the Active Authorization for DIMSR application", e);
    }
  }

  /**
   * Retrieve all the Permit Application forms for the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id
   * 
   * @return - returns the list of Application form(s).
   */
  @SuppressWarnings("unchecked")
  public List<ApplicationPermitForm> retrievePermitApplicationForm(String userId, String contextId,
     final Long projectId) {
    
    logger.info("Entering into Retrieve the Application Permit Forms for the project Id {} "
        + ". User Id {}, Context Id {}", projectId, userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(DartDBConstants.PROJECT_ID, projectId);
      
      eTrackPermitFormsProcCall
          .declareParameters(
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlParameter(DartDBConstants.PROJECT_ID, Types.BIGINT),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.REQD_PERMIT_FORMS_CURSOR, Types.REF_CURSOR))
          .returningResultSet(DartDBConstants.REQD_PERMIT_FORMS_CURSOR,
              BeanPropertyRowMapper.newInstance(ApplicationPermitForm.class));

      Map<String, Object> result = eTrackPermitFormsProcCall.execute(inputParam);
      logger.debug("Raw data received as eTrackPermitFormsProcCall procedure output {}", result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from Retrieve the Application "
            + "Permit Form User Id {}, Context Id {}", userId, contextId);
        return  (List<ApplicationPermitForm>)result.get(DartDBConstants.REQD_PERMIT_FORMS_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("PERMIT_APPLN_FORM_RETRIEVAL_ERR", 
            "Error while retrieving the Application Permit Form details " + statusMessage);
      } else {
        throw new DartDBException("PERMIT_APPLN_FORM_RETRIEVAL_ERR", 
            "Received unsuccesful error while retrieving PERMIT Forms " + statusMessage);
      }
    } catch (Exception e) {
      throw new DartDBException("PERMIT_APPLN_FORM_RETRIEVAL_GENERAL_ERR", 
          "General error occurred while retrieving PERMIT Forms ", e);
    }
  }
  
  /**
   * Retrieve all the Facility, LRP, Pending and Renewal eligible applications for the input project id.
   * 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Returns all the details to support enterprise data to display in the virtual workspace. 
   */
  public Map<String, Object> retrieveEnterpriseSupportDetailsForVW(final String userId, final String contextId,
      final Long projectId) {
    
    logger.info("Entering into retrieveEnterpriseSupportDetailsForVW. User Id {}, Context Id {}", userId, contextId);
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(PROJECT_ID, projectId);
      
      enterpriseSupportDetailsProcCall
          .declareParameters(
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlParameter(PROJECT_ID, Types.BIGINT),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.PUBLIC_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.EXISTING_APPS_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.P_PENDING_APPS_CURSOR, -10),
              new SqlOutParameter(DartDBConstants.APPLICATION_CURSOR, -10))
          .returningResultSet(DartDBConstants.PUBLIC_CURSOR,
              BeanPropertyRowMapper.newInstance(PublicAndFacilityDetail.class))
              .returningResultSet(DartDBConstants.EXISTING_APPS_CURSOR,
                  BeanPropertyRowMapper.newInstance(DartPermit.class))
                  .returningResultSet(DartDBConstants.P_PENDING_APPS_CURSOR,
                      BeanPropertyRowMapper.newInstance(DartApplication.class)) 
                      .returningResultSet(DartDBConstants.APPLICATION_CURSOR,
                          BeanPropertyRowMapper.newInstance(Application.class));

      Map<String, Object> result = enterpriseSupportDetailsProcCall.execute(inputParam);
      logger.debug("Enterprise data like LRP, Facility details, Pending and Renewal application details output {}", result);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);
      logger.debug("Enterprise data like LRP, Facility details, Pending and Renewal application details status code: {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from enterpriseSupportDetailsProcCall for the User Id: {}, Context Id: {}",
            userId, contextId);
        return result;
      } else if (statusCode == 100) {
        throw new NoDataFoundException("NO_PROJECT_FOUND_ETRACK" , statusMessage);
      } else if (statusCode == 200) {
        throw new NoDataFoundException("NO_PROJECT_FOUND_EDB", statusMessage);
      } else {
        throw new DartDBException("RETRIEVE_ENTERPRISE_DATA_ERR", 
            "Error while retrieving the enterprise details like LRPs, Facility, Pending and Active Authorization applications. " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new DartDBException("RETRIEVE_ENTERPRISE_GENERAL_ERR", 
          "General error occurred while retrieving the enterprise details like LRPs, Facility, Pending and Active Authorization applications.", e);
    }
  }  
}
