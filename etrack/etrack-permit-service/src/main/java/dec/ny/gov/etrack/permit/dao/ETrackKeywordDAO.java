package dec.ny.gov.etrack.permit.dao;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.KEYWORDS_CUR;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PROJECT_DESC;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.PROJECT_ID;
import java.sql.Types;
import java.util.ArrayList;
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
import dec.ny.gov.etrack.permit.entity.SystemDetectedKeyword;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;

@Repository
public class ETrackKeywordDAO {

  @Autowired
  @Qualifier("retrieveSystemDetectedProcCall")
  private SimpleJdbcCall retrieveSystemDetectedProcCall;
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackKeywordDAO.class.getName());
  
  /**
   * Retrieve the System detected details for the input Project description.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique transaction id
   * @param projectId - Project id.
   * @param projectDescription - Brief description which will be used to generate System Detected keywords.
   * 
   * @return - list of System Detected keyword ids.
   */
  @SuppressWarnings("unchecked")
  public List<SystemDetectedKeyword> retrieveSystemDetectedKeywords(final String userId, final String contextId,
      final Long projectId, final String projectDescription) {
    
    logger.info("Entering into retrieveSystemDetectedKeywords User Id: {}, Context Id {}", userId, contextId);
    Map<String, Object> result = null;
    try {
      Map<String, Object> inputParam = new HashMap<>();
      inputParam.put(PROJECT_ID, projectId);
      inputParam.put(PROJECT_DESC, projectDescription);
      retrieveSystemDetectedProcCall.declareParameters(
          new SqlParameter(PROJECT_ID, Types.BIGINT),
          new SqlParameter(PROJECT_DESC, Types.VARCHAR),
          new SqlOutParameter(KEYWORDS_CUR, Types.REF_CURSOR))
      .returningResultSet(KEYWORDS_CUR, BeanPropertyRowMapper.newInstance(SystemDetectedKeyword.class));

      result = retrieveSystemDetectedProcCall.execute(inputParam);
      logger.debug("Raw data received from the retrieve System Detected Keywords procedure output {}", result);
      return (List<SystemDetectedKeyword>) result.get(KEYWORDS_CUR);
    } catch (Exception e) {
      throw new ETrackPermitException("SYSTEM_DETECTED_KEYWORDS_RETRIEVAL_ERROR",
          "Error from DB while retrieving the System detected Keywords Procedure. Error Details ", e);
    }
  }
}
