package gov.ny.dec.etrack.cache.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.dao.SWFacilityTypeDAO;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeAndSubTypeEntity;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeEntity;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@Repository
public class SWFacilityTypeDAOImpl implements SWFacilityTypeDAO {

  private static final Logger logger =
      LoggerFactory.getLogger(SWFacilityTypeDAOImpl.class.getName());

  @Autowired
  private SimpleJdbcCall swFacilityTypeProcCall;

  private static final String SW_FACILITY_TYPE_PROC_CURSOR_NAME = "CUR_FAC";
 
  /**
   * Retrieve the Solid Waste Facility Type.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Solid Waste Facility Type.
   */
  @SuppressWarnings("unchecked")
  public List<SWFacilityTypeAndSubTypeEntity> getSWFacilityType(final String userId, final String contextId) {
    try {
      logger.info("Request to retrieve the getSWFacilityType from Database to update the Cache. User id: {} context id: {}", userId, contextId);
      swFacilityTypeProcCall.declareParameters(
          new SqlOutParameter(SW_FACILITY_TYPE_PROC_CURSOR_NAME, Types.REF_CURSOR))
      .returningResultSet(SW_FACILITY_TYPE_PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(SWFacilityTypeAndSubTypeEntity.class));

      Map<String, Object> result = swFacilityTypeProcCall.execute(new HashMap<>(0));
      logger.info("Received the getSWFacilityType from Database to update the Cache. User id: {}, context id: {}", userId, contextId);
      return (List<SWFacilityTypeAndSubTypeEntity>) result.get(SW_FACILITY_TYPE_PROC_CURSOR_NAME);
    } catch (Exception e) {
      logger.error("Error while retrieving the getSWFacilityTy from ETrack DB. User id: {}, context id: {}", userId, contextId);
      throw new ETrackConfigException(
          "Error while retrieving the getSWFacilityType from ETrack DB", e);
    }
  }
}
