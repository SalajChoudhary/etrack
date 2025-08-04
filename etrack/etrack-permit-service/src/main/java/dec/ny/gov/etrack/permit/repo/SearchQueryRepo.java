package dec.ny.gov.etrack.permit.repo;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.permit.entity.SearchQuery;

@Repository
public interface SearchQueryRepo  extends CrudRepository<SearchQuery, Long> {

	Optional<SearchQuery> findByQueryName(String queryName);
	
	@Transactional
	@Modifying
	@Query(value="delete {h-schema}e_search_load", nativeQuery = true)
	public void deleteSearchLoadRecord();
	
	
}
