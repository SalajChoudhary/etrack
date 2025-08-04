package dec.ny.gov.etrack.permit.dao;

import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_CODE;
import static dec.ny.gov.etrack.permit.util.ETrackPermitConstant.STATUS_MESSAGE;
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
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;

@Repository
public class ETrackCleanUpDAO {
  
  private static Logger logger = LoggerFactory.getLogger(ETrackCleanUpDAO.class.getName());
  
  @Autowired
  @Qualifier("purgeCleanUpProcCall")
  private SimpleJdbcCall purgeCleanUpProcCall;
  
  public void cleanUpOrphanRecords() {
    logger.info("Entering into Clean Orphan records ");
    purgeCleanUpProcCall.declareParameters(
        new SqlOutParameter(STATUS_MESSAGE, Types.VARCHAR),
        new SqlOutParameter(STATUS_CODE, Types.BIGINT));
    Map<String, Object> result = null;
    try {
      result = purgeCleanUpProcCall.execute(new HashMap<>());
      if (!CollectionUtils.isEmpty(result)) {
        if (result.get(STATUS_CODE) != null) {
          final Long resultCode = (long)result.get(STATUS_CODE);
          if (resultCode == null || resultCode.longValue() != 0) {
            throw new ETrackPermitException("PURGE_CLEANUP_ERR", "Error while processing/cleaning up the Orphan records."
                + " Status Code " + resultCode + "Status message " + result.get(STATUS_MESSAGE));
          }
        }
      }
    } catch (ETrackPermitException e) {
      throw e;
    } catch (Exception e) {
      throw new ETrackPermitException("PURGE_CLEANUP_GEN_ERR", 
          "General error while processing/cleaning up the Orphan records.", e);
    }
    logger.info("Exiting from Clean Orphan records ");
  }
}

