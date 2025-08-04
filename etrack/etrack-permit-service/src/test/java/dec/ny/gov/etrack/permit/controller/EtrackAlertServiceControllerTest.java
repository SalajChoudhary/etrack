package dec.ny.gov.etrack.permit.controller;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.service.ETrackAlertService;

@RunWith(SpringJUnit4ClassRunner.class)
public class EtrackAlertServiceControllerTest {
	
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private ETrackAlertServiceController alertController;
	
	@Mock
	private ETrackAlertService eTrackAlertService;
	
	
	private String userId = "jxpuvoge";
	private Long alertId = 12l;
	private Long projectId = 1002L;
	
	//This is completed, 100% coverage:
	
	//delete Alert test cases
	@Test
	public void testDeleteAlertCallsAlertService() {
		this.alertController.deleteAlert(userId, projectId, 1L, 9L);
		verify(eTrackAlertService).deleteAlertMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
	}
	
	@Test
	public void testDeleteAlertThrowsBREForInvalidProjectIdAndInquiryId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Either Project Id or Inquiry id is not passed");
		alertController.deleteAlert(userId, null, null, alertId);
	}
	
	
	//updateAlertAsRead tests
	@Test
	public void testUpdateAlertAsReadCallsAlertService() {
		this.alertController.updateAlertAsRead(userId, projectId, alertId);
		verify(eTrackAlertService).updateAlertMessageAsRead(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
	}




}
