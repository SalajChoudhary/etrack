package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import dec.ny.gov.etrack.permit.dao.ETrackKeywordDAO;
import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.ActionNoteEntity;
import dec.ny.gov.etrack.permit.entity.Application;
import dec.ny.gov.etrack.permit.entity.DocumentReviewEntity;
import dec.ny.gov.etrack.permit.entity.EmailCorrespondence;
import dec.ny.gov.etrack.permit.entity.Facility;
import dec.ny.gov.etrack.permit.entity.FacilityAddr;
import dec.ny.gov.etrack.permit.entity.Project;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.ProjectDevelopment;
import dec.ny.gov.etrack.permit.entity.ProjectNote;
import dec.ny.gov.etrack.permit.entity.ProjectPolygon;
import dec.ny.gov.etrack.permit.entity.ProjectProgramDistrict;
import dec.ny.gov.etrack.permit.entity.ProjectResidential;
import dec.ny.gov.etrack.permit.entity.ProjectSICNAICSCode;
import dec.ny.gov.etrack.permit.entity.ProjectSpecialAttention;
import dec.ny.gov.etrack.permit.entity.RegionUserEntity;
import dec.ny.gov.etrack.permit.entity.SupportDocument;
import dec.ny.gov.etrack.permit.entity.SystemDetectedKeyword;
import dec.ny.gov.etrack.permit.entity.UploadPolygonEntity;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.BusinessInformation;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameInfo;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameResponse;
import dec.ny.gov.etrack.permit.model.BusinessVerificationResponse;
import dec.ny.gov.etrack.permit.model.DIMSRPermit;
import dec.ny.gov.etrack.permit.model.DIMSRRequest;
import dec.ny.gov.etrack.permit.model.DartUploadDetail;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.FacilityDetail;
import dec.ny.gov.etrack.permit.model.IngestionResponse;
import dec.ny.gov.etrack.permit.model.PermitTaskStatus;
import dec.ny.gov.etrack.permit.model.PolygonStatus;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.model.ProjectInfo;
import dec.ny.gov.etrack.permit.model.ReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.model.SWFacilityType;
import dec.ny.gov.etrack.permit.repo.ActionNoteRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.DocumentReviewRepo;
import dec.ny.gov.etrack.permit.repo.EmailCorrespondenceRepo;
import dec.ny.gov.etrack.permit.repo.FacilityAddrRepo;
import dec.ny.gov.etrack.permit.repo.FacilityBINRepo;
import dec.ny.gov.etrack.permit.repo.FacilityRepo;
import dec.ny.gov.etrack.permit.repo.FoilRequestRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceFeeDetailRepo;
import dec.ny.gov.etrack.permit.repo.InvoiceRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectDevelopRepo;
import dec.ny.gov.etrack.permit.repo.ProjectInquiryAssociateRepo;
import dec.ny.gov.etrack.permit.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.permit.repo.ProjectPolygonRepo;
import dec.ny.gov.etrack.permit.repo.ProjectProgramApplnRepo;
import dec.ny.gov.etrack.permit.repo.ProjectProgramDistrictRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;
import dec.ny.gov.etrack.permit.repo.ProjectResidentialRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSICNAICSCodeRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSWFacilityTypeRepo;
import dec.ny.gov.etrack.permit.repo.ProjectSpecialAttentionRepo;
import dec.ny.gov.etrack.permit.repo.PublicRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.permit.repo.UploadPolygonRepo;
import dec.ny.gov.etrack.permit.service.ETrackKeywordService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectServiceTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@InjectMocks
	private ProjectServiceImpl projectService;
	
	@Mock
	private ETrackPermitDAO eTrackPermitDAO;
	
	@Mock
	private FacilityBINRepo facilityBinRepo;
	
	@Mock
	private LitigationHoldRequestRepo litigationHoldRequestRepo;
	
	@Mock
	private ProjectDevelopRepo projectDevelopRepo;
	
	@Mock
	private ProjectResidentialRepo projectResidentialRepo;
	
	@Mock
	private ProjectSICNAICSCodeRepo projectSICNAICSCodeRepo;
	
	@Mock
	private ProjectSWFacilityTypeRepo projectSWFacilityTypeRepo;
	
	@Mock
	private ETrackKeywordService eTrackKeywordService;
	
	@Mock
	private ProjectActivityRepo projectActivityRepo;
	
	@Mock
	private ETrackKeywordDAO eTrackKeywordDAO;
	
	@Mock
	private LitigationHoldRequestHistoryRepo litigationHoldRequestHistoryRepo;
	@Mock
	private RestTemplate eTrackOtherServiceRestTemplate;
	
	@Mock
	private RestTemplate businessVerificationRestTemplate;
	
	@Mock
	private ProjectProgramApplnRepo programApplnRepo;
	
	@Mock
	private ProjectNoteRepo projectNoteRepo;
	
	@Mock
	private ApplicationRepo applicationRepo;
	
	@Mock
	private SupportDocumentRepo supportDocumentRepo;
	
	@Mock
	private ActionNoteRepo actionNoteRepo;
	
	@Mock
	private InvoiceRepo invoiceRepo;
	
	@Mock
	private FacilityRepo facilityRepo;
	
	@Mock
	private DocumentReviewRepo documentReviewRepo;
	
	@Mock
	private EmailCorrespondenceRepo emailCorrespondenceRepo;
	
	@Mock
	private ProjectRepo projectRepo;
	
	@Mock
	private InvoiceFeeDetailRepo invoiceFeeDetailRepo;
	
	@Mock
	private ProjectAlertRepo projectAlertRepo;
	
	@Mock
	private FoilRequestRepo foilRequestRepo;
	
	@Mock
	private ProjectPolygonRepo projectPolygonRepo;
	
	@Mock
	private ProjectProgramDistrictRepo programDistrictRepo;
	
	@Mock
	private ProjectSpecialAttentionRepo projectSpecialAttentionRepo;
	
	@Mock
	private UploadPolygonRepo uploadEligiblePolygonRepo;
	
	@Mock
	private PublicRepo publicRepo;
	
	@Mock
	private ProjectInquiryAssociateRepo projectInquiryAssociateRepo;
	
	@Mock
	private FacilityAddrRepo facilityAddrRepo;
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;
	private String legalName = "John Doe";
	private String token = "34567";
	private Long noteId = 1L;
	private String fmisInvoiceNum = "12345";
	private int invoiceFee = 100;
	private String invoiceFeeType = "Type";
	private final String systemUserId = "SYSTEM";
	private Long alertId = 4L;
	private String reviewerEmail = "Reviewer@Unisys.com";
	private String email = "Test@Test.com";
	private String dateString ="12/23/2023";
	private String municipality = "Albany";

	private String comments = "Comment";
	
	//saveProject test cases
	//These tests covered 87& of method. commenting out due to JVM Error:
	@Test
	public void testSaveProjectThrowsBREForInvalidRecvdDate() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Received invalid format ReceivedDate");
		ProjectDetail detail = getProjectDetailObj();
		detail.setReceivedDate("Invalid");
		projectService.saveProject(userId, contextId, detail);
	}
	
	@Mock
	private TransformationService transformationService;
	
	@Test
	public void testSaveProjectThrowsBREForInvalidRecvdDateOne() {
		exceptionRule.expect(ETrackPermitException.class);
//		exceptionRule.expectMessage("Received invalid format ReceivedDate");
		ProjectDetail detail = getProjectDetailObj();
		detail.setReceivedDate("02/04/2024");
		detail.setRegions("test,test1");
		FacilityDetail f = getFacilityDetailObj();
		f.setEdbDistrictId(null);
		detail.setFacility(f);
		when(transformationService.transformToProjectEntity(userId, contextId, detail)
          ).thenReturn(new Project());
		when(transformationService.transformToFacilityEntity(userId, contextId,
          detail.getFacility(), projectId)).thenReturn(getFacilityObj());
		projectService.saveProject(userId, contextId, detail);
	}
	
	@Test
	public void testSaveProjectThrowsBREForNoRegions() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Region is not available in the request");
		ProjectDetail detail = getProjectDetailObj();
		detail.setRegions(null);
		projectService.saveProject(userId, contextId, detail);
	
	}
	
	@Test
	public void testSaveProjectThrowsBREForNoMuni() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Municipality is not available in the request");
		ProjectDetail detail = getProjectDetailObj();
		detail.setMunicipalities(null);
		projectService.saveProject(userId, contextId, detail);

	}
	
	@Test
	public void testSaveProjectSavesProjectActivityWhenPolygonStatusEqualsApplicantSubmitted() {
		ProjectDetail detail = getProjectDetailObj();
		projectService.saveProject(userId, contextId, detail);
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	@Test
	public void testSaveProjectDoesNotSaveProjectActivityWhenPolygonStatusIsAnalystApproved() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setPolygonStatus(PolygonStatus.ANALYST_APPROVED);
		projectService.saveProject(userId, contextId, detail);
		verify(projectActivityRepo, never()).save(Mockito.any());
	}
	
	//@Test
	public void testSaveProjectDoesNotSaveProjectActivityException() {
		ProjectDetail detail = getProjectDetailObj();
		FacilityDetail facilityDetail = new FacilityDetail();
		detail.setFacility(facilityDetail);
		assertNotNull(projectService.saveProject(userId, contextId, detail)) ;
		//verify(projectActivityRepo, never()).save(Mockito.any());
	}
	
	
	//getProjectPermitStatus test cases
	//These tests covered method 87%, commenting out due to jacoco jvm crash error
	@Test
	public void testGetProjectPermitStatusMapsTaskStatusCorrectlyInDataEntryMode() {
		int mode = 0;		
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdLTOREQ(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActivityList());
		List<PermitTaskStatus> resultList =	projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
		PermitTaskStatus result = resultList.get(0);
		assertEquals(projectId, result.getProjectId());
		assertEquals(projectId, result.getProjectId());
		assertEquals("Y", result.getCompleted());
	}
	
	@Test
	public void testGetProjectPermitStatusMapsTaskStatusCorrectlyForSupportDocValInValidateMode() {
		int mode = 1;
		List<ProjectActivity> activities = getProjectActivityList();
		//Setting SupportDocVal
		activities.get(0).setActivityStatusId(13);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdGT(Mockito.anyLong(), Mockito.anyInt())).thenReturn(activities);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		when(publicRepo.findAllContactsByProjectId(Mockito.anyLong())).thenReturn(new ArrayList<Long>(Arrays.asList(1l,2l)));
		List<PermitTaskStatus> resultList =	projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
		PermitTaskStatus result = resultList.get(1);
		assertEquals(projectId, result.getProjectId());
		assertEquals(4, result.getActivityStatusId());
		assertEquals("Y", result.getCompleted());
	}
	
	@Test
	public void testGetProjectPermitStatusMapsTaskStatusCorrectlyForSubmitProjectValInValidateMode() {
		int mode = 1;
		List<ProjectActivity> activities = getProjectActivityList();
		//Setting submitProjectVal
		activities.get(0).setActivityStatusId(14);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdGT(Mockito.anyLong(), Mockito.anyInt())).thenReturn(activities);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		when(publicRepo.findAllContactsByProjectId(Mockito.anyLong())).thenReturn(new ArrayList<Long>(Arrays.asList(1l,2l)));
		List<PermitTaskStatus> resultList =	projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
		PermitTaskStatus result = resultList.get(1);
		assertEquals(projectId, result.getProjectId());
		assertEquals(5, result.getActivityStatusId());
		assertEquals("Y", result.getCompleted());
	}
	
	@Test
	public void testGetProjectPermitStatusDoesNotMarkStatusAsCompleteForApplicantValInValidateMode() {
		int mode = 1;
		List<ProjectActivity> activities = getProjectActivityList();
		//Setting applicantVal
		activities.get(0).setActivityStatusId(7);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdGT(Mockito.anyLong(), Mockito.anyInt())).thenReturn(activities);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		when(publicRepo.findAllContactsByProjectId(Mockito.anyLong())).thenReturn(new ArrayList<Long>(Arrays.asList(1l,2l)));
		List<PermitTaskStatus> resultList =	projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
		assertEquals(1, resultList.size());
	}
	
	@Test
	public void testGetProjectPermitStatusMapsTaskStatusCorrectlyForPermitSummaryValInValidateMode() {
		int mode = 1;
		List<ProjectActivity> activities = getProjectActivityList();
		//Setting permit summary Val
		activities.get(0).setActivityStatusId(10);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdGT(Mockito.anyLong(), Mockito.anyInt())).thenReturn(activities);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		when(publicRepo.findAllContactsByProjectId(Mockito.anyLong())).thenReturn(new ArrayList<Long>(Arrays.asList(1l,2l)));
		when(projectActivityRepo.findProjectActivitiesByIds(Mockito.anyList(), Mockito.anyLong())).thenReturn(getProjectActivityList());
		List<PermitTaskStatus> resultList =	projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
		PermitTaskStatus result = resultList.get(0);
		//assertEquals(projectId, result.getProjectId());
