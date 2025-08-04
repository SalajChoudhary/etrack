package dec.ny.gov.etrack.dms.processor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.dms.controller.DMSController;
import dec.ny.gov.etrack.dms.ecmaas.ECMaaSWrapper;
import dec.ny.gov.etrack.dms.model.DMSDocumentMetaData;
import dec.ny.gov.etrack.dms.model.DMSDocumentResponse;
import dec.ny.gov.etrack.dms.model.DMSRequest;
import dec.ny.gov.etrack.dms.model.IngestionRequest;
import dec.ny.gov.etrack.dms.model.IngestionResponse;
import dec.ny.gov.etrack.dms.model.Response;
import dec.ny.gov.etrack.dms.processor.DocumentManagementProcessor;
import dec.ny.gov.etrack.dms.request.AttachmentMetaData;

@Service
public class DocumentManagementProcessorImpl implements DocumentManagementProcessor {

  @Autowired
  private ECMaaSWrapper eCMaaSWrapper;
  
  private static Logger logger = LoggerFactory.getLogger(DocumentManagementProcessor.class);


  @Override
  public ResponseEntity<Response> deleteDocument(DMSRequest deleteRequest) {
    logger.info("Entering deleteDocument(). User id:{}. Context id:{}", deleteRequest.getUserId(), deleteRequest.getContextId());
    return eCMaaSWrapper.deleteDocument(deleteRequest);
  }

  @Override
  public ResponseEntity<DMSDocumentResponse> retrieveDocumentsByGuid(DMSRequest searchRequest) {
    logger.info("Entering retrieveDocumentsByGuid. User id:{}. Context id:{}", searchRequest.getUserId(), searchRequest.getContextId());
    return eCMaaSWrapper.retrieveDocumentByGuid(searchRequest);
  }

  @Override
  public ResponseEntity<byte[]> retrieveDocumentContent(DMSRequest searchRequest, String fileName) {
    String contextId = searchRequest.getContextId();
    String userId = searchRequest.getUserId();
    String documentId = searchRequest.getDocumentId();
    logger.info("Entering retrieveDocumentContent() fileName {}. User id:{}. Context id:{}", fileName, userId ,contextId);
    ResponseEntity<DMSDocumentResponse> documentResponseEntity =
        eCMaaSWrapper.retrieveDocumentByGuid(searchRequest);

    if (!HttpStatus.OK.equals(documentResponseEntity.getStatusCode())) {
      logger.error("Retrieval unsuccessful. Status code:{}. User id:{} context id:{}", documentResponseEntity.getStatusCode(), userId, contextId);
      return new ResponseEntity<>(documentResponseEntity.getStatusCode());
    }

    DMSDocumentResponse documentResponse = documentResponseEntity.getBody();
    if (documentResponse != null) {
      DMSRequest retrieveContentRequest = new DMSRequest();
      retrieveContentRequest.setUserId(searchRequest.getUserId());
      retrieveContentRequest.setClientId(searchRequest.getClientId());
      retrieveContentRequest.setGuid(searchRequest.getGuid());
      retrieveContentRequest.setContextId(contextId);
      for (DMSDocumentMetaData dmsDocumentMetaData : documentResponse.getDocumentsMetaData()) {
        for (AttachmentMetaData attachmentMetaData : dmsDocumentMetaData.getAttachmentMetaDatas()) {
          fileName = fileName.replaceAll("\\s", "");
          String fileNameFromMetaData = attachmentMetaData.getRetrievalName();
          fileNameFromMetaData = fileNameFromMetaData.replaceAll("\\s", "");
          logger.debug("Input file name {} fileNet file {} user Id {}, context Id {}", fileName, fileNameFromMetaData, userId, contextId);
          if (fileName.equals(fileNameFromMetaData)) {
            retrieveContentRequest
                .setElementSeqNumber(attachmentMetaData.getElementSequenceNumber());
            return eCMaaSWrapper.retrieveDocumentContent(retrieveContentRequest);
          }
        }
      }
    }
    
    documentId = documentId == null ? searchRequest.getGuid() : documentId;
    logger.error("No document content found for document {}. User id:{}. Context id:{}", documentId, userId, contextId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<IngestionResponse> uploadDocument(IngestionRequest ingestionRequest,
      MultipartFile[] uploadDocuments) {
    logger.info("Entering uploadDocument(). User id:{}. Context id:{}", ingestionRequest.getUserId() ,ingestionRequest.getContextId());
    return eCMaaSWrapper.uploadDocument(ingestionRequest, uploadDocuments);
  }

  @Override
  public ResponseEntity<IngestionResponse> updateMetadataDocument(
      IngestionRequest updateMetaDataRequest) {
    logger.info("Entering updateMetadataDocument(). User id:{}. Context id:{}", updateMetaDataRequest.getUserId(), updateMetaDataRequest.getContextId());
    return eCMaaSWrapper.updatedMetaData(updateMetaDataRequest);
  }
}
