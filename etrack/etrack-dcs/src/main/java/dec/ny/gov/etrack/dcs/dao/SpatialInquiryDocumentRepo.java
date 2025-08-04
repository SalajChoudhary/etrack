package dec.ny.gov.etrack.dcs.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.SpatialInquiryDocument;

@Repository
public interface SpatialInquiryDocumentRepo extends CrudRepository<SpatialInquiryDocument, Long> {
  
  @Query("select max(documentId) from SpatialInquiryDocument where inquiryId = :inquiryId "
      + "and upper(documentNm)= :documentName  and documentStateCode ='A'")
  public Long findDocumentIdByInquiryIdAndDocumentNm(Long inquiryId, String documentName);
  
  @Query("select sd from SpatialInquiryDocument sd where sd.inquiryId= :inquiryId "
      + "and sd.documentId= :refDocumentId and sd.documentStateCode='A'")
  public List<SpatialInquiryDocument> findByDocumentIdAndInquiryIdAndRefDocumentId(Long inquiryId, Long refDocumentId);
  
  @Modifying
  @Query("UPDATE SpatialInquiryDocument sd set sd.ecmaasGuid= :guid, sd.documentStateCode= :docStateCd WHERE sd.documentId= :documentId")
  public int updateEcmaasGuidAndStatus(final Long documentId, final String guid, final String docStateCd);

  @Query("select sd from SpatialInquiryDocument sd where sd.inquiryId= :inquiryId "
      + "and (sd.documentId in (:documentIds) or sd.refDocumentId in (:documentIds)) and sd.documentStateCode='A'")
  public List<SpatialInquiryDocument> findAllByDocumentIdsAndInquiryId(List<Long> documentIds, Long inquiryId);

  @Query("select sd from SpatialInquiryDocument sd where sd.documentId in (:documentIds) "
      + "or (sd.refDocumentId in (:documentIds) and sd.addlDocInd=1) and sd.documentStateCode='A' and sd.inquiryId= :inquiryId order by documentId asc")
  public List<SpatialInquiryDocument> findAllDocumentsByDocumentIdsAndInquiryId(List<Long> documentIds, Long inquiryId);
  
   @Query(value="select dc.document_class_nm from {h-schema}e_spatial_inq_document sd, {h-schema}e_document_type dt, {h-schema}e_document_class dc "
      + "where sd.document_type_id=dt.document_type_id and dt.document_class_id=dc.document_class_id "
      + "and sd.document_id=?1 and sd.inquiry_id=?2", nativeQuery=true)
  public List<String> findDocumentClassByDocumentIdAndInquiryId(Long documentId, Long inquiryId);

  @Modifying
  @Query("UPDATE SpatialInquiryDocument sd set sd.documentStateCode = 'L', sd.modifiedById= :userId, "
      + "sd.modifiedDate= :modifiedDate WHERE sd.documentId = :documentId")
  public int updateStateCode(@Param("documentId") Long documentId, String userId, Date modifiedDate);

  public SpatialInquiryDocument findByDocumentIdAndDocumentStateCode(Long documentId, String stateCode);

//  public List<SpatialInquiryDocument> findAllByInquiryId(final Long inquiryId);

  @Query("select max(documentId) from SpatialInquiryDocument where inquiryId = :inquiryId "
      + "and documentSubTypeTitleId= :documentTitleId and documentStateCode ='A'")
  public Long findDocumentIdByInquiryIdAndDocumentSubTypeTitleId(Long inquiryId,
      Integer documentTitleId);

  @Query("select sd from SpatialInquiryDocument sd where sd.inquiryId= :inquiryId "
      + "and sd.documentId= :documentId and sd.documentStateCode='A'")
  Optional<SpatialInquiryDocument> findByIdAndInquiryId(Long documentId, Long inquiryId);

  @Query(value="select document_id from {h-schema}e_spatial_inq_document where document_nm=?1 "
      + "and inquiry_id=?2 and document_state_code='A' ", nativeQuery=true)
  List<Long> findDocumentNameExistByInquiryIdAndDocumentName(final String documentName, final Long inquiryId);
  
  @Query("select sd from SpatialInquiryDocument sd where sd.inquiryId= :inquiryId and sd.documentStateCode='A'")
  List<SpatialInquiryDocument> findAllDocumentsByInquiryId(final Long inquiryId);
}
