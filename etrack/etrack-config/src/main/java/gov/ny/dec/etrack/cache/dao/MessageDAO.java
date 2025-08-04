package gov.ny.dec.etrack.cache.dao;

import java.util.List;
import gov.ny.dec.etrack.cache.entity.Message;

public interface MessageDAO {
  public List<Message> getAllMessages(String userId, String contextId);
}
