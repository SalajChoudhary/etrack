package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.ETrackSearchToolDAO;
import dec.ny.gov.etrack.dart.db.entity.SearchAttributeEntiry;
import dec.ny.gov.etrack.dart.db.entity.SearchEntity;
import dec.ny.gov.etrack.dart.db.entity.SearchQuery;
import dec.ny.gov.etrack.dart.db.entity.SearchQueryCondition;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.model.SearchQueryDetail;
import dec.ny.gov.etrack.dart.db.repo.SearchByAttributeRepo;
import dec.ny.gov.etrack.dart.db.repo.SearchQueryRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackSearchServiceImplTest {

	@InjectMocks
	private ETrackSearchServiceImpl eTrackSearchServiceImpl;
	
	
	@Mock
	private ETrackSearchToolDAO eTrackSearchToolDAO;
	
	@Mock
	private SearchByAttributeRepo searchByAttributeRepo;
	
	@Mock
	private SearchQueryRepo searchQueryRepo;
	
	private String userId = "user_one";
	
	private String contextId = UUID.randomUUID().toString();
	
	@Test
	void retrieveAvailableSearchesTest() {
		List<SearchQuery> searchQueries = new ArrayList<>();
		SearchQuery searchQuery = new SearchQuery();
		searchQueries.add(searchQuery);
		Mockito.lenient().when(searchQueryRepo.findByQueryOwnerOrQueryOwnerOrderByQueryId("GLOBAL", userId)).thenReturn(searchQueries);
		List<SearchQueryDetail> searchQueryDetails = eTrackSearchServiceImpl.retrieveAvailableSearches(userId, contextId);
		assertNotNull(searchQueryDetails);
	}
	
	@Test
	void retrieveAvailableSearchesWithSearchQueryConditionsTest() {
		List<SearchQuery> searchQueries = new ArrayList<>();
		SearchQuery searchQuery = new SearchQuery();
		List<SearchQueryCondition> searchQueryConditions = new ArrayList<>();
		SearchQueryCondition searchQueryCondition = new SearchQueryCondition();
		searchQueryConditions.add(searchQueryCondition);
		searchQuery.setSearchQueryCondition(searchQueryConditions);
		searchQueries.add(searchQuery);
		Mockito.lenient().when(searchQueryRepo.findByQueryOwnerOrQueryOwnerOrderByQueryId("GLOBAL", userId)).thenReturn(searchQueries);
		Mockito.lenient().when(searchQueryRepo.findAllByOrderByQueryId()).thenReturn(searchQueries);
		List<SearchQueryDetail> searchQueryDetails = eTrackSearchServiceImpl.retrieveAvailableSearches(userId, contextId);
		assertNotNull(searchQueryDetails);
	}
	
	@Test
	void retrieveAvailableSearchesExceptionTest() {
		List<SearchQuery> searchQueries = new ArrayList<>();
		SearchQuery searchQuery = new SearchQuery();
		searchQueries.add(searchQuery);
		Mockito.lenient().when(searchQueryRepo.findByQueryOwnerOrQueryOwnerOrderByQueryId("GLOBAL", userId)).thenThrow(DataAccessResourceFailureException.class);
		eTrackSearchServiceImpl.retrieveAvailableSearches(userId, contextId);
//		assertThrows(DartDBException.class, ()->{
//			eTrackSearchServiceImpl.retrieveAvailableSearches(userId, contextId);
//		});
	}
	
	@Test
	void retrieveSearchByAttributeTest() {
		List<SearchEntity> entities = new ArrayList<>();
		SearchEntity searchEntity = new SearchEntity();
		entities.add(searchEntity);
		when(searchByAttributeRepo.findAll()).thenReturn(entities);
		
		Object obj = eTrackSearchServiceImpl.retrieveSearchByAttribute(userId, contextId);
		assertNotNull(obj);
	}
	
	@Test
	void retrieveSearchByAttributeWithSearchAttibutesTest() {
		List<SearchEntity> entities = new ArrayList<>();
		SearchEntity searchEntity = new SearchEntity();
		List<SearchAttributeEntiry> searchAttributeEntiries = new ArrayList<>();
		SearchAttributeEntiry attributeEntiry = new SearchAttributeEntiry();
		attributeEntiry.setAttributeDataType(1);
		attributeEntiry.setLovSql("sql");
		searchAttributeEntiries.add(attributeEntiry);
		searchEntity.setAttributeEntiries(searchAttributeEntiries);
		entities.add(searchEntity);
		when(searchByAttributeRepo.findAll()).thenReturn(entities);
		
		Object obj = eTrackSearchServiceImpl.retrieveSearchByAttribute(userId, contextId);
		assertNotNull(obj);
	}
	
	@Test
	void retrieveSearchByAttributeWithSearchAttibutesTypeTwoTest() {
		List<SearchEntity> entities = new ArrayList<>();
		SearchEntity searchEntity = new SearchEntity();
		List<SearchAttributeEntiry> searchAttributeEntiries = new ArrayList<>();
		SearchAttributeEntiry attributeEntiry = new SearchAttributeEntiry();
		attributeEntiry.setAttributeDataType(2);
		attributeEntiry.setLovSql("sql");
		searchAttributeEntiries.add(attributeEntiry);
		searchEntity.setAttributeEntiries(searchAttributeEntiries);
		entities.add(searchEntity);
		when(searchByAttributeRepo.findAll()).thenReturn(entities);
		
		Object obj = eTrackSearchServiceImpl.retrieveSearchByAttribute(userId, contextId);
		assertNotNull(obj);
	}
	
	@Test
	void retrieveSearchByAttributeWithSearchAttibutesEmptySqlTest() {
		List<SearchEntity> entities = new ArrayList<>();
		SearchEntity searchEntity = new SearchEntity();
		List<SearchAttributeEntiry> searchAttributeEntiries = new ArrayList<>();
		SearchAttributeEntiry attributeEntiry = new SearchAttributeEntiry();
		attributeEntiry.setAttributeDataType(1);
		attributeEntiry.setLovSql("");
		searchAttributeEntiries.add(attributeEntiry);
		searchEntity.setAttributeEntiries(searchAttributeEntiries);
		entities.add(searchEntity);
		when(searchByAttributeRepo.findAll()).thenReturn(entities);
		
		Object obj = eTrackSearchServiceImpl.retrieveSearchByAttribute(userId, contextId);
		assertNotNull(obj);
	}
	
	@Test
	void retrieveSearchByAttributeExceptionTest() {
		List<SearchEntity> entities = new ArrayList<>();
		SearchEntity searchEntity = new SearchEntity();
		entities.add(searchEntity);
		when(searchByAttributeRepo.findAll()).thenThrow(TransientDataAccessResourceException.class);
		assertThrows(DartDBException.class, ()->{
			eTrackSearchServiceImpl.retrieveSearchByAttribute(userId, contextId);
		});
	}
	
	@Test
	void retrieveSearchConditionByQueryIdTest() {
		SearchQuery query = new SearchQuery();
		when(searchQueryRepo.findById(1l)).thenReturn(Optional.of(query));
		Object obj = eTrackSearchServiceImpl.retrieveSearchConditionByQueryId(userId, contextId, 1l);
		assertNotNull(obj);
	}
	
	
	@Test
	void testRetriveRunQueryDetails() {
		assertNotNull(eTrackSearchServiceImpl.retriveRunQueryDetails(userId, 1l));
	}
	

}
