package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.EmailContent;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.ProjectRejectDetail;
import dec.ny.gov.etrack.dart.db.model.VirtualDesktopEmailShortDesc;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.EmailService;
import dec.ny.gov.etrack.dart.db.service.OnlineUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartDbServiceControllerTest {

	@Mock
	private DartDbService dartDbService;

	@Mock
	private OnlineUserService onlineUserService;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private DartDbServiceController dartDbServiceController;
	
	String userId = "1234";
	Long projectId = 1l;
	Integer facilityRegionId = 1;
	String token = "token";
	String contextId = UUID.randomUUID().toString();
	
	@Test
	void getProjectInfoTest() {
		when(dartDbService.getProjectInformation(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ProjectInfo());
		assertNotNull(dartDbServiceController.getProjectInfo(userId, projectId));
	}
	
	@Test
	void getProjectInfoBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getProjectInfo(null, null));
	}
	
	@Test
	void getFacilityBINsTest() {
		when(dartDbService.getFacilityBins(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getFacilityBINs(userId, projectId));
	}
	
	@Test
	void getFacilityBINsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getFacilityBINs(null, null));
	}
	
	@Test
	void getPendingApplicationsTest() {
		when(dartDbService.getUnsubmittedApps(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getPendingApplications(userId));
	}
	
	@Test
	void getPendingApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getPendingApplications(""));
	}
	
	@Test
	void getUserDashboardTest() {
		when(dartDbService.getUserDashboardDetails(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getUserDashboard(userId));
	}
	
	@Test
	void getUserDashboardBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getUserDashboard(""));
	}
	
	@Test
	void getProgramReviewerDashboardDetailsTest() {
		when(dartDbService.getProgramReviewerDashboardDetails(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getProgramReviewerDashboardDetails(userId, facilityRegionId));
	}
	
	@Test
	void getProgramReviewerDashboardDetailsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getProgramReviewerDashboardDetails("", facilityRegionId));
	}
	
	@Test
	void getAnalystsAssociatedWithRegionTest() {
		when(dartDbService.getUsersByRegionAndRoleTypeId(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getAnalystsAssociatedWithRegion(userId, facilityRegionId));
	}
	
	@Test
	void getAnalystsAssociatedWithRegionBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getAnalystsAssociatedWithRegion("", facilityRegionId));
	}
	
	@Test
	void getUsersWithValidEmailAddressTest() {
		when(dartDbService.getUsersWithValidEmailAddress(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getUsersWithValidEmailAddress(userId));
	}
	
	@Test
	void getUsersWithValidEmailAddressBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getUsersWithValidEmailAddress(""));
	}
	
	@Test
	void getProgramAreaReviewersAssociatedWithRegionTest() {
		when(dartDbService.getUsersByRegionAndRoleTypeId(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getProgramAreaReviewersAssociatedWithRegion(userId, facilityRegionId));
	}
	
	@Test
	void getProgramAreaReviewersAssociatedWithRegionBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.getProgramAreaReviewersAssociatedWithRegion("", facilityRegionId));
	}
	
	@Test
	void retrieveSupportDocumentsTest() {
		when(dartDbService.retrieveSupportDocumentSummary(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveSupportDocuments(userId, projectId));
	}
	
	@Test
	void retrieveSupportDocumentsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.retrieveSupportDocuments("", projectId));
	}
	
	@Test
	void retrieveRequiredApplicantsToSignTest() {
		when(dartDbService.retrieveRequiredApplicantsToSign(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveRequiredApplicantsToSign(userId, projectId));
	}
	
	@Test
	void retrieveRequiredApplicantsToSignBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.retrieveRequiredApplicantsToSign("", projectId));
	}
	
	@Test
	void retrieveProjectSummaryTest() {
		when(dartDbService.retrieveProjectSummary(Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveProjectSummary(userId, projectId, token));
	}
	
	@Test
	void retrieveProjectSummaryBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartDbServiceController.retrieveProjectSummary("", projectId, token));
	}
	
	@Test
	void retrieveAssignmentDetailsTest() {
		when(dartDbService.retrieveAssignmentDetails(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveAssignmentDetails(userId, projectId));
	}
	
	@Test
	void retrieveAnalystDashboardAlertsTest() {
		when(dartDbService.retrieveAnalystsAlerts(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveAnalystDashboardAlerts(userId));
	}
	
	@Test
	void viewAnalystDashboardAlertsTest() {
		when(dartDbService.viewAnalystDashboardAlerts(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.viewAnalystDashboardAlerts(userId));
	}
	
	@Test
	void retrieveEligibleReviewDocumentsTest() {
		when(dartDbService.retrieveEligibleReviewDocuments(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveEligibleReviewDocuments(userId, projectId));
	}
	
	@Test
	void retrieveRegionIdByUserId() {
		when(dartDbService.findRegionIdByUserId(Mockito.any(), Mockito.any())).thenReturn(1l);
		assertNotNull(dartDbServiceController.retrieveRegionIdByUserId(userId));
	}
	
	@Test
	void retrieveEmailNotificationsIntoDashboardTest() {
		when(emailService.retrieveEmailNotificationDetails(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveEmailNotificationsIntoDashboard(userId));
	}
	
	@Test
	void retrieveEmailNotificationsIntoVirtualDesktopTest() {
		when(emailService.retrieveEmailNotificationsInVirtualDesktop(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(new HashMap<String, List<VirtualDesktopEmailShortDesc>>());
		assertNotNull(dartDbServiceController.retrieveEmailNotificationsIntoVirtualDesktop(userId, projectId));
	}

	@Test
	void retrieveEmailCorrespondenceDetailsForRequestorTest() {
		when(emailService.retrieveCorrespondencesForTheRequestor(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveEmailCorrespondenceDetailsForRequestor(userId, projectId, "email1234", "receiver1234", "R"));
	}
	
	@Test
	void retrieveEmailCorrespondenceDetailsForRequestorBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbServiceController.retrieveEmailCorrespondenceDetailsForRequestor(userId, 0l, "email1234", "receiver1234", "type"));
		
		assertThrows(BadRequestException.class, ()->dartDbServiceController.retrieveEmailCorrespondenceDetailsForRequestor(userId, projectId, "email1234", "receiver1234", "type"));
	}
	
	@Test
	void retrieveEnvelopsDetailByCorrespondenceIdTest() {
		when(emailService.retrieveEmailCorrespondenceByCorrespondenceId(Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(), Mockito.anyBoolean())).thenReturn(new EmailContent());
		assertNotNull(dartDbServiceController.retrieveEnvelopsDetailByCorrespondenceId(userId, projectId, 1l));
	}
	
	@Test
	void retrieveCorrespondenceDetailsTest() {
		when(emailService.retrieveEmailCorrespondenceByDocumentId(Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveCorrespondenceDetails(userId, "123", projectId, 1l));
	}
	
	@Test
	void retrieveDetailForDIMSRTest() {
		when(dartDbService.retrieveSupportDetailsForDIMSR(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveDetailForDIMSR(userId, "123"));
	}
	
	@Test
	void getOnlineUserDashboardTest() {
		when(onlineUserService.getOnlineUserDashboardDetails(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.getOnlineUserDashboard(userId));
	}
	
	@Test
	void getOnlineUserDashboardBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbServiceController.getOnlineUserDashboard(""));
	}
	
	@Test
	void retrieveActiveAuthorizationPermitsTest() {
		when(dartDbService.retrieveActiveAuthorizationPermits(Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveActiveAuthorizationPermits(userId, UUID.randomUUID(), projectId, 1l));
	}
	
	@Test
	void retrieveActiveAuthorizationPermitsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbServiceController.getOnlineUserDashboard(""));
	}
	
	@Test
	void retrieveCorrespondencesByReviewerIdTest() {
		when(emailService.retrieveCorrespondenceByReviewerAndDocumentIds(Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertNotNull(dartDbServiceController.retrieveCorrespondencesByReviewerId(projectId, userId, userId, Arrays.asList(1l)));
	}
	
	@Test
	void retrieveCorrespondencesByReviewerIdBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbServiceController.retrieveCorrespondencesByReviewerId(null, userId, userId, Arrays.asList(1l)));
	}
	
	@Test
	void retrieveProjectRejectionDetailsTest() {
		when(dartDbService.retrieveProjectRejectionDetails(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ProjectRejectDetail());
		assertNotNull(dartDbServiceController.retrieveProjectRejectionDetails(userId, projectId));
	}
	
	@Test
	void retrieveProjectRejectionDetailsdBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbServiceController.retrieveProjectRejectionDetails(null, null));
	}
}
