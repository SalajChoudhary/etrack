package dec.ny.gov.etrack.permit.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import dec.ny.gov.etrack.permit.exception.ETrackPermitException;

@Repository
public class EtrackSearchToolDAO {
  private static final Logger logger = LoggerFactory.getLogger(EtrackSearchToolDAO.class.getName());
  @Autowired
  @Qualifier("etrackPopulateSearchTableProcCall")
  private SimpleJdbcCall etrackPopulateSearchTableProcCall;

  @Autowired
  @Qualifier("etrackIncrementalPopulateSearchTableProcCall")
  private SimpleJdbcCall etrackIncrementalPopulateSearchTableProcCall;

  private static final String STATUS_CODE = "p_status_cd";
  private static final String STATUS_MESSAGE = "p_status_msg";

  /**
   * Load or clean and reload the Search Table details.
   * 
   * @return - Status of this process.
   */
  public String retriveSearchToolsDetail() {
    try {
      etrackPopulateSearchTableProcCall.declareParameters(
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      Map<String, Object> result = etrackPopulateSearchTableProcCall.execute();
      logger.info(
          "Raw data received as etrackPopulateSearchTableProcCall procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));

      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      if (statusCode == 0) {
        logger.info("Exiting from Populating  Search Table");
        return statusCode + statusMessage;
      } else if (statusCode == -100) {
        throw new ETrackPermitException("SEARCH_RETRIEVE_DB_ERROR",
            "Received DB error while  Populating  Search Table Codes  " + statusMessage);
      } else {
        throw new ETrackPermitException("UNEXPECTED_DB_ERROR",
            "Unexpected DB Error while  Populating  Search Table Codes  " + statusMessage);
      }
    }  catch (ETrackPermitException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error while retrieving the etrackPopulateSearchTableProcCall", e);
      throw new ETrackPermitException("SEARCH_RESULT_RETRIEVAL_GENERAL_ERR",
          "General error occurred while Populating  Search Table Codes ", e);
    }
  }

  /**
   * Process the incremental load of Search table details (i.e find the not loaded records and
   * refresh the data)..
   * 
   * @return - Status of this process.
   */
  public String processIncrementalSearchTableDetailsLoad() {
    try {
      Map<String, Object> inputParam = new HashMap<>();
      etrackIncrementalPopulateSearchTableProcCall.declareParameters(
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));

      Map<String, Object> result = etrackIncrementalPopulateSearchTableProcCall.execute(inputParam);
      logger.info(
          "Raw data received as etrackIncrementalPopulateSearchTableProcCall procedure output after processed {}",
          new ObjectMapper().writeValueAsString(result));

      Long statusCode = (Long) result.get(STATUS_CODE);
      String statusMessage = (String) result.get(STATUS_MESSAGE);

      if (statusCode == 0) {
        return statusCode + " : " + statusMessage;
      } else if (statusCode == -100) {
        throw new ETrackPermitException("INCREMENTAL_SEARCH_LOAD_ERR",
            "Received DB error while Populating Search Table in incremental load process :"
                + statusMessage);
      } else {
        throw new ETrackPermitException("INCREMENTAL_SEARCH_LOAD_GEN_ERR",
            "Unexpected DB Error while Populating Search Table in incremental load process :"
                + statusMessage);
      }
    } catch (ETrackPermitException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("SEARCH_RESULT_RETRIEVAL_GENERAL_ERR",
          "General error occurred while Populating Search Table in incremental process ", e);
    }
  }
}
