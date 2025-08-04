package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.ProjectKeywordEntity;

@Repository
public interface ProjectKeywordRepo extends CrudRepository<ProjectKeywordEntity, Long> {
  List<ProjectKeywordEntity> findByProjectIdAndKeywordId(final Long projectId, final Long keywordId);
  @Query(value="select count(*) from {h-schema}e_keyword where keyword_id in (?1) and keyword_category_id=-1", nativeQuery = true)
  int findMatchedProjectKeywords(final List<Long> candidateKeywords);
  
  @Modifying
  @Query(value="update {h-schema}e_project_keyword set keyword_id=?2 where keyword_id in (?1)", nativeQuery = true)
  int updateCandidateKeywordsWithPermitKeyword(List<Long> candidateKeywords, final Long permitKeyword);
}
