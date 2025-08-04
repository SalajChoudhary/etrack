package dec.ny.gov.etrack.permit.service;

import org.springframework.stereotype.Service;

import dec.ny.gov.etrack.permit.model.SearchQueryDetail;
import dec.ny.gov.etrack.permit.model.MaintanenceCodeTable;

@Service
public interface ETrackCodeTableService {
	
	void updateSystemParameter(MaintanenceCodeTable systemParameter);

}
