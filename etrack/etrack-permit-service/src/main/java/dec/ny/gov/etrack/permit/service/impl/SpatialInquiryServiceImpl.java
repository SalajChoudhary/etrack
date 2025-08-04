package dec.ny.gov.etrack.permit.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.permit.entity.GIInquiryAlert;
import dec.ny.gov.etrack.permit.entity.GeoInquiryDocument;
import dec.ny.gov.etrack.permit.entity.GeoInquiryDocumentReview;
import dec.ny.gov.etrack.permit.entity.GeographicalInquiryNote;
import dec.ny.gov.etrack.permit.entity.GeographicalInquiryResponse;
import dec.ny.gov.etrack.permit.entity.SpatialInquiryDetail;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.DocumentReview;
import dec.ny.gov.etrack.permit.model.GIInquiryPolygonAttributes;
import dec.ny.gov.etrack.permit.model.GIInquiryPolygonResponse;
import dec.ny.gov.etrack.permit.model.GIReviewCompletionDetail;
import dec.ny.gov.etrack.permit.model.GeographicalInquiryNoteView;
import dec.ny.gov.etrack.permit.model.GeographicalInquirySubmittalResponse;
import dec.ny.gov.etrack.permit.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.permit.repo.GIInquiryAlertRepo;
import dec.ny.gov.etrack.permit.repo.GeoInquiryDocumentRepo;
import dec.ny.gov.etrack.permit.repo.GeoInquiryDocumentReviewRepo;
import dec.ny.gov.etrack.permit.repo.GeographicalInquiryNoteRepo;
import dec.ny.gov.etrack.permit.repo.GeographicalInquiryResponseRepo;
import dec.ny.gov.etrack.permit.repo.SpatialInquiryRepo;
import dec.ny.gov.etrack.permit.service.SpatialInquiryService;
import dec.ny.gov.etrack.permit.util.ETrackPermitConstant;

@Service
public class SpatialInquiryServiceImpl implements SpatialInquiryService {

  private static final Logger logger = LoggerFactory.getLogger(SpatialInquiryServiceImpl.class.getName());
  
  @Autowired
  private TransformationService transformationService;
  @Autowired
  private SpatialInquiryRepo spatialInquiryRepo;
  @Autowired
  private DocumentUploadService documentUploadService;
  @Autowired
  private GeographicalInquiryNoteRepo geographicalInquiryNoteRepo;
  @Autowired
  private GeographicalInquiryResponseRepo geographicalInquiryResponseRepo;
  @Autowired
  private GeoInquiryDocumentRepo geoInquiryDocumentRepo;
  @Autowired
  private GeoInquiryDocumentReviewRepo geoInquiryDocumentReviewRepo;
  @Autowired
  private GIInquiryAlertRepo giInquiryAlertRepo;
  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
  DateTimeFormatter dateTimeFormattter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
  private final SimpleDateFormat yyyyMMDDdateFormat = new SimpleDateFormat("yyyy-MM-dd");
  
  @Override
  public SpatialInquiryRequest saveSpatialInquiryDetail(String userId, String contextId,
      SpatialInquiryRequest spatialInquiryRequest) {
    
    logger.info("Entering into saveSpatialInquiryDetail. User Id {}, Context Id {}", userId, contextId);
    SpatialInquiryDetail spatialInquiryDetail = transformationService.transformSpatialInquiryRequestToEntity(
        userId, contextId, spatialInquiryRequest);
    spatialInquiryDetail = spatialInquiryRepo.save(spatialInquiryDetail);
    spatialInquiryRequest.setInquiryId(spatialInquiryDetail.getInquiryId());
    logger.info("Exiting from saveSpatialInquiryDetail. User Id {}, Context Id {}", userId, contextId);
    return spatialInquiryRequest;
  }
  
