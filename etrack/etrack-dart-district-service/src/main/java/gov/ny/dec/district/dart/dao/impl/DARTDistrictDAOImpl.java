package gov.ny.dec.district.dart.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import gov.ny.dec.district.dart.dao.DARTDistrictDAO;
import gov.ny.dec.district.dart.entity.ApplicationNarrativeDetail;
import gov.ny.dec.district.dart.entity.District;
import gov.ny.dec.district.exception.DARTDistrictServiceException;
import gov.ny.dec.district.exception.ValidationException;

@Repository
public class DARTDistrictDAOImpl implements DARTDistrictDAO {

  @Autowired
  private SimpleJdbcCall decIDSearchJdbcCall;

  @Autowired
  private SimpleJdbcCall facilityNameSearchJdbcCall;
  
  @Autowired
  @Qualifier("eTrackDartPermitNarrativeDescProcCall")
  private SimpleJdbcCall eTrackDartPermitNarrativeDescProcCall;

  @Autowired
  @Qualifier("eTrackUploadToDartProcCall")
  private SimpleJdbcCall eTrackUploadToDartProcCall;
  
  @Autowired
  @Qualifier("eTrackUploadDIMSRDetailProcCall")
  private SimpleJdbcCall eTrackUploadDIMSRDetailProcCall;

  @Autowired
  @Qualifier("eTrackDartMilestoneRefreshProcCall")
  private SimpleJdbcCall eTrackDartMilestoneRefreshProcCall;

  @Autowired
  @Qualifier("eTrackDartAddAdditionalPermitProcCall")
  private SimpleJdbcCall eTrackDartAddAdditionalPermitProcCall;
  
  private static final Logger logger = LoggerFactory.getLogger(DARTDistrictDAOImpl.class.getName());

  private static final String DISTRICT_SEARCH_CURSOR_NAME = "CUR_FACILITY";
  private static final String P_FACILITY_SEARCH = "P_FACILITY_SEARCH";
  private static final String P_SEARCH_TYPE = "P_SEARCH_TYPE";
  private static final String P_DEC_ID = "P_DEC_ID";
  private static final String P_USER_ID = "p_user_id";
  private static final String P_DISTRICT_ID = "p_district_id";
  private static final String P_STATUS_CODE = "p_status_cd";
  private static final String P_STATUS_MESSAGE = "p_status_msg";
  private static final String P_APPL_NARRATIVE_CALL = "p_narrative_html_cur";
  private static final String P_PROJECT_ID = "p_project_id";
  private static final String P_USER_GUID = "p_user_guid";

  @SuppressWarnings("unchecked")
  @Override
  public List<District> searchDistrictDetailByDecId(final String userId, final String contextId, final String decId) {
    logger.info("Entering searchDistrictDetailByDecId(). Dec: {}, User Id {}, Context Id {}", decId, userId, contextId);
    logger.info("Search by the DEC ID {}", decId);
    decIDSearchJdbcCall.declareParameters(new SqlParameter(P_DEC_ID, Types.VARCHAR),
        new SqlOutParameter(DISTRICT_SEARCH_CURSOR_NAME, Types.REF_CURSOR)).returningResultSet(
            DISTRICT_SEARCH_CURSOR_NAME, BeanPropertyRowMapper.newInstance(District.class));

    Map<String, Object> result = decIDSearchJdbcCall.execute(decIDSearchInputValue(decId));
    return (List<District>) result.get(DISTRICT_SEARCH_CURSOR_NAME);
  }

