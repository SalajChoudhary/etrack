package dec.ny.gov.etrack.dart.db.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackSearchToolDAOTest {
	@Mock
	@Qualifier("submitProjectRetrievalTemplate")
	private JdbcTemplate submitProjectRetrievalTemplate;
	@Mock
	@Qualifier("namedParameterProjectRetrievalTemplate")
	private NamedParameterJdbcTemplate namedParameterProjectRetrievalTemplate;

	@Mock
	@Qualifier("eTrackAttributeDataNameProcCall")
	private SimpleJdbcCall eTrackAttributeDataNameProcCall;
	@Mock
	@Qualifier("retriveSearchResultsProcCall")
	private SimpleJdbcCall etrackRetriveSearchResultsProcCall;

	
	@Mock
	private EntityManager entityManager;
	
	@InjectMocks
	private ETrackSearchToolDAO eTrackSearchToolDAO;

	private SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");

	private static final Logger logger = LoggerFactory.getLogger(ETrackQueryReportDAO.class.getName());

	String userId ="dxdevada";
	
	@Test
	void testGetAttributes() {
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_attr_id", 16);
		when(eTrackAttributeDataNameProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(eTrackAttributeDataNameProcCall);

		Mockito.lenient().when(eTrackAttributeDataNameProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(eTrackAttributeDataNameProcCall);

		Map<String, Object> result = new HashMap<>();
		when(eTrackAttributeDataNameProcCall.execute(Mockito.anyMap())).thenReturn(result);
		eTrackSearchToolDAO.getAttributes("1");
	}
	
	@Test
	void testGetAttributesException() {
		assertThrows(DartDBException.class, ()->eTrackSearchToolDAO.getAttributes("-1"));
	}
	
	@Test
	void testGetSearchResultsDBException() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(inputParam)).thenReturn(result);
		assertThrows(DartDBException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	@Test
	void testGetSearchResults() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertNotNull(eTrackSearchToolDAO.getSearchResults(1L,"1"));
//		assertThrows(DartDBException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	@Test
	void testGetSearchResultsStatusCodeOne() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	@Test
	void testGetSearchResultsStatusCodeTwo() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 2l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	@Test
	void testGetSearchResultsStatusCodeFour() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 4l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertThrows(DartDBException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	@Test
	void testGetSearchResultsStatusCodeThree() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 3l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertThrows(NoDataFoundException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	@Test
	void testGetSearchResultsDartDBException() {		
		Map<String, Object> inputParam = new HashMap();
		inputParam.put("p_query_id", new ArrayList());
		inputParam.put("user_id", userId);
		when(etrackRetriveSearchResultsProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Mockito.lenient().when(etrackRetriveSearchResultsProcCall.returningResultSet(Mockito.any(), Mockito.any())).thenReturn(etrackRetriveSearchResultsProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_results_cur", new ArrayList<>());
		when(etrackRetriveSearchResultsProcCall.execute(Mockito.anyMap())).thenReturn(result);
		assertThrows(DartDBException.class, ()->eTrackSearchToolDAO.getSearchResults(1L,"1"));
	}
	
	
}
