package dec.ny.gov.etrack.permit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.ApplicationPermit;
import dec.ny.gov.etrack.permit.model.ApplicationPermitDetail;
import dec.ny.gov.etrack.permit.model.DIMSRRequest;
import dec.ny.gov.etrack.permit.model.DartUploadDetail;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.EmailContent;
import dec.ny.gov.etrack.permit.model.ProjectInfo;
import dec.ny.gov.etrack.permit.model.ReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.ReviewedPermit;
import dec.ny.gov.etrack.permit.service.ETrackPermitService;
import dec.ny.gov.etrack.permit.service.EmailService;
import dec.ny.gov.etrack.permit.service.ProjectService;


@RunWith(SpringJUnit4ClassRunner.class)
public class PermitControllerTest {
	
	
	@InjectMocks
	private ETrackPermitController controller;
	
	
	@Mock
	private ETrackPermitService permitService;
	
	@Mock
	private ProjectService projectService;
	
	@Mock
	private EmailService emailService;
	
	
	@Mock
	private ApplicationPermit applPermit;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	
	private String userId = "jxpuvoge";
	private String token = "123456";
	private Long projectId = 1002L;
	private String validCategory = "SUPPORT-DOC";
	private String acknowledgedInd = "2";
	
	//Save Validator Step Detail
	@Test(expected = BadRequestException.class)
	public void testSaveValidatorStepDetailThrowsBREForNullCategory() {
		this.controller.saveValidatorStepDetail(userId, projectId, null, 10, 4);
	}
	
	@Test(expected = BadRequestException.class)
	public void testSaveValidatorStepDetailThrowsBREForInvalidCategory() {
		this.controller.saveValidatorStepDetail(userId, projectId, "Invalid Cat", 10, 4);
	}
	
	@Test
	public void testSaveValidatorStepDetailSuccessfully() {
		this.controller.saveValidatorStepDetail(userId, projectId, validCategory, 10, 0);
	}

	//storeProjectInfo Tests
	
	@Test
	public void testStoreProjectInfoThrowsBREForNullProject() {
		ProjectInfo projectInfo = getProjectInfo();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("One or more invalid Project information is passed");
		this.controller.storeProjectInfo(userId, null, projectInfo);
	}
	
	@Test
	public void testStoreProjectInfoThrowsBREForInvalidDate() {
		ProjectInfo projectInfo = getProjectInfo();
		projectInfo.setEstmtdCompletionDate("Invalid date");
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid format of Date value is passed");
		this.controller.storeProjectInfo(userId, projectId, projectInfo);
	}
	
	@Test
	public void testStoreProjectInfoCallsProjectServiceMethod() {
		ProjectInfo projectInfo = getProjectInfo();
		this.controller.storeProjectInfo(userId, projectId, projectInfo);
		verify(projectService).storeProjectInfo(Mockito.anyString(), Mockito.anyString(),  Mockito.anyLong(), Mockito.any());
	}

	//saveApplicationPermits tests
	
	@Test
	public void testSaveApplicationPermitsThrowsBREForInvalidUserId() {
		ApplicationPermit permit = getApplicationPermit();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.saveApplicationPermits(null, projectId, permit);
	}
	
	@Test
	public void testSaveApplicationPermitsThrowsBREForInvalidProjectId() {
		ApplicationPermit permit = getApplicationPermit();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.saveApplicationPermits(userId, null, permit);
	}
	

	@Test
	public void testSaveApplicationPermitsThrowsBREForInvalidConstrnType() {
		ApplicationPermit permit = getApplicationPermit();
		permit.setConstrnType(10);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid Construction type is passed");
		this.controller.saveApplicationPermits(userId, projectId, permit);
	}
	
