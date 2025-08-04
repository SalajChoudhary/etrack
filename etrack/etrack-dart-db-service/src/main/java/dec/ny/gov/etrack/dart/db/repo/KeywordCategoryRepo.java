package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.KeywordCategory;

@Repository
public interface KeywordCategoryRepo extends CrudRepository<KeywordCategory, Long> {
  
  @Query(value="select keyword_category_id, keyword_category "
      + "from {h-schema}e_keyword_category where active_ind=1 and keyword_category_id > 0 order by keyword_category asc", nativeQuery = true)
  List<KeywordCategory> findAllKeywordCategories();
}
