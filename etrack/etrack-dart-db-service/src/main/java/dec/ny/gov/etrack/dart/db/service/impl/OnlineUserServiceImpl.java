package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.dart.db.entity.DartMilestone;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.model.Facility;
import dec.ny.gov.etrack.dart.db.repo.DartMilestoneRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.DashboardService;
import dec.ny.gov.etrack.dart.db.service.OnlineUserService;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {

  @Autowired
  private DartDbService dartDbService;
  @Autowired
  private DashboardService dashboardService;
  
  @Autowired
  private DartMilestoneRepo dartMilestoneRepo;
  
  @Override
  public Object getOnlineUserDashboardDetails(String userId, String contextId) {
    
    Map<String, Object> onlineUserDashboardDetails  = new HashMap<>();
    List<DashboardDetail> onlineUserUnsubmittedAppsList = dartDbService.getUnsubmittedApps(userId, contextId);
    
    if (!CollectionUtils.isEmpty(onlineUserUnsubmittedAppsList)) {
      onlineUserUnsubmittedAppsList.forEach(onlineUserUnsubmittedApp -> {
        Facility facility = onlineUserUnsubmittedApp.getFacility();
        StringBuilder addressFormat = new StringBuilder();
        addressFormat.append(facility.getLocationDirections()).append(",")
            .append(facility.getCity()).append(",")
            .append(facility.getState());
        if (facility.getZip() != null) {
          addressFormat.append(",").append(facility.getZip());
        }
        onlineUserUnsubmittedApp.getFacility().setFormattedAddress(addressFormat.toString());
      });
    }
    Iterable<DartMilestone> dartMilestonesList = dartMilestoneRepo.findAll(); 
    Map<Long, DartMilestone> dartMilestoneMapByBatchId = new HashMap<>();
    dartMilestonesList.forEach(dartMilestone -> {
      dartMilestoneMapByBatchId.put(dartMilestone.getBatchId(), dartMilestone);
    });
    onlineUserDashboardDetails.put("resume-entry", onlineUserUnsubmittedAppsList);
    List<DashboardDetail> unIssuedApplications = dashboardService.retrieveAllThePendingApplications(
        userId, contextId, dartMilestoneMapByBatchId);
    onlineUserDashboardDetails.put("all-active", unIssuedApplications);
    
    List<DashboardDetail> dartPermitsApplications = dartDbService.retrieveAllOnlineUserDartApplications(userId, contextId, dartMilestoneMapByBatchId);
    onlineUserDashboardDetails.put("dart-permits", dartPermitsApplications);
    return onlineUserDashboardDetails;
  }
}
