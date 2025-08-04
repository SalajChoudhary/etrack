package dec.ny.gov.etrack.permit.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.MissingDocument;
import dec.ny.gov.etrack.permit.model.ProjectNoteView;
import dec.ny.gov.etrack.permit.service.ETrackNoteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ETrackNoteServiceController {

  @Autowired
  private ETrackNoteService eTrackNoteService;
  private static final Logger logger = LoggerFactory.getLogger(ETrackNoteServiceController.class.getName());
  private static final String INVALID_PROJECT_ID_PASSED = "INVALID_PROJECT_ID";
  
  /**
   * Retrieve all the notes associated with the project.
   * 
   * @param userId - User initiates this request
   * @param projectId - Project Id
   * 
   * @return - Returns project note details.
   */
  @GetMapping("/notes")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Retrieve all the notes associated this this project.")
  public List<ProjectNoteView> getNotes(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the Project Notes details User Id {} Context ID : {}", userId, contextId);
    if (!(StringUtils.hasLength(userId) && projectId != null && projectId > 0)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    return eTrackNoteService.getNotes(userId, contextId, projectId);
  }

  /**
   * Retrieve the note details for the input note id.
   * 
   * @param userId - User initiates this request.
   * @param projectId - Project Id.
   * @param noteId - Note Id to retrieve the details.
   * 
   * @return - Returns project note details.
   */
  @GetMapping("/notes/{noteId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Note details for the input note id.")
  public Object getNoteByNoteId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @PathVariable @ApiParam(example = "23423", value="Note Id") final Long noteId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Retrieve the Project Notes details User Id {} Context ID : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId) || noteId == null) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    return eTrackNoteService.getNote(userId, contextId, projectId, noteId);
  }

  /**
   * Add the user requested note to the Project id.
   * 
   * @param userId - User initiates this request
   * @param projectId - Project id
   * @param projectNote - Project Note details
   * 
   * @return - Updated note details with note id.
   */
  @PostMapping("/notes")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value="Add the requested Manual notes to this project")
  public ProjectNoteView addNote(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody ProjectNoteView projectNote) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Add the project note User Id {} Context Id : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    return eTrackNoteService.addNotes(userId, contextId, projectId, projectNote);
  }

  /**
   * Amend the existing note with this detail.
   * 
   * @param userId - User initiates this request
   * @param projectId - Project id
   * @param projectNote - Project Note details
   * 
   * @return - Updated Project Note details.
   */
  @PutMapping("/notes")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @ApiOperation(value="Update the existing notes associated with this project.")
  public ProjectNoteView updateNote(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody ProjectNoteView projectNote) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Updated the project note User Id {} Context Id : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    return eTrackNoteService.addNotes(userId, contextId, projectId, projectNote);
  }

  /**
   * Delete the requested note from the input Project.
   * 
   * @param userId - User initiates this request
   * @param projectId - Project id
   * @param noteId - Project note id
   */
  @DeleteMapping("/notes/{noteId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Delete the requested note by the user.")
  public void deleteNote(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "34i5345", value="Note Id") final String noteId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Delete the project note User Id {} Context Id : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(projectId)
        || StringUtils.isEmpty(noteId)) {
      throw new BadRequestException(INVALID_PROJECT_ID_PASSED, "Project and/or User id is blank or invalid passed", projectId);
    }
    eTrackNoteService.deleteNote(userId, contextId, projectId, Long.valueOf(noteId));
  }

  /**
   * Generate required document missing note.
   * 
   * @param userId- User who initiates this request.
   * @param projectId - Project Id.
   * @param missingDocuments - Missing document details.
   */
  @PostMapping(value = "/reqd-doc-missing-note")
  @ApiOperation(value="Request to generate the Missing required documents note.")
  public void generateSystemNoteForMissingReqDocuments(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody(required=false) MissingDocument missingDocuments) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into generateSystemNoteForMissingReqDocuments(). User Id {}, Context Id {}", userId, contextId);
    eTrackNoteService.generateMissingReqDocumentNote(userId, contextId, projectId, missingDocuments);
  }
}
