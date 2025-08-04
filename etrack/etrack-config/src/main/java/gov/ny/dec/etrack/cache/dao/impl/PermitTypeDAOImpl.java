package gov.ny.dec.etrack.cache.dao.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import gov.ny.dec.etrack.cache.dao.PermitTypeDAO;
import gov.ny.dec.etrack.cache.entity.PermitType;

@Repository
public class PermitTypeDAOImpl implements PermitTypeDAO {
  
  private static final Logger logger = LoggerFactory.getLogger(PermitTypeDAOImpl.class.getName());
  
  @Autowired
  @Qualifier("eTrackPermitTypeProcCall")
  private SimpleJdbcCall eTrackPermitTypeProcCall;
  
  /**
   * Retrieve all the Permit Types for the input project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Returns the list of Permit Types.
   */
  @SuppressWarnings("unchecked")
  public List<PermitType> findAllPermitTypesByProjectId(
      final String userId, final String contextId, final Long projectId) {
    
    logger.info("Entering into findAllPermitTypesByProjectId(). "
        + "ProjectId {}, User Id {} Context Id {}", userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put("p_project_id", projectId);

    eTrackPermitTypeProcCall.declareParameters(new SqlParameter("p_project_id", Types.BIGINT),
        new SqlOutParameter("p_permit_cur", -10));

    eTrackPermitTypeProcCall.addDeclaredRowMapper("p_permit_cur",
        BeanPropertyRowMapper.newInstance(PermitType.class));
    
    Map<String, Object> response = eTrackPermitTypeProcCall.execute(inputParam);
    
    if (!CollectionUtils.isEmpty(response)) {
      return  (List<PermitType>)response.get("p_permit_cur");
    }
    return null;
  }


  /**
   * Retrieve all the Active Permit Types for the input project id.
   * * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * * @return - Returns the list of active Permit Types.
   */
  @SuppressWarnings("unchecked")
  public List<PermitType> findAllActivePermitTypesByProjectId(
          final String userId, final String contextId, final Long projectId) {

    logger.info("Entering into findAllActivePermitTypesByProjectId(). "
            + "ProjectId {}, User Id {} Context Id {}", userId, contextId);
    Map<String, Object> inputParam = new HashMap<>();
    inputParam.put("p_project_id", projectId);
    // Rationale: Adding a new input parameter to filter by active status. This assumes the stored procedure can handle this parameter.
    inputParam.put("p_active_ind", 1);

    eTrackPermitTypeProcCall.declareParameters(new SqlParameter("p_project_id", Types.BIGINT),
            new SqlParameter("p_active_ind", Types.INTEGER), // Rationale: Declaring the new parameter.
            new SqlOutParameter("p_permit_cur", -10));

    eTrackPermitTypeProcCall.addDeclaredRowMapper("p_permit_cur",
            BeanPropertyRowMapper.newInstance(PermitType.class));

    Map<String, Object> response = eTrackPermitTypeProcCall.execute(inputParam);

    if (!CollectionUtils.isEmpty(response)) {
      return  (List<PermitType>)response.get("p_permit_cur");
    }
    return null;
  }
}
