package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.PermitKeyword;

@Repository
public interface PermitKeywordRepo extends CrudRepository<PermitKeyword, Long> {

  @Query(value="select p.permit_keyword_id, p.permit_type_code, pt.permit_type_desc, k.keyword_id, k.keyword_text, "
      + "k.keyword_category_id, kc.keyword_category,   "
      + "to_char(p.start_date, 'mm/dd/yyyy') start_date, to_char(p.end_date, 'mm/dd/yyyy') end_date "
      + "from {h-schema}e_permit_keyword p, {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_permit_type_code pt "
      + "where p.keyword_id=k.keyword_id and k.keyword_category_id=kc.keyword_category_id "
      + "and p.permit_type_code=pt.permit_type_code and p.active_ind=1 and kc.keyword_category_id > 0 "
      + "and p.start_date <= sysdate and pt.active_ind=1 order by k.keyword_text", nativeQuery = true)
  List<PermitKeyword> findAllPermitKeywords();

  @Query(value="select p.permit_keyword_id, p.permit_type_code, pt.permit_type_desc, k.keyword_id, k.keyword_text, "
      + "k.keyword_category_id, kc.keyword_category,   "
      + "to_char(p.start_date, 'mm/dd/yyyy') start_date, to_char(p.end_date, 'mm/dd/yyyy') end_date "
      + "from {h-schema}e_permit_keyword p, {h-schema}e_keyword k, {h-schema}e_keyword_category kc, {h-schema}e_permit_type_code pt "
      + "where p.keyword_id=k.keyword_id and k.keyword_category_id=kc.keyword_category_id "
      + "and p.permit_type_code=pt.permit_type_code and p.active_ind=1 and kc.keyword_category_id > 0 "
      + "and p.start_date <= sysdate and pt.active_ind=1 and k.keyword_category_id=?1 order by k.keyword_text", nativeQuery = true)  
  List<PermitKeyword> findAllPermitKeywordsByCategoryid(final Long categoryId);
  
  @Query(value="select permit_type_code, permit_type_desc from {h-schema}e_permit_type_code "
      + "where (effective_start_date is null or effective_start_date <= sysdate) "
      + "and (effective_end_date is null or effective_end_date >= sysdate) "
      + "and general_permit_ind=0 order by permit_type_code asc", nativeQuery = true)
  List<String> findAllPermitTypes();
}
