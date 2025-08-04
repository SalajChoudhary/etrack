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
import gov.ny.dec.etrack.cache.dao.impl.MessageDAOImpl;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;

@RunWith(SpringJUnit4ClassRunner.class)
public class MessageDAOImplTest {

  @InjectMocks
  private MessageDAOImpl messageDAOImpl;

  @Mock
  private SimpleJdbcCall simpleJdbcCall;

  @Test
  public void testGetMessagesReturnResults() {
    Message message = new Message();
    message.setLanguageCode("en-US");
    message.setMessageTypeId(12312);
    message.setMessageDesc("Desc");
    message.setMessageTypeDesc("TypeDesc");
    message.setMessageCode("CODE");
    List<Message> messages = new ArrayList<>();
    messages.add(message);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("CUR_MESSAGES", messages);
    doReturn(simpleJdbcCall).when(simpleJdbcCall).declareParameters(Mockito.any());
    doReturn(simpleJdbcCall).when(simpleJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doReturn(resultMap).when(simpleJdbcCall).execute(Mockito.anyMap());
    List<Message> messageList = messageDAOImpl.getAllMessages("testUserid", "testContextID");
    assertTrue(messageList instanceof List);
    assertNotNull(messageList);
  }

  @Test(expected = ETrackConfigException.class)
  public void testGetMessagesThrowsException() {
    doReturn(simpleJdbcCall).when(simpleJdbcCall).declareParameters(Mockito.any());
    doReturn(simpleJdbcCall).when(simpleJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doThrow(ETrackConfigException.class).when(simpleJdbcCall).execute(Mockito.anyMap());
    messageDAOImpl.getAllMessages("testUserID", "testContextId");
  }
}
