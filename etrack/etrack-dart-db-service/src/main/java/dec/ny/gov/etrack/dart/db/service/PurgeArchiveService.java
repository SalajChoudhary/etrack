package dec.ny.gov.etrack.dart.db.service;

import java.util.Map;

import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;

public interface PurgeArchiveService {
	

	public  Map<String, QueryResultList> getPurgeArchiveQueryResult();

	public PurgeArchiveResultDocuments getResultDocuments(String resultId);
	

}
