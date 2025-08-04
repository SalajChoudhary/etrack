package gov.ny.dec.etrack.cache.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.dao.EnterpriseConfigurationDAO;
import gov.ny.dec.etrack.cache.entity.ProgApplicationType;
import gov.ny.dec.etrack.cache.entity.ProgDistrictType;
import gov.ny.dec.etrack.cache.entity.SpecialAttnCode;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@Repository
public class EnterpriseConfigurationDAOImpl implements EnterpriseConfigurationDAO {

  private static final Logger logger = LoggerFactory.getLogger(EnterpriseConfigurationDAOImpl.class.getName());
  
  @Autowired
  @Qualifier("eTrackXTRAIDandProgramIDandSplAttnProcCall")
  private SimpleJdbcCall eTrackXTRAIDandProgramIDandSplAttnProcCall;
  
  private static final String XTRA_ID_PROC_CURSOR_NAME = "p_xtra_id_cur";
  private static final String PROGRAM_ID__PROC_CURSOR_NAME = "p_prog_id_cur";
  private static final String SPL_ATTN_CODES_PROC_CURSOR_NAME = "p_spl_attn_cur";
  private static final String STATUS_CODE = "p_status_cd";
  private static final String STATUS_MESSAGE = "p_status_msg";


  
  @Override
  @Cacheable(value = "enterpriseIdsCache")
  public Map<String, Object> retriveXTRAProgIdsAndSpecialAttnCodes() {
    logger.info("Entering into retriveXTRAProgIdsAndSpecialAttnCodes");
    try {
      logger.info("Request to retrieve the retriveXTRAProgIdsAndSpecialAttnCodes from Database");
      eTrackXTRAIDandProgramIDandSplAttnProcCall.declareParameters(
          new SqlOutParameter(XTRA_ID_PROC_CURSOR_NAME, -10),
          new SqlOutParameter(PROGRAM_ID__PROC_CURSOR_NAME, -10),
          new SqlOutParameter(SPL_ATTN_CODES_PROC_CURSOR_NAME, -10),
          new SqlOutParameter(STATUS_CODE, Types.BIGINT),
          new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR));
      
      eTrackXTRAIDandProgramIDandSplAttnProcCall.addDeclaredRowMapper(XTRA_ID_PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(ProgApplicationType.class));
      eTrackXTRAIDandProgramIDandSplAttnProcCall.addDeclaredRowMapper(PROGRAM_ID__PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(ProgDistrictType.class));
      eTrackXTRAIDandProgramIDandSplAttnProcCall.addDeclaredRowMapper(SPL_ATTN_CODES_PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(SpecialAttnCode.class));
      Map<String, Object> result = eTrackXTRAIDandProgramIDandSplAttnProcCall.execute();
      
      Map<String, Object> xtraProgIdsAndSplAttnCodes = new HashMap<>();
      xtraProgIdsAndSplAttnCodes.put("XTRA_ID", result.get(XTRA_ID_PROC_CURSOR_NAME));
      xtraProgIdsAndSplAttnCodes.put("PROG_ID", result.get(PROGRAM_ID__PROC_CURSOR_NAME));
      xtraProgIdsAndSplAttnCodes.put("SPL_ATTN_CODE", result.get(SPL_ATTN_CODES_PROC_CURSOR_NAME));
      logger.info("Received the retriveXTRAProgIdsAndSpecialAttnCodes from Database to update the Cache.");
      return xtraProgIdsAndSplAttnCodes;
    } catch (Exception e) {
      logger.error("Error while retrieving the retriveXTRAProgIdsAndSpecialAttnCodes from enterprise DB.");
      throw new ETrackConfigException(
          "Error while retrieving the retriveXTRAProgIdsAndSpecialAttnCodes from enterprise DB", e);
    }
  }

}
