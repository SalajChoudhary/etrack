package gov.ny.dec.etrack.cache.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ny.dec.etrack.cache.dao.TableWiseDataDao;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeEntity;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeTitle;
import gov.ny.dec.etrack.cache.entity.DocumentTitleEntity;
import gov.ny.dec.etrack.cache.entity.DocumentTypeEntity;
import gov.ny.dec.etrack.cache.entity.GISLayerConfig;
import gov.ny.dec.etrack.cache.entity.InvoiceFeeTypeEntity;
import gov.ny.dec.etrack.cache.entity.MessageEntity;
import gov.ny.dec.etrack.cache.entity.MessageType;
import gov.ny.dec.etrack.cache.entity.NaturalResourceGeneralPermitDocument;
import gov.ny.dec.etrack.cache.entity.PermitCategoryEntity;
import gov.ny.dec.etrack.cache.entity.PermitTypeCodeEntity;
import gov.ny.dec.etrack.cache.entity.PermitTypeDocumentEntity;
import gov.ny.dec.etrack.cache.entity.SEQRDocumentEntity;
import gov.ny.dec.etrack.cache.entity.SWFacTypeSubTypeDocument;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeEntity;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeSubEntity;
import gov.ny.dec.etrack.cache.entity.SolidWasteFacilityTypeDocument;
import gov.ny.dec.etrack.cache.entity.SystemParameterEntity;
import gov.ny.dec.etrack.cache.entity.TransType;
import gov.ny.dec.etrack.cache.entity.TransactionTypeRuleEntity;
import gov.ny.dec.etrack.cache.exception.ETrackConfigDuplicateDataFoundException;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;
import gov.ny.dec.etrack.cache.exception.ETrackConfigNoDataFoundException;
import gov.ny.dec.etrack.cache.model.DocumentTypeData;
import gov.ny.dec.etrack.cache.model.ETrackCodeTable;
import gov.ny.dec.etrack.cache.model.ETrackMessage;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeeType;
import gov.ny.dec.etrack.cache.model.KeyValue;
import gov.ny.dec.etrack.cache.model.MaintanenceCodeTable;
import gov.ny.dec.etrack.cache.model.PermitCategoryModel;
import gov.ny.dec.etrack.cache.model.PermitTypeCode;
import gov.ny.dec.etrack.cache.model.SWFacilitySubType;
import gov.ny.dec.etrack.cache.model.SWFacilitySubTypeRequest;
import gov.ny.dec.etrack.cache.model.SWFacilityType;
import gov.ny.dec.etrack.cache.model.SWFacilityTypeRequest;
import gov.ny.dec.etrack.cache.model.TransactionTypeRule;
import gov.ny.dec.etrack.cache.model.UrlValues;
import gov.ny.dec.etrack.cache.repostitory.DocumentClassRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentSubTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentSubTypeTitleRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentSubTypeTitleViewRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentTitleRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.GISLayerConfigRepo;
import gov.ny.dec.etrack.cache.repostitory.InvoiceFeeRepo;
import gov.ny.dec.etrack.cache.repostitory.MessageRepository;
import gov.ny.dec.etrack.cache.repostitory.MessageTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.NaturalResourceGeneralPermitDocumentRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitCategoryRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitTypeCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitTypeDocumentRepo;
import gov.ny.dec.etrack.cache.repostitory.SEQRDocumentRepo;
import gov.ny.dec.etrack.cache.repostitory.SWFacTypeSubTypeDocumentRepo;
import gov.ny.dec.etrack.cache.repostitory.SWFacilitySubTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.SWFacilityTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.SolidWasteFacilityTypeDocumentRepo;
import gov.ny.dec.etrack.cache.repostitory.SpatialInqCategoryRepo;
import gov.ny.dec.etrack.cache.repostitory.SupportDocumentMaintenanceRepo;
import gov.ny.dec.etrack.cache.repostitory.SystemParamterRepo;
import gov.ny.dec.etrack.cache.repostitory.TransTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.TransactionTypeRuleRepo;
import gov.ny.dec.etrack.cache.service.ETrackCodeTableService;
import gov.ny.dec.etrack.cache.util.TransformationService;

@Service
@Transactional
public class ETrackCodeTableServiceImpl<T> implements ETrackCodeTableService {

  @Value("${etrack.codetable.system-parameter}")
  private String systemParameters;

  @Value("${etrack.codetable.documents}")
  private String documents;

  @Value("${etrack.codetable.sw_facilities}")
  private String facilities;

  @Value("${etrack.codetable.messages}")
  private String messages;

  @Value("${etrack.codetable.gis_layer_config}")
  private String gisLayerConfig;

  @Value("${etrack.codetable.permit_type}")
  private String permitTypes;

  @Value("${etrack.codetable.trans_type}")
  private String transTypeCode;

  @Value("${etrack.codetable.spatial_inq_category}")
  private String spatialInqCategory;

  @Value("${etrack.codetable.supporting.documents}")
  private String supportingDocumentTables;
  
  @Autowired
  private TableWiseDataDao tableWiseData;

  @Autowired
  private SystemParamterRepo systemParamterRepo;

  @Autowired
  private TransTypeRepo transtypeRepo;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private MessageTypeRepo messageTypeRepository;

  @Autowired
  private PermitTypeCodeRepo permitTypeCodeRepo;

  @Autowired
  private DocumentTypeRepo documentTypeRepo;

  @Autowired
  private DocumentSubTypeRepo documentSubTypeRepo;

  @Autowired
  private DocumentTitleRepo documentTitleRepo;

  @Autowired
  private DocumentClassRepo documentClassRepo;

  @Autowired
  private GISLayerConfigRepo gISLayerConfigRepo;

  @Autowired
  private InvoiceFeeRepo invoiceFeeRepo;

  @Autowired
  private SpatialInqCategoryRepo spatialInqCategoryRepo;

  @Autowired
  private PermitCategoryRepo permitCategoryRepo;

  @Autowired
  private SWFacilityTypeRepo swFacilityTypeRepo;

  @Autowired
  private SWFacilitySubTypeRepo swFacilitySubTypeRepo;

  @Autowired
  private DocumentSubTypeTitleViewRepo documentSubTypeTitleViewRepo;

  @Autowired
  private DocumentSubTypeTitleRepo documentSubTypeTitleRepo;

  @Autowired
  private SupportDocumentMaintenanceRepo supportDocumentMaintenanceRepo;
  @Autowired
  private SWFacTypeSubTypeDocumentRepo swFacTypeSubTypeDocumentRepo;
  @Autowired
  private PermitTypeDocumentRepo permitTypeDocumentRepo;
  @Autowired
  private SEQRDocumentRepo seqrDocumentRepo;
  @Autowired
  private SolidWasteFacilityTypeDocumentRepo swFacTypeDocRepo;
  @Autowired
  private NaturalResourceGeneralPermitDocumentRepo natGpDocumentRepo;
  @Autowired
  private TransactionTypeRuleRepo transactionTypeRuleRepo;
  @Autowired
  private TransformationService transformationService;
  
  
  private static final Logger logger =
      LoggerFactory.getLogger(ETrackCodeTableServiceImpl.class.getName());

  @Override
  public List<ETrackCodeTable> getCategoryTables() {
    List<ETrackCodeTable> ctList = new ArrayList<ETrackCodeTable>();
    ctList.add(buildCodeTableDto("Documents", documents));
    ctList.add(buildCodeTableDto("GIS", gisLayerConfig));
    ctList.add(buildCodeTableDto("Messages", messages));
    ctList.add(buildCodeTableDto("Permit Types", permitTypes));    
    ctList.add(buildCodeTableDto("Solid Waste Facilities", facilities));
    ctList.add(buildCodeTableDto("Supporting Documents", supportingDocumentTables));    
    ctList.add(buildCodeTableDto("System Parameter", systemParameters));
    return ctList;
  }

  private ETrackCodeTable buildCodeTableDto(String categoryName, String tableNames) {
    ETrackCodeTable codeTable = new ETrackCodeTable();
    codeTable.setCategoryName(categoryName);
    List<String> docTableNames = new ArrayList<String>();
    String[] docsParams = tableNames.split(",");
    for (int i = 0; i < docsParams.length; i++) {
      docTableNames.add(docsParams[i]);
    }
    codeTable.setTableNames(docTableNames);
    return codeTable;
  }

  @Override
  public List<UrlValues> getSelectedTableData(String tableName) {
    return this.tableWiseData.getSelectedTableData(tableName);
  }

