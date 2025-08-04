package gov.ny.dec.etrack.cache.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
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
import gov.ny.dec.etrack.cache.dao.MessageDAO;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@Repository
public class MessageDAOImpl implements MessageDAO {
  
  private static final Logger logger =
      LoggerFactory.getLogger(MessageDAOImpl.class.getName());

  private static final String MESSAGE_PROC_CURSOR_NAME = "CUR_MESSAGES";

  @Autowired
  @Qualifier("messageJdbcCall")
  private SimpleJdbcCall messageJdbcCall;
  
  @SuppressWarnings("unchecked")
  @Cacheable(value = "messageCache")
  @Override
  public List<Message> getAllMessages(String userId, String contextId) {
    try {
      logger.info("Request to retrieve the Messages from Database to update the Cache. User id: {}, context id: {}", userId, contextId);
      messageJdbcCall.declareParameters(new SqlOutParameter(MESSAGE_PROC_CURSOR_NAME, Types.REF_CURSOR))
      .returningResultSet(MESSAGE_PROC_CURSOR_NAME,
          BeanPropertyRowMapper.newInstance(Message.class));

      Map<String, Object> result = messageJdbcCall.execute(new HashMap<>(0));
      logger.info("Received the Messages from Database to update the Cache. User id: {} context id: {}", userId, contextId);
      return (List<Message>) result.get(MESSAGE_PROC_CURSOR_NAME);
    } catch (Exception e) {
      logger.error("Error while retrieving the message from ETrack DB. User id: {}, context id: {}", userId, contextId);
      throw new ETrackConfigException("Error while retrieving the message from ETrack DB", e);
    }
  }
}
