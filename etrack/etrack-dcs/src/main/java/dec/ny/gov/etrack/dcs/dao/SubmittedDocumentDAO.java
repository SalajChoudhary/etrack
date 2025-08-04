
package dec.ny.gov.etrack.dcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.SubmittedDocument;

@Repository
public interface SubmittedDocumentDAO extends CrudRepository<SubmittedDocument, Long> {

  public SubmittedDocument findByDocumentIdAndDocumentStateCode(Long documentId, String documentStateCode);
  
  @Query("select upper(documentNm) from SubmittedDocument where edbDistrictId = :districtId and documentStateCode='A'")
  public List<String> findDocumentNmByDistrictId(Long districtId);
  
  @Query(value = "select distinct upper(s.document_nm) from {h-schema}e_support_document s, {h-schema}e_facility f "
      + "where s.project_id=f.project_id and f.edb_district_id=?1 and s.document_state_code='A' "
      + "and upper(s.document_nm)=?2", nativeQuery = true)
  List<String> findSupportDocumentNameExistByDistrictId(final Long edbDistrictId, final String documentName);
  
  @Query("select upper(documentNm) from SubmittedDocument where edbDistrictId = :districtId and documentStateCode='A' and documentId!=:documentId")
  public List<String> findDocumentNmByDistrictIdForOtherDocIds(Long districtId,Long documentId);
  
  @Query("select documentTypeId from SubmittedDocument sd where sd.documentId=:documentId")
  public Integer findDocumentTypeIdByDocumentId(Long documentId);

  @Query("select max(documentId) from SubmittedDocument where edbDistrictId = :districtId and upper(documentNm)= :documentName and documentStateCode='A'")
  public Long findDocumentIdByDistrictIdAndDocumentNm(Long districtId,String documentName);
  
  @Modifying
  @Query("UPDATE SubmittedDocument sd set sd.documentTypeId = :documentTypeId, sd.documentSubTypeId = :documentSubTypeId WHERE sd.documentId = :documentId")
  public Integer updateMetadata(Integer documentTypeId, Integer documentSubTypeId, Long documentId);

  @Modifying
  @Query("UPDATE SubmittedDocument sd set sd.documentStateCode = 'L' WHERE sd.documentId = :documentId")
  public int updateStateCode(@Param("documentId") Long documentId);
  
  
  @Modifying
  @Query("UPDATE SubmittedDocument sd set sd.ecmaasGUID=:guid,sd.documentStateCode = :docStateCd WHERE sd.documentId = :documentId")
  public int updateEcmaasGuidAndStatus(@Param("documentId") Long documentId,@Param("guid") String guid,@Param("docStateCd") String docStateCd);
  
}