//		assertNull(result.getProjectActivityStatusId());
		assertEquals(1, result.getActivityStatusId());
		assertEquals("N", result.getCompleted());
	}
	
	@Test
	public void testGetProjectPermitStatusThrowsDNFEForNoProjectActivitiesInDataEntryMode() {
		int mode = 0;
		exceptionRule.expect(DataNotFoundException.class);
		exceptionRule.expectMessage("No data found for the project");
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdLTOREQ(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
	}
	
	@Test
	public void testGetProjectPermitStatusThrowsBREForInvalidMode() {
		int mode = 1000;
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Mode of operation is neither Data Entry nor Validate passed");
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusIdLTOREQ(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		projectService.getProjectPermitStatus(userId, contextId, projectId, mode);
	}
	
	
	
	//rejectProjectValidation
	@Test
	public void testRejectProjectValidationRevertsProjectToDataEntry() {
		when(projectRepo.retrieveRetrieveEligibleProjectId(Mockito.anyString(), Mockito.anyLong())).thenReturn(1);
		projectService.rejectProjectValidation(userId, contextId, projectId, "Reason");
		verify(projectRepo).revertProjectToDataEntry(Mockito.anyString(), Mockito.anyLong(), Mockito.anyString());
	}
	
	@Test
	public void testRejectProjectValidationThrowsBREForIneligibleProject() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("This project cannot be deleted");
		when(projectRepo.retrieveRetrieveEligibleProjectId(Mockito.anyString(), Mockito.anyLong())).thenReturn(0);
		projectService.rejectProjectValidation(userId, contextId, projectId, "Reason");
	}
		

	

	
	//updateProject test cases
	@Test
	public void testUpdateProjectThrowsBREForNullFacility() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Facility details or Facility Address details is empty");
		ProjectDetail detail = getProjectDetailObj();
		projectService.updateProject(userId, contextId, token, detail);
	}
	
	@Test
	public void testUpdateProjectThrowsDNFEFForNullFacilityEntity() {
		exceptionRule.expect(DataNotFoundException.class);
		exceptionRule.expectMessage("There is no project/facility associated with this project");
		ProjectDetail detail = getProjectDetailObj();
		detail.setFacility(getFacilityDetailObj());
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		projectService.updateProject(userId, contextId, token, detail);
	}
	
	@Test
	public void testUpdateProjectWithValidatedProject() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setProjectId(projectId);
		detail.setHasSameGeometry(1);
		detail.setFacility(getFacilityDetailObj());
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFacilityObj());
		ProjectDetail result =	projectService.updateProject(userId, contextId, token, detail);
		assertEquals(PolygonStatus.ANALYST_APPROVED, result.getPolygonStatus());
		assertTrue(result.getRegions().contains("Albany"));
		assertEquals(projectId, result.getProjectId());
	}
	
	@Test
	public void testUpdateProjectSavesNewProjectActivity() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setProjectId(projectId);
		detail.setHasSameGeometry(1);
		detail.setValidatedInd(null);
		detail.setFacility(getFacilityDetailObj());
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFacilityObj());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		ProjectDetail result =	projectService.updateProject(userId, contextId, token, detail);
		assertEquals(PolygonStatus.APPLICANT_SUBMITTED, result.getPolygonStatus());
		assertTrue(result.getRegions().contains("Albany"));
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	@Test(expected = BadRequestException.class)
	public void testUpdateProjectSavesNewProjectActivityOne() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setProjectId(projectId);
		detail.setHasSameGeometry(1);
		detail.setValidatedInd("N");
		//detail.setFacility(getFacilityDetailObj());
		//detail.getFacility().setDecIdFormatted("23");
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
//		Facility f = getFacilityObj();
//		f.setDecId("");
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		ProjectDetail result =	projectService.updateProject(userId, contextId, token, detail);
		assertEquals(PolygonStatus.APPLICANT_SUBMITTED, result.getPolygonStatus());
		assertTrue(result.getRegions().contains("Albany"));
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	@Test(expected = BadRequestException.class)
	public void testRetrieveProjectDetailBadRequestException() {
		Optional<Project> projectAvailability = null;
		when(projectRepo.findById(projectId)).thenReturn(null);
		projectService.retrieveProjectDetail("DXDEV", contextId, 1L);
	}
	
	@Test(expected = BadRequestException.class)
	public void testRetrieveProjectDetailBadRequestException2() {
		Optional<Project> projectAvailability =projectRepo.findById(projectId);
		Facility facility = new Facility();
		facility.setProjectId(projectId);
		facility.setDecId("");
		facility.setCreatedById("dxdevada");
		when(facilityRepo.findByProjectId(projectId)).thenReturn(facility);
		projectService.retrieveProjectDetail("DXDEV", contextId, 1L);
	}
	
	@Test(expected = ETrackPermitException.class)
	public void testRetrieveProjectDetailETrackPermitException() {
		Project p =new Project();
		Optional<Project> projectAvailability = Optional.of(p);
	    when(projectRepo.findById(Mockito.any())).thenReturn(projectAvailability);
		Facility facility = new Facility();
		facility.setProjectId(projectId);
		facility.setDecId("");
		facility.setCreatedById("dxdevada");
		when(facilityRepo.findByProjectId(Mockito.any())).thenReturn(facility);
		FacilityAddr facilityAddress = new FacilityAddr();
		when(facilityAddrRepo.findByProjectId(Mockito.any())).thenReturn(facilityAddress);
		projectService.retrieveProjectDetail("dxdevada", contextId, 1L);
	}
	
	@Test
	public void testUpdateProjectSavesNewProjectActivityOneException() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setProjectId(projectId);
		detail.setHasSameGeometry(1);
		detail.setValidatedInd("N");
		detail.setFacility(getFacilityDetailObj());
		detail.getFacility().setDecIdFormatted("23");
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
		Facility f = getFacilityObj();
		f.setDecId("");
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(f);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		when(eTrackPermitDAO.updateProjectDetails(userId, contextId, detail, null))
		.thenThrow(DataExistException.class);
		ProjectDetail result =	projectService.updateProject(userId, contextId, token, detail);

	}
	
	@Test
	public void saveNewFacilityAndProject() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setProjectId(projectId);
		detail.setHasSameGeometry(1);
		detail.setValidatedInd("N");
		detail.setFacility(getFacilityDetailObj());
		detail.getFacility().setDecIdFormatted("23");
		detail.setPolygonStatus(PolygonStatus.APPLICANT_SUBMITTED);
		Facility f = getFacilityObj();
		//f.setDecId("");
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(f);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		ProjectDetail result =	projectService.updateProject(userId, contextId, token, detail);
		when(facilityRepo.save(Mockito.any())).thenReturn(f);
		assertEquals(PolygonStatus.APPLICANT_SUBMITTED, result.getPolygonStatus());
		assertTrue(result.getRegions().contains("Albany"));
	}
	
	//submitProject Tests
	@Test
	public void testSubmitProjectThrowsBREForInvalidProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no project available with this id");
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		this.projectService.submitProject(userId, contextId, projectId);
	}
	@Test
	public void testSubmitProjectThrowsBREForRejectedProject() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("This reject project cannot be re-submitted. Please delete this one and submit new one");
		Project project = getProjectObj();
		project.setRejectedInd(1);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		this.projectService.submitProject(userId, contextId, projectId);
	}
	
	@Test
	public void testSubmitProjectThrowsBREForIncompleteProject() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("This project is in incomplete status or not available");
		Project project = getProjectObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
		List<ProjectActivity> projectActivityList = getProjectActvityList();
		for(int i =0; i>4; i++) {
			projectActivityList.add(new ProjectActivity());	
		}
		when(projectActivityRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(projectActivityList);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(projectActivityList);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		this.projectService.submitProject(userId, contextId, projectId);
	}
	
	@Test
	public void testSubmitProjectSavesProjectNoteForSubmittedProject() {
		Project project = getProjectObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
		List<ProjectActivity> projectActivityList = new ArrayList<>();
		for(int i =0; i<=4; i++) {
			ProjectActivity activity = new ProjectActivity();
			projectActivityList.add(activity);	
		}
		when(projectActivityRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(projectActivityList);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(projectActivityList);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		this.projectService.submitProject(userId, contextId, projectId);
		verify(projectNoteRepo).save(Mockito.any());
	}
	
	//addSupportDocument tests
	
	@Test
	public void testAddSupportDocumentDoesNotSaveProjectActivity() {
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		this.projectService.addSupportDocument(userId, contextId, projectId);
		verify(projectActivityRepo, never()).save(Mockito.any());
	}
	
	@Test 
	public void testAddSupportDocumentSavesProjectActivity() {
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		this.projectService.addSupportDocument(userId, contextId, projectId);
		verify(projectActivityRepo).save(Mockito.any());
	}
	
	
	//deleteProject tests
	
	@Test
	public void testDeleteProjectsThrowsBREWhenSubmittedProjectIsDeleted() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project is already submitted");
	     when(projectRepo.findUnsubmittedProjectByProjectId(Mockito.anyLong())).thenReturn(null);
		//when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		this.projectService.deleteProject(userId, contextId, token, projectId);
	}
	
	
	@Test
	public void testDeleteProjectsCallsDAOToDeleteProject() {
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(null);
		when(supportDocumentRepo.findAllDocumentsByProjectId(Mockito.anyLong())).thenReturn(getSupportDocumentList());
		this.projectService.deleteProject(userId, contextId, token, projectId);
		verify(eTrackPermitDAO).deleteEtrackProject(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
	}
	
	//updateSignatureReceived tests
	
	@Test
	public void testUpdateSignatureReceivedCallsProjectRepoToUpdateSignature() {
		projectService.updateSignatureReceived(userId, contextId, projectId, Arrays.asList(1l,2l,3l));
		verify(projectRepo).updatePublicSignatureReceivedInd(Mockito.anyList(), Mockito.anyString(), Mockito.any(), Mockito.anyLong());
	}
	
	
	//updateProjectAssignment tests
	@Test
	public void testUpdateProjectAssignmentSavesProjectAlert() {
		ProjectNote note = getProjectNoteObj();
		when(projectNoteRepo.save(Mockito.any())).thenReturn(note);
		this.projectService.updateProjectAssignment(userId, contextId, projectId, getAssignmentNoteObj());
		verify(projectAlertRepo).save(Mockito.any());
	}
	
	//saveDIMSRDetails test cases
	@Test
	public void testSaveDIMSRDetailsThrowsBREForInvalidDIMSRRequest() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no DIMSR application requested");
		projectService.saveDIMSRDetails(userId, contextId, token, "12", null);
	}
	
	@Test
	public void testSaveDIMSRDetailsMapsRequestDataCorrectly() {
	DIMSRRequest result = projectService.saveDIMSRDetails(userId, contextId, token, "12", getDIMSRRequestObj());
		assertEquals("DESC", result.getProjectDesc());
		assertEquals(legalName, result.getAssignedAnalystName());
		assertEquals("Lemk Property", result.getFacilityName());
	}
	
	@Test
	public void testSaveDIMSRDetailsThrowsETrackPermitExceptionForErrorWhileUploadingToDART() {
		exceptionRule.expect(ETrackPermitException.class);
		//exceptionRule.expectMessage("Error while uploading the DIMSR details into DART");
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST),Mockito.any(HttpEntity.class), Mockito.<Class<Void>>any())).thenThrow(HttpServerErrorException.class);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		projectService.saveDIMSRDetails(userId, contextId, token, "12", getDIMSRRequestObj());
	}
	
	@Test(expected = BadRequestException.class)
	public void testSaveDIMSRDetailsThrowsETrackPermitExceptionForErrorWhileUploadingToDART2() {
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), 
				Mockito.eq(HttpMethod.POST),Mockito.any(HttpEntity.class), 
				Mockito.<Class<Void>>any())).thenThrow(HttpServerErrorException.class);
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
		projectService.saveDIMSRDetails(userId, contextId, token, "12", null);
	}
	
