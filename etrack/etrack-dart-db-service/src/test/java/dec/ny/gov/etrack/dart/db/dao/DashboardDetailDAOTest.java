package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.DISTRICT_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.REGION_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.DartApplication;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DashboardDetailDAOTest {

	@Mock
	  @Qualifier("eTrackDartUnIssuedProcCall")
	  private SimpleJdbcCall eTrackDartUnIssuedProcCall;

	  @Mock
	  @Qualifier("eTrackDartDueAppsProcCall")
	  private SimpleJdbcCall eTrackDartDueAppsProcCall;

	  @Mock
	  @Qualifier("eTrackDartReviewAppsProcCall")
	  private SimpleJdbcCall eTrackDartReviewAppsProcCall;

	  @Mock
	  @Qualifier("eTrackGetReviewDetailsProcCall")
	  private SimpleJdbcCall eTrackGetReviewDetailsProcCall;

	  @Mock
	  @Qualifier("eTrackDartAplctDueAppsProcCall")
	  private SimpleJdbcCall eTrackDartAplctDueAppsProcCall;

	  @Mock
	  @Qualifier("eTrackDartSuspendedAppsProcCall")
	  private SimpleJdbcCall eTrackDartSuspendedAppsProcCall;

	  @Mock
	  @Qualifier("eTrackDartDisposedAppsProcCall")
	  private SimpleJdbcCall eTrackDartDisposedAppsProcCall;

	  @Mock
	  @Qualifier("eTrackDartEmergencyAppsProcCall")
	  private SimpleJdbcCall eTrackDartEmergencyAppsProcCall;
	  
	  @InjectMocks
	  private DashboardDetailDAO dashboardDetailDAO; 
	  
		String userId = "dxdev";
		String contextId = UUID.randomUUID().toString();
		Long projectId = 1l;
		Long publicId = 1l;
		Long edbDistrictId =1l;
		
	@Test
	public void retrieveDARTPendingApplicationsTest() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(DISTRICT_ID, 1L);
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartUnIssuedProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Mockito.lenient().when(eTrackDartUnIssuedProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartUnIssuedProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,dashboardDetailDAO.retrieveDARTPendingApplications(userId, contextId, projectId,1));
	  }

	
	@Test
	public void retrieveDARTPendingApplicationsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(DISTRICT_ID, 1L);
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartUnIssuedProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Mockito.lenient().when(eTrackDartUnIssuedProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartUnIssuedProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTPendingApplications(userId, contextId, projectId,1));
	  }

	@Test
	public void retrieveDARTPendingApplications100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(DISTRICT_ID, 1L);
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartUnIssuedProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Mockito.lenient().when(eTrackDartUnIssuedProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartUnIssuedProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartUnIssuedProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTPendingApplications(userId, contextId, projectId,1));
	  }
	
	@Test
	public void retrieveDARTDueAppsTest() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
		when(eTrackDartDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Mockito.lenient().when(eTrackDartDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,dashboardDetailDAO.retrieveDARTDueApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTDueAppsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    
		when(eTrackDartDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Mockito.lenient().when(eTrackDartDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTDueApps(userId, contextId));
	  }
	
	

	@Test
	public void retrieveDARTDueApps100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);
	    
		when(eTrackDartDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Mockito.lenient().when(eTrackDartDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTDueApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTDueAppsExceptionTest() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);
	    inputParam.put(DISTRICT_ID, 1);
		when(eTrackDartDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Mockito.lenient().when(eTrackDartDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTDueApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTAplctResponseDueAppsTest() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
		when(eTrackDartAplctDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Mockito.lenient().when(eTrackDartAplctDueAppsProcCall.returningResultSet(Mockito.any(), 
				Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartAplctDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,dashboardDetailDAO.retrieveDARTAplctResponseDueApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTAplctResponseDueAppsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    
		when(eTrackDartAplctDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Mockito.lenient().when(eTrackDartAplctDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartAplctDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTAplctResponseDueApps(userId, contextId));
	  }
	
	

	@Test
	public void retrieveDARTAplctResponseDueApps100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);	    
		when(eTrackDartAplctDueAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Mockito.lenient().when(eTrackDartAplctDueAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartAplctDueAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartAplctDueAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTAplctResponseDueApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTSuspendedAppsTest() {	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
		when(eTrackDartSuspendedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);
		Mockito.lenient().when(eTrackDartSuspendedAppsProcCall.returningResultSet(Mockito.any(), 
				Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartSuspendedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,dashboardDetailDAO.retrieveDARTSuspendedApps(userId, contextId));
	  }
	
	@Test
	public void retrieveDARTSuspendedAppsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    
		when(eTrackDartSuspendedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);

		Mockito.lenient().when(eTrackDartSuspendedAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartSuspendedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTSuspendedApps(userId, contextId));
	  }
	
	

	@Test
	public void retrieveDARTSuspendedApps100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);	    
		when(eTrackDartSuspendedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);

		Mockito.lenient().when(eTrackDartSuspendedAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartSuspendedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTSuspendedApps(userId, contextId));
	  }
	
//	@Test
//	public void findAllReviewProjectDetailsByUserIdTest() {
//	    
//	    Map<String, Object> inputParam = new HashMap<>();
//	    inputParam.put(USER_ID, userId);
//	    inputParam.put(REGION_ID, "1");
//		when(eTrackGetReviewDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
//				Mockito.any(),Mockito.any())).thenReturn(eTrackGetReviewDetailsProcCall);
//		Map<String, Object> result = new HashMap<>();
//		result.put("p_status_cd", 0l);
//		result.put("p_reqd_permit_form_cur", new ArrayList<>());
//		when(eTrackGetReviewDetailsProcCall.execute(inputParam)).thenReturn(result);
//
//		assertEquals(null,dashboardDetailDAO.findAllReviewProjectDetailsByUserId(userId, contextId,1));
//	  }
//	
	@Test
	public void findAllReviewProjectDetailsByUserIdNegative100Test() {	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackGetReviewDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetReviewDetailsProcCall);
		Mockito.lenient().when(eTrackGetReviewDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackGetReviewDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.findAllReviewProjectDetailsByUserId(userId, contextId,1));
	  }
	
	

	@Test
	public void findAllReviewProjectDetailsByUserId100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackGetReviewDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetReviewDetailsProcCall);
		Mockito.lenient().when(eTrackGetReviewDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackGetReviewDetailsProcCall.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()->dashboardDetailDAO.findAllReviewProjectDetailsByUserId(userId, contextId,1));
	  }
	
	@Test
	public void retrieveDARTDisposedAppsTest() {	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartDisposedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDisposedAppsProcCall);
		Mockito.lenient().when(eTrackDartDisposedAppsProcCall.returningResultSet(Mockito.any(), 
				Mockito.any())).thenReturn(eTrackDartDisposedAppsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDisposedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,dashboardDetailDAO.retrieveDARTDisposedApps(userId, contextId,1));
	  }
	
	@Test
	public void retrieveDARTDisposedAppsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartDisposedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDisposedAppsProcCall);

		Mockito.lenient().when(eTrackDartDisposedAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDisposedAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDisposedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTDisposedApps(userId, contextId,1));
	  }
	
	

	@Test
	public void retrieveDARTDisposedApps100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartDisposedAppsProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartDisposedAppsProcCall);

		Mockito.lenient().when(eTrackDartDisposedAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartSuspendedAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartDisposedAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveDARTDisposedApps(userId, contextId,1));
	  }
	
