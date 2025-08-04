package dec.ny.gov.etrack.dcs.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.SupportDocument;

@Repository
public interface SupportDocumentDAO extends CrudRepository<SupportDocument, Long> {

  @Query("select max(documentId) from SupportDocument where projectId = :projectId "
      + "and upper(documentNm)= :documentName  and documentStateCode ='A'")
  Long findDocumentIdByProjectIdAndDocumentNm(Long projectId, String documentName);

  @Query("select sd from SupportDocument sd where sd.projectId= :projectId "
      + "and sd.documentId= :refDocumentId and sd.documentStateCode='A'")
  List<SupportDocument> findByDocumentIdAndProjectIdAndRefDocumentId(Long projectId,
      Long refDocumentId);

  @Modifying
  @Query("UPDATE SupportDocument sd set sd.ecmaasGuid= :guid, sd.documentStateCode= :docStateCd WHERE sd.documentId= :documentId")
  int updateEcmaasGuidAndStatus(final Long documentId, final String guid,
      final String docStateCd);

  @Query("select sd from SupportDocument sd where sd.projectId= :projectId "
      + "and (sd.documentId in (:documentIds) or sd.refDocumentId in (:documentIds)) and sd.documentStateCode='A'")
  List<SupportDocument> findAllByDocumentIdsAndProjectId(List<Long> documentIds,
      Long projectId);

  @Query("select sd from SupportDocument sd where sd.documentId= :documentId "
      + "or sd.documentSubTypeTitleId in (select sdi.documentSubTypeTitleId from SupportDocument "
      + "sdi where sdi.documentId= :documentId) and sd.documentStateCode='A' "
      + "and sd.projectId= :projectId order by sd.documentId asc")
  List<SupportDocument> findAllDocumentsByDocumentIdsAndProjectId(final Long documentId, Long projectId);
  
  @Query("select sd from SupportDocument sd where sd.documentId in (:documentIds) "
      + "or (sd.refDocumentId in (:documentIds) and sd.addlDocInd is null) and sd.documentStateCode='A' and sd.projectId= :projectId order by documentId asc")
  List<SupportDocument> findAllDocumentsAndRefByDocumentIdsAndProjectId(List<Long> documentIds,
      Long projectId);

  @Query(
      value = "select dc.document_class_nm from {h-schema}e_support_document sd, {h-schema}e_document_type dt, {h-schema}e_document_class dc "
          + "where sd.document_type_id=dt.document_type_id and dt.document_class_id=dc.document_class_id "
          + "and sd.document_id=?1 and sd.project_id=?2",
      nativeQuery = true)
  List<String> findDocumentClassByDocumentIdAndProjectId(Long documentId, Long prjectId);

  @Modifying
  @Query("UPDATE SupportDocument sd set sd.documentStateCode = 'L', sd.modifiedById= :userId, sd.modifiedDate= :modifiedDate WHERE sd.documentId = :documentId")
  int updateStateCode(@Param("documentId") Long documentId, String userId,
      Date modifiedDate);

  SupportDocument findByDocumentIdAndDocumentStateCode(Long documentId, String string);

  List<SupportDocument> findAllByProjectId(final Long projectId);

  @Query("select sd from SupportDocument sd where sd.projectId= :projectId "
      + "and sd.documentId= :documentId and sd.documentStateCode='A'")
  Optional<SupportDocument> findByIdAndProjectId(Long documentId, Long projectId);

  @Query(value = "select document_id from {h-schema}e_support_document where upper(document_nm)=?1 "
      + "and project_id=?2 and document_state_code='A' ", nativeQuery = true)
  List<Long> findDocumentNameExistByProjectIdAndDocumentName(final String documentName,
      final Long projectId);

  @Query(value="select edb_district_id from {h-schema}e_facility where project_id=?1", nativeQuery=true)
  Long findDistrictIdByProjectId(final Long projectId);
  
  @Query(value="select s.document_id from {h-schema}e_submitted_document s where s.edb_district_id=?2 "
      + "and s.document_state_code='A' and upper(s.document_nm)=?1", nativeQuery=true)
  List<Long> findDocumentNameExistByDistrictIdIdAndDocumentName(final String documentName,
      final Long edbDistrictId);
  
  @Query(value = "select document_id from {h-schema}e_support_document where upper(document_nm)=?1 "
      + "and project_id=?2 and document_state_code='A' and document_id != ?3 ", nativeQuery = true)
  List<Long> findDocumentNameExistByProjectIdAndDocumentNameAndDocumentId(final String documentName,
      final Long projectId, final Long documentId);
  
  @Query(value="select dst.document_type_id, dst.document_sub_type_id from {h-schema}e_document_title dt, "
      + "{h-schema}e_document_sub_type_title st, {h-schema}e_document_sub_type dst where "
      + "dt.document_title_id=st.document_title_id and st.document_sub_type_id=dst.document_sub_type_id and "
      + "lower(dt.document_title)=lower(?1)", nativeQuery=true)
  List<String> findDocumentTypeAndSubTypeByDisplayName(final String displayName);

  @Modifying
  @Query(value="update {h-schema}e_support_document set document_desc=?3, "
      + "ref_document_desc=?3 where document_id=?2 and project_id=?1 ", nativeQuery=true)
  void updateDocumentDesc(Long projectId, Long documentId, String documentDesc);
}
