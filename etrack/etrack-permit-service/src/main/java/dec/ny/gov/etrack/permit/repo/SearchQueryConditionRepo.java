package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.permit.entity.SearchQuery;
import dec.ny.gov.etrack.permit.entity.SearchQueryCondition;

@Repository
public interface SearchQueryConditionRepo  extends CrudRepository<SearchQueryCondition, Long> {

}
