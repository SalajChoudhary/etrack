package dec.ny.gov.etrack.dcs.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.management.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dec.ny.gov.etrack.dcs.entity.DocumentReviewEntity;
import dec.ny.gov.etrack.dcs.entity.EmailCorrespondence;
import dec.ny.gov.etrack.dcs.entity.ProjectNote;
import dec.ny.gov.etrack.dcs.exception.DcsException;
import dec.ny.gov.etrack.dcs.model.CustomMultipartFile;
import dec.ny.gov.etrack.dcs.model.EmailContent;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.ReviewCompletionDetail;
import dec.ny.gov.etrack.dcs.repo.CorrespondenceRepo;
import dec.ny.gov.etrack.dcs.repo.DocumentReviewRepo;
import dec.ny.gov.etrack.dcs.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.dcs.service.SupportDocumentService;

@Service
public class UploadDocumentDisposedProject {

  private static final Logger logger =
      LoggerFactory.getLogger(UploadDocumentDisposedProject.class.getName());

  @Autowired
  private ProjectNoteRepo projectNoteRepo;
  @Autowired
  private DocumentReviewRepo documentReviewRepo;
  @Autowired
  private CorrespondenceRepo emailCorrespondenceRepo;
  @Autowired
  private SupportDocumentService supportDocumentService;

  private final SimpleDateFormat mmDDYYYFormatAMPM = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  private final DateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyyMMddHHmmss");
  private final SimpleDateFormat MM_DD_YYYY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

  /**
   * This is scheduler service to process the Disposed applications. Below are operations will be
   * performed. 1. Read all the eligible Disposed Projects from the table. 2. Read all the notes
   * from E_PROJECT_NOTE table for the project id(s) and prepare the notes document and upload to
   * DMS. 3. Collect all the review requested correspondence for the project id(s) and prepare the
   * notes document and upload to DMS. 4. Delete the project id once its processed successfully.
   */
  @Scheduled(fixedDelay = 5 * Timer.ONE_MINUTE)
  public void processDisposedProjects() {
    String contextId = UUID.randomUUID().toString();
    try {
      logger.info("Entering into process the eligible Disposed Projects. Context Id {}", contextId);
      // Collect all the Project Ids
      List<Long> projectIds = projectNoteRepo.findAllDisposedProjectIds();
      if (!CollectionUtils.isEmpty(projectIds)) {
        projectIds.forEach(projectId -> {
          try {
            List<ProjectNote> projectNotes = projectNoteRepo.findAllByProjectId(projectId);
            if (!CollectionUtils.isEmpty(projectNotes)) {
              logger.info("Processing the all the notes associated with the project id {}",
                  projectId);
              List<ProjectNote> sortedProjectNotes = projectNotes.stream()
                  .sorted(Comparator.comparing(ProjectNote::getProjectNoteId).reversed())
                  .collect(Collectors.toList());
              uploadProjectNotesToDMS("system-authorized", contextId, sortedProjectNotes, projectId);
            }
          } catch (Exception e) {
            logger.error("Error while uploading notes document into into DMS. Context Id {}",
                contextId, e);
            throw e;
          }
          logger.info("Processing the all the notes associated with the project id {} is completed ",
              projectId);
          try {
            
            List<DocumentReviewEntity> documentReviewEntities =
                documentReviewRepo.findAllCorrespondenceDetailsByProjectId(projectId);
            if (!CollectionUtils.isEmpty(documentReviewEntities)) {
              logger.info("Processing the all the Correspondences associated with the project id {}",
                  projectId);
              documentReviewEntities.forEach(documentReviewEntity -> {
                ReviewCompletionDetail reviewCompletionDetail = new ReviewCompletionDetail();
                reviewCompletionDetail
                    .setCorrespondenceId(documentReviewEntity.getCorrespondenceId());
                reviewCompletionDetail.setDocReviewerName(documentReviewEntity.getDocReviewerName());
                reviewCompletionDetail.setDocumentId(documentReviewEntity.getDocumentId());
                reviewCompletionDetail
                    .setDocumentReviewId(documentReviewEntity.getDocumentReviewId());
                reviewCompletionDetail.setReviewerId(documentReviewEntity.getDocReviewerId());
                updateDocumentReviewerCompletionDetails("system-authorized", contextId, null,
                    projectId, reviewCompletionDetail);
              });
            }
            logger.info("Delete the Project Id {} from the Process list "
                + "as its processed successfully. Context Id {}", projectId, contextId);
            projectNoteRepo.deleteProcessedDisposedProjectByProjectId(projectId);
          } catch (Exception e) {
            logger.error("Error while uploading correspondences into DMS. Context Id {}",
                contextId, e);
            throw e;
          }
        });
      } else {
        logger.info("There is no Disposed Projects available to process: Context Id {}", contextId);
      }
    } catch (Exception e) {
      logger.error("Failed to upload either notes document or correspondence into DMS", e);
    }
    
    logger.info("Existing from process the eligible Disposed Projects.Context Id {}", contextId);
  }

