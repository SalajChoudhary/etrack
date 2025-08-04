package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import dec.ny.gov.etrack.dart.db.dao.ETrackSearchToolDAO;
import dec.ny.gov.etrack.dart.db.entity.SearchAttributeEntiry;
import dec.ny.gov.etrack.dart.db.entity.SearchEntity;
import dec.ny.gov.etrack.dart.db.entity.SearchModel;
import dec.ny.gov.etrack.dart.db.entity.SearchQuery;
import dec.ny.gov.etrack.dart.db.entity.SearchQueryCondition;
import dec.ny.gov.etrack.dart.db.entity.SearchResult;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.model.LastLoadDetails;
import dec.ny.gov.etrack.dart.db.model.SearchAttributeModel;
import dec.ny.gov.etrack.dart.db.model.SearchQueryConditionDetail;
import dec.ny.gov.etrack.dart.db.model.SearchQueryDetail;
import dec.ny.gov.etrack.dart.db.repo.SearchByAttributeRepo;
import dec.ny.gov.etrack.dart.db.repo.SearchQueryRepo;
import dec.ny.gov.etrack.dart.db.service.ETrackSearchService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Service
public class ETrackSearchServiceImpl implements ETrackSearchService {

	@Autowired
	private ETrackSearchToolDAO eTrackSearchToolDAO;
	
	@Autowired
	private SearchQueryRepo searchQueryRepo;
	
	@Autowired
	private SearchByAttributeRepo searchByAttributeRepo;
	
	
	private static final Logger logger =
		      LoggerFactory.getLogger(ETrackSearchServiceImpl.class.getName());
	
	@Override
	public List<SearchQueryDetail> retrieveAvailableSearches(String userId, String contextId) {
		List<SearchQueryDetail> searchQueryDtos = new ArrayList<>();
		try {
			boolean isAdmin = false;
			List<SearchQuery> searchQueries = new ArrayList<>();
			if(!isAdmin)
				searchQueries = searchQueryRepo.findAllByOrderByQueryId();
			else
				searchQueries = searchQueryRepo.findByQueryOwnerOrQueryOwnerOrderByQueryId("GLOBAL", userId);
		for(SearchQuery searchQuery :searchQueries) {
			SearchQueryDetail searchQueryDto = buildSearchQueryModel(searchQuery);
			searchQueryDtos.add(searchQueryDto);
		}
		}catch(Exception e) {
			logger.error("Error while fetching search query condition.", e);
			throw new DartDBException("FETCH_QUERY_ERROR",
					"Error while fetching search query.", e);
		}
		return searchQueryDtos.stream().sorted(Comparator.comparing(SearchQueryDetail::getModifiedDate).reversed()).collect(Collectors.toList());
	}


