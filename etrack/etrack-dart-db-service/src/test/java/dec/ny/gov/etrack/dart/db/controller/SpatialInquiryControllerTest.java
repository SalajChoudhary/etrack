package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
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
import dec.ny.gov.etrack.dart.db.model.GIReviewerDashboardDetail;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.dart.db.service.SpatialInquiryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class SpatialInquiryControllerTest {
	
	@Mock	
	 private SpatialInquiryService geographicalInquiryService;
	
	@InjectMocks
	private SpatialInquiryController spatialInquiryController;

	@Test
	public void getSpatialInquiryDetailsByInquiryTypeTest() {
		List<SpatialInquiryRequest> spatialrequest = new ArrayList<SpatialInquiryRequest>();
		//SpatialInquiryRequest spatialrequest = new SpatialInquiryRequest();
		SpatialInquiryCategory spatialInquiryCategory = null ;
		Mockito.lenient().when(geographicalInquiryService.retrieveSpatialInquiryServiceByInquiryType("userId", 
				"contextId",spatialInquiryCategory.SERP_CERT)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.getSpatialInquiryDetailsByInquiryType("dxdev",
				spatialInquiryCategory.SERP_CERT);
		assertNotNull(object);
	}
	
	@Test
	public void getSpatialInquiryDetailsByInquiryTypeBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.getSpatialInquiryDetailsByInquiryType(null, null));
	}
	
	@Test
	public void retrieveAllSpatialInquiryDetailsByRegionTest() {
		List<SpatialInquiryRequest> spatialrequest = new ArrayList<SpatialInquiryRequest>();
		Mockito.lenient().when(geographicalInquiryService.retrieveAllSpatialInquiryDetails("userId","contextId",0)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retrieveAllSpatialInquiryDetailsByRegion("dxdev",0);
		assertEquals(null, object);
	}
	
	@Test
	public void retrieveAllSpatialInquiryDetailsByRegionBadExceptionTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.retrieveAllSpatialInquiryDetailsByRegion(null, null));
	}

	@Test
	public void retrieveSpatialPolygonInquiryDetailsTest() {
		List<SpatialInquiryRequest> spatialrequest = new ArrayList<SpatialInquiryRequest>();
		Mockito.lenient().when(geographicalInquiryService.retrieveSpatialInqDetail("userId","contextId",1L,
				"requestorName")).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retrieveSpatialPolygonInquiryDetails("dxdev","","",1L);
		assertEquals(null, object);
	}
	
	@Test
	public void getRegionalSpatialInquiryDetailsByInquiryTypeAndRegionIdTest() {
		List<SpatialInquiryRequest> spatialrequest = new ArrayList<SpatialInquiryRequest>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveRegionalSpatialInquiryServiceByInquiryType("userId","contextId",
				spatialInquiryCategory.BOROUGH_DETERMINATION,1)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.getRegionalSpatialInquiryDetailsByInquiryTypeAndRegionId("dxdev",
				spatialInquiryCategory.BOROUGH_DETERMINATION,1);
		assertEquals(null, object);
	}
	
	@Test
	public void getRegionalSpatialInquiryDetailsByInquiryTypeAndRegionIdBadExcepTest() {
		SpatialInquiryCategory spatialInquiryCategory=null;
		assertThrows(BadRequestException.class, ()-> spatialInquiryController
				.getRegionalSpatialInquiryDetailsByInquiryTypeAndRegionId(null,spatialInquiryCategory.BOROUGH_DETERMINATION,1));
	}
	
	@Test
	public void getSpatialInquiryDocumentsTest() {
		List<SpatialInquiryRequest> spatialrequest = new ArrayList<SpatialInquiryRequest>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveSpatialDocumentSummary("userId","contextId",1L)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.getSpatialInquiryDocuments("dxdev",1L);
		assertEquals(null, object);
	}
	
	@Test
	public void getSpatialInquiryDocumentsBadExcepTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.getSpatialInquiryDocuments(null,1L));
	}
	
	@Test
	public void retrieveSpatialInquiryStatusTest() {
		Map<String, Long> spatialrequest = new HashMap<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveSpatialInquiryStatus("userId","contextId",1L)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retrieveSpatialInquiryStatus("dxdev",1L);
		assertNotNull(object);
	}
	
	@Test
	public void retrieveSpatialInquiryStatusBadExcepTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.retrieveSpatialInquiryStatus(null,1L));
	}
	
	@Test
	public void retriveGeographicalInquiryDetailsTest() {
		Map<String, Long> spatialrequest = new HashMap<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveGeographicalInquiryForVW("userId","contextId",1L)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retriveGeographicalInquiryDetails("dxdev",1L);
		assertEquals(null,object);
	}
	
	@Test
	public void retriveGeographicalInquiryDetailsBadExcepTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.retriveGeographicalInquiryDetails(null,1L));
	}
	
	@Test
	public void retriveGeographicalNoteConfigurationDetailsTest() {
		Map<String, Long> spatialrequest = new HashMap<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveGeographicalNoteConfig("userId","contextId")).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retriveGeographicalNoteConfigurationDetails("dxdev");
		assertEquals(null,object);
	}
	
	@Test
	public void retriveGeographicalNoteConfigurationDetailsBadExcepTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.retriveGeographicalNoteConfigurationDetails(null));
	}
	
	@Test
	public void getNoteByNoteIdTest() {
		Map<String, Long> spatialrequest = new HashMap<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.getNote("userId","contextId",1L,1L)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.getNoteByNoteId("dxdev",1L,1L);
		assertEquals(null,object);
	}
	
	@Test
	public void getNoteByNoteIdBadExcepTest() {
		assertThrows(BadRequestException.class, ()-> spatialInquiryController.getNoteByNoteId(null,1L,1L));
	}
	
	@Test
	public void retrieveGeographicalProgramReviewerDashboardDetailsTest() {
		List<GIReviewerDashboardDetail> spatialrequest = new ArrayList<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.getProgramReviewerDashboardDetails("userId","contextId")).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retrieveGeographicalProgramReviewerDashboardDetails("dxdev");
		assertNotNull(object);
	}
	
	
	@Test
	public void retrieveEligibleReviewDocumentsTest() {
		List<GIReviewerDashboardDetail> spatialrequest = new ArrayList<>();
		SpatialInquiryCategory spatialInquiryCategory=null;
		Mockito.lenient().when(geographicalInquiryService.retrieveEligibleReviewDocuments("userId","contextId",1L)).thenReturn(spatialrequest);
		Object object = spatialInquiryController.retrieveEligibleReviewDocuments("dxdev",1L);
		assertEquals(null,object);
	}
	
	
}
