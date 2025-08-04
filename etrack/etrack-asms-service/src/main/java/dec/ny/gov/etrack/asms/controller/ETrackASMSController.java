package dec.ny.gov.etrack.asms.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.asms.service.ETrackASMSService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackASMSController {

  @Autowired
  private ETrackASMSService eTrackASMSService;

  @Autowired
  private JwtDecoder jwtDecoder;

  private static Logger logger = LoggerFactory.getLogger(ETrackASMSController.class.getName());


  /**
   * Retrieve the user authorization details.
   * 
   * @param userId - User who initiates this request.
   * @param token - JWT token.
   * 
   * @return - User authorization details.
   */
  @GetMapping(value = "/user/roles", produces = "application/json",
      headers = {"Accept=application/json"})
  @ResponseBody
  @ApiOperation(value = "Retrieve the User Roles details by calling internal ITS ASMS Service.")
  public ResponseEntity<Object> getUserAuthDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve the User's auth Details for the user {}, Context id {}",
        userId, contextId);
    token = token.replaceAll("Bearer ", "");
    String guid = (String) jwtDecoder.decode(token).getClaims().get("ppid");
    logger.info("Guid will be used to retrieve the User auth details {}", guid);
    return eTrackASMSService.getUserAuthDetails(userId, guid, contextId);
  }

  /**
   * Returns the User roles and permissions for the input user id.
   * 
   * @param userId - User who initiates this request.
   * @param token - JWT Token.
   * 
   * @return - User roles and permissions.
   */
  @GetMapping(value = "/user/authInfo", produces = "application/json",
      headers = {"Accept=application/json"})
  @ResponseBody
  @ApiOperation(
      value = "Retrieve the User Roles and Permission details for the input user id. by calling internal ITS ASMS Service.")
  public ResponseEntity<Object> getUserRoles(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieve the User's auth Details for the user {}, Context id {}",
        userId, contextId);

    token = token.replaceAll("Bearer ", "");
    String guid = (String) jwtDecoder.decode(token).getClaims().get("ppid");
    return eTrackASMSService.getRoles(userId, guid, contextId);
  }
}