  private void updateDocumentReviewerCompletionDetails(String userId, String contextId,
      final String token, Long projectId, ReviewCompletionDetail reviewCompletionDetail) {

    logger.debug("Entering into updateDocumentReviewerCompletionDetails. User Id {}, Context Id {} ", userId, contextId);
    if (!(StringUtils.hasLength(reviewCompletionDetail.getDocReviewerName())
        && reviewCompletionDetail.getDocumentReviewId() != null
        && StringUtils.hasLength(reviewCompletionDetail.getReviewerId()))) {
      throw new DcsException("NO_DOC_REVIEW",
          "One of the field Document Reviewer Id, Reviewer name and Reviewer id is missing");
    }

    logger.debug("Finding all the document reviewer associated with this "
        + "correspondence id {}. User Id {}, Context Id {} ", reviewCompletionDetail.getCorrespondenceId(), userId, contextId);
    List<DocumentReviewEntity> documentReviewsInputCorrespondenceId =
        documentReviewRepo.findByCorrespondenceId(reviewCompletionDetail.getCorrespondenceId());

    if (CollectionUtils.isEmpty(documentReviewsInputCorrespondenceId)) {
      throw new DcsException("NO_DOC_REVIEW",
          "There is no document review available for this Correspondence id "
              + reviewCompletionDetail.getDocumentReviewId());
    }

    logger.info(
        "Reseting this review as incomplete. "
            + "So, delete the existing review document if any. User Id {}, Context Id {}",
        userId, contextId);
    documentReviewRepo.updateDocumentReviewCompletionDetails(userId,
        reviewCompletionDetail.getCorrespondenceId(), reviewCompletionDetail.getReviewerId(), 0);

    StringBuilder documentNameBuilder = new StringBuilder();
    documentNameBuilder.append(reviewCompletionDetail.getDocReviewerName().replace(" ", "_"))
        .append("_").append(reviewCompletionDetail.getCorrespondenceId().toString()).append("%");

    logger.info(
        "Setting this review as complete. " + "So, delete the existing review document {} in"
            + " DMS if present. User Id {}, Context Id {}",
        documentNameBuilder.toString(), userId, contextId);
    List<Long> documentIds = documentReviewRepo.findByDocumentNameAndProjectId(projectId,
        documentNameBuilder.toString());

    logger.info(
        "Delete the documents from DMS associated "
            + "with the document Ids {}, User Id {}, Context Id {}",
        documentIds, userId, contextId);
    if (!CollectionUtils.isEmpty(documentIds)) {
      supportDocumentService.deleteDocument(userId, contextId, projectId, null, documentIds);
    }

    logger.info(
        "Setting this review as complete. "
            + "Upload the correspondence document into DMS User Id {}, Context Id {}",
        userId, contextId);
    documentReviewRepo.updateDocumentReviewCompletionDetails(userId,
        reviewCompletionDetail.getCorrespondenceId(), reviewCompletionDetail.getReviewerId(), 1);
    List<dec.ny.gov.etrack.dcs.entity.EmailCorrespondence> emailCorrespondences =
        emailCorrespondenceRepo.findAllCorrespondenceByReviewerIdAndDocReviewId(
            reviewCompletionDetail.getReviewerId(),
            documentReviewsInputCorrespondenceId.get(0).getCorrespondenceId());

    EmailContent emailCorrespondenceContent =
        retrievePrintableEmailContent(userId, contextId, projectId, emailCorrespondences);
    if (!CollectionUtils.isEmpty(emailCorrespondences)) {
      logger.info("Upload the correspondence document into DMS. User Id {}, Context Id {}", userId,
          contextId);
      uploadEmailCorrespondenceDocumentToDMS(userId, projectId, contextId, reviewCompletionDetail,
          emailCorrespondenceContent.getExistingContents(), token, false,
          documentReviewsInputCorrespondenceId.get(0).getReviewAssignedDate(),
          documentReviewsInputCorrespondenceId.get(0).getReviewDueDate());
    }
    logger.debug("Exiting from updateDocumentReviewerCompletionDetails. User Id {}, Context Id {} ", userId, contextId);
  }

