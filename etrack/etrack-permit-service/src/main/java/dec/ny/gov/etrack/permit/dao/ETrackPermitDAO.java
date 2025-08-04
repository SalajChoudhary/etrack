package dec.ny.gov.etrack.permit.dao;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.APPLICANT_TYPE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.CITY;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.DEC_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.DISTRICT_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.EDB_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.EDB_PUBLIC_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.ETRACK_PUBLIC_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.FACILITY_CURSOR;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.FACILITY_NAME;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.LOC_DIRECTIONS;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.MAIL_IN_IND;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.POLYGON_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PROJECT_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PUBLIC_CURSOR;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STAFF_DETAILS_CURSOR;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_CODE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_MESSAGE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.USER_ID;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.ZIP;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.ZIP_EXTN;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.CATEGORY;

import java.sql.Types;
import java.util.Date;
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
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.entity.FacilityDetail;
import dec.ny.gov.etrack.permit.entity.PublicDetail;
import dec.ny.gov.etrack.permit.entity.RegionUserEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.ProjectDetail;

@Repository
public class ETrackPermitDAO {

  @Autowired
  @Qualifier("populateFacilityProcCall")
  private SimpleJdbcCall populateFacilityProcCall;

  @Autowired
  @Qualifier("updateFacilityProcCall")
  private SimpleJdbcCall updateFacilityProcCall;

  @Autowired
  @Qualifier("eTrackFacilityInfoProc")
  private SimpleJdbcCall eTrackFacilityInfoProc;

  @Autowired
  @Qualifier("eTrackDeleteProjectCall")
  private SimpleJdbcCall eTrackDeleteProjectCall;

  @Autowired
  @Qualifier("eTrackPopulatePublicInfoProc")
  private SimpleJdbcCall eTrackPopulateApplicantProc;

  @Autowired
  @Qualifier("eTrackStaffDetailsProcCall")
  private SimpleJdbcCall eTrackStaffDetailsProcCall;

  @Autowired
  @Qualifier("eTrackPopulateFacilityHistProcCall")
  private SimpleJdbcCall eTrackPopulateFacilityHistProcCall;

  @Autowired
  @Qualifier("eTrackPopulatePublicHistProcCall")
  private SimpleJdbcCall eTrackPopulatePublicHistProcCall;

  @Autowired
  @Qualifier("eTrackGetUserDetailsProcCall")
  private SimpleJdbcCall eTrackGetUserDetailsProcCall;

  @Autowired
  @Qualifier("eTrackUpdateOriginalSubmittalIndProcCall")
  private SimpleJdbcCall eTrackUpdateOriginalSubmittalIndProcCall;

  @Autowired
  @Qualifier("eTrackDARTPermitAndAppTypeValidProcCall")
  private SimpleJdbcCall eTrackDARTPermitAndAppTypeValidProcCall;

  @Autowired
  @Qualifier("eTrackDARTRelatedRegularMappingProcCall")
  private SimpleJdbcCall eTrackDARTRelatedRegularMappingProcCall;
  
  /**
   * Logging
   */
  private static final Logger logger = LoggerFactory.getLogger(ETrackPermitDAO.class.getName());

