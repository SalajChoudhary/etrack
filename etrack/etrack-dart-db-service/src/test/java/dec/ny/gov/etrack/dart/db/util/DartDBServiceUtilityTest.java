package dec.ny.gov.etrack.dart.db.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.County;
import dec.ny.gov.etrack.dart.db.entity.Municipality;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.CountyRepo;
import dec.ny.gov.etrack.dart.db.repo.MunicipalityRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartDBServiceUtilityTest {

	 @Mock
	  private MunicipalityRepo municipalityRepo;
	  
	  @Mock
	  private CountyRepo countyRepo;
	  
	  @Mock
	  private ApplicantRepo applicantRepo;
	  
	  @InjectMocks
	  DartDBServiceUtility dartDBServiceUtility;
	
	  @Test
		void amendMunicipalityDetailsEmptyTest() {
			List<DashboardDetail> dashboardDetails = new ArrayList();
			
			List<DashboardDetail> list = dartDBServiceUtility.amendMunicipalityDetails(dashboardDetails);
		}
	  
	@Test
	void amendMunicipalityDetails() {
		List<DashboardDetail> dashboardDetails = new ArrayList();
		DashboardDetail d = new DashboardDetail();
		d.setProjectId(1L);
		dashboardDetails.add(d);
		List<Municipality> municipalities = new ArrayList();
		Municipality m = new Municipality();
		municipalities.add(m);
		when(municipalityRepo.findMunicipalitiesForProjectIds(Mockito.anySet())).thenReturn(municipalities);
		List<DashboardDetail> list = dartDBServiceUtility.amendMunicipalityDetails(dashboardDetails);
	}
	
	@Test
	void amendMunicipalityDetailsOneTest() {
		List<DashboardDetail> dashboardDetails = new ArrayList();
		DashboardDetail d = new DashboardDetail();
		d.setProjectId(1L);
		Facility f = new Facility();
		d.setFacility(f);
		dashboardDetails.add(d);
		List<Municipality> municipalities = new ArrayList();
		Municipality m = new Municipality();
		m.setProjectId(1L);
		m.setMunicipalityName("Test");
		municipalities.add(m);
		municipalities.add(m);
		when(municipalityRepo.findMunicipalitiesForProjectIds(Mockito.anySet())).thenReturn(municipalities);
		List<DashboardDetail> list = dartDBServiceUtility.amendMunicipalityDetails(dashboardDetails);
	}
	
	@Test
	void amendCountyDetailsEmpty() {
		List<DashboardDetail> dashboardDetails = new ArrayList();
		List<DashboardDetail> list = dartDBServiceUtility.amendCountyDetails(dashboardDetails);
	}
	
	@Test
	void amendCountyDetails() {
		List<DashboardDetail> dashboardDetails = new ArrayList();
		DashboardDetail d = new DashboardDetail();
		d.setProjectId(1L);
		Facility f = new Facility();
		d.setFacility(f);
		dashboardDetails.add(d);
		List<County> counties = new ArrayList();
		County c = new County();
		c.setProjectId(1L);
		counties.add(c);
		when(countyRepo.findCountiesForProjectIds(Mockito.anySet())).thenReturn(counties);
		List<DashboardDetail> list = dartDBServiceUtility.amendCountyDetails(dashboardDetails);
	}

	@Test
	void preparePublicNameINSearchResultFormat() {
		String name = dartDBServiceUtility.preparePublicNameINSearchResultFormat("test","rest","repeat");
		assertEquals("test, rest repeat", name);
	}
	
	@Test
	void getLegalResponsePartyDetails() {
		List<ApplicantDto> lrpsList = new ArrayList();
		ApplicantDto ad =new ApplicantDto();
		ad.setFirstName("test f name");
		ad.setMiddleName("test f name");
		ad.setLastName("test f name");
		lrpsList.add(ad);
		when(applicantRepo.findLRPDetailsByCreateById(Mockito.any())).thenReturn(lrpsList);
		Map<Long, ApplicantDto> map =  dartDBServiceUtility.getLegalResponsePartyDetails("", "", false);
	}
	
	@Test
	void getLegalResponsePartyDetailsTrueInput() {
		List<ApplicantDto> lrpsList = new ArrayList();
		ApplicantDto ad =new ApplicantDto();
		ad.setFirstName("test f name");
		ad.setMiddleName("test f name");
		ad.setLastName("test f name");
		lrpsList.add(ad);
		when(applicantRepo.findLRPDetailsByCreateById(Mockito.any())).thenReturn(lrpsList);
		Map<Long, ApplicantDto> map =  dartDBServiceUtility.getLegalResponsePartyDetails("", "", true);
	}
}
