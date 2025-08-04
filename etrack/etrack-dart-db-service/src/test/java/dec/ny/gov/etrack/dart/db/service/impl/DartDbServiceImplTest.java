package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.dao.SupportDocumentDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.County;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartInCompleteMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.DartSuspensionMilestone;
import dec.ny.gov.etrack.dart.db.entity.DocumentReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.entity.FacilityBIN;
import dec.ny.gov.etrack.dart.db.entity.FacilityLRPDetail;
import dec.ny.gov.etrack.dart.db.entity.GIInquiryAlert;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.entity.LitigationHold;
import dec.ny.gov.etrack.dart.db.entity.LitigationHoldHistory;
import dec.ny.gov.etrack.dart.db.entity.Municipality;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectAlert;
import dec.ny.gov.etrack.dart.db.entity.ProjectNote;
import dec.ny.gov.etrack.dart.db.entity.PublicAndFacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.SignedApplicant;
import dec.ny.gov.etrack.dart.db.entity.SupportDocument;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentEntity;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Alert;
import dec.ny.gov.etrack.dart.db.model.BridgeIdNumber;
import dec.ny.gov.etrack.dart.db.model.CurrentDartStatus;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.model.ProjectRejectDetail;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;
import dec.ny.gov.etrack.dart.db.model.ReviewerDocumentDetail;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.CountyRepo;
import dec.ny.gov.etrack.dart.db.repo.DartInCompleteMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DartSuspendedMilestoneRepo;
import dec.ny.gov.etrack.dart.db.repo.DocumentReviewRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityBINRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.FoilRequestRepo;
import dec.ny.gov.etrack.dart.db.repo.GIInquiryAlertRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;
import dec.ny.gov.etrack.dart.db.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.dart.db.repo.LitigationHoldRequestRepo;
import dec.ny.gov.etrack.dart.db.repo.MunicipalityRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectAlertRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.repo.ReviewDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SignedApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.UserAssignmentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartDbServiceImplTest {

	@Mock
	private DartDbDAO dartDBDAO;

	@Mock
	private TransformationService transformationService;

	@Mock
	private ProjectRepo projectRepo;

	@Mock
	private FacilityBINRepo facilityBinRepo;

	@Mock
	private PendingAppRepo pendingAppRepo;

	@Mock
	private PermitRepo permitRepo;

	@Mock
	private FacilityRepo facilityRepo;

	@Mock
	private SignedApplicantRepo signedApplicantRepo;

	@Mock
	private ProjectNoteRepo projectNoteRepo;

	@Mock
	private ApplicationRepo applicationRepo;

	@Mock
	private SupportDocumentRepo supportDocumentRepo;

	@Mock
	private InvoiceRepo invoiceRepo;

	@Mock
	private DashboardDetailDAO dashboardDetailDAO;

	@Mock
	private UserAssignmentRepo userAssignmentRepo;

	@Mock
	private ProjectAlertRepo projectAlertRepo;

	@Mock
	private ReviewDocumentRepo reviewDocumentRepo;

	@Mock
	private DocumentReviewRepo documentReviewRepo;

	@Mock
	private SupportDocumentDAO supportDocumentDao;

	@Mock
	private LitigationHoldRequestRepo litigationRequestRepo;

	@Mock
	private LitigationHoldRequestHistoryRepo litigationRequestHistoryRepo;

	@Mock
	private FoilRequestRepo foilRequestRepo;
	@Mock
	private MunicipalityRepo municipalityRepo;
	@Mock
	private CountyRepo countyRepo;
	@Mock
	private ProjectActivityRepo projectActivityRepo;
	@Mock
	private RestTemplate eTrackOtherServiceRestTemplate;
	@Mock
	private DartMilestoneRepo dartMilestoneRepo;
	@Mock
	private DartDBServiceUtility dartDBServiceUtility;
	@Mock
	private GIInquiryAlertRepo giInquiryAlertRepo;
	@Mock
	private DartInCompleteMilestoneRepo dartInCompleteMilestoneRepo;
	@Mock
	private DartSuspendedMilestoneRepo dartSuspendedMilestoneRepo;
	
	@InjectMocks
	DartDbService dartDbService = new DartDbServiceImpl();
	

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGetProjectInformation() {
	}

	@Test
	public void getFacilityBinsEmptyTest() {
		String userId = ""; String contextId = ""; Long projectId = 100L;
		List<FacilityBIN> existingFacilityBins = new ArrayList<>();
		when(facilityBinRepo.findByProjectId(projectId)).thenReturn(existingFacilityBins);
		List<BridgeIdNumber> bridgeNumbers =  dartDbService.getFacilityBins(userId, contextId, projectId);
		assertEquals(bridgeNumbers.size(), 0);
	}	
	
	
	@Test
	public void getFacilityBinsTest() {
		String userId = ""; String contextId = ""; Long projectId = 100L;
		List<FacilityBIN> existingFacilityBins = new ArrayList<>();
		FacilityBIN bin = new FacilityBIN();
		bin.setBin("BIN");
		bin.setEdbBin("EDBBIN");
		existingFacilityBins.add(bin);
		when(facilityBinRepo.findByProjectId(projectId)).thenReturn(existingFacilityBins);
		List<BridgeIdNumber> bridgeNumbers =  dartDbService.getFacilityBins(userId, contextId, projectId);
		assertEquals(bridgeNumbers.size(), 1);
		assertEquals("EDBBIN", bridgeNumbers.get(0).getEdbBin());
	}
	
	@Test
	public void getUnsubmittedAppsEmpty() {
		String userId = ""; String contextId = "";
		List<PendingApplication> unSubmittedApplicationsByTheUser = new ArrayList<>();
		when(pendingAppRepo.findAllUnSubmittedApplications(userId)).thenReturn(unSubmittedApplicationsByTheUser);
		Map<Long, ApplicantDto> lrpsMap = new HashMap<>();
		List<DashboardDetail> unsubmittedApps = new ArrayList<>();
		when(transformationService.prepareApplicationInfo(userId,
        contextId, unSubmittedApplicationsByTheUser, lrpsMap)).thenReturn(unsubmittedApps);
		List<DashboardDetail> unsubmittedAppsTest = dartDbService.getUnsubmittedApps(userId, contextId);
		assertEquals(unsubmittedAppsTest.size(), 0);
		
	}
	
	@Test
	public void getUnsubmittedAppsTest() {
		String userId = ""; String contextId = "";
		List<PendingApplication> unSubmittedApplicationsByTheUser = new ArrayList<>();
		PendingApplication pending = new PendingApplication();
		pending.setAnalystAssignedDate("12/12/2024");
		pending.setAnalystAssignedId("Z123");
		pending.setApplicantId(200L);
		pending.setCity("BERLIN");
		pending.setCountry("USA");
		pending.setCreateDate(new Date());
		pending.setDisplayName("Mock Pending");
		unSubmittedApplicationsByTheUser.add(pending);
		when(pendingAppRepo.findAllUnSubmittedApplications(userId)).thenReturn(unSubmittedApplicationsByTheUser);
		Map<Long, ApplicantDto> lrpsMap = new HashMap<>();
		ApplicantDto dto = new ApplicantDto();
		dto.setDisplayName("Junit Full Test");
		dto.setFirstName("Tesr");
		lrpsMap.put(203L,dto);
		List<DashboardDetail> unsubmittedApps = new ArrayList<>();
		DashboardDetail d=new DashboardDetail();
		d.setApplicant("New");
		d.setAppType("Test Applicant");
		unsubmittedApps.add(d);
		when(transformationService.prepareApplicationInfo(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(unsubmittedApps);
		List<DashboardDetail> unsubmittedAppsResult =dartDbService.getUnsubmittedApps(userId, contextId);
		assertEquals(unsubmittedAppsResult.size(), 1);
		assertEquals("New", unsubmittedAppsResult.get(0).getApplicant());
		
	}
	
	@Test
	public void getProjectInfoTest() {
		
	}
	
	@Test
	public void retrieveAllOnlineUserDartApplicationsEmpty() {
		String userId = ""; String contextId=""; 
		Map<Long, DartMilestone> dartMilestoneMapByBatchI = new HashMap<>();
		 List<DashboardDetail> applicationsFromDart = dartDbService.retrieveAllOnlineUserDartApplications(userId, contextId, dartMilestoneMapByBatchI);
		 assertEquals(applicationsFromDart.size(), 0);
	}
	
	@Test
	public void retrieveAllOnlineUserDartApplicationsTest() {
		String userId = ""; String contextId=""; 
		Map<Long, DartMilestone> dartMilestoneMapByBatchI = new HashMap<>();
		 List<DartApplication> tasksDueFromDart = new ArrayList<>();
		 List<DashboardDetail> transformedTasksDue = new ArrayList<>();
		 DashboardDetail d=new DashboardDetail();
			d.setApplicant("New");
			d.setAppType("Test Applicant");
			transformedTasksDue.add(d);
	     when(dashboardDetailDAO.retrieveDARTDueApps(userId, contextId)).thenReturn(tasksDueFromDart);
		  List<DashboardDetail> transformedApplicantResponsesDue = new ArrayList<>();
		 transformedApplicantResponsesDue.add(d);
		 List<DartApplication> applicantResponseDueFromDart = new ArrayList<>();
		 when(transformationService.transformDataIntoDashboardData(applicantResponseDueFromDart, null,
				 dartMilestoneMapByBatchI)).thenReturn(transformedApplicantResponsesDue);
		 List<DashboardDetail> transformedSuspendedApps = new ArrayList<>();
		 transformedSuspendedApps.add(d);
		 List<DartApplication> suspendedApps = new ArrayList<DartApplication>();
		 when( transformationService
        .transformDataIntoDashboardData(suspendedApps, null, dartMilestoneMapByBatchI)).thenReturn(transformedSuspendedApps);
		 List<DashboardDetail> applicationsFromDart = dartDbService.retrieveAllOnlineUserDartApplications(userId, contextId, dartMilestoneMapByBatchI);
		 assertEquals(applicationsFromDart.size(), 3);
	}
	
	@Test
	public void getProjectInformationTest() {
		Project project = new Project();
		Long projectId = 1l;
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setProjectId(projectId);
		List<String> xtraIds = new ArrayList<>();
		xtraIds.add("1,2,3");
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(projectRepo.findAllProgramXTRAIDSByProjectId(projectId)).thenReturn(xtraIds);
		when(projectRepo.findAllProgramProgramIdsByProjectId(projectId)).thenReturn(xtraIds);
		when(transformationService.transformProjectEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(projectInfo);
		ProjectInfo response = dartDbService.getProjectInformation("","", projectId);
		assertEquals(projectId, response.getProjectId());
	}
	
	@Test
	public void retrieveSupportDocumentSummaryTest() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(1l);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummaryTitleIdTwoTest() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(1);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySupportDocCategoryCode2Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(2);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySupportDocCategoryCode3Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(3);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySupportDocCategoryCode4Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(4);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySupportDocCategoryCode5Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(5);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySupportDocCategoryCodeNullTest() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();

		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		supportDocumentEntity.setDocumentSubTypeTitleId(2l);
		supportDocumentEntity.setSupportDocCategoryCode(null);
		uploadedDocuments.add(supportDocumentEntity);
		when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);

		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummaryBadExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbService.
				retrieveSupportDocumentSummary("", "", 1l));
		
	}
	

	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroTest() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroDocCode1Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 supportDocumentEntity.setSupportDocCategoryCode(1);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroDocCode2Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 supportDocumentEntity.setSupportDocCategoryCode(2);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroDocCode3Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 supportDocumentEntity.setSupportDocCategoryCode(3);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroDocCode4Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 supportDocumentEntity.setSupportDocCategoryCode(4);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSupportDocumentSummarySubTypeTitleIdZeroDocCode5Test() {
		List<SupportDocument> supportDocumentList = new ArrayList<>();
		SupportDocument supportDocument = new SupportDocument();
		supportDocument.setDocumentSubTypeTitleId(1l);
		supportDocumentList.add(supportDocument);
		List<ETrackPermit> eTrackPermits = new ArrayList<>();
		ETrackPermit eTrackPermit = new ETrackPermit();
		eTrackPermit.setPermitTypeCode("1");
		eTrackPermits.add(eTrackPermit);
		Project project = new Project();
		 
		List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity = new SupportDocumentEntity();
		 supportDocumentEntity.setDocumentSubTypeTitleId(0l);
		 supportDocumentEntity.setSupportDocCategoryCode(5);
		 uploadedDocuments.add(supportDocumentEntity);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1l)).thenReturn(uploadedDocuments);
		 
		when(permitRepo.findETrackPermits(1l)).thenReturn(eTrackPermits);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		when(supportDocumentDao.retrieveAllSupportDocumentsForTheProjectId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(supportDocumentList);
		Object obj = dartDbService.retrieveSupportDocumentSummary("", "", 1l);
		assertNotNull(obj);
	}

	@Test
	public void getProgramReviewerDashboardDetailsTest() {
		List<ReviewerDocumentDetail> reviewerDocumentDetails = new ArrayList<>();
		ReviewerDocumentDetail reviewerDocumentDetail = new ReviewerDocumentDetail();
		reviewerDocumentDetail.setProjectId(1l);
		reviewerDocumentDetails.add(reviewerDocumentDetail);
		when(dashboardDetailDAO.findAllReviewProjectDetailsByUserId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(reviewerDocumentDetails);

		List<Municipality> municipalities = new ArrayList<>();
		Municipality municipality = new Municipality();
		municipality.setProjectId(1l);
		municipalities.add(municipality);
		when(municipalityRepo.findMunicipalitiesForProjectIds(Mockito.any())).thenReturn(municipalities);


		List<County> counties =  new ArrayList<>();
		County county = new County();
		county.setProjectId(1l);
		counties.add(county);
		
		County county2 = new County();
		county2.setProjectId(1l);
		counties.add(county2);
		when(countyRepo.findCountiesForProjectIds(Mockito.any())).thenReturn(counties);
		Object obj = dartDbService.getProgramReviewerDashboardDetails("", "", 1);
		assertNotNull(obj);
	}
	
	@Test
	public void getProgramReviewerDashboardDetailsNullPorjectIdTest() {
		List<ReviewerDocumentDetail> reviewerDocumentDetails = new ArrayList<>();
		ReviewerDocumentDetail reviewerDocumentDetail = new ReviewerDocumentDetail();
		reviewerDocumentDetail.setProjectId(1l);
		reviewerDocumentDetails.add(reviewerDocumentDetail);
		when(dashboardDetailDAO.findAllReviewProjectDetailsByUserId(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(reviewerDocumentDetails);

		List<Municipality> municipalities = new ArrayList<>();
		Municipality municipality = new Municipality();
//		municipality.setProjectId(1l);
		municipalities.add(municipality);
		when(municipalityRepo.findMunicipalitiesForProjectIds(Mockito.any())).thenReturn(municipalities);


		List<County> counties =  new ArrayList<>();
		County county = new County();
		county.setProjectId(1l);
		counties.add(county);
		
		County county2 = new County();
		county2.setProjectId(1l);
		counties.add(county2);
		when(countyRepo.findCountiesForProjectIds(Mockito.any())).thenReturn(counties);
		Object obj = dartDbService.getProgramReviewerDashboardDetails("", "", 1);
		assertNotNull(obj);
	}
	
	
	@Test
	public void getUserDashboardDetailsTest() {
		 List<DashboardDetail> dashboardDetails = new ArrayList<>();
		 DashboardDetail unsubmittedProjects = new DashboardDetail();
		 dashboardDetails.add(unsubmittedProjects);
		 when(pendingAppRepo.findAllUnSubmittedApplications("")).thenReturn(null);
		 Object obj = dartDbService.getUserDashboardDetails("", "");
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveRequiredApplicantsToSignTest() {
		List<SignedApplicant> signedApplicants = new ArrayList<>();
		SignedApplicant signedApplicant = new SignedApplicant();
		signedApplicant.setPublicId("1,2");
		signedApplicant.setRole("Owner");
		signedApplicant.setLegallyResponsibleTypeCode(1);
		signedApplicants.add(signedApplicant);
		when(signedApplicantRepo.findOwnerAndApplicantDetails(1l)).thenReturn(signedApplicants);
		
		Object obj = dartDbService.retrieveRequiredApplicantsToSign("", "", 1l);
		assertNotNull(obj);

	}

	@Test
	public void retrieveProjectSummaryTest() {
		Project project = new Project();
		project.setAssignedAnalystName("name");
		when(projectRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
		
		 Map<String, Object> enterpriseDataDetails = new HashMap<>();
		 List<PublicAndFacilityDetail> publicAndFacilityDetails = new ArrayList<>();
		 PublicAndFacilityDetail publicAndFacilityDetail = new PublicAndFacilityDetail();
		 publicAndFacilityDetail.setOwnerRec(1);
		 publicAndFacilityDetails.add(publicAndFacilityDetail);	
	     Application application = new Application();
	     application.setProjectId(111L);
	     List<Application> applications = new ArrayList<>();
	     applications.add(application);
	     enterpriseDataDetails.put(DartDBConstants.APPLICATION_CURSOR, applications);
		 enterpriseDataDetails.put(DartDBConstants.PUBLIC_CURSOR, publicAndFacilityDetails);
		when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW(
		    Mockito.anyString(),Mockito.anyString(),Mockito.anyLong())).thenReturn(enterpriseDataDetails);
		 
		 HttpHeaders headers = new HttpHeaders();
	      headers.add("userId", "");
	      headers.add("contextId", "");
	      headers.add("projectId", String.valueOf(1l));
	      headers.add(HttpHeaders.AUTHORIZATION, "");
	      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<Void> response = new ResponseEntity(HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class),
		    Mockito.any(HttpEntity.class), Mockito.<Class<Void>>any())).thenReturn(response);
		
		List<RegionUserEntity> userDetails = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		userDetails.add(regionUserEntity);
		when(dartDBDAO.retrieveStaffDetailsByUserId(Mockito.any(), Mockito.any())).thenReturn(userDetails);
		
		 List<String> noteActionTypeDescList = new ArrayList<>();
		 noteActionTypeDescList.add("1,type");
		when(projectNoteRepo.findAllProjectNoteActionType()).thenReturn(noteActionTypeDescList);
		
		List<ProjectNote> notesList = new ArrayList<>();
		ProjectNote projectNote = new ProjectNote();
		projectNote.setActionTypeCode(DartDBConstants.REQUIRED_DOCUMENTS_NOT_RECEIVED);
		projectNote.setActionNote("1,2");
		projectNote.setActionDate(new Date());
		notesList.add(projectNote);
		when(projectNoteRepo.findAllByProjectId(1l)).thenReturn(notesList);
		

//		List<Application> applcationList = new ArrayList<>();
//		Application application = new Application();
//		applcationList.add(application);
//		when(applicationRepo.findAllUploadedApplnByProjectId(Mockito.anyLong())).thenReturn(applcationList);

		List<InvoiceEntity> invoices = new ArrayList<>();
		InvoiceEntity invoiceEntity = new InvoiceEntity();
		invoiceEntity.setCreateDate(new Date());
		invoiceEntity.setInvoiceFeeType1("1");
		invoiceEntity.setInvoiceFeeType2("1");
		invoiceEntity.setInvoiceFeeType3("1");
		invoiceEntity.setInvoiceFeeTypeFee1(1);
		invoiceEntity.setInvoiceFeeTypeFee2(2);
		invoiceEntity.setInvoiceFeeTypeFee3(3);
		invoiceEntity.setCheckRcvdDate(new Date());
		invoices.add(invoiceEntity);
		when(invoiceRepo.findAllByProjectIdOrderByCreateDateDesc(1l)).thenReturn(invoices);
		
		 List<String> invoiceStatuses = new ArrayList<>();
		 invoiceStatuses.add("test,test");
		 when(invoiceRepo.findAllInvoiceStatus()).thenReturn(invoiceStatuses);
		 
		 List<DartMilestone> dartMilestonesList = new ArrayList<>();
		 DartMilestone dartMilestone = new DartMilestone();
		 dartMilestone.setEdbCurrentStatusCode(CurrentDartStatus.SUSPEND_PERMIT.name());
		 dartMilestone.setUpdateDate(new Date());
		 dartMilestone.setProjectReceivedDate(new Date());
		 dartMilestone.setIncompleteSentDate(new Date());
		 dartMilestone.setCompleteSentDate(new Date());
		 dartMilestone.setFinalDispositionDate(new Date());
		 dartMilestone.setAdditionalInfoRequestDate(new Date());
		 dartMilestone.setAdditionalInfoRecvdDate(new Date());
		 dartMilestone.setSuspendedDate(new Date());
		 dartMilestone.setUnsuspendedDate(new Date());
		 dartMilestone.setAuthEffectiveDate(new Date());
		 dartMilestone.setAuthExpDate(new Date());
		 dartMilestone.setHearingDate(new Date());
		 dartMilestone.setEnbDate(new Date());
		 dartMilestone.setFiveDayLetterRecvdDate(new Date());
		 dartMilestone.setFiveDayLetterResponseDate(new Date());
		 dartMilestone.setResubmissionRecvdDate(new Date());
		 dartMilestone.setDeisCompleteDate(new Date());
		 dartMilestone.setFeisCompleteDate(new Date());
		 dartMilestone.setSeqrFindingsIssuedDate(new Date());
		 dartMilestone.setCommentsDeadlineDate(new Date());
		 dartMilestonesList.add(dartMilestone);
		 when(dartMilestoneRepo.findAllMilestoneByProjectIdOrderByBatchIdAsc(1l)).thenReturn(dartMilestonesList);
		 List<DartInCompleteMilestone> dartInCompleteMilestones = new ArrayList<>();
		 when(dartInCompleteMilestoneRepo.findAllInCompleteMilestoneByProjectIdOrderByBatchIdAsc(
		     Mockito.anyLong())).thenReturn(dartInCompleteMilestones);
	      List<DartSuspensionMilestone> dartInSuspensionMilestones = new ArrayList<>();
	         when(dartSuspendedMilestoneRepo.findAllSuspendedMilestoneByProjectIdOrderByBatchIdAsc(
	             Mockito.anyLong())).thenReturn(dartInSuspensionMilestones);
	         
		 List<SupportDocumentEntity> uploadedDocuments = new ArrayList<>();
		 SupportDocumentEntity supportDocumentEntity1 = new SupportDocumentEntity();
		 SupportDocumentEntity supportDocumentEntity2 = new SupportDocumentEntity();
		 supportDocumentEntity1.setFileCount(1);
		 supportDocumentEntity1.setDocumentId(1l);
		 supportDocumentEntity1.setCreateDate(new Date());
		 supportDocumentEntity2.setFileCount(1);
		 supportDocumentEntity2.setDocumentId(1l);
		 supportDocumentEntity2.setCreateDate(new Date());
		 uploadedDocuments.add(supportDocumentEntity1);
		 uploadedDocuments.add(supportDocumentEntity2);
		 when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectIdWithFilesCount(1l)).thenReturn(uploadedDocuments);
		 
		 List<DocumentReviewEntity> reviewDocumentsList = new ArrayList<>();
		 DocumentReviewEntity documentReviewEntity = new DocumentReviewEntity();
		 documentReviewEntity.setDocReviewerName("Test");
		 documentReviewEntity.setReviewAssignedDate(new Date());
		 documentReviewEntity.setReviewDueDate(new Date());
		 reviewDocumentsList.add(documentReviewEntity);
		 when(documentReviewRepo.findAllByProjectId(1l)).thenReturn(reviewDocumentsList);
		 
		 LitigationHold litigationHold = new LitigationHold();
		 litigationHold.setLitigationHoldEndDate(new Date());
		 litigationHold.setLitigationHoldStartDate(new Date());
		 when(litigationRequestRepo.findByProjectId(1l)).thenReturn(litigationHold);
		 
		 List<LitigationHoldHistory> litigationHoldHistoryList = new ArrayList<>();
		 LitigationHoldHistory litigationHoldHistory = new LitigationHoldHistory();
		 litigationHoldHistory.setLitigationHoldStartDate(new Date());
		 litigationHoldHistory.setLitigationHoldEndDate(new Date());
		 litigationHoldHistoryList.add(litigationHoldHistory);
		 when(litigationRequestHistoryRepo.findByProjectIdOrderByLitigationHoldHIdDesc(1l)).thenReturn(litigationHoldHistoryList);
		 
		Object obj = dartDbService.retrieveProjectSummary("", "", "", 1l);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveProjectSummaryNoDataFoundExceptionTest() {
		when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW("", "", 1l)).thenThrow(NoDataFoundException.class);
		assertThrows(NoDataFoundException.class, ()->dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	@Test
	public void retrieveProjectSummaryDartDBExceptionTest() {
		when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW("", "", 1l)).thenThrow(DartDBException.class);
		assertThrows(DartDBException.class, ()->dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	@Test
	public void retrieveProjectSummaryBadRequestExceptionTest() {
		assertThrows(BadRequestException.class, ()->dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	@Test
	public void retrieveProjectSummaryBadRequestTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<Void> response = new ResponseEntity(HttpStatus.BAD_REQUEST);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenReturn(response);

		assertNotNull(dartDbService.retrieveProjectSummary("", "", "", 1l));

	}
	
	@Test
	public void retrieveProjectSummaryHttpClientErrorExceptionTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		assertNotNull(dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	@Test
	public void retrieveProjectSummaryHttpServerErrorExceptionTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

		assertNotNull(dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	@Test
	public void retrieveProjectSummaryExceptionTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenThrow(new NullPointerException());

		assertThrows(DartDBException.class, ()->dartDbService.retrieveProjectSummary("", "", "", 1l));
	}
	
	
	@Test
	public void retrieveProjectSummaryDartDBExceptionOneTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		project.setAssignedAnalystName("test test");
		project.setAnalystAssignedDate(new Date());
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<Void> response = new ResponseEntity(HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenReturn(response);

		when(dartDBDAO.retrieveStaffDetailsByUserId("", "")).thenThrow(NoDataFoundException.class);

		Map<String, Object> enterpriseDataDetails = new HashMap<>();
		List<PublicAndFacilityDetail> publicAndFacilityDetails = new ArrayList<>();
		PublicAndFacilityDetail publicAndFacilityDetail = new PublicAndFacilityDetail();
		publicAndFacilityDetail.setOwnerRec(0);
		publicAndFacilityDetail.setDecId("test123");
		publicAndFacilityDetails.add(publicAndFacilityDetail);		 
		enterpriseDataDetails.put(DartDBConstants.PUBLIC_CURSOR, publicAndFacilityDetails);
		when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW("","",1l)).thenReturn(enterpriseDataDetails);


		assertThrows(DartDBException.class, ()-> dartDbService.retrieveProjectSummary("", "", "", 1l));
	} 
	
	@Test
	public void retrieveProjectSummaryRolrType80DartDBExceptionOneTest() {
		Project project = new  Project();
		project.setDimsrInd(1);
		project.setAssignedAnalystName("test test test");
		project.setAnalystAssignedDate(new Date());
		when(projectRepo.findById(1l)).thenReturn(Optional.of(project));

		HttpHeaders headers = new HttpHeaders();
		headers.add("userId", "");
		headers.add("contextId", "");
		headers.add("projectId", String.valueOf(1l));
		headers.add(HttpHeaders.AUTHORIZATION, "");
		HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<Void> response = new ResponseEntity(HttpStatus.OK);
		when(eTrackOtherServiceRestTemplate.exchange("//etrack-dart-district/refresh-project-milestone", HttpMethod.POST, requestEntity, Void.class)).thenReturn(response);

		when(dartDBDAO.retrieveStaffDetailsByUserId("", "")).thenThrow(NoDataFoundException.class);

		Map<String, Object> enterpriseDataDetails = new HashMap<>();
		List<PublicAndFacilityDetail> publicAndFacilityDetails = new ArrayList<>();
		PublicAndFacilityDetail publicAndFacilityDetail = new PublicAndFacilityDetail();
		publicAndFacilityDetail.setOwnerRec(0);
		publicAndFacilityDetail.setDecId("test123");
		publicAndFacilityDetail.setRoleTypeId(80);
		publicAndFacilityDetails.add(publicAndFacilityDetail);		 
		enterpriseDataDetails.put(DartDBConstants.PUBLIC_CURSOR, publicAndFacilityDetails);
		when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW("","",1l)).thenReturn(enterpriseDataDetails);


		assertThrows(DartDBException.class, ()-> dartDbService.retrieveProjectSummary("", "", "", 1l));
	} 
	
	@Test
	public void retrieveSupportDetailsForDIMSRTest() {
		Map<String, Object> supportDetailsOfDIMSR = new HashMap<>();
		List<FacilityLRPDetail> facilityLRPDetailsList = new ArrayList<>();
		FacilityLRPDetail fFacilityLRPDetail = new FacilityLRPDetail();
		facilityLRPDetailsList.add(fFacilityLRPDetail);
		
		List<DartPermit> existingApplicationsDetailsList = new ArrayList<>();
		DartPermit dartPermit = new DartPermit();
		dartPermit.setReceivedDate("test");
		dartPermit.setStartDate(new Date());
		dartPermit.setExpiryDate(new Date());
		existingApplicationsDetailsList.add(dartPermit);
		
		List<DartApplication> pendingApplicationsList = new ArrayList<>();
		DartApplication dartApplication = new DartApplication();
		dartApplication.setReceivedDate(new Date());
		dartApplication.setStartDate(new Date());
		dartApplication.setExpiryDate(new Date());
		pendingApplicationsList.add(dartApplication);
		supportDetailsOfDIMSR.put(DartDBConstants.FACILITY_CURSOR, facilityLRPDetailsList);
		supportDetailsOfDIMSR.put(DartDBConstants.EXISTING_APPS_CURSOR, existingApplicationsDetailsList);
		supportDetailsOfDIMSR.put(DartDBConstants.P_PENDING_APPS_CURSOR, pendingApplicationsList);

		when(dartDBDAO.retrieveSupportDetailsForDIMSR("", "", "")).thenReturn(supportDetailsOfDIMSR);
		Object obj = dartDbService.retrieveSupportDetailsForDIMSR("", "", "");
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveActiveAuthorizationPermitsTest() {
		dec.ny.gov.etrack.dart.db.entity.Facility facilityEntity = new Facility();
		facilityEntity.setEdbDistrictId(1l);
		when(facilityRepo.findByProjectId(1l)).thenReturn(facilityEntity);

		List<DartPermit> existingApplicationsDetailsList = new ArrayList<>();
		DartPermit dartPermit = new DartPermit();
		dartPermit.setReceivedDate("test");
		dartPermit.setStartDate(new Date());
		dartPermit.setExpiryDate(new Date());
		existingApplicationsDetailsList.add(dartPermit);
		Map<String, Object> supportDetailsOfDIMSR = new HashMap<>();
		supportDetailsOfDIMSR.put(DartDBConstants.EXISTING_APPS_CURSOR, existingApplicationsDetailsList);

		List<String> res = dartDbService.retrieveActiveAuthorizationPermits("","", 1l,1l);
		assertNotNull(res);
	}
	
	@Test
	public void retrieveActiveAuthorizationPermitsBadExceptioTest() {		
		assertThrows(BadRequestException.class, ()->dartDbService.retrieveActiveAuthorizationPermits("","", -1l,1l));
		
	}
	
	@Test
	public void retrieveActiveAuthorizationPermitsBadException2Test() {
		dec.ny.gov.etrack.dart.db.entity.Facility facilityEntity = null;
		Mockito.lenient().when(facilityRepo.findByProjectId(123L)).thenReturn(facilityEntity);
		assertThrows(BadRequestException.class, ()->dartDbService.retrieveActiveAuthorizationPermits("dxdev","context", 123l,null));
		
	}
	
	@Test
	public void  retrieveProjectRejectionDetailsTest() {
		List<String> rejectionDetails = new ArrayList<>();
		rejectionDetails.add("test,test,test");
		when(projectRepo.retrieveProjectRejectionDetails(1l)).thenReturn(rejectionDetails);
		ProjectRejectDetail res = dartDbService.retrieveProjectRejectionDetails("","", 1l);
		assertNotNull(res);
	}
	
	@Test
	public void getUsersWithValidEmailAddressTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersWithValidEmailAddress("1234", "")).thenReturn(regionUsersList);
		
		List<RegionUserEntity> regionUserEntities = dartDbService.getUsersWithValidEmailAddress("1234", "");
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersWithValidEmailAddressWithDisplayNameTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersWithValidEmailAddress("1234", "")).thenReturn(regionUsersList);
		
		List<RegionUserEntity> regionUserEntities = dartDbService.getUsersWithValidEmailAddress("1234", "");
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersWithValidEmailAddressWithSpaceInDisplayNameTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersWithValidEmailAddress("1234", "")).thenReturn(regionUsersList);
		
		List<RegionUserEntity> regionUserEntities = dartDbService.getUsersWithValidEmailAddress("1234", "");
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersWithValidEmailAddressWithTwoSpaceInDisplayNameTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name name name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersWithValidEmailAddress("1234", "")).thenReturn(regionUsersList);
		
		List<RegionUserEntity> regionUserEntities = dartDbService.getUsersWithValidEmailAddress("1234", "");
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersWithValidEmailAddressEmptyTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		when(dartDBDAO.findAllTheUsersWithValidEmailAddress("1234", "")).thenReturn(regionUsersList);
		
		List<RegionUserEntity> regionUserEntities = dartDbService.getUsersWithValidEmailAddress("1234", "");
		assertNull(regionUserEntities);
	}
	
	@Test
	public void getUsersByRegionAndRoleTypeIdTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersByRoleTypeId("1234", "",1,1)).thenReturn(regionUsersList);
		
		Object regionUserEntities = dartDbService.getUsersByRegionAndRoleTypeId("1234", "",1,1);
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersByRegionAndRoleTypeIdWithDisplayNameTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersByRoleTypeId("1234", "",1,1)).thenReturn(regionUsersList);
		
		Object regionUserEntities = dartDbService.getUsersByRegionAndRoleTypeId("1234", "",1,1);
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersByRegionAndRoleTypeIdWithDisplayNameWithSpaceTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersByRoleTypeId("1234", "",1,1)).thenReturn(regionUsersList);
		
		Object regionUserEntities = dartDbService.getUsersByRegionAndRoleTypeId("1234", "",1,1);
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersByRegionAndRoleTypeIdWithDisplayNameWithTwoSpaceTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		RegionUserEntity regionUserEntity = new RegionUserEntity();
		regionUserEntity.setUserId("1234");
		regionUserEntity.setDisplayName("name name name");
		regionUsersList.add(regionUserEntity);
		when(dartDBDAO.findAllTheUsersByRoleTypeId("1234", "",1,1)).thenReturn(regionUsersList);
		
		Object regionUserEntities = dartDbService.getUsersByRegionAndRoleTypeId("1234", "",1,1);
		assertNotNull(regionUserEntities);
	}
	
	@Test
	public void getUsersByRegionAndRoleTypeIdEmptyTest() {
		List<RegionUserEntity> regionUsersList = new ArrayList<>();
		when(dartDBDAO.findAllTheUsersByRoleTypeId("1234", "",1,1)).thenReturn(regionUsersList);
		
		Object regionUserEntities = dartDbService.getUsersByRegionAndRoleTypeId("1234", "",1,1);
		assertNull(regionUserEntities);
	}
	
	@Test
	public void viewAnalystDashboardAlertsTest() {
		List<ProjectAlert> alertsList = new ArrayList<>();
		ProjectAlert projectAlert = new ProjectAlert();
		projectAlert.setComments("comments");
		alertsList.add(projectAlert);
		when(projectAlertRepo.findAllAlertsByUserId("")).thenReturn(alertsList);
		
		 List<GIInquiryAlert> inquiryAlerts = new ArrayList<>();
		 GIInquiryAlert giInquiryAlert = new GIInquiryAlert();
		 giInquiryAlert.setComments("comments");
		 inquiryAlerts.add(giInquiryAlert);
		 when(giInquiryAlertRepo.findAllAlertsByUserId("")).thenReturn(inquiryAlerts);
		 
		
		List<Alert> alerts = dartDbService.viewAnalystDashboardAlerts("", "");
		assertNotNull(alerts);
	}
	
	@Test
	public void viewAnalystDashboardAlertsEmptyCommentsTest() {
		List<ProjectAlert> alertsList = new ArrayList<>();
		ProjectAlert projectAlert = new ProjectAlert();
		alertsList.add(projectAlert);
		when(projectAlertRepo.findAllAlertsByUserId("")).thenReturn(alertsList);
		
		 List<GIInquiryAlert> inquiryAlerts = new ArrayList<>();
		 GIInquiryAlert giInquiryAlert = new GIInquiryAlert();
		 inquiryAlerts.add(giInquiryAlert);
		 when(giInquiryAlertRepo.findAllAlertsByUserId("")).thenReturn(inquiryAlerts);
		 
		
		List<Alert> alerts = dartDbService.viewAnalystDashboardAlerts("", "");
		assertNotNull(alerts);
	}
	
	@Test
	public void viewAnalystDashboardAlertsEmptyDataTest() {
		List<ProjectAlert> alertsList = new ArrayList<>();
		when(projectAlertRepo.findAllAlertsByUserId("")).thenReturn(alertsList);
		
		 List<GIInquiryAlert> inquiryAlerts = new ArrayList<>();
		 when(giInquiryAlertRepo.findAllAlertsByUserId("")).thenReturn(inquiryAlerts);
		 
		
		List<Alert> alerts = dartDbService.viewAnalystDashboardAlerts("", "");
		assertNotNull(alerts);
	}
	
	@Test
	public void retrieveAnalystsAlertsTest() {
		List<ProjectAlert> alertsList = new ArrayList<>();
		ProjectAlert projectAlert = new ProjectAlert();
		alertsList.add(projectAlert);
		when(projectAlertRepo.findAllAlertsByUserId("")).thenReturn(alertsList);
		
		List<GIInquiryAlert> inquiryAlerts = new ArrayList<GIInquiryAlert>();
		GIInquiryAlert alert = new GIInquiryAlert();
		inquiryAlerts.add(alert);
		when(giInquiryAlertRepo.findAllAlertsByUserId("")).thenReturn(inquiryAlerts);
		assertNotNull(dartDbService.retrieveAnalystsAlerts("", ""));
	}
	
	@Test
	public void retrieveAnalystsAlertsEmptyDateTest() {
		List<ProjectAlert> alertsList = new ArrayList<>();
		when(projectAlertRepo.findAllAlertsByUserId("")).thenReturn(alertsList);
		
		List<GIInquiryAlert> inquiryAlerts = new ArrayList<GIInquiryAlert>();
		when(giInquiryAlertRepo.findAllAlertsByUserId("")).thenReturn(inquiryAlerts);
		assertNotNull(dartDbService.retrieveAnalystsAlerts("", ""));
	}
	
