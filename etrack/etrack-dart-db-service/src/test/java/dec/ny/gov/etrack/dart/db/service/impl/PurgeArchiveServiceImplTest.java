package dec.ny.gov.etrack.dart.db.service.impl;

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

import dec.ny.gov.etrack.dart.db.dao.PurgeArchiveDao;
import dec.ny.gov.etrack.dart.db.model.PurgeArchiveResultDocuments;
import dec.ny.gov.etrack.dart.db.model.QueryResultList;


@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class PurgeArchiveServiceImplTest {
	
	  @Mock
	  private PurgeArchiveDao purgeArchiveDao;
	 	  
	  @InjectMocks
	  PurgeArchiveServiceImpl purgeArchiveServiceImpl;

	
	@Test
	public void getPurgeArchiveQueryResultTest() throws Exception {
		 Map<String, QueryResultList> result = new HashMap<>();	
		when(purgeArchiveDao.getPurgeArchiveQueryResults()).thenReturn(result);		
		Map<String, QueryResultList> obj =purgeArchiveServiceImpl.getPurgeArchiveQueryResult();
		assertNotNull(obj);
	}
	
	
	@Test
	public void getPurgeArchiveDocumentTest() {
		PurgeArchiveResultDocuments purgeArchiveResultDocuments = new PurgeArchiveResultDocuments();
		purgeArchiveResultDocuments.setDocuments(new ArrayList<>());
		when(purgeArchiveDao.getPurgeArchiveQueryResultDocuments(anyString())).thenReturn(purgeArchiveResultDocuments);		
		PurgeArchiveResultDocuments object = purgeArchiveServiceImpl.getResultDocuments("123");
		assertNotNull(object);
		assertNotNull(object.getDocuments());
	}
	

}
