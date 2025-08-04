package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddressHistory;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.FacilityHistory;
import dec.ny.gov.etrack.dart.db.service.DartFacilityService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartFacilityServiceImplTest {
	
	  @Mock
	  private DartDbDAO dartDBDAO;
	  @Mock
	  private TransformationService transformationService;
	  
	  @InjectMocks
	  DartFacilityService dartFacilityService = new DartFacilityServiceImpl();


	@Test
	public void getDECIDByProgramTypeEmptyTest() throws Exception {
		String userId = ""; String contextId = ""; 		
		List<FacilityAddress> decIDlist = new ArrayList<>();		
		when(dartDBDAO.findDECIdByProgramType(userId,contextId,"","")).thenReturn(decIDlist);		
		Assert.assertThrows(RuntimeException.class, () ->  dartFacilityService.getDECIDByProgramType("", "","",""));
	}
	
	@Test
	public void getDECIDByTaxMapTest() throws Exception {
		String userId = ""; String contextId = ""; 		
		List<FacilityAddress> decIDlist = new ArrayList<>();
		FacilityAddress address = new  FacilityAddress();
		address.setCountry("USA");
		decIDlist.add(address);
		when(dartDBDAO.findDECIdByTaxMap(userId,contextId,"","","")).thenReturn(decIDlist);		
		@SuppressWarnings("unchecked")
		List<FacilityAddress> actual =  (List<FacilityAddress>) dartFacilityService.getDECIDByTaxMap("", "","","","");
		assertEquals(decIDlist.size(), actual.size());
	}
	
	@Test
	public void getDECIDByTaxMapEmptyTest() throws Exception {
		String userId = ""; String contextId = ""; 		
		List<FacilityAddress> decIDlist = new ArrayList<>();
		when(dartDBDAO.findDECIdByTaxMap(userId,contextId,"","","")).thenReturn(decIDlist);		
		Assert.assertThrows(RuntimeException.class, () ->  dartFacilityService.getDECIDByTaxMap("", "","","",""));
	}
	
	@Test
	public void getEtrackFacilityDetailsTest() {
		Map<String, Object> response = new HashedMap();
		when(dartDBDAO.geETrackFacilityDetails("", "", 100L)).thenReturn(response);
		FacilityDetail result =  dartFacilityService.getEtrackFacilityDetails("", "", Long.valueOf("100"));
		assertNull(result);		
	}
	
	
	
	@Test
	public void getAllMatchedFacilitiesTest() {
		List<FacilityAddress> facilityAddress = new ArrayList<>();
		when(dartDBDAO.getMatchedFacilityAddress("", "", "","")).thenReturn(facilityAddress);
		 List<FacilityDetail> result =  (List<FacilityDetail>) dartFacilityService.getAllMatchedFacilities("", "", "","");
		assertEquals(facilityAddress,result);		
	}
	
	
	@Test
	public void retrieveFacilityHistoryEmptyTest() {
	   Map<String, Object> facilityData = new HashedMap();
	   Map<String, Object> result = new LinkedHashMap<>();
	   @SuppressWarnings("unchecked")
	    List<FacilityDetail> facilityDetailsList = new ArrayList<>();
	   when(dartDBDAO.findFacilityHistoryDetail("", "", 100L)).thenReturn(facilityData);
	   ResponseEntity<Object> retrieveFacilityHistory = dartFacilityService.retrieveFacilityHistory("", "", 100L);
	   assertNotNull(retrieveFacilityHistory);
	}
	
//	@Test
//	public void retrieveFacilityHistoryTest() {
//	   Map<String, Object> facilityData = new HashedMap();
//	   Map<String, Object> result = new LinkedHashMap<>();
//	   @SuppressWarnings("unchecked")
//	    List<FacilityDetail> facilityDetailsList = new ArrayList<>();
//	   facilityDetailsList.add((FacilityDetail) facilityData);
//	   when(dartDBDAO.findFacilityHistoryDetail("", "", 100L)).thenReturn(facilityData);
//	   ResponseEntity<Object> retrieveFacilityHistory = dartFacilityService.retrieveFacilityHistory("", "", 100L);
//	   assertNotNull(retrieveFacilityHistory);
//	}

	
	
	@Test
	public void formatDECIdTest() {
//	 String decId = "TESTDECIDTEST";
//	 String formattedId = dartFacilityService.formatDECId(decId);
		FacilityDetail facilityDetail = new FacilityDetail();
		facilityDetail.setDecId("TESTDECID");
		FacilityHistory facilityHistory = new FacilityHistory();
		facilityHistory.setHDecId("");
		facilityHistory.setHFacilityName("");
		FacilityAddressHistory facilityAddressHistory = new FacilityAddressHistory();
		facilityAddressHistory.setHStreet1("dane-lane-jane");
	    @SuppressWarnings("unchecked")
		Map<String, Object> facilityData = new HashedMap();
	    List<FacilityDetail> facilityDetailsList = new ArrayList<>();
	    List<FacilityHistory> facilityHistoryList = new ArrayList<>();
	    List<FacilityAddressHistory> facilityAddressHistoryDetails = new ArrayList<>();
	    facilityDetailsList.add(facilityDetail);
	    facilityHistoryList.add(facilityHistory);
	    facilityAddressHistoryDetails.add(facilityAddressHistory);
	    facilityData.put(DartDBConstants.FACILITY_CURSOR, facilityDetailsList);
	    facilityData.put(DartDBConstants.FACILITY_HIST_CURSOR, facilityHistoryList);
	    facilityData.put(DartDBConstants.FACILITY_ADDR_HIST_CURSOR, facilityAddressHistoryDetails);
	    when(dartDBDAO.findFacilityHistoryDetail("", "", 100L)).thenReturn(facilityData);
	    ResponseEntity<Object> retrieveFacilityHistory = dartFacilityService.retrieveFacilityHistory("", "", 100L);
	    assertNotNull(retrieveFacilityHistory);
	}

	@Test
	public void formatDECIdBranchTest() {
//	 String decId = "TESTDECIDTEST";
//	 String formattedId = dartFacilityService.formatDECId(decId);
		FacilityDetail facilityDetail = new FacilityDetail();
		facilityDetail.setDecId("TESTDECID");
		FacilityHistory facilityHistory = new FacilityHistory();
		facilityHistory.setHDecId("");
		facilityHistory.setHFacilityName("");
		FacilityAddressHistory facilityAddressHistory = new FacilityAddressHistory();
		facilityAddressHistory.setHStreet1("dane-lane-jane");
	    @SuppressWarnings("unchecked")
		Map<String, Object> facilityData = new HashedMap();
	    List<FacilityDetail> facilityDetailsList = new ArrayList<>();
	    List<FacilityHistory> facilityHistoryList = new ArrayList<>();
	    List<FacilityAddressHistory> facilityAddressHistoryDetails = new ArrayList<>();
	    facilityDetailsList.add(facilityDetail);
	    facilityHistoryList.add(facilityHistory);
	    facilityAddressHistoryDetails.add(facilityAddressHistory);
	    facilityData.put(DartDBConstants.FACILITY_CURSOR, facilityDetailsList);
	    facilityData.put(DartDBConstants.FACILITY_HIST_CURSOR, new ArrayList<>());
	    facilityData.put(DartDBConstants.FACILITY_ADDR_HIST_CURSOR, new ArrayList<>());
	    when(dartDBDAO.findFacilityHistoryDetail("", "", 100L)).thenReturn(facilityData);
	    ResponseEntity<Object> retrieveFacilityHistory = dartFacilityService.retrieveFacilityHistory("", "", 100L);
	    assertNotNull(retrieveFacilityHistory);
	}

}