	private SearchQueryDetail buildSearchQueryModel(SearchQuery searchQuery) {
		SearchQueryDetail searchQueryDto = new SearchQueryDetail();
		searchQueryDto.setQueryId(searchQuery.getQueryId());
		searchQueryDto.setQueryName(searchQuery.getQueryName());
		searchQueryDto.setQueryOwner(searchQuery.getQueryOwner());
		searchQueryDto.setResultDetails(searchQuery.getResultDetails());
		searchQueryDto.setDocumentSearchType(searchQuery.getDocumentSearchType());
		searchQueryDto.setPersistenceDataType(searchQuery.getPersistenceDataType());
		searchQueryDto.setComments(searchQuery.getComments());
		searchQueryDto.setModifiedDate(searchQuery.getModifiedDate());
		List<SearchQueryConditionDetail> searchQueryConditionDtos = new ArrayList<>();
		if(!CollectionUtils.isEmpty(searchQuery.getSearchQueryCondition())) {
			for(SearchQueryCondition searchQueryCondition: searchQuery.getSearchQueryCondition()) {
				SearchQueryConditionDetail searchQueryConditionDto = new SearchQueryConditionDetail();
			
				searchQueryConditionDto.setSearchQueryConditionId(searchQueryCondition.getSearchQueryConditionId());
				searchQueryConditionDto.setConditionOperator(searchQueryCondition.getConditionOperator());
				searchQueryConditionDto.setSearchAttributeId(searchQueryCondition.getSearchAttributeId());
				searchQueryConditionDto.setComparisonOperator(searchQueryCondition.getComparisonOperator());
				searchQueryConditionDto.setComparisonValue(searchQueryCondition.getComparisonValue());
				searchQueryConditionDto.setSearchAttributeOrder(searchQueryCondition.getSearchAttributeOrder());
				searchQueryConditionDto.setSearchEntityCode(searchQueryCondition.getSearchEntityCode());
				searchQueryConditionDtos.add(searchQueryConditionDto);
			}
		}
		searchQueryDto.setSearchQueryConditions(searchQueryConditionDtos);
		return searchQueryDto;
	}
	
	
	@Override
	public Object retrieveSearchByAttribute(String userId, String contextId) {
		try {
		logger.info("Entering into retrieve the Search By Attributes. User Id {}, Context Id {}", userId,
		        contextId);
		LastLoadDetails lastLoadDetails = new LastLoadDetails();
		List<SearchEntity> searchEntities= searchByAttributeRepo.findAll();

		List<SearchModel> attributes = new ArrayList<>();
		for(SearchEntity ent : searchEntities) {
			SearchModel search = new SearchModel();
			search.setSearchEntityCode(ent.getSearchEntityCode());
			search.setSearchEntityDesc(ent.getSearchEntityDesc());
			List<SearchAttributeModel> attributeModels = new ArrayList<>();
			if(!CollectionUtils.isEmpty(ent.getAttributeEntiries())) {
				for(SearchAttributeEntiry attributeEntity : ent.getAttributeEntiries()) {
					SearchAttributeModel attributeModel = new SearchAttributeModel();
					attributeModel.setAttributeDataType(attributeEntity.getAttributeDataType());
					attributeModel.setSearchAttributeId(attributeEntity.getSearchAttributeId());
					attributeModel.setSearchAttributeName(attributeEntity.getSearchAttributeName());
					
					if((attributeEntity.getAttributeDataType() == 2 || attributeEntity.getAttributeDataType() == 6)&& !StringUtils.isEmpty(attributeEntity.getLovSql())) {
						Map<String, Object> result = eTrackSearchToolDAO.getAttributes(String.valueOf(attributeEntity.getSearchAttributeId()));
						attributeModel.setAttributes((List<String>) result.get(DartDBConstants.ATTRIBUTE_CURSOR));
						lastLoadDetails.setPLastLoadDate((String) result.get(DartDBConstants.LAST_LOAD_TIME));
					}
					
					attributeModels.add(attributeModel);
				}
			}
			if(!CollectionUtils.isEmpty(attributeModels)) {
				search.setAttributeModels(attributeModels);	
				attributes.add(search);
			}
		}
		lastLoadDetails.setSearchModels(attributes);
		return lastLoadDetails;
		}catch(Exception e) {
			logger.error("Error while fetching search attributes.", e);
			throw new DartDBException("FETCH_SEARCH_ATTRIBUTE",
					"Error while fetching search attributes.", e);
		}
	}
	

	@Override
	public Object retrieveSearchConditionByQueryId(String userId, String contextId, Long queryId) {
		logger.info("Entering into retrieve the Search Criteria By Search Query ID. User Id {}, Context Id {}", userId,
		        contextId, queryId);
		Optional<SearchQuery> searchQuery = searchQueryRepo.findById(queryId);
		SearchQueryDetail searchQueryDto = buildSearchQueryModel(searchQuery.get());
		return searchQueryDto;
	}


	@Override
	public List<SearchResult> retriveRunQueryDetails(String userId, Long queryId) {	
		List<SearchResult> searchResults= eTrackSearchToolDAO.getSearchResults(queryId, userId).stream()
		        .sorted(Comparator.comparing(SearchResult::getProjectId).reversed()).collect(Collectors.toList());
		 return searchResults;
	}

}
