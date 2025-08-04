package dec.ny.gov.etrack.permit.service.impl;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.permit.entity.Address;
import dec.ny.gov.etrack.permit.entity.Facility;
import dec.ny.gov.etrack.permit.entity.FacilityAddr;
import dec.ny.gov.etrack.permit.entity.FacilityPolygon;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.Public;
import dec.ny.gov.etrack.permit.entity.Role;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.FacilityDetail;
import dec.ny.gov.etrack.permit.model.Organization;
import dec.ny.gov.etrack.permit.model.ProjectDetail;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransformationServiceTest {

  @InjectMocks
  private TransformationService transformationService;

  private String userId = "jxpuvoge";
  private String contextId = "context1234";
  private Long projectId = 1002L;
  private String region = "Region 2";
  private String counties = "Albany";
  private String municipalities = "Albany";
  private String taxMapNumber = "12356";


  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();
	//transformPropertyRelationshipToRoleEntity tests
  @Test
  public void testTransformToFacilityEntityReturnsValidFacilityObject() {
    FacilityDetail detail = getFacilityDetailObject();
    Facility facility = new Facility();
    facility.setProjectId(projectId);
    facility.setFacilityName(detail.getFacilityName());
    facility.setEdbDistrictId(detail.getEdbDistrictId());
    Facility result =
        this.transformationService.transformToFacilityEntity(userId, contextId, detail, projectId);
    assertNotNull(result);
    assertEquals(projectId, result.getProjectId());
  }

  @Test
  public void testTransformToFacilityEntityReturnsNullObjectIfNullFacilityPassedIn() {
    Facility result =
        this.transformationService.transformToFacilityEntity(userId, contextId, null, projectId);
    assertNull(result);
  }

  // transformProjectEntityTests

  @Test
  public void testTransformProjectEntityReturnsValidObject() {
    Project project = getProjectObj();
    ProjectDetail resultDetail =
        this.transformationService.transformProjectEntity(userId, contextId, project);
    assertEquals(project.getProjectId(), resultDetail.getProjectId());
    assertEquals(project.getProjectDesc(), resultDetail.getProjectDesc());
    assertEquals(project.getApplicantTypeCode(), resultDetail.getApplicantTypeCode());
  }


  @Test
  public void testTransformProjectEntityReturnsNullIfNullProjectObjectPassedIn() {
    ProjectDetail resultDetail =
        this.transformationService.transformProjectEntity(userId, contextId, null);
    assertNull(resultDetail);
  }



  // transformToFacilityAddressEntity tests

  @Test
  public void testTransformToFacilityAddressEntityReturnsNullObjIfNullDetailIsGiven() {
    FacilityAddr result = this.transformationService.transformToFacilityAddressEntity(userId,
        contextId, null, projectId);
    assertNull(result);
  }


  @Test
  public void testTransformToFacilityAddressEntityReturnsValidFacAddressObject() {
    FacilityDetail detail = getFacilityDetailObject();
    FacilityAddr result = this.transformationService.transformToFacilityAddressEntity(userId,
        contextId, detail, projectId);
    assertEquals(detail.getAddress().getCity(), result.getCity());
    assertEquals(detail.getAddress().getState(), result.getState());
    assertEquals(detail.getAddress().getStreet1(), result.getStreet1());
    assertEquals(detail.getAddress().getZip(), result.getZip());

  }


  // transformFacilityToProjectDetail

  @Test
  public void testTransformFacilityToProjectDetailReturnsValidObjectFromProjectObj() {
    Project project = getProjectObj();
    Facility facility = getFacilityObj();
    FacilityAddr facilityAddress = getFacilityAddrObj();
    ProjectDetail result =
        this.transformationService.transformFacilityToProjectDetail(userId, contextId, project,
            facility, facilityAddress, null, region, counties, municipalities, taxMapNumber);
    assertEquals(project.getProjectId(), result.getProjectId());
    assertEquals(project.getMailInInd(), result.getMailInInd());
    assertEquals("N", result.getValidatedInd());
  }

  @Test
  public void testTransformFacilityToProjectDetailReturnsCorrectValidatedInd() {
    Project project = getProjectObj();
    Facility facility = getFacilityObj();
    FacilityAddr facilityAddress = getFacilityAddrObj();
    project.setValidatedInd(1);
    ProjectDetail result =
        this.transformationService.transformFacilityToProjectDetail(userId, contextId, project,
            facility, facilityAddress, null, region, counties, municipalities, taxMapNumber);
    assertEquals("Y", result.getValidatedInd());
  }

  @Test
  public void testTransformFacilityToProjectDetailCorrectlyMapsFromFacilityAddrObj() {
    Project project = getProjectObj();
    Facility facility = getFacilityObj();
    FacilityAddr facilityAddress = getFacilityAddrObj();
    ProjectDetail result =
        this.transformationService.transformFacilityToProjectDetail(userId, contextId, project,
            facility, facilityAddress, null, region, counties, municipalities, taxMapNumber);
    assertEquals(facilityAddress.getCity(), result.getFacility().getAddress().getCity());
    assertEquals(facilityAddress.getCountry(), result.getFacility().getAddress().getCountry());
    assertEquals(facilityAddress.getPhoneNumber(),
        result.getFacility().getAddress().getPhoneNumber());
  }

  @Test
  public void testTransformFacilityToProjectDetailCorrectlyMapsFromFacPolyonObj() {
    Project project = getProjectObj();
    Facility facility = getFacilityObj();
    FacilityAddr facilityAddress = getFacilityAddrObj();
    FacilityPolygon facilityPolygon = getFacPolygonObj();
    facilityPolygon.setNytmeCoordinate(BigDecimal.ONE);
    facilityPolygon.setNytmnCoordinate(BigDecimal.ONE);
    project.setReceivedDate(null);
    ProjectDetail result = this.transformationService.transformFacilityToProjectDetail(userId,
        contextId, project, facility, facilityAddress, facilityPolygon, region, counties,
        municipalities, taxMapNumber);
    assertEquals(facilityPolygon.getPolygonGisId(), result.getPolygonId());
    assertEquals(facilityPolygon.getLatitude(), result.getLatitude());
    assertEquals(facilityPolygon.getLongitude(), result.getLongitude());
  }

  // transformToProjectEntity tests

  @Test
  public void testTransformToProjectEntityCorrectlyMapsProjectDetailObj() {
    ProjectDetail detail = getProjectDetailObj();
    Project result = this.transformationService.transformToProjectEntity(userId, contextId, detail);
    assertEquals(detail.getMailInInd(), result.getMailInInd());
    assertEquals(detail.getApplicantTypeCode(), result.getApplicantTypeCode());
    assertEquals(detail.getProposedUseCode(), result.getProposedUseCode());
  }

  @Test
  public void testTransformToProjectEntityCorrectlyMapsSeqrClassification() {
    ProjectDetail detail = getProjectDetailObj();
    detail.setClassifiedUnderSeqr("2");
    Project result = this.transformationService.transformToProjectEntity(userId, contextId, detail);
    assertEquals(Integer.parseInt(detail.getClassifiedUnderSeqr()), result.getSeqrInd());
  }

  @Test
  public void testTransformToProjectEntityThrowsBREForIncorrectDateFormat() {
    exceptionRule.expect(BadRequestException.class);
    exceptionRule.expectMessage("Received Date is passed incorrect format.");
    ProjectDetail detail = getProjectDetailObj();
    detail.setReceivedDate("Bad date");
    this.transformationService.transformToProjectEntity(userId, contextId, detail);
  }

  // transformInputAddressToEntityAddress tests
  @Test
  public void testTransformInputAddressToEntityAddressMapsApplicantAddressCorrectly() {
    Applicant applicant = getApplicantObj();
    Address entityAddress = new Address();
    entityAddress = this.transformationService.transformInputAddressToEntityAddress(userId,
        contextId, projectId, applicant, entityAddress);
    assertEquals(applicant.getAddress().getStreetAdr1(), entityAddress.getStreet1());
    assertEquals(applicant.getAddress().getStreetAdr2(), entityAddress.getStreet2());
    assertEquals(applicant.getAddress().getCity(), entityAddress.getCity());
    assertEquals(applicant.getAddress().getState(), entityAddress.getState());
  }

  @Test
  public void testTransformInputAddressToEntityAddressMapsContactDataCorrectly() {
    Applicant applicant = getApplicantObj();
    Address entityAddress = new Address();
    entityAddress = this.transformationService.transformInputAddressToEntityAddress(userId,
        contextId, projectId, applicant, entityAddress);
    assertEquals(applicant.getContact().getCellNumber(), entityAddress.getCellPhoneNumber());
    assertEquals(applicant.getContact().getHomePhoneNumber(), entityAddress.getHomePhoneNumber());
    assertEquals(applicant.getContact().getEmailAddress(), entityAddress.getEmailAddress());
  }


  // transformPropertyRelationshipToRoleEntity tests
  @Test
  public void testTransformPropertyRelationshipToRoleEntityThrowsBREForInvalidCategory() {
    exceptionRule.expect(BadRequestException.class);
    exceptionRule.expectMessage("Invalid Category Code is passed.");
    this.transformationService.transformPropertyRelationshipToRoleEntity(userId, contextId,
        getApplicantObj(), 1L, getPublicObj(), "Invalid", getRolesList());
  }

  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithContactAgentWithNoRoles() {
    String category = "C";
    Integer expectedRoleType = 5;
    Long addressId = 1L;
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, getApplicantObj(), addressId, getPublicObj(), category, getRolesList());
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(0).getCreatedById(), userId);
    assertEquals(result.get(0).getAddressId(), addressId);
  }

  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithContactAgentWithRoles() {
    String category = "C";
    Integer expectedRoleType = 5;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, getApplicantObj(), addressId, existingPublic, category, getRolesList());
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(0).getAddressId(), addressId);
    assertNull(result.get(0).getLegallyResponsibleTypeCode());
  }


  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithPublicWithRoles() {
    String category = "P";
    Integer expectedRoleType = 6;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    Applicant applicant = getApplicantObj();
    applicant.setPropertyRelationships(Arrays.asList(1));
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, applicant, addressId, existingPublic, category, getRolesList());
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(0).getChangeCtr(), 0);
    assertEquals(result.get(0).getLegallyResponsibleTypeCode(), 1);
  }


  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithOwnerWithRoles() {
    String category = "O";
    Integer expectedRoleType = 6;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    Applicant applicant = getApplicantObj();
    applicant.setPropertyRelationships(Arrays.asList(1));
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, applicant, addressId, existingPublic, category, getRolesList());
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(0).getModifiedById(), userId);
    assertNull(result.get(0).getLegallyResponsibleTypeCode());
  }

  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithOwnerWithNoRoles() {
    String category = "O";
    Integer expectedRoleType = 6;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    Applicant applicant = getApplicantObj();
    applicant.setPropertyRelationships(Arrays.asList(1));
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, applicant, addressId, existingPublic, category, getRolesList());
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(0).getCreatedById(), userId);
  }

  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithOwnerAlreadyAsApplicant() {
    String category = "O";
    Integer expectedRoleType = 6;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    List<Role> roles = getRolesList();
    roles.get(0).setRoleTypeId(6);
    Applicant applicant = getApplicantObj();
    applicant.setPropertyRelationships(Arrays.asList(1));
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, applicant, addressId, existingPublic, category, roles);
    assertEquals(result.get(0).getRoleTypeId(), expectedRoleType);
  }


  @Test
  public void testTransformPropertyRelationshipToRoleEntityWithOwner() {
    String category = "O";
    Integer expectedRoleType = 6;
    Long addressId = 1L;
    Public existingPublic = getPublicObj();
    List<Role> roles = getRolesList();
    roles.get(0).setRoleTypeId(4);
    Applicant applicant = getApplicantObj();
    applicant.setPropertyRelationships(Arrays.asList(1));
    List<Role> result = this.transformationService.transformPropertyRelationshipToRoleEntity(userId,
        contextId, applicant, addressId, existingPublic, category, roles);
    assertEquals(result.get(1).getRoleTypeId(), expectedRoleType);
    assertEquals(result.get(1).getCreatedById(), userId);
    assertEquals(result.get(1).getAddressId(), addressId);
  }



  // transformApplicantToPublicEntity tests
  @Test
  public void testTransformApplicantToPublicEntityWithNoIndividualOrOrg() {
    Applicant applicant = getApplicantObj();
    String publicTypeCode = "Test";
    Public publicDetail = getPublicObj();
    applicant.setPublicTypeCode(publicTypeCode);
    applicant.setOrganization(getOrganizationObj());
    Public result = this.transformationService.transformApplicantToPublicEntity(userId, contextId,
        projectId, applicant, publicDetail);
    assertEquals(publicTypeCode, result.getPublicTypeCode());
    assertEquals(projectId, result.getProjectId());
  }

  @Test
  public void testTransformApplicantToPublicEntityWithNonIncorporatedOrganization() {
    Applicant applicant = getApplicantObj();
    String publicTypeCode = "Test";
    Public publicDetail = getPublicObj();
    applicant.setPublicTypeCode(publicTypeCode);
    applicant.setOrganization(getOrganizationObj());
    Organization org = getOrganizationObj();
    Public result = this.transformationService.transformApplicantToPublicEntity(userId, contextId,
        projectId, applicant, publicDetail);
    assertEquals(org.getTaxPayerId(), result.getTaxpayerId());
    assertEquals(0, result.getIncorpInd());
    assertNull(result.getBusinessValidatedInd());
  }

  @Test
  public void testTransformApplicantToPublicEntityWithIncorporatedOrganization() {
    Applicant applicant = getApplicantObj();
    String publicTypeCode = "Test";
    Public publicDetail = getPublicObj();
    applicant.setPublicTypeCode(publicTypeCode);
    applicant.setOrganization(getOrganizationObj());
    applicant.getOrganization().setIsIncorporated("Y");
    applicant.getOrganization().setBusinessVerified("Y");
    Public result = this.transformationService.transformApplicantToPublicEntity(userId, contextId,
        projectId, applicant, publicDetail);
    assertEquals(1, result.getBusinessValidatedInd());
    assertEquals(1, result.getIncorpInd());

  }

  // private methods

  private Organization getOrganizationObj() {
    Organization organization = new Organization();
    organization.setBusOrgName("Test Org");
    organization.setIncorporationState("New York");
    organization.setTaxPayerId("1234");
    organization.setIsIncorporated("N");
    return organization;
  }

  private Public getPublicObj() {
    Public publicObj = new Public();
    return publicObj;
  }

  private List<Role> getRolesList() {
    List<Role> roleList = new ArrayList<>();
    Role role = new Role();
    roleList.add(role);
    return roleList;
  }

  private Applicant getApplicantObj() {
    Applicant applicant = new Applicant();
    applicant.setAddress(getApplicantAddressObj());
    applicant.setContact(getContactObj());
    return applicant;
  }

  private ApplicantAddress getApplicantAddressObj() {
    ApplicantAddress applicantAddress = new ApplicantAddress();
    applicantAddress.setState("New York");
    applicantAddress.setStreetAdr1("PEarl St");
    applicantAddress.setStreetAdr2("State St");
    applicantAddress.setAddressId(10L);
    applicantAddress.setAddressId(1L);
    applicantAddress.setEdbAddressId(10L);
    return applicantAddress;

  }

  private Contact getContactObj() {
    Contact contact = new Contact();
    contact.setCellNumber("1234567");
    contact.setEmailAddress("j@yahoo.com");
    contact.setHomePhoneNumber("56678899");
    contact.setWorkPhoneNumber("987654");
    return contact;
  }

  private ProjectDetail getProjectDetailObj() {
    ProjectDetail detail = new ProjectDetail();
    detail.setMailInInd(1);
    detail.setApplicantTypeCode(2);
    detail.setProposedUseCode("Code");
    detail.setReceivedDate("12/23/2023");
    return detail;
  }


  private FacilityPolygon getFacPolygonObj() {
    FacilityPolygon polygon = new FacilityPolygon();
    polygon.setPolygonGisId("GISID");
    polygon.setLatitude("1234");
    polygon.setLongitude("567");
    polygon.setNytmeCoordinate(new BigDecimal("12.2"));
    polygon.setPolygonTypeCode(2);
    return polygon;
  }

  private FacilityAddr getFacilityAddrObj() {
    FacilityAddr facilityAddr = new FacilityAddr();
    facilityAddr.setCity("Albany");
    facilityAddr.setStreet1("Pearl st");
    facilityAddr.setStreet2("State st");
    facilityAddr.setState("New York");
    facilityAddr.setZip("12207");
    facilityAddr.setPhoneNumber("4562312");
    return facilityAddr;
  }


  private Facility getFacilityObj() {
    Facility facility = new Facility();
    facility.setFacilityName("Lemk Property");
    facility.setChgBoundaryReason("Reason");
    facility.setEdbDistrictId(10L);
    return facility;
  }

  private Project getProjectObj() {
    Project project = new Project();
    project.setProjectId(100L);
    project.setApplicantTypeCode(10);
    project.setMailInInd(1);
    project.setProjectDesc("Desc");
    project.setProposedUseCode("test");
    project.setReceivedDate(new Date(2024, 11, 10));
    return project;
  }


  private FacilityDetail getFacilityDetailObject() {
    FacilityDetail detail = new FacilityDetail();
    detail.setDecId("0-9999-00214");
    detail.setFacilityName("Lemk Property");
    detail.setEdbDistrictId(10L);
    detail.setAddress(getFacAddressObj());
    return detail;
  }

  private FacilityAddress getFacAddressObj() {
    FacilityAddress address = new FacilityAddress();
    address.setCity("Albany");
    address.setCountry("USA");
    address.setPhoneNumber("4560076");
    address.setState("NY");
    address.setStreet1("Pearl St");
    address.setStreet2("State St");
    address.setZip("12207");
    address.setZipExtension("111");
    return address;
  }

}
