package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.entity.SearchQuery;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.SearchQueryConditionDetail;
import dec.ny.gov.etrack.permit.model.SearchQueryDetail;
import dec.ny.gov.etrack.permit.repo.SearchQueryRepo;


@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackSearchServiceImplTest {

	
	@InjectMocks
	private ETrackSearchServiceImpl eTrackSearchServiceImpl;
	

	@Mock
	private  SearchQueryRepo searchQueryRepo;
	
	private String userIid = "user_one";
	
	private String contextId = UUID.randomUUID().toString();
	
	@Test
	public void storeSearchQuerytest(){
		SearchQueryDetail searchQueryDetail = new SearchQueryDetail();
		List<SearchQueryConditionDetail> searchQueryConditions = new ArrayList<>();
		SearchQueryConditionDetail searchQueryConditionDetail= new SearchQueryConditionDetail();
		searchQueryConditionDetail.setSearchEntityCode(1);
		searchQueryConditions.add(searchQueryConditionDetail);
		searchQueryDetail.setSearchQueryConditions(searchQueryConditions);
		eTrackSearchServiceImpl.storeSearchQuery(userIid, contextId, searchQueryDetail);
		assertTrue(true);
		
	}
	
	@Test(expected = ETrackPermitException.class)
	public void storeSearchQueryDuplicateTest(){
		SearchQueryDetail searchQueryDetail = new SearchQueryDetail();
		searchQueryDetail.setQueryName("test");
		List<SearchQueryConditionDetail> searchQueryConditions = new ArrayList<>();
		SearchQueryConditionDetail searchQueryConditionDetail= new SearchQueryConditionDetail();
		searchQueryConditionDetail.setSearchEntityCode(1);
		searchQueryConditions.add(searchQueryConditionDetail);
		searchQueryDetail.setSearchQueryConditions(searchQueryConditions);
		when(searchQueryRepo.findByQueryName("test")).thenReturn(Optional.of(new SearchQuery()));
		eTrackSearchServiceImpl.storeSearchQuery(userIid, contextId, searchQueryDetail);
		assertTrue(true);
		
	}
	
	@Test
	public void deleteSearchQueryTest() {
		when(searchQueryRepo.findById(1l)).thenReturn(Optional.of(new SearchQuery()));
		eTrackSearchServiceImpl.deleteSearchQuery(1l);
		assertTrue(true);
	}
	
	@Test(expected = ETrackPermitException.class)
	public void deleteSearchQueryExceptionTest() {
		eTrackSearchServiceImpl.deleteSearchQuery(1l);
		assertTrue(true);
	}
}
