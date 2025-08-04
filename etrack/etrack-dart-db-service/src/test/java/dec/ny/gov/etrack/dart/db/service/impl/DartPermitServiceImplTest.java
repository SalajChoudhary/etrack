package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.dao.DashboardDetailDAO;
import dec.ny.gov.etrack.dart.db.entity.Application;
import dec.ny.gov.etrack.dart.db.entity.ApplicationNarrativeDetail;
import dec.ny.gov.etrack.dart.db.entity.ApplicationPermitForm;
import dec.ny.gov.etrack.dart.db.entity.ContactAgent;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.entity.PermitType;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.AvailTransType;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.ContactRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitRepo;
import dec.ny.gov.etrack.dart.db.repo.PermitTypeRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.DartPermitService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartPermitServiceImplTest {
	
	  @Mock
	  private DartDbDAO dartDBDAO;
	  @Mock
	  private ProjectRepo projectRepo;
	  @Mock
	  private PermitTypeRepo permitTypeRepo;
	  @Mock
	  private ContactRepo contactRepo;
	  @Mock
	  private ProjectActivityRepo projectActivityRepo;
	  @Mock
	  private PermitRepo permitRepo;
	  @Mock
	  private FacilityRepo facilityRepo;
	  @Mock
	  private ApplicationRepo applicationRepo;
	  @Mock
	  private DashboardDetailDAO dashboardDetailDAO;
	  @Mock
	  private TransformationService transformationService;
	  @Mock
	  private RestTemplate eTrackOtherServiceRestTemplate;
	  
	  private static final String VALIDATED_IND = "validateInd";
	  private static final String EMERGENCY_AUTH_IND = "emergencyInd";
	  private static final String CONSTRUCTION_TYPE = "constrnType";
	  private static final String EXTEND_TRANS_TYPE = "Extend";
	  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	  @InjectMocks
	  DartPermitService dartPermitService = new DartPermitServiceImpl();
	  Long projectId = 100L;
	  String userId = "";
	  String contextId = "";
	  @Test
	  public void retrievePermitsAssignmentTest() {
		  Long projectId = 100L;
		  String userId = "";
		  String contextId = "";
		  Optional<Project> pro = Optional.of(new Project());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  ApplicationPermitForm apf = new ApplicationPermitForm();
		  apf.setPermitFormName("JAFT");
		  apf.setPermitFormDesc("Joint Application Form");
		  List<ApplicationPermitForm> applicationPermitFormList = new ArrayList();
		  applicationPermitFormList.add(apf);
		  when(dartDBDAO.retrievePermitApplicationForm(userId, contextId, projectId)).thenReturn(applicationPermitFormList);
		  ContactAgent cAgent = new ContactAgent();
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		  List<Integer> activityList = new ArrayList();
		  activityList.add(23);
		  when(projectActivityRepo.findProjectActivityStatusId(projectId,
			        DartDBConstants.ASSIGN_CONTACT_VAL)).thenReturn(activityList);
		  Map<String, Object> retriveObj =  dartPermitService.retrievePermitsAssignment(userId, contextId,projectId);
		  assertNotNull(retriveObj);
	  }
	  
	  @Test
	  public void retrievePermitsAssignmentTwoTest() {
		  Long projectId = 100L;
		  String userId = "";
		  String contextId = "";
		  Optional<Project> pro = Optional.of(new Project());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  ApplicationPermitForm apf = new ApplicationPermitForm();
		  apf.setPermitFormName("JAFT");
		  apf.setPermitFormDesc("Application Form");
		  apf.setContactAssignedId(projectId);
		  List<ApplicationPermitForm> applicationPermitFormList = new ArrayList();
		  applicationPermitFormList.add(apf);
		  apf.setPermitFormName("JAF");
		  applicationPermitFormList.add(apf);
		  when(dartDBDAO.retrievePermitApplicationForm(userId, contextId, projectId)).thenReturn(applicationPermitFormList);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(projectId);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		  List<Integer> activityList = new ArrayList();
		  activityList.add(23);
		  when(projectActivityRepo.findProjectActivityStatusId(projectId,
			        DartDBConstants.ASSIGN_CONTACT_VAL)).thenReturn(activityList);
		  Map<String, Object> retriveObj =  dartPermitService.retrievePermitsAssignment(userId, contextId,projectId);
		  assertNotNull(retriveObj);
	  }
	  
	  @Test
	  public void retrievePermitsAssignmentoneTest() {
		  
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  ApplicationPermitForm apf = new ApplicationPermitForm();
		  apf.setPermitFormName("EAFT");
		  apf.setPermitFormDesc("d Form");
		  List<ApplicationPermitForm> applicationPermitFormList = new ArrayList();
		  applicationPermitFormList.add(apf);
		  when(dartDBDAO.retrievePermitApplicationForm(userId, contextId, projectId)).thenReturn(applicationPermitFormList);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList<>();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		  List<Integer> activityList = new ArrayList();
		  when(projectActivityRepo.findProjectActivityStatusId(projectId,
			        DartDBConstants.ASSIGN_CONTACT_VAL)).thenReturn(activityList);
		  Map<String, Object> retriveObj =  dartPermitService.retrievePermitsAssignment(userId, contextId,projectId);
		  assertNotNull(retriveObj);
	  }
	  
	  @Test
	  public void retrieveAllPermitApplicationsTest() {
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setProgId("1");
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);		  
		  Map<String, Object> existingApplicationsData = new HashMap<>();
		  List<DartPermit> activeAuthorizations = new ArrayList<>();
		  DartPermit dartPermit = new DartPermit();
		  dartPermit.setApplId(1l);
		  activeAuthorizations.add(dartPermit);
		  existingApplicationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR,activeAuthorizations);
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise("","",1l)).thenReturn(existingApplicationsData);		 
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		  Map<String, Object> obj = dartPermitService.retrieveAllPermitApplications(userId,
			      contextId, projectId, true);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryExistingPermitTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Map<String, Object>  dartModifyExtendEligiblePermits = new HashMap<>();
		  Map<String, Object> existingAuthorizationsData = new HashMap<>();
		  DartPermit dp = new DartPermit();
		  dp.setTrackedId("Test");
		  dp.setTransType("REI");
		  List<DartPermit> dartModifyExtendAvailablePermits = new ArrayList();
		  dartModifyExtendAvailablePermits.add(dp);
		  existingAuthorizationsData.put(DartDBConstants.EXISTING_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  existingAuthorizationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  
		  Mockito.lenient().when(dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, 1L)).thenReturn(existingAuthorizationsData);
		  Map<String, Object> expiredPermitsData = new HashMap<>();
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, 1L)).thenReturn(expiredPermitsData);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		 
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            Mockito.lenient().when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryRenewablePermitTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Map<String, Object>  dartModifyExtendEligiblePermits = new HashMap<>();
		  Map<String, Object> existingAuthorizationsData = new HashMap<>();
		  DartPermit dp = new DartPermit();
		  dp.setTrackedId("Test");
		  dp.setRenewedInd("1");
		  dp.setGpAuthId(200L);
		  List<DartPermit> dartModifyExtendAvailablePermits = new ArrayList();
		  dartModifyExtendAvailablePermits.add(dp);
		  existingAuthorizationsData.put(DartDBConstants.EXISTING_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  existingAuthorizationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  
		  Mockito.lenient().when(dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, 1L)).thenReturn(existingAuthorizationsData);
		  Map<String, Object> expiredPermitsData = new HashMap<>();
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, 1L)).thenReturn(expiredPermitsData);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		 
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            Mockito.lenient().when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryNonRenewablePermitTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Map<String, Object>  dartModifyExtendEligiblePermits = new HashMap<>();
		  Map<String, Object> existingAuthorizationsData = new HashMap<>();
		  DartPermit dp = new DartPermit();
		  dp.setTrackedId("Test");
		  dp.setRenewedInd("1");
		  List<DartPermit> dartModifyExtendAvailablePermits = new ArrayList();
		  dartModifyExtendAvailablePermits.add(dp);
		  existingAuthorizationsData.put(DartDBConstants.EXISTING_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  existingAuthorizationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  
		  Mockito.lenient().when(dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, 1L)).thenReturn(existingAuthorizationsData);
		  Map<String, Object> expiredPermitsData = new HashMap<>();
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, 1L)).thenReturn(expiredPermitsData);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		 
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            Mockito.lenient().when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryGeneralNonRenewablePermitTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Map<String, Object>  dartModifyExtendEligiblePermits = new HashMap<>();
		  Map<String, Object> existingAuthorizationsData = new HashMap<>();
		  DartPermit dp = new DartPermit();
		  dp.setTrackedId("Test");
		  dp.setGpAuthId(200L);
		  List<DartPermit> dartModifyExtendAvailablePermits = new ArrayList();
		  dartModifyExtendAvailablePermits.add(dp);
		  existingAuthorizationsData.put(DartDBConstants.EXISTING_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  existingAuthorizationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  
		  Mockito.lenient().when(dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, 1L)).thenReturn(existingAuthorizationsData);
		  Map<String, Object> expiredPermitsData = new HashMap<>();
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, 1L)).thenReturn(expiredPermitsData);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		 
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            Mockito.lenient().when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAllPermitSummaryRegularNonRenewablePermitTest() {
		  Optional<Project> pro = Optional.of(new Project());
		  pro.get().setEaInd(1);
		  pro.get().setReceivedDate(new Date());
		  when(projectRepo.findById(projectId)).thenReturn(pro);
		  List<ETrackPermit> eTrackPermits = new ArrayList();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbApplId(1L);
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  when(permitRepo.findETrackPermits(projectId)).thenReturn(eTrackPermits);
		  Map<String, Object>  dartModifyExtendEligiblePermits = new HashMap<>();
		  Map<String, Object> existingAuthorizationsData = new HashMap<>();
		  DartPermit dp = new DartPermit();
		  dp.setTrackedId("Test");
		  List<DartPermit> dartModifyExtendAvailablePermits = new ArrayList();
		  dartModifyExtendAvailablePermits.add(dp);
		  existingAuthorizationsData.put(DartDBConstants.EXISTING_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  existingAuthorizationsData.put(DartDBConstants.EXPIRED_APPS_CURSOR, dartModifyExtendAvailablePermits);
		  
		  Mockito.lenient().when(dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, 1L)).thenReturn(existingAuthorizationsData);
		  Map<String, Object> expiredPermitsData = new HashMap<>();
		  when(dartDBDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, 1L)).thenReturn(expiredPermitsData);
		  ContactAgent cAgent = new ContactAgent();
		  cAgent.setRoleId(1L);
		  List<ContactAgent> contactAgents = new ArrayList();
		  contactAgents.add(cAgent);
		  when(contactRepo.findAllContactsByAssociatedInd(projectId, 1)).thenReturn(contactAgents);
		 
		  HttpHeaders headers = new HttpHeaders();
	        headers.add("userId", userId);
	        headers.add("contextId", contextId);
	        headers.add(HttpHeaders.AUTHORIZATION, "");
	        Long edbDistrictId = 1L;
	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
	        String uri = UriComponentsBuilder.newInstance()
	            .pathSegment("/etrack-dart-district/retrieveNarrativeDesc/" + edbDistrictId).build()
	            .toString();
	        ParameterizedTypeReference<List<ApplicationNarrativeDetail>> applicationNarrativeTypeRef =
	            new ParameterizedTypeReference<List<ApplicationNarrativeDetail>>() {};
	            List<ApplicationNarrativeDetail> aap = new ArrayList();
	            ApplicationNarrativeDetail aape = new ApplicationNarrativeDetail();
	            ResponseEntity<List<ApplicationNarrativeDetail>> responsee = new  ResponseEntity(HttpStatus.OK);
	            Mockito.lenient().when(eTrackOtherServiceRestTemplate
	            .exchange(uri, HttpMethod.GET, requestEntity, applicationNarrativeTypeRef)).thenReturn(responsee);
		  
		  Object obj = dartPermitService.retrieveAllPermitSummary(userId, contextId,
			      "", projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrievePermitModificationSummaryEmptytest() {
		  List<PermitApplication> permitApplicationResult = new ArrayList<>();
		  PermitApplication permitApplication = new PermitApplication();
		  permitApplicationResult.add(permitApplication);
		  List<ETrackPermit> eTrackPermits = new ArrayList<>();
		  when(permitRepo.findETrackModifiedPermits(projectId)).thenReturn(eTrackPermits);
		  List<PermitApplication>  permitApplicationRes = dartPermitService.retrievePermitModificationSummary(userId, contextId, projectId);
		  assertNotNull( permitApplicationRes);
	  }
	  
	  
	  @Test
	  public void retrievePermitModificationSummarytest() {
		  List<PermitApplication> permitApplicationResult = new ArrayList<>();
		  PermitApplication permitApplication = new PermitApplication();
		  permitApplicationResult.add(permitApplication);
		  List<ETrackPermit> eTrackPermits = new ArrayList<>();
		  ETrackPermit eTrackPermit = new ETrackPermit();
		  eTrackPermit.setEdbDistrictId(1L);
		  eTrackPermits.add(eTrackPermit);
		  ETrackPermit eTrackPermit1 = new ETrackPermit();
		  eTrackPermit1.setEdbDistrictId(1L);
		  eTrackPermit1.setChgOriginalProjectInd(1);
		  eTrackPermits.add(eTrackPermit1);
		  when(permitRepo.findETrackModifiedPermits(projectId)).thenReturn(eTrackPermits);
		  List<PermitApplication>  permitApplicationRes = dartPermitService.retrievePermitModificationSummary(userId, contextId, projectId);
		  assertNotNull( permitApplicationRes);
	  }
	  
	  @Test
	  public void retrieveAvailablePermitsAddAsAdditionalEmptyTest() {
		  List<PermitType> availablePermitTypesToApply = new ArrayList<>();

//		  PermitType permitType = new PermitType();
//		  availablePermitTypesToApply.add(permitType);
		  Map<String, Object> result = new HashMap<>();
		  Application application = new Application();
		  application.setProjectId(111L);
		  List<Application> applications = new ArrayList<>();
		  applications.add(application);
		  result.put(DartDBConstants.APPLICATION_CURSOR, applications);
		  when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW(
		      Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(result);
		  when(permitTypeRepo.findEligiblePermitTypesToAddAdditional(Mockito.anySet())).thenReturn(availablePermitTypesToApply);

		  Facility facility = new Facility();
		  when(facilityRepo.findByProjectId(projectId)).thenReturn(facility);
		  Object obj = dartPermitService.retrieveAvailablePermitsAddAsAdditional(userId, contextId, projectId);
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveAvailablePermitsAddAsAdditionalTest() {
		  Map<String, Object> detailsFromEnterprise = new HashMap<>();
		  List<Application> applicationList = new ArrayList<>();
		  Application application = new Application();
		  applicationList.add(application);
		  detailsFromEnterprise.put(DartDBConstants.APPLICATION_CURSOR, applicationList);
		  when( dartDBDAO.retrieveEnterpriseSupportDetailsForVW(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(detailsFromEnterprise);
		  List<PermitType> availablePermitTypesToApply = new ArrayList<>();
		  PermitType permitType = new PermitType();
		  availablePermitTypesToApply.add(permitType);

          Map<String, Object> result = new HashMap<>();
          Application application1 = new Application();
          application1.setProjectId(111L);
          List<Application> applications = new ArrayList<>();
          applications.add(application1);
          result.put(DartDBConstants.APPLICATION_CURSOR, applications);
          when(dartDBDAO.retrieveEnterpriseSupportDetailsForVW(
              Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(result);
		  when(permitTypeRepo.findEligiblePermitTypesToAddAdditional(Mockito.anySet())).thenReturn(availablePermitTypesToApply);

		  Facility facility = new Facility();
		  when(facilityRepo.findByProjectId(projectId)).thenReturn(facility);
		  Map<String, Object> existingApplicationsData = new HashMap<>();
		  List<DartPermit> activeAuthorizations = new ArrayList<>();
		  DartPermit dartPermit = new DartPermit();
		  dartPermit.setApplId(1l);
		  activeAuthorizations.add(dartPermit);
		  existingApplicationsData.put(DartDBConstants.EXISTING_APPS_CURSOR,activeAuthorizations);
		  when( dartDBDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId,
            null)).thenReturn(existingApplicationsData);
		 List<Long> edbApplicationIds =new ArrayList<>();
		 edbApplicationIds.add(11l);
		 when(applicationRepo.findAllEnterpriseApplicationsAppliedInETrack(projectId)).thenReturn(edbApplicationIds);
		  List<String> batchIdAndTransTypeList = new ArrayList<>();
		  batchIdAndTransTypeList.add("test,test");
		  when(applicationRepo.findBatchIdAndTransType(projectId)).thenReturn(batchIdAndTransTypeList);
		  Object obj = dartPermitService.retrieveAvailablePermitsAddAsAdditional(userId, contextId, projectId);
		  assertNotNull(obj);
	  }

}
