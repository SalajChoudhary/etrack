package dec.ny.gov.etrack.permit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.entity.SearchQuery;
import dec.ny.gov.etrack.permit.entity.SearchQueryCondition;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.SearchQueryConditionDetail;
import dec.ny.gov.etrack.permit.model.SearchQueryDetail;
import dec.ny.gov.etrack.permit.repo.SearchQueryConditionRepo;
import dec.ny.gov.etrack.permit.repo.SearchQueryRepo;
import dec.ny.gov.etrack.permit.service.ETrackSearchService;

@Service
public class ETrackSearchServiceImpl implements ETrackSearchService {

	@Autowired
	private SearchQueryRepo searchQueryRepo;

	@SuppressWarnings("unused")
	@Autowired
	private SearchQueryConditionRepo searchQueryConditionRepo;

	private static final Logger logger = LoggerFactory.getLogger(ETrackSearchServiceImpl.class.getName());

	@Override
	public void storeSearchQuery(String userId, String contextId, SearchQueryDetail searchQuery) {
		try {
			SearchQuery searchQueryEntity = new SearchQuery();
			Optional<SearchQuery> optionalSearchQuery = searchQueryRepo.findByQueryName(searchQuery.getQueryName());
			if (optionalSearchQuery.isPresent() && searchQuery.getQueryId() == null) {
				logger.info("Duplicate query names are not allowed");
				throw new BadRequestException("DUPLICATE_QUERY_NAME_ERROR", "This search query name already exists",
						searchQuery.getQueryName());
			}
			if(searchQuery.getQueryId()!=null) {
				optionalSearchQuery = searchQueryRepo.findById(searchQuery.getQueryId());
				if(optionalSearchQuery.isPresent())
					searchQueryEntity = optionalSearchQuery.get();
				else
					throw new BadRequestException("INVALID_QUERY_ID","Invalid query Id", searchQuery.getQueryId());
			}else {
				searchQueryEntity.setSearchQueryCondition(new ArrayList<>());
			}			
			buildSearchQueryEntity(userId, searchQuery, searchQueryEntity);
			
			List<SearchQueryCondition> removedSearchQueryConditions = new ArrayList<>();
			for(SearchQueryCondition searchQueryCondition : searchQueryEntity.getSearchQueryCondition()) {
				Optional<SearchQueryConditionDetail> optional =  searchQuery.getSearchQueryConditions().stream().filter(obj->{
					return obj.getSearchQueryConditionId() != null && obj.getSearchQueryConditionId().equals(searchQueryCondition.getSearchQueryConditionId());
				}).findFirst();
				if(!optional.isPresent()) {
					removedSearchQueryConditions.add(searchQueryCondition);
				}
			}
			for(SearchQueryCondition searchQueryCondition : removedSearchQueryConditions) {
				searchQueryEntity.removeSearchQueryCondition(searchQueryCondition, searchQueryEntity);
			}
			
			for(SearchQueryConditionDetail detail : searchQuery.getSearchQueryConditions()) {
				if(detail.getSearchQueryConditionId()==null) {
					SearchQueryCondition searchQueryCondition = new SearchQueryCondition();
					buildSearchQueryCondition(userId, detail, searchQueryCondition);
					searchQueryEntity.addSearchQueryCondition(searchQueryCondition, searchQueryEntity);
				}else {
					Optional<SearchQueryCondition> optional = searchQueryEntity.getSearchQueryCondition().stream().filter(obj ->{
						return obj.getSearchQueryConditionId()!=null && obj.getSearchQueryConditionId().equals(detail.getSearchQueryConditionId());
						}).findFirst();
					if(optional.isPresent())
						buildSearchQueryCondition(userId, detail, optional.get());
				}
			}
			searchQueryEntity = searchQueryRepo.save(searchQueryEntity);
			
		} catch (BadRequestException e) {
			logger.error("Error while saving search query condition", e);
			throw new ETrackPermitException(e.getErrorCode(), e.getErrorMessage(), e);
		} catch (Exception e) {
			logger.error("Error while saving search query condition", e);
			throw new ETrackPermitException("SAVE_QUERY_ERROR", "Error while persisting search query condition", e);
		}
	}
	