//	@Test
//	public void getPrepareProjectMilestoneDetailsTest() {
//		 DartMilestone dartMilestone = new DartMilestone();
//		 List<DartMilestone> dartMilestonesList = new ArrayList<>();
//		 dartMilestone.setBatchId(202L);
//		 dartMilestone.setEdbCurrentStatusDesc("TEST DESC");
//		 dartMilestone.setUpdateDate(new Date());
//		 dartMilestone.setProjectReceivedDate(new Date());
//		 dartMilestone.setIncompleteSentDate(new Date());
//		 dartMilestone.setCompleteSentDate(new Date());
//		 dartMilestone.setFinalDispositionDate(new Date());
//		 dartMilestone.setAdditionalInfoRequestDate(new Date());
//		 dartMilestone.setAdditionalInfoRecvdDate(new Date());
//		 dartMilestone.setSuspendedDate(new Date());
//		 dartMilestone.setUnsuspendedDate(new Date());
//		 dartMilestone.setAuthEffectiveDate(new Date());
//		 dartMilestone.setAuthExpDate(new Date());
//		 dartMilestone.setHearingDate(new Date());
//		 dartMilestone.setEnbDate(new Date());
//		 dartMilestone.setFiveDayLetterRecvdDate(new Date());
//		 dartMilestone.setFiveDayLetterResponseDate(new Date());
//		 dartMilestone.setResubmissionRecvdDate(new Date());
//		 dartMilestone.setDeisCompleteDate(new Date());
//		 dartMilestone.setFeisCompleteDate(new Date());
//		 dartMilestone.setSeqrFindingsIssuedDate(new Date());
//		 dartMilestone.setCommentsDeadlineDate(new Date());
//		 dartMilestone.setEdbGpInd("Test edbind");
//		 dartMilestonesList.add(dartMilestone);
//		 Long projectId = 202L;
//		 Mockito.lenient().when(dartMilestoneRepo.findAllMilestoneByProjectIdOrderByBatchIdAsc(projectId)).thenReturn(dartMilestonesList);
//		 Project projetc = new Project();
//		 Optional<Project> proO=Optional.of(projetc);
//		 Mockito.lenient().when(projectRepo.findById(10126L)).thenReturn(proO);
//		 ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.OK);
//		 HttpHeaders headers = new HttpHeaders();
//	      headers.add("userId", "");
//	      headers.add("contextId", "");
//	      headers.add("projectId", String.valueOf(projectId));
//	      headers.add(HttpHeaders.AUTHORIZATION, "");
//	      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
//	      String uri = UriComponentsBuilder.newInstance()
//	          .pathSegment("/etrack-dart-district/refresh-project-milestone").build().toString();
//		 Mockito.when(eTrackOtherServiceRestTemplate.exchange(uri,
//          HttpMethod.POST, requestEntity, Void.class)).thenReturn(response);
//		 Object obj = dartDbService.retrieveProjectSummary("", "", "ejhyaagf", 10126L);
//		 assertNotNull(obj);
//		 
//	}
}
