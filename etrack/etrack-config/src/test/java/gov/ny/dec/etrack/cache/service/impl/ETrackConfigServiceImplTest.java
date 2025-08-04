package gov.ny.dec.etrack.cache.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.etrack.cache.dao.DocTypeSubTypeDAO;
import gov.ny.dec.etrack.cache.dao.MessageDAO;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.exception.ETrackConfigNoDataFoundException;
import gov.ny.dec.etrack.cache.model.ETrackDocType;
import gov.ny.dec.etrack.cache.util.ETrackConfigHandler;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackConfigServiceImplTest {

  @InjectMocks
  private ETrackConfigServiceImpl eTrackConfigServiceImpl;

  @Mock
  private MessageDAO messageDAO;

  @Mock
  private DocTypeSubTypeDAO docTypeSubTypeDAO;

  @Mock
  private ETrackConfigHandler configHandler;

  @Test(expected = ETrackConfigNoDataFoundException.class)
  public void testGetAllDocTypeAndSubTypesThrowsExceptionWhenReturnsNull() {
    doReturn(null).when(docTypeSubTypeDAO).getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
    eTrackConfigServiceImpl.getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
  }

  @Test(expected = ETrackConfigNoDataFoundException.class)
  public void testGetAllDocTypeAndSubTypesReturnEmptyMap() {
    doReturn(new ArrayList<>()).when(docTypeSubTypeDAO).getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
    eTrackConfigServiceImpl.getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void testGetAllDocTypeAndSubTypesReturnSuccessfully() {
    DocTypeSubType docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(2342);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setLanguageCode("en-US");
    List<DocTypeSubType> docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    doReturn(docTypeAndSubTypes).when(docTypeSubTypeDAO).getDocTypeAndSubTypes(Mockito.anyString(), Mockito.anyString());

    Map<String, Map<Integer, ETrackDocType>> map = new HashMap<>();
    ETrackDocType eTrackDocType = new ETrackDocType();
    eTrackDocType.setDocTypeId(docTypeAndSubType.getDocumentTypeId());
    eTrackDocType.setDocTypeDesc(docTypeAndSubType.getDocumentTypeDesc());
    eTrackDocType.setDocClassId(docTypeAndSubType.getDocumentClassId());
    eTrackDocType.setDocClassName(docTypeAndSubType.getDocumentClassNm());
    Map<Integer, ETrackDocType> docTypeMap = new HashMap<>();
    docTypeMap.put(eTrackDocType.getDocTypeId(), eTrackDocType);
    map.put(docTypeAndSubType.getLanguageCode(), docTypeMap);
    doReturn(map).when(configHandler).getDocTypeAndSubTypeDetails(docTypeAndSubTypes,"testUserId", "testContextId");
    ResponseEntity<Map<String, Map<Integer, ETrackDocType>>> response =
        eTrackConfigServiceImpl.getDocTypeAndSubTypes("testUserId", "testContextId");
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  @Test(expected = ETrackConfigNoDataFoundException.class)
  public void testGetAllMessageThrowsExceptionWhenReturnsNull() {
    doReturn(null).when(messageDAO).getAllMessages(Mockito.anyString(), Mockito.anyString());
    eTrackConfigServiceImpl.getMessages(Mockito.anyString(), Mockito.anyString());
  }

  @Test(expected = ETrackConfigNoDataFoundException.class)
  public void testGetAllMessageThrowsExceptionWhenReturnEmptyMap() {
    doReturn(new ArrayList<>()).when(messageDAO).getAllMessages(Mockito.anyString(), Mockito.anyString());
    eTrackConfigServiceImpl.getMessages(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void testGetAllMessageReturnSuccessfully() {
    Message message = new Message();
    message.setLanguageCode("en-US");
    message.setMessageCode("INVALID_SEARCH_FAC_CRIT");
    message.setMessageDesc("Error");
    message.setMessageTypeDesc("Invalid criteria to retrieve Facility.");
    message.setMessageTypeId(23424);
    List<Message> messages = new ArrayList<>();
    messages.add(message);
    doReturn(messages).when(messageDAO).getAllMessages(Mockito.anyString(), Mockito.anyString());

    Map<String, Map<String, String>> map = new HashMap<>();
    Map<String, String> errorMessageMap = new HashMap<>();
    errorMessageMap.put("INVALID_SEARCH_FAC_CRIT",
        "Error : Invalid criteria to retrieve Facility.");
    map.put("en-US", errorMessageMap);
    doReturn(map).when(configHandler).convertMessages(Mockito.anyList(), Mockito.anyString(), Mockito.anyString());
    assertEquals(HttpStatus.OK, eTrackConfigServiceImpl.getMessages(Mockito.anyString(), Mockito.anyString()).getStatusCode());
  }
}