  private Map<String, String> decIDSearchInputValue(String decId) {
    Map<String, String> valueMap = new HashMap<>();
    valueMap.put(P_DEC_ID, decId);
    return valueMap;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<District> searchDistrictDetailByFacilityName(final String userId, final String contextId, 
      final String facilityName, final String searchType) {
    
    logger.info("Entering searchDistrictDetailByFacilityName(). User Id {}, Context Id {}, "
        + "Facility name: {}, Search Type {}", userId, contextId, facilityName, searchType);
    facilityNameSearchJdbcCall.declareParameters(
        new SqlParameter(P_FACILITY_SEARCH, Types.VARCHAR),
        new SqlParameter(P_SEARCH_TYPE, Types.VARCHAR),
        new SqlOutParameter(DISTRICT_SEARCH_CURSOR_NAME, Types.REF_CURSOR)).returningResultSet(
            DISTRICT_SEARCH_CURSOR_NAME, BeanPropertyRowMapper.newInstance(District.class));
    Map<String, Object> result =
        facilityNameSearchJdbcCall.execute(facilityNameSearchInputValue(facilityName, searchType));
    logger.info("Exiting from searchDistrictDetailByFacilityName(). User Id {}, Context Id {}, "
        + "Facility name: {}, Search Type {}", userId, contextId, facilityName, searchType);
    return (List<District>) result.get(DISTRICT_SEARCH_CURSOR_NAME);
  }

  private Map<String, String> facilityNameSearchInputValue(String facilityName, String searchType) {
    Map<String, String> valueMap = new HashMap<>();
    valueMap.put(P_FACILITY_SEARCH, facilityName);
    valueMap.put(P_SEARCH_TYPE, searchType);
    return valueMap;
  }

  /**
   * This method is used to retrieve all the Applications permit for the Existing Authorization application(s).
   * 
   * @param userId - Unique user id initiated for this request.
   * @param contextId - Unique UUID to track this transaction
   * @param districtId - District Id
   * 
   * @return - returns the list of Application Narrative text.
   */
  @Transactional
  @SuppressWarnings("unchecked")
  public List<ApplicationNarrativeDetail> retrieveApplicationPermitDescNarrative(
      final String userId, final String contextId, final Long districtId) {
    
    logger.info("Entering into Retrieve the Application "
        + "Permit Description Narrative . User Id {}, Context Id {}", userId, contextId);
    Map<String, Object> result = null;
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(P_USER_ID, userId);
    inputParam.put(P_DISTRICT_ID, districtId);
    
    eTrackDartPermitNarrativeDescProcCall
        .declareParameters(
            new SqlParameter(P_USER_ID, Types.VARCHAR),
            new SqlParameter(P_DISTRICT_ID, Types.BIGINT),
            new SqlOutParameter(P_STATUS_CODE, Types.BIGINT),
            new SqlOutParameter(P_STATUS_MESSAGE, Types.VARCHAR),
            new SqlOutParameter(P_APPL_NARRATIVE_CALL, Types.REF_CURSOR))
        .returningResultSet(P_APPL_NARRATIVE_CALL,
            BeanPropertyRowMapper.newInstance(ApplicationNarrativeDetail.class));

    result = eTrackDartPermitNarrativeDescProcCall.execute(inputParam);
    logger.debug("Raw data received as eTrackDartPermitNarrativeDescProcCall procedure output {}", result);
    Long statusCode = (Long) result.get(P_STATUS_CODE);
    String statusMessage = (String) result.get(P_STATUS_MESSAGE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode.equals(0L)) {
      logger.info("Exiting from Retrieve the Application "
          + "Permit Description Narrative. User Id {}, Context Id {}", userId, contextId);
      return  (List<ApplicationNarrativeDetail>)result.get(P_APPL_NARRATIVE_CALL);
    } else if (statusCode.equals(-100L)) {
      throw new DARTDistrictServiceException("Error while retrieving the Application Permit Description Narrative details " + statusMessage);
    } else if (statusCode.equals(1L) || statusCode.equals(2L)) {
      throw new ValidationException(statusMessage,
          "There is no data associated with this " + userId);
    } else {
      throw new DARTDistrictServiceException("Unexpected DB error received " + statusMessage);
    }
  }

  
  @Override
  public void uploadDIMSRApplicationDetailsToDart(final String userId, final String contextId, final Long projectId,
      final String guid) {
    
    logger.info("Entering into uploadDIMSRApplicationDetailsToDart. User Id : {}, Context Id : {}", userId, contextId);
    try {
      Map<String, Object> result = null;
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(P_USER_ID, userId);
      inputParam.put(P_PROJECT_ID, projectId);
      inputParam.put(P_USER_GUID, guid);
      
      eTrackUploadDIMSRDetailProcCall
          .declareParameters(
              new SqlParameter(P_USER_ID, Types.VARCHAR),
              new SqlParameter(P_USER_GUID, Types.VARCHAR),
              new SqlParameter(P_PROJECT_ID, Types.BIGINT),
              new SqlOutParameter(P_STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(P_STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackUploadDIMSRDetailProcCall.execute(inputParam);
      logger.debug("Raw data received as uploadDIMSRApplicationDetailsToDart procedure output {}", result);
      Long statusCode = (Long) result.get(P_STATUS_CODE);
      String statusMessage = (String) result.get(P_STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode.equals(0L)) {
        logger.info("Exiting from Upload the DIMSR Application to DART. "
            + " User Id {}, Context Id {}", userId, contextId);
      } else if (statusCode.equals(100L)) {
        logger.error("Missing some data to upload this DIMSR applicaiton. User Id {}, Context Id {}, Status {}", userId, contextId, statusMessage);
        throw new DARTDistrictServiceException(HttpStatus.BAD_REQUEST, statusMessage);
      } else if (statusCode.equals(-100L)) {
        throw new DARTDistrictServiceException("Error while uploading the DIMSR application details " + statusMessage);
      } else {
        throw new DARTDistrictServiceException("Unexpected DB error received while uploading DIMSR application " + statusMessage);
      }
    } catch (ValidationException | DARTDistrictServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new DARTDistrictServiceException("Error while uploading the DIMSR application details", e);
    }
  }

  @Override
  public void uploadETrackDataToDart(String userId, String contextId, Long projectId, String guid) {
    
    logger.info("Entering into uploadETrackDataToDart. User Id : {}, Context Id : {}", userId, contextId);
    try {
      Map<String, Object> result = null;
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(P_USER_ID, userId);
      inputParam.put(P_PROJECT_ID, projectId);
      inputParam.put(P_USER_GUID, guid);
      eTrackUploadToDartProcCall
          .declareParameters(
              new SqlParameter(P_USER_ID, Types.VARCHAR),
              new SqlParameter(P_USER_GUID, Types.VARCHAR),
              new SqlParameter(P_PROJECT_ID, Types.BIGINT),
              new SqlOutParameter(P_STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(P_STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackUploadToDartProcCall.execute(inputParam);
      logger.debug("Raw data received as uploadETrackDataToDart procedure output {}", result);
      Long statusCode = (Long) result.get(P_STATUS_CODE);
      String statusMessage = (String) result.get(P_STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode.equals(0L)) {
        logger.info("Exiting from Upload the eTrack Application data to DART. "
            + " User Id {}, Context Id {}", userId, contextId);
      } else if (statusCode > 0) {
        logger.error("eTrack Project data is missing some details to upload to DART. "
            + "User Id {}, Context Id {}, Status Code {} ", userId, contextId, statusMessage);
        throw new DARTDistrictServiceException(HttpStatus.BAD_REQUEST, statusMessage);
      } else if (statusCode.equals(-100l)) {
        throw new DARTDistrictServiceException("Error while uploading the eTrack Application data to DART details " + statusMessage);
      } else {
        throw new DARTDistrictServiceException("Unexpected DB error received while uploading the eTrack Application data " + statusMessage);
      }
    } catch (ValidationException | DARTDistrictServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new DARTDistrictServiceException("Error while uploading the eTrack Application data ", e);
    }
  }

  @Override
  public void refreshMilestone(final String userId, final String contextId, final Long projectId) {
    logger.info("Refreshing the Project application status (loading the updated status details) into eTrack.");
    try {
      Map<String, Object> inputParam = new HashMap<>();
      if (StringUtils.hasLength(userId)) {
        inputParam.put(P_USER_ID, userId);
      } else {
        inputParam.put(P_USER_ID, "SYSTEM");
      }
      inputParam.put(P_PROJECT_ID, projectId);
      
      eTrackDartMilestoneRefreshProcCall
          .declareParameters(
              new SqlParameter(P_USER_ID, Types.VARCHAR),
              new SqlParameter(P_PROJECT_ID, Types.BIGINT),
              new SqlOutParameter(P_STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(P_STATUS_MESSAGE, Types.VARCHAR));

      Map<String, Object> result = eTrackDartMilestoneRefreshProcCall.execute(inputParam);
      logger.debug("Raw data received as refershMilestone procedure output {}", result);
      Long statusCode = (Long) result.get(P_STATUS_CODE);
      String statusMessage = (String) result.get(P_STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed in refershMilestone {}", statusCode);
      if (statusCode == 0) {
        logger.info("Exiting from refershMilestone. Context Id {}", contextId);
      } else if (statusCode == 100) {
        throw new ValidationException("PROJECT_ID_MISSING", statusMessage);
      } else if (statusCode == -100) {
        throw new DARTDistrictServiceException(
            "Error while refresh the Milestone details " + statusMessage);
      } else {
        throw new DARTDistrictServiceException(
            "Unexpected DB error received while refresh the Milestone details " + statusMessage);
      }
    } catch (ValidationException | DARTDistrictServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new DARTDistrictServiceException("Error while refresh the Project milestone details ", e);
    }
    logger.info("Refreshed the Project application status (loading the updated status details) into eTrack.");
  }

  @Override
  public void addAdditionalPermitToDart(final String userId, final String contextId, final Long projectId,
      final Long applId, final String guid) {
    logger.info("Entering into uploadETrackDataToDart. User Id : {}, Context Id : {}", userId, contextId);
    try {
      Map<String, Object> result = null;
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(P_USER_ID, userId);
      inputParam.put("p_appl_id", applId);
      inputParam.put(P_USER_GUID, guid);
      eTrackDartAddAdditionalPermitProcCall
          .declareParameters(
              new SqlParameter(P_USER_ID, Types.VARCHAR),
              new SqlParameter(P_USER_GUID, Types.VARCHAR),
              new SqlParameter("p_appl_id", Types.BIGINT),
              new SqlOutParameter(P_STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(P_STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackDartAddAdditionalPermitProcCall.execute(inputParam);
      logger.debug("Raw data received as addAdditionalPermitToDart procedure output {}", result);
      Long statusCode = (Long) result.get(P_STATUS_CODE);
      String statusMessage = (String) result.get(P_STATUS_MESSAGE);
      logger.debug("Raw data received as procedure output after processed {}", statusCode);
      if (statusCode.equals(0L)) {
        logger.info("Exiting from Add additional application into DART. "
            + " User Id {}, Context Id {}", userId, contextId);
      } else if (statusCode.equals(-100l)) {
        throw new DARTDistrictServiceException(
            HttpStatus.BAD_REQUEST,  "Error while Adding additional application to DART details " + statusMessage);
      } else if (statusCode.equals(200l)) {
        throw new DARTDistrictServiceException(
            HttpStatus.IM_USED,  "There is a Build  while Adding additional application to DART details. " + statusMessage);
      } else {
        throw new DARTDistrictServiceException("Unexpected DB error received "
            + "while Adding additional application data " + statusMessage);
      }
    } catch (DARTDistrictServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new DARTDistrictServiceException("Error while Adding additional application data " + applId, e);
    }
  }
}
