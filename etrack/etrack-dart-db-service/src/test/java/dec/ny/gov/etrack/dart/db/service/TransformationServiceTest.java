package dec.ny.gov.etrack.dart.db.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.Application;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.ContactAgent;
import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.entity.DartPermit;
import dec.ny.gov.etrack.dart.db.entity.ETrackPermit;
import dec.ny.gov.etrack.dart.db.entity.FacilityBIN;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.GIInquiryAlert;
import dec.ny.gov.etrack.dart.db.entity.OutForReviewEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectAlert;
import dec.ny.gov.etrack.dart.db.entity.ProjectDevelopment;
import dec.ny.gov.etrack.dart.db.entity.ProjectResidential;
import dec.ny.gov.etrack.dart.db.entity.ProjectSICNAICSCode;
import dec.ny.gov.etrack.dart.db.entity.ProjectSWFacilityType;
import dec.ny.gov.etrack.dart.db.entity.PublicDetail;
import dec.ny.gov.etrack.dart.db.entity.history.AddressHistory;
import dec.ny.gov.etrack.dart.db.entity.history.PublicHistoryDetail;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Alert;
import dec.ny.gov.etrack.dart.db.model.Applicant;
import dec.ny.gov.etrack.dart.db.model.AvailTransType;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.model.ProjectInfo;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class TransformationServiceTest {

	  @Mock
	  private DartDBServiceUtility dartDBServiceUtility;
	  
	  @Mock
	  private ApplicationRepo applicationRepo;
	  
	  @InjectMocks
	  TransformationService transactionServie = new TransformationService();
	  
	  private static final String PROJECT_ID = "projectId";
	  private static final String APPLICANTS = "applicants";
	  private static final String OWNER = "1";
	  
	  @Test
	  public void getOwnerSummaryTest() {
		  List<PublicDetail> publicDetails =  new ArrayList<>();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  publicDetails.add(pbd);
		  Map<String, Object> resultSet = new HashMap<>();
		  resultSet.put(DartDBConstants.PUBLICS_CURSOR, publicDetails);
		  Map<String, Object> map = transactionServie.getOwnerSummary("", "",
			      resultSet, 1L, true);
	  }
	  
	  @Test
	  public void getOwnerSummaryEmptyTest() {
		  List<PublicDetail> publicDetails =  new ArrayList<>();
		  PublicDetail pbd = new PublicDetail();
		  publicDetails.add(pbd);
		  Map<String, Object> resultSet = new HashMap<>();
		  Map<String, Object> map = transactionServie.getOwnerSummary("", "",
			      resultSet, 1L, true);
	  }
	  
	  @Test
	  public void getOwnerSummaryElseTest() {
		  List<PublicDetail> publicDetails =  new ArrayList<>();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  publicDetails.add(pbd);
		  Map<String, Object> resultSet = new HashMap<>();
		  resultSet.put(DartDBConstants.PUBLICS_CURSOR, publicDetails);
		  Map<String, Object> map = transactionServie.getOwnerSummary("", "",
			      resultSet, 1L, false);
	  }
	
	  @Test
	  public void getOwnerSummaryElseEmptyTest() {
		  List<PublicDetail> publicDetails =  new ArrayList<>();
		  PublicDetail pbd = new PublicDetail();
		  publicDetails.add(pbd);
		  Map<String, Object> resultSet = new HashMap<>();
		  resultSet.put(DartDBConstants.PUBLICS_CURSOR, publicDetails);
		  Map<String, Object> map = transactionServie.getOwnerSummary("", "",
			      resultSet, 1L, false);
	  }
	  
//	  @Test
//	  public void getOwnerSummaryElseEIndTest() {
//		  List<PublicDetail> publicDetails =  new ArrayList<>();
//		  PublicDetail pbd = new PublicDetail();
//		  pbd.setLegallyResponsibleTypeCode(OWNER);
//		  pbd.setSelectedInEtrackInd(1);
//		  publicDetails.add(pbd);
//		  Map<String, Object> resultSet = new HashMap<>();
//		  //resultSet.put(DartDBConstants.PUBLICS_CURSOR, publicDetails);
//		  Map<String, Object> map = transactionServie.getOwnerSummary("", "",
//			      resultSet, 1L, false);
//	  }

	  
	  @Test
	  public void transformPublicEntityToApplicant() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "I", publicDetails, 1L);
	  }
	  
	  @Test
	  public void transformPublicEntityToApplicantEmptyTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
