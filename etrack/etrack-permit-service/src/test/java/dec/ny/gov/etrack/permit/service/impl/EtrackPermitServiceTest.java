package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.Application;
import dec.ny.gov.etrack.permit.entity.Facility;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.Public;
import dec.ny.gov.etrack.permit.entity.PublicDetail;
import dec.ny.gov.etrack.permit.entity.Role;
import dec.ny.gov.etrack.permit.entity.TransactionTypeRule;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.ApplicationPermit;
import dec.ny.gov.etrack.permit.model.ApplicationPermitDetail;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.FacilityDetail;
import dec.ny.gov.etrack.permit.model.PolygonStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.repo.AddressRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationContactAssignmentRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.FacilityRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;
import dec.ny.gov.etrack.permit.repo.PublicRepo;
import dec.ny.gov.etrack.permit.repo.TransactionTypeRuleRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class EtrackPermitServiceTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@InjectMocks
	private ETrackPermitServiceImpl permitService;
	
	@Mock
	private ProjectActivityRepo projectActivityRepo;
	
	
	@Mock
	private RestTemplate businessVerificationRestTemplate;
	
	@Mock
	private ProjectNoteRepo projectNoteRepo;
	
	@Mock
	private AddressRepo addressRepo;
	
	@Mock
	private ApplicationRepo applicationRepo;
	
	@Mock
	private FacilityRepo facilityRepo;

	
	@Mock
	private ProjectRepo projectRepo;
	
	@Mock
	private TransformationService transformationService;
	
	@Mock
	private TransactionTypeRuleRepo transactionTypeRuleRepo;
	
	@Mock
    private ApplicationContactAssignmentRepo applicationContactAssignmentRepo;
	
	@Mock
	private ETrackPermitDAO etrackPermitDao;
	
	
	
	
	@Mock
	private PublicRepo publicRepo;
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;
	private String token = "34567";
	private String dateString = "12/23/2023";
	private int activityStatusId = 1;
	private int projectActivityStatusId = 10;
	private String municipality = "Muni";
	private Long applicationId =321L;
	private String permitTypeCode = "CE";
	private Long batchId = 1L;

	//getProjectDetails test cases
	@Test
	public void testGetProjectDetailsThrowsBREForNoProjectsReturned() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no project associated with this input id");
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		permitService.getProjectDetails(userId, contextId, projectId);
	}
	
	@Test
	public void testGetProjectDetailsCallsTransformationService() {
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		permitService.getProjectDetails(userId, contextId, projectId);
		verify(transformationService).transformProjectEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any());
	}
	
	//getPermitTypes test cases
	@Test
	public void testGetPermitTypesReturnsCorrectPermitTypes() {
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(getApplicationList());
		List<String> result =	permitService.getPermitTypes(userId, contextId, projectId);
		assertEquals(permitTypeCode, result.get(0));
	}
	
	@Test
	public void testGetPermitTypesThrowsDNFEForNoDataReturned() {
		expectedException.expect(DataNotFoundException.class);
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		permitService.getPermitTypes(userId, contextId, projectId);
		
	}
	
	
	//removeApplicationPermit test cases
	@Test
	public void testRemoveApplicationPermitCallsRepoToDeleteApplication() {
		when(applicationRepo.findByApplicationIdAndProjectIdAndPermitTypeCode(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.of(getApplicationObj()));
		permitService.removeApplicationPermit(userId, contextId, projectId, applicationId, permitTypeCode);
		verify(applicationRepo).deleteById(Mockito.anyLong());
	}
	
	
	@Test
	public void testRemoveApplicationPermitThrowsBREForInvalidProjectId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Permit applications available for the Project id");
		when(applicationRepo.findByApplicationIdAndProjectIdAndPermitTypeCode(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
		permitService.removeApplicationPermit(userId, contextId, projectId, applicationId, permitTypeCode);
	}
	
	//saveApplicationPermits test cases
	@Test
	public void testSaveApplicationPermitsSavesConstructionApplicationPermits() {
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		ApplicationPermit permit = getApplicationPermitObj();
		permit.setDartPermits(getApplicationPermitDetailList());
		permit.setEtrackPermits(null);
		when(applicationRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getApplicationObj()));
		when(transactionTypeRuleRepo.findTranstypeAndAssociateDetails(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getTransactionTypeRuleList());
		permitService.saveApplicationPermits(userId, contextId, projectId, permit);
		verify(applicationRepo, times(2)).saveAll(Mockito.anyIterable());
	}
	
	@Test
	public void testSaveApplicationPermitsSavesNonConstructionApplicationPermits() {;
		Project project = getProjectObj();
		project.setConstrnType(1);
		ApplicationPermit permit = getApplicationPermitObj();
		permit.setConstrnType(null);
		permit.setDartPermits(null);
		permit.setEtrackPermits(getApplicationPermitDetailList());
		when(applicationRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getApplicationObj()));
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
		when(transactionTypeRuleRepo.findTranstypeAndAssociateDetails(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getTransactionTypeRuleList());
		permitService.saveApplicationPermits(userId, contextId, projectId, permit);
		verify(applicationRepo).saveAll(Mockito.anyIterable());
	}
	
	
	
	//storeValidatorForStep test cases
	@Test
	public void testStoreValidatorForStepSavesValidatorForExisting() {
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActivityList());
		permitService.storeValidatorForStep(userId, contextId, projectId, "cat", 1, 2);
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	@Test
	public void testStoreValidatorForStepSavesValidatorForNew() {
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		permitService.storeValidatorForStep(userId, contextId, projectId, "cat", 1, 1);
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	//updateExistingPermitAmendDetails test cases
	@Test
	public void testUpdateExistingPermitAmendDetails() {
		ApplicationPermitDetail detail = getApplicationPermitDetailList().get(0);
		detail.setApplicationId(10L);
		detail.setEdbApplnId(1L);
		detail.setPermitTypeCode(permitTypeCode);
		detail.setBatchId(batchId);
		detail.setModExtReason("Reason");
		List<ApplicationPermitDetail> details = new ArrayList<>();
		details.add(detail);
		permitService.updateExistingPermitAmendDetails(userId, contextId, projectId, details);
		verify(applicationRepo).updatePermitformSubmissionDetails(detail.getModExtReason(), null, detail.getApplicationId(), detail.getEdbApplnId(), detail.getPermitTypeCode(), detail.getBatchId(), userId);
	}
	
	
	@Test
	public void testUpdateExistingPermitAmendDetailsThrowsBREForInvalidCompletionDate() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Incorrect details are passed");
		ApplicationPermitDetail detail = getApplicationPermitDetailList().get(0);
		detail.setEstCompletionDate("Invalid");
		List<ApplicationPermitDetail> details = new ArrayList<>();
		details.add(detail);
		permitService.updateExistingPermitAmendDetails(userId, contextId, projectId, details);
	}
	
	@Test
	public void testUpdateExistingPermitAmendDetailsThrowsBREForNoExistingPermits() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Existing permit details are passed for the amendment request");
		permitService.updateExistingPermitAmendDetails(userId, contextId, projectId, null);
		}

	
	private List<Application> getApplicationList(){
		List<Application> applications = new ArrayList<>();
		Application application = new Application();
		application.setPermitTypeCode(permitTypeCode);
		application.setApplicationId(applicationId);
		application.setBatchIdEdb(batchId);
		application.setProgId("123");
		applications.add(application);
		return applications;
	}
	//retrievePermitDetails test cases
	@Test
	public void testRetrievePermitDetailsReturnsValidData() {
		when(applicationRepo.findAllByBatchIdEdbAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getApplicationList());
		List<ApplicationPermitDetail> resultList = permitService.retrievePermitDetails(userId, contextId, projectId, batchId);
		ApplicationPermitDetail result = resultList.get(0); 
		assertEquals("123", result.getProgramId());
		assertEquals(applicationId, result.getApplicationId());
		assertEquals(permitTypeCode, result.getPermitTypeCode());
	}
	
	@Test
	public void testRetrievePermitDetailsReturnEmptyListIfNoApplicationsFound() {
		when(applicationRepo.findAllByBatchIdEdbAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		List<ApplicationPermitDetail> resultList = permitService.retrievePermitDetails(userId, contextId, projectId, batchId);
		assertTrue(CollectionUtils.isEmpty(resultList));
	}

	//storeReviewedPermits test cases
	private Map<String, List<ReviewedPermit>> getReviewedPermitMap(){
		Map<String, List<ReviewedPermit>> permits = new HashMap<>();
		List<ReviewedPermit> reviewedPermits = new ArrayList<>();
		ReviewedPermit permit = new ReviewedPermit();
		permit.setApplicationId(applicationId);
		permit.setBatchId(batchId);
		permit.setPermitTypeCode(permitTypeCode);
		permit.setTrackingInd(1);
		reviewedPermits.add(permit);
		permits.put("etrack-permits", reviewedPermits);
		return permits;
		
	}
	
	@Test
	public void testStoreReviewedPermits() {
		Map<String, List<ReviewedPermit>> resultMap = (Map<String, List<ReviewedPermit>>)  permitService.storeReviewedPermits(userId, contextId, getReviewedPermitMap());
		List<ReviewedPermit> resultList =	resultMap.get("etrack-permits");
		ReviewedPermit result = resultList.get(0);
		assertEquals(applicationId, result.getApplicationId());
		assertEquals(permitTypeCode, result.getPermitTypeCode());
		
	}
	
	
	//updateAmendedApplicationTransTypes test cases
	@Test
	public void testUpdateAmendedApplicationTransTypesSavesUpdatedApplication() {
		ApplicationPermit permits = getApplicationPermitObj();
		permits.setDartPermits(getApplicationPermitDetailList());
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		when(applicationRepo.findAllByIdAndProjectId(Mockito.anySet(), Mockito.anyLong())).thenReturn(getApplicationList());
		when(transactionTypeRuleRepo.findTranstypeAndAssociateDetails(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getTransactionTypeRuleList());
		permitService.updateAmendedApplicationTransTypes(userId, contextId, projectId, permits);
		verify(applicationRepo).saveAll(Mockito.anyIterable());
	}
	
	@Test
	public void testUpdateAmendedApplicationTransTypesThrowsBREForInvalidProjectId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Project Found associated with the id");
		ApplicationPermit permits = getApplicationPermitObj();
		permits.setDartPermits(getApplicationPermitDetailList());
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		permitService.updateAmendedApplicationTransTypes(userId, contextId, projectId, permits);
	}
	
	@Test
	public void testUpdateAmendedApplicationTransTypesThrowsBREForNoDartPermits() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no permits requested to update the Trans Type");
		ApplicationPermit permits = getApplicationPermitObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		permitService.updateAmendedApplicationTransTypes(userId, contextId, projectId, permits);
	}
	
	@Test
	public void testUpdateAmendedApplicationTransTypesThrowsBREForNoApplicationsFound() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Applications Found associated for the project id");
		ApplicationPermit permits = getApplicationPermitObj();
		permits.setDartPermits(getApplicationPermitDetailList());
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		permitService.updateAmendedApplicationTransTypes(userId, contextId, projectId, permits);
	}
	

	
	
	

	//removeApplicationPermits test cases: NOT COMPLETED
