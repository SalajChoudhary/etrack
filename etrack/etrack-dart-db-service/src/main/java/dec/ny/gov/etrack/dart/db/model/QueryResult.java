package dec.ny.gov.etrack.dart.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryResult {
	
	private Integer queryCode;
	private String queryDesc;
	private Integer resultCode;
	private String resultDesc;
	private Boolean resultReviewedInd;

}
