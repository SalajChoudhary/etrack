package dec.ny.gov.etrack.dart.db.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class PurgeArchiveDAOTest {


	@Mock
	@Qualifier("purgeArchiveResultDetailsProcCall")
	private SimpleJdbcCall purgeArchiveResultDetailsProcCall;
	
	@InjectMocks
	private PurgeArchiveDao purgeArchiveDao;
				
	@Test
	public void getPurgeArchiveQueryResultsTest() {
		when(purgeArchiveResultDetailsProcCall.declareParameters()).thenReturn(purgeArchiveResultDetailsProcCall);
		 Map<String, Object> result = new HashMap<>();
		 result.put("P_ARC_PRG_CUR", new ArrayList<>());
		when(purgeArchiveResultDetailsProcCall.execute()).thenReturn(result);;
		assertNotNull(purgeArchiveDao.getPurgeArchiveQueryResults());
	}
	
	@Test
	public void getPurgeArchiveQueryResultsTestNegativeTest() {
		when(purgeArchiveResultDetailsProcCall.declareParameters()).thenReturn(purgeArchiveResultDetailsProcCall);
		 Map<String, Object> result = new HashMap<>();
		when(purgeArchiveResultDetailsProcCall.execute()).thenReturn(result);;
		assertThrows(Exception.class, ()-> purgeArchiveDao.getPurgeArchiveQueryResults());
	}
	
	
	
}