//	@Test
//	public void saveDimsDetailsException() {
//		exceptionRule.expect(ETrackPermitException.class);
//		//exceptionRule.expectMessage("Error while uploading the DIMSR details into DART");
//		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST),Mockito.any(HttpEntity.class), Mockito.<Class<Void>>any())).thenThrow(HttpServerErrorException.class);
//		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getProjectObj()));
//		projectService.saveDIMSRDetails(userId, contextId, token, "12", getDIMSRRequestObj());
//	}
		
	//uploadProjectDetailsToEnterprise test cases
	@Test
	public void testUploadProjectDetailsToEnterpriseThrowsNoExceptions() {
		byte[] reportFile = "Any String you want".getBytes();
		Map<String, List<ReviewedPermit>> reviewedPermits = new HashMap();
		reviewedPermits.put("5", getReviewedPermitList());
		DartUploadDetail dartUploadDetail = getDartUploadDetailObj();
		dartUploadDetail.setReviewedPermits(reviewedPermits);
		ResponseEntity<byte[]> responseEntity = new ResponseEntity(reportFile, HttpStatus.OK);
		ResponseEntity<IngestionResponse> ingestionResponseEntity = new ResponseEntity(getIngestionResponseObj(), HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<byte[]>>any())).thenReturn(responseEntity);
		when(applicationRepo.findAllPermitTypesByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList("CE", "FW"));
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(projectRepo.updateProjectUploadedDetail(Mockito.anyString(), Mockito.anyLong(), Mockito.any(Date.class))).thenReturn(1);
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		projectService.uploadProjectDetailsToEnterprise(userId, contextId, token, "123", projectId, dartUploadDetail);
	}
	
	@Test
	public void testUploadProjectDetailsToEnterpriseThrowsNoExceptionsEbpIdNull() {
		byte[] reportFile = "Any String you want".getBytes();
		Map<String, List<ReviewedPermit>> reviewedPermits = new HashMap();
		List<ReviewedPermit> list = getReviewedPermitList();
		list.get(0).setEdbApplnId(null);
		reviewedPermits.put("5", list);
		DartUploadDetail dartUploadDetail = getDartUploadDetailObj();
		dartUploadDetail.setReviewedPermits(reviewedPermits);
		ResponseEntity<byte[]> responseEntity = new ResponseEntity(reportFile, HttpStatus.OK);
		ResponseEntity<IngestionResponse> ingestionResponseEntity = new ResponseEntity(getIngestionResponseObj(), HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<byte[]>>any())).thenReturn(responseEntity);
		when(applicationRepo.findAllPermitTypesByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList("CE", "FW"));
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(projectRepo.updateProjectUploadedDetail(Mockito.anyString(), Mockito.anyLong(), Mockito.any(Date.class))).thenReturn(1);
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		projectService.uploadProjectDetailsToEnterprise(userId, contextId, token, "123", projectId, dartUploadDetail);
	}
	
	@Test
	public void testUploadProjectDetailsToEnterpriseThrowsBREForNoProjectToUpload() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no project details to associated with this project Id");
		byte[] reportFile = "Any String you want".getBytes();
		ResponseEntity<byte[]> responseEntity = new ResponseEntity(reportFile, HttpStatus.OK);
		ResponseEntity<IngestionResponse> ingestionResponseEntity = new ResponseEntity(getIngestionResponseObj(), HttpStatus.OK);
		ResponseEntity<JsonNode> nodeEntity = new ResponseEntity(HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<byte[]>>any())).thenReturn(responseEntity);
		when(applicationRepo.findAllPermitTypesByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList("CE", "FW"));
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<IngestionResponse>>any())).thenReturn(ingestionResponseEntity);
		when(projectRepo.updateProjectUploadedDetail(Mockito.anyString(), Mockito.anyLong(), Mockito.any(Date.class))).thenReturn(0);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<JsonNode>>any())).thenReturn(nodeEntity);
		when(applicationRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new Application()));
		projectService.uploadProjectDetailsToEnterprise(userId, contextId, token, "123", projectId, getDartUploadDetailObj());
	}

	@Test
	public void associateGeographicalInquiryToProjectTest() {
		when(projectInquiryAssociateRepo.findByInquiriesList(Mockito.any())).thenReturn(1);
//		exceptionRule.expect(BadRequestException.class);
		projectService.associateGeographicalInquiryToProject(systemUserId, contextId, projectId, alertId);
	}
	
	//UploadGISApprovedPolygonToEFind tests
	@Test
	public void testUploadGISApprovedPolygonToEFind() {
		ResponseEntity<Map<Long, String>> requestStatusResponse = new ResponseEntity(HttpStatus.OK);
		when(projectPolygonRepo.findPolygonUploadEligibleProjects()).thenReturn(getProjectPolygonList());
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.POST.getClass()), Mockito.any(HttpEntity.class),Mockito.any(ParameterizedTypeReference.class) )).thenReturn(requestStatusResponse);
		projectService.uploadGISApprovedPolygonToEFind();
	}
	
	//:ToDO Write up for else loop
	@Test
	public void testUploadGISApprovedPolygonToEFindWithRequest() {
		Map<Long,String> body = new HashMap();
		body.put(1L, comments);
		body.put(5L, "5");
		ResponseEntity<Map<Long, String>> requestStatusResponse = new ResponseEntity(body,HttpStatus.OK);
		List<ProjectPolygon> polygons = new ArrayList<>();
		ProjectPolygon polygon = new ProjectPolygon();
		polygon.setApprovedPolygonChangeInd(1000);
		polygon.setEdbDistrictId(20L);
		polygon.setProjectId(12L);
		polygons.add(polygon);
		when(projectPolygonRepo.findPolygonUploadEligibleProjects()).thenReturn(polygons);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.POST.getClass()), 
				Mockito.any(HttpEntity.class),Mockito.any(ParameterizedTypeReference.class) )).thenReturn(requestStatusResponse);
		
		UploadPolygonEntity uploadPolygonEntity = new  UploadPolygonEntity();
		uploadPolygonEntity.setRetryCounter(1);
		when(uploadEligiblePolygonRepo.findByProjectId(Mockito.anyLong())).thenReturn(uploadPolygonEntity);
		projectService.uploadGISApprovedPolygonToEFind();
	}


	//updateDocumentReviewerDetails test cases
	@Test
	public void testUpdateDocumentReviewerDetailsThrowsBREForInvalidDateFormat() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid dates are passed incorrect format");
		DocumentReview review = getDocumentReviewObj();
		review.setDateAssigned("INVALID");
		projectService.updateDocumentReviewerDetails(userId, contextId, projectId, review);
	}
	
	@Test
	public void testUpdateDocumentReviewerDetailsThrowsBREForInvalidDates() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid dates are passed");
		DocumentReview review = getDocumentReviewObj();
		review.setDateAssigned("01/19/2021");
		projectService.updateDocumentReviewerDetails(userId, contextId, projectId, review);
	}
	
	@Test
	public void testUpdateDocumentReviewerDetailsThrowsDataNotFoundExceptionForInvalidUserId() {
		exceptionRule.expect(DataNotFoundException.class);
		exceptionRule.expectMessage("There is no staff associated with this user id");
		when(supportDocumentRepo.findAllByDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getSupportDocumentList());
		when(eTrackPermitDAO.retrieveStaffDetailsByUserId(Mockito.anyString(), Mockito.anyString())).thenReturn(Arrays.asList());
		
		projectService.updateDocumentReviewerDetails(userId, contextId, projectId, getDocumentReviewObj());
	}
	
	@Test
	public void testUpdateDocumentReviewerDetailsThrowsDataNotFoundExceptionForInvalidProjectId() {
		exceptionRule.expect(DataNotFoundException.class);
		exceptionRule.expectMessage("There is no facility associated with this project id");
		when(supportDocumentRepo.findAllByDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getSupportDocumentList());
		when(eTrackPermitDAO.retrieveStaffDetailsByUserId(Mockito.anyString(), Mockito.anyString())).thenReturn(getRegionUserEntityList());
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		projectService.updateDocumentReviewerDetails(userId, contextId, projectId, getDocumentReviewObj());
	}
	
	@Test
	public void testUpdateDocumentReviewerDetailsCorrectlyMapsToAndFromEmailAddresses() {
		when(supportDocumentRepo.findAllByDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getSupportDocumentList());
		when(eTrackPermitDAO.retrieveStaffDetailsByUserId(Mockito.anyString(), Mockito.anyString())).thenReturn(getRegionUserEntityList());
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFacilityObj());
		EmailContent result = projectService.updateDocumentReviewerDetails(userId, contextId, projectId, getDocumentReviewObj());
		assertEquals(reviewerEmail, result.getToEmailId().get(0));
		assertEquals(email, result.getFromEmailId());
	}
	
	@Test
	public void testUpdateDocumentReviewerDetailsSavesDocumentReviewWhenReviewerHasDocumentIds() {
		when(supportDocumentRepo.findAllByDocumentIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getSupportDocumentList());
		when(eTrackPermitDAO.retrieveStaffDetailsByUserId(Mockito.anyString(), Mockito.anyString())).thenReturn(getRegionUserEntityList());
		when(facilityRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFacilityObj());
		DocumentReview docReview = getDocumentReviewObj();
		docReview.setDocumentIds(Arrays.asList(1L));
		projectService.updateDocumentReviewerDetails(userId, contextId, projectId, docReview);
		verify(documentReviewRepo).save(Mockito.any());
	}
	
	//Only 19% coverage on this method
	//updateDocumentReviewerCompletionDetails test cases
	@Test
	public void testUpdateDocumentReviewerCompletionDetailsThrowsBREForInvalidReviewerId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("One of the field Document Reviewer Id, Reviewer name and Reviewer id is missing");
		ReviewCompletionDetail detail = getReviewCompletionDetailObj();
		detail.setReviewerId(null);
		projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, detail);
	}
	
	@Test
	public void testUpdateDocumentReviewerCompletionDetailsThrowsBREForInvalidCorrespondenceId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no document review available for this Correspondence id");
		when(documentReviewRepo.findByCorrespondenceId(Mockito.anyLong())).thenReturn(null);
		projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, getReviewCompletionDetailObj());
	}

	
	@Test
	public void testUpdateDocumentReviewerCompletionDetailsCallsRepoToRetrieveDocIdsToResetReviewAsIncomplete() {
		List<DocumentReviewEntity> entities = getDocumentReviewEntityList();
		DocumentReviewEntity entity = entities.get(0);
		entity.setDocReviewedInd(1);
	when(documentReviewRepo.findByCorrespondenceId(Mockito.anyLong())).thenReturn(entities);
	when(supportDocumentRepo.findByDocumentNameAndProjectId(Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
	when(emailCorrespondenceRepo.findAllCorrespondenceByReviewerIdAndDocReviewId(Mockito.anyString(), Mockito.anyLong())).thenReturn(Arrays.asList(new EmailCorrespondence()));
	projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, getReviewCompletionDetailObj());
	verify(supportDocumentRepo).findByDocumentNameAndProjectId(Mockito.anyLong(), Mockito.anyString());
}
	
	@Test
	public void testUpdateDocumentReviewerCompletionDetailsMakesCallToDMS() {
		List<DocumentReviewEntity> entities = getDocumentReviewEntityList();
		DocumentReviewEntity entity = entities.get(0);
		entity.setReviewAssignedDate(new Date());
		entity.setReviewDueDate(new Date());
		EmailCorrespondence correspondence = new EmailCorrespondence();
		correspondence.setCcEmailAdr("J@J.com");
		correspondence.setCorrespondenceId(100L);
		correspondence.setEmailContent("Content");
		correspondence.setEmailRcvdUserName(userId);
		correspondence.setProjectId(projectId);
		correspondence.setEmailSubject("Subject");
		correspondence.setToEmailAdr("k@Yahoo.com");
		correspondence.setSubShortDesc("Desc");
	when(documentReviewRepo.findByCorrespondenceId(Mockito.anyLong())).thenReturn(entities);
	when(supportDocumentRepo.findByDocumentNameAndProjectId(Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
	when(emailCorrespondenceRepo.findAllCorrespondenceByReviewerIdAndDocReviewId(Mockito.anyString(), Mockito.anyLong())).thenReturn(Arrays.asList(correspondence));
	projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, getReviewCompletionDetailObj());
	verify(eTrackOtherServiceRestTemplate).exchange(Mockito.anyString(),  ArgumentMatchers.any(HttpMethod.class),
	        ArgumentMatchers.any(HttpEntity.class),
	        ArgumentMatchers.<Class<JsonNode>>any());
}

	//storeProjectInfo tests
	@Test
	public void testStoreProjectInfoThrowsBREForInvalidProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project Id is invalid");
		Optional<Project> projOptional = Optional.empty();
		ProjectInfo projectInfo = getProjectInfoObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(facilityBinRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectSWFacilityTypeRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
	}
	
	
	@Test
	public void testStoreProjectInfoSavesDevelopmentTypes() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectDevelopRepo).saveAll(Mockito.anyIterable());
	}
	
	@Test
	public void testStoreProjectInfoDeletesAllProjectResidentials() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectResidentialRepo).deleteAll(Mockito.anyIterable());
	}
	
	@Test
	public void testStoreProjectInfoSavesAllProjectResidentials() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectResidentialRepo).saveAll(Mockito.anyIterable());
	}
	


	
	@Test
	public void testStoreProjectInfoSavesAllExistingSICNAICSCodes() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setSicCodeNaicsCode(getSicCodeNAicsCodeMap());
		List<ProjectSICNAICSCode> sicCodes = getProjectSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectSICNAICSCodeRepo).saveAll(Mockito.anyIterable());
	}
	

	
	@Test
	public void testStoreProjectInfoSavesAllProjectsSwFacilityTypes() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setSicCodeNaicsCode(getSicCodeNAicsCodeMap());
		projectInfo.setSwFacilityTypes(getSwFacilityTypes());
		List<ProjectSICNAICSCode> sicCodes = getProjectSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);		
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectSWFacilityTypeRepo).saveAll(Mockito.anyIterable());
		}
	
	
	@Test
	public void testStoreProjectInfo() {
		Project project = getProjectObj();
		project.setOriginalSubmittalInd(0);
		project.setProjectDesc(null);
		Optional<Project> projOptional = Optional.of(project);
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setBriefDesc("Brief");
		
		when(eTrackKeywordDAO.retrieveSystemDetectedKeywords(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())).thenReturn(getDetectedKeywordList());
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectDevelopRepo).saveAll(Mockito.anyIterable());
	}
	
	private List<SystemDetectedKeyword> getDetectedKeywordList(){
		List<SystemDetectedKeyword> keywords = new ArrayList<>();
		SystemDetectedKeyword keyword = new SystemDetectedKeyword();
		keyword.setKeywordId(1000L);
		keywords.add(keyword);
		return keywords;
	}
	
	//These three have been commented out due to JVM crash error
	
	@Test
	public void testStoreProjectInfoSavesAllProjectActivities() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setSicCodeNaicsCode(getSicCodeNAicsCodeMap());
		projectInfo.setSwFacilityTypes(getSwFacilityTypes());
		List<ProjectSICNAICSCode> sicCodes = getProjectSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		ProjectActivity projectActivity = getProjectActvityList().get(0);
		assertEquals("John", projectActivity.getCreatedById());
		verify(projectActivityRepo).save(Mockito.any(ProjectActivity.class));
		}
	
	@Test
	public void testStoreProjectInfoDeletesAllExistingProjDevelopment() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setSicCodeNaicsCode(getSicCodeNAicsCodeMap());
		projectInfo.setSwFacilityTypes(getSwFacilityTypes());
		List<ProjectSICNAICSCode> sicCodes = getProjectSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList(new ProjectDevelopment()));
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActvityList());
		when(programDistrictRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectProgramDistrictList());
		when(projectSpecialAttentionRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectSpecialAttentionList());
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectDevelopRepo).deleteAll(Mockito.anyIterable());
		}
	
	@Test
	public void testStoreProjectInfoDeletesAllSICNAICSCodes() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		List<ProjectSICNAICSCode> sicCodes = getSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);

		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
		verify(projectSICNAICSCodeRepo).deleteAll(Mockito.anyIterable());
	}
	
	
	private List<ProjectSICNAICSCode> getSicCodeList(){
		List<ProjectSICNAICSCode> codes = new ArrayList<>();
		ProjectSICNAICSCode code = new ProjectSICNAICSCode();
		code.setNaicsCode("123445");
		code.setProjectId(projectId);
		code.setProjectSicNaicsId(100L);
		code.setSicCode("09876");
		codes.add(code);
		return codes;
	}
	
	
	//These tests were commented out. Not sure if still applicable:
	//BRE Exception here:
	@Test(expected = BadRequestException.class)
	public void testSaveProjectWithNewFacility() {
		ProjectDetail detail = getProjectDetailObj();
		detail.setFacility(new FacilityDetail());
		projectService.saveProject(userId, contextId, detail);
		verify(projectActivityRepo, never()).save(Mockito.any());
	}

	
	//Test is not completed, come back***