//	@Test
//	public void testGetProjectDetailsCallsTransformationService() {
//		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
//		permitService.getProjectDetails(userId, contextId, projectId);
//		verify(transformationService).transformProjectEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any());
//	}
//	
//	//getPermitTypes test cases
//	@Test
//	public void testGetPermitTypesReturnsCorrectPermitTypes() {
//		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(getApplicationList());
//		List<String> result =	permitService.getPermitTypes(userId, contextId, projectId);
//		assertEquals(permitTypeCode, result.get(0));
//	}
//	
//	@Test
//	public void testGetPermitTypesThrowsDNFEForNoDataReturned() {
//		expectedException.expect(DataNotFoundException.class);
//		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
//		permitService.getPermitTypes(userId, contextId, projectId);
//		
//	}


	

	
	//assignContacts test cases
	@Test
	public void testAssignContactsThrowsBREForNoContacts() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no assigned contacts for this project");
		permitService.assignContacts(userId, contextId, projectId, null);
	}
	
	@Test
	public void testAssignContactsThrowsBREForInsufficientContactDetails() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("One or more Permit Contact Assignment details is missing");
		permitService.assignContacts(userId, contextId, projectId, getApplicationPermitDetailList());
	}
	
	@Test
	public void testAssignContactsSavesAssignedContacts() {
		List<ApplicationPermitDetail> assignedContacts = new ArrayList<>();
		ApplicationPermitDetail detail = new ApplicationPermitDetail();
		detail.setApplicationId(applicationId);
		detail.setRoleId(1L);
		detail.setPermitFormId(100L);
		assignedContacts.add(detail);
		permitService.assignContacts(userId, contextId, projectId, assignedContacts);
		verify(applicationContactAssignmentRepo).saveAll(Mockito.anyIterable());
	}
	

	//saveAdditionalApplicationPermit test cases:
	
	@Test
	public void testSaveAdditionalApplicationPermitSavesAdditionalPermitToEtrack() {
		ApplicationPermit permit = getApplicationPermitObj();
		
		ArrayList<ApplicationPermitDetail> eTrackPermits = new ArrayList<>();
		ApplicationPermitDetail detail = new ApplicationPermitDetail();
		detail.setTransType("NEW");
		detail.setBatchId(batchId);
		detail.setPermitTypeCode("CE");
		eTrackPermits.add(detail);
		permit.setEtrackPermits(eTrackPermits);
		Application application = getApplicationObj();
		application.setApplicationId(applicationId);
		application.setCreatedById(userId);
		application.setProjectId(projectId);
		application.setBatchIdEdb(batchId);
		application.setUserSelNewInd(12);
		application.setUserSelModInd(10);
		application.setUserSelExtInd(3);
		application.setUserSelTransferInd(5);
		application.setUserSelRenInd(6);
		application.setChgOriginalProjectInd(9);
		
		when(applicationRepo.findApplicantAlreadySubmitted(Mockito.anyString(), Mockito.anyLong())).thenReturn(0);
		when(projectRepo.findByProjectIdAndUploadToDart(Mockito.anyLong())).thenReturn(getProjectObj());
		when(applicationRepo.findExistingApplicationByProjectIdAndBatchId(projectId, permit.getEtrackPermits().get(0).getBatchId())).thenReturn(application);
		when(transactionTypeRuleRepo.findTranstypeAndAssociateDetails(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getTransactionTypeRuleList());
		when(applicationRepo.save(Mockito.any())).thenReturn(application);
		
		permitService.saveAdditionalApplicationPermit(userId, contextId, token, "123", projectId, permit);
		verify(applicationRepo).save(Mockito.any());
	}
	
	
	
	//private methods
	
	
	
	private ApplicationPermit getApplicationPermitObj() {
		ApplicationPermit permit = new ApplicationPermit();
		permit.setEmergencyInd("E");
		permit.setEtrackPermits(getApplicationPermitDetailList());
		return permit;
	}
	
	private List<ApplicationPermitDetail> getApplicationPermitDetailList(){
		List<ApplicationPermitDetail> details = new ArrayList<>();
		ApplicationPermitDetail detail = new ApplicationPermitDetail();
		detail.setPermitTypeCode(permitTypeCode);
		detail.setApplicationId(applicationId);
		details.add(detail);
		return details;
	}
	
	private List<TransactionTypeRule> getTransactionTypeRuleList(){
		List<TransactionTypeRule> rules = new ArrayList<>();
		TransactionTypeRule rule = new TransactionTypeRule();
		rule.setEdbTransTypeCode("REI");
		rule.setTransTypeCode("Code");
		rule.setTransactionTypeRuleId(10);
		rules.add(rule);
		return rules;
	}

	
   private List<Public> getPublicsList(){
	   List<Public> publics = new ArrayList<>();
	   Public public1 = new Public();
	   public1.setDisplayName("Name");
	   public1.setEdbPublicId(120L);
//	   public1.setRoles(getRolesList());
	   public1.setPublicId(10L);
	   publics.add(public1);
	   return publics;
   } 
   
   private List<Role> getRolesList(){
	   List<Role> roles = new ArrayList<>();
//	   Role role = new Role();
//	   role.setRoleTypeId(10);
	   roles.add(getRoleObj());
	   return roles;
   }
   
   private Role getRoleObj() {
	   Role role = new Role();
	   role.setRoleTypeId(10);
	   role.setAddressId(100L);
	   return role;
   }
	
	

		private Application getApplicationObj() {
			Application application = new Application();
			application.setApplicationId(applicationId);
			return application;
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
		
	private Facility getFacilityObj() {
		Facility facility = new Facility();
		facility.setCreatedById(userId);
		facility.setProjectId(projectId);
		return facility;
	}
	
	private FacilityDetail getFacilityDetailObj() {
		FacilityDetail detail = new FacilityDetail();
		detail.setAddress(new FacilityAddress());
		return detail;
	}
	
	private ProjectDetail getProjectDetailObj() {
		ProjectDetail detail = new ProjectDetail();
		detail.setReceivedDate(dateString);
		detail.setRegions("Albany, Hudson Valley");
		detail.setMunicipalities(municipality);
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
		detail.setValidatedInd("Y");
//		detail.setFacility(new FacilityDetail());
		return detail;
	}
	
	private Project getProjectObj() {
		Project project = new Project();
		project.setValidatedInd(1);
		project.setProjectId(projectId);
		return project;
	}
	
	private List<ProjectActivity> getProjectActivityList(){
		ProjectActivity activity = new ProjectActivity();
		activity.setActivityStatusId(activityStatusId);
		activity.setCreatedById(userId);
		activity.setProjectId(projectId);
		activity.setProjectActivityStatusId(projectActivityStatusId);
		activity.setCompletionDate(new Date(12,01,2023));
		List<ProjectActivity> activities = new ArrayList<>();
		activities.add(activity);
		return activities;
	}
	
	

}
