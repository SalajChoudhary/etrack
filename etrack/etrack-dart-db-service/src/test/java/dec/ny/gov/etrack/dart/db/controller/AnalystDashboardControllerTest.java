package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.service.DashboardService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class AnalystDashboardControllerTest {

	@InjectMocks
	private AnalystDashboardController analystDashboardController;
	
	@Mock
	private DashboardService dashboardService;
	
	String userId = "1234"; 
	Integer facilityRegionId = 1;
	
	@Test
	public void getResumeEntryProjectsTest() {
		List<DashboardDetail> dashboardDetails = new ArrayList<>();
		when(dashboardService.getResumeEntryPrjects(Mockito.anyString(), Mockito.anyString())).thenReturn(dashboardDetails);
		Object object = analystDashboardController.getResumeEntryProjects(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getResumeEntryProjectsBadRequestTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getResumeEntryProjects(""));
	}
	
	@Test
	public void getAllActiveApplicationsTest() {
		when(dashboardService.getAllActiveProjects(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getAllActiveApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getAllActiveApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getAllActiveApplications(""));
	}
	
	@Test
	public void getAllValidateProjectsTest() {
		when(dashboardService.getValidateEligibleProjects(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getAllValidateProjects(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getAllValidateProjectsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getAllValidateProjects(""));
	}
	
	@Test
	public void getTasksDueApplicationsTest() {
		when(dashboardService.getTasksDueApplications(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getTasksDueApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getTasksDueApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getTasksDueApplications(""));
	}
	
	@Test
	public void getApplicantResponseDueApplicationsTest() {
		when(dashboardService.getApplicantResponseDueApplications(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getApplicantResponseDueApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getApplicantResponseDueApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getApplicantResponseDueApplications(""));
	}
	
	@Test
	public void getSuspendedApplicationsTest() {
		when(dashboardService.getSuspendedApplications(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getSuspendedApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getSuspendedApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getSuspendedApplications(""));
	}
	
	@Test
	public void getOutForReviewApplicationsTest() {
		when(dashboardService.getOutForReviewApplications(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getOutForReviewApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getOutForReviewApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getOutForReviewApplications(""));
	}
	
	@Test
	public void getEmergencyAuthorizationApplicationsTest() {
		when(dashboardService.getEmergencyAuthorizationApplications(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getEmergencyAuthorizationApplications(userId);
		assertNotNull(object);
	}
	
	@Test
	public void getEmergencyAuthorizationApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getEmergencyAuthorizationApplications(""));
	}
	
	@Test
	public void getRegionalAllActiveApplicationsTest() {
		when(dashboardService.getRegionalAllActiveApplications(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getRegionalAllActiveApplications(userId, facilityRegionId);
		assertNotNull(object);
	}
	
	@Test
	public void getRegionalAllActiveApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getRegionalAllActiveApplications("", facilityRegionId));
	}
	
	@Test
	public void getRegionalUnvalidatedApplicationsTest() {
		when(dashboardService.getRegionalUnvalidatedApplications(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getRegionalUnvalidatedApplications(userId, facilityRegionId);
		assertNotNull(object);
	}
	
	@Test
	public void getRegionalUnvalidatedApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getRegionalUnvalidatedApplications("", facilityRegionId));
	}
	
	@Test
	public void getRegionalProgramReviewApplicationsTest() {
		when(dashboardService.getRegionalProgramReviewApplications(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getRegionalProgramReviewApplications(userId, facilityRegionId);
		assertNotNull(object);
	}
	
	@Test
	public void getRegionalProgramReviewApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getRegionalProgramReviewApplications("", facilityRegionId));
	}
	
	@Test
	public void getRegionalDisposedApplicationsTest() {
		when(dashboardService.getRegionalDisposedApplications(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		Object object = analystDashboardController.getRegionalDisposedApplications(userId, facilityRegionId);
		assertNotNull(object);
	}
	
	@Test
	public void getRegionalDisposedApplicationsBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()-> analystDashboardController.getRegionalDisposedApplications("", facilityRegionId));
	}
}