//	@Test
//	public void retrieveEmergencyAuthorizationAppsTest() {	    
//	    Map<String, Object> inputParam = new HashMap<>();
//	    inputParam.put(USER_ID, userId);
//	    inputParam.put(REGION_ID, 1);
//		when(eTrackDartEmergencyAppsProcCall.declareParameters( Mockito.any(), Mockito.any(),
//				Mockito.any(),Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);
//		Mockito.lenient().when(eTrackDartEmergencyAppsProcCall.returningResultSet(Mockito.any(), 
//				Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);
//		Map<String, Object> result = new HashMap<>();
//		result.put("p_status_cd", 0l);
//		result.put("p_reqd_permit_form_cur", new ArrayList<>());
//		when(eTrackDartEmergencyAppsProcCall.execute(inputParam)).thenReturn(result);
//
//		assertEquals(null,dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId));
//	  }
	
	@Test
	public void retrieveEmergencyAuthorizationAppsNegative100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartEmergencyAppsProcCall.declareParameters( Mockito.any(), Mockito.any(), 
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);

		Mockito.lenient().when(eTrackDartEmergencyAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartEmergencyAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId));
	  }
	
	

	@Test
	public void retrieveEmergencyAuthorizationApps100Test() {
	    
	    Map<String, Object> inputParam = new HashMap<>();	    
	    inputParam.put(USER_ID, userId);
	    inputParam.put(REGION_ID, 1);
		when(eTrackDartEmergencyAppsProcCall.declareParameters( Mockito.any(), Mockito.any(), 
				Mockito.any(),Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);

		Mockito.lenient().when(eTrackDartEmergencyAppsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartEmergencyAppsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackDartEmergencyAppsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dashboardDetailDAO.retrieveEmergencyAuthorizationApps(userId, contextId));
	  }
	
	
}

