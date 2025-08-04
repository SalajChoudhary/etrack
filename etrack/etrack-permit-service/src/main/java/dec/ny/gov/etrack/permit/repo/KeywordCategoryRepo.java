package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.KeywordCategoryEntity;

@Repository
public interface KeywordCategoryRepo extends CrudRepository<KeywordCategoryEntity, Long> {
 
  List<KeywordCategoryEntity> findByKeywordCategory(String keywordCategory);

  @Query(value="select * from {h-schema}e_keyword_category where lower(keyword_category)=?1 and active_ind=1", nativeQuery=true)
  List<KeywordCategoryEntity> findByCategoryText(String lowerCase);
}
