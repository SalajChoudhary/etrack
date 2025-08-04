package dec.ny.gov.etrack.dart.db.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.SpatialInquiryDetailDAO;
import dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryNote;
import dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryNoteConfig;
import dec.ny.gov.etrack.dart.db.entity.SpatialInqDocumentEntity;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDetail;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocument;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocumentReview;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryReviewDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.Document;
import dec.ny.gov.etrack.dart.db.model.GIReviewerDashboardDetail;
import dec.ny.gov.etrack.dart.db.model.GeographicalInquiryNoteView;
import dec.ny.gov.etrack.dart.db.model.GeographicalInquiryResponse;
import dec.ny.gov.etrack.dart.db.model.GeographicalInquirySummary;
import dec.ny.gov.etrack.dart.db.model.InquiryContact;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.dart.db.model.SupportDocument;
import dec.ny.gov.etrack.dart.db.repo.GeographicalInquiryNoteRepo;
import dec.ny.gov.etrack.dart.db.repo.GeographicalInquiryResponseRepo;
import dec.ny.gov.etrack.dart.db.repo.ReviewDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInqDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInquiryDocumentReviewRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInquiryRepo;
import dec.ny.gov.etrack.dart.db.service.SpatialInquiryService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;

@Service
public class SpatialInquiryServiceImpl implements SpatialInquiryService {

  @Autowired
  private SpatialInquiryRepo spatialInquiryRepo;
  @Autowired
  private TransformationService transformationService;
  @Autowired
  private SpatialInquiryDetailDAO spatialInquiryDetailDAO;
  @Autowired
  private GeographicalInquiryNoteRepo geographicalInquiryNoteRepo;
  @Autowired
  private SpatialInqDocumentRepo spatialInqDocumentRepo;
  @Autowired
  private SpatialInquiryDocumentReviewRepo spatialInquiryDocumentReviewRepo;
  @Autowired
  private GeographicalInquiryResponseRepo geographicalInquiryResponseRepo;
  @Autowired
  private ReviewDocumentRepo reviewDocumentRepo;

  private static final Logger logger =
      LoggerFactory.getLogger(SpatialInquiryServiceImpl.class.getName());
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
  private final SimpleDateFormat MM_DD_YYYY_AM_PM_FORMAT =
      new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  private String SYSTEM_USER_ID = "SYSTEM";
  private static Map<String, String> non_releasable_status = new HashMap<>();

  static {
    non_releasable_status.put("NODET", "No Determination");
    non_releasable_status.put("NOREL", "Non - Releasable");
    non_releasable_status.put("REL", "Releasable");
  }