  private void uploadProjectNotesToDMS(final String userId, final String contextId,
      List<ProjectNote> projectNotes, final Long projectId) {

    StringBuilder documentName = new StringBuilder();
    documentName.append("PID_").append(projectId).append("_Notes");

    List<Long> documentIds = documentReviewRepo.findByDocumentNameAndProjectId(projectId,
        documentName.toString());

    logger.info(
        "Delete the existing document notes from DMS associated "
            + "with the document Ids {}, User Id {}, Context Id {}",
        documentIds, userId, contextId);
    if (!CollectionUtils.isEmpty(documentIds)) {
      supportDocumentService.deleteDocument(userId, contextId, projectId, null, documentIds);
    }

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> filesDate = new HashMap<>();
    String currentDate = YYYY_MM_DD_HH_MM_SS.format(new Date());
    filesDate.put("0", currentDate);
    ingestionRequest.setFileDates(filesDate);
    Map<String, String> metadataProperties = new HashMap<>();
    metadataProperties.put("Description", "Note created for this project");
    metadataProperties.put("docCategory", "3");
    metadataProperties.put("docSubCategory", "284");
    metadataProperties.put("docCreationType", "TEXT");
    metadataProperties.put("DocumentTitle", documentName.toString());
    metadataProperties.put("historic", "0");
    metadataProperties.put("docCreator", userId);
    metadataProperties.put("indexDate", currentDate);
    metadataProperties.put("docLastModifier", userId);
    metadataProperties.put("source", "ETRACK");
    metadataProperties.put("projectID", String.valueOf(projectId));
    metadataProperties.put("applicationID", String.valueOf(projectId));
    metadataProperties.put("foilStatus", "NODET");
    metadataProperties.put("deleteFlag", "F");
    metadataProperties.put("renewalNumber", "0");
    metadataProperties.put("modificationNumber", "0");
    metadataProperties.put("trackedAppId", "");
    metadataProperties.put("access", "0");
    metadataProperties.put("nonRelReasonCodes", "");
    metadataProperties.put("receivedDate", currentDate);
    metadataProperties.put("permitType", "");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();
    Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    fontTitle.setSize(20);
    StringBuilder sb = new StringBuilder();
    sb.append("List of Notes generated for the the Project Id : ").append(projectId).append("\n");
    projectNotes.forEach(projectNote -> {
      sb.append(projectNote.getActionNote()).append("\n");
    });
    Paragraph paragraph = new Paragraph(sb.toString());
    document.add(paragraph);
    document.close();

//    ByteArrayResource byteArrayResource = new ByteArrayResource(outputStream.toByteArray()) {
//      @Override
//      public String getFilename() {
//        return documentName.toString() + ".pdf";
//      }
//    };
    CustomMultipartFile customMultipartFile =
        new CustomMultipartFile(
            outputStream.toByteArray(), documentName.toString() + ".pdf", "application/pdf");
    MultipartFile[] files = new CustomMultipartFile[] {customMultipartFile};
    supportDocumentService.uploadSupportDocument(userId, contextId, projectId, null,
        ingestionRequest, files, 2);
  }