  @Override
  public void updateSystemParameter(MaintanenceCodeTable maintanenceCodeTable) {
    try {
      List<SystemParameterEntity> systemParameterEntities = new ArrayList<>();
      for (KeyValue keyValue : maintanenceCodeTable.getKeyValues()) {
        SystemParameterEntity systemParameterEntity = new SystemParameterEntity();
        systemParameterEntity.setUrlId(keyValue.getUniquekey());
        systemParameterEntity.setUrlLink(keyValue.getValue());
        systemParameterEntities.add(systemParameterEntity);
      }
      systemParamterRepo.saveAll(systemParameterEntities);
    } catch (Exception e) {
      logger.error("Error while updating system parameter", e);
      throw new ETrackConfigException("Error while saving system parameter", e);
    }
  }

  private void swFacilitySubTypeDuplicateCheck(SWFacilitySubTypeRequest swFacilitySubType) {
    Optional<SWFacilityTypeSubEntity> subTypeRegCodeOptional = swFacilitySubTypeRepo
        .findBySubRegIgnoreCase(swFacilitySubType.getFacilitySubTypeRegulationCode());
    Optional<SWFacilityTypeSubEntity> subTypeDescOptional = swFacilitySubTypeRepo
        .findBySubTypeDescriptionIgnoreCase(swFacilitySubType.getFacilitySubType());

    if ((subTypeRegCodeOptional.isPresent() && (swFacilitySubType.getSwFacilitySubTypeId() == null
        || !swFacilitySubType.getSwFacilitySubTypeId()
            .equals(subTypeRegCodeOptional.get().getSwFacilitySubTypeId())))
        || (subTypeDescOptional.isPresent() && (swFacilitySubType.getSwFacilitySubTypeId() == null
            || !swFacilitySubType.getSwFacilitySubTypeId()
                .equals(subTypeDescOptional.get().getSwFacilitySubTypeId())))) {
      throw new ETrackConfigDuplicateDataFoundException(
          "This SW Facility Type/SW Regulation Code/SW Facility Sub Type Combination already exists in the list.");
    }
  }

  @Override
  public void updateSWFacilitySubType(final String userId, final String contextId,
      List swFacilitySubTypes) {

    logger.info("Entering into updateSWFacilitySubType. User Id {}, Context Id {}", userId,
        contextId);
    if (CollectionUtils.isEmpty(swFacilitySubTypes)) {
      throw new ETrackConfigDuplicateDataFoundException("There is no Solid Waste Facility Sub Type is passed to add or amend");
    }
    @SuppressWarnings("unchecked")
    List<SWFacilitySubTypeRequest> swFacilitySubTypesRequest = (List<SWFacilitySubTypeRequest>)swFacilitySubTypes;
    try {
      swFacilitySubTypesRequest.forEach(swFacilitySubType -> {
        SWFacilityTypeSubEntity swFacilityTypeSubEntity = null;
        if (swFacilitySubType.getSwFacilitySubTypeId() != null) {
          Optional<SWFacilityTypeSubEntity> optional =
              swFacilitySubTypeRepo.findById(swFacilitySubType.getSwFacilitySubTypeId());
          swFacilitySubTypeDuplicateCheck(swFacilitySubType);
          swFacilityTypeSubEntity = optional.get();
          swFacilityTypeSubEntity.setMoifiedById(userId);
          swFacilityTypeSubEntity.setModifiedDate(new Date());
          swFacilityTypeSubEntity.setActiveInd(swFacilitySubType.getActiveInd());
        } else {
          swFacilitySubTypeDuplicateCheck(swFacilitySubType);
          swFacilityTypeSubEntity = new SWFacilityTypeSubEntity();
          swFacilityTypeSubEntity.setCreatedById(userId);
          swFacilityTypeSubEntity.setCreateDate(new Date());
          swFacilityTypeSubEntity.setActiveInd(1);
        }
        swFacilityTypeSubEntity.setSubReg(swFacilitySubType.getFacilitySubTypeRegulationCode());
        swFacilityTypeSubEntity.setSubTypeDescription(swFacilitySubType.getFacilitySubType());
        swFacilityTypeSubEntity.setSwFacilityTypeId(swFacilitySubType.getFacilityTypeId());
        swFacilitySubTypeRepo.save(swFacilityTypeSubEntity);        
      });      
    } catch (ETrackConfigException | ETrackConfigNoDataFoundException
        | ETrackConfigDuplicateDataFoundException e) {
      logger.error("Error while updating SWFacilitySubType", e);
      throw e;
    } catch (Exception e) {
      logger.error("General error while updating SWFacilitySubType", e);
      throw new ETrackConfigException("Error while saving SWFacilitySubType", e);
    }
    logger.info("Exiting from updateSWFacilitySubType. User Id {}, Context Id {}", userId,
        contextId);
  }

