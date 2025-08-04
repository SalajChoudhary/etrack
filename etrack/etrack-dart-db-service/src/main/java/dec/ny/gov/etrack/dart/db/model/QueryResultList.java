package dec.ny.gov.etrack.dart.db.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryResultList {
	
	private List<Map<String, QueryResult>> queryResults;

}
