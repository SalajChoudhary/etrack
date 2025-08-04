package gov.ny.dec.district.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import gov.ny.dec.dart.district.model.AttachedFile;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.dart.district.model.Document;
import gov.ny.dec.district.etrack.entity.ETrackDocumentFile;
import gov.ny.dec.district.etrack.entity.ETrackDocumentNonRelDetails;
import gov.ny.dec.district.etrack.entity.SubmitDocument;
import gov.ny.dec.district.etrack.entity.SupportDocNonRelReasonDetail;
import gov.ny.dec.district.etrack.entity.SupportDocument;
import gov.ny.dec.district.etrack.entity.SupportDocumentFile;
import gov.ny.dec.district.etrack.repository.SupportDocumentRepo;
import gov.ny.dec.district.exception.DARTDistrictServiceException;
import gov.ny.dec.district.exception.ValidationException;

@Component
public class DistrictResponseHandler {

  private static final Map<String, String> REL_CODES;

  @Autowired
  SupportDocumentRepo supportDocumentRepo;

  static {
    REL_CODES = new HashMap<>();
    REL_CODES.put("NODET", "No Determination");
    REL_CODES.put("NOREL", "Non - Releasable");
    REL_CODES.put("REL", "Releasable");
  }

  private static Logger logger = LoggerFactory.getLogger(DistrictResponseHandler.class.getName());
  private SimpleDateFormat mmDDYYYFormatAMPM = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  
  /**
   * Transform the District details and documents from the database.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param submittedDocuments - List of uploaded historical documents.
   * 
   * @return - Transformed {@link DistrictDetail}
   */
  public DistrictDetail transformDistrictDetails(final String userId, final String contextId, List<SubmitDocument> submittedDocuments) {
    logger.info("Entering into transformDistrictDetails(). User Id {}, Context Id {}", userId, contextId);
    DistrictDetail districtDetail = null;
    List<Document> documents = null;
    try {
      if (!CollectionUtils.isEmpty(submittedDocuments)) {
        districtDetail = new DistrictDetail();
        documents = new ArrayList<>();
        for (SubmitDocument submitDocument : submittedDocuments) {
          Document document = new Document();
          document.setDocumentId(submitDocument.getDocumentId());
          document.setAccessByDepOnly(submitDocument.getAccessByDepOnlyInd());
          document.setDocumentReleasableCode(submitDocument.getDocReleasableCode());
          document.setDocumentReleasableDesc(REL_CODES.get(submitDocument.getDocReleasableCode()));
          document.setDocCategory(submitDocument.getDocumentTypeId());
          document.setDocSubCategory(submitDocument.getDocumentSubTypeId());
          document.setDocumentStateCode(submitDocument.getDocumentStateCode());
          document.setDescription(submitDocument.getDocumentDesc());
          document.setDocumentName(submitDocument.getDocumentNm());
          document.setOtherDocSubCategory(submitDocument.getDocSubTypeOtherTxt());
          document.setCreatedBy(submitDocument.getCreatedById());
          document.setModifiedBy(submitDocument.getModifiedById());
          if (submitDocument.getCreateDate() != null) {
            document.setUploadDateTime(mmDDYYYFormatAMPM.format(submitDocument.getCreateDate()));
          }
          
          document.setTrackedApplicationId(submitDocument.getTrackedApplicationId());
          Set<ETrackDocumentFile> documentFiles = submitDocument.getETrackDocumentFile();
          if (!CollectionUtils.isEmpty(documentFiles)) {
            List<AttachedFile> files = new ArrayList<>();
            Iterator<ETrackDocumentFile> documentFile = documentFiles.iterator();
            while (documentFile.hasNext()) {
              ETrackDocumentFile file = documentFile.next();
              AttachedFile attachedFile = new AttachedFile();
              attachedFile.setFileNbr(file.getFileNbr());
              attachedFile.setFileName(file.getFileNm());
              attachedFile.setFileDate(file.getFileDate());
              files.add(attachedFile);
            }
            document.setFiles(files);
          }
          Set<ETrackDocumentNonRelDetails> nonReleasableDetails =
              submitDocument.getETrackDocumentNonRelDetails();
          if (!CollectionUtils.isEmpty(nonReleasableDetails)) {
            List<String> nonReleasableCodes = new ArrayList<>();
            Iterator<ETrackDocumentNonRelDetails> nonReleasable = nonReleasableDetails.iterator();
            while (nonReleasable.hasNext()) {
              nonReleasableCodes.add(nonReleasable.next().getDocNonRelReasonCode());
            }
            document.setDocNonRelReasonCodes(nonReleasableCodes);
          }
          documents.add(document);
        }
        districtDetail.setDocuments(documents);
      } else {
        logger.debug("The district Id has no related documents. User Id {}, Context Id {}", userId, contextId);
        districtDetail = new DistrictDetail();
        districtDetail.setDocuments(new ArrayList<>());
      }
    } catch (Exception e) {
      populateLoggerExeptionMap("transformDistrictDetails()",
          "Mapping response data to Submitted Document", "No applicable Id", e.getMessage());
      throw new DARTDistrictServiceException("Exception while handling response data", e);
    }
    logger.info("Exiting from transformDistrictDetails(). User Id {}, Context Id {}", userId, contextId);
    return districtDetail;
  }

  
  /**
   * Transform the District and Support document details received from the database.
   * 
   * @param supportDocuments - List of Support documents uploaded for this district Id.
   * @param districtDetail - District details.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Transformed District details.
   */
  public DistrictDetail transformSupportDocumentDistrictDetails(List<SupportDocument> supportDocuments, 
      DistrictDetail districtDetail, final String userId, final String contextId) {
    
    logger.info("Entering transformSupportDocumentDistrictDetails(). User Id {}, Context Id {}", userId, contextId);
    List<Document> documents = null;
    try {
      if (!CollectionUtils.isEmpty(supportDocuments)) {
        if (districtDetail == null) {
          districtDetail = new DistrictDetail();
          documents = new ArrayList<>();
        } else {
          documents = districtDetail.getDocuments();
        }
        for (SupportDocument supportDocument : supportDocuments) {
          if (CollectionUtils.isEmpty(supportDocument.getDocFiles()) 
              && supportDocument.getRefDocumentId() != null) {
            logger.info("Have to pull the reference document instead of Original one. User Id {} , Context Id {} ", userId, contextId);
            Optional<SupportDocument> referenceSupportDocument = supportDocumentRepo.findById(supportDocument.getRefDocumentId());
            if (!referenceSupportDocument.isPresent()) {
              throw new ValidationException("NO_DOCUMENT_FOUND", "There is no document "
                  + "associated for this document id " + supportDocument.getDocumentId());
            }
            documents.add(prepareDocument(supportDocument, referenceSupportDocument.get()));
          } else {
            logger.debug("Document added to the District details. "
                + "Document Id {}, User Id {}, Context Id {}", supportDocument.getDocumentId(), userId, contextId);
            documents.add(prepareDocument(supportDocument, null));
          }
        }
        districtDetail.setDocuments(documents);
      } else {
        logger.debug("The district Id has no related support documents.");
        districtDetail = new DistrictDetail();
        districtDetail.setDocuments(new ArrayList<>());
      }
    } catch (ValidationException ve) {
      throw ve;
    } catch (Exception e) {
       populateLoggerExeptionMap("transformSupportDocumentDistrictDetails()", "Mapping response data to Support Document", "No applicable Id", e.getMessage());
      throw new DARTDistrictServiceException("Exception while handling response data", e);
    }
    logger.info("Exiting transformSupportDocumentDistrictDetails(). User Id {}, Context Id {}", userId, contextId);
    return districtDetail;
  }