  @Override
  public void updateSWFacilityType(final String userId, final String contextId,
      List swFacilityTypes) {
    try {
      if (CollectionUtils.isEmpty(swFacilityTypes)) {
        throw new ETrackConfigDuplicateDataFoundException(
            "There is no facility Type is passed to add or amend");
      }
      @SuppressWarnings("unchecked")
      List<SWFacilityTypeRequest> swFacilityTypeRequest = (List<SWFacilityTypeRequest>)swFacilityTypes;
      swFacilityTypeRequest.forEach(swFacilityType -> {
        SWFacilityTypeEntity swFacilityTypeEntity = null;
        if (swFacilityType.getSwFacilityTypeId() != null) {
          Optional<SWFacilityTypeEntity> optionalFacilityTypeId =
              swFacilityTypeRepo.findById(swFacilityType.getSwFacilityTypeId());
          if (optionalFacilityTypeId.isPresent()) {
            logger.debug("updateSWFacilityType id present {}", swFacilityType.getSwFacilityTypeId());
            swFacilityTypeEntity = optionalFacilityTypeId.get();
            duplicateCheck(swFacilityType);
            swFacilityTypeEntity.setMoifiedById(userId);
            swFacilityTypeEntity.setModifiedDate(new Date());
            swFacilityTypeEntity.setActiveInd(swFacilityType.getActiveInd());        
          }
        } else {
          duplicateCheck(swFacilityType);
          swFacilityTypeEntity = new SWFacilityTypeEntity();
          swFacilityTypeEntity.setCreatedById(userId);
          swFacilityTypeEntity.setCreateDate(new Date());
          swFacilityTypeEntity.setActiveInd(1); 
        }
        swFacilityTypeEntity.setFacilityTypeDescription(swFacilityType.getFacilityType());
        swFacilityTypeEntity.setFtReg(swFacilityType.getRegulationCode());
        swFacilityTypeRepo.save(swFacilityTypeEntity);

      });
    } catch (ETrackConfigDuplicateDataFoundException | ETrackConfigException e) {
      logger.error(
          "This SW Facility Type/SW Regulation Code combination already exists in the list.", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while updating SWFacilityType", e);
      throw new ETrackConfigException("Error while saving SWFacilityType.", e);
    }
  }

  private void duplicateCheck(SWFacilityTypeRequest swFacilityType) {
    Optional<SWFacilityTypeEntity> optionalFacilityType = swFacilityTypeRepo
        .findByFacilityTypeDescriptionIgnoreCase(swFacilityType.getFacilityType());
    Optional<SWFacilityTypeEntity> optionalFtReg =
        swFacilityTypeRepo.findByFtRegIgnoreCase(swFacilityType.getRegulationCode());
    logger.info("updateSWFacilityType id  {}", swFacilityType.getSwFacilityTypeId());
    if ((optionalFacilityType.isPresent()
        && (swFacilityType.getSwFacilityTypeId() == null || !optionalFacilityType.get()
            .getSwFacilityTypeId().equals(swFacilityType.getSwFacilityTypeId())))
        || (optionalFtReg.isPresent()
            && (swFacilityType.getSwFacilityTypeId() == null || !optionalFtReg.get()
                .getSwFacilityTypeId().equals(swFacilityType.getSwFacilityTypeId())))) {
      throw new ETrackConfigDuplicateDataFoundException(
          "This SW Facility Type/SW Regulation Code combination already exists in the list.");
    }
  }


  private void saveTransType(MaintanenceCodeTable transType, List<String> ids) {
    List<TransType> trasnTypeEntities = (List<TransType>) transtypeRepo.findAllById(ids);
    for (KeyValue keyValue : transType.getKeyValues()) {
      TransType transTypeParam = trasnTypeEntities.stream()
          .filter(obj -> obj.getTransTypeCode().equalsIgnoreCase(keyValue.getUniquekey()))
          .findFirst().get();
      transTypeParam.setTransTypeDesc(keyValue.getValue());
    }
    transtypeRepo.saveAll(trasnTypeEntities);
  }

  private void savePermitType(MaintanenceCodeTable codeTableDto, List<String> ids) {
    List<PermitTypeCodeEntity> permiTypeEntitis =
        (List<PermitTypeCodeEntity>) permitTypeCodeRepo.findAllById(ids);
    for (KeyValue keyValue : codeTableDto.getKeyValues()) {
      PermitTypeCodeEntity permitType = permiTypeEntitis.stream()
          .filter(obj -> obj.getPermitTypeCode().equalsIgnoreCase(keyValue.getUniquekey()))
          .findFirst().get();
      permitType.setPermitTypeDescription(keyValue.getValue());
    }
    permitTypeCodeRepo.saveAll(permiTypeEntitis);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> getConfigurationDataByRequestedTableName(final String tableName) {
    List<T> tableData = (List<T>) new ArrayList<DocumentTypeData>();
    switch (tableName.toLowerCase()) {
      case "e_document_type":
        List<DocumentTypeEntity> tbd =
            (List<DocumentTypeEntity>) documentTypeRepo.findAllByOrderByDescription();
        tbd.forEach(e -> {
          e.setDocumentClassName(e.getDocumentClass().getDescription());
        });
        tableData = (List<T>) tbd;
        break;

      case "e_document_sub_type":
        List<DocumentSubTypeEntity> subTypes = documentSubTypeRepo.findAllByOrderByDescription();
        subTypes.forEach(e -> {
          e.setDocumentTypeName(e.getDocumentType().getDescription());
        });
        tableData = (List<T>) subTypes.stream()
            .filter(obj -> !StringUtils.isEmpty(obj.getDescription())).collect(Collectors.toList());
        break;

      case "e_document_title":
        List<DocumentTitleEntity> documentTitles = documentTitleRepo.findAllByOrderByDescription();
        tableData = (List<T>) documentTitles.stream().sorted(Comparator
            .comparing(DocumentTitleEntity::getDescription, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
        break;
      case "e_document_class":
        tableData = (List<T>) documentClassRepo.findAllByOrderByDescription();
        break;

      case "e_message":
        tableData = (List<T>) getE_Message();
        break;

      case "e_message_type":
        tableData = (List<T>) messageTypeRepository.findAllByOrderByMessageTypeDescription();
        break;

      case "e_system_parameter":
        tableData = (List<T>) systemParamterRepo.findAllByOrderByUrlId();
        break;

      case "e_permit_category":
        tableData = (List<T>) permitCategoryRepo.findAllByOrderByPermitCategoryDescription();
        break;

      case "e_permit_type_code":
        List<PermitTypeCodeEntity> permitCodeMapping =
            (List<PermitTypeCodeEntity>) permitTypeCodeRepo.findAllByOrderByPermitTypeDescription();
        permitCodeMapping.forEach(e -> {
          e.setPermitCategoryDescription(e.getPermitCategory().getPermitCategoryDescription());
        });
        tableData = (List<T>) permitCodeMapping;
        break;

      case "e_gis_layer_config":
        List<GISLayerConfig> gisConfigs = gISLayerConfigRepo.findAllOrderByLayerName();
        List<GISLayerConfigView> gisConfigViews = new ArrayList<>();
        gisConfigs.forEach(gisConfig -> {
          GISLayerConfigView gisConfigView = new GISLayerConfigView();
          gisConfigView.setActiveInd(gisConfig.getActiveInd());
          gisConfigView.setLayerName(gisConfig.getLayerName());
          gisConfigView.setOrderInd(gisConfig.getOrderInd());
          gisConfigView.setLayerType(gisConfig.getLayerType());
          gisConfigView.setLayerUrl(gisConfig.getLayerUrl());
          gisConfigViews.add(gisConfigView);
        });
        tableData = (List<T>) gisConfigViews;
        break;

      case "e_invoice_fee_type":
        tableData = (List<T>) invoiceFeeRepo.findAll();
        break;

      case "e_spatial_inq_category":
        tableData = (List<T>) spatialInqCategoryRepo.findAll();
        break;

      case "e_trans_type_code":
        tableData = (List<T>) transtypeRepo.findAll();
        break;

      case "e_sw_facility_type":
        tableData = (List<T>) getSWFacilityType();
        break;

      case "e_sw_facility_sub_type":
        tableData = (List<T>) getSWFacilitySubType();
        break;

    }
    return tableData;
  }

  private List<SWFacilityType> getSWFacilityType() {
    List<SWFacilityType> facilityTypes = new ArrayList<>();
    swFacilityTypeRepo.findAll().forEach(facilityTypeEntity -> {
      SWFacilityType facilityType = new SWFacilityType();
      facilityType.setSwFacilityTypeId(facilityTypeEntity.getSwFacilityTypeId());
      facilityType.setFacilityType(facilityTypeEntity.getFacilityTypeDescription());
      facilityType.setRegulationCode(facilityTypeEntity.getFtReg());
      facilityType.setActiveInd(facilityTypeEntity.getActiveInd());
      facilityTypes.add(facilityType);
    });
    return facilityTypes;
  }

  
  private List<SWFacilitySubType> getSWFacilitySubType() {
    List<SWFacilitySubType> facilitySubTypes = new ArrayList<>();
    swFacilityTypeRepo.findAll().forEach(facilityTypeEntity -> {
      List<SWFacilityTypeSubEntity> swFacilityTypeSubEntities =
          facilityTypeEntity.getSwFacilityTypeSubEntities();
      if (swFacilityTypeSubEntities != null) {
        swFacilityTypeSubEntities.forEach(facilitySubTypeEntity -> {
          SWFacilitySubType facilitySubType = new SWFacilitySubType();
          facilitySubType.setFacilityTypeId(facilityTypeEntity.getSwFacilityTypeId());
          facilitySubType.setSwFacilitySubTypeId(facilitySubTypeEntity.getSwFacilitySubTypeId());
          facilitySubType.setFacilitySubType(facilitySubTypeEntity.getSubTypeDescription());
          facilitySubType.setFacilitySubTypeRegulationCode(facilitySubTypeEntity.getSubReg());
          facilitySubType.setFacilityTypeRegulationCode(facilityTypeEntity.getFtReg());
          facilitySubType.setFacilityType(facilityTypeEntity.getFacilityTypeDescription());
          facilitySubType.setActiveInd(facilitySubTypeEntity.getActiveInd());
          facilitySubTypes.add(facilitySubType);
        });
      }
    });
    return facilitySubTypes;
  }


  @Override
  public List<SWFacilitySubType> retrieveSWFacilitySubTypeByTypeId(Integer swFacilityTypeId,
      String userId) {
    try {
      List<SWFacilityTypeSubEntity> swFacilitySubTypes = swFacilitySubTypeRepo
          .findAllBySwFacilityTypeIdOrderBySubTypeDescription(swFacilityTypeId);

      List<SWFacilitySubType> sWFacilityTypeSubTypeList = new ArrayList<>();
      swFacilitySubTypes.forEach(swFacilitySubTypeEntity -> {
        SWFacilitySubType sWFacilitySubType = new SWFacilitySubType();
        sWFacilitySubType.setFacilitySubType(swFacilitySubTypeEntity.getSubTypeDescription());
        sWFacilitySubType.setSwFacilitySubTypeId(swFacilitySubTypeEntity.getSwFacilitySubTypeId());
        sWFacilitySubType.setFacilityTypeId(swFacilitySubTypeEntity.getSwFacilityTypeId());
        sWFacilitySubType.setActiveInd(swFacilitySubTypeEntity.getActiveInd());
        sWFacilityTypeSubTypeList.add(sWFacilitySubType);
      });
      return sWFacilityTypeSubTypeList;
    }
    catch (Exception e) {
      logger.error("Error while fetching SW Facility Sub Type", e);
      throw new ETrackConfigException("Error while fetching SW Facility Sub Type", e);
    }

  }

  private List<ETrackMessage> getE_Message() {
    try {
      List<ETrackMessage> messages = new ArrayList<>();
      for (MessageEntity messageEntity : messageRepository.findAllByOrderByMessageCode()) {
        ETrackMessage eTrackMessage = new ETrackMessage();
        BeanUtils.copyProperties(messageEntity, eTrackMessage);
        if (messageEntity.getMessageTypeId() != null) {
          Optional<MessageType> meOptional =
              messageTypeRepository.findById(messageEntity.getMessageTypeId());
          if (meOptional.isPresent()) {
            eTrackMessage.setMessageTypeDescription(meOptional.get().getMessageTypeDescription());
          }
        }
        messages.add(eTrackMessage);
      }
      return messages;
    } catch (Exception e) {
      logger.error("Error while fetching message type description", e);
      throw new ETrackConfigException("Error while persistig document type", e);
    }
  }


  @Override
  public List<T> persistDocumentType(final String userId, final String contextId,
      List documentTypeDatas) {
    try {
      for (DocumentTypeData documentTypeData : (List<DocumentTypeData>) documentTypeDatas) {
        DocumentTypeEntity documentTypeEntity = null;

        if (documentTypeData.getDocumentTypeId() == null) {
          documentTypeEntity = new DocumentTypeEntity();
          List<DocumentTypeEntity> existtingDocumentTypes =
              documentTypeRepo.findByDescription(documentTypeData.getDocDescription());
          if (!CollectionUtils.isEmpty(existtingDocumentTypes)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Type already exists in the list.");
          }
          documentTypeEntity.setDescription(documentTypeData.getDocDescription());
          documentTypeEntity.setDocumentClassId(documentTypeData.getDocumentClassId());
          documentTypeEntity.setCreatedById(userId);
          documentTypeEntity.setCreateDate(new Date());
          documentTypeEntity.setActiveInd(1);
          documentTypeEntity.setAvailToDepInd("0");
          documentTypeEntity = documentTypeRepo.save(documentTypeEntity);
          DocumentSubTypeEntity documentSubTypeEntity = new DocumentSubTypeEntity();
          documentSubTypeEntity.setActiveInd(1);
          documentSubTypeEntity.setAvailToDepInd("0");
          documentSubTypeEntity.setDocumentTypeId(documentTypeEntity.getId());
          documentSubTypeEntity.setCreatedById(userId);
          documentSubTypeEntity.setCreateDate(new Date());
          documentSubTypeRepo.save(documentSubTypeEntity);
        } else {
          List<DocumentTypeEntity> existtingDocumentTypes =
              documentTypeRepo.findByDescriptionAndDocumentTypeId(
                  documentTypeData.getDocumentTypeId(), documentTypeData.getDocDescription());
          if (!CollectionUtils.isEmpty(existtingDocumentTypes)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Type already exists in the list.");
          }
          Optional<DocumentTypeEntity> documentTypeEntityOptional =
              documentTypeRepo.findById(documentTypeData.getDocumentTypeId());
          documentTypeEntity = documentTypeEntityOptional.get();
          documentTypeEntity.setActiveInd(documentTypeData.getActiveInd());
          documentTypeEntity.setAvailToDepInd(documentTypeData.getAvailToDepOnlyInd());
          if (documentTypeData.getActiveInd() == null
              || documentTypeData.getActiveInd().equals(0)) {
            documentTypeEntity.setInactivatedDate(new Date());
          } else {
            documentTypeEntity.setInactivatedDate(null);
          }
          documentTypeEntity.setModifiedDate(new Date());
          documentTypeEntity.setMoifiedById(userId);
          documentTypeEntity.setDescription(documentTypeData.getDocDescription());
          documentTypeEntity.setDocumentClassId(documentTypeData.getDocumentClassId());
          documentTypeRepo.save(documentTypeEntity);
        }
      }
      return documentTypeDatas;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting document type description", e);
      throw new ETrackConfigException("Error while persistig document type", e);
    }
  }

  @Override
  public List<T> persistDocumentSubType(final String userId, final String contextId,
      List documentTypeDatas) {
    try {
      for (DocumentTypeData documentTypeData : (List<DocumentTypeData>) documentTypeDatas) {
        DocumentSubTypeEntity documentSubTypeEntity = new DocumentSubTypeEntity();
        if (documentTypeData.getDocumentSubTypeId() == null) {
          List<DocumentSubTypeEntity> existingDocumentSubTypes = 
              documentSubTypeRepo.findByDescription(documentTypeData.getDocSubTypeDescription());
          if (!CollectionUtils.isEmpty(existingDocumentSubTypes)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Type/Sub Type already exists in the list.");
          }
          documentSubTypeEntity.setCreatedById(userId);
          documentSubTypeEntity.setCreateDate(new Date());
          documentSubTypeEntity.setActiveInd(1);
          documentSubTypeEntity.setAvailToDepInd("0");
        } else {
          List<DocumentSubTypeEntity> existingDocumentSubTypes =
              documentSubTypeRepo.findByDocumentSubTypeIdAndDescription(
                  documentTypeData.getDocumentSubTypeId(), documentTypeData.getDocumentTypeId(),
                  documentTypeData.getDocSubTypeDescription());
          if (!CollectionUtils.isEmpty(existingDocumentSubTypes)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Type/Sub Type already exists in the list.");
          }
          Optional<DocumentSubTypeEntity> documentSubTypeEntityOptional =
              documentSubTypeRepo.findById(documentTypeData.getDocumentSubTypeId());
          documentSubTypeEntity = documentSubTypeEntityOptional.get();
          documentSubTypeEntity.setAvailToDepInd(documentTypeData.getAvailToDepOnlyInd());
          documentSubTypeEntity.setActiveInd(documentTypeData.getActiveInd());
          if (documentTypeData.getActiveInd() == null
              || documentTypeData.getActiveInd().equals(0)) {
            documentSubTypeEntity.setInactivatedDate(new Date());
          } else {
            documentSubTypeEntity.setInactivatedDate(null);
          }
          documentSubTypeEntity.setModifiedDate(new Date());
          documentSubTypeEntity.setMoifiedById(userId);
        }
        documentSubTypeEntity.setDescription(documentTypeData.getDocSubTypeDescription());
        documentSubTypeEntity.setDocumentTypeId(documentTypeData.getDocumentTypeId());
        documentSubTypeRepo.save(documentSubTypeEntity);
      }
      return documentTypeDatas;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      logger.error("Duplicate document sub type cannot be persisted", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting document sub type description", e);
      throw new ETrackConfigException("Error while persisting sub document type", e);
    }
  }

  @Override
  public List<T> persistDocumentTitle(final String userId, final String contextId,
      List documentTypeDatas) {
    try {
      for (DocumentTypeData documentTypeData : (List<DocumentTypeData>) documentTypeDatas) {
        DocumentTitleEntity documentTitleEntity = new DocumentTitleEntity();
        if (documentTypeData.getDocTitleId() == null) {
          List<DocumentTitleEntity> existingDocumentTitles =
              documentTitleRepo.findByDescription(documentTypeData.getDocTitle());
          if (!CollectionUtils.isEmpty(existingDocumentTitles)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Title already exists in the list.");
          }
          documentTitleEntity.setCreatedById(userId);
          documentTitleEntity.setCreateDate(new Date());
        } else {
          List<DocumentTitleEntity> existingDocumentTitles =
              documentTitleRepo.findByDocumentTitleIdAndDescription(
                  documentTypeData.getDocTitleId(), documentTypeData.getDocTitle());
          if (!CollectionUtils.isEmpty(existingDocumentTitles)) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Document Title already exists in the list.");
          }
          Optional<DocumentTitleEntity> documentTitleEntityOptional =
              documentTitleRepo.findById(documentTypeData.getDocTitleId());
          documentTitleEntity = documentTitleEntityOptional.get();
          documentTitleEntity.setModifiedDate(new Date());
          documentTitleEntity.setMoifiedById(userId);
        }
        documentTitleEntity.setDescription(documentTypeData.getDocTitle());
        documentTitleRepo.save(documentTitleEntity);
      }
      return documentTypeDatas;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      logger.error("Duplicate document title cannot be persisted", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting document title description ", e);
      throw new ETrackConfigException("Error while persisting sub document type", e);
    }
  }

  @Override
  public List persistPermitCategory(final String userId, final String contextId,
      List permitCategoryModels) {
    try {
      for (PermitCategoryModel permitCategoryModel : (List<PermitCategoryModel>) permitCategoryModels) {
        permitCategoryModel
            .setPermitCategoryDesc(permitCategoryModel.getPermitCategoryDesc().trim());
        String permitCategoryDescription =
            permitCategoryModel.getPermitCategoryDesc().substring(0, 1).toUpperCase()
                + permitCategoryModel.getPermitCategoryDesc().substring(1);
        permitCategoryModel.setPermitCategoryDesc(permitCategoryDescription);
        PermitCategoryEntity categoryEntity = new PermitCategoryEntity();
        Optional<PermitCategoryEntity> permitCategoryTypeEntity = permitCategoryRepo
            .findByPermitCategoryDescription(permitCategoryModel.getPermitCategoryDesc());
        if (permitCategoryTypeEntity.isPresent() && permitCategoryTypeEntity.get()
            .getPermitCategoryId() != permitCategoryModel.getPermitCategoryId()) {
          throw new ETrackConfigDuplicateDataFoundException(
              "This Permit category is already exists in the list.");
        }
        if (permitCategoryModel.getPermitCategoryId() == null) {
          Optional<PermitCategoryEntity> categoryDesc = permitCategoryRepo
              .findByPermitCategoryDescription(permitCategoryModel.getPermitCategoryDesc());
          if (categoryDesc.isPresent()) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Permit category is already exists in the list.");
          }
          categoryEntity.setCreatedById(userId);
          categoryEntity.setCreateDate(new Date());
          categoryEntity.setActiveInd(1);
        } else {
          Optional<PermitCategoryEntity> categoryEntityOptional =
              permitCategoryRepo.findById(permitCategoryModel.getPermitCategoryId());
          categoryEntity = categoryEntityOptional.get();
          categoryEntity.setActiveInd(permitCategoryModel.getActiveInd());
        }
        categoryEntity.setModifiedById(userId);
        categoryEntity.setModifiedDate(new Date());
        categoryEntity.setPermitCategoryDescription(permitCategoryModel.getPermitCategoryDesc());
        permitCategoryRepo.save(categoryEntity);
        BeanUtils.copyProperties(categoryEntity, permitCategoryModel);
      }
      return permitCategoryModels;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      logger.error("Duplicate permit category cannot be persisted.", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting permit category", e);
      throw new ETrackConfigException("Error while persisting permit categorys.", e);
    }
  }

  @Override
  public List persistPermitTypeCode(final String userId, final String contextId, List permitTypes) {
    try {
      for (PermitTypeCode permitType : (List<PermitTypeCode>) permitTypes) {

        if (permitType.getPermitTypeCode() != null) {
          PermitTypeCodeEntity permitTypeCodeEntity =
              permitTypeCodeRepo.findById(permitType.getPermitTypeCode()).get();
          permitTypeCodeEntity.setMoifiedById(userId);
          permitTypeCodeEntity.setModifiedDate(new Date());
          permitTypeCodeEntity.setActiveInd(permitType.getActiveInd());
          permitTypeCodeEntity.setNatResInd(permitType.getNatResInd());
          permitTypeCodeEntity.setPermitCategoryId(permitType.getPermitCategoryId());
          permitTypeCodeRepo.save(permitTypeCodeEntity);
          BeanUtils.copyProperties(permitTypeCodeEntity, permitType);
        }
      }
      return permitTypes;
    } catch (Exception e) {
      logger.error("Error while persisting permit type code", e);
      throw new ETrackConfigException("Error while persisting permit type codes", e);
    }
  }

  @Override
  public List<T> persistMessages(final String userId, final String contextId, List messageData) {
    try {
      for (DocumentTypeData documentTypeData : (List<DocumentTypeData>) messageData) {
        MessageEntity messageEntities = new MessageEntity();
        if (documentTypeData.getNewMessageCode() != null
            && documentTypeData.getNewMessageCode() == 1) {
          Optional<MessageEntity> messageCode =
              messageRepository.findByMessageCode(documentTypeData.getMessageCode());
          if (messageCode.isPresent()) {
            throw new ETrackConfigDuplicateDataFoundException(
                "This Message already exists in the list.");
          }
          messageEntities.setCreatedById(userId);
          messageEntities.setCreateDate(new Date());
          messageEntities.setMessageCode(documentTypeData.getMessageCode());
        } else {
          Optional<MessageEntity> messageEntityOptional =
              messageRepository.findById(documentTypeData.getMessageCode());
          messageEntities = messageEntityOptional.get();
          messageEntities.setModifiedDate(new Date());
          messageEntities.setMoifiedById(userId);
        }
        messageEntities.setMessageTypeId(Integer.valueOf(documentTypeData.getMessageTypeId()));
        messageEntities.setMessageDesc(documentTypeData.getMessageDesc());
        messageRepository.save(messageEntities);
      }
      return messageData;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      logger.error("Duplicate Message Code cannot be persisted", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting Message Description ", e);
      throw new ETrackConfigException("Error while persisting Message Description", e);
    }
  }

  @Override
  public void persistGISLayerDetails(final String userId, final String contextId,
      final List gisLayerConfigs) {
    if (CollectionUtils.isEmpty(gisLayerConfigs)) {
      throw new ETrackConfigException("There are no GIS Layer details passed to add or amend");
    }
    @SuppressWarnings("unchecked")
    List<GISLayerConfigView> gisLayerDetails = (List<GISLayerConfigView>) gisLayerConfigs;
    Map<Integer, GISLayerConfigView> gisLayerMap = new HashMap<>();

    for (GISLayerConfigView gisLayerConfig : gisLayerDetails) {
      if (!StringUtils.hasLength(gisLayerConfig.getLayerName())) {
        throw new ETrackConfigDuplicateDataFoundException("Layer Name is Required.");
      }
      if (!StringUtils.hasLength(gisLayerConfig.getLayerType())) {
        throw new ETrackConfigDuplicateDataFoundException("Layer Type is Required.");
      }
      if (!StringUtils.hasLength(gisLayerConfig.getLayerUrl())) {
        throw new ETrackConfigDuplicateDataFoundException("Layer URL is Required.");
      }
      if (gisLayerConfig.getActiveInd() != null && gisLayerConfig.getActiveInd().equals(1)
          && gisLayerConfig.getOrderInd() == null) {
        throw new ETrackConfigDuplicateDataFoundException("Order Number is Required.");
      }
      if (gisLayerConfig.getOrderInd() != null
          && gisLayerMap.get(gisLayerConfig.getOrderInd()) != null) {
        throw new ETrackConfigDuplicateDataFoundException(
            "Order Numbers in this list must be unique.");
      } else if (gisLayerConfig.getOrderInd() != null) {
        gisLayerMap.put(gisLayerConfig.getOrderInd(), gisLayerConfig);
      }
    }

    try {
      Iterable<GISLayerConfig> existingGISLayers = gISLayerConfigRepo.findAll();
      GISLayerConfig existingGISLayer = existingGISLayers.iterator().next();
      gISLayerConfigRepo.deleteAll();
      gisLayerDetails.forEach(gisLayer -> {
        GISLayerConfig newGisLayerConfig = new GISLayerConfig();
        newGisLayerConfig.setLayerName(gisLayer.getLayerName());
        newGisLayerConfig.setLayerUrl(gisLayer.getLayerUrl());
        newGisLayerConfig.setLayerType(gisLayer.getLayerType());
        newGisLayerConfig.setActiveInd(gisLayer.getActiveInd());
        newGisLayerConfig.setOrderInd(gisLayer.getOrderInd());

        if (existingGISLayer != null && StringUtils.hasLength(existingGISLayer.getCreatedById())) {
          newGisLayerConfig.setCreateDate(existingGISLayer.getCreateDate());
          newGisLayerConfig.setCreatedById(existingGISLayer.getCreatedById());
          newGisLayerConfig.setModifiedById(userId);
          newGisLayerConfig.setModifiedDate(new Date());
        } else {
          newGisLayerConfig.setCreatedById(userId);
          newGisLayerConfig.setCreateDate(new Date());
        }
        gISLayerConfigRepo.save(newGisLayerConfig);
      });
    } catch (Exception e) {
      throw new ETrackConfigException("Error while updating the GIS Layer details", e);
    }
  }

  @Override
  public List retrieveDocumentSubTypeTitle(final Long id) {
    if (id != null && id > 0) {
      return documentSubTypeTitleViewRepo.findAllDocumentSubTypeTitlesById(id);
    } else {
      return documentSubTypeTitleViewRepo.findAllDocumentSubTypeTitles();
    }
  }

  @Override
  public List retrieveDocumentSubType(Long id) {
    return documentSubTypeRepo.findByDocumentTypeId(id);
  }

  @Override
  public void storeDocumentSubTypeTitles(String userId, String contextId,
      List documentSubTypeTitles) {
    if (CollectionUtils.isEmpty(documentSubTypeTitles)) {
      throw new ETrackConfigException("There is no Document Sub Type Titles passed to amend");
    }
    for (DocumentSubTypeTitle requestedSubTypeTitle : (List<DocumentSubTypeTitle>) documentSubTypeTitles) {
      if (requestedSubTypeTitle.getDocumentSubTypeTitleId() != null
          && requestedSubTypeTitle.getDocumentSubTypeTitleId() > 0) {
        Optional<DocumentSubTypeTitle> existingDocumentSubTypeTitleAvail =
            documentSubTypeTitleRepo.findById(requestedSubTypeTitle.getDocumentSubTypeTitleId());
        if (!existingDocumentSubTypeTitleAvail.isPresent()) {
          throw new ETrackConfigNoDataFoundException("Document Sub Type Title doesn't exists.");
        }
        List<DocumentSubTypeTitle> existingSubTypeTitles = documentSubTypeTitleRepo
            .findAllDocumentSubTypeTitle(requestedSubTypeTitle.getDocumentSubTypeTitleId(),
                requestedSubTypeTitle.getDocumentSubTypeId(),
                requestedSubTypeTitle.getDocumentTitleId());
        if (!CollectionUtils.isEmpty(existingSubTypeTitles)) {
          throw new ETrackConfigDuplicateDataFoundException(
              "Document Sub Type Title already exists.");
        }
        DocumentSubTypeTitle existingDocumentSubTypeTitle = existingDocumentSubTypeTitleAvail.get();
        existingDocumentSubTypeTitle.setModifiedById(userId);
        existingDocumentSubTypeTitle.setModifiedDate(new Date());
        existingDocumentSubTypeTitle.setDocumentTitleId(requestedSubTypeTitle.getDocumentTitleId());
        existingDocumentSubTypeTitle.setActiveInd(requestedSubTypeTitle.getActiveInd());
        documentSubTypeTitleRepo.save(existingDocumentSubTypeTitle);
      } else {
        requestedSubTypeTitle.setCreatedById(userId);
        requestedSubTypeTitle.setCreateDate(new Date());
        documentSubTypeTitleRepo.save(requestedSubTypeTitle);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<InvoiceFeeType> persistInvoiceFeeType(List invoiceFeeTypeInput, String userId,
      String contextId) {
    try {
      if (CollectionUtils.isEmpty(invoiceFeeTypeInput)) {
        throw new ETrackConfigDuplicateDataFoundException("There is no invoice fee type passed to add/amend");
      }
      List<InvoiceFeeType> invoiceFeeTypes = (List<InvoiceFeeType>)invoiceFeeTypeInput;
      invoiceFeeTypes.forEach(invoiceFeeType -> {
        Optional<InvoiceFeeTypeEntity> invoiceFeeTypeOptional =
            invoiceFeeRepo.findByInvoiceFeeTypeIgnoreCase(invoiceFeeType.getInvoiceFeeType());
        InvoiceFeeTypeEntity invoiceFeeTypeEntity = null;
        if (invoiceFeeTypeOptional.isPresent()) {
          invoiceFeeTypeEntity = invoiceFeeTypeOptional.get();
          invoiceFeeTypeEntity.setModifiedDate(new Date());
          invoiceFeeTypeEntity.setMoifiedById(userId);
          invoiceFeeTypeEntity.setActiveInd(invoiceFeeType.getActiveInd());          
        } else {
          invoiceFeeTypeEntity = new InvoiceFeeTypeEntity();
          invoiceFeeTypeEntity.setInvoiceFeeType(invoiceFeeType.getInvoiceFeeType());
          invoiceFeeTypeEntity.setCreateDate(new Date());
          invoiceFeeTypeEntity.setCreatedById(userId);
          invoiceFeeTypeEntity.setActiveInd(1); 
        }
        buildInvoiceFeeTypeEntity(userId, invoiceFeeType, invoiceFeeTypeEntity);
        invoiceFeeRepo.save(invoiceFeeTypeEntity);
      });      
      
      return invoiceFeeTypes;
    } catch (ETrackConfigDuplicateDataFoundException e) {
      logger.error("Duplicate invoice fee type code", contextId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error while persisting invoice fee type for {}", contextId, e);
      throw new ETrackConfigException("Error while persisting invoice fee type ", e);
    }
  }

  private void buildInvoiceFeeTypeEntity(String userId, InvoiceFeeType invoiceFeeType,
      InvoiceFeeTypeEntity invoiceFeeTypeEntity) {
    invoiceFeeTypeEntity.setInvoiceFee(invoiceFeeType.getInvoiceFee());
    invoiceFeeTypeEntity.setInvoiceFeeDesc(invoiceFeeType.getInvoiceFeeDesc());
    invoiceFeeTypeEntity.setPermitTypeCode(invoiceFeeType.getPermitTypeCode());
  }

  @Override
  public Object retrieveSupportDocumentMaintenanceTableDetails(String userId, String contextId,
      String tableName, String permitTypeCode, Integer swFacTypeId, Integer swFacSubTypeId) {

    switch (tableName) {
      case "e_required_doc_for_permit_type":
        if (!StringUtils.hasLength(permitTypeCode)) {
          throw new ETrackConfigDuplicateDataFoundException("Permit Type Code cannot be empty.");
        }
        return supportDocumentMaintenanceRepo
            .findAllPermitTypeAssociatedDocumentConfigurationRecords(permitTypeCode);
      case "e_required_doc_for_fac_type":
        if (swFacTypeId == null) {
          throw new ETrackConfigDuplicateDataFoundException("Solid Waste Id cannot be empty.");
        }
        if (swFacSubTypeId == null) {
          return supportDocumentMaintenanceRepo
              .findAllFacTypeAndSubTypeAssociatedConfigurationRecords(swFacTypeId);
        } else {
          return supportDocumentMaintenanceRepo
              .findAllFacTypeAndSubTypeAssociatedConfigurationRecordsBySubTypeId(swFacTypeId,
                  swFacSubTypeId);
        }
      case "e_required_doc_for_nat_gp":
        return supportDocumentMaintenanceRepo
            .findAllNaturalResourceGPAssociatedDocumentConfigurationRecords();
      case "e_required_doc_for_seqr":
        return supportDocumentMaintenanceRepo.findAllSEQRAssociatedDocumentConfigurationRecords();
      default:
        throw new ETrackConfigDuplicateDataFoundException("No valid table data is passed");
    }
  }

  @Override
  public Object retrieveDocumentTitleAssociatedSWFacTypesAndSubTypes(String userId,
      String contextId) {
    List<SWFacTypeSubTypeDocument> swFacTypeSubTypes =
        swFacTypeSubTypeDocumentRepo.findAllDocumentAssociatedSWFacTypeAndSubType();
    if (CollectionUtils.isEmpty(swFacTypeSubTypes)) {
      logger.info("There is no Solid Waste Facility Type and Sub Types ");
      return new ArrayList<>();
    }
    logger.info("Total number of Solid Waste Facility Type and Sub Types . {}",
        swFacTypeSubTypes.size());

    Map<Integer, SWFacilityType> swFacilityTypeMap = new HashMap<>();
    swFacTypeSubTypes.forEach(swFacTypeSubTypeDocument -> {
      SWFacilitySubType swFacilitySubType = new SWFacilitySubType();
      swFacilitySubType.setFacilitySubType(swFacTypeSubTypeDocument.getSubTypeDescription());
      swFacilitySubType.setSwFacilitySubTypeId(swFacTypeSubTypeDocument.getSwFacilitySubTypeId());
      if (swFacilityTypeMap.get(swFacTypeSubTypeDocument.getSwFacilityTypeId()) != null) {
        if (swFacTypeSubTypeDocument.getSwFacilitySubTypeId() != null) {
          swFacilityTypeMap.get(swFacTypeSubTypeDocument.getSwFacilityTypeId())
              .getFacilitySubTypes().add(swFacilitySubType);
        }
      } else {
        SWFacilityType swFacilityType = new SWFacilityType();
        swFacilityType.setSwFacilityTypeId(swFacTypeSubTypeDocument.getSwFacilityTypeId());
        swFacilityType.setFacilityType(swFacTypeSubTypeDocument.getFacilityTypeDesc());
        List<SWFacilitySubType> swFacilitySubTypes = new ArrayList<>();
        if (swFacTypeSubTypeDocument.getSwFacilitySubTypeId() != null) {
          swFacilitySubTypes.add(swFacilitySubType);
        }
        swFacilityType.setFacilitySubTypes(swFacilitySubTypes);
        swFacilityTypeMap.put(swFacTypeSubTypeDocument.getSwFacilityTypeId(), swFacilityType);
      }
    });
    return swFacilityTypeMap.values();
  }

  @Override
  public void persistSupportDocumentMaintenanceTableDetails(final String userId,
      final String contextId, final String tableName, final List supportDocumentMaintenancesDetail) {
    
    @SuppressWarnings("unchecked")
    List<gov.ny.dec.etrack.cache.model.SupportDocumentMaintenance> supportDocumentMaintenances 
      = (List<gov.ny.dec.etrack.cache.model.SupportDocumentMaintenance>) supportDocumentMaintenancesDetail;
    
    if (CollectionUtils.isEmpty(supportDocumentMaintenances)) {
      throw new ETrackConfigDuplicateDataFoundException("No Document detail is passed to persist");
    }
    switch (tableName) {
      case "e_required_doc_for_permit_type":
        supportDocumentMaintenances.forEach(supportDocumentMaintenance -> {
          if (!StringUtils.hasLength(supportDocumentMaintenance.getPermitTypeCode())) {
            throw new ETrackConfigDuplicateDataFoundException("Permit Type Code cannot be empty.");
          }
          PermitTypeDocumentEntity permitTypeDocumentEntity = null;
          if (supportDocumentMaintenance.getUniqueId() != null) {
            Optional<PermitTypeDocumentEntity> permitTypeDocumentOptional =
                permitTypeDocumentRepo.findById(supportDocumentMaintenance.getUniqueId());
            if (!permitTypeDocumentOptional.isPresent()) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Permit Type Code Document is not available.");
            }
            permitTypeDocumentEntity = permitTypeDocumentOptional.get();
            permitTypeDocumentEntity.setActiveInd(supportDocumentMaintenance.getActiveInd());
            permitTypeDocumentEntity.setModifiedById(userId);
            permitTypeDocumentEntity.setModifiedDate(new Date());
          } else {
            List<PermitTypeDocumentEntity> existingDocuments = permitTypeDocumentRepo.findByDocumentTitleIdAndPermitTypeCode(
                supportDocumentMaintenance.getDocumentTitleId(), supportDocumentMaintenance.getPermitTypeCode());
            if (!CollectionUtils.isEmpty(existingDocuments)) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Supporting Document already exists in list."); 
            }
            permitTypeDocumentEntity = new PermitTypeDocumentEntity();
            permitTypeDocumentEntity.setActiveInd(1);
            permitTypeDocumentEntity.setCreatedById(userId);
            permitTypeDocumentEntity.setCreateDate(new Date());
            List<Long> documentSubTypeTitleIds = supportDocumentMaintenanceRepo.findDocumentSubTypeTitleIdByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            permitTypeDocumentEntity
                .setDocumentSubTypeTitleId(documentSubTypeTitleIds.get(0));
            permitTypeDocumentEntity
                .setPermitTypeCode(supportDocumentMaintenance.getPermitTypeCode());
          }
          permitTypeDocumentEntity.setReqdNew(supportDocumentMaintenance.getReqdNew());
          permitTypeDocumentEntity.setReqdMod(supportDocumentMaintenance.getReqdMod());
          permitTypeDocumentEntity.setReqdExt(supportDocumentMaintenance.getReqdExt());
          permitTypeDocumentEntity.setReqdMnm(supportDocumentMaintenance.getReqdMnm());
          permitTypeDocumentEntity.setReqdMtn(supportDocumentMaintenance.getReqdMtn());
          permitTypeDocumentEntity.setReqdRen(supportDocumentMaintenance.getReqdRen());
          permitTypeDocumentEntity.setReqdRtn(supportDocumentMaintenance.getReqdRtn());
          permitTypeDocumentEntity.setReqdXfer(supportDocumentMaintenance.getReqdXfer());
          permitTypeDocumentRepo.save(permitTypeDocumentEntity);
        });
        return;
      case "e_required_doc_for_fac_type":
        supportDocumentMaintenances.forEach(supportDocumentMaintenance -> {
          if (supportDocumentMaintenance.getSwFacilityTypeId() == null) {
            throw new ETrackConfigDuplicateDataFoundException("Solid Waste Id cannot be empty.");
          }
          SolidWasteFacilityTypeDocument sWasteFacilityTypeDocument = null;
          if (supportDocumentMaintenance.getUniqueId() != null) {
            Optional<SolidWasteFacilityTypeDocument> swFacilityDocumentOptional =
                swFacTypeDocRepo.findById(supportDocumentMaintenance.getUniqueId());
            if (!swFacilityDocumentOptional.isPresent()) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Solid Waste Facility Type Document is not available.");
            }
            sWasteFacilityTypeDocument = swFacilityDocumentOptional.get();
            sWasteFacilityTypeDocument.setActiveInd(supportDocumentMaintenance.getActiveInd());
            sWasteFacilityTypeDocument.setModifiedById(userId);
            sWasteFacilityTypeDocument.setModifiedDate(new Date());
          } else {
            List<SolidWasteFacilityTypeDocument> existingDocuments = null;
            
            if (supportDocumentMaintenance.getSwFacilityTypeId() != null &&  
                supportDocumentMaintenance.getSwFacilitySubTypeId() == null) {
              existingDocuments = swFacTypeDocRepo.findByDocumentTitleIdAndSWFacTypeId(
                  supportDocumentMaintenance.getDocumentTitleId(), supportDocumentMaintenance.getSwFacilityTypeId());              
            } else  if (supportDocumentMaintenance.getSwFacilityTypeId() != null &&  
                supportDocumentMaintenance.getSwFacilitySubTypeId() != null){
              existingDocuments = swFacTypeDocRepo.findByDocumentTitleIdAndSWFacTypeIds(
                  supportDocumentMaintenance.getDocumentTitleId(), 
                  supportDocumentMaintenance.getSwFacilityTypeId(), supportDocumentMaintenance.getSwFacilitySubTypeId()); 
            }
            
            if (!CollectionUtils.isEmpty(existingDocuments)) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Supporting Document already exists in list."); 
            }
            sWasteFacilityTypeDocument = new SolidWasteFacilityTypeDocument();
            sWasteFacilityTypeDocument.setActiveInd(1);
            sWasteFacilityTypeDocument.setCreatedById(userId);
            sWasteFacilityTypeDocument.setCreateDate(new Date());
            sWasteFacilityTypeDocument
                .setSwFacilityTypeId(supportDocumentMaintenance.getSwFacilityTypeId());
            sWasteFacilityTypeDocument
                .setSwFacilitySubTypeId(supportDocumentMaintenance.getSwFacilitySubTypeId());
            List<Long> documentSubTypeTitleIds = supportDocumentMaintenanceRepo.findDocumentSubTypeTitleIdByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            if (CollectionUtils.isEmpty(documentSubTypeTitleIds)) {
              throw new ETrackConfigNoDataFoundException("Document Title is not available");
            }
            sWasteFacilityTypeDocument
                .setDocumentSubTypeTitleId(documentSubTypeTitleIds.get(0));
          }
          sWasteFacilityTypeDocument.setReqdNew(supportDocumentMaintenance.getReqdNew());
          sWasteFacilityTypeDocument.setReqdMod(supportDocumentMaintenance.getReqdMod());
          sWasteFacilityTypeDocument.setReqdExt(supportDocumentMaintenance.getReqdExt());
          sWasteFacilityTypeDocument.setReqdMnm(supportDocumentMaintenance.getReqdMnm());
          sWasteFacilityTypeDocument.setReqdMtn(supportDocumentMaintenance.getReqdMtn());
          sWasteFacilityTypeDocument.setReqdRen(supportDocumentMaintenance.getReqdRen());
          sWasteFacilityTypeDocument.setReqdRtn(supportDocumentMaintenance.getReqdRtn());
          sWasteFacilityTypeDocument.setReqdXfer(supportDocumentMaintenance.getReqdXfer());
          swFacTypeDocRepo.save(sWasteFacilityTypeDocument);
        });
        return;
      case "e_required_doc_for_nat_gp":
        supportDocumentMaintenances.forEach(supportDocumentMaintenance -> {
          NaturalResourceGeneralPermitDocument naturalResourceGeneralPermitDocument = null;
          if (supportDocumentMaintenance.getUniqueId() != null) {
            Optional<NaturalResourceGeneralPermitDocument> natGpDocumentOptional =
                natGpDocumentRepo.findById(supportDocumentMaintenance.getUniqueId());
            if (!natGpDocumentOptional.isPresent()) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Natural Resource General Permit Document is not available.");
            }
            naturalResourceGeneralPermitDocument = natGpDocumentOptional.get();
            naturalResourceGeneralPermitDocument
                .setActiveInd(supportDocumentMaintenance.getActiveInd());
            naturalResourceGeneralPermitDocument.setModifiedById(userId);
            naturalResourceGeneralPermitDocument.setModifiedDate(new Date());
          } else {
            List<NaturalResourceGeneralPermitDocument> existingDocuments = natGpDocumentRepo.findByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            if (!CollectionUtils.isEmpty(existingDocuments)) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Supporting Document already exists in list."); 
            }
            naturalResourceGeneralPermitDocument = new NaturalResourceGeneralPermitDocument();
            naturalResourceGeneralPermitDocument.setActiveInd(1);
            naturalResourceGeneralPermitDocument.setCreatedById(userId);
            naturalResourceGeneralPermitDocument.setCreateDate(new Date());
            List<Long> documentSubTypeTitleIds = supportDocumentMaintenanceRepo.findDocumentSubTypeTitleIdByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            if (CollectionUtils.isEmpty(documentSubTypeTitleIds)) {
              throw new ETrackConfigDuplicateDataFoundException("Document Title is not associated with Document Sub Type Configuration");
            }
            naturalResourceGeneralPermitDocument
                .setDocumentSubTypeTitleId(documentSubTypeTitleIds.get(0));
          }
          naturalResourceGeneralPermitDocument.setReqdNew(supportDocumentMaintenance.getReqdNew());
          naturalResourceGeneralPermitDocument.setReqdMod(supportDocumentMaintenance.getReqdMod());
          naturalResourceGeneralPermitDocument.setReqdExt(supportDocumentMaintenance.getReqdExt());
          naturalResourceGeneralPermitDocument.setReqdMnm(supportDocumentMaintenance.getReqdMnm());
          naturalResourceGeneralPermitDocument.setReqdMtn(supportDocumentMaintenance.getReqdMtn());
          naturalResourceGeneralPermitDocument.setReqdRen(supportDocumentMaintenance.getReqdRen());
          naturalResourceGeneralPermitDocument.setReqdRtn(supportDocumentMaintenance.getReqdRtn());
          naturalResourceGeneralPermitDocument.setReqdXfer(supportDocumentMaintenance.getReqdXfer());
          natGpDocumentRepo.save(naturalResourceGeneralPermitDocument);
        });
        return;
      case "e_required_doc_for_seqr":
        supportDocumentMaintenances.forEach(supportDocumentMaintenance -> {
          SEQRDocumentEntity seqrDocumentEntity = null;
          if (supportDocumentMaintenance.getUniqueId() != null) {
            Optional<SEQRDocumentEntity> seqrDocumentOptional =
                seqrDocumentRepo.findById(supportDocumentMaintenance.getUniqueId());
            if (!seqrDocumentOptional.isPresent()) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "SEQR Document Document is not available.");
            }
            seqrDocumentEntity = seqrDocumentOptional.get();
            seqrDocumentEntity.setActiveInd(supportDocumentMaintenance.getActiveInd());
            seqrDocumentEntity.setModifiedById(userId);
            seqrDocumentEntity.setModifiedDate(new Date());
          } else {
            List<SEQRDocumentEntity> existingDocuments = seqrDocumentRepo.findByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            if (!CollectionUtils.isEmpty(existingDocuments)) {
              throw new ETrackConfigDuplicateDataFoundException(
                  "Supporting Document already exists in list."); 
            }
            seqrDocumentEntity = new SEQRDocumentEntity();
            List<Long> documentSubTypeTitleIds = supportDocumentMaintenanceRepo.findDocumentSubTypeTitleIdByDocumentTitleId(
                supportDocumentMaintenance.getDocumentTitleId());
            seqrDocumentEntity
                .setDocumentSubTypeTitleId(documentSubTypeTitleIds.get(0));
            seqrDocumentEntity.setActiveInd(1);
            seqrDocumentEntity.setCreatedById(userId);
            seqrDocumentEntity.setCreateDate(new Date());
          }
          seqrDocumentEntity.setReqdNew(supportDocumentMaintenance.getReqdNew());
          seqrDocumentEntity.setReqdMod(supportDocumentMaintenance.getReqdMod());
          seqrDocumentEntity.setReqdExt(supportDocumentMaintenance.getReqdExt());
          seqrDocumentEntity.setReqdMnm(supportDocumentMaintenance.getReqdMnm());
          seqrDocumentEntity.setReqdMtn(supportDocumentMaintenance.getReqdMtn());
          seqrDocumentEntity.setReqdRen(supportDocumentMaintenance.getReqdRen());
          seqrDocumentEntity.setReqdRtn(supportDocumentMaintenance.getReqdRtn());
          seqrDocumentEntity.setReqdXfer(supportDocumentMaintenance.getReqdXfer());
          seqrDocumentRepo.save(seqrDocumentEntity);
        });
        return;
      default:
        throw new ETrackConfigDuplicateDataFoundException("No valid table data is passed");
    }
  }

  @Override
  public void persistTransactionTypeRuleConfigurationMaintenanceTableDetails(String userId,
      String contextId, String tableName, List transactionTypeRule) {
    @SuppressWarnings("unchecked")
    List<TransactionTypeRule> transactionTypeRules = (List<TransactionTypeRule>) transactionTypeRule;
    List<TransactionTypeRuleEntity> transactionTypeRuleEntity = transformationService.transferTransactionTypeRuleToEntity(
        userId, contextId, transactionTypeRules);
    transactionTypeRuleRepo.saveAll(transactionTypeRuleEntity);
  }

  @Override
  public Object retrieveTransactionTypeRuleConfigurationMaintenanceTableDetails(String userId,
      String contextId, String tableName, String permitSubCategory) {
    switch (tableName) {
      case "e_transaction_type_rule":
        List<TransactionTypeRuleEntity> transactionTypeRuleList = null;
        switch (permitSubCategory) {
          case "C":
            transactionTypeRuleList = transactionTypeRuleRepo.findAllRulesByPermitByRenewedInd(0);
            break;
          case "O":
            transactionTypeRuleList = transactionTypeRuleRepo.findAllRulesByPermitByRenewedInd(1);
            break;
          case "GP":
            transactionTypeRuleList = transactionTypeRuleRepo.findAllRulesByGeneralPermits();
            break;            
          default:
            throw new ETrackConfigDuplicateDataFoundException("No valid Permit Sub Category is passed");
        }
        return transformationService.transferTransactionTypeRuleToView(transactionTypeRuleList);
      default:
        throw new ETrackConfigDuplicateDataFoundException("No valid table data is passed");
    }
  }

  // @Override
  // public KeyValue persistTransTypeCode(KeyValue keyValue) {
  // try {
  // TransType transType = new TransType();
  // transType.setTransTypeCode(keyValue.getUniquekey());
  // transType.setTransTypeDesc(keyValue.getValue());
  // transtypeRepo.save(transType);
  // return keyValue;
  // } catch (Exception e) {
  // logger.error("Error while persisting trans type code {}", keyValue.getUniquekey(), e);
  // throw new ETrackConfigException("Error while persisting trans type code", e);
  // }
  // }

  // @Override
  // public List<PermitCategoryModel> getPermitCategory(String userId) {
  // try {
  // List<PermitCategoryModel> categoryModels = new ArrayList<>();
  // List<PermitCategoryEntity> categoryEntities = permitCategoryRepo.findAll();
  // categoryEntities.forEach(obj -> {
  // PermitCategoryModel categoryModel = new PermitCategoryModel();
  // BeanUtils.copyProperties(obj, categoryModel);
  // categoryModels.add(categoryModel);
  // });
  // return categoryModels;
  // } catch (Exception e) {
  // logger.error("Error while retriving Permit Category code {}", e);
  // throw new ETrackConfigException("Error while retriving Permit Category code", e);
  // }
  // }

}
