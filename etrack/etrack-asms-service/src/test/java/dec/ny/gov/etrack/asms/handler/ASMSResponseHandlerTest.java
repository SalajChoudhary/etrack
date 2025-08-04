package dec.ny.gov.etrack.asms.handler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.asms.exception.ETrackASMSException;
import dec.ny.gov.etrack.asms.model.AuthResponse;
import dec.ny.gov.etrack.asms.model.User;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups.Group;
import dec.ny.gov.etrack.asms.schema.SchemaSecurityObjects.Groups.Group.SchemaSecurityObject;
import dec.ny.gov.etrack.asms.schema.SchemaUser;
import dec.ny.gov.etrack.asms.schema.SchemaUser.Name;

@RunWith(SpringJUnit4ClassRunner.class)
public class ASMSResponseHandlerTest {

  SchemaSecurityObjects securityObjects = null;
  
  @InjectMocks
  private ASMSResponseHandler handler;
  
  
  @Before
  public void setUp() {
    securityObjects = new SchemaSecurityObjects();
    securityObjects.setApplication("Test");
    securityObjects.setGuid("TestGuid");
  }

  @Test(expected = ETrackASMSException.class)
  public void testThrowsExceptionWhenNoSecurityObjectsPassed() {
    handler.handleASMSObjectAuthResponse("userId","contextId", null);
  }

  @Test
  public void testReturnsEmptyRolesWhenNoGroupsInASMSResponse() {
    AuthResponse authResponse = (AuthResponse) handler.handleASMSObjectAuthResponse("userId","contextId", securityObjects);
    assertEquals("userId", authResponse.getUserId());
    assertEquals("TestGuid", authResponse.getGuid());
    assertNull(authResponse.getRoles());
    assertNull(authResponse.getPermissions());
  }
  
  @Test
  public void testReturnsRolesSuccessfullyWhenGroupObjectsReturnsInASMSResponse() {
    Group group = new Group();
    group.setName("SystemAdmin");
    Groups groups = new Groups();
    groups.getGroup().add(group);
    securityObjects.setGroups(groups);
    AuthResponse authResponse = (AuthResponse) handler.handleASMSObjectAuthResponse("userId","validRole", securityObjects);
    assertEquals("userId", authResponse.getUserId());
    assertEquals("TestGuid", authResponse.getGuid());
    assertNotNull(authResponse.getRoles());
    assertEquals("SystemAdmin", authResponse.getRoles().get(0));
  }
  
  @Test
  public void testReturnsEmptyPermissionsWhenNoGroupObjectsReturnsInASMSResponse() {
    Group group = new Group();
    group.setName("SystemAdmin");
    Groups groups = new Groups();
    groups.getGroup().add(group);
    securityObjects.setGroups(groups);
    AuthResponse authResponse = (AuthResponse) handler.handleASMSObjectAuthResponse("userId","contextId", securityObjects);
    assertEquals("userId", authResponse.getUserId());
    assertEquals("TestGuid", authResponse.getGuid());
    assertNotNull(authResponse.getRoles());
    assertNull(authResponse.getPermissions());
  }
  
  @Test
  public void testReturnsPermissionsWhenGroupObjectsReturnsObjectInASMSResponse() {
    Group group = new Group();
    group.setName("SystemAdmin");
    SchemaSecurityObject securityObject = new SchemaSecurityObject();
    securityObject.setName("Add_Permission");
    group.getObject().add(securityObject);
    Groups groups = new Groups();
    groups.getGroup().add(group);
    securityObjects.setGroups(groups);
    AuthResponse authResponse = (AuthResponse) handler.handleASMSObjectAuthResponse("userId", "contextId", securityObjects);
    assertEquals("userId", authResponse.getUserId());
    assertEquals("TestGuid", authResponse.getGuid());
    assertNotNull(authResponse.getRoles());
    assertNotNull(authResponse.getPermissions());
    assertEquals("Add_Permission", authResponse.getPermissions().iterator().next());
  }
  
  @Test
  public void testTransformASMSUserResponseReturnsSuccessTransformation() {
    SchemaUser schemaUser = new SchemaUser();
    Name name = new Name();
    name.setFirst("First");
    name.setLast("last");
    schemaUser.setName(name);
    schemaUser.setEmailAddress("email");
    schemaUser.setGuid("guid");
    User response = (User) handler.transformASMSUserResponse("userId", "contextId", schemaUser);
    Assert.assertEquals("First", response.getName().getFirst());
    Assert.assertEquals("last", response.getName().getLast());
    Assert.assertEquals("email", response.getEmailAddress());
  }
  
  @After
  public void tearDown() {
    securityObjects = null;
  }
  
}