  private Document prepareDocument(SupportDocument supportDocument, SupportDocument referenceDocument) {
    Document document = new Document();
    document.setDocumentId(supportDocument.getDocumentId());
    if (supportDocument.getAccessByDepOnlyInd() != null) {
      document.setAccessByDepOnly(String.valueOf(supportDocument.getAccessByDepOnlyInd()));
    }
    document.setDocumentReleasableCode(supportDocument.getDocReleasableCode());
    document.setDocumentReleasableDesc(REL_CODES.get(supportDocument.getDocReleasableCode()));
    document.setDocCategory(supportDocument.getDocumentTypeId());
    document.setDocSubCategory(supportDocument.getDocumentSubTypeId());
    document.setDocumentStateCode(supportDocument.getDocumentStateCode());
    document.setDescription(supportDocument.getDocumentDesc());
    document.setDocumentName(supportDocument.getDocumentNm());
    document.setOtherDocSubCategory(supportDocument.getDocSubTypeOtherTxt());
    document.setCreatedBy(supportDocument.getCreatedById());
    document.setModifiedBy(supportDocument.getModifiedById());
    document.setProjectId(supportDocument.getProjectId());
    document.setDocumentReviewedInd(supportDocument.getArchiveCompletedInd());
    document.setQueryResultId(supportDocument.getArcPrgQueryResultId());
    if (supportDocument.getCreateDate() != null) {
      document.setUploadDateTime(mmDDYYYFormatAMPM.format(supportDocument.getCreateDate()));
    }
    document.setTrackedApplicationId(supportDocument.getTrackedApplicationId());

    Set<SupportDocumentFile> documentFiles = null;
    if (referenceDocument != null) {
      document.setDocumentId(referenceDocument.getDocumentId());
      documentFiles = referenceDocument.getDocFiles();
    } else {
      documentFiles = supportDocument.getDocFiles();
    }
    if (!CollectionUtils.isEmpty(documentFiles)) {
      List<AttachedFile> files = new ArrayList<>();
      for (SupportDocumentFile documentFile : documentFiles) {
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFileNbr(documentFile.getFileNumber());
        attachedFile.setFileName(documentFile.getFileName());
        if (documentFile.getFileDate() != null) {
          attachedFile.setFileDate(new Timestamp(documentFile.getFileDate().getTime()));
        }
        files.add(attachedFile);
      }
      document.setFiles(files);
    }
    Set<SupportDocNonRelReasonDetail> nonReleasableDetails =
        supportDocument.getDocNonRelReasons();
    if (!CollectionUtils.isEmpty(nonReleasableDetails)) {
      List<String> nonReleasableCodes = new ArrayList<>();
      for (SupportDocNonRelReasonDetail nonRelReasonDetail : nonReleasableDetails) {
        nonReleasableCodes.add(nonRelReasonDetail.getDocNonRelReasonCode());
      }
      document.setDocNonRelReasonCodes(nonReleasableCodes);
    }
    return document;
  }

  private void populateLoggerExeptionMap(String methodName, String eventName, String applicableId,
      String errorMessage) {
    logger.debug("Entering populateLoggerExceptionMap().");
    Map<String, String> loggingMap = new HashMap<>();
    loggingMap.put("Application name", "eTrack");
    loggingMap.put("Method name", methodName);
    loggingMap.put("Event name", eventName);
    loggingMap.put("Applicable ids", applicableId);
    loggingMap.put("Error message", errorMessage);
    logger.error(loggingMap.toString());
    loggingMap = null;
  }
}
