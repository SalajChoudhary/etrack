package gov.ny.dec.etrack.cache.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;
import gov.ny.dec.etrack.cache.model.ETrackDocType;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackConfigHandlerTest {

  @InjectMocks
  private ETrackConfigHandler eTrackConfigHandler;
  
  private List<DocTypeSubType> docTypeAndSubTypes;
  private DocTypeSubType docTypeAndSubType;
  private List<Message> messages;
  private Message message;
  
  @After
  public void tearDown() {
    docTypeAndSubTypes = null;
    docTypeAndSubType = null;
    message = null;
    messages = null;
  }
  
  @Test
  public void testConfigHandlerReturnsNullWhenInputRecordListIsNull() {
    assertNull(eTrackConfigHandler.getDocTypeAndSubTypeDetails(null, "testUserId", "testContextId"));
  }

  @Test
  public void testConfigHandlerReturnsNullWhenInputRecordListIsEmpty() {
    docTypeAndSubTypes = new ArrayList<>();
    assertNull(eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId"));
  }

  @Test(expected = ETrackConfigException.class)
  public void testConfigHandlerReturnsExceptionWhenDocTypeIdIsNull() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
  }
  

  @Test(expected = ETrackConfigException.class)
  public void testConfigHandlerReturnsExceptionWhenLangCodeIsEmpty() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setLanguageCode("");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
  }

  @Test(expected = ETrackConfigException.class)
  public void testConfigHandlerReturnsExceptionWhenLangCodeIsNull() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
  }

  @Test(expected = ETrackConfigException.class)
  public void testConfigHandlerReturnsExceptionWhenLangCodeHasNoText() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setLanguageCode(" ");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
  }

  @Test
  public void testConfigHandlerAddNewEntryForLangCodeDoesNotExist() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    
    Map<String, Map<Integer, ETrackDocType>> eTrackDocTypesLangMap =
        eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
    
    assertNotNull(eTrackDocTypesLangMap);
    assertTrue(eTrackDocTypesLangMap.keySet().contains("en-us"));
    assertEquals(1, eTrackDocTypesLangMap.size());
  }

  @Test
  public void testConfigHandlerAddAdditionalDocTypeAndSubTypeForSameLangCode() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(2342);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12322);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(23421);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = null;
    Map<String, Map<Integer, ETrackDocType>> map =
        eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
    assertEquals(1, map.size());
    assertEquals(12322, map.get("en-us").get(12322).getDocTypeId());
  }
  
  @Test
  public void testConfigHandlerAddSubTypeWhenItProvidedForSameLangCode() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(2342);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setDocSubTypeDesc("SubType");
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = null;
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12322);
    docTypeAndSubType.setDocumentTypeDesc("Description");
    docTypeAndSubType.setDocumentClassId(23421);
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubType.setDocumentSubTypeId(45645);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = null;
    Map<String, Map<Integer, ETrackDocType>> map =
        eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
    assertEquals(1, map.size());
    assertEquals(12322, map.get("en-us").get(12322).getDocTypeId());
    assertEquals(45645, map.get("en-us").get(12322).getDocSubTypes().get(0).getSubTypeId());
  }


  @Test
  public void testConfigHandlerAddAdditonalSubTypeWhenItProvidedForSameLangCodeAndDocType() {
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12321);
    docTypeAndSubType.setDocumentTypeDesc("Beneficial Use Determination (BUD)");
    docTypeAndSubType.setDocumentClassId(2342);
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubType.setDocSubTypeDesc("Dams Supplement D-1");
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubTypes = new ArrayList<>();
    docTypeAndSubTypes.add(docTypeAndSubType);
    
    docTypeAndSubType = null;
    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12322);
    docTypeAndSubType.setDocumentTypeDesc("Engineering Report/Process Diagram/Plans");
    docTypeAndSubType.setDocumentClassId(23421);
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubType.setDocumentSubTypeId(45645);
    docTypeAndSubType.setDocSubTypeDesc("Short Environmental Assessment Form");
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = null;

    docTypeAndSubType = new DocTypeSubType();
    docTypeAndSubType.setDocumentTypeId(12322);
    docTypeAndSubType.setDocumentTypeDesc("Notices");
    docTypeAndSubType.setDocumentClassId(23421);
    docTypeAndSubType.setLanguageCode("en-us");
    docTypeAndSubType.setDocumentSubTypeId(45646);
    docTypeAndSubType.setDocSubTypeDesc("Wild, Scenic and Recreational Rivers Supplement WSR-1");
    docTypeAndSubType.setDocumentClassNm("APPLICATION");
    docTypeAndSubTypes.add(docTypeAndSubType);
    docTypeAndSubType = null;

    Map<String, Map<Integer, ETrackDocType>> map =
        eTrackConfigHandler.getDocTypeAndSubTypeDetails(docTypeAndSubTypes, "testUserId", "testContextId");
    assertEquals(1, map.size());
    assertEquals(12322, map.get("en-us").get(12322).getDocTypeId());
    assertEquals(45645, map.get("en-us").get(12322).getDocSubTypes().get(0).getSubTypeId());
    assertEquals(45646, map.get("en-us").get(12322).getDocSubTypes().get(1).getSubTypeId());
  }

  
  @Test
  public void testConvertMessagesReturnsNullWhenNoMessagesPassed() {
    assertNull(eTrackConfigHandler.convertMessages(null, "testUserId", "testContextId"));
  }

  @Test
  public void testConvertMessagesReturnsNullWhenEmptyListsArePassed() {
    messages  = new ArrayList<>(); 
    assertNull(eTrackConfigHandler.convertMessages(messages, "testUserId", "testContextId"));
  }


  @Test(expected = ETrackConfigException.class)
  public void testConvertMessagesThrowsNoLangCodeIsPassed() {
    messages = new ArrayList<>();
    message = new Message();
    message.setMessageCode("MSG_CODE");
    messages.add(message);
    eTrackConfigHandler.convertMessages(messages, "testUserId", "testContextId");
  }

  @Test
  public void testConvertMessageAddNewHashMap() {
    messages = new ArrayList<>();
    message = new Message();
    message.setMessageCode("MSG_CODE");
    message.setLanguageCode("en-us");
    message.setMessageDesc("Desc");
    message.setMessageTypeDesc("TypeDesc");
    message.setMessageTypeId(1231);
    messages.add(message);
    Map<String, Map<String, String>> result = eTrackConfigHandler.convertMessages(messages,"testUserId", "testContextId");
    assertNotNull(result);
    assertEquals(1, result.entrySet().size());
  }

}
