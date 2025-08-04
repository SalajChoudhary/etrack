package dec.ny.gov.etrack.dart.db.service;

import org.springframework.stereotype.Service;

@Service
public interface OnlineUserService {

  Object getOnlineUserDashboardDetails(String userId, String contextId);

}