  @Override
  public void uploadSpatialInquiryMap(final String userId, final String contextId, final String jwtToken,
      final Long inquiryId, final String spatialInquiryMapUrl) {
    logger.info("Entering into uploadSpatialInquiryMap. User Id {}, Context Id {}", userId, contextId);
    if (StringUtils.hasLength(spatialInquiryMapUrl)) {
      try {
        documentUploadService.uploadGISPrintFormattedMapDocumentToDMS(userId, contextId, jwtToken, inquiryId,
            spatialInquiryMapUrl, String.valueOf(inquiryId), false, true);
      } catch(ETrackPermitException e) {
        if (StringUtils.hasLength(e.getErrorCode()) 
            && (e.getErrorCode().equals("UNSUCCESSFUL_TO_RETRIEVE_MAP") 
                || e.getErrorCode().equals("UNABLE_TO_RETRIEVE_MAP"))) {
          logger.error("Create a Geographical Inquiry note to inform the Staff about GIS Map retrieval issue. User Id {}, Context Id {}", userId, contextId);
          GeographicalInquiryNote inquiryNote = new GeographicalInquiryNote();
          inquiryNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
          inquiryNote.setCreateDate(new Date());
          inquiryNote.setActionDate(new Date());
          inquiryNote.setInquiryId(inquiryId);
          inquiryNote.setActionTypeCode(5);
          inquiryNote.setActionDate(new Date());
          String formattedInquiryId = String.format("%06d", inquiryId);
          inquiryNote.setActionNote("Failed to retrieve the geographical inquiry map for GID-" 
          + formattedInquiryId + ". Please upload a test document for this inquiry to assign reviewer work-around.");
          geographicalInquiryNoteRepo.save(inquiryNote);          
        }
        throw e;
      }
    }
  }

  @Override
  public void submitSpatialInquiry(
      final String userId, final String contextId, final String jwtToken, final Long inquiryId) {
    logger.info("Entering into submitSpatialInquiry. User Id {}, Context Id {}", userId, contextId);
    Optional<SpatialInquiryDetail> spatialInquiryAvail = spatialInquiryRepo.findById(inquiryId);
    if (!spatialInquiryAvail.isPresent()) {
      throw new BadRequestException("SPATIAL_INQ_NA", "Geographical Inquiry is "
          + "not available for the input requested Inquiry id " + inquiryId , inquiryId);
    }
    SpatialInquiryDetail spatialInquiryDetail = spatialInquiryAvail.get();
    spatialInquiryDetail.setModifiedById(userId);
    spatialInquiryDetail.setModifiedDate(new Date());
    spatialInquiryDetail.setOriginalSubmittalInd(1);
    spatialInquiryDetail.setOriginalSubmittalDate(new Date());
    spatialInquiryRepo.save(spatialInquiryDetail);
    
    GeographicalInquiryNote geographicalInquiryNote = new GeographicalInquiryNote();
    geographicalInquiryNote.setActionDate(new Date());
    String formattedInquiryId = String.format("%06d", inquiryId); 
        geographicalInquiryNote.setActionNote("GID-" + formattedInquiryId + " has been submitted");
    geographicalInquiryNote.setCreateDate(new Date());
    geographicalInquiryNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    geographicalInquiryNote.setActionTypeCode(1);
    geographicalInquiryNote.setInquiryId(inquiryId);
    geographicalInquiryNoteRepo.save(geographicalInquiryNote);
    requestGISToUpdatePolygonInquiryResponseDetail(userId, contextId, jwtToken, 
        spatialInquiryAvail.get(), new Date(), true);
    logger.info("Exiting from submitSpatialInquiry. User Id {}, Context Id {}", userId, contextId);
  }

