package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.SearchModel;
import dec.ny.gov.etrack.dart.db.model.QueryRunDetails;
import dec.ny.gov.etrack.dart.db.model.SearchQueryDetail;
import dec.ny.gov.etrack.dart.db.service.ETrackSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackSearchControllerTest {

	@InjectMocks
	private ETrackSearchController eTrackSearchController;
	
	@Mock
	private ETrackSearchService etrackSearchService;
	
	private String userId = "test";
	private Long queryId=123l;
	
	@Test
	public void retriveSearchByAttributesTest() {
		List<SearchModel> list =new ArrayList<>();
		when(etrackSearchService.retrieveSearchByAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(list);
		Object obj = eTrackSearchController.retriveSearchByAttributes(userId);
		assertNotNull(obj);
	}
	
	@Test
	public void retriveAllAvailableSearchesTest() {
		List<SearchQueryDetail> list =new ArrayList<>();
		when(etrackSearchService.retrieveAvailableSearches(Mockito.anyString(), Mockito.anyString())).thenReturn(list);
		Object obj = eTrackSearchController.retriveAllAvailableSearches(userId);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveSearchConditionByQueryIdTest() {
		SearchQueryDetail searchQueryDetail = new SearchQueryDetail();
		when(etrackSearchService.retrieveSearchConditionByQueryId(Mockito.anyString(), Mockito.anyString(),Mockito.anyLong())).thenReturn(searchQueryDetail);
		Object obj = eTrackSearchController.retrieveSearchConditionByQueryId(userId, queryId);
		assertNotNull(obj);
	}
	
	@Test
	public void retriveRunQueryDetailsTest() {
		QueryRunDetails details = new QueryRunDetails();
		when(etrackSearchService.retriveRunQueryDetails(Mockito.anyString(),Mockito.anyLong())).thenReturn(details);
		Object obj = eTrackSearchController.retriveRunQueryDetails(userId, queryId);
		assertNotNull(obj);
	}

}
