package dec.ny.gov.etrack.dcs.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dcs.exception.ValidationException;
import dec.ny.gov.etrack.dcs.model.IngestionRequest;
import dec.ny.gov.etrack.dcs.model.SpatialInqDocNonRelReasonDetail;
import dec.ny.gov.etrack.dcs.model.SpatialInquiryDocument;
import dec.ny.gov.etrack.dcs.model.SupportDocNonRelReasonDetail;
import dec.ny.gov.etrack.dcs.model.SupportDocument;
import dec.ny.gov.etrack.dcs.model.SupportDocumentConfig;

@Component
public class TransformationService implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(TransformationService.class.getName());
  
  @Autowired
  private DCSServiceUtil dcsServiceUtil;

  /**
   * Transform the support document model into Support Document Entity which can be persisted. 
   * 
   * @param ingestionRequest - Metadata details.
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * @param contextId - Unique UUID to track this request.
   * @param configList - Configuration list.
   * @param supportDocument - Support document details.
   * 
   * @return - Transformed Support document Entity.
   */
  public SupportDocument getSupportDocument(IngestionRequest  ingestionRequest, String userId,
      Long projectId, String contextId, List<SupportDocumentConfig> configList, SupportDocument supportDocument) {
    logger.debug("Entering getSupportDocument(). User id: {} context id: {}", userId, contextId);
    Date currentDate = new Date();
    Map<String, String> metadata = ingestionRequest.getMetaDataProperties();
    supportDocument.setAccessByDepOnlyInd(Integer.parseInt(metadata.get(DCSServiceConstants.ACCESS)));
    
    if (StringUtils.hasLength(metadata.get(DCSServiceConstants.DOC_CATEGORY))) {
      supportDocument.setDocumentTypeId(Integer.valueOf(metadata.get(DCSServiceConstants.DOC_CATEGORY)));
      if (StringUtils.hasLength(metadata.get(DCSServiceConstants.DOC_SUB_CATEGORY))) {
        Integer subCategory = Integer.valueOf(metadata.get(DCSServiceConstants.DOC_SUB_CATEGORY));
        if (subCategory <= 0) {
          supportDocument.setDocumentSubTypeId(null);
        } else {
          supportDocument.setDocumentSubTypeId(subCategory);
        }
      }
      supportDocument.setDocSubTypeOtherTxt(metadata.get(DCSServiceConstants.OTHER_SUB_CAT_TEXT));
    } else {
      SupportDocumentConfig config = configList.get(0);
      supportDocument.setDocumentTypeId(config.getDocumentTypeId());
      supportDocument.setDocumentSubTypeId(config.getDocumentSubTypeId());
      supportDocument.setDocumentSubTypeTitleId(config.getDocumentSubTypeTitleId());
    }
    
    if (StringUtils.hasText(metadata.get(DCSServiceConstants.CONFIDENTIAL)) 
        && "Y".equals(metadata.get(DCSServiceConstants.CONFIDENTIAL))) {
      supportDocument.setDocConfInd(1);
    }
    supportDocument.setProjectId(projectId);
    if (!StringUtils.hasLength(supportDocument.getDocumentStateCode())) {
      supportDocument.setDocumentStateCode(DCSServiceConstants.PENDING_UPLOAD);
    }
    supportDocument.setDocumentDesc(metadata.get(DCSServiceConstants.DOC_DESCRIPTION));
    supportDocument.setDocumentNm(metadata.get(DCSServiceConstants.DOCUMENT_TITLE));
    supportDocument.setTrackedApplicationId(metadata.get(DCSServiceConstants.TRACKED_APP_ID));
    String foilStatus = metadata.get(DCSServiceConstants.FOIL_STATUS);

    if (StringUtils.isEmpty(foilStatus)) {
      logger.error("There is no data for Foil Status. User id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    supportDocument.setDocReleasableCode(foilStatus);
    if ("NOREL".equals(foilStatus)) {
      String nonRelReasonCodes = metadata.get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
      if (!StringUtils.hasLength(nonRelReasonCodes)) {
        logger.error("There is no data for Non Releasable Reason codes. User Id {}, Context Id {}", userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      nonRelReasonCodes = dcsServiceUtil.removeUnwantedChars(nonRelReasonCodes);
      
      Set<SupportDocNonRelReasonDetail> docNonReleaseReasonList = new HashSet<>();
      if (!CollectionUtils.isEmpty(supportDocument.getDocNonRelReasons())) {
        supportDocument.getDocNonRelReasons().clear();
      }
      
      for (String nonReleasableReasonCode : nonRelReasonCodes.split(",")) {
       SupportDocNonRelReasonDetail nonReleasableReason = new SupportDocNonRelReasonDetail();
       nonReleasableReason.setCreatedById(userId);
       nonReleasableReason.setCreatedDate(currentDate);
       nonReleasableReason.setDocNonRelReasonCode(nonReleasableReasonCode);
       nonReleasableReason.setSupportDocument(supportDocument);
       docNonReleaseReasonList.add(nonReleasableReason);
       supportDocument.setDocNonRelReasons(docNonReleaseReasonList);
      }
    }
    supportDocument.setCreateDate(currentDate);
    supportDocument.setCreatedById(userId);
    return supportDocument;
  }
  
  /**
   * Transform the Spatial inquiry document Spatial Inquiry Document Entity.
   * 
   * @param ingestionRequest - Metatdata details.
   * @param userId - User who initiates this request.
   * @param inquiryId - Inquiry id.
   * @param contextId - Unique UUID to track this request.
   * @param configList - Configuration list.
   * 
   * @return - Spatial Inquiry Entity.
   */
  public SpatialInquiryDocument transformMetadataToSpatialInquiryDocument(IngestionRequest  ingestionRequest, String userId,
      Long inquiryId, String contextId, List<SupportDocumentConfig> configList) {
    logger.debug("Entering transformMetadataToSpatialInquiryDocument(). User id: {} context id: {}", userId, contextId);
    SpatialInquiryDocument spatialInquiryDocument = new SpatialInquiryDocument();
    Date currentDate = new Date();
    Map<String, String> metadata = ingestionRequest.getMetaDataProperties();
    spatialInquiryDocument.setAccessByDepOnlyInd(Integer.parseInt(metadata.get(DCSServiceConstants.ACCESS)));
    
    if (StringUtils.hasLength(metadata.get(DCSServiceConstants.DOC_CATEGORY))) {
      spatialInquiryDocument.setDocumentTypeId(Integer.valueOf(metadata.get(DCSServiceConstants.DOC_CATEGORY)));
      if (StringUtils.hasLength(metadata.get(DCSServiceConstants.DOC_SUB_CATEGORY))) {
        Integer subCategory = Integer.valueOf(metadata.get(DCSServiceConstants.DOC_SUB_CATEGORY));
        if (subCategory <= 0) {
          spatialInquiryDocument.setDocumentSubTypeId(null);
        } else {
          spatialInquiryDocument.setDocumentSubTypeId(subCategory);
        }
      }
      spatialInquiryDocument.setDocSubTypeOtherTxt(metadata.get(DCSServiceConstants.OTHER_SUB_CAT_TEXT));
    } else {
      SupportDocumentConfig config = configList.get(0);
      spatialInquiryDocument.setDocumentTypeId(config.getDocumentTypeId());
      spatialInquiryDocument.setDocumentSubTypeId(config.getDocumentSubTypeId());
      spatialInquiryDocument.setDocumentSubTypeTitleId(config.getDocumentSubTypeTitleId());
    }
    
    if (StringUtils.hasText(metadata.get(DCSServiceConstants.CONFIDENTIAL)) 
        && "Y".equals(metadata.get(DCSServiceConstants.CONFIDENTIAL))) {
      spatialInquiryDocument.setDocConfInd(1);
    }
    spatialInquiryDocument.setInquiryId(inquiryId);
    spatialInquiryDocument.setDocumentStateCode(DCSServiceConstants.PENDING_UPLOAD);
    spatialInquiryDocument.setDocumentDesc(metadata.get(DCSServiceConstants.DOC_DESCRIPTION));
    spatialInquiryDocument.setDocumentNm(metadata.get(DCSServiceConstants.DOCUMENT_TITLE));
    String foilStatus = metadata.get(DCSServiceConstants.FOIL_STATUS);

    if (StringUtils.isEmpty(foilStatus)) {
      logger.error("There is no data for Foil Status. User id {}, Context Id {}", userId, contextId);
      throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
          DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
    }
    spatialInquiryDocument.setDocReleasableCode(foilStatus);
    if ("NOREL".equals(foilStatus)) {
      String nonRelReasonCodes = metadata.get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
      if (!StringUtils.hasLength(nonRelReasonCodes)) {
        logger.error("There is no data for Non Releasable Reason codes. User Id {}, Context Id {}", userId, contextId);
        throw new ValidationException(DCSServiceConstants.INVALID_REQUEST,
            DCSServiceConstants.INVALID_REQUEST_ERR_MSG);
      }
      nonRelReasonCodes = dcsServiceUtil.removeUnwantedChars(nonRelReasonCodes);
      Set<SpatialInqDocNonRelReasonDetail> docNonReleaseReasonList = new HashSet<>();
      for (String nonReleasableReasonCode : nonRelReasonCodes.split(",")) {
       SpatialInqDocNonRelReasonDetail nonReleasableReason = new SpatialInqDocNonRelReasonDetail();
       nonReleasableReason.setCreatedById(userId);
       nonReleasableReason.setCreatedDate(currentDate);
       nonReleasableReason.setDocNonRelReasonCode(nonReleasableReasonCode);
       nonReleasableReason.setSpatialInqDocument(spatialInquiryDocument);
       docNonReleaseReasonList.add(nonReleasableReason);
       spatialInquiryDocument.setSpaInqDocNonRelReasonDetails(docNonReleaseReasonList);
      }
    }
    spatialInquiryDocument.setCreateDate(currentDate);
    spatialInquiryDocument.setCreatedById(userId);
    return spatialInquiryDocument;
  }

  /**
   * Prepare the Ingestion request for the document class and request parameter.
   * 
   * @param ingestReq - Ingestion request.
   * @param docClass - Document class.
   * @param docId - Document Id.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Transformed Ingestion request.
   */
  public IngestionRequest getIngestReqForDocClass(IngestionRequest ingestReq, String docClass,
      Long docId, String userId, String contextId) {
    logger.debug("Entering getIngestReqForDocClass(). Document Id {}, User id: {} Context Id : {}",
        docId, userId, contextId);
    Map<String, String> ingestReqMap = ingestReq.getMetaDataProperties();
    if (ingestReqMap != null && ingestReqMap.size() > 0) {
      ingestReqMap.put(DCSServiceConstants.INDEX_DATE, DateUtil.formatDateToString(new Date()));
      if (docClass != null) {
        if (docClass.equals(DCSServiceConstants.APPLICATION)) {
          ingestReqMap.remove(DCSServiceConstants.PERMIT_TYPE);
          ingestReqMap.remove(DCSServiceConstants.EXPIRATION_DATE);
        } else if (docClass.equals(DCSServiceConstants.PERMIT)) {
          ingestReqMap.remove(DCSServiceConstants.RECEIVED_DATE);
        } else if (docClass.equals(DCSServiceConstants.SUPPORTINGDOCUMENTS)) {
          ingestReqMap.remove(DCSServiceConstants.RECEIVED_DATE);
          ingestReqMap.remove(DCSServiceConstants.EXPIRATION_DATE);
          
        } else if (DCSServiceConstants.CORRESPONDENCE.equals(docClass)) {
          ingestReqMap.remove(DCSServiceConstants.APPLICATION_ID);
          ingestReqMap.remove(DCSServiceConstants.PERMIT_TYPE);
          ingestReqMap.remove(DCSServiceConstants.MODIFICATION_NUMBER);
          ingestReqMap.remove(DCSServiceConstants.RENEWAL_NUMBER);
          
          String emailSubject = ingestReqMap.get(DCSServiceConstants.DOC_DESCRIPTION);
          if (StringUtils.isEmpty(emailSubject)) {
            emailSubject = "NULL";
          }
          ingestReqMap.put(DCSServiceConstants.EMAIL_SUBJECT, emailSubject);
          ingestReqMap.put(DCSServiceConstants.SENT_DATE, ingestReqMap.get(DCSServiceConstants.INDEX_DATE));
        } else if (docClass.equals(DCSServiceConstants.NOTICES)) {
          ingestReqMap.remove(DCSServiceConstants.RECEIVED_DATE);
          ingestReqMap.remove(DCSServiceConstants.PERMIT_TYPE);
          ingestReqMap.put(DCSServiceConstants.SENT_DATE, ingestReqMap.get(DCSServiceConstants.INDEX_DATE));
        }
      }
      ingestReqMap.remove(DCSServiceConstants.TRACKED_APP_ID);
      ingestReqMap.remove(DCSServiceConstants.OTHER_SUB_CAT_TEXT);
      // concatenate non releasable reason codes to foilStatus if it is non releasable
      String docRelCode = ingestReqMap.get(DCSServiceConstants.FOIL_STATUS);
      String nonRelReasonCd = ingestReqMap.get(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
      if (docRelCode != null && docRelCode.trim().equals("NOREL"))
        ingestReqMap.put(DCSServiceConstants.FOIL_STATUS, docRelCode + "-" + nonRelReasonCd);
      logger.debug(
          "Doc rel code after concatination= {}, nonRelReasonCd== {} , user id: {} context Id {} ",
          ingestReqMap.get(DCSServiceConstants.FOIL_STATUS), nonRelReasonCd, userId, contextId);
      ingestReqMap.remove(DCSServiceConstants.DOC_NON_REL_REAS_CODES);
      ingestReqMap.put(DCSServiceConstants.ETRACK_DOC_ID, docId.toString());
      
      logger.debug("Inside method getIngestReqForDocClass after {}, user id: {} Context Id {} ",
          ingestReqMap, userId, contextId);
    }
    ingestReq.setMetaDataProperties(ingestReqMap);
    return ingestReq;
  }

}