  @Override
  public void submitSpatialInquiryResponse(final String userId, final String contextId, 
      final Long inquiryId, final String jwtToken,
      final GeographicalInquirySubmittalResponse submittalResponse) {
    
    logger.info("Entering into submitSpatialInquiryResponse. "
        + "Inquiry Id {}, User Id {}, Context Id {}", inquiryId, userId, contextId);

    Optional<SpatialInquiryDetail> spatialInquiryAvail = spatialInquiryRepo.findById(inquiryId);
    if (!spatialInquiryAvail.isPresent()) {
      throw new BadRequestException("SPATIAL_INQ_NA", "Geographical Inquiry is "
          + "not available for the input requested Inquiry id " + inquiryId , inquiryId);
    }
    
    GeographicalInquiryResponse geographicalInquiryResponse = null;
    List<GeographicalInquiryResponse> submittalInquiryResponses = geographicalInquiryResponseRepo.findByInquiryId(
        inquiryId);
    Date currentDate = new Date();
    if (!CollectionUtils.isEmpty(submittalInquiryResponses)) {
      geographicalInquiryResponse = submittalInquiryResponses.get(0);
      geographicalInquiryResponse.setModifiedById(userId);
      geographicalInquiryResponse.setModifiedDate(currentDate);
    } else {
      geographicalInquiryResponse = new GeographicalInquiryResponse();
      geographicalInquiryResponse.setCreatedById(userId);
      geographicalInquiryResponse.setCreateDate(currentDate);
    }
    if (StringUtils.hasLength(submittalResponse.getResponseSentInd()) 
        && "Y".equals(submittalResponse.getResponseSentInd())) {
      geographicalInquiryResponse.setResponseSentInd(1);
    } else {
      geographicalInquiryResponse.setResponseSentInd(0);
    }
    boolean isInquiryCompleted = false;
    if (StringUtils.hasLength(submittalResponse.getInquiryCompletedInd()) 
        && "Y".equals(submittalResponse.getInquiryCompletedInd())) {
      geographicalInquiryResponse.setInquiryCompletedInd(1);
      isInquiryCompleted = true;
    } else {
      geographicalInquiryResponse.setInquiryCompletedInd(0);
    }
    try {
      if (StringUtils.hasLength(submittalResponse.getResponseSentDate())) {
        LocalDate.parse(submittalResponse.getResponseSentDate(), dateTimeFormattter);
        geographicalInquiryResponse.setResponseSentDate(dateFormat.parse(submittalResponse.getResponseSentDate()));
      } else {
        geographicalInquiryResponse.setResponseSentDate(null);
      }
    } catch (DateTimeParseException | ParseException e) {
      throw new BadRequestException("SPATIAL_INQ_RESPONSE_DT_INVALID", 
          "Geographical Inquiry submittal response date is not an valid format "
          + "for the input requested Inquiry id " + inquiryId , inquiryId);
    }
    geographicalInquiryResponse.setResponseText(submittalResponse.getResponse());
    geographicalInquiryResponse.setInquiryId(inquiryId);
    
    if (isInquiryCompleted) {
      logger.info("Call GIS Service to update the Inquiry Response details. User Id {}, Context Id {}", userId, contextId);
      requestGISToUpdatePolygonInquiryResponseDetail(userId, contextId, jwtToken, 
          spatialInquiryAvail.get(), geographicalInquiryResponse.getResponseSentDate(), false);
    }
    geographicalInquiryResponseRepo.save(geographicalInquiryResponse);
    logger.info("Exiting from submitSpatialInquiryResponse. "
        + "Inquiry Id {}, User Id {}, Context Id {}", inquiryId, userId, contextId);
  }

  private void requestGISToUpdatePolygonInquiryResponseDetail(
      final String userId, final String contextId, final String jwtToken,
      SpatialInquiryDetail spatialInquiryDetail, Date responseDate, final boolean inquirySubmittalInd) {
    try {
      GIInquiryPolygonResponse giInquiryPolygonResponse = new GIInquiryPolygonResponse();
      GIInquiryPolygonAttributes attributes = new GIInquiryPolygonAttributes();
      attributes.setObjectId(Long.valueOf(spatialInquiryDetail.getPolygonId()));
      if (inquirySubmittalInd) {
        attributes.setReceivedDate(yyyyMMDDdateFormat.format(responseDate));
      } else if (responseDate != null) {
        attributes.setResponseDate(yyyyMMDDdateFormat.format(responseDate));
      }
      giInquiryPolygonResponse.setAttributes(attributes);
      List<GIInquiryPolygonResponse> giInquiryPolygonResponses = new ArrayList<>();
      giInquiryPolygonResponses.add(giInquiryPolygonResponse);
      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("contextId", contextId);
      headers.add(HttpHeaders.AUTHORIZATION, jwtToken);
      HttpEntity<List<GIInquiryPolygonResponse>> requestEntity =
          new HttpEntity<>(giInquiryPolygonResponses, headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-gis/upload-inq-response").build().toString();
      logger.info(
          "Making a call to eTrack-gis-service to invoke upload the Inquiry Response details.");
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, Void.class);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      logger.error("Error while uploading the inquiry response details to GIS system. Status code {} ", ex.getRawStatusCode());
      throw new ETrackPermitException("UNABLE_TO_UPLOAD_INQ_RESP", "Unable to upload the Inquiry response details into GIS");
    }
  }
  
