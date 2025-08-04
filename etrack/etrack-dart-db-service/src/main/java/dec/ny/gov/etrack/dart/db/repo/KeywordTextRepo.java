package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.KeywordText;

@Repository
public interface KeywordTextRepo extends CrudRepository<KeywordText, Long> {
  
  @Query(value="select k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, to_char(k.start_date, 'mm/dd/yyyy') start_date, "
      + "to_char(k.end_date, 'mm/dd/yyyy') end_date from {h-schema}e_keyword k, {h-schema}e_keyword_category kc "
      + "where k.keyword_category_id=kc.keyword_category_id and k.active_ind=1 and kc.active_ind=1 "
      + "and k.start_date <= sysdate ", nativeQuery = true)
  List<KeywordText> findAllActiveKeywordTexts();

  @Query(value="select k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, to_char(k.start_date, 'mm/dd/yyyy') start_date, "
      + "to_char(k.end_date, 'mm/dd/yyyy') end_date from {h-schema}e_keyword k, {h-schema}e_keyword_category kc "
      + "where k.keyword_category_id=kc.keyword_category_id and k.active_ind=1 and kc.active_ind=1 "
      + "and k.start_date <= sysdate and k.keyword_category_id=?1", nativeQuery = true)
  List<KeywordText> findAllActiveKeywordTextsByCategoryId(Long categoryId);
}
