package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.KeywordTextEntity;

@Repository
public interface KeywordTextRepo extends CrudRepository<KeywordTextEntity, Long> {
  @Query(value="select * from {h-schema}e_keyword where lower(keyword_text)=lower(?1) and active_ind=1", nativeQuery = true)
  List<KeywordTextEntity> findByKeywordText(String keywordText);
  
  @Modifying
  @Query(value="delete {h-schema}e_keyword where keyword_id in (?1)", nativeQuery = true)
  int deleteReplacedCandidateKeywords(final List<Long> candidateKeywords);

  @Query(value="select keyword_text from ("
      + "    select k.keyword_text from {h-schema}e_keyword k, {h-schema}e_project_keyword pk, "
      + "    {h-schema}e_project p where k.keyword_id=pk.keyword_id and pk.project_id=p.project_id "
      + "    and p.upload_to_dart_ind=1 and lower(k.keyword_text)=lower(?2) "
      + "union"
      + "    select k.keyword_text from {h-schema}e_keyword k, {h-schema}e_project_keyword pk  "
      + "    where k.keyword_id=pk.keyword_id and pk.project_id=?1 and lower(k.keyword_text)=lower(?2))", nativeQuery = true) 
  List<String> findByKeywordTextFromApprovedList(final Long projectId, final String keywordText);
}
