package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;
import dec.ny.gov.etrack.dart.db.service.PurgeArchiveService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class PurgArchiveControllerTest {

	@InjectMocks
	private PurgeArchiveController purgeArchiveController;

	@Mock
	private PurgeArchiveService purgeArchiveService;

	@Test
	public void getPurgeArchiveQueryResultTest() {
		Map<String, QueryResultList> result = new HashMap<>();
		when(purgeArchiveService.getPurgeArchiveQueryResult()).thenReturn(result);
		Object object = purgeArchiveController.getPurgeArchiveQueryResult();
		assertNotNull(object);
	}
	

	@Test
	public void getPurgeArchiveDocumentTest() {
		PurgeArchiveResultDocuments purgeArchiveResultDocuments = new PurgeArchiveResultDocuments();
		purgeArchiveResultDocuments.setDocuments(new ArrayList<>());
		when(purgeArchiveService.getResultDocuments(anyString())).thenReturn(purgeArchiveResultDocuments);
		PurgeArchiveResultDocuments object = purgeArchiveController.getPurgeArchiveDocument("123");
		assertNotNull(object);
		assertNotNull(object.getDocuments());
	}


}
