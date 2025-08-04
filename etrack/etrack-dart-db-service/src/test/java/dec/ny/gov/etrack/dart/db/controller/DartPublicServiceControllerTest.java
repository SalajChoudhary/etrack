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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.PublicSummary;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.Applicant;
import dec.ny.gov.etrack.dart.db.model.ApplicantType;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.service.DartPublicService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartPublicServiceControllerTest {

	
	  @Mock
	  private DartPublicService dartPublicService;

	  @InjectMocks
	  private DartPublicServiceController dartPublicServiceController;
	  
	  @Test
		public void getApplicantsTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.retrieveApplicants(Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(),Mockito.anyLong(),Mockito.anyInt())).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getApplicants("userId", 123L,
					dec.ny.gov.etrack.dart.db.model.ApplicantType.P,1);
			assertNotNull(object);
		}
	  
	  @Test
		public void getApplicantsBadExceptionTest() {
			assertThrows(BadRequestException.class, ()-> dartPublicServiceController.getApplicants(null, null, null, null));
		}
	  
	  @Test
		public void getPublicsSummaryTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.retrieveAllPublicsAssociatedWithThisProject(Mockito.anyString(), Mockito.anyString(),
					Mockito.anyLong())).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getPublicsSummary("userId", 123L);
			assertNotNull(object);
		}
	  
	  @Test
		public void getPublicsSummaryBadExceptionTest() {
			assertThrows(BadRequestException.class, ()-> dartPublicServiceController.getPublicsSummary(null, null));
		}
	  
	  @Test
		public void getMatchedApplicantsFromEnterpriseTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.getAllMatchedApplicants("dxdevada", "Context",
					dec.ny.gov.etrack.dart.db.model.PublicType.I, "firstname",
					dec.ny.gov.etrack.dart.db.model.SearchPatternEnum.S,"lastName", 
					dec.ny.gov.etrack.dart.db.model.SearchPatternEnum.E)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getMatchedApplicantsFromEnterprise("userId","", 
					dec.ny.gov.etrack.dart.db.model.SearchPatternEnum.S,"",
					dec.ny.gov.etrack.dart.db.model.SearchPatternEnum.E,
					dec.ny.gov.etrack.dart.db.model.PublicType.I);
			assertEquals(null, object);
		}
	  
//	  @Test
//		public void getMatchedApplicantsFromEnterpriseBadExceptionTest() {
//			assertThrows(BadRequestException.class, ()-> dartPublicServiceController.
//					getMatchedApplicantsFromEnterprise(null,"", 
//					null,"",null,null));
//		}
	  
	  @Test
		public void getApplicantInfoTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.getApplicantInfo("dxdevada", "Context",123L, 1L,null)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getApplicantInfo("userId",123L, 1L);
			assertEquals(null, object);
		}
	  
	  @Test
		public void getApplicantInfoBadExceptionTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.getApplicantInfo("dxdevada", "Context",123L, 1L,null)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getApplicantInfo("userId",123L, 1L);
			assertThrows(BadRequestException.class, ()-> dartPublicServiceController.getApplicantInfo("userId",123L, null));
		}
	  
	  @Test
		public void getEdbApplicantInfoTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.getEdbApplicantInfo("dxdevada", "Context",123L, 1L,null)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getEdbApplicantInfo("userId",123L, 1L,
					dec.ny.gov.etrack.dart.db.model.PublicType.I);
			assertEquals(null, object);
		}
	  
	  @Test
		public void getContactInfoTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.getApplicantInfo("dxdevada", 
					"Context",123L,1L,"M")).thenReturn(dartPublic);
			Object object = dartPublicServiceController.getContactInfo("userId",123L, 1L,
					dec.ny.gov.etrack.dart.db.model.PublicType.M);
			assertEquals(null, object);
		}
	  
	  @Test
		public void applicantHistoryTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.retrieveApplicantHistory("dxdevada", "Context",123L, 1L)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.applicantHistory("userId",123L, 1L);
			assertEquals(null, object);
		}
	  
	  @Test
		public void validateEdbPublicIdTest() {
		  ResponseEntity<Object> dartPublic =  new ResponseEntity<Object>(HttpStatus.OK);
			Mockito.lenient().when(dartPublicService.validateEdbPublicId("dxdevada", "Context",123L, 1L,1L)).thenReturn(dartPublic);
			Object object = dartPublicServiceController.validateEdbPublicId("userId",123L, 1L,1L);
			assertEquals(null, object);
		}
	  
}
