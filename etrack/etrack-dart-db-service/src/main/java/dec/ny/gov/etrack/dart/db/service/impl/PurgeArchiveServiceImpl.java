package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dec.ny.gov.etrack.dart.db.dao.PurgeArchiveDao;
import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;
import dec.ny.gov.etrack.dart.db.service.PurgeArchiveService;


@Service
public class PurgeArchiveServiceImpl implements PurgeArchiveService {
	
	@Autowired
	PurgeArchiveDao purgeArchiveDao;

	@Override
	public  Map<String, QueryResultList> getPurgeArchiveQueryResult() {
		return purgeArchiveDao.getPurgeArchiveQueryResults();
	}

	@Override
	public PurgeArchiveResultDocuments getResultDocuments(String resultId) {
		return purgeArchiveDao.getPurgeArchiveQueryResultDocuments(resultId);
	}
	  
}
