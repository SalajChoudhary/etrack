package gov.ny.dec.etrack.cache.dao;

import java.util.List;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;

public interface DocTypeSubTypeDAO {
  public List<DocTypeSubType> getDocTypeAndSubTypes(String userId, String contextId);
}
