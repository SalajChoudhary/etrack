package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.ProjectActivity;
import dec.ny.gov.etrack.dart.db.entity.Public;
import dec.ny.gov.etrack.dart.db.entity.PublicAssociatedFacility;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.Applicant;
import dec.ny.gov.etrack.dart.db.model.PublicType;
import dec.ny.gov.etrack.dart.db.model.SearchPatternEnum;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.DartDbRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;
import dec.ny.gov.etrack.dart.db.util.DartDBServiceUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartPublicServiceImplTest {
	
	  @Mock
	  private ProjectRepo projectRepo;
	  @Mock
	  private ProjectActivityRepo projectActivityRepo;
	  @Mock
	  private ApplicantRepo applicantRepo;
	  @Mock
	  private DartDbDAO dartDBDAO;
	  @Mock
	  private DartDBServiceUtility dartDBServiceUtility;
	  @Mock
	  private DartDbRepo dartDbRepo;
	  @Mock
	  private TransformationService transformationService;
	  
	  @InjectMocks
	  private DartPublicServiceImpl dartPublicServiceImpl;
	  
	  @Test
	  public void retrieveApplicantsEmptyOneTest() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  publicLists.add(applicantDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l,1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList<>();
		  signSubmitActivityList.add(1);
		  when(projectActivityRepo.findProjectSignedAndSubmitted(1l,5)).thenReturn(signSubmitActivityList);
		  
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity activity= new  ProjectActivity();
		  activity.setCompletionDate(new Date());
//		  validatedIndList.add(activity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              1l, DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","C", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveApplicantsTest() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  publicLists.add(applicantDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l,1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList<>();
		  signSubmitActivityList.add(1);
		  when(projectActivityRepo.findProjectSignedAndSubmitted(1l,5)).thenReturn(signSubmitActivityList);
		  
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity activity= new  ProjectActivity();
		  activity.setCompletionDate(new Date());
		  validatedIndList.add(activity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              1l, DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","C", 1l,1);
		  assertNotNull(response);
	  }
	  
	  
	  @Test
	  public void retrieveApplicantsTes1t() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  applicantDto.setValidatedInd(1);
		  publicLists.add(applicantDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l,1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList<>();
		  signSubmitActivityList.add(1);
		  when(projectActivityRepo.findProjectSignedAndSubmitted(1l,5)).thenReturn(signSubmitActivityList);
		  
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity activity= new  ProjectActivity();
		  validatedIndList.add(activity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              1l, DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  
		  Project project = new Project();
		  when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","C", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveApplicantsEmptyTest() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  when(applicantRepo.findAllContactsByAssociatedInd(1l,1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList<>();
		  signSubmitActivityList.add(1);		  
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","C", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveApplicantsOTest() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  applicantDto.setValidatedInd(2);
		  publicLists.add(applicantDto);
		  when(applicantRepo.findAllOwnersByAssociatedInd(1l,1)).thenReturn(publicLists);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","O", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveApplicantsATest() {
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  applicantDto.setValidatedInd(0);
		  publicLists.add(applicantDto);
		  Mockito.lenient().when(applicantRepo.findAllOwnersByAssociatedInd(1l,2)).thenReturn(publicLists);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","A", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveApplicantsPTest() {
		  List<ProjectActivity> validatedIndList = new ArrayList();
		  ProjectActivity projectActivity = new ProjectActivity();
		  projectActivity.setCompletionDate(new Date());
		  validatedIndList.add(projectActivity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(
              1l, DartDBConstants.APPLICANT_VALIDATED)).thenReturn(validatedIndList);
		  List<ApplicantDto> publicLists = new ArrayList<>();
		  ApplicantDto applicantDto = new ApplicantDto();
		  applicantDto.setValidatedInd(1);
		  publicLists.add(applicantDto);
		  when(applicantRepo.findAllPublicsByAssociatedInd(1l,1)).thenReturn(publicLists);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveApplicants("","","P", 1l,1);
		  assertNotNull(response);
	  }
	  
	  
	  @Test
	  public void retrieveAllPublicsAssociatedWithThisProjectTest() {
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity projectActivity = new ProjectActivity();
		  projectActivity.setCompletionDate(new Date());
		  validatedIndList.add(projectActivity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(1l,
        DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  Project project = new Project();
		  when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		  List<ApplicantDto> publicLists = new ArrayList();
		  ApplicantDto appDto = new ApplicantDto();
		  publicLists.add(appDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l, 1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList();
		  signSubmitActivityList.add(3);
		  when(projectActivityRepo.findProjectActivityStatusId(1l, 5)).thenReturn(signSubmitActivityList);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveAllPublicsAssociatedWithThisProject("","", 1l);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveAllPublicsAssociatedWithThisProjectOneTest() {
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity projectActivity = new ProjectActivity();
		  validatedIndList.add(projectActivity);
//		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(1l,
//        DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  Project project = new Project();
		  when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		  List<ApplicantDto> publicLists = new ArrayList();
		  ApplicantDto appDto = new ApplicantDto();
		  publicLists.add(appDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l, 1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList();
		  signSubmitActivityList.add(3);
		  when(projectActivityRepo.findProjectActivityStatusId(1l, 5)).thenReturn(signSubmitActivityList);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveAllPublicsAssociatedWithThisProject("","", 1l);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void retrieveAllPublicsAssociatedWithThisProjectTwoTest() {
		  List<ProjectActivity> validatedIndList = new ArrayList<>();
		  ProjectActivity projectActivity = new ProjectActivity();
		  projectActivity.setCompletionDate(new Date());
		  validatedIndList.add(projectActivity);
		  when(projectActivityRepo.findProjectActivityStatusByActivityStatusId(1l,
        DartDBConstants.CONTACT_AGENT_VALIDATED)).thenReturn(validatedIndList);
		  Project project = new Project();
		  when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
		  List<ApplicantDto> publicLists = new ArrayList();
		  ApplicantDto appDto = new ApplicantDto();
		  publicLists.add(appDto);
		  when(applicantRepo.findAllContactsByAssociatedInd(1l, 1)).thenReturn(publicLists);
		  List<Integer> signSubmitActivityList = new ArrayList();
		  signSubmitActivityList.add(3);
		  when(projectActivityRepo.findProjectActivityStatusId(1l, 5)).thenReturn(signSubmitActivityList);
		  ResponseEntity<Object> response = dartPublicServiceImpl.retrieveAllPublicsAssociatedWithThisProject("","", 1l);
		  assertNotNull(response);
	  }

	  @Test
	  public void getAllMatchedApplicantsTest() {
		  List<PublicAssociatedFacility> publicAndAssociatedFacilities = new ArrayList();
		  PublicAssociatedFacility pb = new PublicAssociatedFacility();
		  pb.setPublicId(1l);
		  publicAndAssociatedFacilities.add(pb);
		  when(dartDBDAO.searchAllMatchedApplicants(
		            "", "", "", SearchPatternEnum.S.name().toUpperCase(),            
		            "team".toUpperCase(), SearchPatternEnum.S.name().toUpperCase())).thenReturn(publicAndAssociatedFacilities);
		  ResponseEntity<Object> response = dartPublicServiceImpl.getAllMatchedApplicants("","", PublicType.I, "",SearchPatternEnum.S, "team", SearchPatternEnum.S);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getAllMatchedApplicantsOneTest() {
		   String userId="",   contextId="",firstName="test",lastName="testl";
	       PublicType publicType = PublicType.I; 
	      SearchPatternEnum fType=SearchPatternEnum.S;
	      SearchPatternEnum lType=SearchPatternEnum.S;
	      List<PublicAssociatedFacility> publicAndAssociatedFacilities = new ArrayList();
		  PublicAssociatedFacility pb = new PublicAssociatedFacility();
		  pb.setPublicId(1l);
		  pb.setFname("test12");
		  pb.setLname("test");
		  pb.setLocationDirections("test");
		  pb.setCity("newyork");
		  publicAndAssociatedFacilities.add(pb);
		  when(dartDBDAO.searchAllMatchedApplicants(
            userId, contextId, firstName.toUpperCase(), fType.name().toUpperCase(),            
            lastName.toUpperCase(), lType.name().toUpperCase())).thenReturn(publicAndAssociatedFacilities);
		
		  ResponseEntity<Object> re = dartPublicServiceImpl.getAllMatchedApplicants( userId, contextId,
			        publicType,  firstName,  fType,  lastName,  lType);
		  assertNotNull(re);
	  }
	  
	  @Test
	  public void getAllMatchedApplicantsPTest() {
		  String userId="",   contextId="",firstName="test",lastName="testl";
	       PublicType publicType = PublicType.M; 
	      SearchPatternEnum fType=SearchPatternEnum.S;
	      SearchPatternEnum lType=SearchPatternEnum.S;
	      List<PublicAssociatedFacility> publicAndAssociatedFacilities = new ArrayList();
		  PublicAssociatedFacility pb = new PublicAssociatedFacility();
		  pb.setPublicId(2l);
		  pb.setFname("test12");
		  pb.setLname("test");
		  pb.setLocationDirections("test");
		  pb.setCity("newyork");
		  publicAndAssociatedFacilities.add(pb);
//		  when(dartDBDAO.searchAllMatchedApplicants(
//           userId, contextId, firstName.toUpperCase(), fType.name().toUpperCase(),            
//           lastName.toUpperCase(), lType.name().toUpperCase())).thenReturn(publicAndAssociatedFacilities);
//		
		  ResponseEntity<Object> re = dartPublicServiceImpl.getAllMatchedApplicants( userId, contextId,
			        publicType,  firstName,  fType,  lastName,  lType);
		  assertNotNull(re);
	  }
	  
	  @Test
	  public void getAllMatchedApplicantsITest() {
		  ResponseEntity<Object> response = dartPublicServiceImpl.getAllMatchedApplicants("","", PublicType.I, "",SearchPatternEnum.E, "", SearchPatternEnum.S);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void validateEdbPublicIdTest() {
		  List<Public> anyExistingAssociatedPublic = new ArrayList<>();
		  Public publics = new Public();
		  publics.setPublicTypeCode("p");
		  anyExistingAssociatedPublic.add(publics);
		  when(dartDbRepo.findAllPublicsAssociatedProject(1l,2l)).thenReturn(new ArrayList());
		  when(dartDbRepo.findAllPublicsAssociatedProject(1l, 1l)).thenReturn(anyExistingAssociatedPublic);
		  Map<String, Object> edbPublics = new HashMap();
		  edbPublics.put("p_status_cd", "200Ok");
		  edbPublics.put("p_status_msg", "200Ok");
		  edbPublics.put("p_public_cur", new ArrayList());
		  Mockito.lenient().when(dartDBDAO.getPublicInfoFromDart("", "", 2l, "I")).thenReturn(edbPublics);
		  Object response = dartPublicServiceImpl.validateEdbPublicId("","",1l,1l,2l);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getApplicantsSummaryTest() {
		  List<Public> publicLists = new ArrayList<>();
		  Public publics = new Public();
		  publicLists.add(publics);
		  when(dartDbRepo.findAllApplicantsByProjectIdAndSelectedInEtrackInd(1l,1)).thenReturn(publicLists);
		  ResponseEntity<Object> response = dartPublicServiceImpl.getApplicantsSummary("","", 1l,1);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getApplicantInfoTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      List<Public> existingPublic = new ArrayList();
	      Public pb = new Public();
	      pb.setPublicId(1L);
	      existingPublic.add(pb);
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenReturn(existingPublic);
	      Map<String, Object> res = new HashMap(); 
	      res.put("p_status_cd", "200Ok");
	      res.put("p_status_msg", "200Ok");
	      res.put("p_public_cur", new ArrayList());
		  when(dartDBDAO.getApplicantDetails(userId, contextId, projectId, publicId)).thenReturn(res);
		  Applicant applicant = new Applicant();
		  when(transformationService.transformPublicEntityToApplicant(userId,
		          contextId, aplctType, new ArrayList(), publicId)).thenReturn(applicant);;
		  ResponseEntity<Object> response = dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType);
		  assertNotNull(response);
	  }

	  @Test
	  public void getApplicantInfoOneTest() {
		   String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      List<Public> existingPublic = new ArrayList();
	      Public pb = new Public();
	      pb.setPublicId(1L);
	      existingPublic.add(pb);
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenReturn(existingPublic);
	      Map<String, Object> res = null; 
		  when(dartDBDAO.getApplicantDetails(userId, contextId, projectId, publicId)).thenReturn(res);
		  ResponseEntity<Object> response = dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getApplicantInfoDataIntegrityViolationExceptionTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenThrow(DataIntegrityViolationException.class);
		  assertThrows(BadRequestException.class, ()-> dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType));
	  }
	  
	  @Test
	  public void getApplicantInfoNoDataFoundExceptionTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenThrow(NoDataFoundException.class);
		  assertThrows(NoDataFoundException.class, ()-> dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType));
	  }
	  
	  @Test
	  public void getApplicantInfoDartDBExceptionTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenThrow(DartDBException.class);
		  assertThrows(DartDBException.class, ()-> dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType));
	  }
	  
	  @Test
	  public void getApplicantInfoBadRequestExceptionTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenThrow(BadRequestException.class);
		  assertThrows(BadRequestException.class, ()-> dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType));
	  }
	  
	  @Test
	  public void getApplicantInfoNullpointerExceptionTest() {
		  String userId = null, contextId = "";
	      Long projectId=1L; 
	      Long publicId=1L;
	      String aplctType ="";
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenThrow(NullPointerException.class);
		  assertThrows(DartDBException.class, ()-> dartPublicServiceImpl.getApplicantInfo(userId,contextId,
			      projectId,  publicId, aplctType));
	  }
	  
	  @Rule
	  public final ExpectedException exception = ExpectedException.none();
	  
//	  @Test
//	  public void getApplicantInfoTwoTest() {
//		   String userId = null, contextId = "";
//	      Long projectId=1L; 
//	      Long publicId=1L;
//	      String aplctType ="";
//	      List<Public> existingPublic = new ArrayList();
//	      Public pb = new Public();
//	      pb.setPublicId(1L);
//	      existingPublic.add(pb);
//	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, publicId)).thenReturn(existingPublic);
//	      Map<String, Object> res = null; 
////	      res.put("p_status_cd", "200Ok");
////	      res.put("p_status_msg", "200Ok");
////	      res.put("p_public_cur", new ArrayList());
//		  when(dartDBDAO.getApplicantDetails(userId, contextId, projectId, publicId)).thenThrow(new DataIntegrityViolationException("Test"));
////		  Applicant applicant = new Applicant();
////		  when(transformationService.transformPublicEntityToApplicant(userId,
////		          contextId, aplctType, new ArrayList(), publicId)).thenReturn(applicant);;
//		  exception.expect(BadRequestException.class);
//		  ResponseEntity<Object> response = dartPublicServiceImpl.getApplicantInfo(userId,contextId,
//			      projectId,  publicId, aplctType);
////	      assertNotNull(response);
//	  }

	  @Test
	  public void getEdbApplicantInfo() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      Map<String, Object> res = new HashMap();
	      res.put("p_status_cd", "200Ok");
      res.put("p_status_msg", "200Ok");
      res.put("p_public_cur", new ArrayList());
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenReturn(res);
		  ResponseEntity<Object> response =  dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getEdbApplicantInfoOneTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      Map<String, Object> res = new HashMap();
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenReturn(res);
		  ResponseEntity<Object> response =  dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType);
		  assertNotNull(response);
	  }
	  
	  @Test
	  public void getEdbApplicantInfoBadRequestExceptionTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenThrow(DataIntegrityViolationException.class);
		  assertThrows(BadRequestException.class, ()-> dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType));
	  }
	  
	  @Test
	  public void getEdbApplicantInfoNoDataFoundExceptionTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenThrow(NoDataFoundException.class);
		  assertThrows(NoDataFoundException.class, ()-> dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType));
	  }
	  
	  @Test
	  public void getEdbApplicantInfoDartDBExceptionTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenThrow(DartDBException.class);
		  assertThrows(DartDBException.class, ()-> dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType));
	  }
	  
	  @Test
	  public void getEdbApplicantInfoBadReqExceptionTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenThrow(BadRequestException.class);
		  assertThrows(BadRequestException.class, ()-> dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType));
	  }
	  
	  @Test
	  public void getEdbApplicantInfoNullpointerExceptionTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  edbPublicId = 1L; 
	      when(dartDBDAO.getPublicInfoFromDart(userId, contextId, edbPublicId, null)).thenThrow(NullPointerException.class);
		  assertThrows(DartDBException.class, ()-> dartPublicServiceImpl.getEdbApplicantInfo(userId, contextId,
			      projectId, edbPublicId, aplctType));
	  }
	  
	  @Test
	  public void retrieveApplicantHistoryTest() {
		  String userId="", contextId="",aplctType="";
	      Long projectId=1L,  applicantId = 1L; 
	      List<Public> existingPublic = new ArrayList();
	      Public pb = new Public();
	      pb.setPublicId(1L);
	      existingPublic.add(pb);
	      when(dartDbRepo.findAllPublicsAssociatedProject(projectId, applicantId)).thenReturn(existingPublic);;
	      Map<String, Object> res = new HashMap();
	      res.put("p_status_cd", "200Ok");
	      res.put("p_status_msg", "200Ok");
	      res.put("p_public_cur", new ArrayList());
	      res.put("p_public_hist_cur", new ArrayList());
	      res.put("p_addr_hist_cur", new ArrayList());
	      when(dartDBDAO.getApplicantDetails(userId, contextId, projectId, applicantId)).thenReturn(res);
	      Applicant applicant = new Applicant();
	      applicant.setEdbApplicantId(1L);
	      when(transformationService.transformPublicEntityToApplicant(userId, contextId,
	    	        null, new ArrayList(), applicantId)).thenReturn(applicant);
	      when(transformationService.transformPublicHistoryIntoApplicant(userId,
          contextId,  new ArrayList(),  new ArrayList())).thenReturn(applicant);
	      ResponseEntity<Object> response =dartPublicServiceImpl.retrieveApplicantHistory(userId, contextId,
			       projectId,  applicantId);
	      assertNotNull(response);
	  }
	 
}
