package dec.ny.gov.etrack.dart.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryNote;

@Repository
public interface GeographicalInquiryNoteRepo extends CrudRepository<GeographicalInquiryNote, Long> {
  
  @Query(value="select note.action_note, to_char(note.action_date, 'mm/dd/yyyy') action_date, note.comments, note.inquiry_id, "
      + "note.inquiry_note_id, t.action_type_code, t.action_type_desc, note.created_by_id, "
      + "note.modified_by_id, note.create_date, note.modified_date "
      + "from {h-schema}e_geo_inquiry_note note, {h-schema}e_geo_inq_note_action_type t "
      + "where note.action_type_code=t.action_type_code and note.inquiry_id=?1", nativeQuery=true)
  List<GeographicalInquiryNote> findNotesByInquiryId(Long inquiryId);
  
  @Query(value="select action_type_code, action_type_desc from {h-schema}e_geo_inq_note_action_type where ("
      + "system_note_ind is null or system_note_ind=0) and active_ind=1", nativeQuery = true)
  List<String> findAllActiveGeographicalNoteConfig();
  
  @Query(value="select note.action_note, to_char(note.action_date, 'mm/dd/yyyy') action_date, note.comments, note.inquiry_id, "
      + "note.inquiry_note_id, t.action_type_code, t.action_type_desc, note.created_by_id, "
      + "note.modified_by_id, to_char(note.create_date, 'mm/dd/yyyy') create_date, note.modified_date "
      + "from {h-schema}e_geo_inquiry_note note, {h-schema}e_geo_inq_note_action_type t "
      + "where note.action_type_code=t.action_type_code and note.inquiry_id=?1 and note.inquiry_note_id=?2", nativeQuery=true)
  List<GeographicalInquiryNote> findNoteByInquiryIdAndNoteId(Long inquiryId, Long noteId);

}
