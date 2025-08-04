package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.DashboardService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class OnlineUserServiceImplTest {
	  @Mock
	  private DartDbService dartDbService;
	  @Mock
	  private DashboardService dashboardService;
	  
	  @Mock
	  private DartMilestoneRepo dartMilestoneRepo;
	  
	  @InjectMocks
	  OnlineUserServiceImpl onlineUserServiceImpl;
	  
	  @Test
	  public void getOnlineUserDashboardDetails() {
		  String userId=""; String contextId="";
		  List<DashboardDetail> onlineUserUnsubmittedAppsList = new ArrayList();
		  DashboardDetail dd = new DashboardDetail();
		  Facility facility = new Facility();
		  facility.setLocationDirections("sd");
		  facility.setCity("ds");
		  facility.setState("sd");
		  facility.setZip("532421"); 
		  dd.setFacility(facility);
		  onlineUserUnsubmittedAppsList.add(dd);
		  when(dartDbService.getUnsubmittedApps(userId, contextId)).thenReturn(onlineUserUnsubmittedAppsList);  
		  DartMilestone dm =new DartMilestone();
		  dm.setBatchId(1L);
		  List<DartMilestone> dartMilestonesList = new ArrayList();
		  dartMilestonesList.add(dm);
		  when(dartMilestoneRepo.findAll()).thenReturn(dartMilestonesList); 
		  Object ob = onlineUserServiceImpl.getOnlineUserDashboardDetails(userId, contextId);
		  assertNotNull(ob);
	  }
	  
	  @Test
	  public void getOnlineUserDashboardDetails1() {
		  String userId=""; String contextId="";
		  List<DashboardDetail> onlineUserUnsubmittedAppsList = new ArrayList();
		  DashboardDetail dd = new DashboardDetail();
		  Facility facility = new Facility();
		  facility.setLocationDirections("sd");
		  facility.setCity("ds");
		  facility.setState("sd");
		  dd.setFacility(facility);
		  onlineUserUnsubmittedAppsList.add(dd);
		  when(dartDbService.getUnsubmittedApps(userId, contextId)).thenReturn(onlineUserUnsubmittedAppsList);  
		  DartMilestone dm =new DartMilestone();
		  dm.setBatchId(1L);
		  List<DartMilestone> dartMilestonesList = new ArrayList();
		  dartMilestonesList.add(dm);
		  when(dartMilestoneRepo.findAll()).thenReturn(dartMilestonesList); 
		  Object ob = onlineUserServiceImpl.getOnlineUserDashboardDetails(userId, contextId);
		  assertNotNull(ob);
	  }

}
