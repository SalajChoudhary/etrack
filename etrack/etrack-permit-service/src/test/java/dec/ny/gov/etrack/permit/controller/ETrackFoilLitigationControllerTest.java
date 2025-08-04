package dec.ny.gov.etrack.permit.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.model.FoilRequest;
import dec.ny.gov.etrack.permit.model.LitigationRequest;
import dec.ny.gov.etrack.permit.service.ETrackFoilLigitationService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackFoilLitigationControllerTest {
	
	@InjectMocks
	private ETrackFoilLitigationController eTrackFoilLitigationController;
	
	@Mock
    private ETrackFoilLigitationService eTrackFoilLigitationService;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	//**This is completed, 100% coverage**
	
	private String userId = "jxpuvoge";
	private Long projectId = 1002L;

	
	
	@Test
	public void testAddLitRequestReturnsMap() {
		Map<String, Object> map = new HashMap<>();
		when(eTrackFoilLigitationService.saveOrUpdateLitigationRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any())).thenReturn(map);
		Map<String, Object> resultMap  = this.eTrackFoilLitigationController.addLitigationRequest(userId, projectId, new LitigationRequest());
		assertEquals(map, resultMap);
	}
	
	@Test
	public void testAddFoilRequestDetailsCallsProjectService() {
		this.eTrackFoilLitigationController.addFoilRequestDetails(userId, projectId, new FoilRequest());
		verify(eTrackFoilLigitationService).saveOrUpdateFoilRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}

}
