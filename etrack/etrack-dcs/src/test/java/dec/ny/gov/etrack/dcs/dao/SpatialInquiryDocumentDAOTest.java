package dec.ny.gov.etrack.dcs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.exception.DocumentNotFoundException;
import dec.ny.gov.etrack.dcs.model.SpatialInquirySupportDocument;

@RunWith(SpringJUnit4ClassRunner.class)
public class SpatialInquiryDocumentDAOTest {

  @Mock
  private SimpleJdbcCall spatialInqDocumentRetrieveProcCall = Mockito.mock(SimpleJdbcCall.class);

  @InjectMocks
  private SpatialInquiryDocumentDAO spatialInquiryDocumentDAO;
  
  @Test
  public void testRetrieveSISupportDocumentReturnsSuccessfully() {
    Map<String, Object> results = new HashMap<>();
    results.put("p_status_cd", 0L);
    results.put("p_status_msg", "Success");
    results.put("p_reqd_spatial_inq_doc_cur", new ArrayList<>());
    SimpleJdbcCall resultCall = Mockito.mock(SimpleJdbcCall.class);
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).declareParameters(Mockito.any());
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).returningResultSet(Mockito.any(), Mockito.any());
    when(spatialInqDocumentRetrieveProcCall.execute(Mockito.anyMap())).thenReturn(results);
    List<SpatialInquirySupportDocument> response = spatialInquiryDocumentDAO.retrieveSISupportDocuments("userId", "contextId", 243242L);
    assertEquals(0, response.size());
  }
  
  @Test(expected = DcsException.class)
  public void testRetrieveSISupportDocumentThrowsWhenStatusCodeIs100() {
    Map<String, Object> results = new HashMap<>();
    results.put("p_status_cd", -100L);
    results.put("p_status_msg", "Failed");
    results.put("p_reqd_spatial_inq_doc_cur", new ArrayList<>());
    SimpleJdbcCall resultCall = Mockito.mock(SimpleJdbcCall.class);
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).declareParameters(Mockito.any());
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).returningResultSet(Mockito.any(), Mockito.any());
    when(spatialInqDocumentRetrieveProcCall.execute(Mockito.anyMap())).thenReturn(results);
    spatialInquiryDocumentDAO.retrieveSISupportDocuments("userId", "contextId", 243242L);
  }

  @Test(expected = DocumentNotFoundException.class)
  public void testRetrieveSISupportDocumentThrowsWhenStatusCodeIs1() {
    Map<String, Object> results = new HashMap<>();
    results.put("p_status_cd", 1L);
    results.put("p_status_msg", "Failed");
    results.put("p_reqd_spatial_inq_doc_cur", new ArrayList<>());
    SimpleJdbcCall resultCall = Mockito.mock(SimpleJdbcCall.class);
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).declareParameters(Mockito.any());
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).returningResultSet(Mockito.any(), Mockito.any());
    when(spatialInqDocumentRetrieveProcCall.execute(Mockito.anyMap())).thenReturn(results);
    spatialInquiryDocumentDAO.retrieveSISupportDocuments("userId", "contextId", 243242L);
  }

  @Test(expected = DocumentNotFoundException.class)
  public void testRetrieveSISupportDocumentThrowsWhenStatusCodeIs2() {
    Map<String, Object> results = new HashMap<>();
    results.put("p_status_cd", 2L);
    results.put("p_status_msg", "Failed");
    results.put("p_reqd_spatial_inq_doc_cur", new ArrayList<>());
    SimpleJdbcCall resultCall = Mockito.mock(SimpleJdbcCall.class);
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).declareParameters(Mockito.any());
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).returningResultSet(Mockito.any(), Mockito.any());
    when(spatialInqDocumentRetrieveProcCall.execute(Mockito.anyMap())).thenReturn(results);
    spatialInquiryDocumentDAO.retrieveSISupportDocuments("userId", "contextId", 243242L);
  }

  @Test(expected = DcsException.class)
  public void testRetrieveSISupportDocumentThrowsWhenStatusCodeIs3() {
    Map<String, Object> results = new HashMap<>();
    results.put("p_status_cd", 3L);
    results.put("p_status_msg", "Failed");
    results.put("p_reqd_spatial_inq_doc_cur", new ArrayList<>());
    SimpleJdbcCall resultCall = Mockito.mock(SimpleJdbcCall.class);
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).declareParameters(Mockito.any());
    doReturn(resultCall).when(spatialInqDocumentRetrieveProcCall).returningResultSet(Mockito.any(), Mockito.any());
    when(spatialInqDocumentRetrieveProcCall.execute(Mockito.anyMap())).thenReturn(results);
    spatialInquiryDocumentDAO.retrieveSISupportDocuments("userId", "contextId", 243242L);
  }
}
