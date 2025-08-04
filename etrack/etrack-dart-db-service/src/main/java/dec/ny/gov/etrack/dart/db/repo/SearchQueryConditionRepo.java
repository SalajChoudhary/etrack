package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.dart.db.entity.SearchQueryCondition;

@Repository
public interface SearchQueryConditionRepo  extends JpaRepository<SearchQueryCondition, Long> {

//	@Query("SELECT c FROM SearchQueryCondition c JOIN SearchQuery q ON c.searchQuery.queryOwner := queryOwner ")
//	List<SearchQueryCondition> findByCreatedByIdOrOwner(StrinString queryOwner);

}
