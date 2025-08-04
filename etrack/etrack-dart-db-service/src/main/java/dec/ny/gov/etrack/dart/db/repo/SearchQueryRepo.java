package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.dart.db.entity.SearchQuery;

@Repository
public interface SearchQueryRepo  extends JpaRepository<SearchQuery, Long> {

	List<SearchQuery> findAllByOrderByQueryId();

	List<SearchQuery> findByQueryOwnerOrQueryOwnerOrderByQueryId(String string, String userId);


}
