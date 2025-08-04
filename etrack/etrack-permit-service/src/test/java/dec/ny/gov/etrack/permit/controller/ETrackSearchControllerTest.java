package dec.ny.gov.etrack.permit.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.model.SearchQueryDetail;
import dec.ny.gov.etrack.permit.service.ETrackSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackSearchControllerTest {

	 @Mock
	 private ETrackSearchService etrackSearchService;
	 
	 @InjectMocks
	 private ETrackSearchController eTrackSearchController;
	 
	 @Test
	 public void testStoreSearchQuery() {
		 SearchQueryDetail searchQueryDetail = new SearchQueryDetail();
		 eTrackSearchController.storeSearchQuery("dxdevada", searchQueryDetail);		 
	 }
	 
	 @Test
	 public void deleteSearchQueryTest() {
		 eTrackSearchController.deleteSearchQuery("dxdevada", 1L);
	 }

}
