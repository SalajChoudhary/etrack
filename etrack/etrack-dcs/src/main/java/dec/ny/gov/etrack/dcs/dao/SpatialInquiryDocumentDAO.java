package dec.ny.gov.etrack.dcs.dao;

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
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.model.SpatialInquirySupportDocument;
import dec.ny.gov.etrack.dcs.util.DBConstant;

@Repository
public class SpatialInquiryDocumentDAO {

  @Autowired
  @Qualifier("spatialInqDocumentRetrieveProcCall")
  private SimpleJdbcCall spatialInqDocumentRetrieveProcCall;

  /**
   * Logging
   */
  private static final Logger logger = LoggerFactory.getLogger(SpatialInquiryDocumentDAO.class.getName());

  /**
   * This method is used to retrieve the Documents for the input Spatial Inquiry Document id.
   * 
   * @param userId - Unique user id initiated for this request.
   * @param contextId - Unique UUID to track this transaction
   * @param inquiryId - Inquiry ID requested by the user earlier.
   * 
   * @return - List of support documents for the input category code. 
   */
  @SuppressWarnings("unchecked")
  public List<SpatialInquirySupportDocument> retrieveSISupportDocuments(
      final String userId, final String contextId, final Long inquiryId) {
    
    logger.info("Entering into Retrieve the Spatial Inquiry Support document for the Inquiry Id {} "
        + ". User Id {}, Context Id {}", inquiryId, userId, contextId);
    Map<String, Object> result = null;
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put(DBConstant.USER_ID, userId);
    inputParam.put(DBConstant.SI_INQUIRY_ID, inquiryId);
//    inputParam.put(DBConstant.SI_CATEGORY_CODE, siCategoryCode);
    
    spatialInqDocumentRetrieveProcCall
        .declareParameters(
            new SqlParameter(DBConstant.USER_ID, Types.VARCHAR),
            new SqlParameter(DBConstant.SI_INQUIRY_ID, Types.BIGINT),
            new SqlOutParameter(DBConstant.STATUS_CODE, Types.BIGINT),
            new SqlOutParameter(DBConstant.STATUS_MESSAGE, Types.VARCHAR),
            new SqlOutParameter(DBConstant.SI_DOCUMENT_CURSOR, Types.REF_CURSOR))
        .returningResultSet(DBConstant.SI_DOCUMENT_CURSOR,
            BeanPropertyRowMapper.newInstance(SpatialInquirySupportDocument.class));

    result = spatialInqDocumentRetrieveProcCall.execute(inputParam);
    logger.debug("Raw data received as eTrackPermitFormsProcCall procedure output {}", result);
    Long statusCode = (Long) result.get(DBConstant.STATUS_CODE);
    String statusMessage = (String) result.get(DBConstant.STATUS_MESSAGE);
    logger.debug("Raw data received as procedure output after processed {}", statusCode);
    if (statusCode == 0) {
      logger.info("Exiting from Retrieve the Application "
          + "Permit Form Narrative. User Id {}, Context Id {}", userId, contextId);
      return  (List<SpatialInquirySupportDocument>)result.get(DBConstant.SI_DOCUMENT_CURSOR);
    } else if (statusCode == -100) {
      throw new DcsException("Error while retrieving the Application Permit Form details " + statusMessage);
    } else if (statusCode == 1 || statusCode == 2) {
      throw new DocumentNotFoundException(statusMessage,
          "There is no data associated with this " + userId);
    } else {
      throw new DcsException("Unexpected DB error received " + statusMessage);
    }
  }
}