//		  PublicDetail pbd = new PublicDetail();
//		  pbd.setLegallyResponsibleTypeCode(OWNER);
//		  pbd.setSelectedInEtrackInd(1);
//		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "I", publicDetails, 1L);
	  }
	  
	  
	  @Test
	  public void transformPublicEntityToApplicantElseTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  pbd.setIncorpInd(1);
		  pbd.setBusinessValidatedInd(1);
		  pbd.setCountry("USA");
		  pbd.setValidatedInd(1);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "X", publicDetails, 1L);
	  }
	  @Test
	  public void transformPublicEntityToApplicantElseoneTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  pbd.setIncorpInd(1);
		  pbd.setBusinessValidatedInd(1);
		  pbd.setCountry("USAe");
		  pbd.setValidatedInd(0);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "X", publicDetails, 1L);
	  }
	  @Test
	  public void transformPublicEntityToApplicantElseTwoTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  pbd.setIncorpInd(1);
		  pbd.setBusinessValidatedInd(1);
		  pbd.setForeignAddressInd(1);
		  pbd.setCountry("USAe");
		  pbd.setValidatedInd(0);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "X", publicDetails, 1L);
	  }
	  
	  @Test
	  public void transformPublicEntityToApplicantElseThreeTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setSelectedInEtrackInd(1);
		  pbd.setIncorpInd(0);
		  pbd.setBusinessValidatedInd(0);
		  pbd.setForeignAddressInd(1);
		  pbd.setCountry("USAe");
		  pbd.setValidatedInd(0);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "T", publicDetails, 1L);
	  }
	  
	  @Test
	  public void transformPublicEntityToApplicantElseEmptyTest() {
		  List<PublicDetail> publicDetails = new ArrayList();
		  PublicDetail pbd = new PublicDetail();
		  pbd.setLegallyResponsibleTypeCode(OWNER);
		  pbd.setForeignAddressInd(0);
		  pbd.setCountry("USA");
		  pbd.setValidatedInd(0);
		  publicDetails.add(pbd);
		  Applicant ap = transactionServie.transformPublicEntityToApplicant("", "",
			      "C", publicDetails, 1L);
	  }
	  @Test
	  public void transformPublicHistoryIntoApplicant() {
		  List<PublicHistoryDetail> publicHistoryList = new ArrayList();
		  PublicHistoryDetail ph = new PublicHistoryDetail();
		  ph.setHPublicTypeCode("I");
		  publicHistoryList.add(ph);
		  List<AddressHistory> addressHistoryList = new ArrayList();
		  AddressHistory ah = new AddressHistory();
		  ah.setHCountry("USA");
		  addressHistoryList.add(ah);
		  Applicant ap = transactionServie.transformPublicHistoryIntoApplicant("","", 
			      publicHistoryList,  addressHistoryList);
	  }
	  
	  @Test
	  public void transformPublicHistoryIntoApplicantElseTest() {
		  List<PublicHistoryDetail> publicHistoryList = new ArrayList();
		  PublicHistoryDetail ph = new PublicHistoryDetail();
		  ph.setHPublicTypeCode("X");
		  ph.setHIncorpInd(1);
		  ph.setHBusinessValidatedInd(1);
		  publicHistoryList.add(ph);
		  List<AddressHistory> addressHistoryList = new ArrayList();
		  AddressHistory ah = new AddressHistory();
		  ah.setHCountry("USA");
		  addressHistoryList.add(ah);
		  Applicant ap = transactionServie.transformPublicHistoryIntoApplicant("","", 
			      publicHistoryList,  addressHistoryList);
	  }
	  
	  @Test
	  public void transformPublicHistoryIntoApplicantElseIncorpTest() {
		  List<PublicHistoryDetail> publicHistoryList = new ArrayList();
		  PublicHistoryDetail ph = new PublicHistoryDetail();
		  ph.setHPublicTypeCode("C");
		  ph.setHIncorpInd(0);
		  ph.setHBusinessValidatedInd(0);
		  publicHistoryList.add(ph);
		  List<AddressHistory> addressHistoryList = new ArrayList();
		  AddressHistory ah = new AddressHistory();
		  ah.setHCountry("USA");
		  addressHistoryList.add(ah);
		  Applicant ap = transactionServie.transformPublicHistoryIntoApplicant("","", 
			      publicHistoryList,  addressHistoryList);
	  }
	  
	  @Test
	  public void transformPublicHistoryIntoApplicantElseOneTest() {
		  List<PublicHistoryDetail> publicHistoryList = new ArrayList();
		  PublicHistoryDetail ph = new PublicHistoryDetail();
		  ph.setHPublicTypeCode("S");
		  ph.setHIncorpInd(0);
		  ph.setHBusinessValidatedInd(0);
		  publicHistoryList.add(ph);
		  List<AddressHistory> addressHistoryList = new ArrayList();
		  AddressHistory ah = new AddressHistory();
		  ah.setHCountry("USAe");
		  addressHistoryList.add(ah);
		  Applicant ap = transactionServie.transformPublicHistoryIntoApplicant("","", 
			      publicHistoryList,  addressHistoryList);
	  }
	  
	  @Test
	  public void getFacilityDetails() {
		  Map<String, Object> resultSet = new HashMap();
		  List<FacilityDetail> facilityDetails = new ArrayList();
		  FacilityDetail fds = new FacilityDetail();
		  fds.setStreet1("TEST");
		  fds.setStreet2("TEST");
		  facilityDetails.add(fds);
		  resultSet.put(DartDBConstants.FACILITY_CURSOR, facilityDetails);
		  FacilityDetail fd = transactionServie.getFacilityDetails("","",
			     resultSet, 1L);
	  }
	  
	  
	  @Test
		public void getFacilityDetailsDBExceptionTest() {
		  Map<String, Object> resultSet = new HashMap();
		  assertThrows(NoDataFoundException.class, ()->transactionServie.getFacilityDetails("","",
				     resultSet, 1L));
		}
	  
	 @Test
	 public void transformProjectEntity() {
		 Project project = new Project();
		 project.setProposedStartDate(new Date());
		 project.setEstmtdCompletionDate(new Date());
		 List<ProjectDevelopment> projectDevList = new ArrayList();
		 ProjectDevelopment pd = new ProjectDevelopment();
		 projectDevList.add(pd);
		 project.setProjectDevelopments(projectDevList);
		 List<ProjectResidential> projectResidentialList = new ArrayList();
		 ProjectResidential pr = new ProjectResidential();
		 projectResidentialList.add(pr);
		 project.setProjectResidentials(projectResidentialList);
		 List<ProjectSICNAICSCode> projectSicNaicsList = new ArrayList();
		 ProjectSICNAICSCode pns = new ProjectSICNAICSCode();
		 pns.setSicCode("1");
		 projectSicNaicsList.add(pns);
		 project.setProjectSicNaicsCodes(projectSicNaicsList);
		 List<ProjectSWFacilityType> projectSWFacilityTypes = new ArrayList();
		 ProjectSWFacilityType psw = new ProjectSWFacilityType();
		 psw.setSwFacilitySubTypeId(2);
		 projectSWFacilityTypes.add(psw);
		 project.setProjectSWFacilityType(projectSWFacilityTypes);
		 List<FacilityBIN> binNumbers = new ArrayList();
		 FacilityBIN fb = new FacilityBIN();
		 fb.setDeletedInd("1");
		 fb.setBin("1");
		 binNumbers.add(fb);
		 ProjectInfo pinfo = transactionServie.transformProjectEntity("","", project, binNumbers);
	 }
	 
	 @Test
	 public void transformProjectEntityNotEquals() {
		 Project project = new Project();
		 project.setProposedStartDate(new Date());
		 project.setEstmtdCompletionDate(new Date());
		 List<ProjectDevelopment> projectDevList = new ArrayList();
		 ProjectDevelopment pd = new ProjectDevelopment();
		 projectDevList.add(pd);
		 project.setProjectDevelopments(projectDevList);
		 List<ProjectResidential> projectResidentialList = new ArrayList();
		 ProjectResidential pr = new ProjectResidential();
		 projectResidentialList.add(pr);
		 project.setProjectResidentials(projectResidentialList);
		 List<ProjectSICNAICSCode> projectSicNaicsList = new ArrayList();
		 ProjectSICNAICSCode pns = new ProjectSICNAICSCode();
		 pns.setSicCode("1");
		 projectSicNaicsList.add(pns);
		 project.setProjectSicNaicsCodes(projectSicNaicsList);
		 List<ProjectSWFacilityType> projectSWFacilityTypes = new ArrayList();
		 ProjectSWFacilityType psw = new ProjectSWFacilityType();
		 psw.setSwFacilitySubTypeId(-1);
		 projectSWFacilityTypes.add(psw);
		 project.setProjectSWFacilityType(projectSWFacilityTypes);
		 List<FacilityBIN> binNumbers = new ArrayList();
		 FacilityBIN fb = new FacilityBIN();
		 fb.setBin("0");
		 binNumbers.add(fb);
		 ProjectInfo pinfo = transactionServie.transformProjectEntity("","", project, binNumbers);
	 }
	 
	 @Test
	 public void transformPermitApplicationData() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Integer, List<PermitApplication>> map = transactionServie.transformPermitApplicationData("","", 1L, eTrackPermits,
			     contactAgents, false);
	 }
	 
	 @Test
	 public void transformDartPermitApplicationData() {
		 DartPermit dartExistingPermit = new DartPermit();
		 dartExistingPermit.setTransType("REI");
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Long, DartPermit> dartModExtEligiblePermitApps = new HashMap();
		 dartModExtEligiblePermitApps.put(2L, dartExistingPermit);
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap = new HashMap();
		 dartModExtAssignedPermitInETrackMap.put(1L, etp);
		 Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap();
		 DartApplication da =new DartApplication();
		 dartPendingTransferEligiblePermitApps.put(1L, da);
		 Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap();
		 dartTransferAssignedPermitsMap.put(1L, etp);
		 Map<Long, String> extendedDateDetailMap = new HashMap();
		 extendedDateDetailMap.put(1L, "one,two,three");
		 Map<String, Object> pa = transactionServie.transformDartPermitApplicationData(
				 "", "", 1L, dartModExtEligiblePermitApps, dartModExtAssignedPermitInETrackMap,
			      dartPendingTransferEligiblePermitApps, dartTransferAssignedPermitsMap,
			      contactAgents, false, extendedDateDetailMap); 
	 }
	 
	 @Test
	 public void transformDartPermitApplicationDataOne() {
		 DartPermit dartExistingPermit = new DartPermit();
		 dartExistingPermit.setTransType("REItt");
		 dartExistingPermit.setRenewedInd("1");
		 dartExistingPermit.setGpAuthId(2L);
		 dartExistingPermit.setGpPermitType("test");
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Long, DartPermit> dartModExtEligiblePermitApps = new HashMap();
		 dartModExtEligiblePermitApps.put(2L, dartExistingPermit);
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap = new HashMap();
		 dartModExtAssignedPermitInETrackMap.put(1L, etp);
		 Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap();
		 DartApplication da =new DartApplication();
		 dartPendingTransferEligiblePermitApps.put(1L, da);
		 Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap();
		 dartTransferAssignedPermitsMap.put(1L, etp);
		 Map<Long, String> extendedDateDetailMap = new HashMap();
		 extendedDateDetailMap.put(1L, "one,two,three");
		 Map<String, Object> pa = transactionServie.transformDartPermitApplicationData(
				 "", "", 1L, dartModExtEligiblePermitApps, dartModExtAssignedPermitInETrackMap,
			      dartPendingTransferEligiblePermitApps, dartTransferAssignedPermitsMap,
			      contactAgents, false, extendedDateDetailMap); 
	 }
	 
	 @Test
	 public void transformDartPermitApplicationDataTwo() {
		 DartPermit dartExistingPermit = new DartPermit();
		 dartExistingPermit.setTransType("REItt");
		 dartExistingPermit.setRenewedInd("1");
		 dartExistingPermit.setGpAuthId(null);
		 dartExistingPermit.setGpPermitType("test");
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Long, DartPermit> dartModExtEligiblePermitApps = new HashMap();
		 dartModExtEligiblePermitApps.put(2L, dartExistingPermit);
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap = new HashMap();
		 dartModExtAssignedPermitInETrackMap.put(1L, etp);
		 Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap();
		 DartApplication da =new DartApplication();
		 dartPendingTransferEligiblePermitApps.put(1L, da);
		 Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap();
		 dartTransferAssignedPermitsMap.put(1L, etp);
		 Map<Long, String> extendedDateDetailMap = new HashMap();
		 extendedDateDetailMap.put(1L, "one,two,three");
		 Map<String, Object> pa = transactionServie.transformDartPermitApplicationData(
				 "", "", 1L, dartModExtEligiblePermitApps, dartModExtAssignedPermitInETrackMap,
			      dartPendingTransferEligiblePermitApps, dartTransferAssignedPermitsMap,
			      contactAgents, false, extendedDateDetailMap); 
	 }
	 
	 @Test
	 public void transformDartPermitApplicationDataThree() {
		 DartPermit dartExistingPermit = new DartPermit();
		 dartExistingPermit.setTransType("REItt");
		 dartExistingPermit.setRenewedInd("2");
		 dartExistingPermit.setGpAuthId(2L);
		 dartExistingPermit.setGpPermitType("test");
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Long, DartPermit> dartModExtEligiblePermitApps = new HashMap();
		 dartModExtEligiblePermitApps.put(2L, dartExistingPermit);
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap = new HashMap();
		 dartModExtAssignedPermitInETrackMap.put(1L, etp);
		 Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap();
		 DartApplication da =new DartApplication();
		 dartPendingTransferEligiblePermitApps.put(1L, da);
		 Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap();
		 dartTransferAssignedPermitsMap.put(1L, etp);
		 Map<Long, String> extendedDateDetailMap = new HashMap();
		 extendedDateDetailMap.put(1L, "one,two,three");
		 Map<String, Object> pa = transactionServie.transformDartPermitApplicationData(
				 "", "", 1L, dartModExtEligiblePermitApps, dartModExtAssignedPermitInETrackMap,
			      dartPendingTransferEligiblePermitApps, dartTransferAssignedPermitsMap,
			      contactAgents, false, extendedDateDetailMap); 
	 }
	 
	 @Test
	 public void transformDartPermitApplicationDataFour() {
		 DartPermit dartExistingPermit = new DartPermit();
		 dartExistingPermit.setTransType("REItt");
		 dartExistingPermit.setRenewedInd("2");
		 dartExistingPermit.setGpAuthId(0L);
		 dartExistingPermit.setGpPermitType("test");
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<Long, DartPermit> dartModExtEligiblePermitApps = new HashMap();
		 dartModExtEligiblePermitApps.put(2L, dartExistingPermit);
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 Map<Long, ETrackPermit> dartModExtAssignedPermitInETrackMap = new HashMap();
		 dartModExtAssignedPermitInETrackMap.put(1L, etp);
		 Map<Long, DartApplication> dartPendingTransferEligiblePermitApps = new HashMap();
		 DartApplication da =new DartApplication();
		 dartPendingTransferEligiblePermitApps.put(3L, da);
		 Map<Long, ETrackPermit> dartTransferAssignedPermitsMap = new HashMap();
		 dartTransferAssignedPermitsMap.put(1L, etp);
		 Map<Long, String> extendedDateDetailMap = new HashMap();
		 extendedDateDetailMap.put(1L, "one,two,three");
		 Map<String, Object> pa = transactionServie.transformDartPermitApplicationData(
				 "", "", 1L, dartModExtEligiblePermitApps, dartModExtAssignedPermitInETrackMap,
			      dartPendingTransferEligiblePermitApps, dartTransferAssignedPermitsMap,
			      contactAgents, false, extendedDateDetailMap); 
	 }
	 
	 @Test
	 public void transformPermitSummaryDetails() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 etp.setEdbApplId(1L);
		 etp.setPendingInd(1);
