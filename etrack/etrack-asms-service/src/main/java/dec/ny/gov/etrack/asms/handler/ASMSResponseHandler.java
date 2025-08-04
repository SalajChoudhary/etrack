package dec.ny.gov.etrack.asms.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.asms.exception.ETrackASMSException;
import dec.ny.gov.etrack.asms.model.AuthResponse;
import dec.ny.gov.etrack.asms.model.Name;
import dec.ny.gov.etrack.asms.model.User;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups.Group;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups.Group.SchemaSecurityObject;
import dec.ny.gov.etrack.asms.schema.SchemaUser;

@Component
public class ASMSResponseHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(ASMSResponseHandler.class.getName().toString());

  /**
   * Transform the ASMS data into eTrack model.
   * 
   * @param userId - Unique user Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param schemaSecurityObjects - User response received from ASMS service.
   * 
   * @return - Transformed eTrack response.
   */
  public Object handleASMSObjectAuthResponse(final String userId, final String contextId,
      SchemaSecurityObjects schemaSecurityObjects) {

    logger.info("Entering into handleASMSObjectAuthResponse, ContextId {}", contextId);
    AuthResponse authResponse = new AuthResponse();
    try {
      authResponse.setUserId(userId);
      authResponse.setGuid(schemaSecurityObjects.getGuid());
      Groups groupObj = schemaSecurityObjects.getGroups();
      if (groupObj != null) {
        List<Group> groups = schemaSecurityObjects.getGroups().getGroup();
        if (!CollectionUtils.isEmpty(groups)) {
          logger.debug("Groups received from the response {} ", groups);
          List<String> roles = new ArrayList<>();
          Set<String> permissions = new HashSet<>();
//          List<String> permissions = new ArrayList<>();
          for (Group group : groups) {
            roles.add(group.getName());
            List<SchemaSecurityObject> objects = group.getObject();
            if (!CollectionUtils.isEmpty(objects)) {
              for (SchemaSecurityObject object : group.getObject()) {
                permissions.add(object.getName());
              }
              authResponse.setPermissions(permissions);
            }
          }
          authResponse.setRoles(roles);
        }        
      }
    } catch (Exception e) {
      throw new ETrackASMSException("Error while processing the ASMS Response ", e);
    }
    logger.info("Exiting from handleASMSObjectAuthResponse, ContextId {}", contextId);
    return authResponse;
  }

  /**
   * Transform the ASMS User response to eTrack model.
   * 
   * @param userId - Unique user Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param schemaUser - User details received from ASMS.
   * 
   * @return - Transformed response.
   */
  public Object transformASMSUserResponse(final String userId, 
      final String contextId, final SchemaUser schemaUser) {
    User user = new User();
    user.setId(schemaUser.getId());
    user.setGuid(schemaUser.getGuid());
    user.setLoginId(schemaUser.getLoginId());
    user.setEmailAddress(schemaUser.getEmailAddress());
    Name name = new Name(schemaUser.getName().getFirst(), 
        schemaUser.getName().getLast());
    user.setName(name);
    user.setUserType(schemaUser.getUserType());
    user.setLoginStatus(schemaUser.getLoginStatus());
    user.setDivision(schemaUser.getDivision());
    user.setWorkLocation(schemaUser.getWorkLocation());
    user.setTrustLevel(schemaUser.getTrustLevel());
    return user;
  }
}