  @Transactional
  @Override
  public void updateGeographicalInquiryAssignment(String userId, String contextId, Long inquiryId,
      AssignmentNote assignmentNote) {
    logger.info("Entering into updating the assignment details for the Geographical Inquiry {}. User Id {}. Context Id {} ",
        inquiryId, userId, contextId);
    spatialInquiryRepo.updateGeographicalInquiryAssignmentDetails(userId, inquiryId, assignmentNote.getAnalystId(),
        assignmentNote.getAnalystName());
    logger.info("Updating the Geographical Inquiry note details. User Id {}, Context Id {}", userId, contextId);
    GeographicalInquiryNote inquiryNote = new GeographicalInquiryNote();
    inquiryNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    inquiryNote.setCreateDate(new Date());
    inquiryNote.setActionDate(new Date());
    inquiryNote.setInquiryId(inquiryId);
    inquiryNote.setActionTypeCode(2);
    inquiryNote.setActionDate(new Date());
    inquiryNote.setComments(assignmentNote.getComments());
    String formattedInquiry = String.format("%06d", inquiryId);
    inquiryNote.setActionNote(assignmentNote.getAnalystName().toUpperCase()
        + " has been assigned to GID-" + formattedInquiry);
    
    inquiryNote = geographicalInquiryNoteRepo.save(inquiryNote);
    GIInquiryAlert giInquiryAlert = new GIInquiryAlert();
    giInquiryAlert.setInquiryId(inquiryId);
    StringBuilder sb = new StringBuilder();
    sb.append(assignmentNote.getAnalystName().toUpperCase()).append(" has been assigned to GID-").append(formattedInquiry);
    if (StringUtils.hasLength(assignmentNote.getComments())) {
      sb.append(" View the assignment note");
    }
    giInquiryAlert.setAlertNote(sb.toString());
    giInquiryAlert.setAlertDate(new Date());
    giInquiryAlert.setCreateDate(new Date());
    giInquiryAlert.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
    giInquiryAlert.setMsgReadInd(0);
    giInquiryAlert.setAlertRcvdUserId(assignmentNote.getAnalystId());
    giInquiryAlert.setInquiryNoteId(inquiryNote.getInquiryNoteId());
    giInquiryAlertRepo.save(giInquiryAlert);
    logger.info("Exiting from updating the assignment details for the Geographical Inquiry {}. "
        + "User Id {}. Context Id {} ", inquiryId, userId, contextId);
  }

  @Override
  public GeographicalInquiryNoteView addNotes(String userId, String contextId, Long inquiryId,
      GeographicalInquiryNoteView geographicalInquiryNoteView) {
    
    GeographicalInquiryNote geographicalInquiryNote = null;
    dateFormat.setLenient(false);
    if (!(StringUtils.hasLength(geographicalInquiryNoteView.getActionDate()) 
        && StringUtils.hasLength(geographicalInquiryNoteView.getActionNote()) 
        && (geographicalInquiryNoteView.getActionTypeCode() != null && geographicalInquiryNoteView.getActionTypeCode() > 0))) {
      throw new BadRequestException("INQ_REQD_PARAMETER_MISSING", 
          "One or more required parameter missing", geographicalInquiryNote);
    }
    
    if (geographicalInquiryNoteView.getInquiryNoteId() != null 
        && geographicalInquiryNoteView.getInquiryNoteId() > 0) {
      Optional<GeographicalInquiryNote> geoOptional = geographicalInquiryNoteRepo.findById(geographicalInquiryNoteView.getInquiryNoteId());
      if (!geoOptional.isPresent()) {
        throw new BadRequestException("INQUIRY_NOTE_NA", "Geographical Inquiry note available to update", geoOptional);
      }
      geographicalInquiryNote = geoOptional.get();
      geographicalInquiryNote.setModifiedById(userId);
      geographicalInquiryNote.setModifiedDate(new Date());
    } else {
      geographicalInquiryNote = new GeographicalInquiryNote();
      geographicalInquiryNote.setCreatedById(userId);
      geographicalInquiryNote.setCreateDate(new Date());
    }
    try {
      LocalDate.parse(geographicalInquiryNoteView.getActionDate(), dateTimeFormattter);
      geographicalInquiryNote.setActionDate(dateFormat.parse(geographicalInquiryNoteView.getActionDate()));
    } catch (DateTimeParseException | ParseException e) {
      throw new BadRequestException("ACTION_DATE_INVALID", "Action Date passed not a valid one", geographicalInquiryNote);
    }
    geographicalInquiryNote.setActionNote(geographicalInquiryNoteView.getActionNote());
    geographicalInquiryNote.setComments(geographicalInquiryNoteView.getComments());
    geographicalInquiryNote.setInquiryId(geographicalInquiryNoteView.getInquiryId());
    geographicalInquiryNote.setActionTypeCode(geographicalInquiryNoteView.getActionTypeCode());
    geographicalInquiryNote.setComments(geographicalInquiryNoteView.getComments());
    geographicalInquiryNote = geographicalInquiryNoteRepo.save(geographicalInquiryNote);
    geographicalInquiryNoteView.setInquiryNoteId(geographicalInquiryNote.getInquiryNoteId());
    return geographicalInquiryNoteView;
  }