  @Override
  public List<SpatialInquiryRequest> retrieveSpatialInquiryServiceByInquiryType(final String userId,
      final String contextId, final SpatialInquiryCategory inquiryType) {
    List<SpatialInquiryDetail> spatialInquiryDetails = spatialInquiryRepo
        .findByAssignedAnalystIdAndSpatialInqCategoryId(userId, inquiryType.getCategory());
    List<SpatialInquiryRequest> spatialInquiryRequests = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDetails)) {
      spatialInquiryDetails.forEach(spatialInquiryDetail -> {
        spatialInquiryRequests.add(transformationService
            .transformSpatialInquiryRequestToEntity(userId, contextId, spatialInquiryDetail, retrieveInquiryCompleteDate()));
      });
    }
    return spatialInquiryRequests;
  }

  private Map<Long, String> retrieveInquiryCompleteDate() {
    List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses =
        geographicalInquiryResponseRepo.findByInquiryCompletedInd(1);
    Map<Long, String> inquiryIdAndCompletedDate = new HashMap<>();
    if (!CollectionUtils.isEmpty(geographicalInquiryResponses)) {
      geographicalInquiryResponses.forEach(geographicalInquiryResponse -> {
        Date completeDate = geographicalInquiryResponse.getModifiedDate() 
            != null ? geographicalInquiryResponse.getModifiedDate() : geographicalInquiryResponse.getCreateDate();
        inquiryIdAndCompletedDate.put(geographicalInquiryResponse.getInquiryId(),
            dateFormat.format(completeDate));
      });
    }
    return inquiryIdAndCompletedDate;
  }

  @Override
  public Object retrieveSpatialInqDetail(String userId, String contextId, Long inquiryId,
      final String requestorName) {
    if (inquiryId != null && inquiryId > 0) {
      Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail =
          spatialInquiryRepo.findById(inquiryId);
      return transformationService.transformSpatialInquiryRequestToEntity(userId, contextId,
          spatialInquiryDetailsAvail.get(), retrieveInquiryCompleteDate());
    } else if (StringUtils.hasLength(userId)) {
      return spatialInquiryRepo.findByCreatedById(userId);
    } else if (StringUtils.hasLength(requestorName.toLowerCase())) {
      return spatialInquiryRepo.findAllByRequestorName(requestorName.toLowerCase());
    } else {
      throw new BadRequestException("INQUIRY_OR_REQUESTOR_NOT PASSED",
          "Neither requestor name nor Inquiry Id is passed", requestorName);
    }
  }

  @Override
  public Object retrieveRegionalSpatialInquiryServiceByInquiryType(String userId, String contextId,
      SpatialInquiryCategory inquiryType, Integer facilityRegionId) {
    logger.info(
        "Entering into Retrieve all the regional Spatial Inquiry details. User Id {}, Context Id {}",
        userId, contextId);
    List<SpatialInquiryDetail> spatialInquiryDetails = null;
    if (facilityRegionId == null) {
      spatialInquiryDetails =
          spatialInquiryRepo.findBySpatialInqCategoryId(inquiryType.getCategory());
    } else {
      spatialInquiryDetails = spatialInquiryRepo
          .findBySpatialInqCategoryIdAndRegion(inquiryType.getCategory(), facilityRegionId);
    }
    List<SpatialInquiryRequest> spatialInquiryRequests = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDetails)) {
      spatialInquiryDetails.forEach(spatialInquiryDetail -> {
        spatialInquiryRequests.add(transformationService
            .transformSpatialInquiryRequestToEntity(userId, contextId, spatialInquiryDetail, retrieveInquiryCompleteDate()));
      });
    }
    logger.info(
        "Exiting from Retrieve all the regional Spatial Inquiry details. User Id {}, Context Id {}",
        userId, contextId);
    return spatialInquiryRequests;
  }

  @Override
  public Object retrieveSpatialDocumentSummary(String userId, String contextId, Long inquiryId) {
    List<SpatialInquiryDocument> spatialInquiryDocuments =
        spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId);

    Optional<SpatialInquiryDetail> spatialInquiryAvail = spatialInquiryRepo.findById(inquiryId);
    if (!spatialInquiryAvail.isPresent()) {
      throw new BadRequestException("SPATIAL_INQ_NA",
          "Spatial Inquiry is not available for the input inquiry id" + inquiryId, inquiryId);
    }
    Integer spatialInquiryCategoryId = spatialInquiryAvail.get().getSpatialInqCategoryId();
    SupportDocument supportDocument = new SupportDocument();
    if (!CollectionUtils.isEmpty(spatialInquiryDocuments)) {
      List<Document> requiredDocumentsList = new ArrayList<>();
      List<Document> seqrDocumentsList = new ArrayList<>();
      List<Document> shpaDocumentsList = new ArrayList<>();
      List<Document> relatedDocumentList = new ArrayList<>();
      spatialInquiryDocuments.forEach(spatialInqDoc -> {
        if ("A".equals(spatialInqDoc.getDocumentStateCode())) {
          Document document = new Document();
          document.setDocumentTitle(spatialInqDoc.getDocumentTitle());
          document.setDocumentId(spatialInqDoc.getDocumentId());
          document.setRefDocumentDesc(spatialInqDoc.getRefDocumentDesc());
          document.setDocumentTitleId(spatialInqDoc.getDocumentSubTypeTitleId());
          document.setDocumentSubType(spatialInqDoc.getDocumentSubTypeId());
          document.setDocumentType(spatialInqDoc.getDocumentTypeId());
          if (spatialInqDoc.getUploadedInd() != null && spatialInqDoc.getUploadedInd().equals(1)) {
            document.setUploadInd("Y");
          } else {
            document.setUploadInd("N");
          }
          if (spatialInquiryCategoryId != null && spatialInquiryCategoryId.equals(5)) {
            if (spatialInqDoc.getReqdDocumentInd() != null
                && spatialInqDoc.getReqdDocumentInd().equals(1)) {
              requiredDocumentsList.add(document);
            } else if ("SEQR".equals(spatialInqDoc.getRefDocument())
                || spatialInqDoc.getDocumentTypeId().equals(21)) {
              seqrDocumentsList.add(document);
            } else if ("SHPA".equals(spatialInqDoc.getRefDocument())
                || spatialInqDoc.getDocumentTypeId().equals(23)) {
              shpaDocumentsList.add(document);
            } else {
              relatedDocumentList.add(document);
            }
          } else {
            if (!("SEQR".equals(spatialInqDoc.getRefDocument())
                || "SHPA".equals(spatialInqDoc.getRefDocument()))) {
              if (spatialInqDoc.getReqdDocumentInd() != null
                  && spatialInqDoc.getReqdDocumentInd().equals(1)) {
                requiredDocumentsList.add(document);
              } else if (spatialInqDoc.getDocumentTypeId().equals(21)) {
                seqrDocumentsList.add(document);
              } else if (spatialInqDoc.getDocumentTypeId().equals(23)) {
                shpaDocumentsList.add(document);
              } else {
                relatedDocumentList.add(document);
              }
            }
          }
        }
      });
      List<Document> sortedRequiredDocumentsList = requiredDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setRequiredDoc(sortedRequiredDocumentsList);

      List<Document> sortedSeqrDocumentsList = seqrDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setSeqrDoc(sortedSeqrDocumentsList);

      List<Document> sortedShpaDocumentsList = shpaDocumentsList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setShpaDoc(sortedShpaDocumentsList);

      List<Document> sortedRelatedDocumentsList = relatedDocumentList.stream()
          .sorted(Comparator.comparing(Document::getDocumentTitle, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
      supportDocument.setRelatedDoc(sortedRelatedDocumentsList);
    }
    return supportDocument;
  }

  @Override
  public Map<String, Long> retrieveSpatialInquiryStatus(String userId, String contextId,
      Long inquiryId) {
    return spatialInquiryDetailDAO.getSpatialInquiryStatus(userId, contextId, inquiryId);
  }

  @Override
  public Object retrieveGeographicalInquiryForVW(final String userId, final String contextId,
      final Long inquiryId) {
    logger.info("Entering into retrieveGeographicalInquiryForVW "
        + "for the Inquiry Id {}. User Id {}, Context Id {}", inquiryId, userId, contextId);
    Optional<SpatialInquiryDetail> spatialInquiryAvail = spatialInquiryRepo.findById(inquiryId);
    if (!spatialInquiryAvail.isPresent()) {
      throw new BadRequestException("GEOGRAPHICAL_INQ_NA",
          "Geographical Inquiry is not available for the input inquiry id " + inquiryId, inquiryId);
    }
    GeographicalInquirySummary geographicalInquirySummary = new GeographicalInquirySummary();
    List<String> geographicalInquiryCategories =
        spatialInquiryRepo.findInquiryCategoryByInquiryId(inquiryId);

    geographicalInquirySummary.setInquiryId(inquiryId);
    String[] inquiryCategoryIdAndDesc = geographicalInquiryCategories.get(0).split(",");
    geographicalInquirySummary.setInquiryTypeCategory(inquiryCategoryIdAndDesc[1]);
    String inquiryCategoryId = inquiryCategoryIdAndDesc[0];
    SpatialInquiryDetail spatialInquiryDetail = spatialInquiryAvail.get();
    String requestIdentifier = null;
    switch (inquiryCategoryId) {
      case "1":
        List<String> boroughBlockLot = new ArrayList<>();
        if (StringUtils.hasLength(spatialInquiryDetail.getBorough())) {
          boroughBlockLot.add(spatialInquiryDetail.getBorough());
        }
        if (StringUtils.hasLength(spatialInquiryDetail.getBlock())) {
          boroughBlockLot.add(spatialInquiryDetail.getBlock());
        }
        if (StringUtils.hasLength(spatialInquiryDetail.getLot())) {
          boroughBlockLot.add(spatialInquiryDetail.getLot());
        }
        requestIdentifier = String.join("/", boroughBlockLot);
        break;
      case "6":
        requestIdentifier = spatialInquiryDetail.getPlanName();
        break;
      case "7":
        requestIdentifier = spatialInquiryDetail.getExtenderName();
        break;
      default:
        requestIdentifier = spatialInquiryDetail.getProjectName();
        break;
    }
    geographicalInquirySummary.setRequestIdentifier(requestIdentifier);
    geographicalInquirySummary.setProjectName(spatialInquiryDetail.getProjectName());
    geographicalInquirySummary
        .setContact(prepareGeographicalInquiryContact(userId, contextId, spatialInquiryDetail));
    geographicalInquirySummary
        .setAssignedAnalystName(spatialInquiryDetail.getAssignedAnalystName());
    List<GeographicalInquiryNote> geographicalInquiryNotes =
        geographicalInquiryNoteRepo.findNotesByInquiryId(inquiryId);
    geographicalInquirySummary
        .setGeographicalInquiryNotes(prepareGeographicalInquiryNote(geographicalInquiryNotes));
    prepareGeographicalInquiryDocumentAndFilesDetails(userId, contextId, inquiryId,
        geographicalInquirySummary);
    prepareSpatialInquiryReviewDocuments(userId, contextId, inquiryId, geographicalInquirySummary);
    geographicalInquirySummary.setMunicipality(spatialInquiryDetail.getMunicipality());
    geographicalInquirySummary.setRegion(spatialInquiryDetail.getRegion());
    if (spatialInquiryDetail.getOriginalSubmittalDate() != null) {
      geographicalInquirySummary
          .setReceivedDate(dateFormat.format(spatialInquiryDetail.getOriginalSubmittalDate()));
    }
    prepareGeographicalInquiryResponseDetails(userId, contextId, inquiryId,
        geographicalInquirySummary, spatialInquiryDetail);

    logger.info("Exiting from retrieveGeographicalInquiryForVW "
        + "for the Inquiry Id {}. User Id {}, Context Id {}", inquiryId, userId, contextId);
    return geographicalInquirySummary;
  }

  private InquiryContact prepareGeographicalInquiryContact(String userId, String contextId,
      SpatialInquiryDetail spatialInquiryDetail) {
    InquiryContact inquiryContact = new InquiryContact();
    inquiryContact.setPhoneNumber(spatialInquiryDetail.getPhoneNumber());
    inquiryContact.setDepProjectManager(spatialInquiryDetail.getDepProjectManager());
    inquiryContact.setDeveloper(spatialInquiryDetail.getDeveloper());
    inquiryContact.setDowContact(spatialInquiryDetail.getDowContact());
    inquiryContact.setEfcContact(spatialInquiryDetail.getEfcContact());
    inquiryContact.setEmail(spatialInquiryDetail.getEmail());
    inquiryContact.setExtenderName(spatialInquiryDetail.getExtenderName());
    inquiryContact.setLeadAgencyContact(spatialInquiryDetail.getLeadAgencyContact());
    inquiryContact.setLeadAgencyName(spatialInquiryDetail.getLeadAgencyName());
    inquiryContact.setOwner(spatialInquiryDetail.getOwner());
    inquiryContact.setProjectDescription(spatialInquiryDetail.getProjectDescription());
    inquiryContact.setProjectName(spatialInquiryDetail.getProjectName());
    inquiryContact.setProjectSponsor(spatialInquiryDetail.getProjectSponsor());
    inquiryContact.setPscDocketNum(spatialInquiryDetail.getPscDocketNum());
    inquiryContact.setRequestorName(spatialInquiryDetail.getRequestorName());
    inquiryContact.setCity(spatialInquiryDetail.getMailingAddressCity());
    inquiryContact.setStreet1(spatialInquiryDetail.getMailingAddressStreet1());
    inquiryContact.setStreet2(spatialInquiryDetail.getMailingAddressStreet2());
    inquiryContact.setState(spatialInquiryDetail.getMailingAddressState());
    inquiryContact.setZip(spatialInquiryDetail.getMailingAddressZip());
    return inquiryContact;
  }

  private List<GeographicalInquiryNoteView> prepareGeographicalInquiryNote(
      List<GeographicalInquiryNote> geographicalInquiryNotes) {

    List<GeographicalInquiryNoteView> geographicalInquiries = new ArrayList<>();
    if (!CollectionUtils.isEmpty(geographicalInquiryNotes)) {
      geographicalInquiryNotes.forEach(geographicalInquiry -> {
        GeographicalInquiryNoteView geographicalInquiryView = new GeographicalInquiryNoteView();
        geographicalInquiryView.setActionDate(geographicalInquiry.getActionDate());
        geographicalInquiryView.setActionNote(geographicalInquiry.getActionNote());
        geographicalInquiryView.setActionTypeCode(geographicalInquiry.getActionTypeCode());
        geographicalInquiryView.setActionTypeDesc(geographicalInquiry.getActionTypeDesc());
        geographicalInquiryView.setInquiryId(geographicalInquiry.getInquiryId());
        geographicalInquiryView.setInquiryNoteId(geographicalInquiry.getInquiryNoteId());
        geographicalInquiryView.setComments(geographicalInquiry.getComments());
        geographicalInquiryView.setCreateDate(geographicalInquiry.getCreateDate());
        if (StringUtils.hasLength(geographicalInquiry.getCreatedById())
            && SYSTEM_USER_ID.equals(geographicalInquiry.getCreatedById())) {
          geographicalInquiryView.setSystemGenerated("Y");
        } else {
          geographicalInquiryView.setSystemGenerated("N");
        }
        if (geographicalInquiry.getModifiedDate() != null) {
          geographicalInquiryView.setUpdatedDate(
              MM_DD_YYYY_AM_PM_FORMAT.format(geographicalInquiry.getModifiedDate()));
        }
        geographicalInquiryView.setUpdatedBy(geographicalInquiry.getModifiedById());
        geographicalInquiries.add(geographicalInquiryView);
      });
    }
    return geographicalInquiries;
  }

  private void prepareSpatialInquiryReviewDocuments(String userId, String contextId,
      final Long inquiryId, GeographicalInquirySummary geographicalInquirySummary) {

    logger.info(
        "Collecting prepareSpatialInquiryReviewDocuments details for VW. User Id {}, Context Id {}",
        userId, contextId);
    List<SpatialInquiryDocumentReview> reviewDocumentsList =
        spatialInquiryDocumentReviewRepo.findAllReviewDocumentsByInquiryId(inquiryId);

    List<Document> reviewDocumentList = new ArrayList<>();
    Map<Long, Document> reviewDocumentDetailsMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(reviewDocumentsList)) {
      reviewDocumentsList.forEach(uploadDocument -> {
        if (uploadDocument.getDocReviewerName() != null) {

          if (reviewDocumentDetailsMap.get(uploadDocument.getReviewGroupId()) == null) {
            Document reviewDocument = new Document();
            reviewDocument.setDocumentReviewId(uploadDocument.getGiDocumentReviewId());
            reviewDocument.setDocumentId(uploadDocument.getDocumentId());
            // reviewDocument.setDocumentReviewId(uploadDocument.getCorrespondenceId());
            reviewDocument.setDocReviewerName(uploadDocument.getDocReviewerName());
            reviewDocument.setDocReviewerId(uploadDocument.getDocReviewerId());
            reviewDocument.setDescription(uploadDocument.getDocumentDesc());
            reviewDocument.setDocumentTitle(uploadDocument.getDocumentNm());
            if (uploadDocument.getDocReviewedInd() != null
                && uploadDocument.getDocReviewedInd().equals(1)) {
              reviewDocument.setDocReviewedInd("Y");
            } else {
              reviewDocument.setDocReviewedInd("N");
            }
            if (uploadDocument.getReviewAssignedDate() != null) {
              reviewDocument
                  .setReviewAssignedDate(dateFormat.format(uploadDocument.getReviewAssignedDate()));
            }
            if (uploadDocument.getReviewDueDate() != null) {
              reviewDocument.setReviewDueDate(dateFormat.format(uploadDocument.getReviewDueDate()));
            }
            List<String> reviewDocumentNames = new ArrayList<>();
            reviewDocumentNames.add(uploadDocument.getDocumentNm());
            reviewDocument.setDocumentTitles(reviewDocumentNames);
            reviewDocumentDetailsMap.put(uploadDocument.getReviewGroupId(), reviewDocument);
            reviewDocumentList.add(reviewDocument);
          } else {
            reviewDocumentDetailsMap.get(uploadDocument.getReviewGroupId()).getDocumentTitles()
                .add(uploadDocument.getDocumentNm());
            reviewDocumentDetailsMap.get(uploadDocument.getReviewGroupId())
                .setDocumentTitle(String.join(", ", reviewDocumentDetailsMap
                    .get(uploadDocument.getReviewGroupId()).getDocumentTitles()));
          }
        }
      });
    }
    geographicalInquirySummary.setReviewDocuments(reviewDocumentList);
  }

  private void prepareGeographicalInquiryResponseDetails(final String userId,
      final String contextId, final Long inquiryId,
      GeographicalInquirySummary geographicalInquirySummary,
      final SpatialInquiryDetail spatialInquiryDetail) {
    List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses =
        geographicalInquiryResponseRepo.findByInquiryId(inquiryId);

    if (!CollectionUtils.isEmpty(geographicalInquiryResponses)) {
      GeographicalInquiryResponse geographicalInquiryResponse = new GeographicalInquiryResponse();
      dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse response =
          geographicalInquiryResponses.get(0);
      geographicalInquiryResponse.setInqResponseId(response.getInqResponseId());
      if (response.getResponseSentDate() != null) {
        geographicalInquiryResponse
            .setResponseSentDate(dateFormat.format(response.getResponseSentDate()));
      }
      if (response.getResponseSentInd() != null && response.getResponseSentInd().equals(1)) {
        geographicalInquiryResponse.setResponseSentInd("Y");
      } else {
        geographicalInquiryResponse.setResponseSentInd("N");
      }
      if (response.getInquiryCompletedInd() != null
          && response.getInquiryCompletedInd().equals(1)) {
        geographicalInquiryResponse.setInquiryCompletedInd("Y");
      } else {
        geographicalInquiryResponse.setInquiryCompletedInd("N");
      }
      geographicalInquiryResponse.setResponse(response.getResponseText());
      geographicalInquirySummary.setGeographicalInquiryResponse(geographicalInquiryResponse);
    }
  }

  private void prepareGeographicalInquiryDocumentAndFilesDetails(final String userId,
      final String contextId, final Long inquiryId,
      GeographicalInquirySummary geographicalInquirySummary) {

    logger.info("Entering into prepareGeographicalInquiryDocumentAndFilesDetails. "
        + "Inquiry Id {}, User Id {}, Context Id {}", inquiryId, userId, contextId);

    List<SpatialInqDocumentEntity> spatialInquiryDocuments =
        spatialInqDocumentRepo.findAllUploadedSupportDocumentsByInquiryIdWithFilesCount(inquiryId);
    Map<String, Object> documents = new HashMap<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDocuments)) {
      Map<Long, SpatialInqDocumentEntity> documentIdAndFileCount = new HashMap<>();
      spatialInquiryDocuments.forEach(uploadDocument -> {
        Integer fileCount = uploadDocument.getFileCount();
        logger.debug("Document Id {} File Count {}. User Id {}, Context Id {}",
            uploadDocument.getDocumentId(), fileCount, userId, contextId);
        if (fileCount != null) {
          if (documentIdAndFileCount.get(uploadDocument.getDocumentId()) != null) {
            documentIdAndFileCount.get(uploadDocument.getDocumentId()).setFileCount(
                documentIdAndFileCount.get(uploadDocument.getDocumentId()).getFileCount()
                    + fileCount);
          } else {
            documentIdAndFileCount.put(uploadDocument.getDocumentId(), uploadDocument);
          }
        }
      });
      List<String> documentNames = new ArrayList<>();
      List<Document> documentsList = new ArrayList<>();
      int documentLimit = 1;
      int maxDocumentAllowed = 13;
      for (SpatialInqDocumentEntity uploadedDoc : documentIdAndFileCount.values()) {
        documentNames.add(uploadedDoc.getDocumentNm());
        if (documentLimit < maxDocumentAllowed) {
          Document document = new Document();
          document.setDocumentId(uploadedDoc.getDocumentId());
          document.setDescription(uploadedDoc.getDocumentDesc());
          document.setDocumentTitle(uploadedDoc.getDocumentNm());
          document.setRefDocumentDesc(uploadedDoc.getRefDocumentDesc());
          document.setReleasableCode(non_releasable_status.get(uploadedDoc.getDocReleasableCode()));
          document.setFileCount(uploadedDoc.getFileCount());
          if (uploadedDoc.getCreateDate() != null) {
            document.setUploadDate(dateFormat.format(uploadedDoc.getCreateDate()));
            document.setUploadDateFormat(uploadedDoc.getCreateDate());
          }
          documentsList.add(document);
        }
        documentLimit++;
      }
      documents.put("documentNames", documentNames);
      documentsList = documentsList.stream()
          .sorted(Comparator.comparing(Document::getUploadDateFormat, Comparator.reverseOrder()))
          .collect(Collectors.toList());
      documents.put("documents", documentsList);
    }
    geographicalInquirySummary.setDocuments(documents);
    logger.info("Exiting from prepareGeographicalInquiryDocumentAndFilesDetails. "
        + "Inquiry Id {}, User Id {}, Context Id {}", inquiryId, userId, contextId);
  }

  @Override
  public Object retrieveGeographicalNoteConfig(final String userId, final String contextId) {
    List<String> geographicalActiveNotes =
        geographicalInquiryNoteRepo.findAllActiveGeographicalNoteConfig();
    dateFormat.setLenient(false);
    MM_DD_YYYY_AM_PM_FORMAT.setLenient(false);
    List<GeographicalInquiryNoteConfig> geographicalInquiryNoteConfigs = new ArrayList<>();
    if (!CollectionUtils.isEmpty(geographicalActiveNotes)) {
      geographicalActiveNotes.forEach(geographicNoteConfig -> {
        GeographicalInquiryNoteConfig geoGraphicalInquiryNoteConfig =
            new GeographicalInquiryNoteConfig();
        String[] noteConfig = geographicNoteConfig.split(",");
        geoGraphicalInquiryNoteConfig.setActionTypeCode(noteConfig[0]);
        geoGraphicalInquiryNoteConfig.setActionTypeDesc(noteConfig[1]);
        geographicalInquiryNoteConfigs.add(geoGraphicalInquiryNoteConfig);
      });
    }
    return geographicalInquiryNoteConfigs;
  }

  @Override
  public Object getNote(String userId, String contextId, Long inquiryId, Long noteId) {
    logger.info(
        "Entering into retrieve the Geographical Inquiry note details User Id {}, Context Id {}",
        userId, contextId);
    List<GeographicalInquiryNote> geographicalInquiryNotes =
        geographicalInquiryNoteRepo.findNoteByInquiryIdAndNoteId(inquiryId, noteId);
    if (CollectionUtils.isEmpty(geographicalInquiryNotes)) {
      throw new BadRequestException("NO_INQUIRY_NOTE_AVAIL",
          "There is no Geographical Inquiry note available for the input inquiry note Id", noteId);
    }
    GeographicalInquiryNoteView geographicalInquiryNoteView =
        prepareGeographicalInquiryNote(geographicalInquiryNotes).get(0);
    logger.info("Exiting from retrieve the Note details User Id {}, Context Id {}", userId,
        contextId);
    return geographicalInquiryNoteView;
  }

  @Override
  public List<GIReviewerDashboardDetail> getProgramReviewerDashboardDetails(final String userId,
      final String contextId) {
    List<SpatialInquiryReviewDetail> spatialInquiryReviewDetails =
        spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId);
    List<GIReviewerDashboardDetail> geographicalInquiryReviewerDashboardDetails = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryReviewDetails)) {
      spatialInquiryReviewDetails.forEach(spatialInquiryReviewDetail -> {
        geographicalInquiryReviewerDashboardDetails
            .add(transformInquiryReviewDetails(spatialInquiryReviewDetail));
      });
    }
    return geographicalInquiryReviewerDashboardDetails;
  }

  private GIReviewerDashboardDetail transformInquiryReviewDetails(
      SpatialInquiryReviewDetail spatialInquiryReviewDetail) {
    GIReviewerDashboardDetail giReviewerDashboardDetail = new GIReviewerDashboardDetail();
    giReviewerDashboardDetail.setAnalystName(spatialInquiryReviewDetail.getAssignedAnalystName());
    giReviewerDashboardDetail.setCounty(spatialInquiryReviewDetail.getCounty());
    giReviewerDashboardDetail.setMunicipality(spatialInquiryReviewDetail.getMunicipality());
    giReviewerDashboardDetail.setDueDate(spatialInquiryReviewDetail.getReviewDueDate());
    giReviewerDashboardDetail
        .setGiDocumentReviewId(spatialInquiryReviewDetail.getReviewGroupId());
    giReviewerDashboardDetail.setInquiryId(spatialInquiryReviewDetail.getInquiryId());
    giReviewerDashboardDetail
        .setInquiryTypeDesc(spatialInquiryReviewDetail.getSpatialInqCategoryDesc());
    giReviewerDashboardDetail.setPhoneNumber(spatialInquiryReviewDetail.getPhoneNumber());
    giReviewerDashboardDetail
        .setDepProjectManager(spatialInquiryReviewDetail.getDepProjectManager());
    giReviewerDashboardDetail.setDeveloper(spatialInquiryReviewDetail.getDeveloper());
    giReviewerDashboardDetail.setDowContact(spatialInquiryReviewDetail.getDowContact());
    giReviewerDashboardDetail.setEfcContact(spatialInquiryReviewDetail.getEfcContact());
    giReviewerDashboardDetail.setEmail(spatialInquiryReviewDetail.getEmail());
    giReviewerDashboardDetail.setExtenderName(spatialInquiryReviewDetail.getExtenderName());
    giReviewerDashboardDetail
        .setLeadAgencyContact(spatialInquiryReviewDetail.getLeadAgencyContact());
    giReviewerDashboardDetail.setLeadAgencyName(spatialInquiryReviewDetail.getLeadAgencyName());
    giReviewerDashboardDetail.setOwner(spatialInquiryReviewDetail.getOwner());
    giReviewerDashboardDetail
        .setProjectDescription(spatialInquiryReviewDetail.getProjectDescription());
    giReviewerDashboardDetail.setProjectName(spatialInquiryReviewDetail.getProjectName());
    giReviewerDashboardDetail.setProjectSponsor(spatialInquiryReviewDetail.getProjectSponsor());
    giReviewerDashboardDetail.setPscDocketNum(spatialInquiryReviewDetail.getPscDocketNum());
    giReviewerDashboardDetail.setRequestorName(spatialInquiryReviewDetail.getRequestorName());
    giReviewerDashboardDetail.setCity(spatialInquiryReviewDetail.getMailingAddressCity());
    giReviewerDashboardDetail.setStreet1(spatialInquiryReviewDetail.getMailingAddressStreet1());
    giReviewerDashboardDetail.setStreet2(spatialInquiryReviewDetail.getMailingAddressStreet2());
    giReviewerDashboardDetail.setState(spatialInquiryReviewDetail.getMailingAddressState());
    giReviewerDashboardDetail.setZip(spatialInquiryReviewDetail.getMailingAddressZip());
    return giReviewerDashboardDetail;
  }

  @Override
  public Object retrieveEligibleReviewDocuments(String userId, String contextId, Long inquiryId) {
    return reviewDocumentRepo.findAllGIReviewEligibleDocuments(inquiryId);
  }

  @Override
  public Object retrieveAllSpatialInquiryDetails(String userId, String contextId,
      Integer regionId) {
    logger.info(
        "Entering into Retrieve all the regional Spatial Inquiry details. User Id {}, Context Id {}",
        userId, contextId);
    List<SpatialInquiryDetail> spatialInquiryDetails = null;
    if (regionId == null) {
      spatialInquiryDetails = spatialInquiryRepo.findAllSpatialInquiries();
    } else {
      spatialInquiryDetails = spatialInquiryRepo.findBySpatialInquiriesByRegion(regionId);
    }
    List<SpatialInquiryRequest> spatialInquiryRequests = new ArrayList<>();
    if (!CollectionUtils.isEmpty(spatialInquiryDetails)) {
      spatialInquiryDetails.forEach(spatialInquiryDetail -> {
        spatialInquiryRequests.add(transformationService
            .transformSpatialInquiryRequestToEntity(userId, contextId, 
                spatialInquiryDetail, retrieveInquiryCompleteDate()));
      });
    }
    logger.info("Exiting from Retrieve all the regional Spatial Inquiry details. "
        + "User Id {}, Context Id {}", userId, contextId);
    return spatialInquiryRequests;
  }
}
