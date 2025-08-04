package dec.ny.gov.etrack.dart.db.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;
import dec.ny.gov.etrack.dart.db.service.PurgeArchiveService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/purge-archive")
public class PurgeArchiveController {

	private static final Logger logger = LoggerFactory.getLogger(PurgeArchiveController.class.getName());

	@Autowired
	private PurgeArchiveService purgeArchiveService;
	/**
	 * Retrieve the query results for the input request parameters.
	 * @return - query results.
	 */
	@GetMapping("/query-result")
	@ApiOperation(value="Return query results.")
	public Map<String, QueryResultList> getPurgeArchiveQueryResult() {
	    logger.info("Entering into getPurgeArchiveQueryResult");
		return purgeArchiveService.getPurgeArchiveQueryResult();
	}
	
	/**
	 * Retrieves the documents for a resultId.
	 * @param resultId - Unique id for resultset created for purge/archive.
	 * @return - Documents list for a resultset.
	 */
	@GetMapping("/result-document/{resultId}")
	@ApiOperation(value="Return document results according to the resultId.") 
	public PurgeArchiveResultDocuments getPurgeArchiveDocument(@PathVariable String resultId)  {
	    logger.info("Entering into getPurgeArchiveQueryResult");
		return purgeArchiveService.getResultDocuments(resultId);
	}


}