  @Override
  public void deleteNote(String userId, String contextId, Long inquiryId, Long noteId) {
    logger.info("Entering into deleting the Geographical Inquiry note. User Id {}, Context Id {}", userId, contextId);
    Optional<GeographicalInquiryNote> geoOptional = geographicalInquiryNoteRepo.findById(noteId);
    if (!geoOptional.isPresent()) {
      throw new BadRequestException("INQUIRY_NOTE_NA", "Geographical Inquiry note available to delete", geoOptional);
    }
    geographicalInquiryNoteRepo.delete(geoOptional.get());
    logger.info("Exiting from deleting the Geographical Inquiry note. User Id {}, Context Id {}", userId, contextId);
  }

  @Override
  public void updateDocumentReviewerDetails(String userId, String contextId, Long inquiryId,
      DocumentReview documentReview) {
    try {
      dateFormat.setLenient(false);
      LocalDate.parse(documentReview.getDateAssigned(), dateTimeFormattter);
      LocalDate.parse(documentReview.getDueDate(), dateTimeFormattter);
      final Date assignedDate = dateFormat.parse(documentReview.getDateAssigned());
      final Date dueDate = dateFormat.parse(documentReview.getDueDate());
      Date currentDate = dateFormat.parse(dateFormat.format(new Date()));

      if (assignedDate.before(currentDate) || assignedDate.after(dueDate)
          || dueDate.before(currentDate)) {
        throw new BadRequestException("INVALID_REQ", "Invalid dates are passed", documentReview);
      }

      if (CollectionUtils.isEmpty(documentReview.getDocumentIds())) {
        throw new BadRequestException("NO_DOCUMENT_IDs_PASSED", 
            "No Document ids passed to assign Program Reviewer", documentReview);
      }

      List<GeoInquiryDocument> geoInquiryDocumentList = 
          geoInquiryDocumentRepo.findAllByInquiryIdDocumentIds(inquiryId, documentReview.getDocumentIds());
      
      if (CollectionUtils.isEmpty(geoInquiryDocumentList)) {
        throw new BadRequestException("NO_DOCUMENTS_AVAIL", 
            "There is no documents available to assign Program Reviewer", documentReview);
      }
      
      if (geoInquiryDocumentList.size() != documentReview.getDocumentIds().size()) {
        throw new BadRequestException("DOCUMENTS_MISSING", 
            "One or more documents not available to assign Program Reviewer", documentReview);
      }
      
      Long documentReviewId = 0L;
      for (Long documentId : documentReview.getDocumentIds()) {
        GeoInquiryDocumentReview geoInquiryDocumentReview = new GeoInquiryDocumentReview();
        geoInquiryDocumentReview.setDocumentId(documentId);
        geoInquiryDocumentReview.setCreateDate(currentDate);
        geoInquiryDocumentReview.setCreatedById(userId);
        geoInquiryDocumentReview.setDocReviewerId(documentReview.getReviewerId());
        geoInquiryDocumentReview.setDocReviewerName(documentReview.getReviewerName());
        geoInquiryDocumentReview.setAssignedReviewerRoleId(documentReview.getReviewerRoleId());
        geoInquiryDocumentReview.setReviewAssignedDate(assignedDate);
        geoInquiryDocumentReview.setReviewDueDate(dueDate);
        if (documentReviewId > 0) {
          geoInquiryDocumentReview.setReviewGroupId(documentReviewId);
        }
        geoInquiryDocumentReview = geoInquiryDocumentReviewRepo.save(geoInquiryDocumentReview);
        if (documentReviewId == 0) {
          geoInquiryDocumentReview.setReviewGroupId(geoInquiryDocumentReview.getGiDocumentReviewId());
          geoInquiryDocumentReviewRepo.save(geoInquiryDocumentReview);
          documentReviewId = geoInquiryDocumentReview.getGiDocumentReviewId();
        }
      }
      GeographicalInquiryNote inquiryNote = new GeographicalInquiryNote();
      inquiryNote.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
      inquiryNote.setCreateDate(new Date());
      inquiryNote.setActionDate(new Date());
      inquiryNote.setInquiryId(inquiryId);
      inquiryNote.setActionTypeCode(4);
      inquiryNote.setActionDate(new Date());
      String formattedInquiry = String.format("%06d", inquiryId);
      inquiryNote.setActionNote(documentReview.getReviewerName().toUpperCase()
          + " has been assigned to GID-" + formattedInquiry);
      inquiryNote = geographicalInquiryNoteRepo.save(inquiryNote);
      
      GIInquiryAlert giInquiryAlert = new GIInquiryAlert();
      giInquiryAlert.setInquiryId(inquiryId);
      StringBuilder sb = new StringBuilder();
      sb.append(documentReview.getReviewerName().toUpperCase()).append(" has been assigned to GID-").append(formattedInquiry);
      giInquiryAlert.setAlertNote(sb.toString());
      giInquiryAlert.setAlertDate(new Date());
      giInquiryAlert.setCreateDate(new Date());
      giInquiryAlert.setCreatedById(ETrackPermitConstant.SYSTEM_USER_ID);
      giInquiryAlert.setMsgReadInd(0);
      giInquiryAlert.setAlertRcvdUserId(documentReview.getReviewerId());
      giInquiryAlert.setInquiryNoteId(inquiryNote.getInquiryNoteId());
      giInquiryAlertRepo.save(giInquiryAlert);
    } catch (DateTimeParseException | ParseException e) {
      logger.error("Invalid date fields are passed. User Id {} , Context Id {}", userId, contextId, e);
      throw new BadRequestException("INVALID_DATE_REQ", "Dates are not a valid format.",
          documentReview);
    }    
  }

