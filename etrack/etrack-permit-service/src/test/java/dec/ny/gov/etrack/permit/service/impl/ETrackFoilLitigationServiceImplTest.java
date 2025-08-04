package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import dec.ny.gov.etrack.permit.entity.LitigationHold;
import dec.ny.gov.etrack.permit.entity.LitigationHoldHistory;
import dec.ny.gov.etrack.permit.entity.ProjectFoilStatusDetail;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.FoilRequest;
import dec.ny.gov.etrack.permit.model.LitigationRequest;
import dec.ny.gov.etrack.permit.repo.FoilRequestRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestHistoryRepo;
import dec.ny.gov.etrack.permit.repo.LitigationHoldRequestRepo;
import dec.ny.gov.etrack.permit.repo.ProjectRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackFoilLitigationServiceImplTest {
	
	@InjectMocks
	private ETrackFoilLigitationServiceImpl foilLitService;
	
	@Mock
	private FoilRequestRepo foilRequestRepo;
	
	@Mock
	private LitigationHoldRequestRepo litigationHoldRequestRepo;
	
	@Mock
	private LitigationHoldRequestHistoryRepo litigationHoldRequestHistoryRepo;
	
	@Mock
	private ProjectRepo projectRepo;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;

	//**Completed, test coverage is 86%**
	
	//saveOrUpdateFoilRequest tests
	@Test
	public void testSaveOrUpdateFoilRequestReturnsFoilRequestNumsSavedOrUpdated() {
	when(foilRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFoilStatusDetailList());
	List<String> result =	foilLitService.saveOrUpdateFoilRequest(userId, contextId, projectId, getFoilRequestObj());
	assertEquals("123", result.get(0));
	assertEquals("345", result.get(1));
	}
	
	@Test
	public void testSaveOrUpdateFoilRequestReturnsEmptyListIfFoilRequestNumIsEmpty() {
	FoilRequest request = getFoilRequestObj();
	request.setFoilRequestNumber(Arrays.asList());
	when(foilRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(getFoilStatusDetailList());
	List<String> result =	foilLitService.saveOrUpdateFoilRequest(userId, contextId, projectId, request);
	assertTrue(CollectionUtils.isEmpty(result));
	}
	
	
	
	//saveOrUpdateLitigationRequest test cases
	
	@Test
	public void testSaveOrUpdateLitigationRequestThrowsBREForInvalidLitHold() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Litigation Start date is not available for this new Litigation Hold request.");
		LitigationRequest request = getLitigationRequestObj();
		request.setLitigationStartDate(null);
		when(litigationHoldRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		foilLitService.saveOrUpdateLitigationRequest(userId, contextId, projectId, request);
		
	}

	
	@Test
	public void testSaveOrUpdateLitigationRequestThrowsBREForInvalidDate() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Incorrect format date is passed");
		LitigationRequest request = getLitigationRequestObj();
		request.setLitigationEndDate("INVALID");
		when(litigationHoldRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(getLitigationHoldObj());
		foilLitService.saveOrUpdateLitigationRequest(userId, contextId, projectId, request);
	}
	
	@Test
	public void testSaveOrUpdateLitigationRequestUpdatesLitigationHold() {
		LitigationRequest request = getLitigationRequestObj();
		when(litigationHoldRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(getLitigationHoldObj());
		when(litigationHoldRequestHistoryRepo.findByProjectIdOrderByLitigationHoldHIdDesc(Mockito.anyLong())).thenReturn(getLitHoldHistoryList());
		foilLitService.saveOrUpdateLitigationRequest(userId, contextId, projectId, request);
		verify(litigationHoldRequestRepo).save(Mockito.any());
	}
	
	@Test
	public void testSaveOrUpdateLitigationRequestSavesNewLitigationHold() {
		LitigationRequest request = getLitigationRequestObj();
		request.setLitigationStartDate("12/23/2024");
		when(litigationHoldRequestRepo.findByProjectId(Mockito.anyLong())).thenReturn(null);
		when(litigationHoldRequestHistoryRepo.findByProjectIdOrderByLitigationHoldHIdDesc(Mockito.anyLong())).thenReturn(getLitHoldHistoryList());
		foilLitService.saveOrUpdateLitigationRequest(userId, contextId, projectId, request);
		verify(litigationHoldRequestRepo).save(Mockito.any());
	}
	
	//private Methods
	
	private LitigationRequest getLitigationRequestObj() {
		LitigationRequest request = new LitigationRequest();
		request.setLitigationStartDate("12/23/2024");
		request.setLitigationEndDate("12/25/2024");
		
		return request;
	}
	
	private LitigationHold getLitigationHoldObj() {
		LitigationHold hold = new LitigationHold();
		hold.setLitigationHoldStartDate(new Date(11,23,2023));
		return hold;
	}
	
	private FoilRequest getFoilRequestObj() {
		FoilRequest request = new FoilRequest();
		request.setFoilRequestNumber(Arrays.asList("123","345"));
		return request;
	}
	
	private List<ProjectFoilStatusDetail> getFoilStatusDetailList(){
		List<ProjectFoilStatusDetail> detailList = new ArrayList<>();
		ProjectFoilStatusDetail detail = new ProjectFoilStatusDetail();
		detail.setCreateDate(new Date(02,07,2024));
		detail.setCreatedById(userId);
		detailList.add(detail);
		return detailList;
	}
	

	private List<LitigationHoldHistory> getLitHoldHistoryList(){
		LitigationHoldHistory history = new LitigationHoldHistory();
		history.setLitigationHoldId(5L);
		history.setLitigationHoldStartDate(new Date());
		history.setLitigationHoldEndDate(new Date());
		return Arrays.asList(history);
	}
}
