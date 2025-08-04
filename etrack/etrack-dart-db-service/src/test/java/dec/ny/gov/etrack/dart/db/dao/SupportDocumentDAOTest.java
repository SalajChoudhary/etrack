package dec.ny.gov.etrack.dart.db.dao;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class SupportDocumentDAOTest {


	  @Mock
	  @Qualifier("eTrackSupportDocumentProcCall")
	  private SimpleJdbcCall eTrackSupportDocumentProcCall;

	  @Mock
	  @Qualifier("eTrackOutForReviewAppsProcCall")
	  private SimpleJdbcCall eTrackOutForReviewAppsProcCall;
	  
	  @InjectMocks
	  private SupportDocumentDAO supportDocumentDAO;
	  
	  Long projectId = 1234l;
	  String userId = "1234";
	  String contextId = UUID.randomUUID().toString();
			  

	  @Test
	  void retrieveAllSupportDocumentsForTheProjectIdTest() {
		  Map<String, Object> result = new HashMap<>();
		  result.put("p_doc_cur", new ArrayList<>());
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.PROJECT_ID, projectId);
	      
		  when(eTrackSupportDocumentProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackSupportDocumentProcCall);
		  when(eTrackSupportDocumentProcCall.execute(input)).thenReturn(result);
		  assertNotNull(supportDocumentDAO.retrieveAllSupportDocumentsForTheProjectId(userId, contextId, projectId));
	  }
	  
	  @Test
	  void retrieveAllSupportDocumentsForTheProjectIdNullResultTest() {
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.PROJECT_ID, projectId);
	      
		  when(eTrackSupportDocumentProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackSupportDocumentProcCall);
		  when(eTrackSupportDocumentProcCall.execute(input)).thenReturn(null);
		  assertNull(supportDocumentDAO.retrieveAllSupportDocumentsForTheProjectId(userId, contextId, projectId));
	  }
	  
	  @Test
	  void retrieveAllSupportDocumentsForTheProjectIdExceptionTest() {
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.PROJECT_ID, projectId);
	      
		  when(eTrackSupportDocumentProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackSupportDocumentProcCall);
		  when(eTrackSupportDocumentProcCall.execute(input)).thenThrow(NullPointerException.class);
		  assertThrows(DartDBException.class, ()-> supportDocumentDAO.retrieveAllSupportDocumentsForTheProjectId(userId, contextId, projectId));
	  }
	  
	  @Test
	  void retrieveOutForReviewAppsTest() {
		  Map<String, Object> result = new HashMap<>();
		  result.put("p_out_for_review_cur", new ArrayList<>());
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.USER_ID, userId);
	      
		  when(eTrackOutForReviewAppsProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackOutForReviewAppsProcCall);
		  when(eTrackOutForReviewAppsProcCall.execute(input)).thenReturn(result);
		  assertNotNull(supportDocumentDAO.retrieveOutForReviewApps(userId, contextId));
	  }
	  
	  @Test
	  void retrieveOutForReviewAppsNullResultTest() {
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.USER_ID, userId);
	      
		  when(eTrackOutForReviewAppsProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackOutForReviewAppsProcCall);
		  when(eTrackOutForReviewAppsProcCall.execute(input)).thenReturn(null);
		  assertNull(supportDocumentDAO.retrieveOutForReviewApps(userId, contextId));
	  }
	  
	  @Test
	  void retrieveOutForReviewAppsExceptionTest() {
		  Map<String, Object> input = new HashMap<>();
	      input.put(DartDBConstants.USER_ID, userId);
	      
		  when(eTrackOutForReviewAppsProcCall.declareParameters(Mockito.any(), Mockito.any())).thenReturn(eTrackOutForReviewAppsProcCall);
		  when(eTrackOutForReviewAppsProcCall.execute(input)).thenThrow(NullPointerException.class);
		  assertThrows(DartDBException.class, ()-> supportDocumentDAO.retrieveOutForReviewApps(userId, contextId));
	  }
}
