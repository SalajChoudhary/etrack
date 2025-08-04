package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_CODE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_MESSAGE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import dec.ny.gov.etrack.dart.db.entity.SearchModel;
import dec.ny.gov.etrack.dart.db.entity.SearchResult;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Repository
public class ETrackSearchToolDAO {

	@Autowired
	@Qualifier("submitProjectRetrievalTemplate")
	private JdbcTemplate submitProjectRetrievalTemplate;
	@Autowired
	@Qualifier("namedParameterProjectRetrievalTemplate")
	private NamedParameterJdbcTemplate namedParameterProjectRetrievalTemplate;

	@Autowired
	@Qualifier("eTrackAttributeDataNameProcCall")
	private SimpleJdbcCall eTrackAttributeDataNameProcCall;
	@Autowired
	@Qualifier("retriveSearchResultsProcCall")
	private SimpleJdbcCall etrackRetriveSearchResultsProcCall;

	@Autowired
	private EntityManager entityManager;

	private SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");

	private static final Logger logger = LoggerFactory.getLogger(ETrackQueryReportDAO.class.getName());

	/**
	 * Build the query to fetch Search Attributes from the data base with SQL
	 * 
	 * @param userId        - User who initiates this request.
	 * @param contextId     - Unique UUID to track this request.
	 * @param queryCriteria - Search Criteria parameters. Object of
	 *                      {@link SearchModel}
	 * 
	 * @return - Data result list of {}
	 */

	public Map<String, Object> getAttributes(String attributeId) {
		logger.info("Entering into Retrieve the Search Attribute Data for the Attribute ID {} ", attributeId);
		try {
			Map<String, Object> inputParam = new HashMap<>();
			inputParam.put(DartDBConstants.ATTRIBUTE_ID, attributeId);
			eTrackAttributeDataNameProcCall
					.declareParameters(new SqlParameter(DartDBConstants.ATTRIBUTE_ID, Types.INTEGER),
							new SqlOutParameter(STATUS_CODE, Types.BIGINT),
							new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
							new SqlOutParameter(DartDBConstants.ATTRIBUTE_CURSOR, Types.REF_CURSOR),
							new SqlOutParameter(DartDBConstants.LAST_LOAD_TIME,Types.VARCHAR))
					.returningResultSet(DartDBConstants.ATTRIBUTE_CURSOR, new RowMapper<String>() {

						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							// TODO Auto-generated method stub
							return rs.getString(1);
						}
					});

			Map<String, Object> result = eTrackAttributeDataNameProcCall.execute(inputParam);
//			List<String> attributesList = (List<String>) result.get(DartDBConstants.ATTRIBUTE_CURSOR);
//			attributes.setLastLoadTime((String) result.get(DartDBConstants.LAST_LOAD_TIME));
			return result;
		} catch (Exception e) {
			logger.error("Error while retiving Attribute List procedure", e);
			throw new DartDBException("PERMIT_APPLN_FORM_RETRIEVAL_GENERAL_ERR",
					"General error occurred while retrieving PERMIT Forms ", e);
		}
	}

	/**
	 * Build the query to fetch Search Attributes from the data base with SQL
	 * 
	 * @param userId        - User who initiates this request.
	 * @param contextId     - Unique UUID to track this request.
	 * @param queryCriteria - Search Criteria parameters. Object of
	 *                      {@link SearchModel}
	 * 
	 * @return - Data result list of {}
	 */

	public List<SearchResult> getSearchResults(Long queryId, String userId) {
		logger.info("Entering into Retrieve the Search Result Data for the Query ID {} ", queryId);
		try {
			Map<String, Object> inputParam = new HashMap<>();
			inputParam.put(DartDBConstants.QUERY_ID, queryId);
			inputParam.put(DartDBConstants.USER_ID, userId);
			etrackRetriveSearchResultsProcCall
					.declareParameters(new SqlParameter(DartDBConstants.QUERY_ID, Types.BIGINT),
							new SqlParameter(DartDBConstants.USER_ID, Types.VARCHAR),
							new SqlOutParameter(STATUS_CODE, Types.BIGINT),
							new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
							new SqlOutParameter(DartDBConstants.SEARCH_RESULT_CURSOR, -10))
					.returningResultSet(DartDBConstants.SEARCH_RESULT_CURSOR,
							BeanPropertyRowMapper.newInstance(SearchResult.class));

			Map<String, Object> result = etrackRetriveSearchResultsProcCall.execute(inputParam);
			Long statusCode = (Long) result.get(STATUS_CODE);
			String statusMessage = (String) result.get(STATUS_MESSAGE);
			
			List<SearchResult> attributesList = (List<SearchResult>) result.get(DartDBConstants.SEARCH_RESULT_CURSOR);
			logger.info("Raw data received as etrackRetriveSearchResultsProcCall procedure output after processed {}",
					new ObjectMapper().writeValueAsString(result));
			
			if(statusCode == 100){
				logger.info("Raw data received as etrackRetriveSearchResultsProcCall procedure output after processed {}",statusMessage);
				throw new NoDataFoundException("DATA_GEN_INPROGRESS", statusMessage);
			}
			
			if (statusCode == 0) {
				logger.info("Exiting from geETrackDetails User Id: {}, Context ID {}", userId);
				return attributesList;
			} else if (statusCode == -100) {
				throw new DartDBException("SEARCH_RETRIEVE_DB_ERROR",
						"Received DB error while retrieving the Search Result details " + statusMessage);
			} else if (statusCode >= 1 && statusCode <= 3) {
				if (statusCode == 1) {
					throw new NoDataFoundException("NO_RESULT_FOUND",
							"There is no Result found for the input search Query Id " + queryId);
				}
				if (statusCode == 2) {
					throw new NoDataFoundException("NO_SEARCH_RESULT_FOUND",
							"There is no search condition found for the input Query Id " + queryId);
				}
				if (statusCode == 3) {
					throw new NoDataFoundException("NO_SEARCH_FOUND",
							"There is no Result found for the input Query Id " + queryId);
				}
			} else {
				throw new DartDBException("UNEXPECTED_DB_ERROR",
						"Unexpected DB Error while retrieving the search result details " + statusMessage);
			}
			return attributesList;
			
			
		} catch(NoDataFoundException e) {
			logger.error("Error while retrieving the etrackRetriveSearchResultsProc", e);
			throw e;
		}
		catch (Exception e) {
			logger.error("Error while retrieving the etrackRetriveSearchResultsProc", e);
			throw new DartDBException("SEARCH_RESULT_RETRIEVAL_GENERAL_ERR",
					"General error occurred while retrieving Search Results ", e);
		}

	}

}