  @Transactional
  @Override
  public void updateDocumentReviewerCompletionDetails(final String userId, final String contextId,
      final Long inquiryId, GIReviewCompletionDetail reviewCompletionDetail) {
    
    if (!(StringUtils.hasLength(reviewCompletionDetail.getDocReviewerName())
        && reviewCompletionDetail.getDocumentReviewId() != null
        && StringUtils.hasLength(reviewCompletionDetail.getReviewerId()))) {
      throw new BadRequestException("NO_DOC_REVIEW",
          "One of the field Document Reviewer Id, Reviewer name and Reviewer id is missing",
          reviewCompletionDetail);
    }
    Integer documentReviewedInd = 0;
    if (StringUtils.hasLength(reviewCompletionDetail.getDocReviewedInd()) 
        && "Y".equals(reviewCompletionDetail.getDocReviewedInd())) {
      documentReviewedInd = 1;
    }
    geoInquiryDocumentReviewRepo.updateDocumentReviewCompletionDetails(
        userId, reviewCompletionDetail.getDocumentReviewId(), 
        reviewCompletionDetail.getDocumentId(), reviewCompletionDetail.getReviewerId(), documentReviewedInd);
  }

  @Transactional
  @Override
  public void skipRequiredDocumentUploadForInquiries(String userId, String contextId,
      Long inquiryId) {
    spatialInquiryRepo.updateSkipDocumentUploadProcess(userId, inquiryId);
  }
}