  /**
   * This method is used to persist the Facility selected by the user.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique transaction id to track the request.
   * @param projectDetail - Project detail includes the facility chose by the user.
   * @param receivedDate - Application received date.
   * 
   * @return - Project details along with successfully created project id.
   */
  public ProjectDetail saveProjectDetails(String userId, String contextId,
      ProjectDetail projectDetail, Date receivedDate) {
    logger.info("Entering into saveProjectDetails User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(DISTRICT_ID, projectDetail.getFacility().getEdbDistrictId());
    inputParam.put(PROJECT_ID, projectDetail.getProjectId());
    inputParam.put(MAIL_IN_IND, projectDetail.getMailInInd());
    inputParam.put(APPLICANT_TYPE, projectDetail.getApplicantTypeCode());
    inputParam.put(POLYGON_ID, projectDetail.getPolygonId());
    inputParam.put(LOC_DIRECTIONS, projectDetail.getLocDirections());
    FacilityAddress facilityAddress = projectDetail.getFacility().getAddress();
    inputParam.put(CITY, facilityAddress.getCity());
    inputParam.put(STATE, facilityAddress.getState());
    inputParam.put(ZIP, facilityAddress.getZip());
    inputParam.put(ZIP_EXTN, facilityAddress.getZipExtension());
    inputParam.put("p_street1", projectDetail.getFacility().getAddress().getStreet1());
    inputParam.put("p_street2", projectDetail.getFacility().getAddress().getStreet2());
    inputParam.put("p_dep_region_id", projectDetail.getRegions());
    inputParam.put("p_counties", projectDetail.getCounties());
    inputParam.put("p_municipalities", projectDetail.getMunicipalities());
    inputParam.put("p_taxmaps", projectDetail.getTaxmaps());
    inputParam.put("p_comments", projectDetail.getReason());
    inputParam.put("p_chg_boundary_reason", projectDetail.getBoundaryChangeReason());
    inputParam.put("p_latitude", projectDetail.getLatitude());
    inputParam.put("p_longitude", projectDetail.getLongitude());
    inputParam.put("p_polygon_type_code", projectDetail.getPolygonStatus().getStatus());
    inputParam.put("p_received_date", receivedDate);
    inputParam.put("p_seqr_ind", projectDetail.getClassifiedUnderSeqr());
    inputParam.put("p_work_area_id", projectDetail.getWorkAreaId());
    inputParam.put("p_nytmn_coordinate", projectDetail.getNytmy());
    inputParam.put("p_nytme_coordinate", projectDetail.getNytmx());
    inputParam.put("p_primary_region", projectDetail.getPrimaryRegion());
    inputParam.put("p_primary_municipality", projectDetail.getPrimaryMunicipality());
    inputParam.put(USER_ID, userId);

    Map<String, Object> result = null;
    try {
      populateFacilityProcCall.declareParameters(new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlParameter(DISTRICT_ID, Types.INTEGER),
          new SqlParameter(MAIL_IN_IND, Types.VARCHAR),
          new SqlParameter(APPLICANT_TYPE, Types.VARCHAR),
          new SqlParameter(POLYGON_ID, Types.VARCHAR),
          new SqlParameter(LOC_DIRECTIONS, Types.VARCHAR), new SqlParameter(CITY, Types.VARCHAR),
          new SqlParameter(STATE, Types.VARCHAR), new SqlParameter(ZIP, Types.VARCHAR),
          new SqlParameter(ZIP_EXTN, Types.VARCHAR), new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter("p_dep_region_id", Types.VARCHAR),
          new SqlParameter("p_counties", Types.VARCHAR),
          new SqlParameter("p_municipalities", Types.VARCHAR),
          new SqlParameter("p_taxmaps", Types.VARCHAR),
          new SqlParameter("p_comments", Types.VARCHAR),
          new SqlParameter("p_chg_boundary_reason", Types.VARCHAR),
          new SqlParameter("p_latitude", Types.VARCHAR),
          new SqlParameter("p_longitude", Types.VARCHAR),
          new SqlParameter("p_street1", Types.VARCHAR),
          new SqlParameter("p_street2", Types.VARCHAR),
          new SqlParameter("p_seqr_ind", Types.VARCHAR),
          new SqlParameter("p_work_area_id", Types.VARCHAR),
          new SqlParameter("p_polygon_type_code", Types.BIGINT),
          new SqlParameter("p_received_date", Types.DATE),
          new SqlParameter("p_nytmn_coordinate", Types.DECIMAL),
          new SqlParameter("p_nytme_coordinate", Types.DECIMAL),
          new SqlParameter("p_primary_region", Types.VARCHAR),
          new SqlParameter("p_primary_municipality", Types.VARCHAR),
          new SqlOutParameter("p_out_project_id", Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT));
      result = populateFacilityProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("POPULATE_FACILITY_DB_ERROR",
          "Error from Database while populating the project/facility details. ",
          e);
    }

    logger.info("Raw data received as procedure output {}", result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    logger.info("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode > 0) {
      throw new BadRequestException("POPULATE_FACILITY_DATA_ERROR",
          "Unable to process the request due to some data issue "
              + "while populating the project details. Error message "
              + result.get(STATUS_MESSAGE), projectDetail.getFacilityId());
    } else if (statusCode < 0) {
      throw new ETrackPermitException("POPULATE_FACILITY_DB_ERROR",
          "Received Unsuccessful status code recieved from the DB "
              + "while populating the project details. DB Status message "
              + result.get(STATUS_MESSAGE));
    }
    projectDetail.setProjectId((Long) result.get("p_out_project_id"));

    logger.info("Exiting from saveProjectDetails User Id: {}, Context ID {}", userId, contextId);
    return projectDetail;
  }

  /**
   * This method is used to update the existing facility/project details.
   * 
   * @param userId - User id who initiates this request
   * @param contextId - Unique transaction id
   * @param projectDetail - Updated Project details
   * @param receivedDate - Project received date.
   * 
   * @return - Updated Project details.
   */
  public ProjectDetail updateProjectDetails(String userId, String contextId,
      ProjectDetail projectDetail, Date receivedDate) {
    logger.info("Entering into updateProjectDetails User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(PROJECT_ID, projectDetail.getProjectId());
    inputParam.put(FACILITY_NAME, projectDetail.getFacility().getFacilityName());
    inputParam.put(POLYGON_ID, projectDetail.getPolygonId());
    inputParam.put(LOC_DIRECTIONS, projectDetail.getLocDirections());
    FacilityAddress facilityAddress = projectDetail.getFacility().getAddress();
    inputParam.put(CITY, facilityAddress.getCity());
    inputParam.put(STATE, facilityAddress.getState());
    inputParam.put(ZIP, facilityAddress.getZip());
    inputParam.put("p_counties", projectDetail.getCounties());
    inputParam.put("p_municipalities", projectDetail.getMunicipalities());
    inputParam.put("p_taxmaps", projectDetail.getTaxmaps());
    inputParam.put("p_polygon_type_code", projectDetail.getPolygonStatus().getStatus());
    inputParam.put("p_comments", projectDetail.getReason());
    inputParam.put("p_chg_boundary_reason", projectDetail.getBoundaryChangeReason());
    inputParam.put("p_latitude", projectDetail.getLatitude());
    inputParam.put("p_longitude", projectDetail.getLongitude());
    inputParam.put(USER_ID, userId);
    inputParam.put("p_street1", facilityAddress.getStreet1());
    inputParam.put("p_street2", facilityAddress.getStreet2());
    inputParam.put("p_received_date", receivedDate);
    inputParam.put("p_work_area_id", projectDetail.getWorkAreaId());
    inputParam.put("p_nytmn_coordinate", projectDetail.getNytmy());
    inputParam.put("p_nytme_coordinate", projectDetail.getNytmx());
    inputParam.put("p_dep_region_id", projectDetail.getRegions());
    inputParam.put("p_primary_region", projectDetail.getPrimaryRegion());
    inputParam.put("p_primary_municipality", projectDetail.getPrimaryMunicipality());
    inputParam.put("p_mode", projectDetail.getMode());
    inputParam.put("geometry_change_ind", projectDetail.getHasSameGeometry());

    Map<String, Object> result = null;
    try {
      updateFacilityProcCall.declareParameters(new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlParameter(FACILITY_NAME, Types.VARCHAR),
          new SqlParameter(POLYGON_ID, Types.VARCHAR),
          new SqlParameter(LOC_DIRECTIONS, Types.VARCHAR), new SqlParameter(CITY, Types.VARCHAR),
          new SqlParameter(STATE, Types.VARCHAR), new SqlParameter(ZIP, Types.VARCHAR),
          new SqlParameter("p_dep_region_id", Types.VARCHAR),
          new SqlParameter("p_counties", Types.VARCHAR),
          new SqlParameter("p_municipalities", Types.VARCHAR),
          new SqlParameter("p_taxmaps", Types.CLOB),
          new SqlParameter("p_polygon_type_code", Types.BIGINT),
          new SqlParameter("p_latitude", Types.VARCHAR),
          new SqlParameter("p_longitude", Types.VARCHAR), new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter("p_street1", Types.VARCHAR),
          new SqlParameter("p_street2", Types.VARCHAR),
          new SqlParameter("p_received_date", Types.DATE),
          new SqlParameter("p_work_area_id", Types.VARCHAR),
          new SqlParameter("p_nytmn_coordinate", Types.DECIMAL),
          new SqlParameter("p_nytme_coordinate", Types.DECIMAL),
          new SqlParameter("p_comments", Types.VARCHAR),
          new SqlParameter("p_chg_boundary_reason", Types.VARCHAR),
          new SqlParameter("p_primary_region", Types.VARCHAR),
          new SqlParameter("p_primary_municipality", Types.VARCHAR),
          new SqlParameter("p_mode", Types.INTEGER),
          new SqlParameter("geometry_change_ind", Types.INTEGER),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter("p_dec_change_detail", Types.VARCHAR));
      result = updateFacilityProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("UPDATE_PROJ_FACILITY_DB_ERROR",
          "General Error " + "from DB while updating the Project/Facility details.", e);
    }

    logger.info("Raw data received as procedure output {}", result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    logger.info("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode > 0) {
      throw new BadRequestException("UPDATE_PROJ_FACILITY_DB_ERROR",
          "Unable to update the Project/Facility details due to some data issue. "
              + " Error message "
              + result.get(STATUS_MESSAGE), projectDetail.getFacilityId());
    } else if (statusCode < 0) {
      throw new ETrackPermitException("UPDATE_PROJ_FACILITY_DB_ERROR",
          "Received Incorrect status code "
              + "from DB while updating the Project/Facility details. Message "
              + result.get(STATUS_MESSAGE));
    }
    if (projectDetail.getMode() != null && projectDetail.getMode().equals(1)) {
      String decChangeDetails = (String) result.get("p_dec_change_detail");
      if (StringUtils.hasLength(decChangeDetails)) {
        if (projectDetail.getIgnoreDecIdMismatch() == null
            || projectDetail.getIgnoreDecIdMismatch().equals(0)) {
          logger.info("Validating whether any DEC ID mismatch for the input polygon/facility details");
          if (decChangeDetails.startsWith("E:")) {
            throw new BadRequestException("DEC_ID_MATCH_ERROR", decChangeDetails, projectDetail);
          } else if (decChangeDetails.startsWith("Y:")) {
            throw new DataExistException("DEC_ID_MISMATCH", decChangeDetails);
          }
        }
      }
    }
    logger.info("Exiting from updateProjectDetails User Id: {}, Context ID {}", userId, contextId);
    return projectDetail;
  }

  /**
   * This method is used to delete the Un-submitted project.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id
   * @param projectId - Project id to delete
   */
  public void deleteEtrackProject(final String userId, final String contextId,
      final Long projectId) {
    logger.info("Entering into deleteEtrackProject User Id: {}, Context Id {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(PROJECT_ID, projectId);
      inputParam.put(USER_ID, userId);
      eTrackDeleteProjectCall.declareParameters(new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlParameter(USER_ID, Types.VARCHAR), new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackDeleteProjectCall.execute(inputParam);
      logger.debug("Raw data received as procedure output {}", result);
    } catch (Exception e) {
      throw new ETrackPermitException("PROJECT_DELETE_DB_ERROR",
          "Error " + "from DB while deleting the projecct. Error Details ", e);
    }

    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);
    if (statusCode == 0) {
      logger.info("Exiting from deleteEtrackProject User Id: {}, Context Id {} ", userId,
          contextId);
      return;
    } else if (statusCode == 1) {
      throw new BadRequestException("INVALID_PROJ_ID", "Submitted Project cannot be deleted. ",
          projectId);
    } else if (statusCode == 3) {
      throw new BadRequestException("INVALID_PROJ_ID",
          "Project is not available to delete the project.", projectId);
    } else {
      throw new ETrackPermitException("PROJECT_DELETE_DB_ERROR", "Received incorrect/unsuccesful "
          + "status code from DB while deleting the projecct. Error Details " + statusMessage);
    }
  }

  /**
   * This method is used to retrieve the Facility details for the input project Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id
   * @param projectId - Project Id
   * @return - Facility details along with all the DB transaction status.
   * 
   */
  public Map<String, Object> getETrackFacility(String userId, String contextId, Long projectId) {
    logger.info("Entering into getETrackFacility User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(PROJECT_ID, projectId);
      inputParam.put(USER_ID, userId);
      eTrackFacilityInfoProc
          .declareParameters(new SqlParameter(DISTRICT_ID, Types.BIGINT),
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(FACILITY_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(PUBLIC_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(PUBLIC_CURSOR, BeanPropertyRowMapper.newInstance(PublicDetail.class))
          .returningResultSet(FACILITY_CURSOR,
              BeanPropertyRowMapper.newInstance(FacilityDetail.class));
      result = eTrackFacilityInfoProc.execute(inputParam);
    } catch (Exception e) {
      logger.error(
          "Error while retrieving the eTrack facility data for the "
              + "Project Id {} user Id: {}, context Id: {}, Error {}:",
          projectId, userId, contextId, e);
      throw new ETrackPermitException("RETRIEVE_FACILITY_DB_ERROR",
          "Error from DB while retrieving etack facility data for the Project id " + projectId, e);
    }

    logger.debug("Raw data received as procedure output {}", result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode > 0) {
      throw new BadRequestException("RETRIEVE_FACILITY_DATA_ERROR",
          "Unable to process the request due to some data issue "
              + "while retrieving the facility details. Error message "
              + result.get(STATUS_MESSAGE), projectId);
    } else if (statusCode < 0) {
      throw new ETrackPermitException("RETRIEVE_FACILITY_DB_ERROR",
          "Received Unsuccessful status code recieved from the DB "
              + "while retrieving the facility details. DB Status message "
              + result.get(STATUS_MESSAGE));
    }
    return result;
  }

  /**
   * This method is used to store the applicant details by retrieving them from enterprise database.
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @param projectId - Project Id.
   * @param edbPublicId - Enterprise Public id.
   * @param category - Applicant Category P - Public, O - Owner and C - Category.
   * 
   * @return - Returns new applicant id if its new applicant. Else returns the existing one
   */
  public Long populateApplicantDetails(String userId, String contextId, Long projectId,
      Long edbPublicId, final String category) {
    logger.info("Entering into populateApplicantDetails() User Id {}, Context Id: {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(EDB_PUBLIC_ID, edbPublicId);
      inputParam.put(PROJECT_ID, projectId);
      inputParam.put(USER_ID, userId);
      inputParam.put(CATEGORY, category);
      eTrackPopulateApplicantProc.declareParameters(new SqlParameter(EDB_PUBLIC_ID, Types.BIGINT),
          new SqlParameter(PROJECT_ID, Types.BIGINT), new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(CATEGORY, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter("p_public_id_out", Types.BIGINT));
      result = eTrackPopulateApplicantProc.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("POPULATE_PUBLIC_DB_ERRROR",
          "Error received from DB while populating Public details ", e);
    }
    logger.debug("Raw data received as populating Public details procedure output {}", result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode == 0) {
      logger.info("Exiting from populateApplicantDetails User Id: {}, Context ID {}", userId,
          contextId);
      return (Long) result.get("p_public_id_out");
    } else if (statusCode == 1 || statusCode == 2) {
      throw new DataNotFoundException("POPULATE_PUBLIC_DB_ERRROR",
          "There is no data associated with this public " + edbPublicId);
    } else {
      throw new ETrackPermitException("POPULATE_PUBLIC_DB_ERRROR",
          "Received Unsucecesful status code from DB while populating the Public details. "
              + "Error details " + statusMessage);
    }
  }


  /**
   * This method is used to retrieve the staff details by the user from enterprise database.
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @return - Returns the staff details for the input user id.
   */
  @SuppressWarnings("unchecked")
  public List<RegionUserEntity> retrieveStaffDetailsByUserId(String userId, String contextId) {
    logger.info("Entering into retrieveStaffDetailsByUserId() Context Id: {}", contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      eTrackStaffDetailsProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR)).returningResultSet(
              STAFF_DETAILS_CURSOR, BeanPropertyRowMapper.newInstance(RegionUserEntity.class));
      result = eTrackStaffDetailsProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("RETRIEVE_STAFF_DB_ERROR",
          "Error from DB while retrieving the Staff details", e);
    }
    logger.debug("Raw data received as procedure output {}", result);
    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode == 0) {
      logger.info("Exiting from retrieve Get Staff details for the User Id: {}, Context ID {}",
          userId, contextId);
      return (List<RegionUserEntity>) result.get(STAFF_DETAILS_CURSOR);
    } else if (statusCode == 1 || statusCode == 2) {
      throw new DataNotFoundException(statusMessage,
          "There is no data associated with this user id " + userId);
    } else {
      throw new ETrackPermitException("RETRIEVE_STAFF_DB_ERROR",
          "Error while retrieving the staff details for input user. Error details" + statusMessage);
    }
  }

  /**
   * This method is used to attach the enterprise public attach to the eTrack if the user added the
   * existing public as a new public in eTrack
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @param publicId - Unique ID associated in eTrack Database
   * @param edbPublicId - Unique ID associated in Enterprise Database
   * @param projectId - Project Id.
   * @param category - Applicant category. P - Public, O-Owner and C- Contact/Agent.
   */
  public void populatePublicDataIntoETrack(final String userId, final String contextId,
      final Long publicId, final Long edbPublicId, final Long projectId, final String category) {

    logger.info("Entering into populatePublicDataIntoETrack User Id: {}, Context Id: {}", userId,
        contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(ETRACK_PUBLIC_ID, publicId);
      inputParam.put(EDB_ID, edbPublicId);

      eTrackPopulatePublicHistProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(ETRACK_PUBLIC_ID, Types.BIGINT), new SqlParameter(EDB_ID, Types.BIGINT),
          new SqlParameter(CATEGORY, Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackPopulatePublicHistProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("POPULATE_PUBLIC_HIST_DB_ERROR",
          "Error from DB while retrieving Public History details from Enterprise", e);
    }

    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);

    logger.info(
        "Status code {} and status message {} while populating the "
            + "Public Data into eTrack. User Id {}, Context Id{}",
        statusCode, statusMessage, userId, contextId);

    if (statusCode == 0) {
      logger.info("Exiting from populatePublicDataIntoETrack for the User Id: {}, Context ID {}",
          userId, contextId);
    } else if (statusCode == 1) {
      throw new DataNotFoundException("POPULATE_PUBLIC_HIST_DB_ERROR",
          "There is no eTrack public data associated with this Public Id " + publicId);
    } else if (statusCode == 3) {
      throw new DataNotFoundException("POPULATE_PUBLIC_HIST_DB_ERROR",
          "There is no public data associated with this EDB Public Id " + edbPublicId);
    } else {
      throw new ETrackPermitException("POPULATE_PUBLIC_HIST_DB_ERROR",
          "Received error while Populating the Public details into eTrack. Error details "
              + statusMessage);
    }
  }


  /**
   * 
   * @param publicId - Unique ID associated in eTrack Database
   * @param edbPublicId - Unique ID associated in Enterprise Database
   */
  /**
   * This method is used to attach the enterprise Facility attach to the eTrack if the user added
   * the existing Facility as a new Facility in eTrack
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @param projectId - Project Id
   * @param decId - Enterprise Standard code associated with the Facility.
   */
  public void populateFaciltyDataIntoETrack(final String userId, final String contextId,
      final Long projectId, final String decId) {

    logger.info("Entering into populateFaciltyDataIntoETrack User Id: {}, Context Id: {}", userId,
        contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put(DEC_ID, decId);
      inputParam.put(PROJECT_ID, projectId);

      eTrackPopulateFacilityHistProcCall.declareParameters(new SqlParameter(USER_ID, Types.VARCHAR),
          new SqlParameter(DEC_ID, Types.VARCHAR), new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      result = eTrackPopulateFacilityHistProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("POPULATE_FACILITY_DB_ERROR",
          "Error from DB while Populating Facility details. Error detail " + e.getMessage(), e);
    }

    Long statusCode = (Long) result.get(STATUS_CODE);
    String statusMessage = (String) result.get(STATUS_MESSAGE);

    logger.info(
        "Status code {} and status message {} while populating the "
            + "Facility Data into eTrack. User Id {}, Context Id {}",
        statusCode, statusMessage, userId, contextId);

    if (statusCode == 0) {
      logger.info("Exiting from populateFaciltyDataIntoETrack for the User Id: {}, Context ID {}",
          userId, contextId);
    } else if (statusCode == 1) {
      throw new DataNotFoundException("POPULATE_FACILITY_DB_ERROR",
          "There is no Facility found for the given DEC ID Id " + decId);
    } else if (statusCode == 3) {
      throw new DataNotFoundException("POPULATE_FACILITY_DB_ERROR",
          "There is no project associated with this Project Id  " + projectId);
    } else {
      throw new ETrackPermitException("POPULATE_FACILITY_DB_ERROR",
          "Received Unexpected/Unsuccessful error "
              + "from DB while Populating the Facility details " + statusMessage);
    }

  }

  /**
   * This method is used to find the user details for the input email address.
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @param emailAddress - email address
   * @return - List of User details associated with email address.
   */
  @SuppressWarnings("unchecked")
  public List<RegionUserEntity> findTheUserDetailsByEmailAddress(final String userId,
      final String contextId, final String emailAddress) {

    logger.info("Entering into findTheUserDetailsByEmailAddress(). User Id {} Context Id {}",
        userId, contextId);
    Map<String, Object> response = null;

    try {
      Map<String, Object> inputParam = new HashMap<>();

      inputParam.put("p_email_address", emailAddress);

      eTrackGetUserDetailsProcCall
          .declareParameters(new SqlParameter("p_email_address", Types.VARCHAR),
              new SqlOutParameter("p_user_cur", Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet("p_user_cur",
              BeanPropertyRowMapper.newInstance(RegionUserEntity.class));
      response = eTrackGetUserDetailsProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("USER_BY_EMAIL_DB_ERROR",
          "Error from DB while retrieving the User details for the input email address ", e);
    }

    Long statusCode = (Long) response.get(STATUS_CODE);
    String statusMessage = (String) response.get(STATUS_MESSAGE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);

    if (statusCode == 0) {
      return (List<RegionUserEntity>) response.get("p_user_cur");
    } else if (statusCode == 1) {
      throw new DataNotFoundException("USER_BY_EMAIL_DB_ERROR",
          "There is not data found for the input email address");
    } else {
      throw new ETrackPermitException("USER_BY_EMAIL_DB_ERROR",
          "Received Unsuccessful/Unexpeted status code from DB while retrieving "
              + "the User detials for the email addres " + emailAddress + " " + statusMessage);
    }
  }

  /**
   * This method is used to record the Project submission details.
   * 
   * @param userId - User who initiates this request
   * @param contextId - UUID to track the transaction
   * @param projectId - Project Id
   */
  public void recordTheProjectSubmissionDetails(final String userId, final String contextId,
      final Long projectId) {

    logger.info("Entering into recordTheProjectSubmissionDetails (). User Id {} Context Id {}",
        userId, contextId);
    Map<String, Object> response = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();

      inputParam.put(USER_ID, userId);
      inputParam.put(PROJECT_ID, projectId);
      eTrackUpdateOriginalSubmittalIndProcCall.declareParameters(
          new SqlParameter(USER_ID, Types.VARCHAR), new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));
      response = eTrackUpdateOriginalSubmittalIndProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("PROJECT_SUBMISSION_DB_ERROR",
          "Error from DB while recording the Project Submission details ", e);
    }

    Long statusCode = (Long) response.get(STATUS_CODE);
    String statusMessage = (String) response.get(STATUS_MESSAGE);

    logger.info("Status code {} and Status Message {} received the Project "
        + "Submission details execution. Project Id {} ", statusCode, statusMessage, projectId);
    if (statusCode == 0) {
      return;
    } else if (statusCode == 1) {
      throw new DataNotFoundException("NO_DATA_FOUND",
          "There is no project found for the input project id " + projectId);
    } else {
      throw new ETrackPermitException("PROJECT_SUBMISSION_DB_ERROR",
          "Received Unexpected/Unsuccessful response from DB while"
              + "recording the Project Submission details for the project Id " + projectId
              + " Status message " + statusMessage);
    }
  }

  /**
   * This method is used to identify the whether the input Permit and Trans type mapping is valid or
   * not.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - UUID to track the transaction.
   * @param permitType - Permit Type.
   * @param transType - Transaction/App Type.
   */
  public void isValidPermitAndTransTypeMapping(final String userId, final String contextId,
      final String permitType, final String transType) {
    logger.info("Entering into isValidPermitAndTransTypeMapping(). User Id {} Context Id {}",
        userId, contextId);
    Map<String, Object> response = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();

      inputParam.put(USER_ID, userId);
      inputParam.put("p_auth_type", permitType);
      inputParam.put("p_trans_type", transType);

      eTrackDARTPermitAndAppTypeValidProcCall.declareParameters(
          new SqlParameter(USER_ID, Types.VARCHAR), new SqlParameter("p_auth_type", Types.VARCHAR),
          new SqlParameter("p_trans_type", Types.VARCHAR),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));
      response = eTrackDARTPermitAndAppTypeValidProcCall.execute(inputParam);

    } catch (Exception e) {
      throw new ETrackPermitException("PERMIT_TRANS_TYPE_VALIDATION_ERROR",
          "Error while checking whether permit and trans type is valid or not ", e);
    }
    Long statusCode = (Long) response.get(STATUS_CODE);
    String statusMessage = (String) response.get(STATUS_MESSAGE);

    logger.info("Status code {} and Status Message {} received the Project "
        + "DART Permit and App Type mapping procedure execution. Permit Type {} and Trans Type {} ",
        statusCode, statusMessage, permitType, transType);
    if (statusCode == 0) {
      return;
    } else if (statusCode == 1) {
      throw new BadRequestException("NOT_VALID_MAPPING",
          "Permit and Trans type is invalid Permit Type " + permitType + " and Trans Type "
              + transType,
          statusCode);
    } else {
      throw new ETrackPermitException("PERMIT_TRANS_TYPE_VALIDATION_ERROR",
          "Received Unexpected/Unsuccessful response from DB while"
              + "checking the Permit and Trans Type mapping.");
    }
  }

  /**
   * This method is used to retrieve the related regular permits Auth type for the input General Permit's Application Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - UUID to track the transaction.
   * @param edbGPApplnId -General Permit application id from enterprise.
   * 
   * @return - String of Auth type.
   */
  public String retrieveRegularRelatedPermitsByGPApplnId(final String userId, final String contextId,
      final Long edbGPApplnId) {
    logger.info("Entering into retrieveRegularRelatedPermitsByGPApplnId(). User Id {} Context Id {}",
        userId, contextId);
    Map<String, Object> response = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      inputParam.put("p_appl_id", edbGPApplnId);
      eTrackDARTRelatedRegularMappingProcCall.declareParameters(
          new SqlParameter(USER_ID, Types.VARCHAR), 
          new SqlParameter("p_appl_id", Types.BIGINT),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
          new SqlOutParameter("auth_type", Types.VARCHAR));
      
      response = eTrackDARTRelatedRegularMappingProcCall.execute(inputParam);
    } catch (Exception e) {
      throw new ETrackPermitException("REG_RELATED_PERMIT_RETRIEVAL_ERR",
          "Error while retrieving the regular related Permit associated "
          + "to the General Permit application Id " + edbGPApplnId, e);
    }
    Long statusCode = (Long) response.get(STATUS_CODE);
    String statusMessage = (String) response.get(STATUS_MESSAGE);

    logger.info("Status code {} and Status Message {} received "
        + "the Related regular Permit for the GP application Id {}",
        statusCode, statusMessage, edbGPApplnId);
    if (statusCode == 0) {
      return (String) response.get("auth_type");
    } else if (statusCode == 1) {
      throw new DataNotFoundException("NO_EXISTING_APPLN_FOUND",
          "There is no existing application available in the enterprise "
          + "for the input GP application Id." + edbGPApplnId);      
    } else {
      throw new ETrackPermitException("GENERAL_PERMIT_ERROR",
          "Received Unexpected/Unsuccessful response from DB while "
              + "retrieving the related regular permit for the input GP application Id." + edbGPApplnId);
    }
  }
}
