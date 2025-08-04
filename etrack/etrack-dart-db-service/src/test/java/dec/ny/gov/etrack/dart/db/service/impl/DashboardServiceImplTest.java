package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.dao.SupportDocumentDAO;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.OutForReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Milestone;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.DashboardService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {
	  @Mock
	  private PendingAppRepo pendingAppRepo;
	  @Mock
	  private DartDBServiceUtility dartDBServiceUtility;
	  @Mock
	  private TransformationService transformationService;
	  @Mock
	  private DartMilestoneRepo dartMilestoneRepo;
	  @Mock
	  private DashboardDetailDAO dashboardDetailDAO;
	  @Mock
	  private SupportDocumentRepo supportDocumentRepo;
	  @Mock
	  private SupportDocumentDAO supportDocumentDao;
	  @Mock
	  private DartDbService dartDBService;
	  @Mock
	  private DartDbDAO dartDBDAO;
	  
	  @InjectMocks
	  DashboardService dashboardService = new DashboardServiceImpl();

	  @Test
	  public void getResumeEntryPrjectsTest() {
		  List<DashboardDetail> dashboardDetails = dashboardService.getResumeEntryPrjects("","");
		  assertEquals(0,dashboardDetails.size());
	  }
	  
	  @Test
	  public void getValidateEligibleProjectsTest() {
		  List<PendingApplication> validateApplications = new ArrayList<>();
		  PendingApplication pending = new PendingApplication();
		  validateApplications.add(pending);
		  when(pendingAppRepo.findAllValidationEligibleApplicationsByUserId("")).thenReturn(validateApplications);
		  List<DashboardDetail> validationEligibleApplications = new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  dashboardDetail.setProjectId(1L);
		  validationEligibleApplications.add(dashboardDetail);
		  when(transformationService.prepareApplicationInfo("", "", validateApplications, null)).thenReturn(validationEligibleApplications);
		  List<DashboardDetail> validationEligibleProjects =new ArrayList<>();
		  validationEligibleProjects.add(dashboardDetail);
		  when(dartDBServiceUtility.amendMunicipalityDetails(validationEligibleApplications)).thenReturn(validationEligibleProjects);
		  List<DashboardDetail> eligible = dashboardService.getValidateEligibleProjects("", "");
		  assertEquals(1,eligible.size());
	  }
	  
	  @Test
	  public void getAllActiveProjects() {
		  String userId=""; String contextId="";
	    List<DartMilestone> dartMilestonesList = new ArrayList<>();
	    DartMilestone dartMilestone = new DartMilestone();
	    dartMilestone.setBatchId(201L);
	    dartMilestonesList.add(dartMilestone);
	    Mockito.lenient().when(dartMilestoneRepo.findAll()).thenReturn(dartMilestonesList);
	    List<DartApplication> allactiveApplicationsList = new ArrayList<>();
	    DartApplication dartApplication = new DartApplication();
	    allactiveApplicationsList.add(dartApplication);
	    List<DashboardDetail> dashboardDetails = new ArrayList<>();
	    DashboardDetail dashboardDetail= new DashboardDetail();
	    dashboardDetail.setProjectId(100L);
	    dashboardDetails.add(dashboardDetail);
//	    when(dashboardDetailDAO.retrieveDARTPendingApplications(userId, contextId, null, null)).thenReturn(allactiveApplicationsList);
//	    when(transformationService.transformDataIntoDashboardData(allactiveApplicationsList, null,
//        new HashMap<>())).thenReturn(dashboardDetails);
	    Mockito.lenient().when(dashboardService.retrieveAllThePendingApplications(userId, contextId, new HashMap<>())).thenReturn(dashboardDetails);
	    List<DashboardDetail> eligible = dashboardService.getAllActiveProjects(userId, contextId);
		  assertEquals(0,eligible.size());
	  }
	  
	  @Test
	  public void outForReviewProjectsTest() {
		  String userId="";
		  List<DartMilestone> dartMilestonesList = new ArrayList<>();
		  DartMilestone dartMilestone = new DartMilestone();
		  dartMilestone.setBatchId(201L);
		  dartMilestonesList.add(dartMilestone);
		  List<Long> outFOrReviewProjects = new ArrayList<Long>();
		  outFOrReviewProjects.add(1L);
		  when(supportDocumentRepo.findAllOutForReviewProjects(userId)).thenReturn(outFOrReviewProjects);
		  when(dartMilestoneRepo.findAll()).thenReturn(dartMilestonesList);
		  List<DartApplication> tasksDueFromDart = new ArrayList<>();
		  when(dashboardDetailDAO.retrieveDARTDueApps("", "")).thenReturn(tasksDueFromDart);
		  List<DashboardDetail> dash = dashboardService.getTasksDueApplications("", "");
		  assertEquals(0,dash.size());
	  }
	  
	  @Test
	  public void getTasksDueApplicationsTest() {
		  List<DashboardDetail> tasksDueFromDart = dashboardService.getTasksDueApplications("", "");
		  assertEquals(0,tasksDueFromDart.size());
		  
	  }
	  
	  @Test
	  public void getApplicantResponseDueApplicationsTest() {
		  List<DashboardDetail> applicantResponseDueFromDart = dashboardService.getApplicantResponseDueApplications("", "");
		  assertEquals(0,applicantResponseDueFromDart.size());
		  
	  }
	  
	  @Test
	  public void getSuspendedApplicationsTest() {
		  List<DashboardDetail> suspendedApps = dashboardService.getSuspendedApplications("", "");
		  assertEquals(0,suspendedApps.size());
		  
	  }
	  
	  @Test
	  public void getOutForReviewApplicationsTest() {
		  String userId="";
		  String contextId="";
		  List<OutForReviewEntity> outForReviewEntityApps = new ArrayList<>();
		  OutForReviewEntity outForReviewEntity = new OutForReviewEntity();
		  outForReviewEntityApps.add(outForReviewEntity);
		  when(supportDocumentDao.retrieveOutForReviewApps(userId, contextId)).thenReturn(outForReviewEntityApps);
		  List<DashboardDetail> outforReviewApps = new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  dashboardDetail.setProjectId(1L);
		  outforReviewApps.add(dashboardDetail);
		  when(transformationService.transformOutForReviewAppsToDashboard(outForReviewEntityApps)).thenReturn(outforReviewApps);
		  List<DashboardDetail> outForReview=dashboardService.getOutForReviewApplications("", "");
		  assertEquals(1,outForReview.size());
		  
	  }
	  
	  @Test
	  public void getOutForReviewApplicationsEmptyTest() {
		  String userId="";
		  String contextId="";
		  List<OutForReviewEntity> outForReviewEntityApps = new ArrayList<>();
		  OutForReviewEntity outForReviewEntity = new OutForReviewEntity();
		  when(supportDocumentDao.retrieveOutForReviewApps(userId, contextId)).thenReturn(outForReviewEntityApps);
		  List<DashboardDetail> outforReviewApps = new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  Mockito.lenient().when(transformationService.transformOutForReviewAppsToDashboard(outForReviewEntityApps)).thenReturn(outforReviewApps);
		  List<DashboardDetail> outForReview=dashboardService.getOutForReviewApplications("", "");
		  assertNotEquals(0, outForReview);;
		  
	  }
	  
	  @Test
	  public void getEmergencyAuthorizationApplicationsEmptyTest() {
		  String userId="";
		  String contextId="";
		  List<DartApplication> emergencyAuthorizationAppsList = new ArrayList<>();
		  DartApplication dartApplication = new DartApplication();
		  emergencyAuthorizationAppsList.add(dartApplication);
		  when(dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId)).thenReturn(emergencyAuthorizationAppsList);
		  List<DashboardDetail> emergencyAuthorizationApps = new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  dashboardDetail.setProjectId(1L);
		  emergencyAuthorizationApps.add(dashboardDetail);
		  Map<Long, Long> outForReviewProjectMapping = new HashMap<>();
		  List<DashboardDetail> emergencyAuth=dashboardService.getEmergencyAuthorizationApplications("", "");
		  assertEquals(0,emergencyAuth.size());
	  }
	  
	  @Test
	  public void getEmergencyAuthorizationApplicationsTest() {
		  String userId="";
		  String contextId="";
		  List<DartApplication> emergencyAuthorizationAppsList = new ArrayList<>();
		  DartApplication dartApplication = new DartApplication();
		  emergencyAuthorizationAppsList.add(dartApplication);
		  when(dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId)).thenReturn(emergencyAuthorizationAppsList);
		  List<DashboardDetail> emergencyAuthorizationApps = new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  dashboardDetail.setProjectId(1L);
		  emergencyAuthorizationApps.add(dashboardDetail);
		  Map<Long, Long> outForReviewProjectMapping = new HashMap<>();
		  DartMilestone dartMilestone = new DartMilestone();
		  dartMilestone.setApplId(1L);
		  Map<Long,DartMilestone > projectIdAndReviewerName = new HashMap<>();
		  Mockito.lenient().when(transformationService.transformDataIntoDashboardData(emergencyAuthorizationAppsList,
				  outForReviewProjectMapping,projectIdAndReviewerName)).thenReturn(emergencyAuthorizationApps);	
		  List<String> projectIdAndReviewerNameList = new ArrayList<>();
		  Set<Long> projectIds = new HashSet<>();
		  projectIds.add(123L);
		  Mockito.lenient().when(supportDocumentRepo.findReviewerDetailsByProjectIds(projectIds)).thenReturn(projectIdAndReviewerNameList);
		  List<DashboardDetail> emergencyAuth=dashboardService.getEmergencyAuthorizationApplications("", "");
		  assertEquals(1,emergencyAuth.size());
	  }
	  
	  @Test
	  public void getRegionalAllActiveApplicationsTest() {
		  String userId="";
		  String contextId="";
		  List<DartApplication> allactiveApplicationsList = new ArrayList<>();
		  Long facilityRegion =1l;
		  Mockito.lenient().when(dartDBDAO.getUserRegionId(userId, contextId)).thenReturn(facilityRegion);
		  Mockito.lenient().when(dashboardDetailDAO.retrieveDARTPendingApplications(null, contextId,null,0)).thenReturn(allactiveApplicationsList);
		  List<DashboardDetail> allActive=new ArrayList<>();
		  DashboardDetail dashboardDetail = new DashboardDetail();
		  allActive.add(dashboardDetail);
		  Mockito.lenient().when(transformationService.transformDataIntoDashboardData(allactiveApplicationsList,
				  null,null)).thenReturn(allActive);
		  List<DashboardDetail> alActiveApps = dashboardService.getRegionalAllActiveApplications(userId, contextId,0);
		  		  
	  }
	  
	  @Test
	  public void getRegionalUnvalidatedApplicationsTest() {
		  String userId="";
		  String contextId="";
		  List<PendingApplication> regionalProjects = new ArrayList<>();
		  PendingApplication pendingApplication = new PendingApplication();
		  regionalProjects.add(pendingApplication);
		  Mockito.lenient().when(pendingAppRepo.findAllUnValidatedApplications()).thenReturn(regionalProjects);
		  Mockito.lenient().when(pendingAppRepo.findAllUnValidatedApplicationsByRegionId(0)).thenReturn(regionalProjects);
		  Mockito.lenient().when(dartDBServiceUtility.getLegalResponsePartyDetails(null, contextId, true)).thenReturn(null, null);
		  List<DashboardDetail> regionalProjectsList = new ArrayList<>();
		  List<DashboardDetail> dashDetail = dashboardService.getRegionalUnvalidatedApplications(userId,contextId,0);
		  
	  }
	  
	  @Test
	  public void getRegionalProgramReviewApplicationsTest() {
		  List<DashboardDetail> regionalProgramReviewApps = dashboardService.getRegionalProgramReviewApplications("","", 0);
		  assertNotEquals(0,regionalProgramReviewApps);		  
	  }

	  
	  @Test
	  public void getRegionalDisposedApplicationsTest() {
		  String userId="";
		  String contextId="";
		  List<DartApplication> disposedApps = new ArrayList<>();
		  DartApplication dartApplication = new DartApplication();
		  dartApplication.setRegion(0);
		  disposedApps.add(dartApplication);
		  Mockito.lenient().when(dashboardDetailDAO.retrieveDARTDisposedApps(userId, contextId, dartApplication.getRegion())).thenReturn(disposedApps, null);
		  List<DashboardDetail> transformedDisposedApps = new ArrayList<>();
		  DashboardDetail dashboardDetail=new DashboardDetail();
		  Mockito.lenient().when( transformationService.transformDataIntoDashboardData(disposedApps, null, null )).thenReturn(transformedDisposedApps, null);
		  List<DashboardDetail> regionalDisposedApps = dashboardService.getRegionalDisposedApplications(userId, contextId,0);
	  }
	  
}
