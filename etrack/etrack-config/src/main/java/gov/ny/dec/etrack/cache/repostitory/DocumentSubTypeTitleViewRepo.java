package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeTitleViewEntity;

@Repository
public interface DocumentSubTypeTitleViewRepo extends CrudRepository<DocumentSubTypeTitleViewEntity, Long>{
  
  @Query(value="select stt.document_sub_type_title_id, dty.document_type_id, dty.document_type_desc, stt.document_sub_type_id, st.document_sub_type_desc,"
      + "dt.document_title_id,dt.document_title, stt.active_ind from {h-schema}e_document_type dty, {h-schema}e_document_sub_type_title stt, "
      + "{h-schema}e_document_sub_type st, {h-schema}e_document_title dt where dty.document_type_id=st.document_type_id "
      + "and st.document_sub_type_id=stt.document_sub_type_id and stt.document_title_id=dt.document_title_id order by document_type_id", nativeQuery = true)
  List<DocumentSubTypeTitleViewEntity> findAllDocumentSubTypeTitles();

  @Query(value="select stt.document_sub_type_title_id, dty.document_type_id, dty.document_type_desc, stt.document_sub_type_id, st.document_sub_type_desc,"
      + "dt.document_title_id,dt.document_title, stt.active_ind from {h-schema}e_document_type dty, {h-schema}e_document_sub_type_title stt, "
      + "{h-schema}e_document_sub_type st, {h-schema}e_document_title dt where dty.document_type_id=st.document_type_id "
      + "and st.document_sub_type_id=stt.document_sub_type_id and stt.document_title_id=dt.document_title_id and stt.document_sub_type_title_id=?1 order by document_type_id", nativeQuery = true)
  List<DocumentSubTypeTitleViewEntity> findAllDocumentSubTypeTitlesById(Long subTypeTitleId);
}
