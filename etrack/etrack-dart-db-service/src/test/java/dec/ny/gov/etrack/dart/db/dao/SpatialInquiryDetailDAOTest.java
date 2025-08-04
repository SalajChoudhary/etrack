package dec.ny.gov.etrack.dart.db.dao;

import static dec.ny.gov.etrack.dart.db.util.DartDBConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class SpatialInquiryDetailDAOTest {

	@Qualifier("spatialInqDocumentRetrieveProcCall")
	@Mock
	private SimpleJdbcCall spatialInqDocumentRetrieveProcCall;

	@Qualifier("spatialInqStatusRetrieveProcCall")
	@Mock
	private SimpleJdbcCall spatialInqStatusRetrieveProcCall;

	@Qualifier("spatialInqReviewRetrieveProcCall")
	@Mock
	private SimpleJdbcCall spatialInqReviewRetrieveProcCall;

	@InjectMocks
	private SpatialInquiryDetailDAO spatialInquiryDetailDAO;

	String userId = "dxdev";
	String contextId = UUID.randomUUID().toString();
	Long projectId = 1l;

	@Test
	public void getSpatialInquiryDocumentTest() {	    
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1L);
		when(spatialInqDocumentRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(spatialInqDocumentRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0l);
		result.put("p_si_cat_code", new ArrayList<>());
		when(spatialInqDocumentRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertEquals(null,spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, 1L));
	}

	@Test
	public void getSpatialInquiryDocumentNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1L);
		when(spatialInqDocumentRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(spatialInqDocumentRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100l);
		result.put("p_si_cat_code", new ArrayList<>());
		when(spatialInqDocumentRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, 1L));
	}



	@Test
	public void getSpatialInquiryDocument100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1L);
		when(spatialInqDocumentRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),
				Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(spatialInqDocumentRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100l);
		result.put("p_si_cat_code", new ArrayList<>());
		when(spatialInqDocumentRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, 1L));
	}

	@Test
	public void getSpatialInquiryDocumentExceptionTest() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(USER_ID, userId);
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1L);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, 1L));
	}

	@Test
	public void getSpatialInquiryStatusTest() {	    
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1l);
		inputParam.put(USER_ID, userId);
		when(spatialInqStatusRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(spatialInqStatusRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0L);
		result.put("inq_status",1l);
		when(spatialInqStatusRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, 1L));
	}

	@Test
	public void getSpatialInquiryStatusNegative100Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1l);
		inputParam.put(USER_ID, userId);
		when(spatialInqStatusRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(spatialInqStatusRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100L);
		result.put("inq_status",1l);
		when(spatialInqStatusRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, 1L));
	}



	@Test
	public void getSpatialInquiryStatus1Test() {
		Map<String, Object> inputParam = new HashMap<>();
		inputParam.put(DartDBConstants.SPATIAL_INQUIRY_ID, 1l);
		inputParam.put(USER_ID, userId);
		when(spatialInqStatusRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any(),Mockito.any())).thenReturn(spatialInqStatusRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 1l);
		result.put("inq_status",1l);
		when(spatialInqStatusRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(NoDataFoundException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, 1L));
	}
	
	@Test
	public void getSpatialInquiryStatusExceptionTest() {
		when(spatialInqStatusRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any(),Mockito.any())).thenThrow(new BadRequestException("", "", new NullPointerException()));
		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, 1L));
	}
	
	@Test
	public void getSpatialInquiryStatusNoDataFoundExceptionTest() {
		NoDataFoundException nodataFoundException =  new NoDataFoundException("", "", new NullPointerException());
		nodataFoundException.setErrorCode("");
		when(spatialInqStatusRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any(),Mockito.any())).thenThrow(new NoDataFoundException("", "", new NullPointerException()));
		assertThrows(NoDataFoundException.class, ()->spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, 1L));
	}
	
	@Test
	public void retrieveSpatialInquiryReviewDetailsTest() {	    
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);
		when(spatialInqReviewRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any())).thenReturn(spatialInqReviewRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 0L);
		result.put("p_doc_review_cur",new ArrayList<>());
		when(spatialInqReviewRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertNotNull(spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId));
	}
	
	@Test
	public void retrieveSpatialInquiryReviewDetailsStatusNegative100Test() {	    
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);
		when(spatialInqReviewRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any())).thenReturn(spatialInqReviewRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", -100L);
		result.put("p_doc_review_cur",new ArrayList<>());
		when(spatialInqReviewRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId));
	}
	
	@Test
	public void retrieveSpatialInquiryReviewDetailsStatus100Test() {	    
		Map<String, Object> inputParam = new HashMap<>();
	      inputParam.put(USER_ID, userId);
		when(spatialInqReviewRetrieveProcCall.declareParameters( Mockito.any(), Mockito.any(),  Mockito.any(),
				Mockito.any())).thenReturn(spatialInqReviewRetrieveProcCall);
		Map<String, Object> result = new HashMap<>();
		result.put("p_status_cd", 100L);
		result.put("p_doc_review_cur",new ArrayList<>());
		when(spatialInqReviewRetrieveProcCall.execute(inputParam)).thenReturn(result);

		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId));
	}

	@Test
	public void retrieveSpatialInquiryReviewDetailsExceptionTest() {	    
		assertThrows(DartDBException.class, ()->spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId));
	}

}
