package dec.ny.gov.etrack.permit.repo;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.permit.entity.ArchivePurgeQueryResult;
import dec.ny.gov.etrack.permit.model.DeletedPurgeArchiveDocument;

@Repository
public interface ArchivePurgeQueryResultRepo extends CrudRepository<ArchivePurgeQueryResult, Long> {
 
  @Modifying
  @Query("update ArchivePurgeQueryResult a set a.analystReviewedInd=1, a.reviewedAnalystId= :userId  where a.resultId=:archPrgQueryResultId")
  void updateAnalystIndicator(final String userId, Long archPrgQueryResultId);
  
  @Transactional
  @Modifying
  @Query("update ArchivePurgeQueryResult a set a.completedInd=1, a.reviewedAdminId= :userId where a.resultId=:archPrgQueryResultId")
  void updateCompleteIndicator(final String userId, Long archPrgQueryResultId);
  
  @Query(value = "select sd.document_id documentId, sd.document_nm documentName, f.project_id projectId,"
  		+ " decode(nvl(f.dec_id,'xxx'),'xxx',null,substr(f.dec_id,1,1)||'-'||substr(f.dec_id,2,4)||'-'||substr(f.dec_id,6)) decId "
  		+ " from etrackowner.e_support_document sd, etrackowner.e_facility f "
  		+ " where sd.project_id=f.project_id and sd.document_state_code != 'A' "
  		+ " and sd.document_id in (:documentIds)", 
  		nativeQuery = true)
  List<DeletedPurgeArchiveDocument> getDeletedArchivePurgeDocument(List<Long> documentIds);

  ArchivePurgeQueryResult findByResultId(Long resultId);

  @Query(value="select q.activity_type from {h-schema}e_arc_prg_query_name_code q, {h-schema}e_arc_prg_query_result r "
      + "where q.arc_prg_query_name_code=r.arc_prg_query_name_code and r.arc_prg_query_result_id=?1", nativeQuery = true)
  String findQueryActivityTypeByResultId(Long resultId);
}
