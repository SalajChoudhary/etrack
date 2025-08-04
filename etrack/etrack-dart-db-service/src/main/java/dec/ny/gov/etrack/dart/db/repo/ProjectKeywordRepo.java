package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.ProjectKeyword;

@Repository
public interface ProjectKeywordRepo extends CrudRepository<ProjectKeyword, Long> {
  
  @Query(value="select distinct keyword_id, keyword_text, keyword_category_id, keyword_category, project_selected , system_detected from ("
      + "    select k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, 1 as project_selected, pk.system_detected "
      + "    from {h-schema}e_project_keyword pk, {h-schema}e_keyword k, {h-schema}e_keyword_category kc "
      + "    where pk.keyword_id=k.keyword_id and k.keyword_category_id=kc.keyword_category_id "
      + "    and k.active_ind=1 and kc.active_ind=1 and k.start_date <= sysdate "
      + "    and (k.end_date is null or k.end_date >= sysdate) and pk.project_id=?1 "
      + "union "
      + "    select k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, 0 as project_selected , 0 as system_detected"
      + "    from {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_permit_keyword pk   "
      + "    where kc.keyword_category_id=k.keyword_category_id and k.keyword_id=pk.keyword_id "
      + "    and k.active_ind=1 and kc.active_ind=1 and k.start_date <= sysdate "
      + "    and (k.end_date is null or k.end_date >= sysdate) and (pk.permit_type_code in ("
      + "    select permit_type_code from {h-schema}e_application where project_id=?1) or pk.permit_type_code in ("
      + "    select gp.related_permit_type_code from {h-schema}e_application a, {h-schema}e_gp_related_permit gp "
      + "    where a.permit_type_code=gp.gp_permit_type_code and a.project_id=?1))"
      + "    and k.keyword_id not in (select keyword_id from {h-schema}e_project_keyword where project_id=?1) "
      + "union"
      + "    select k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, 0 as project_selected , 0 as system_detected"
      + "    from {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_project_keyword pk, {h-schema}e_project p  "
      + "    where kc.keyword_category_id=k.keyword_category_id and k.keyword_id=pk.keyword_id and kc.keyword_category_id=-1 "
      + "    and pk.project_id = p.project_id and p.upload_to_dart_ind=1 and p.project_id != ?1 "
      + "    and k.keyword_id not in (select keyword_id from {h-schema}e_project_keyword where project_id=?1)"
      + ") order by keyword_category_id", nativeQuery = true)
  List<ProjectKeyword> findAllKeywordsAndProjectAssociatedKeywords(final Long projectId);

  @Modifying
  @Query(value="delete {h-schema}e_project_keyword p where p.project_id=?1 and p.keyword_id not in ( "
      + "select keyword_id from (select k.keyword_id from {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_permit_keyword pk "
      + "where k.keyword_category_id=kc.keyword_category_id and k.keyword_id=pk.keyword_id "
      + "and k.active_ind=1 and kc.active_ind=1 and k.start_date <= sysdate "
      + "and (k.end_date is null or k.end_date >= sysdate) and pk.permit_type_code in ( "
      + "select permit_type_code from {h-schema}e_application where project_id=?1) "
      + "union "
      + "select k.keyword_id from {h-schema}e_keyword k where k.keyword_category_id = -1))", nativeQuery = true)
  void deleteUnMappedKeywords(Long projectId);

  @Query(value="select distinct k.keyword_id, k.keyword_text, k.keyword_category_id, kc.keyword_category, 0 as project_selected , 0 as system_detected "
      + "from {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_project_keyword pk, {h-schema}e_project p  "
      + "where kc.keyword_category_id=k.keyword_category_id and k.keyword_id=pk.keyword_id and kc.keyword_category_id=-1 "
      + "and pk.project_id = p.project_id and p.upload_to_dart_ind=1 order by k.keyword_text asc", nativeQuery = true)
  List<ProjectKeyword> findAllCandidateKeywords();
}

