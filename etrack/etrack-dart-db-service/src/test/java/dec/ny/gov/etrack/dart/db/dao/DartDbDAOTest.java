package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.COUNTY;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.DEC_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.MUNICIPALITY;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROGRAM_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROGRAM_TYPE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PROJECT_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.PUBLIC_ID;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_CODE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.STATUS_MESSAGE;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.TX_MAP;
import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.USER_ID;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.FacilityLRPDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartDbDAOTest {

	@Mock
	@Qualifier("eTrackFacilityInfoProc")
	private SimpleJdbcCall eTrackFacilityInfoProc;

	@Mock
	@Qualifier("eTrackGetPublicInfoProc")
	private SimpleJdbcCall eTrackGetPublicInfoProc;

	@Mock
	@Qualifier("eTrackGetFacilityInfoProc")
	private SimpleJdbcCall eTrackGetFacilityInfoProc;

	@Mock
	@Qualifier("eTrackGetDECIdProc")
	private SimpleJdbcCall eTrackGetDECIdProc;

	@Mock
	@Qualifier("eTrackGetDECIdByTxMapCall")
	private SimpleJdbcCall eTrackGetDECIdByTxMapCall;

	@Mock
	@Qualifier("eTrackApplicantSearchProc")
	private SimpleJdbcCall eTrackApplicantSearchProc;

	@Mock
	@Qualifier("eTrackOrgSearchProc")
	private SimpleJdbcCall eTrackOrgSearchProc;

	@Mock
	@Qualifier("matchedFacilityAddress")
	private SimpleJdbcCall matchedFacilityAddress;

	@Mock
	@Qualifier("getPublicInfoFromDart")
	private SimpleJdbcCall getPublicInfoFromDart;

	@Mock
	@Qualifier("getExistingPermitsFromDart")
	private SimpleJdbcCall getExistingPermitsFromDart;

	@Mock
	@Qualifier("getExpiredPermitsFromDart")
	private SimpleJdbcCall getExpiredPermitsFromDart;

	@Mock
	@Qualifier("getRegionIdByUserIdProcCall")
	private SimpleJdbcCall getRegionIdByUserIdProcCall;

	@Mock
	@Qualifier("getUsersByRoleTypeIdProcCall")
	private SimpleJdbcCall getUsersByRoleTypeIdProcCall;

	@Mock
	@Qualifier("getUsersWithValidEmailProcCall")
	private SimpleJdbcCall getUsersWithValidEmailProcCall;

	@Mock
	@Qualifier("eTrackGetReviewDetailsProcCall")
	private SimpleJdbcCall eTrackGetReviewDetailsProcCall;

	@Mock
	@Qualifier("eTrackStaffDetailsProcCall")
	private SimpleJdbcCall eTrackStaffDetailsProcCall;

	@Mock
	@Qualifier("eTrackDartDIMSRSupportDetailProcCall")
	private SimpleJdbcCall eTrackDartDIMSRSupportDetailProcCall;

	@Mock
	@Qualifier("eTrackDartPermitNarrativeDescProcCall")
	private SimpleJdbcCall eTrackDartPermitNarrativeDescProcCall;

	@Mock
	@Qualifier("eTrackPermitFormsProcCall")
	private SimpleJdbcCall eTrackPermitFormsProcCall;

	@Mock
	@Qualifier("enterpriseSupportDetailsProcCall")
	private SimpleJdbcCall enterpriseSupportDetailsProcCall;
	
	@InjectMocks
	private DartDbDAO dartDbDAO;
	
	String userId = "12345";
	String contextId = UUID.randomUUID().toString();
	Long projectId = 1l;
	Long publicId = 1l;
	Long edbDistrictId =1l;
			
	@Test
	public void geETrackFacilityDetailsTest() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertNotNull(dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	
	@Test
	public void geETrackFacilityDetailsStatus100Test() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	@Test
	public void geETrackFacilityDetailsStatusNegative100Test() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd",-100l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	@Test
	public void geETrackFacilityDetailsStatus1Test() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd",1l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	@Test
	public void geETrackFacilityDetailsStatus2Test() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd",2l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	@Test
	public void geETrackFacilityDetailsStatu31Test() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd",3l);
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	@Test
	public void geETrackFacilityDetailsDartDBExceptionTest() {
		when(eTrackFacilityInfoProc.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		when(eTrackFacilityInfoProc.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackFacilityInfoProc);
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		inputParam.put(USER_ID, userId);
		when(eTrackFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()-> dartDbDAO.geETrackFacilityDetails(userId, contextId, projectId));
	}
	
	
	@Test
	public void getApplicantDetailsStatus0Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PUBLIC_ID, publicId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		when(eTrackGetPublicInfoProc.execute(inputParam)).thenReturn(result);

		when(getPublicInfoFromDart.declareParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getPublicInfoFromDart);
		Map<String, Object> inputParam2 = new HashMap<>();
		inputParam2.put(DartDBConstants.PUBLIC_ID, publicId);
		inputParam2.put(USER_ID, userId);
		inputParam2.put(DartDBConstants.APPLICANT_TYPE, null);
		when(getPublicInfoFromDart.execute(inputParam2)).thenReturn(result);
		assertNotNull(dartDbDAO.getApplicantDetails(userId, contextId, projectId, publicId));
	}
	
	
	@Test
	public void getApplicantDetailsStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PUBLIC_ID, publicId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		when(eTrackGetPublicInfoProc.execute(inputParam)).thenReturn(result);
		
		assertThrows(DartDBException.class, ()->dartDbDAO.getApplicantDetails(userId, contextId, projectId, publicId));
	}
	
	@Test
	public void getApplicantDetailStatusNot100sTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PUBLIC_ID, publicId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 600l);
		when(eTrackGetPublicInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()->dartDbDAO.getApplicantDetails(userId, contextId, projectId, publicId));
	}
	
	@Test
	public void getApplicantDetailExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PUBLIC_ID, publicId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 600l);
		when(eTrackGetPublicInfoProc.execute(inputParam)).thenThrow(NullPointerException.class);
		assertThrows(DartDBException.class, ()->dartDbDAO.getApplicantDetails(userId, contextId, projectId, publicId));
	}
	
	@Test
	public void getApplicantDetaiDartDBExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PUBLIC_ID, publicId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 600l);
		when(eTrackGetPublicInfoProc.execute(inputParam)).thenThrow(DartDBException.class);
		assertThrows(DartDBException.class, ()->dartDbDAO.getApplicantDetails(userId, contextId, projectId, publicId));
	}
	
	@Test
	public void findFacilityHistoryDetailTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		when(eTrackGetFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertNotNull(dartDbDAO.findFacilityHistoryDetail(userId, contextId, projectId));
	}
	
	@Test
	public void findFacilityHistoryDetailStatus1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		when(eTrackGetFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertNotNull(dartDbDAO.findFacilityHistoryDetail(userId, contextId, projectId));
	}
	
	@Test
	public void findFacilityHistoryDetailStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		when(eTrackGetFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()->dartDbDAO.findFacilityHistoryDetail(userId, contextId, projectId));
	}
	
	@Test
	public void findFacilityHistoryDetailStatuse100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		when(eTrackGetFacilityInfoProc.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()->dartDbDAO.findFacilityHistoryDetail(userId, contextId, projectId));
	}
	
	@Test
	public void findFacilityHistoryDetailExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROJECT_ID, projectId);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		when(eTrackGetFacilityInfoProc.execute(inputParam)).thenThrow(NullPointerException.class);
		assertThrows(DartDBException.class, ()->dartDbDAO.findFacilityHistoryDetail(userId, contextId, projectId));
	}
	
	
	@Test
	public void retrieveExpiredApplicationsToExtendFromEnterpriseTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 0l);
			when(getExpiredPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExpiredPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertNotNull(dartDbDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	
	@Test
	public void retrieveExpiredApplicationsToExtendFromEnterpriseStatusNeative100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", -100l);
			when(getExpiredPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExpiredPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveExpiredApplicationsToExtendFromEnterpriseStatus100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExpiredPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExpiredPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveExpiredApplicationsToExtendFromEnterpriseNoDataFoundExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExpiredPermitsFromDart.execute(inputParam)).thenThrow(NoDataFoundException.class);
		when(getExpiredPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(NoDataFoundException.class, ()->dartDbDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveExpiredApplicationsToExtendFromEnterpriseDartDBExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExpiredPermitsFromDart.execute(inputParam)).thenThrow(NullPointerException.class);
		when(getExpiredPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveExpiredApplicationsToExtendFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	
	@Test
	public void retrieveModExtendEligiblePermitsFromEnterpriseTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 0l);
			when(getExistingPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExistingPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExistingPermitsFromDart);
		assertNotNull(dartDbDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	
	@Test
	public void retrieveModExtendEligiblePermitsFromEnterpriseStatusNeative100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", -100l);
			when(getExistingPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExistingPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveModExtendEligiblePermitsFromEnterpriseStatus100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExistingPermitsFromDart.execute(inputParam)).thenReturn(result);
		when(getExistingPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveModExtendEligiblePermitsFromEnterpriseNoDataFoundExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExistingPermitsFromDart.execute(inputParam)).thenThrow(NoDataFoundException.class);
		when(getExistingPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(NoDataFoundException.class, ()->dartDbDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void retrieveModExtendEligiblePermitsFromEnterpriseDartDBExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		    inputParam.put(DartDBConstants.DISTRICT_ID, edbDistrictId);
		    inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(getExistingPermitsFromDart.execute(inputParam)).thenThrow(NullPointerException.class);
		when(getExistingPermitsFromDart.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getExpiredPermitsFromDart);
		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveModExtendEligiblePermitsFromEnterprise(userId, contextId, edbDistrictId));
	}
	
	@Test
	public void findDECIdByTaxMapTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 0l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenReturn(result);
		when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertNull(dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	
	@Test
	public void findDECIdByTaxMapStatusNeative100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", -100l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenReturn(result);
			when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	@Test
	public void findDECIdByTaxMapStatus100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenReturn(result);
			when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	@Test
	public void findDECIdByTaxMapStatus1Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 1l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenReturn(result);
			when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertThrows(NoDataFoundException.class, ()->dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	@Test
	public void findDECIdByTaxMapNoDataFoundExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenThrow(NoDataFoundException.class);
			when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertThrows(NoDataFoundException.class, ()->dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	@Test
	public void findDECIdByTaxMapeDartDBExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(TX_MAP, "");
	      inputParam.put(COUNTY, "");
	      inputParam.put(MUNICIPALITY, "");
	      inputParam.put(USER_ID, userId);
		    Map<String, Object> result = new HashMap<>();
			result.put("p_status_cd", 100l);
			when(eTrackGetDECIdByTxMapCall.execute(inputParam)).thenThrow(NullPointerException.class);
			when(eTrackGetDECIdByTxMapCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdByTxMapCall);
		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByTaxMap(userId, contextId, "", "", ""));
	}
	
	@Test
	public void findDECIdByProgramTypeTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROGRAM_TYPE, "");
		inputParam.put(PROGRAM_ID, "");
		inputParam.put(USER_ID, userId);
		when(eTrackGetDECIdProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_facility_cur", new ArrayList<>());
		when(eTrackGetDECIdProc.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.findDECIdByProgramType(userId, contextId, "", ""));
	}
	
	@Test
	public void findDECIdByProgramTypeStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROGRAM_TYPE, "");
		inputParam.put(PROGRAM_ID, "");
		inputParam.put(USER_ID, userId);
		when(eTrackGetDECIdProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		when(eTrackGetDECIdProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByProgramType(userId, contextId, "", ""));
	}
	
	@Test
	public void findDECIdByProgramTypeStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROGRAM_TYPE, "");
		inputParam.put(PROGRAM_ID, "");
		inputParam.put(USER_ID, userId);
		when(eTrackGetDECIdProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		when(eTrackGetDECIdProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByProgramType(userId, contextId, "", ""));
	}
	@Test
	public void findDECIdByProgramTypeStatus1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROGRAM_TYPE, "");
		inputParam.put(PROGRAM_ID, "");
		inputParam.put(USER_ID, userId);
		when(eTrackGetDECIdProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		when(eTrackGetDECIdProc.execute(inputParam)).thenReturn(result);

		assertThrows(NoDataFoundException.class, ()->dartDbDAO.findDECIdByProgramType(userId, contextId, "", ""));
	}
	
	@Test
	public void findDECIdByProgramTypeException1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(PROGRAM_TYPE, "");
		inputParam.put(PROGRAM_ID, "");
		inputParam.put(USER_ID, userId);
		when(eTrackGetDECIdProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(eTrackGetDECIdProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		when(eTrackGetDECIdProc.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.findDECIdByProgramType(userId, contextId, "", ""));
	}
	
	@Test
	public void searchAllMatchedApplicantsTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, "");
		inputParam.put(USER_ID, userId);

		when(eTrackApplicantSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackApplicantSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackApplicantSearchProc.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.searchAllMatchedApplicants(userId, contextId, "", "", "", ""));
	}
	
	@Test
	public void searchAllMatchedApplicantsStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, "");
		inputParam.put(USER_ID, userId);

		when(eTrackApplicantSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackApplicantSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackApplicantSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedApplicants(userId, contextId, "", "", "", ""));
	}
	
	@Test
	public void searchAllMatchedApplicantsStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, "");
		inputParam.put(USER_ID, userId);

		when(eTrackApplicantSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackApplicantSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackApplicantSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedApplicants(userId, contextId, "", "", "", ""));
	}
	@Test
	void searchAllMatchedApplicantsStatus1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, "");
		inputParam.put(USER_ID, userId);

		when(eTrackApplicantSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackApplicantSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackApplicantSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(BadRequestException.class, ()->dartDbDAO.searchAllMatchedApplicants(userId, contextId, "", "", "", ""));
	}
	
	@Test
	void searchAllMatchedApplicantsException1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH, "");
		inputParam.put(DartDBConstants.FIRST_NAME_SEARCH_PATTERN, "");
		inputParam.put(DartDBConstants.LAST_NAME_SEARCH_PATTERN, "");
		inputParam.put(USER_ID, userId);

		when(eTrackApplicantSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackApplicantSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackApplicantSearchProc.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedApplicants(userId, contextId, "", "", "", ""));
	}
	
	@Test
	void searchAllMatchedPublicOrganizationsTest() {
		  Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, "");
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, "");
	      inputParam.put(DartDBConstants.APPLICANT_TYPE, "");
	      inputParam.put(USER_ID, userId);

		when(eTrackOrgSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackOrgSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackOrgSearchProc.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.searchAllMatchedPublicOrganizations(userId, contextId, "", "", ""));
	}
	
	@Test
	public void searchAllMatchedPublicOrganizationsStatusNegative100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, "");
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, "");
	      inputParam.put(DartDBConstants.APPLICANT_TYPE, "");
	      inputParam.put(USER_ID, userId);

		when(eTrackOrgSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackOrgSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackOrgSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedPublicOrganizations(userId, contextId, "", "", ""));
	}
	
	@Test
	public void searchAllMatchedPublicOrganizationsStatus100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, "");
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, "");
	      inputParam.put(DartDBConstants.APPLICANT_TYPE, "");
	      inputParam.put(USER_ID, userId);

		when(eTrackOrgSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackOrgSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackOrgSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedPublicOrganizations(userId, contextId, "", "", ""));
	}
	
	
	@Test
	public void searchAllMatchedPublicOrganizationsStatus1Test() {
		  Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, "");
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, "");
	      inputParam.put(DartDBConstants.APPLICANT_TYPE, "");
	      inputParam.put(USER_ID, userId);

		when(eTrackOrgSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackOrgSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackOrgSearchProc.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedPublicOrganizations(userId, contextId, "", "", ""));
	}
	
	@Test
	void searchAllMatchedPublicOrganizationsExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH, "");
	      inputParam.put(DartDBConstants.PUBLIC_NAME_SEARCH_PATTERN, "");
	      inputParam.put(DartDBConstants.APPLICANT_TYPE, "");
	      inputParam.put(USER_ID, userId);

		when(eTrackOrgSearchProc.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eTrackOrgSearchProc);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("p_results_cur", new ArrayList<>());
		when(eTrackOrgSearchProc.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.searchAllMatchedPublicOrganizations(userId, contextId, "", "", ""));
	}

	
	@Test
	void getMatchedFacilityAddressTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(DartDBConstants.ADDRESS_LINE, "");
	      inputParam.put(DartDBConstants.CITY, "");
	      inputParam.put(USER_ID, userId);

		when(matchedFacilityAddress.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(matchedFacilityAddress);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_fac_cur", new ArrayList<>());
		when(matchedFacilityAddress.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.getMatchedFacilityAddress(userId, contextId, "", ""));
	}
	
	@Test
	public void getMatchedFacilityAddressStatusNegative100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(DartDBConstants.ADDRESS_LINE, "");
	      inputParam.put(DartDBConstants.CITY, "");
	      inputParam.put(USER_ID, userId);

		when(matchedFacilityAddress.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(matchedFacilityAddress);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_fac_cur", new ArrayList<>());
		when(matchedFacilityAddress.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.getMatchedFacilityAddress(userId, contextId, "", ""));
	}
	
	@Test
	public void getMatchedFacilityAddressStatus100Test() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(DartDBConstants.ADDRESS_LINE, "");
	      inputParam.put(DartDBConstants.CITY, "");
	      inputParam.put(USER_ID, userId);

		when(matchedFacilityAddress.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(matchedFacilityAddress);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_fac_cur", new ArrayList<>());
		when(matchedFacilityAddress.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.getMatchedFacilityAddress(userId, contextId, "", ""));
	}
	
	@Test
	public void getMatchedFacilityAddressExceptionTest() {
		  Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(DartDBConstants.ADDRESS_LINE, "");
	      inputParam.put(DartDBConstants.CITY, "");
	      inputParam.put(USER_ID, userId);

		when(matchedFacilityAddress.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(matchedFacilityAddress);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_fac_cur", new ArrayList<>());
		when(matchedFacilityAddress.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.getMatchedFacilityAddress(userId, contextId, "", ""));
	}
	
	@Test
	public void getUserRegionIdTest() {
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);

		when(getRegionIdByUserIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
				)).thenReturn(getRegionIdByUserIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_region_id", 1l);
		when(getRegionIdByUserIdProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.getUserRegionId(userId, contextId));
	}
	
	@Test
	public void getUserRegionIdStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);

		when(getRegionIdByUserIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
				)).thenReturn(getRegionIdByUserIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_region_id", 1l);
		when(getRegionIdByUserIdProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.getUserRegionId(userId, contextId));
	}
	
	@Test
	public void getUserRegionIdStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);

		when(getRegionIdByUserIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
				)).thenReturn(getRegionIdByUserIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_region_id", 1l);
		when(getRegionIdByUserIdProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.getUserRegionId(userId, contextId));
	}
	
	@Test
	public void getUserRegionIdExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);

		when(getRegionIdByUserIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
				)).thenReturn(getRegionIdByUserIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_region_id", 1l);
		when(getRegionIdByUserIdProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.getUserRegionId(userId, contextId));
	}
	
	@Test
	public void findAllTheUsersByRoleTypeIdTest() {
		 Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.REGION_ID, 1);
	      inputParam.put(DartDBConstants.ROLE_TYPE_ID, 1);

		when(getUsersByRoleTypeIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(getUsersByRoleTypeIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_region_user_cur", new ArrayList<>());
		when(getUsersByRoleTypeIdProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.findAllTheUsersByRoleTypeId(userId, contextId,1,1));
	}
	
	@Test
	public void findAllTheUsersByRoleTypeIdStatusNegative100Test() {
		 Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.REGION_ID, 1);
	      inputParam.put(DartDBConstants.ROLE_TYPE_ID, 1);

		when(getUsersByRoleTypeIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(getUsersByRoleTypeIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_region_user_cur", new ArrayList<>());
		when(getUsersByRoleTypeIdProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersByRoleTypeId(userId, contextId,1,1));
	}
	
	@Test
	public void findAllTheUsersByRoleTypeIdStatus100Test() {
		 Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.REGION_ID, 1);
	      inputParam.put(DartDBConstants.ROLE_TYPE_ID, 1);

		when(getUsersByRoleTypeIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(getUsersByRoleTypeIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_region_user_cur", new ArrayList<>());
		when(getUsersByRoleTypeIdProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersByRoleTypeId(userId, contextId,1,1));
	}
	
	@Test
	public void findAllTheUsersByRoleTypeIdExceptionTest() {
		 Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(DartDBConstants.REGION_ID, 1);
	      inputParam.put(DartDBConstants.ROLE_TYPE_ID, 1);

		when(getUsersByRoleTypeIdProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(getUsersByRoleTypeIdProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_region_user_cur", new ArrayList<>());
		when(getUsersByRoleTypeIdProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersByRoleTypeId(userId, contextId,1,1));
	}
	
	@Test
	public void findAllTheUsersWithValidEmailAddressTest() {
		 Map<String, Object> inputParam = new HashMap<>();

		when(getUsersWithValidEmailProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getUsersWithValidEmailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_email_user_cur", new ArrayList<>());
		when(getUsersWithValidEmailProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.findAllTheUsersWithValidEmailAddress(userId, contextId));
	}
	
	@Test
	public void findAllTheUsersWithValidEmailAddressStatusNegative100Test() {
		 Map<String, Object> inputParam = new HashMap<>();

		when(getUsersWithValidEmailProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getUsersWithValidEmailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_email_user_cur", new ArrayList<>());
		when(getUsersWithValidEmailProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersWithValidEmailAddress(userId, contextId));
	}
	
	@Test
	public void findAllTheUsersWithValidEmailAddressStatus100Test() {
		 Map<String, Object> inputParam = new HashMap<>();

		when(getUsersWithValidEmailProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getUsersWithValidEmailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_email_user_cur", new ArrayList<>());
		when(getUsersWithValidEmailProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersWithValidEmailAddress(userId, contextId));
	}
	
	@Test
	public void findAllTheUsersWithValidEmailAddressExceptionTest() {
		 Map<String, Object> inputParam = new HashMap<>();

		when(getUsersWithValidEmailProcCall.declareParameters( Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getUsersWithValidEmailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_email_user_cur", new ArrayList<>());
		when(getUsersWithValidEmailProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.findAllTheUsersWithValidEmailAddress(userId, contextId));
	}
	
	@Test
	public void retrieveStaffDetailsByUserIdTest() {
		 Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(USER_ID, userId);
		when(eTrackStaffDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackStaffDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackStaffDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId));
	}
	
	@Test
	public void retrieveStaffDetailsByUserIdStatusNegative100Test() {
		 Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(USER_ID, userId);
		when(eTrackStaffDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackStaffDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackStaffDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId));
	}
	
	@Test
	public void retrieveStaffDetailsByUserIdStatus1Test() {
		 Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(USER_ID, userId);
		when(eTrackStaffDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackStaffDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackStaffDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId));
	}
	
	@Test
	public void retrieveStaffDetailsByUserIdStatus100Test() {
		 Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(USER_ID, userId);
		when(eTrackStaffDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackStaffDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackStaffDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId));
	}
	
	@Test
	public void retrieveStaffDetailsByUserIdExceptionTest() {
		 Map<String, Object> inputParam = new HashMap<>();
		  inputParam.put(USER_ID, userId);
		when(eTrackStaffDetailsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackStaffDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackStaffDetailsProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId));
	}
	
	@Test
	public void retrieveSupportDetailsForDIMSRTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DEC_ID, "");
		when(eTrackDartDIMSRSupportDetailProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		when(eTrackDartDIMSRSupportDetailProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackDartDIMSRSupportDetailProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.retrieveSupportDetailsForDIMSR(userId, contextId, ""));
	}
	
	@Test
	public void retrieveSupportDetailsForDIMSRStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DEC_ID, "");
		when(eTrackDartDIMSRSupportDetailProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		when(eTrackDartDIMSRSupportDetailProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackDartDIMSRSupportDetailProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveSupportDetailsForDIMSR(userId, contextId, ""));
	}
	
	@Test
	public void retrieveSupportDetailsForDIMSRStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DEC_ID, "");
		when(eTrackDartDIMSRSupportDetailProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		when(eTrackDartDIMSRSupportDetailProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackDartDIMSRSupportDetailProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveSupportDetailsForDIMSR(userId, contextId, ""));
	}
	
	@Test
	public void retrieveSupportDetailsForDIMSRStatus1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DEC_ID, "");
		when(eTrackDartDIMSRSupportDetailProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		when(eTrackDartDIMSRSupportDetailProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackDartDIMSRSupportDetailProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveSupportDetailsForDIMSR(userId, contextId, ""));
	}
	
	@Test
	public void retrieveSupportDetailsForDIMSRStatusExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DEC_ID, "");
		when(eTrackDartDIMSRSupportDetailProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		when(eTrackDartDIMSRSupportDetailProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackDartDIMSRSupportDetailProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("P_USER_DETAIL_CUR", new ArrayList<>());
		when(eTrackDartDIMSRSupportDetailProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveSupportDetailsForDIMSR(userId, contextId, ""));
	}
	
	@Test
	public void retrievePermitApplicationFormTest() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(DartDBConstants.PROJECT_ID, projectId);
		when(eTrackPermitFormsProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(eTrackPermitFormsProcCall);

		when(eTrackPermitFormsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackPermitFormsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackPermitFormsProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.retrievePermitApplicationForm(userId, contextId, projectId));
	}
	
	@Test
	public void retrievePermitApplicationFormStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(DartDBConstants.PROJECT_ID, projectId);
		when(eTrackPermitFormsProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(eTrackPermitFormsProcCall);

		when(eTrackPermitFormsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackPermitFormsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackPermitFormsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrievePermitApplicationForm(userId, contextId, projectId));
	}
	
	@Test
	public void retrievePermitApplicationFormStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(DartDBConstants.PROJECT_ID, projectId);
		when(eTrackPermitFormsProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(eTrackPermitFormsProcCall);

		when(eTrackPermitFormsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackPermitFormsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackPermitFormsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrievePermitApplicationForm(userId, contextId, projectId));
	}
	
	@Test
	public void retrievePermitApplicationFormStatusExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(DartDBConstants.PROJECT_ID, projectId);
		when(eTrackPermitFormsProcCall.declareParameters( Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any()
				)).thenReturn(eTrackPermitFormsProcCall);

		when(eTrackPermitFormsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackPermitFormsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(eTrackPermitFormsProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrievePermitApplicationForm(userId, contextId, projectId));
	}
	
	@Test
	public void retrieveEnterpriseSupportDetailsForVWTest() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(PROJECT_ID, projectId);

		when(enterpriseSupportDetailsProcCall.declareParameters(
		    Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()

				)).thenReturn(enterpriseSupportDetailsProcCall);

		when(enterpriseSupportDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(enterpriseSupportDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(enterpriseSupportDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(dartDbDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId));
	}
	
	@Test
	public void retrieveEnterpriseSupportDetailsForVWStatus100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(PROJECT_ID, projectId);
		when(enterpriseSupportDetailsProcCall.declareParameters(
		    Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()
				)).thenReturn(enterpriseSupportDetailsProcCall);

		when(enterpriseSupportDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(enterpriseSupportDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100L);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(enterpriseSupportDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(NoDataFoundException.class, ()->dartDbDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId));
	}
	
	@Test
	public void retrieveEnterpriseSupportDetailsForVWStatus200Test() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(PROJECT_ID, projectId);
		when(enterpriseSupportDetailsProcCall.declareParameters( 
		    Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()
				)).thenReturn(enterpriseSupportDetailsProcCall);

		when(enterpriseSupportDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(enterpriseSupportDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 200L);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(enterpriseSupportDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(NoDataFoundException.class, ()->dartDbDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId));
	}
	
	@Test
	public void retrieveEnterpriseSupportDetailsForVWStatus300Test() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(PROJECT_ID, projectId);
		when(enterpriseSupportDetailsProcCall.declareParameters(
		    Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()
				)).thenReturn(enterpriseSupportDetailsProcCall);

		when(enterpriseSupportDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(enterpriseSupportDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 300l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(enterpriseSupportDetailsProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId));
	}
	
	@Test
	public void retrieveEnterpriseSupportDetailsForVWExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		 inputParam.put(USER_ID, userId);
	      inputParam.put(PROJECT_ID, projectId);
		when(enterpriseSupportDetailsProcCall.declareParameters(
		    Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any()
				)).thenReturn(enterpriseSupportDetailsProcCall);

		when(enterpriseSupportDetailsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(enterpriseSupportDetailsProcCall);

		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 300l);
		result.put("p_reqd_permit_form_cur", new ArrayList<>());
		when(enterpriseSupportDetailsProcCall.execute(inputParam)).thenThrow(NullPointerException.class);

		assertThrows(DartDBException.class, ()->dartDbDAO.retrieveEnterpriseSupportDetailsForVW(userId, contextId, projectId));
	}
}
