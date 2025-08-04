package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_CODE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_MESSAGE;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocument;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryReviewDetail;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Repository
public class SpatialInquiryDetailDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpatialInquiryDetailDAO.class.getName());
  
  @Qualifier("spatialInqDocumentRetrieveProcCall")
  @Autowired
  private SimpleJdbcCall spatialInqDocumentRetrieveProcCall;

  @Qualifier("spatialInqStatusRetrieveProcCall")
  @Autowired
  private SimpleJdbcCall spatialInqStatusRetrieveProcCall;

  @Qualifier("spatialInqReviewRetrieveProcCall")
  @Autowired
  private SimpleJdbcCall spatialInqReviewRetrieveProcCall;

  
  /**
   * Retrieve the facility details from eTrack database.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param inquiryId - Inquiry id.
   * 
   * @return - Response received for the input details.
   */
  @SuppressWarnings("unchecked")
  public List<SpatialInquiryDocument> getSpatialInquiryDocument(String userId, String contextId,
      final Long inquiryId) {
    
    LOGGER.info("Entering into getSpatialInquiryDocument User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, inquiryId);
      inputParam.put(USER_ID, userId);
      spatialInqDocumentRetrieveProcCall
          .declareParameters(new SqlParameter(DartDBConstants.SPATIAL_INQUIRY_ID, Types.VARCHAR),
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.SPATIAL_INQ_CATG_DOCUMENT_CURSOR, Types.REF_CURSOR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR))
          .returningResultSet(DartDBConstants.SPATIAL_INQ_CATG_DOCUMENT_CURSOR,
              BeanPropertyRowMapper.newInstance(SpatialInquiryDocument.class));

      result = spatialInqDocumentRetrieveProcCall.execute(inputParam);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      LOGGER.debug("Raw data received as Spatial Inquiry Retrieval procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));
      if (statusCode == 0) {
        LOGGER.info("Exiting from Spatial Inquiry Retrieval User Id: {}, Context ID {}", userId, contextId);
        return  (List<SpatialInquiryDocument>)result.get(DartDBConstants.SPATIAL_INQ_CATG_DOCUMENT_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("SPATIAL_INQ_RETRIEVE_DB_ERROR", 
            "Received DB error while retrieving the Spatial Inquiry Retrieval details " + statusMessage);
      } else {
        throw new DartDBException("SPATIAL_INQ_RETRIEVE_DB_ERROR", 
            "Unexpected DB Error while retrieving the Spatial Inquiry document summary details " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error(
          "Error while retrieving the Spatial Inquiry document data user Id: {}, context Id: {}, Error :", userId, contextId, e);
      throw new DartDBException("SPATIAL_INQ_RETRIEVE_DB_GEN_ERROR", "Error while retrieving Spatial Inquiry document summary data ", e);
    }
  }

  public Map<String, Long> getSpatialInquiryStatus(String userId, String contextId,
      Long inquiryId) {
    LOGGER.info("Entering into geETrackDetails User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, inquiryId);
      inputParam.put(USER_ID, userId);
      spatialInqStatusRetrieveProcCall
          .declareParameters(
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlParameter(DartDBConstants.SPATIAL_INQUIRY_ID, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.SPATIAL_INQUIRY_STATUS, Types.BIGINT),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      result = spatialInqStatusRetrieveProcCall.execute(inputParam);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      LOGGER.debug("Raw data received as Spatial Inquiry Retrieval procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));
      if (statusCode == 0) {
        LOGGER.info("Exiting from Spatial Inquiry Retrieval User Id: {}, Context ID {}", userId, contextId);
        Long status =  (Long)result.get(DartDBConstants.SPATIAL_INQUIRY_STATUS);
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("status", status);
        return statusMap; 
      } else if (statusCode == 1) {
        throw new NoDataFoundException("NO_SPATIAL_INQ_AVAIL", 
            "There is no Spatial Inquriy availalbe for the requested inquiry id " + inquiryId);
      } else {
        throw new DartDBException("SPATIAL_INQ_RETRIEVE_DB_ERROR", 
            "Unexpected DB Error while retrieving the Spatial Inquiry status " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error(
          "Error while retrieving the Spatial Inquiry document data user Id: {}, context Id: {}, Error :", userId, contextId, e);
      throw new DartDBException("SPATIAL_INQ_RETRIEVE_DB_GEN_ERROR", "Error while retrieving Spatial Inquiry document summary data ", e);
    }
  }
  
  /**
   * Retrieve the Spatial Inquiry Review details for the input user id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Spatial Inquiry review details.
   */
  @SuppressWarnings("unchecked")
  public List<SpatialInquiryReviewDetail> retrieveSpatialInquiryReviewDetails(
      final String userId, final String contextId) {
    
    LOGGER.info("Entering into retrieveSpatialInquiryReviewDetails User Id: {}, Context ID {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(USER_ID, userId);
      spatialInqReviewRetrieveProcCall
          .declareParameters(
              new SqlParameter(USER_ID, Types.VARCHAR),
              new SqlOutParameter(STATUS_CODE, Types.BIGINT),
              new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
              new SqlOutParameter(DartDBConstants.SPATIAL_INQ_REVIEW_CURSOR, Types.REF_CURSOR))
          .returningResultSet(DartDBConstants.SPATIAL_INQ_REVIEW_CURSOR,
              BeanPropertyRowMapper.newInstance(SpatialInquiryReviewDetail.class));

      result = spatialInqReviewRetrieveProcCall.execute(inputParam);
      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      LOGGER.debug("Raw data received as Spatial Inquiry Retrieval procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));
      if (statusCode == 0) {
        LOGGER.info("Exiting from Spatial Inquiry Review Retrieval User Id: {}, Context Id {}", userId, contextId);
        return  (List<SpatialInquiryReviewDetail>)result.get(DartDBConstants.SPATIAL_INQ_REVIEW_CURSOR);
      } else if (statusCode == -100) {
        throw new DartDBException("SPATIAL_INQ_REVIEW_RETRIEVE_DB_ERROR", 
            "Received DB error while retrieving the Spatial Inquiry review retrieval details " + statusMessage);
      } else {
        throw new DartDBException("SPATIAL_INQ_REVIEW_RETRIEVE_DB_ERROR", 
            "Unexpected DB Error while retrieving the Spatial Inquiry review document details " + statusMessage);
      }
    } catch (DartDBException | NoDataFoundException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error(
          "Error while retrieving the Spatial Inquiry review data user Id: {}, context Id: {}, Error :", userId, contextId, e);
      throw new DartDBException("SPATIAL_INQ_REVIEW_RETRIEVE_DB_GEN_ERROR", 
          "Error while retrieving Spatial Inquiry review details", e);
    }
  }

}
