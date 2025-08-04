package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.ARC_PRG_CUR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_ARC_PRG_CUR;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.P_RESULT_ID;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocument;
import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResult;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;
import dec.ny.gov.etrack.dart.db.model.Region;

@Repository
public class PurgeArchiveDao {
	
	private static final Logger logger = LoggerFactory.getLogger(PurgeArchiveDao.class.getName());

	@Autowired
	@Qualifier("purgeArchiveResultDetailsProcCall")
	private SimpleJdbcCall purgeArchiveResultDetailsProcCall;


	@Autowired
	@Qualifier("purgeArchiveResultDocumentsProcCall")
	private SimpleJdbcCall purgeArchiveResultDocumentsProcCall;
    /**
     * Retrieves the query results for all regions.
     * 
     * @return
     */
	@SuppressWarnings("unchecked")
	public Map<String,  QueryResultList> getPurgeArchiveQueryResults() {
		try {
			
			purgeArchiveResultDetailsProcCall.declareParameters().returningResultSet(P_ARC_PRG_CUR, new RowMapper<Region>() {

				@Override
				public Region mapRow(ResultSet rs, int rowNum) throws SQLException {
					Region region = new Region();
					QueryResult queryResult = new QueryResult();
					List<QueryResult> queryResults =new ArrayList<>();
					region.setRegionId(rs.getInt(1));
					queryResult.setQueryCode(rs.getInt(2));
					queryResult.setQueryDesc(rs.getString(4));
					queryResult.setResultCode(rs.getInt(5));
					queryResult.setResultDesc(rs.getString(6));
					BigDecimal bigDecimal =  (BigDecimal)rs.getObject(7);
					boolean aIndicator = bigDecimal != null && bigDecimal.intValue() == 1 ? true : false;
					queryResult.setResultReviewedInd(aIndicator);
					queryResults.add(queryResult);
					region.setQueryResults(queryResults);
					return region;
				}
			});

			Map<String, Object> result = purgeArchiveResultDetailsProcCall.execute();
			Map<String, QueryResultList> resultMap = new TreeMap<>();
			if (result != null) {
				List<Region> regions= (List<Region>) result.get(P_ARC_PRG_CUR);
				for(Region regionObj: regions) {
					String regionId = String.valueOf(regionObj.getRegionId());
					QueryResultList queryResultList = resultMap.get(regionId);
					if(queryResultList == null) { 
						queryResultList = new QueryResultList(new ArrayList<>());
						resultMap.put(regionId, queryResultList);
					}
					logger.info("regions query list size : " + regionObj.getQueryResults().size());
					for(QueryResult queryResult1 : regionObj.getQueryResults()) {
						Map<String, QueryResult> map = new HashMap<>();
						QueryResult queryResult = new QueryResult();
						queryResult.setQueryCode(queryResult1.getQueryCode());
						queryResult.setQueryDesc(queryResult1.getQueryDesc());
						queryResult.setResultCode(queryResult1.getResultCode());
						queryResult.setResultDesc(queryResult1.getResultDesc());
						queryResult.setResultReviewedInd(queryResult1.getResultReviewedInd());
						map.put("queryResult", queryResult);
						queryResultList.getQueryResults().add(map);
					}

				}

				return resultMap;
			}
		} catch (Exception e) {
			throw new DartDBException("Purge Archive",
					"Error while retrieving purge archiveQuery result",  e);
		}
		return null;
	}

	/**
	 * Retrieves all the documents for purge/archive for a given resultset.
	 * 
	 * @param resultId - unique id for a resultset.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PurgeArchiveResultDocuments getPurgeArchiveQueryResultDocuments(String resultId) {
		try {
			
			Map<String, Object> inputParamMap = new HashMap<>();
			inputParamMap.put(P_RESULT_ID, resultId);
			
			purgeArchiveResultDocumentsProcCall.declareParameters()
				.returningResultSet(ARC_PRG_CUR, new RowMapper<PurgeArchiveResultDocument>() {
	
					@Override
					public PurgeArchiveResultDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
						PurgeArchiveResultDocument purgeArchiveResultDocument = new PurgeArchiveResultDocument();
						purgeArchiveResultDocument.setDocumentId(rs.getLong("DOCUMENT_ID"));
						purgeArchiveResultDocument.setEdbDistrictId(rs.getLong("EDB_DISTRICT_ID"));
						purgeArchiveResultDocument.setProjectId(rs.getLong("PROJECT_ID"));
						purgeArchiveResultDocument.setDocCategory(rs.getLong("DOCUMENT_TYPE_ID"));
						purgeArchiveResultDocument.setDocTypeDesc(rs.getString("DOCUMENT_TYPE_DESC"));
						purgeArchiveResultDocument.setDocSubCategory(rs.getLong("DOCUMENT_SUB_TYPE_ID"));
						purgeArchiveResultDocument.setDocumentName(rs.getString("DOCUMENT_NM"));
						
						String markForReview =  StringUtils.isNotBlank(rs.getString("ARCHIVE_REVIEWED_IND")) ?
								rs.getString("ARCHIVE_REVIEWED_IND") :  rs.getString("PURGE_REVIEWED_IND");
						purgeArchiveResultDocument.setMarkForReview(markForReview);
						boolean litigationHoldInd = false;
						Long litigationAddedProject = (Long)rs.getLong("LIT_PROJECT");
						if (litigationAddedProject != null && litigationAddedProject.longValue() > 0) {
						  Date litigationHoldEndDate =  (Date)rs.getObject("LITIGATION_HOLD_END_DATE");
	                        if (litigationHoldEndDate == null || litigationHoldEndDate.after(new Date())) {
	                          litigationHoldInd = true;
	                        }
						}
						purgeArchiveResultDocument.setLitigationHoldInd(litigationHoldInd);
						purgeArchiveResultDocument.setDocSubTypeDesc(rs.getString("DOCUMENT_SUB_TYPE_DESC"));
						purgeArchiveResultDocument.setDecId(rs.getString("DEC_ID"));
						purgeArchiveResultDocument.setDecIdFormatted(rs.getString("DEC_ID_FORMATTED"));
						purgeArchiveResultDocument.setFacilityName(rs.getString("FACILITY_NAME"));
						purgeArchiveResultDocument.setMunicipalityName(rs.getString("MUNICIPALITY"));
						
						//check 
						purgeArchiveResultDocument.setOtherDocSubCategory(null);
						
						return purgeArchiveResultDocument;
					}
				})
				.addDeclaredParameter(new SqlParameter(P_RESULT_ID, Types.VARCHAR));
			
			Map<String, Object> result = purgeArchiveResultDocumentsProcCall.execute(inputParamMap);
			logger.info("result " + result);
			if (result != null) {
				List<PurgeArchiveResultDocument> documents = (List<PurgeArchiveResultDocument>) result.get(ARC_PRG_CUR);
				PurgeArchiveResultDocuments purgeArchiveResultDocuments = new PurgeArchiveResultDocuments();
				purgeArchiveResultDocuments.setDocuments(documents);
				return purgeArchiveResultDocuments;
			}
		} catch (Exception e) {
			throw new DartDBException("Purge Archive",
					"Error while retrieving purge archiveQuery result",  e);
		}
		return null;
	}

}
