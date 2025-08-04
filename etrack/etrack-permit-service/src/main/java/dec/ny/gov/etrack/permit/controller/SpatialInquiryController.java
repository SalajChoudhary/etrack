package dec.ny.gov.etrack.permit.controller;

import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.GIReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.GeographicalInquiryNoteView;
import dec.ny.gov.etrack.permit.model.GeographicalInquirySubmittalResponse;
import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.permit.service.SpatialInquiryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/spatial-inquiry")
public class SpatialInquiryController {

  private static final Logger logger = LoggerFactory.getLogger(
      SpatialInquiryController.class.getName());
  
  @Autowired
  private SpatialInquiryService spatialInquiryService;
  
  
  /**
   * Persist the Spatial Inquiry details. requested by the user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId -Unique UUID to track this request.
   * @param spatialInquiryPolygon - Spatial Inquiry details.
   * 
   * @return - Updated Spatial Inquiry details with inquiry id.
   */
  @PostMapping
  @ApiOperation(value="Store the Spatial Inquiry details into eTrack.")
  public SpatialInquiryRequest saveSpatialPolygonInquiryDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader final @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId,
      @RequestBody final SpatialInquiryRequest spatialInquiryPolygon) {
    
    logger.info("Entering into saveSpatialPolygonInquiryDetails() method . User Id {}, Context Id{}", userId, contextId);
    return spatialInquiryService.saveSpatialInquiryDetail(userId, contextId, spatialInquiryPolygon);
  }
  
  /**
   * Upload the Geographical Inquiry Map into DMS.
   * 
   * @param response - {@link HttpServletResponse}
   * @param userId - User who initiates this request.
   * @param jwtToken - JWT token
   * @param inquiryId - Inquiry Id.
   * @param printUrl - Spatial Inquiry Map URL.
   * 
   */
  @GetMapping(value = {"/upload-gi-map/{inquiryId}"})
  @ApiOperation(value="Upload the Geographical Map created for this inuqiry to DMS for the input inquiry id.")
  public void uploadSpatialInquiryMapDetails(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "mapurl", value="Spatial Inquiry Map URL") final String spatialInquiryMapUrl,
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken,
      @PathVariable @ApiParam(example = "872934", value="Geographical Inquiry Id.") final Long inquiryId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into uploadSpatialInquiryMapDetails. User Id {}, Context Id: {}", userId, contextId);
    spatialInquiryService.uploadSpatialInquiryMap(userId, contextId, jwtToken, inquiryId,
        spatialInquiryMapUrl);
  }

  
  /**
   * Submit the inquiry into eTrack.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Inquiry Id.
   */
  @PostMapping(value = {"/submit"})
  @ApiOperation(value="Request to submit the Geographical Inquiry into eTrack.")
  public void submitSpatialInquiry(
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "872934", value="Geographical Inquiry Id.") final Long inquiryId) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into submitSpatialInquiry.  Context Id: {}", contextId);
    spatialInquiryService.submitSpatialInquiry(userId, contextId, jwtToken, inquiryId);
  }

  /**
   * Submit the Geographical Inquiry response details.
   * 
   * @param jwtToken - JWT Token.
   * @param userId - User who initiates this request.
   * @param inquiryId - Inquiry Id.
   */
  @PostMapping(value = {"/response"})
  @ApiOperation(value="Store the response details entered by the user for the input Geographical Inquiry into eTrack.")
  public void submitSpatialInquiryResponse(
      @RequestHeader (HttpHeaders.AUTHORIZATION) final String jwtToken,
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "872934", value="Geographical Inquiry Id.") final Long inquiryId, 
      @RequestBody GeographicalInquirySubmittalResponse submittalResponse) {

    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into submitSpatialInquiryReponse.  Context Id: {}", contextId);
    spatialInquiryService.submitSpatialInquiryResponse(userId, contextId, inquiryId, jwtToken, submittalResponse);
  }

  /**
   * Assign Analyst for the input Geographical Inquiry to review and do further action if nay.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * @param assignmentNote - Assignment note.
   */
  @PutMapping("/assign-inquiry")
  @ApiOperation(value="Assigning the Geographical Inquiry to DEP Analyst to review")
  public void assignGeographicalInquiryToAnalyst(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Inquiry Id") final Long inquiryId, 
      @RequestBody AssignmentNote assignmentNote) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into assignGeographicalInquiryToAnalyst. User Id {} , Context Id{} ", userId,
        contextId);
    if (inquiryId == null || inquiryId <= 0) {
      throw new BadRequestException("INQUIRY_ID_NOT_PASSED", "Inquiry Id is not passed.", inquiryId);
    }
    
    if (StringUtils.hasText(assignmentNote.getAnalystName()) 
        && StringUtils.hasText(assignmentNote.getAnalystId())) {
      spatialInquiryService.updateGeographicalInquiryAssignment(userId, contextId, inquiryId, assignmentNote);
    } else {
      throw new BadRequestException("NO_ASSIGNED_USER_PASSED", "User Assigned details cannot be empty", assignmentNote);
    }
    logger.info("Existing from assignGeographicalInquiryToAnalyst. User Id {} , Context Id {} ", userId,
        contextId);
  }


  /**
   * Amend the existing note with this detail.
   * 
   * @param userId - User initiates this request
   * @param inquiryId - Inquiry id.
   * @param projectNote - Geographical inquiry note details.
   * 
   * @return - Updated Geographical Inquiry note details.
   */
  @PutMapping("/notes")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @ApiOperation(value="Update the existing inquiry notes associated with this Geographical Inquiry.")
  public GeographicalInquiryNoteView addOrUpdateNote(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Inquiry Id") final Long inquiryId, 
      @RequestBody GeographicalInquiryNoteView geographicalInquiryNote) {
    
    final String contextId = UUID.randomUUID().toString();
    logger.info("Updated the project note User Id {} Context Id : {}", userId, contextId);
    if (!StringUtils.hasLength(userId) || inquiryId == null || inquiryId <= 0) {
      throw new BadRequestException("INVALID_INQUIRY_ID_PASSED", "Inquiry id  and/or User id is blank or invalid passed", inquiryId);
    }
    return spatialInquiryService.addNotes(userId, contextId, inquiryId, geographicalInquiryNote);
  }

  /**
   * Delete the requested note from the input Project.
   * 
   * @param userId - User initiates this request.
   * @param inquiryId - Geographical Inquiry id.
   * @param noteId - Geographical Inquiry note id.
   */
  @DeleteMapping("/notes/{noteId}")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="Delete the requested Geographical Inquiry note by the user.")
  public void deleteGeographicalInquiryNote(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long inquiryId,
      @PathVariable @ApiParam(example = "34i5345", value="Note Id") final Long noteId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Delete the deleteGeographicalInquiryNote User Id : {} Context Id : {}", userId, contextId);
    if (StringUtils.isEmpty(userId) || inquiryId == null || inquiryId <= 0 || noteId == null || noteId <= 0) {
      throw new BadRequestException("INVALID_INQUIRY_ID_PASSED", "Inquiry ID is Required", inquiryId);
    }
    spatialInquiryService.deleteNote(userId, contextId, inquiryId, noteId);
  }

  /**
   * Assign the requested reviewer for the Geographical Inquiry Support document to review.
   * 
   * @param userId - User id who initiates this request.
   * @param inquiryId - Inquiry Id.
   * @param documentReview- Document Review details.
   */
  @PostMapping("/assign-doc-reviewer")
  @ApiOperation(value="Assign the DEC Program Area Reviewer to review this Geographical Inquiry document")
  public void assignProgramReviewerToDocument(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Inquiry Id") final Long inquiryId, 
      @RequestBody DocumentReview documentReview) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into assignProjectReviewerToDocument. User Id {} , Context Id{} ", userId,
        contextId);
    if (inquiryId == null || inquiryId <= 0) {
      throw new BadRequestException("INVALID_INQUIRY_ID_PASSED", "Inquiry id is Required.", inquiryId);
    }
    if (!(StringUtils.hasText(documentReview.getReviewerName()) 
        && StringUtils.hasText(documentReview.getDateAssigned())
        && StringUtils.hasText(documentReview.getDueDate()))) {

      throw new BadRequestException("INVALID_REVIEW_DETAIL", "Reviewer details details cannot be empty ", documentReview);
    }
    spatialInquiryService.updateDocumentReviewerDetails(userId, contextId, inquiryId, documentReview);
  }
  
  /**
   * Mark the status as Review Complete for the requested review documents.
   * 
   * @param token - JWT Token.
   * @param userId - User who initiates this request.
   * @param inquiryId - Inquiry Id.
   * @param reviewCompletionDetail - Review completion details.
   */
  @PostMapping("/review-complete")
  @ApiOperation(value="Mark the review as completed for the Geographical Inquiry Document requested for the Program Reviewer")
  public void updateGeoInquiryDocReviewCompletion(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long inquiryId, 
      @RequestBody GIReviewCompletionDetail reviewCompletionDetail) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updateGeoInquiryDocReviewCompletion. User Id {} , Context Id{} ", userId,
        contextId);
    if (inquiryId == null || inquiryId <= 0) {
      throw new BadRequestException("INVALID_INQUIRY_ID_PASSED", "Inquiry id is Required.", inquiryId);
    }
    if (reviewCompletionDetail == null) {
      throw new BadRequestException("INVALID_REVIEW_DETAIL", "Review request details should not be empty", reviewCompletionDetail);
    }
    spatialInquiryService.updateDocumentReviewerCompletionDetails(userId, contextId, inquiryId, reviewCompletionDetail);
    logger.info("Existing from updateGeoInquiryDocReviewCompletion. User Id {} , Context Id {} ", userId,
        contextId);
  }
  
  /**
   * Mark whether the user requested to skip the required documents upload process and do it later.
   * 
   * @param userId - User who initiates this request.
   * @param inquiryId - Inquiry Id.
   * 
   */
  @PostMapping("/skip-documents")
  @ApiOperation(value="Mark the user preference of skipping the upload process of required documents.")
  public void skipRequiredDocumentUploadForInquiries(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader  @ApiParam(example = "123213", value="Project Id") final Long inquiryId) {
    
    String contextId = UUID.randomUUID().toString();
    spatialInquiryService.skipRequiredDocumentUploadForInquiries(userId, contextId, inquiryId);
  }
}
