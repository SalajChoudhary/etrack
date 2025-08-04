package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.CandidateKeywordDetail;

@Repository
public interface ETrackQueryReportRepo extends CrudRepository<CandidateKeywordDetail, String>{
  @Query(value="select k.keyword_text, substr(f.dec_id, 1,1) as region,  count(*) as count, k.keyword_text ||':'||substr(f.dec_id, 1,1) as unique_keyword_text "
      + "from {h-schema}e_project_keyword pk, {h-schema}e_keyword k, {h-schema}e_facility f, {h-schema}e_project p "
      + "where pk.keyword_id=k.keyword_id and pk.project_id=f.project_id "
      + "and f.project_id=p.project_id and p.upload_to_dart_ind=1 and k.keyword_category_id=-1 "
      + " group by k.keyword_text, substr(f.dec_id, 1,1) order by 2", nativeQuery = true)
  List<CandidateKeywordDetail> retrieveCandidateKeywordDetails();
}
