package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.entity.GIInquiryAlert;
import dec.ny.gov.etrack.permit.entity.ProjectAlert;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.repo.GIInquiryAlertRepo;
import dec.ny.gov.etrack.permit.repo.ProjectAlertRepo;
@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackAlertServiceImplTest {
	
	@InjectMocks
	private ETrackAlertServiceImpl alertService;
	
	@Mock
	private ProjectAlertRepo projectAlertRepo;
	
	@Mock
	private GIInquiryAlertRepo giInquiryAlertRepo;
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;
	private Long alertId = 1L;
	private Long inquiryId = 10l;
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	
	//completed, 100% coverage.
	//deleteAlertMessage tests
	@Test
	public void testDeleteAlertMessageThrowsBREForInvalidProjectOrAlertId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no alert associated with this Project and Alert Id");
		when(projectAlertRepo.findByProjectAlertIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		alertService.deleteAlertMessage(userId, contextId, projectId, inquiryId, alertId);
	}
	
	@Test
	public void testDeleteAlertMessagesDeletesProjectAlert() {
		when(projectAlertRepo.findByProjectAlertIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getProjectAlertObj());
		alertService.deleteAlertMessage(userId, contextId, projectId, inquiryId, alertId);
		verify(projectAlertRepo).delete(Mockito.any());
		
	}
	
	@Test
	public void testDeleteAlertMessageThrowsBREForInvalidAlertIdForGIAlert() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no Geographical Inquiry alert associated with this alert id");
		when(giInquiryAlertRepo.findByInquiryAlertIdAndInquiryId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		alertService.deleteAlertMessage(userId, contextId, null, inquiryId, alertId);
	}
	
	@Test
	public void testDeleteAlertMessageDeletesGIAlert() {
		GIInquiryAlert giAlert = getGIAlertObj();
		when(giInquiryAlertRepo.findByInquiryAlertIdAndInquiryId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(giAlert);
		alertService.deleteAlertMessage(userId, contextId, null, inquiryId, alertId);
		verify(giInquiryAlertRepo).delete(giAlert);
	}
	
	private GIInquiryAlert getGIAlertObj() {
		GIInquiryAlert alert = new GIInquiryAlert();
		alert.setAlertNote("Note");
		return alert;
	}

	
	
	//updateAlertMessageAsRead tests
	@Test
	public void testUpdateAlertMessageAsReadSuccessfully() {
		ProjectAlert alert = getProjectAlertObj();
		when(projectAlertRepo.findByProjectAlertIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(alert);
		alertService.updateAlertMessageAsRead(userId, contextId, projectId, alertId);
		assertEquals(userId, alert.getModifiedById());
//		assertEquals(1, alert.getMsgReadInd());
		
	}
	
	@Test
	public void testUpdateAlertMessageAsReadThrowsBREForInvalidAlertOrProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no alert for this input Alert and Project Id");
		when(projectAlertRepo.findByProjectAlertIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		alertService.updateAlertMessageAsRead(userId, contextId, projectId, alertId);

	}
	
	
	private ProjectAlert getProjectAlertObj() {
		ProjectAlert alert = new ProjectAlert();
		alert.setAlertNote("note");
		alert.setMsgReadInd(1);
		alert.setModifiedById(userId);
		return alert;
	}
}
