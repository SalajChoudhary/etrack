package dec.ny.gov.etrack.dart.db.model;

import java.util.List;

import lombok.Data;

@Data
public class Region {
	private Integer regionId;
	private List<QueryResult> queryResults;

}