	@Override
	public SearchQueryDetail getSearchQueryDetail(String queryName) {
		
		Optional<SearchQuery> optionalSearchQuery = searchQueryRepo.findByQueryName(queryName);
		SearchQuery searchQueryEntity = optionalSearchQuery.get();
		SearchQueryDetail searchQueryDetail = new SearchQueryDetail();
		searchQueryDetail.setQueryId(searchQueryEntity.getQueryId());
		searchQueryDetail.setComments(searchQueryEntity.getComments());
		searchQueryDetail.setDocumentSearchType(searchQueryEntity.getDocumentSearchType());
		searchQueryDetail.setQueryName(searchQueryEntity.getQueryName());
		searchQueryDetail.setQueryOwner(searchQueryEntity.getQueryOwner());
		searchQueryDetail.setResultDetails(searchQueryEntity.getResultDetails());
		searchQueryDetail.setPersistenceDataType(searchQueryEntity.getPersistenceDataType());
		searchQueryDetail.setSearchQueryConditions(buildSearchQueryCondition(searchQueryEntity.getSearchQueryCondition()));
		return searchQueryDetail;
	}

	private List<SearchQueryConditionDetail> buildSearchQueryCondition(
			List<SearchQueryCondition> searchQueryCondition) {
		List<SearchQueryConditionDetail>  conditionDetails = new  ArrayList<>();
		for(SearchQueryCondition condition : searchQueryCondition) {
			SearchQueryConditionDetail searchQueryConditionDetail = new SearchQueryConditionDetail();
			searchQueryConditionDetail.setComparisonOperator(condition.getComparisonOperator());
			searchQueryConditionDetail.setComparisonValue(
			    StringUtils.hasLength(condition.getComparisonValue()) ? condition.getComparisonValue().trim() : condition.getComparisonValue());
			searchQueryConditionDetail.setConditionOperator(condition.getConditionOperator());
			searchQueryConditionDetail.setSearchAttributeId(condition.getSearchAttributeId());
			searchQueryConditionDetail.setSearchAttributeOrder(condition.getSearchAttributeOrder());
			searchQueryConditionDetail.setSearchEntityCode(condition.getSearchEntityCode());
			searchQueryConditionDetail.setSearchQueryConditionId(condition.getSearchQueryConditionId());
			conditionDetails.add(searchQueryConditionDetail);
		}
		return conditionDetails;
	}

 
	private void buildSearchQueryEntity(String userId, SearchQueryDetail searchQuery, SearchQuery searchQueryEntity) {
		searchQueryEntity.setQueryName(searchQuery.getQueryName());
		searchQueryEntity.setQueryOwner(searchQuery.getQueryOwner());
		searchQueryEntity.setCreatedById(userId);
		searchQueryEntity.setModifiedById(userId);
		searchQueryEntity.setCreateDate(new Date());
		searchQueryEntity.setModifiedDate(new Date());
		searchQueryEntity.setQueryId(searchQuery.getQueryId());
		searchQueryEntity.setResultDetails(searchQuery.getResultDetails());
		searchQueryEntity.setPersistenceDataType(searchQuery.getPersistenceDataType());
		searchQueryEntity.setDocumentSearchType(searchQuery.getDocumentSearchType());
		searchQueryEntity.setComments(searchQuery.getComments());
	}

	private void buildSearchQueryCondition(String userId, SearchQueryConditionDetail searchQueryConditionDto, SearchQueryCondition searchQueryConditionEntity) {
		searchQueryConditionEntity.setConditionOperator(searchQueryConditionDto.getConditionOperator());
		searchQueryConditionEntity.setSearchAttributeId(searchQueryConditionDto.getSearchAttributeId());
		searchQueryConditionEntity.setComparisonOperator(searchQueryConditionDto.getComparisonOperator());
		searchQueryConditionEntity.setComparisonValue(searchQueryConditionDto.getComparisonValue());
		searchQueryConditionEntity.setSearchEntityCode(Integer.valueOf(searchQueryConditionDto.getSearchEntityCode()));
		searchQueryConditionEntity.setSearchAttributeOrder(Integer.valueOf(searchQueryConditionDto.getSearchAttributeOrder()));
		searchQueryConditionEntity.setCreatedById(userId);
		searchQueryConditionEntity.setModifiedById(userId);
		searchQueryConditionEntity.setCreateDate(new Date());
		searchQueryConditionEntity.setModifiedDate(new Date());
	}

	@Override
	public void deleteSearchQuery(Long searchQueryIid) {
		try {
			SearchQuery searchQuery = searchQueryRepo.findById(searchQueryIid).get();
			searchQueryRepo.delete(searchQuery);
		} catch (Exception e) {
			logger.error("Error while deleting search query condition", e);
			throw new ETrackPermitException("SAVE_QUERY_ERROR", "Error while deleting search query condition", e);
		}
	}

}