	@Test
	public void testSaveApplicationPermitsCallsService() {
		ApplicationPermit permit = getApplicationPermit();
		permit.setConstrnType(1);
		this.controller.saveApplicationPermits(userId, projectId, permit);
		verify(permitService, times(1)).saveApplicationPermits(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ApplicationPermit.class));
	}

	
	//updateApplicationAppType Tests
	
	@Test
	public void testUpdateApplicationAppTypesThrowsBREForInvalidProjectId() {
		ApplicationPermit permit = mock(ApplicationPermit.class);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.updateApplicationAppType(userId, null, permit);
	}
	
	@Test
	public void testUpdateApplicationAppTypesThrowsBREForInvalidUserId() {
		ApplicationPermit permit = mock(ApplicationPermit.class);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.updateApplicationAppType(null, projectId, permit);
	}
	
	@Test
	public void testUpdateApplicationAppTypesCallsPermitService() {
		ApplicationPermit permit = mock(ApplicationPermit.class);
		this.controller.updateApplicationAppType(userId, projectId, permit);
		verify(permitService, times(1)).updateAmendedApplicationTransTypes(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ApplicationPermit.class));
	}

	//saveAdditionalApplicationPermit tests
	
	@Test
	public void testSaveAdditionalApplicationPermitThrowsBREForInvalidUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("One or more mandatory detail is missing for the project");
		this.controller.saveAdditionalApplicationPermit(null, projectId, "123", token, applPermit);

	}
	
	@Test
	public void testSaveAdditionalApplicationPermitThrowsBREForInvalidProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("One or more mandatory detail is missing for the project");
		this.controller.saveAdditionalApplicationPermit(userId, null, "123", token, applPermit);

	}
	
	@Test
	public void testSaveAdditionalApplicationPermitCallsService() {
		this.controller.saveAdditionalApplicationPermit(userId, projectId, "123", token, applPermit);
		verify(permitService, times(1)).saveAdditionalApplicationPermit(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ApplicationPermit.class));
	}
	
	
	//assignContacts test cases
	@Test
	public void testAssignContactsThrowsBREforNoContacts() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.assignContacts(userId, projectId, null);
	}
	
	@Test
	public void testAssignContactsThrowsBREforNoUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.assignContacts(null, projectId, getPermitDetailList());
	}
	
	@Test
	public void testAssignContactsThrowsBREforNoProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.assignContacts(userId, null, getPermitDetailList());
	}
	
	@Test
	public void testAssignContactsCallsService() {
		ApplicationPermitDetail detail = new ApplicationPermitDetail();
		List<ApplicationPermitDetail> detailList = Arrays.asList(detail);
		this.controller.assignContacts(userId, projectId, detailList);
		verify(permitService, times(1)).assignContacts(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyListOf((ApplicationPermitDetail.class)));
	}
	
	//deleteApplicationPermitTests
	
	@Test
	public void testDeleteApplicationPermitThrowsBREForNoUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermit(null, projectId, 10L, "CE");
	}
	
	@Test
	public void testDeleteApplicationPermitThrowsBREForNoProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermit(userId, null, 10L, "CE");
	}
	
	@Test
	public void testDeleteApplicationPermitThrowsBREForNoPermitType() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermit(userId, null, 10L, "");
	}
	
	@Test
	public void testDeleteApplicationPermitThrowsBREForInvalidApplId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermit(userId, projectId, -10L, "CE");
	}
	
	@Test
	public void testDeleteApplicationPermitCallsService() {
		this.controller.deleteApplicationPermit(userId, projectId, 10L, "CE");
		verify(permitService, times(1)).removeApplicationPermit(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
	}
	
//deleteApplicationPermits tests
	@Test
	public void testDeleteApplicationPermitsThrowsBREForNullUserId() {
		List<Long> applicantIds = Arrays.asList(10L, 2L);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermits(null, projectId, applicantIds);
	}
	
	@Test
	public void testDeleteApplicationPermitsThrowsBREForNullProjectId() {
		List<Long> applicantIds = Arrays.asList(10L, 2L);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermits(userId, null, applicantIds);
	}
	
	@Test
	public void testDeleteApplicationPermitsThrowsBREForEmptyApplicantIds() {
		List<Long> applicantIds = Arrays.asList();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.deleteApplicationPermits(userId, projectId, applicantIds);
	}
	
	@Test
	public void testDeleteApplicationPermitsCallsService() {
		List<Long> applicantIds = Arrays.asList(10L, 2L);
		this.controller.deleteApplicationPermits(userId, projectId, applicantIds);
		verify(permitService, times(1)).removeApplicationPermits(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyListOf(Long.class));
	}
	
	
	//getApplicationPermits tests
	@Test
	public void testGetApplicationPermitsThrowsBREForNoUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		
		this.controller.getApplicationPermits(null, projectId);
	}
	
	@Test
	public void testGetApplicationPermitsThrowsBREForNoProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.getApplicationPermits(userId, null);
	}
	
	@Test
	public void testGetApplicationPermitsReturnsList() {
	assertTrue(this.controller.getApplicationPermits(userId, projectId) instanceof List);
	}
	



	//supportDocument tests
	@Test
	public void testSupportDocumentCallsProjectService() {
		this.controller.supportDocument(userId, projectId);
		verify(projectService).addSupportDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
	}
	

	//projectSubmission tests
	
	@Test
	public void testProjectSubmissionThrowsBREForInvalidProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.projectSubmission(userId, acknowledgedInd, null);
	}

	@Test
	public void testProjectSubmissionThrowsBREForInvalidUserId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.controller.projectSubmission(null, acknowledgedInd, projectId);
	}
	
	@Test
	public void testProjectSubmissionCallsProjectService() {
		this.controller.projectSubmission(userId, acknowledgedInd, projectId);
		verify(projectService, times(1)).submitProject(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
	}



	//updateSignatureReceived tests
	
	@Test
	public void testUpdateSignatureReceviedThrowsBREForNoPublicIds() {
		List<Long> publicIds = new ArrayList<>();
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no public Ids passed to update the Signature");
		this.controller.updateSignatureReceived(userId, projectId, publicIds);
	}
	
	
	@Test
	public void testUpdateSignatureReceviedCallsProjectService() {
		List<Long> publicIds = Arrays.asList(1L, 2L);
		this.controller.updateSignatureReceived(userId, projectId, publicIds);
		verify(projectService, times(1)).updateSignatureReceived(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyListOf(Long.class));
	}


		
		//assignProjectReviewerToDocument test
		
		@Test
		public void testAssignProjectReviewerToDocThrowsBREForInvalidProjectId() {
			DocumentReview doc = new DocumentReview();
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project id is blank or invalid passed");
			this.controller.assignProjectReviewerToDocument(userId, null, doc);
		}
		
		@Test
		public void testAssignProjectReviewerToDocThrowsBREForNoReviewerName() {
			DocumentReview doc = new DocumentReview();
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Review request details should not be empty");
			this.controller.assignProjectReviewerToDocument(userId, projectId, doc);
		}
		
		@Test
		public void testAssignProjectReviewerToDocReturnsEmailContent() {
			DocumentReview doc = new DocumentReview();
			doc.setReviewerName("John");
			doc.setDueDate("12/21/2021");
			doc.setDateAssigned("12/23/2019");
			doc.setDocumentIds(Arrays.asList(1L));
			EmailContent content = new EmailContent();
			when(projectService.updateDocumentReviewerDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(DocumentReview.class))).thenReturn(content);
		EmailContent result =	this.controller.assignProjectReviewerToDocument(userId, projectId, doc);
		assertEquals(content, result);
		}
		
		
		//updateDocReviewCompletion tests
		
		@Test
		public void testUpdateDocReviewCompletionThrowsBREForInvalidProjectId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project id is blank or invalid passed");
			this.controller.updateDocReviewCompletion(token, userId, null, null);
		}
		
		@Test
		public void testUpdateDocReviewCompletionThrowsBREForNullDetails() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Review request details should not be empty");
			this.controller.updateDocReviewCompletion(token, userId, projectId, null);
		}
		
		@Test
		public void testUpdateDocReviewCompletionCallsProjectService() {
			ReviewCompletionDetail detail = new ReviewCompletionDetail();
			this.controller.updateDocReviewCompletion(token, userId, projectId, detail);
			verify(projectService, times(1)).updateDocumentReviewerCompletionDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(ReviewCompletionDetail.class));
		}
		
		//updateEmailReadStatus tests
		@Test
		public void testUpdateEmailReadStatusCallsEmailService() {
			this.controller.updateEmailReadStatus(userId, projectId, 100L);
			verify(emailService).updateEmailReadStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
		}

		//deleteEMailCorrespondenceTests
		@Test
		public void testDeleteEmailCorrespondenceCallsEmailService() {
			this.controller.deleteEmailCorrespondence(userId, projectId, 100L);
			verify(emailService).deleteEmailCorrespondence(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
		}
		
			//updateEcistingPermitAmendRequest tests
			
		@Test
		public void testUpdateExistingPermitAmendRequestsThrowsBREForNoPermits() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Input request is invalid");
			this.controller.updateExistingPermitAmendRequest(userId, projectId, null);
		}
		
		
		@Test
		public void testUpdateExistingPermitAmendRequestsThrowsBREForNoProjId() {
			ApplicationPermitDetail detail = new ApplicationPermitDetail();
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Input request is invalid");
			this.controller.updateExistingPermitAmendRequest(userId, null, Arrays.asList(detail));
		}
		
		@Test
		public void testUpdateExistingPermitAmendRequestsThrowsBREForNoUserId() {
			ApplicationPermitDetail detail = new ApplicationPermitDetail();
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Input request is invalid");
			this.controller.updateExistingPermitAmendRequest(null, projectId, Arrays.asList(detail));
		}
		
		@Test
		public void testUpdateExistingPermitAmendRequestsCallsPermitService() {
			ApplicationPermitDetail detail = new ApplicationPermitDetail();
			this.controller.updateExistingPermitAmendRequest(userId, projectId, Arrays.asList(detail));
			verify(permitService).updateExistingPermitAmendDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
		}	
	
		//retrievePermitFormDetails tests
		
		@Test
		public void testRetrievePermitFormDetailsReturnsList() {
			ApplicationPermitDetail detail = new ApplicationPermitDetail();
			List<ApplicationPermitDetail> detailsList = Arrays.asList(detail);
			when(permitService.retrievePermitDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(detailsList);
			List<ApplicationPermitDetail> result = this.controller.retrievePermitFormDetails(userId, projectId, 100L);
			assertEquals(detailsList, result);
		}
		
	
		//storeReviewedPermits tests
		@Test
		public void testStoreReviewedPermitsThrowsBREForInvalidUserId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
			this.controller.storeReviewedPermits(null, projectId, null);
		}
		
		@Test
		public void testStoreReviewedPermitsThrowsBREForInvalidProjectId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
			this.controller.storeReviewedPermits(userId, null, null);
		}
		
		
		@Test
		public void testStoreReviewedPermitsThrowsBREForNoPermits() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("There is no reviewed Permits requested by the user to Persist");
			this.controller.storeReviewedPermits(userId, projectId, null);
		}
		
		@Test
		public void testStoreReviewedPermitsReturnsObject() {
			Map<String, List<ReviewedPermit>> permitsMap = new HashMap<>();
			permitsMap.put("Test", Arrays.asList(new ReviewedPermit()));
			Object obj = new Object();
			when(permitService.storeReviewedPermits(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(obj);
			controller.storeReviewedPermits(userId, projectId, permitsMap);
			assertTrue(controller.storeReviewedPermits(userId, projectId, permitsMap) instanceof Object);
		}
		
		//storeDIMSRDetails tests
		@Test
		public void testStoreDIMSRDetailsCallsProjectService() {
			this.controller.storeDIMSRDetails(userId, token, "10", new DIMSRRequest());
			verify(projectService).saveDIMSRDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
		}
		
		//upload
		@Test
		public void testUploadProjectDetailsToDARTBREForInvalidUserId() {
//			exceptionRule.expect(BadRequestException.class);
//			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
			assertThrows(BadRequestException.class,()->this.controller.uploadProjectDetailsToDART(null,"","", projectId, null));
		}
		
		@Test
		public void testUploadProjectDetailsToDARTReturnsObject() {
			Map<String, List<ReviewedPermit>> permitsMap = new HashMap<>();
			permitsMap.put("Test", Arrays.asList(new ReviewedPermit()));
			Object obj = new Object();
			DartUploadDetail dartUploadDetail = new DartUploadDetail();
			dartUploadDetail.setReceivedDate("03/03/2024");
//			exceptionRule.expect(BadRequestException.class);
//			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
//			when(permitService.uploadProjectDetailsToDART(Mockito.anyString(), Mockito.anyString(), 
//					Mockito.anyString(), Mockito.anyLong(), dartUploadDetail )).thenReturn(obj);
			assertThrows(BadRequestException.class,()->controller.uploadProjectDetailsToDART(userId, "", "",projectId, dartUploadDetail));
//			assertTrue(controller.uploadProjectDetailsToDART(userId,"", "", projectId, dartUploadDetail) instanceof Object);
		}
		
		@Test
		public void testUploadProjectDetailsToDARTReturnsObject1() {
			Map<String, List<ReviewedPermit>> permitsMap = new HashMap<>();
			permitsMap.put("Test", Arrays.asList(new ReviewedPermit()));
			Object obj = new Object();
			DartUploadDetail dartUploadDetail = new DartUploadDetail();
			dartUploadDetail.setReceivedDate("03/03/2024");
			dartUploadDetail.setReviewedPermits(permitsMap);
//			exceptionRule.expect(BadRequestException.class);
//			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
//			when(permitService.uploadProjectDetailsToDART(Mockito.anyString(), Mockito.anyString(), 
//					Mockito.anyString(), Mockito.anyLong(), dartUploadDetail )).thenReturn(obj);
			controller.uploadProjectDetailsToDART(userId, "", "",projectId, dartUploadDetail);
//			assertTrue(controller.uploadProjectDetailsToDART(userId,"", "", projectId, dartUploadDetail) instanceof Object);
		}



	//private methods	
	
	private List<ApplicationPermitDetail> getPermitDetailList(){
		ApplicationPermitDetail detail = new ApplicationPermitDetail();
		List<ApplicationPermitDetail> detailList = Arrays.asList(detail);
		return detailList;
	}
	
	private ApplicationPermit getApplicationPermit() {
		ApplicationPermit permit = new ApplicationPermit();
		
		return permit;
	}
	

	
	private ProjectInfo getProjectInfo() {
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setProposedStartDate("01/13/2024");
		projectInfo.setEstmtdCompletionDate("01/31/2024");
		projectInfo.setConstrnType(1);
		return projectInfo;
	}

}
