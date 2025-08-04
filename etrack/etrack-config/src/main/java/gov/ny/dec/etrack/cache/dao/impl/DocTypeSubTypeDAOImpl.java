package gov.ny.dec.etrack.cache.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.dao.DocTypeSubTypeDAO;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@Repository
public class DocTypeSubTypeDAOImpl implements DocTypeSubTypeDAO {

  private static final Logger logger =
      LoggerFactory.getLogger(DocTypeSubTypeDAOImpl.class.getName());

  @Autowired
  private SimpleJdbcCall docTyeAndSubTypeJdbcCall;

  private static final String DOCTYPE_PROC_CURSOR_NAME = "CUR_DOCTYPE_SUBTYPE";

  @SuppressWarnings("unchecked")
  @Cacheable(value = "docTypeSubTypeCache")
  public List<DocTypeSubType> getDocTypeAndSubTypes(String userId, String contextId) {
    try {
      logger.info("Request to retrieve the DocTypes and SubTypes from Database to update the Cache. User id: {} context id: {}", userId, contextId);
      docTyeAndSubTypeJdbcCall.declareParameters(new SqlOutParameter(DOCTYPE_PROC_CURSOR_NAME, Types.REF_CURSOR))
      .returningResultSet(DOCTYPE_PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(DocTypeSubType.class));

      Map<String, Object> result = docTyeAndSubTypeJdbcCall.execute(new HashMap<>(0));
      logger.info("Received the DocTypes and SubTypes from Database  to update the Cache. User id: {}, context id: {}", userId, contextId);
      return (List<DocTypeSubType>) result.get(DOCTYPE_PROC_CURSOR_NAME);
    } catch (Exception e) {
      logger.error("Error while retrieving the DocTypes and SubTypes from ETrack DB. User id: {}, context id: {}", userId, contextId);
      throw new ETrackConfigException(
          "Error while retrieving the DocTypes and SubTypes from ETrack DB", e);
    }
  }
}
