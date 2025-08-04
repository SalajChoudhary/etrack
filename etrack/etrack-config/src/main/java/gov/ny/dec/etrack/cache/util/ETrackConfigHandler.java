package gov.ny.dec.etrack.cache.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;
import gov.ny.dec.etrack.cache.model.ETrackDocType;
import gov.ny.dec.etrack.cache.model.ETrackDocumentSubType;

@Component
public class ETrackConfigHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackConfigHandler.class.getName());

  
  /**
   * Transform the Doc Type and Sub Type received from Entity to consume model.
   * 
   * @param docTypesAndSubTypes - Doc Type and Sub TYpes retrieved from the database.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns {@link Map}
   */
  public Map<String, Map<Integer, ETrackDocType>> getDocTypeAndSubTypeDetails(
      List<DocTypeSubType> docTypesAndSubTypes, final String userId, final String contextId) {
    
    logger.info("Entering getDocTypAndSubTypeDetails(). User id: {} context Id: {}", userId, contextId);
    Map<String, Map<Integer, ETrackDocType>> eTrackDocTypesLangMap = null;
    Map<Integer, ETrackDocType> eTrackDocTypeMap = null;

    try {
      if (CollectionUtils.isEmpty(docTypesAndSubTypes)) {
        logger.error("DocTypesAndSubTypes list is empty. User id: {} context Id: {}", userId, contextId);
        return null;
      }

      logger.debug("Sorting document type and sub types. User id: {} context id: {}", userId, contextId);
      logger.debug("document type and sub type results {}", new ObjectMapper().writeValueAsString(docTypesAndSubTypes));
      List<DocTypeSubType> sortedDocTypeAndSubTypes =
          docTypesAndSubTypes.stream().sorted(Comparator.comparing(DocTypeSubType::getDocumentTypeDesc, String.CASE_INSENSITIVE_ORDER))
              .collect(Collectors.toList());
      eTrackDocTypesLangMap = new LinkedHashMap<>();
      for (DocTypeSubType docTypeSubType : sortedDocTypeAndSubTypes) {
        Integer docTypeId = docTypeSubType.getDocumentTypeId();
        String langCode = docTypeSubType.getLanguageCode();
        ETrackDocType eTrackDocType = null;

        if (null == docTypeId || !StringUtils.hasText(langCode)) {
          logger.error("DocType ID {} or Lang code cannot be empty {}. User id: {} context id: {}", docTypeId, langCode, userId, contextId);
          throw new ETrackConfigException(
              "DocType ID " + docTypeId + " or " + "Lang code cannot be empty " + langCode);
        }

        eTrackDocTypeMap = eTrackDocTypesLangMap.get(langCode);
        if (CollectionUtils.isEmpty(eTrackDocTypeMap)) {
          eTrackDocTypeMap = new LinkedHashMap<>();
          eTrackDocType = addNewETrackDocTypeAndSubType(docTypeSubType, userId, contextId);
        } else if (eTrackDocTypeMap.get(docTypeId) == null) {
          eTrackDocType = addNewETrackDocTypeAndSubType(docTypeSubType, userId, contextId);
        } else {
          eTrackDocType = eTrackDocTypeMap.get(docTypeId);
          Integer docSubTypeId = docTypeSubType.getDocumentSubTypeId();
          if (docSubTypeId != null && StringUtils.hasLength(docTypeSubType.getDocSubTypeDesc())) {
            List<ETrackDocumentSubType> eTrackDocumentSubTypes = eTrackDocType.getDocSubTypes();
            ETrackDocumentSubType eTrackDocumentSubType = new ETrackDocumentSubType();
            eTrackDocumentSubType.setSubTypeId(docSubTypeId);
            eTrackDocumentSubType.setSubTypeDesc(docTypeSubType.getDocSubTypeDesc());
            if (CollectionUtils.isEmpty(eTrackDocumentSubTypes)) {
              eTrackDocumentSubTypes = new ArrayList<>();
            }
            eTrackDocumentSubTypes.add(eTrackDocumentSubType);
            eTrackDocumentSubTypes = eTrackDocumentSubTypes.stream()
                .sorted(Comparator.comparing(ETrackDocumentSubType::getSubTypeDesc, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
            eTrackDocType.setDocSubTypes(eTrackDocumentSubTypes);
          }
        }
        eTrackDocTypeMap.put(docTypeId, eTrackDocType);
        eTrackDocTypesLangMap.put(langCode, eTrackDocTypeMap);
      }
    } catch (ETrackConfigException ce) {
      populateLoggerExeptionMap("getDocTypeAndSubTypeDetails()", "Processing doc types and sub types", "User id: ".concat(userId),
          ce.getMessage(), contextId, userId);
      throw ce;
    } catch (Exception e) {
      logger.error("Error while processing the doc type and sub type ", e);
      populateLoggerExeptionMap("getDocTypeAndSubTypeDetails()", "Processing doc types and sub types", "User id: ".concat(userId),
      e.getMessage(), contextId, userId);
      throw new ETrackConfigException(
          "Unable to process the Doc Types and Sub Types", e);
    }
    logger.info("Details for doc type and sub type have been retrieved. Exiting getDocTypeAndSubTypeDetails(). User id: {} context id: {}", userId, contextId);
    return eTrackDocTypesLangMap;
  }

  private ETrackDocType addNewETrackDocTypeAndSubType(DocTypeSubType docTypeAndSubType, String userId, String contextId) {
    logger.debug("Entering getDocTypAndSubTypeDetails(). User id: {} context Id: {}", userId, contextId);
    ETrackDocType eTrackDocType = new ETrackDocType();
    eTrackDocType.setDocTypeId(docTypeAndSubType.getDocumentTypeId());
    eTrackDocType.setDocTypeDesc(docTypeAndSubType.getDocumentTypeDesc());
    eTrackDocType.setDocClassId(docTypeAndSubType.getDocumentClassId());
    eTrackDocType.setDocClassName(docTypeAndSubType.getDocumentClassNm());
    Integer docSubTypeId = docTypeAndSubType.getDocumentSubTypeId();
    if (docSubTypeId != null && StringUtils.hasLength(docTypeAndSubType.getDocSubTypeDesc())) {
      List<ETrackDocumentSubType> eTrackDocumentSubTypes = new ArrayList<>();
      ETrackDocumentSubType eTrackDocumentSubType = new ETrackDocumentSubType();
      eTrackDocumentSubType.setSubTypeId(docSubTypeId);
      eTrackDocumentSubType.setSubTypeDesc(docTypeAndSubType.getDocSubTypeDesc());
      eTrackDocumentSubTypes.add(eTrackDocumentSubType);
      eTrackDocType.setDocSubTypes(eTrackDocumentSubTypes);
    }
    return eTrackDocType;
  }

  /**
   * Transform the messages received from entity to consumer format.
   * 
   * @param messages - Messages retrieved from the database.
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns {@link Map}
   */
  public Map<String, Map<String, String>> convertMessages(List<Message> messages, String userId, String contextId) {
    Map<String, Map<String, String>> eTrackMessageMap = null;
    Map<String, String> messageMap = null;
    String languageCode = null;

    try {
      if (CollectionUtils.isEmpty(messages)) {
        logger.error("Message list is empty, exiting convertMessages(). User id: {} context Id: {}", userId, contextId);
        return null;
      }
      eTrackMessageMap = new HashMap<>();
      for (Message message : messages) {
        languageCode = message.getLanguageCode();
        if (!StringUtils.hasText(languageCode)) {
          logger.error("Language code is null. Exiting convertMessages(). User id: {} context id: {}", userId, contextId);
          throw new ETrackConfigException("Language code cannot be null");
        }
        messageMap = eTrackMessageMap.get(languageCode);
        if (CollectionUtils.isEmpty(messageMap)) {
          messageMap = new HashMap<>();
        }
        messageMap.put(message.getMessageCode(),
            message.getMessageTypeDesc() + ": " + message.getMessageDesc());
        eTrackMessageMap.put(languageCode, messageMap);
      }
    } catch (Exception e) {
      populateLoggerExeptionMap("convertMessages()", "Error converting messages received from Etrack Database.", "User id: ".concat(userId), e.getMessage(), contextId, userId);
      throw new ETrackConfigException("Unable to process the message received from ETrack Database",
          e);
    }
    logger.info("Converting of messages is complete. Exiting convertMessages(). User id: {} context id: {}", userId, contextId);
    return eTrackMessageMap;
  }
  
  private void populateLoggerExeptionMap(String methodName, String eventName, String applicableId,
      String errorMessage, String contextId, String userId) {
    logger.debug("Entering populateLoggerExceptionMap(). User id: {} context id: {}", userId, contextId);
    Map<String, String> loggingMap = new HashMap<>();
    loggingMap.put("Application name", "eTrack");
    loggingMap.put("Method name", methodName);
    loggingMap.put("Event name", eventName);
    loggingMap.put("Applicable id", applicableId);
    loggingMap.put("Context id", contextId);
    loggingMap.put("User id", userId);
    loggingMap.put("Error message", errorMessage);
    logger.error(loggingMap.toString());
    loggingMap = null;
  }
}
