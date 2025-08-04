package dec.ny.gov.etrack.dart.db.model;

import lombok.Data;

@Data
public class QueryRunDetails {

	private String projectId;
	private String documentType;
	private String documentSubType;
	private String facilityId;
	private String facilityName;
	private String address;
	private String muni;
	private String permitType;
	private String applicationType;
	private String receivedDate;
}
