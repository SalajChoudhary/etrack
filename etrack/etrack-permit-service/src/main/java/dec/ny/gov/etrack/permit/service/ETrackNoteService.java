package dec.ny.gov.etrack.permit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.permit.model.MissingDocument;
import dec.ny.gov.etrack.permit.model.ProjectNoteView;

@Service
public interface ETrackNoteService {

  /**
   * Returns all the notes associated with the input project id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @return - List of notes.
   */
  List<ProjectNoteView> getNotes(final String userId, final String contextId,
      final Long projectId);
  
  /**
   * Returns the note details for the input note id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param noteId - Note id.
   * 
   * @return - Note details. 
   */
  ProjectNoteView getNote(final String userId, final String contextId, final Long projectId,
      final Long noteId);
  
  /**
   * Add notes to the input project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param projectNotes - new note information.
   * 
   * @return - Note details with newly created Note id.
   */
  ProjectNoteView addNotes(final String userId, final String contextId, final Long projectId,
      ProjectNoteView projectNotes);
  
  /**
   * Delete the requested note from the project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param noteId - Note Id to be deleted.
   */
  void deleteNote(final String userId, final String contextId, final Long projectId,
      final Long noteId);

  /**
   * Generate a system note if any required documents are missed to upload for this project.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param missingDocumentsTitleIds - Missing document details.
   */
  void generateMissingReqDocumentNote(String userId, String contextId, 
      Long projectId, MissingDocument missingDocumentsTitleIds);
  
}