  /**
   * This method is used to prepare the Email Correspondence document and upload into DMS.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id
   * @param contextId - Unique UUID to track this request.
   * @param reviewCompletionDetail - Review Completion details.
   * @param emailCorrespondences - Email Correspondences details.
   * @param token - JWT Token
   * @param documentReviewId - Document Review Id.
   * @param reviewAssignedDate - Review Assigned Date.
   * @param reviewDueDate - Review due date.
   */
  private void uploadEmailCorrespondenceDocumentToDMS(final String userId, final Long projectId,
      final String contextId, final ReviewCompletionDetail reviewCompletionDetail,
      final List<String> exitingEmailCorrespondences, final String token,
      boolean aplctCorrespondenceInd, final Date reviewAssignedDate, final Date reviewDueDate) {

    IngestionRequest ingestionRequest = new IngestionRequest();
    Long documentId = reviewCompletionDetail.getCorrespondenceId();
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> filesDate = new HashMap<>();
    String currentDate = YYYY_MM_DD_HH_MM_SS.format(new Date());
    filesDate.put("0", currentDate);
    ingestionRequest.setFileDates(filesDate);
    Map<String, String> metadataProperties = new HashMap<>();
    if (aplctCorrespondenceInd) {
      metadataProperties.put("Description",
          "Applicant amended correspondences which needs to be revisited later");
    } else {
      metadataProperties.put("Description",
          "Review Period from : " + MM_DD_YYYY_FORMAT.format(reviewAssignedDate) + " to : "
              + MM_DD_YYYY_FORMAT.format(reviewDueDate));
    }
    metadataProperties.put("docCategory", "3");
    metadataProperties.put("docSubCategory", "284");
    metadataProperties.put("docCreationType", "TEXT");
    StringBuilder documentName = new StringBuilder();
    if (aplctCorrespondenceInd) {
      documentName.append(reviewCompletionDetail.getDocReviewerName()).append("_")
          .append(currentDate);
    } else {
      documentName.append(reviewCompletionDetail.getDocReviewerName().replace(" ", "_")).append("_")
          .append(documentId);
    }
    metadataProperties.put("DocumentTitle", documentName.toString());
    metadataProperties.put("historic", "0");
    metadataProperties.put("docCreator", userId);
    metadataProperties.put("indexDate", currentDate);
    metadataProperties.put("docLastModifier", userId);
    metadataProperties.put("source", "ETRACK");
    metadataProperties.put("projectID", String.valueOf(projectId));
    if (aplctCorrespondenceInd) {
      metadataProperties.put("applicationID", String.valueOf(documentId));
    } else {
      metadataProperties.put("applicationID",
          String.valueOf(reviewCompletionDetail.getCorrespondenceId()));
    }
    metadataProperties.put("foilStatus", "NODET");
    metadataProperties.put("deleteFlag", "F");
    metadataProperties.put("renewalNumber", "0");
    metadataProperties.put("modificationNumber", "0");
    metadataProperties.put("trackedAppId", "");
    metadataProperties.put("access", "0");
    metadataProperties.put("nonRelReasonCodes", "");
    metadataProperties.put("receivedDate", currentDate);
    metadataProperties.put("permitType", "");
    ingestionRequest.setMetaDataProperties(metadataProperties);
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();
    Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    fontTitle.setSize(20);
    StringBuilder sb = new StringBuilder();
    exitingEmailCorrespondences.forEach(emailCorrespondence -> {
      if (StringUtils.hasLength(emailCorrespondence)) {
        if (!emailCorrespondence.equals("<br>")) {
          emailCorrespondence = emailCorrespondence.replaceAll("<br>", "\n");
          sb.append(emailCorrespondence).append("\n");
        }
      }
    });
    Paragraph paragraph = new Paragraph(sb.toString());
    document.add(paragraph);
    document.close();
    String fileName = null;
    if (aplctCorrespondenceInd) {
      fileName = reviewCompletionDetail.getDocReviewerName() + "_" + currentDate + ".pdf";
    } else {
      fileName = "Project" + "_" + projectId + "_PAR_" + reviewCompletionDetail.getDocReviewerName()
          + "_" + currentDate + ".pdf";
    }
    // ByteArrayResource byteArrayResource = new ByteArrayResource(outputStream.toByteArray()) {
    // @Override
    // public String getFilename() {
    // if (aplctCorrespondenceInd) {
    // return reviewCompletionDetail.getDocReviewerName() + "_" + currentDate + ".pdf";
    // } else {
    // return "Project" + "_" + projectId + "_PAR_" + reviewCompletionDetail.getDocReviewerName()
    // + "_" + currentDate + ".pdf";
    // }
    // }
    // };
    CustomMultipartFile customMultipartFile =
        new CustomMultipartFile(outputStream.toByteArray(), fileName, "application/pdf");
    MultipartFile[] files = new CustomMultipartFile[] {customMultipartFile};
    supportDocumentService.uploadSupportDocument(userId, contextId, projectId, null,
        ingestionRequest, files, 2);
  }

  private EmailContent retrievePrintableEmailContent(final String userId, final String contextId,
      final Long projectId, List<EmailCorrespondence> emailCorrespondences) {

    EmailContent emailContent = new EmailContent();
    List<String> emailContents = new ArrayList<>();
    EmailCorrespondence emailCorrespondence = emailCorrespondences.get(0);
    emailContents.add(emailCorrespondence.getEmailContent());
    emailContent.setTopicId(emailCorrespondence.getTopicId());

    for (int index = 1; index < emailCorrespondences.size(); index++) {
      StringBuilder sb = new StringBuilder();
      EmailCorrespondence existingEmailCorrespondence = emailCorrespondences.get(index);
      sb.append("From: ").append(existingEmailCorrespondence.getFromEmailAdr()).append("\n");
      sb.append("To: ").append(existingEmailCorrespondence.getToEmailAdr()).append("\n");
      if (StringUtils.hasLength(existingEmailCorrespondence.getCcEmailAdr())) {
        sb.append("Cc: ").append(existingEmailCorrespondence.getCcEmailAdr()).append("\n");
      }
      if (existingEmailCorrespondence.getCorrespondenceId()
          .equals(existingEmailCorrespondence.getTopicId())
          && existingEmailCorrespondence.getModifiedDate() != null) {
        sb.append("Sent at: ")
            .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getModifiedDate()))
            .append("\n");
      } else {
        sb.append("Sent at: ")
            .append(mmDDYYYFormatAMPM.format(existingEmailCorrespondence.getCreateDate()))
            .append("\n");
      }
      if (StringUtils.hasLength(existingEmailCorrespondence.getEmailContent())) {
        sb.append("\n").append(existingEmailCorrespondence.getEmailContent()).append("\n");
      }
      sb.append(
          "-----------------------------------------------------------------------------------------------------------")
          .append("\n");
      emailContents.add(sb.toString());
    }
    emailContent.setExistingContents(emailContents);
    return emailContent;
  }
}
