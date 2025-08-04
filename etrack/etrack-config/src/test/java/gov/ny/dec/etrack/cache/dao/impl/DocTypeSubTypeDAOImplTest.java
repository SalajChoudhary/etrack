package gov.ny.dec.etrack.cache.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import gov.ny.dec.etrack.cache.dao.impl.DocTypeSubTypeDAOImpl;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocTypeSubTypeDAOImplTest {

  @InjectMocks
  private DocTypeSubTypeDAOImpl docTypeSubTypeDAOImpl;

  @Mock
  private SimpleJdbcCall simpleJdbcCall;



  @Test
  public void testGetDocTypeAndSubTypesReturnResults() {
    DocTypeSubType docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(2342);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setLanguageCode(" ");
    List<DocTypeSubType> docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("CUR_DOCTYPE_SUBTYPE", docTypeAndSubTypes);
    doReturn(simpleJdbcCall).when(simpleJdbcCall).declareParameters(Mockito.any());
    doReturn(simpleJdbcCall).when(simpleJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doReturn(resultMap).when(simpleJdbcCall).execute(Mockito.anyMap());
    List<DocTypeSubType> docTypesAndSubTypes = docTypeSubTypeDAOImpl.getDocTypeAndSubTypes("userId", "contextId");
    assertTrue(docTypesAndSubTypes instanceof List);
    assertNotNull(docTypesAndSubTypes);
  }

  @Test(expected = ETrackConfigException.class)
  public void testGetDocTypeAndSubTypesThrowsException() {
    doReturn(simpleJdbcCall).when(simpleJdbcCall).declareParameters(Mockito.any());
    doReturn(simpleJdbcCall).when(simpleJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doThrow(ETrackConfigException.class).when(simpleJdbcCall).execute(Mockito.anyMap());
    docTypeSubTypeDAOImpl.getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
  }
}