//		 etp.setExtnReqInd("Y");
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
//	      List<AvailTransType> ats = new ArrayList();
//	      AvailTransType at =new AvailTransType(null, null);
//	      ats.add(at);
//	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsAddAllTypesTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 //etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 etp.setUserSelExtInd(1);
		 etp.setUserSelNewInd(1);
		 etp.setUserSelRenInd(1);
		 etp.setUserSelTransferInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsAddExtModTransTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 //etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 etp.setUserSelExtInd(1);
//		 etp.setUserSelNewInd(1);
//		 etp.setUserSelRenInd(1);
		 etp.setUserSelTransferInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsAddExtRENTransTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 //etp.setEdbApplId(null);
//		 etp.setUserSelModInd(1);
		 etp.setUserSelExtInd(1);
//		 etp.setUserSelNewInd(1);
		 etp.setUserSelRenInd(1);
		 etp.setUserSelTransferInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsAddExtModTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 //etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
		 etp.setUserSelExtInd(1);
//		 etp.setUserSelNewInd(1);
//		 etp.setUserSelRenInd(1);
//		 etp.setUserSelTransferInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelExtTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelExtInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelModTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelModInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelNewTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelModInd(1);
		 etp.setUserSelExtInd(1);
		 etp.setUserSelRenInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelTransferTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelTransferInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelTransRenTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelTransferInd(1);
		 etp.setUserSelRenInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsUserSelRenewTest() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setUserSelRenInd(1);
		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pa.setPendingAppTransferReqInd("Y");
		 pa.setExtnReqInd("Y");
		 pa.setRenewReqInd("Y");
		 pa.setTransferReqInd("Y");
		 pa.setModReqInd("Y");
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType("NEW", "");
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformPermitSummaryDetailsOne() {
		 List<ETrackPermit> eTrackPermits = new ArrayList();
		 ETrackPermit etp = new ETrackPermit();
		 etp.setEdbApplId(null);
		 etp.setUserSelModInd(1);
//		 etp.setEdbApplId(1L);
		 eTrackPermits.add(etp);
		 List<ContactAgent> contactAgents = new ArrayList();
		 ContactAgent ca = new ContactAgent();
		 contactAgents.add(ca);
		 Map<String, List<PermitApplication>> permitApplicationResult = new HashMap();
		 List<PermitApplication> pas = new ArrayList();
		 PermitApplication pa =new PermitApplication();
		 pas.add(pa);
		 permitApplicationResult.put("1", pas);
	      Map<Long, String> narrativeDescMapping= new HashMap();
	      narrativeDescMapping.put(1L, "one,two,three");
	      Map<Long, List<AvailTransType>> availableTransTypesMap= new HashMap();
	      List<AvailTransType> ats = new ArrayList();
	      AvailTransType at =new AvailTransType(null, null);
	      ats.add(at);
	      availableTransTypesMap.put(1L, ats);
		 Map<String, List<PermitApplication>> map= transactionServie.transformPermitSummaryDetails("",
			      "", 1L, eTrackPermits, contactAgents, permitApplicationResult, narrativeDescMapping, 
			      availableTransTypesMap);
	 }
	 
	 @Test
	 public void transformInquiryAlertMessage() {
		 List<ProjectAlert> alertsList = new ArrayList();
		 ProjectAlert pa =new ProjectAlert();
		 pa.setReadInd(1);
		 pa.setAlertDate(new Date());
		 alertsList.add(pa);
		 List<Alert> la = transactionServie.transformAlertMessage("", "",alertsList);
	 }
	 
	 @Test
	 public void transformInquiryAlertMessageGi() {
		 List<GIInquiryAlert> alertsList = new ArrayList();
		 GIInquiryAlert gi =new GIInquiryAlert();
		 gi.setReadInd(1);
		 gi.setAlertDate(new Date());
		 alertsList.add(gi);
		 List<Alert> la = transactionServie.transformInquiryAlertMessage("", "",alertsList);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardData() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("2");
	      dm.setCompletenessDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboard2DataTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("20");
	      dm.setCompletenessDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardWRCOMTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("3");
	      dm.setCommentsDeadlineDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardHearingTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("4");
	      dm.setHearingDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardHearingTest2() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("4");
	      //dm.setHearingDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardFDispoTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("5");
	      dm.setFinalDispositionDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardFDispoTest2() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("5");
	      //dm.setFinalDispositionDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardResponseDueTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("6");
	      dm.setPermitteeRespDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardResponseDue2Test() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("6");
	      //dm.setPermitteeRespDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardDIMSRResTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("7");
	      dm.setDimsrDecisionDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardDIMSRRes2Test() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("7");
	      //dm.setDimsrDecisionDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardIncompleteResTest() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("9");
	      dm.setCompletenessDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void transformDataIntoDashboardIncompleteRes2Test() {
		 List<DartApplication> applications = new ArrayList();
		 DartApplication da = new DartApplication();
		 da.setBatchId(1L);
		 da.setProgramManager("test test test");
		 da.setPublicName("sdds*dsfds*sdf*ds");
		 da.setEmergencyInd("E");
		 da.setZip("532421");
		 da.setSapaDate(new Date());
	     da.setDueDate(new Date());
	     da.setReceivedDate(new Date());
	     da.setStartDate(new Date());
	     da.setExpiryDate(new Date());
		 applications.add(da);
		 Map<Long, Long> outForReviewProject = new HashMap();
		 outForReviewProject.put(1L, 1L);
	      Map<Long, DartMilestone> dartMilestoneMapWithBatchId = new HashMap();
	      DartMilestone dm =new DartMilestone();
	      dm.setEdbCurrentStatusCode("9");
	      //dm.setCompletenessDueDate(new Date());
	      dartMilestoneMapWithBatchId.put(1L, dm);
		 List<DashboardDetail> dd = transactionServie.transformDataIntoDashboardData(applications,
			      outForReviewProject, dartMilestoneMapWithBatchId);
	 }
	 
	 @Test
	 public void prepareApplicationInfo() {
		 List<PendingApplication> applications = new ArrayList();
		 PendingApplication pa =new PendingApplication();
		 pa.setProjectId(1L);
		 pa.setCreateDate(new Date());
		 pa.setDecId("testtestetst");
		 applications.add(pa);
		 Map<Long, ApplicantDto> lrpsMap = new HashMap();
		 ApplicantDto apd = new ApplicantDto();
		 apd.setFirstName("test");
		 apd.setLastName("test");
		 apd.setMiddleName("test");
		 lrpsMap.put(1L,apd);
		 List<dec.ny.gov.etrack.dart.db.entity.Application> permitApplications = new ArrayList();
		 dec.ny.gov.etrack.dart.db.entity.Application a = new dec.ny.gov.etrack.dart.db.entity.Application();
		 a.setPermitTypeCode("!");
		 a.setGpInd("1");
		 a.setTransTypeCode("1");
		 when(applicationRepo.findAllByProjectId(1L)).thenReturn(permitApplications);
		 List<DashboardDetail> dd = transactionServie.prepareApplicationInfo("", "",applications, lrpsMap);
	 }
	 
	 @Test
	 public void prepareApplicationInfoTest() {
		 List<PendingApplication> applications = new ArrayList();
		 PendingApplication pa =new PendingApplication();
		 pa.setProjectId(1L);
		 pa.setEaInd(1);
		 pa.setCreateDate(new Date());
		 pa.setDecId("testtestetst");
		 applications.add(pa);
		 Map<Long, ApplicantDto> lrpsMap = new HashMap();
		 ApplicantDto apd = new ApplicantDto();
		 apd.setFirstName("test");
		 apd.setLastName("test");
		 apd.setMiddleName("test");
		 lrpsMap.put(1L,apd);
		 List<dec.ny.gov.etrack.dart.db.entity.Application> permitApplications = new ArrayList();
		 dec.ny.gov.etrack.dart.db.entity.Application a = new dec.ny.gov.etrack.dart.db.entity.Application();
		 a.setPermitTypeCode("!");
		 a.setGpInd("1");
		 a.setTransTypeCode("1");
		 when(applicationRepo.findAllByProjectId(1L)).thenReturn(permitApplications);
		 List<DashboardDetail> dd = transactionServie.prepareApplicationInfo("", "",applications, lrpsMap);
	 }
	 
	 @Test
	 public void transformOutForReviewAppsToDashboardTest() {
		 List<OutForReviewEntity> outForReviewEntityApps = new ArrayList<OutForReviewEntity>();
		 OutForReviewEntity outForReviewEntity = new OutForReviewEntity();
		 outForReviewEntity.setProjectId(1l);
		 outForReviewEntity.setStreet1("Street");
		 outForReviewEntity.setCity("City");
		 outForReviewEntity.setState("State");
		 outForReviewEntity.setZip("Zip");
		 outForReviewEntity.setReviewDueDate(new Date());
		 outForReviewEntityApps.add(outForReviewEntity);
		 
		 OutForReviewEntity outForReviewEntity1 = new OutForReviewEntity();
		 outForReviewEntity1.setProjectId(1l);
		 outForReviewEntity1.setStreet1("Street");
		 outForReviewEntity1.setCity("City");
		 outForReviewEntity1.setState("State");
		 outForReviewEntity1.setZip("Zip");
		 outForReviewEntity1.setReviewDueDate(new Date());
		 outForReviewEntityApps.add(outForReviewEntity1);
		 assertNotNull(transactionServie.transformOutForReviewAppsToDashboard(outForReviewEntityApps));
	 }

}
