package dec.ny.gov.etrack.dms.model;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuditLogTest {

  @InjectMocks
  AuditLog auditLog;

  @Mock
  private ObjectMapper mapper;

  @Test
  public void testAuditLogToStringWithObject() {
    auditLog.setApplicationId("Test_Application_id");
    auditLog.setDecId("Test_DEC_ID");
    auditLog.setDistrictId("Test_DISTRICT_ID");
    String str = auditLog.toString();
    assertNotNull(str);
  }
}
