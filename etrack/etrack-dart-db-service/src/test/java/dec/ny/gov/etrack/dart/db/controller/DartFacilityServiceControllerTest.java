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

import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.service.DartFacilityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartFacilityServiceControllerTest {
	
	  @Mock
	  private DartFacilityService dartFacilityService;
	  
	  @InjectMocks
	  private DartFacilityServiceController dartFacilityServiceController;

	@Test
	public void getDECIDByProgramTypeTest() {
	
		FacilityAddress facilityAddress = new FacilityAddress();
		when(dartFacilityService.getDECIDByProgramType(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(facilityAddress);
		Object object = dartFacilityServiceController.getDECIDByProgramType("dxdev", "", "", "");
		assertNotNull(object);
	}
	
	@Test
	public void getDECIDByTaxMapNumberTest() {
		FacilityDetail facilityDetail = new FacilityDetail();
		Mockito.lenient().when(dartFacilityService.getDECIDByTaxMap(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(facilityDetail);
		Object object = dartFacilityServiceController.getDECIDByTaxMapNumber("dxdev", "context", "156.17-2-41","county", "rosendale");
		assertNotNull(object);
	}
	
	
	@Test
	public void getDECIDByTaxMapNumberBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartFacilityServiceController.getDECIDByTaxMapNumber("dxdev", "", "","", ""));
	}
	
	
	@Test
	public void getETrackFacilityTest() {
		FacilityDetail facilityDetail = new FacilityDetail();
		Mockito.lenient().when(dartFacilityService.getEtrackFacilityDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(facilityDetail);
		Object object = dartFacilityServiceController.getETrackFacility("dxdev", 1L);
		assertNotNull(object);
	}
	
	
	@Test
	public void getETrackFacilityBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartFacilityServiceController.getETrackFacility("dxdev", null));
	}
	
	@Test
	public void getMatchingFacilitiesTest() {
		FacilityDetail facilityDetail = new FacilityDetail();
		Mockito.lenient().when(dartFacilityService.getAllMatchedFacilities(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(facilityDetail);
		Object object = dartFacilityServiceController.getMatchingFacilities("dxdev", "contectId","adr1");
		assertNotNull(object);
	}
	
	
	@Test
	public void getMatchingFacilitiesBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartFacilityServiceController.getMatchingFacilities("dxdev","" ,""));
	}
	
	@Test
	public void facilityHistoryTest() {
		ResponseEntity<Object> facilityDetail = new ResponseEntity<Object>(HttpStatus.OK);
		Mockito.lenient().when(dartFacilityService.retrieveFacilityHistory(Mockito.anyString(), Mockito.anyString(),  Mockito.anyLong())).thenReturn(facilityDetail);
		Object object = dartFacilityServiceController.facilityHistory("dxdev", "contectId",1L);
		assertNotNull(object);
	}
	
	@Test
	public void facilityHistoryNullTest() {
		ResponseEntity<Object> facilityDetail = new ResponseEntity<Object>(HttpStatus.OK);
		Mockito.lenient().when(dartFacilityService.retrieveFacilityHistory(Mockito.anyString(), Mockito.anyString(),  Mockito.anyLong())).thenReturn(facilityDetail);
		Object object = dartFacilityServiceController.facilityHistory("dxdev", null,1L);
		assertNotNull(object);
	}

}
