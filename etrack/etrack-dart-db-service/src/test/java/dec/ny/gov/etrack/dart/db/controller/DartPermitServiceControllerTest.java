package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.AdditionalPermitDetail;
import dec.ny.gov.etrack.dart.db.model.PermitApplication;
import dec.ny.gov.etrack.dart.db.service.DartPermitService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartPermitServiceControllerTest {
	
	 @Mock
	 private DartPermitService dartPermitService;
	 
	 @InjectMocks
	 private DartPermitServiceController dartPermitServiceController;

	@Test
	public void retrievePermitAssignedApplicationsTest() {
		Map<String, Object> dartPermit = new HashMap<>();
		when(dartPermitService.retrievePermitsAssignment(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(dartPermit);
		Object object = dartPermitServiceController.retrievePermitAssignedApplications("dxdev", 1L);
		assertNotNull(object);
	}

	
	@Test
	public void retrievePermitAssignedApplicationsBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitAssignedApplications(null, null));
	}
	
	@Test
	public void retrievePermitAssignedApplicationsBadException2Test() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitAssignedApplications("", -1L));
	}
	
	@Test
	public void retrievePermitDetailsToApplyTest() {
		Map<String, Object> dartPermit = new HashMap<>();
		Mockito.lenient().when(dartPermitService.retrievePermitsAssignment(Mockito.anyString(), Mockito.anyString(), 
				Mockito.anyLong())).thenReturn(dartPermit);
		Object object = dartPermitServiceController.retrievePermitDetailsToApply("dxdev", 1L);
		assertNotNull(object);
	}

	
	@Test
	public void retrievePermitDetailsToApplyBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitDetailsToApply(null, null));
	}
	
	@Test
	public void retrievePermitDetailsToApplyBadException2Test() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitDetailsToApply("", -1L));
	}
	
	@Test
	public void retrievePermitSummaryDetailTest() {
		Map<String, Object> dartPermit = new HashMap<>();
		Mockito.lenient().when(dartPermitService.retrieveAllPermitSummary(Mockito.anyString(), Mockito.anyString(), 
				 Mockito.anyString(), Mockito.anyLong())).thenReturn(dartPermit);
		Object object = dartPermitServiceController.retrievePermitSummaryDetail("dxdev", 1L,"");
		assertNotNull(object);
	}

	
	@Test
	public void retrievePermitSummaryDetailBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitSummaryDetail(null, null, null));
	}
	
	@Test
	public void retrievePermitSummaryDetailBadException2Test() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitSummaryDetail("", -1L, null));
	}
	
//	@Test
//	public void retrievePermitModificationSummaryDetailTest() {
//		List<PermitApplication> dartPermit = new ArrayList<>();
//		Mockito.lenient().when(dartPermitService.retrievePermitModificationSummary(Mockito.anyString(),  
//				 Mockito.anyString(), Mockito.anyLong())).thenReturn(dartPermit);
//		Object object = dartPermitServiceController.retrievePermitModificationSummaryDetail("dxdev",123L);
//		assertNotNull(object);
//	}

	
	@Test
	public void retrievePermitModificationSummaryDetailBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitModificationSummaryDetail(null, null));
	}
	
	@Test
	public void retrievePermitModificationSummaryDetailBadException2Test() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrievePermitModificationSummaryDetail("", -1L));
	}
	
	@Test
	public void retrieveAvailablePermitsAddAdditionalTest() {
		AdditionalPermitDetail dartPermit = new AdditionalPermitDetail();
		Mockito.lenient().when(dartPermitService.retrieveAvailablePermitsAddAsAdditional(Mockito.anyString(),  
				 Mockito.anyString(), Mockito.anyLong())).thenReturn(dartPermit);
		Object object = dartPermitServiceController.retrieveAvailablePermitsAddAdditional(123L,"dxdev");
		assertNotNull(object);
	}

	
	@Test
	public void retrieveAvailablePermitsAddAdditionalBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrieveAvailablePermitsAddAdditional(null, null));
	}
	
	@Test
	public void retrieveAvailablePermitsAddAdditionalBadException2Test() {
		assertThrows(BadRequestException.class, ()-> dartPermitServiceController.retrieveAvailablePermitsAddAdditional(0L, ""));
	}
}