//	@Test
//	public void testUpdateDocumentReviewerCompletionDetailsCallsRepoToFindCorrespondence() {
//		when(documentReviewRepo.findByCorrespondenceId(Mockito.anyLong())).thenReturn(getDocumentReviewEntityList());
//		projectService.updateDocumentReviewerCompletionDetails(userId, contextId, token, projectId, getReviewCompletionDetailObj());
//		when(emailCorrespondenceRepo.findAllCorrespondenceByReviewerIdAndDocReviewId(Mockito.anyString(), Mockito.anyLong())).thenReturn(Arrays.asList(new EmailCorrespondence()));
//		verify(emailCorrespondenceRepo).findAllCorrespondenceByReviewerIdAndDocReviewId(Mockito.anyString(), Mockito.anyLong());
//	}
	
	
	//getBusinessVerified tests
	
		@Test
		public void testGetBusinessVerifiedReturnsCorrectNameAndOkStatus() {
			BusinessLegalNameResponse responseBody = getBusinessLegalNameResponseObj();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.OK);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenReturn(bizResponseEntity);
			//ReflectionTestUtils.setField(projectService, "akanaAuthToken", "test");
//			ResponseEntity result =	(ResponseEntity) projectService.getBusinessVerified(userId, contextId, );
//			assertEquals(Arrays.asList(legalName), result.getBody());
//			assertEquals(HttpStatus.OK, result.getStatusCode());
		}
		
		
	

	@Test
	public void testStoreProjectInfoUpdatesProjectInfo() {
		Optional<Project> projOptional = Optional.of(getProjectObj());
		ProjectInfo projectInfo = getProjectInfoObj();
		projectInfo.setSicCodeNaicsCode(getSicCodeNAicsCodeMap());
		List<ProjectSICNAICSCode> sicCodes = getProjectSicCodeList();
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(projOptional);
		when(projectDevelopRepo.findByProjectId(Mockito.anyLong())).thenReturn(Arrays.asList());
		when(projectResidentialRepo.findByProjectId(Mockito.anyLong())).thenReturn(getProjectResidentialList());
		when(projectSICNAICSCodeRepo.findByProjectId(Mockito.anyLong())).thenReturn(sicCodes);
		
		this.projectService.storeProjectInfo(userId, contextId, projectId, projectInfo);
//		verify(projectRepo).updateProjectInfo(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), 
//				Mockito.anyString(), Mockito.any(Date.class), Mockito.anyInt(), Mockito.any(Date.class), 
//				Mockito.any(Date.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
	}
	
	
	
	//private methods

	private List<ProjectSpecialAttention> getProjectSpecialAttentionList(){
		ProjectSpecialAttention attention = new ProjectSpecialAttention();
		attention.setCreateDate(new Date());
		attention.setCreatedById(userId);
		attention.setSpecialAttentionCode("12");
		List<ProjectSpecialAttention> attentions = new ArrayList<>();
		attentions.add(attention);
		return attentions;
		
	}
	
	private List<ProjectProgramDistrict> getProjectProgramDistrictList(){
		ProjectProgramDistrict district = new ProjectProgramDistrict();
		List<ProjectProgramDistrict> districts = new ArrayList<>();
		districts.add(district);
		return districts;
		
	}
	private List<ActionNoteEntity> getActionNoteList(){
		ActionNoteEntity actionNoteEntity = new ActionNoteEntity();
		actionNoteEntity.setActionTypeDesc("DESC");
		actionNoteEntity.setActionDate(new Date());
		actionNoteEntity.setActionTypeCode(17);
		actionNoteEntity.setComments("Comment");
		actionNoteEntity.setProjectNoteId(1L);
		return Arrays.asList(actionNoteEntity);
	}
	
	
	
	
	private List<ProjectActivity> getProjectActvityList(){
		ProjectActivity projectActivity = new ProjectActivity();
		projectActivity.setCreatedById("John");
		projectActivity.setStartDate(new Date());
		List<ProjectActivity> projectActivities = Arrays.asList(projectActivity);
		return projectActivities;
		}
	
	private List<SWFacilityType> getSwFacilityTypes(){
		SWFacilityType swFacilityType = new SWFacilityType();
		List<SWFacilityType> swFacilityTypes = Arrays.asList(swFacilityType);
		return swFacilityTypes;
	}
	
	private BusinessLegalNameResponse getBusinessLegalNameResponseObj() {
		BusinessLegalNameResponse responseBody = new BusinessLegalNameResponse();
		responseBody.setLegalNameInformation(getBizLegalNameInfoObj());
		responseBody.setResponseInfo(getBizVerificationResponseObj());
		return responseBody;
	}
	
	private BusinessVerificationResponse getBizVerificationResponseObj() {
		BusinessVerificationResponse response = new BusinessVerificationResponse();
		response.setCustTransactionId("ID");
		response.setErrorMessage("ERROR");
		response.setResponseMessage("Success");
		return response;
	}
	
	private List<ProjectActivity> getProjectActivityList(){
		ProjectActivity activity = new ProjectActivity();
		//activity.setActivityStatusId(activityStatusId);
		activity.setCreatedById(userId);
		activity.setProjectId(projectId);
		//activity.setProjectActivityStatusId(projectActivityStatusId);
		activity.setCompletionDate(new Date(12,01,2023));
		List<ProjectActivity> activities = new ArrayList<>();
		activities.add(activity);
		return activities;
	}
	
	
	private BusinessLegalNameInfo getBizLegalNameInfoObj(){
		BusinessLegalNameInfo info = new BusinessLegalNameInfo();
		info.setBusinessInformation(getBusinessInformationList());
		return info;
	}
	
	private List<BusinessInformation> getBusinessInformationList(){
		BusinessInformation information = new BusinessInformation();
		information.setCounty("Albany");
		information.setDosId("1234");
		information.setLegalName(legalName);
		information.setFilingDate("12/23/2023");
		return Arrays.asList(information);
	}
	
	private List<Map<String, String>> getSicCodeNAicsCodeMap(){
		Map<String, String> sicMap = new HashMap<>();
		sicMap.put("456", "789");
		List<Map<String, String>> sicList = Arrays.asList(sicMap);
		return sicList;
	}
	
	private List<ProjectSICNAICSCode> getProjectSicCodeList(){
		List<ProjectSICNAICSCode> sicCodes = new ArrayList<>();
		ProjectSICNAICSCode sicnaicsCode = new ProjectSICNAICSCode();
		sicnaicsCode.setNaicsCode("1234");
		sicnaicsCode.setSicCode("4567");
		
		return sicCodes;
	}
	
	
	private DIMSRRequest getDIMSRRequestObj() {
		DIMSRRequest request = new DIMSRRequest();
		request.setIntentMailingDate("12/23/2023");
		request.setProposedEffDate("12/24/2023");
		request.setPermits(getDIMSRPermitList());
		request.setAssignedAnalystName(legalName);
		request.setProjectDesc("DESC");
		request.setFacilityName("Lemk Property");
		request.setProjectId(projectId);
		return request;
	}
	
	private List<DIMSRPermit> getDIMSRPermitList(){
		DIMSRPermit permit = new DIMSRPermit();
		permit.setBatchId(123L);
		permit.setEdbApplnId(100L);
		permit.setPermitTypeCode("CE");
		permit.setTransType("MOD");
		return Arrays.asList(permit);
	}
	
	private ReviewCompletionDetail getReviewCompletionDetailObj() {
		ReviewCompletionDetail detail = new ReviewCompletionDetail();
		detail.setCorrespondenceId(10L);
		detail.setDocReviewerName(legalName);
		detail.setDocumentId(1L);
		detail.setDocumentReviewId(100L);
		detail.setReviewerId("ID");
		return detail;
	}
	
	private List<DocumentReviewEntity> getDocumentReviewEntityList(){
		DocumentReviewEntity reviewEntity = new DocumentReviewEntity();
//		reviewEntity.setDocReviewedInd("0");
		reviewEntity.setCorrespondenceId(10L);
		reviewEntity.setCreatedById(userId);
		reviewEntity.setDocReviewerId("ID");
		reviewEntity.setDocReviewedInd(2);
		List<DocumentReviewEntity> reviewEntities = new ArrayList<>();
		reviewEntities.add(reviewEntity);
		return reviewEntities;
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
	
	private List<ProjectPolygon> getProjectPolygonList(){
		List<ProjectPolygon> polygons = new ArrayList<>();
		ProjectPolygon polygon = new ProjectPolygon();
		polygon.setApprovedPolygonChangeInd(1000);
		polygon.setEdbDistrictId(20L);
		polygon.setProjectId(12L);
		polygons.add(polygon);
		return polygons;
	}
	
	private FacilityDetail getFacilityDetailObj() {
		FacilityDetail detail = new FacilityDetail();
		detail.setAddress(new FacilityAddress());
		return detail;
	}
	
	private AssignmentNote getAssignmentNoteObj() {
		AssignmentNote note = new AssignmentNote();
		note.setComments("Comment");
		note.setAnalystName(legalName);
		return note;
	}

	private Facility getFacilityObj() {
		Facility facility = new Facility();
		facility.setDecId("33-4433-4433");
		facility.setFacilityName("Lemk Property");
		return facility;
	}
	
	private IngestionResponse getIngestionResponseObj() {
		IngestionResponse response = new IngestionResponse();
		response.setDocumentId("2");
		response.setGuid("123");
		response.setResultMessage("Message");
		return response;
	}
	private DartUploadDetail getDartUploadDetailObj() {
		DartUploadDetail detail = new DartUploadDetail();
		detail.setReviewedPermits(getReviewedPermitMap());
		detail.setReceivedDate("12/23/2023");
		return detail;
	}
	
	private Map<String, List<ReviewedPermit>> getReviewedPermitMap(){
		Map<String, List<ReviewedPermit>> reviewedPermits = new HashMap();
		reviewedPermits.put("Test", getReviewedPermitList());
		reviewedPermits.put("Test2", getReviewedPermitList());
		reviewedPermits.put("Test3", getReviewedPermitList());
		return reviewedPermits;
	}
	
	private List<ReviewedPermit> getReviewedPermitList(){
		ReviewedPermit permit = new ReviewedPermit();
		permit.setApplicationId(10L);
		permit.setBatchGroup("A");
		permit.setBatchId(1l);
		permit.setPermitTypeCode("CE");
		permit.setProgramId("1234");
		permit.setTrackingInd(1);
		permit.setTransType("Trans");
		permit.setEdbApplnId(1L);
		return Arrays.asList(permit);
	}
	
	private DocumentReview getDocumentReviewObj() {
		DocumentReview doc = new DocumentReview();
		doc.setDateAssigned("12/23/2025");
		doc.setDueDate("01/12/2026");
		doc.setReviewerEmail(reviewerEmail);
		doc.setReviewerId("ID");
		return doc;
	}
	
	private List<RegionUserEntity> getRegionUserEntityList(){
		List<RegionUserEntity> userEntities = new ArrayList<>();
		RegionUserEntity user = new RegionUserEntity();
		user.setDisplayName("Test");
		user.setEmailAddress(email);
		user.setUserId(userId);
		userEntities.add(user);
		return userEntities;
	}
	
	
	
	
	private List<SupportDocument> getSupportDocumentList(){
		List<SupportDocument> documents = new ArrayList<>();
		SupportDocument document = new SupportDocument();
		document.setEcmaasGuid("12345667");
		documents.add(document);
		return documents;
	} 

	
	
	private ProjectNote getProjectNoteObj() {
		ProjectNote note = new ProjectNote();
		note.setActionNote("ACTION");
		note.setComments(comments);
		note.setProjectId(projectId);
		note.setProjectNoteId(noteId);
		return note;
	}
	
	
	private Project getProjectObj() {
		Project project = new Project();
		return project;
	}
	
	private ProjectInfo getProjectInfoObj() {
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setDevelopmentType(Arrays.asList(1,2,3));
		projectInfo.setStructureType(Arrays.asList(3,4,5));
		return projectInfo;
	}
	
	private List<ProjectResidential> getProjectResidentialList(){
		ProjectResidential projectResidential = new ProjectResidential();
		List<ProjectResidential> projectResidentials = Arrays.asList(projectResidential);
		return projectResidentials;
	}
	
}
