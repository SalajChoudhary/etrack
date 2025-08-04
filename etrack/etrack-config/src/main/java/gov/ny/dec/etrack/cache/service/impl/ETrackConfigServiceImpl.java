package gov.ny.dec.etrack.cache.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import gov.ny.dec.etrack.cache.dao.DocTypeSubTypeDAO;
import gov.ny.dec.etrack.cache.dao.EnterpriseConfigurationDAO;
import gov.ny.dec.etrack.cache.dao.MessageDAO;
import gov.ny.dec.etrack.cache.dao.PermitTypeDAO;
import gov.ny.dec.etrack.cache.dao.SWFacilityTypeDAO;
import gov.ny.dec.etrack.cache.entity.ActionType;
import gov.ny.dec.etrack.cache.entity.ActivityTaskStatus;
import gov.ny.dec.etrack.cache.entity.ApplicantType;
import gov.ny.dec.etrack.cache.entity.CountryCode;
import gov.ny.dec.etrack.cache.entity.DevelopmentType;
import gov.ny.dec.etrack.cache.entity.DocTypeSubType;
import gov.ny.dec.etrack.cache.entity.GISLayerConfig;
import gov.ny.dec.etrack.cache.entity.Message;
import gov.ny.dec.etrack.cache.entity.PermitType;
import gov.ny.dec.etrack.cache.entity.ProposedUseCode;
import gov.ny.dec.etrack.cache.entity.PublicType;
import gov.ny.dec.etrack.cache.entity.ResidentialDevelopType;
import gov.ny.dec.etrack.cache.entity.SWFacilityTypeAndSubTypeEntity;
import gov.ny.dec.etrack.cache.entity.SpatialInqCategory;
import gov.ny.dec.etrack.cache.entity.StateCode;
import gov.ny.dec.etrack.cache.entity.SystemParameterEntity;
import gov.ny.dec.etrack.cache.entity.TransType;
import gov.ny.dec.etrack.cache.exception.ETrackConfigNoDataFoundException;
import gov.ny.dec.etrack.cache.model.ConfigType;
import gov.ny.dec.etrack.cache.model.ETrackDocType;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeesDetail;
import gov.ny.dec.etrack.cache.model.SWFacilitySubType;
import gov.ny.dec.etrack.cache.model.SWFacilityType;
import gov.ny.dec.etrack.cache.repostitory.ActionTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.ActivityTaskStatusRepo;
import gov.ny.dec.etrack.cache.repostitory.ApplicantTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.CountryCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.DevelopmentTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.GISLayerConfigRepo;
import gov.ny.dec.etrack.cache.repostitory.InvoiceFeeRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.ProposedUseCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.PublicTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.RegionRepo;
import gov.ny.dec.etrack.cache.repostitory.ResidentialDevelopTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.SpatialInqCategoryRepo;
import gov.ny.dec.etrack.cache.repostitory.StateCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.SystemParameterRepo;
import gov.ny.dec.etrack.cache.repostitory.TransTypeRepo;
import gov.ny.dec.etrack.cache.service.ETrackConfigService;
import gov.ny.dec.etrack.cache.util.ETrackConfigHandler;

@Service
@Transactional(readOnly = true)
public class ETrackConfigServiceImpl implements ETrackConfigService {

  @Autowired
  private DocTypeSubTypeDAO docTypeSubTypeDAO;

  @Autowired
  private MessageDAO messageDAO;

  @Autowired
  private ETrackConfigHandler configHandler;

  @Autowired
  private ApplicantTypeRepo applicantTypeDAO;

  @Autowired
  private ActivityTaskStatusRepo activityTaskStatusDAO;

  @Autowired
  private DevelopmentTypeRepo developmentTypeDAO;

  @Autowired
  private ProposedUseCodeRepo proposedUseCodeRepo;

  @Autowired
  private PublicTypeRepo publicTypeRepo;

  @Autowired
  private CountryCodeRepo countryCodeRepo;

  @Autowired
  private StateCodeRepo stateCodeRepo;

  @Autowired
  private ResidentialDevelopTypeRepo residentialTypeRepo;

  @Autowired
  private ActionTypeRepo actionTypeRepo;

//  @Autowired
//  private SupportDocConfigRepo supportDocConfigRepo;

  @Autowired
  private RegionRepo regionRepo;

  @Autowired
  private SWFacilityTypeDAO swFacilityTypeDAO;
  
  @Autowired
  private PermitTypeDAO permitTypeDao;

  @Autowired
  private TransTypeRepo transTypeRepo;

  @Autowired
  private InvoiceFeeRepo invoiceFeeRepo;
  
  @Autowired
  private SpatialInqCategoryRepo spatialCategoryRepo;
  
  @Autowired
  private SystemParameterRepo systemParameterRepo;
  
  @Autowired
  private PermitTypeRepo permitTypeRepo;
  
  @Autowired
  private EnterpriseConfigurationDAO enterpriseConfigurationDAO;
  
  @Autowired  
  private GISLayerConfigRepo gisConfigRepo;
  
  private static final String GENERAL_PERMIT = "general";
  private static final String NON_GENERAL_PERMIT = "non-general";

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackConfigServiceImpl.class.getName());


  @Override
  public ResponseEntity<Map<String, Map<Integer, ETrackDocType>>> getDocTypeAndSubTypes(
      final String userId, final String contextId) {
    logger.info("Requesting to retrieve the DocType and SubTypes in service");
    return new ResponseEntity<>(retrieveAllDocTypesAndSubTypes(userId, contextId), HttpStatus.OK);
  }

//  @Override
//  public ResponseEntity<Map<Integer, ETrackDocType>> getDocTypeAndSubTypesByLangCode(
//      final String langCode, final String userId, final String contextId) {
//    logger.info(
//        "Requesting to retrieve the DocType and SubTypes in service for the language {} . User id: {} context id: {}",
//        langCode, userId, contextId);
//
//    Map<Integer, ETrackDocType> docTypesAndSubTypeForTheLangCode =
//        retrieveAllDocTypesAndSubTypes(userId, contextId).get(langCode);
//
//    if (CollectionUtils.isEmpty(docTypesAndSubTypeForTheLangCode)) {
//      throw new ETrackConfigNoDataFoundException(
//          "No DocTypes and Sub Types found for the Language code" + langCode
//              + " in ETrack Database");
//    }
//    logger.info("Received the DocType and SubType in service for the language {}", langCode);
//    return new ResponseEntity<>(docTypesAndSubTypeForTheLangCode, HttpStatus.OK);
//  }

  @Override
  public ResponseEntity<Map<String, Map<String, String>>> getMessages(final String userId,
      final String contextId) {
    logger.info("Requesting to retrieve the Messages in service. User id: {} context id: {}");
    return new ResponseEntity<>(retrieveMessages(userId, contextId), HttpStatus.OK);
  }

//  @Override
//  public ResponseEntity<Map<String, String>> getMessagesByLangCode(final String langCode,
//      final String userId, final String contextId) {
//
//    logger.info(
//        "Requesting to retrieve the Messages in service for the language {}. User id: {} context id: {}",
//        langCode, userId, contextId);
//
//    Map<String, String> languageCodeMessages = retrieveMessages(userId, contextId).get(langCode);
//    if (CollectionUtils.isEmpty(languageCodeMessages)) {
//      logger.error("No Messages found for the Language code {} in ETrack Database", langCode);
//      throw new ETrackConfigNoDataFoundException(
//          "No Messages found for the Language code " + langCode + " in ETrack Database");
//    }
//    logger.info("Received the Messages in service for the language {}. User id: {} context id: {}",
//        langCode, userId, contextId);
//    return new ResponseEntity<>(languageCodeMessages, HttpStatus.OK);
//  }

  private Map<String, Map<Integer, ETrackDocType>> retrieveAllDocTypesAndSubTypes(
      final String userId, final String contextId) {
    List<DocTypeSubType> docTypesAndSubTypes =
        docTypeSubTypeDAO.getDocTypeAndSubTypes(userId, contextId);
    if (CollectionUtils.isEmpty(docTypesAndSubTypes)) {
      logger.error("No DocTypes and Sub Types found in ETrack Database. User id: {} context id: {}",
          userId, contextId);
      throw new ETrackConfigNoDataFoundException(
          "No DocTypes and Sub Types found in ETrack Database");
    }
    return configHandler.getDocTypeAndSubTypeDetails(docTypesAndSubTypes, userId, contextId);
  }

  private Map<String, Map<String, String>> retrieveMessages(final String userId,
      final String contextId) {
    List<Message> messages = messageDAO.getAllMessages(userId, contextId);
    if (CollectionUtils.isEmpty(messages)) {
      logger.error("No Messages found in ETrack Database. User id: {} context id: {}", userId,
          contextId);
      throw new ETrackConfigNoDataFoundException("No Messages found in ETrack Database");
    }
    return configHandler.convertMessages(messages, userId, contextId);
  }

  @Override
  public ConfigType getConfigTypes() {
    List<ApplicantType> applicantTypes = new ArrayList<>();
    applicantTypeDAO.findAll().forEach(applicantType -> {
      applicantTypes.add(applicantType);
    });
    ConfigType configType = new ConfigType();
    configType.setApplicantTypes(applicantTypes);

    List<ActivityTaskStatus> statuses = new ArrayList<>();
    activityTaskStatusDAO.findAll().forEach(activityTaskStatus -> {
      statuses.add(activityTaskStatus);
    });
    configType.setActivityTaskStatus(statuses);

    List<DevelopmentType> developmentTypes = developmentTypeDAO.findByActiveInd(1);

    DevelopmentType notApplicable = null;

    if (!CollectionUtils.isEmpty(developmentTypes)) {
      for (DevelopmentType developmentType : developmentTypes) {
        if (developmentType.getDevelopmentTypeDesc().equals("N/A")) {
          notApplicable = developmentType;
        }
      }
      developmentTypes.remove(notApplicable);
      List<DevelopmentType> developmentTypesList = developmentTypes.stream()
          .sorted(Comparator.comparing(DevelopmentType::getDevelopmentTypeDesc))
          .collect(Collectors.toList());
      developmentTypesList.add(notApplicable);
      configType.setDevelopmentTypes(developmentTypesList);
    }

    List<ProposedUseCode> proposedUseCodes = proposedUseCodeRepo.findByActiveInd(1);

    if (!CollectionUtils.isEmpty(proposedUseCodes)) {
      configType.setProposedUseCodes(proposedUseCodes);
    }

    List<PublicType> publicTypes = publicTypeRepo.findByActiveInd(1);
    if (!CollectionUtils.isEmpty(publicTypes)) {
      List<PublicType> publicTypeList = publicTypes.stream()
          .sorted(Comparator.comparing(PublicType::getDisplayOrder)).collect(Collectors.toList());
      configType.setPublicTypes(publicTypeList);
    }

    List<CountryCode> countryCodes = new ArrayList<>();
    countryCodeRepo.findAll().forEach(countryCode -> {
      countryCodes.add(countryCode);
    });

    List<CountryCode> countryCodesList = countryCodes.stream()
        .sorted(Comparator.comparing(CountryCode::getCountryName)).collect(Collectors.toList());
    configType.setCountries(countryCodesList);

    List<StateCode> stateCodes = new ArrayList<>();
    stateCodeRepo.findAll().forEach(state -> {
      stateCodes.add(state);
    });
    List<StateCode> stateCodesList = stateCodes.stream()
        .sorted(Comparator.comparing(StateCode::getStateCode)).collect(Collectors.toList());
    configType.setStates(stateCodesList);


    List<ResidentialDevelopType> structureTypes = residentialTypeRepo.findAllByActiveInd(1);

    if (!CollectionUtils.isEmpty(structureTypes)) {
      ResidentialDevelopType notApplicableResidentialDevelopType = null;
      for (ResidentialDevelopType structureType : structureTypes) {
        if (structureType.getResDevTypeDesc().equals("N/A")) {
          notApplicableResidentialDevelopType = structureType;
        }
      }
      structureTypes.remove(notApplicableResidentialDevelopType);
      List<ResidentialDevelopType> structureTypesList = structureTypes.stream()
          .sorted(Comparator.comparing(ResidentialDevelopType::getResDevTypeDesc))
          .collect(Collectors.toList());
      structureTypesList.add(notApplicableResidentialDevelopType);
      configType.setResidentialDevelopType(structureTypesList);
    }

    try {
      Iterable<ActionType> actionTypes = actionTypeRepo.findBySystemNoteInd(0);
      
      List<ActionType> actionTypesList = new ArrayList<>();
      actionTypes.forEach(actionType -> {
        actionTypesList.add(actionType);
      });
      
      List<ActionType> sortedActionType = actionTypesList.stream()
          .sorted(Comparator.comparing(ActionType::getDisplayOrder)).collect(Collectors.toList());
      configType.setActionTypes(sortedActionType);

    } catch (Exception e) {
      logger.error("Error while interacting with PROJECT_ACTION_TYPE table. Error {} ", e.getMessage(), e);
    }
    return configType;
  }

//  @Override
//  public Map<String, List<PermitType>> getPermitType(Integer generalPermitId) {
//    return getAllPermitTypes().get(generalPermitId);
//  }

  @Override
  public Map<String, Map<String, List<PermitType>>> getAllPermitTypesByProjectId(
      final String userId, final String contextId, final Long projectId) {

    logger.info("Entering into getAllPermitTypesByProjectId(). "
        + "Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);
    
    Map<String, Map<String, List<PermitType>>> permitCategoryMap = new LinkedHashMap<>();
    
//    List<PermitType> permitTypes = permitTypeRepo.findAllPermitByPermitCategoryInd();
    List<PermitType> permitTypes = permitTypeDao.findAllPermitTypesByProjectId(userId, contextId, projectId);
    if (CollectionUtils.isEmpty(permitTypes)) {
      return permitCategoryMap;
    }
    for (PermitType permitType : permitTypes) {
      Map<String, List<PermitType>> permitTypeMap =
          permitCategoryMap.get(permitType.getPermitCategoryDesc());
      if (permitTypeMap != null) {
        if (permitType.getGeneralPermitInd() == 1) {
          List<String> relatedRegularPermitTypes = permitTypeRepo.findRelatedRegularPermitsForTheGeneralPermit(permitType.getPermitTypeCode());
          if (!CollectionUtils.isEmpty(relatedRegularPermitTypes)) {
            List<String> permitTypeCode = new ArrayList<>();
            List<String> permitTypeDesc = new ArrayList<>();
            relatedRegularPermitTypes.forEach(relatedPermitType -> {
              String[] relatedPermit = relatedPermitType.split(",");
              permitTypeCode.add(relatedPermit[0]);
              permitTypeDesc.add(relatedPermit[1]);
            });
            permitType.setRelatedRegularPermitTypeDescForGp(String.join(",", permitTypeDesc));
            permitType.setRelatedRegularPermitTypeCodeForGp(String.join(",", permitTypeCode));
          }
          if (CollectionUtils.isEmpty(permitTypeMap.get(GENERAL_PERMIT))) {
            List<PermitType> generalPermits = new ArrayList<>();
            generalPermits.add(permitType);
            permitTypeMap.put(GENERAL_PERMIT, generalPermits);
          } else {
            permitTypeMap.get(GENERAL_PERMIT).add(permitType);
          }
        } else if (permitType.getGeneralPermitInd() == 0) {
          if (CollectionUtils.isEmpty(permitTypeMap.get(NON_GENERAL_PERMIT))) {
            List<PermitType> nonGeneralPermits = new ArrayList<>();
            nonGeneralPermits.add(permitType);
            permitTypeMap.put(NON_GENERAL_PERMIT, nonGeneralPermits);
          } else {
            permitTypeMap.get(NON_GENERAL_PERMIT).add(permitType);
          }
        }
      } else {
        permitTypeMap = new LinkedHashMap<>();
        List<PermitType> nonGeneralPermits = new ArrayList<>();
        List<PermitType> generalPermits = new ArrayList<>();
        if (permitType.getGeneralPermitInd() == 0) {
          nonGeneralPermits.add(permitType);
        } else if (permitType.getGeneralPermitInd() == 1) {
          generalPermits.add(permitType);
        }
        permitTypeMap.put(GENERAL_PERMIT, generalPermits);
        permitTypeMap.put(NON_GENERAL_PERMIT, nonGeneralPermits);
        permitCategoryMap.put(permitType.getPermitCategoryDesc(), permitTypeMap);
      }
    }

    /*
     * for (PermitType permitType : permitTypes) { if
     * (permitTypeResponse.get(permitType.getPermitCategoryDesc()) != null) {
     * permitTypeResponse.get(permitType.getPermitCategoryDesc()).add(permitType); } else {
     * Map<String, List<PermitType>> permitTypeMap = new HashMap<>(); List<PermitType> permits = new
     * LinkedList<>(); permits.add(permitType);
     * permitTypeMap.put(permitType.getPermitCategoryDesc(), permits); } }
     */

    Map<String, Map<String, List<PermitType>>> sortedCategoryPermitTypeMap = new LinkedHashMap<>();
    for (String category : permitCategoryMap.keySet()) {
      Map<String, List<PermitType>> sortedPermitTypeMap = new LinkedHashMap<>();
      Map<String, List<PermitType>> permitTypeMap = permitCategoryMap.get(category);
      for (String generalNonGeneral : permitTypeMap.keySet()) {
        List<PermitType> sorterdList = permitTypeMap.get(generalNonGeneral).stream()
            .sorted(
                Comparator.comparing(PermitType::getPermitTypeDesc, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
        sortedPermitTypeMap.put(generalNonGeneral, sorterdList);
      }
      sortedCategoryPermitTypeMap.put(category, sortedPermitTypeMap);
    }
    return sortedCategoryPermitTypeMap;
  }

//  @Override
//  public Iterator<SupportDocConfig> findAllSupportConfig(String userId) {
//    return supportDocConfigRepo.findAll().iterator();
//  }

  
  @Override
  public List<String> findAppRegions(String userId, String contextId) {
    return regionRepo.findAllRegionByActiveInd(1);
  }

  @Override
  public List<SWFacilityType> findSWFacilityType(String userId, String contextId) {
    List<SWFacilityTypeAndSubTypeEntity> facilityTypes =  swFacilityTypeDAO.getSWFacilityType(userId, contextId);
    List<SWFacilityType> swFacilityTypes = new ArrayList<>();
    if (!CollectionUtils.isEmpty(facilityTypes)) {
      Map<Integer, SWFacilityType> swFacilityTypeMap = new HashMap<>();
      facilityTypes.forEach(facilityType -> {
        SWFacilityType swFacilityType = swFacilityTypeMap.get(facilityType.getSwFacilityTypeId());
        if (swFacilityType == null) {
          swFacilityType = new SWFacilityType();
          swFacilityType.setSwFacilityTypeId(facilityType.getSwFacilityTypeId());
          StringBuilder sb = new StringBuilder(facilityType.getFacilityTypeDesc());
          if (StringUtils.hasText(facilityType.getFtReg())) {
            sb.append(" (").append(facilityType.getFtReg()).append(")");
          }
          swFacilityType.setFacilityType(sb.toString());
          if (facilityType.getSwFacilitySubTypeId() != null) {
            SWFacilitySubType swFacilitySubType = new SWFacilitySubType();
            swFacilitySubType.setSwFacilitySubTypeId(facilityType.getSwFacilitySubTypeId());
            
            StringBuilder subtype = new StringBuilder(facilityType.getSubTypeDescription());
            if (StringUtils.hasText(facilityType.getSubReg())) {
              subtype.append(" (").append(facilityType.getSubReg()).append(")");
            }
            swFacilitySubType.setFacilitySubType(subtype.toString());
            List<SWFacilitySubType> subTypes = new ArrayList<>();
            subTypes.add(swFacilitySubType);
            swFacilityType.setFacilitySubTypes(subTypes);
          }
          swFacilityTypeMap.put(facilityType.getSwFacilityTypeId(), swFacilityType);
        } else {
          if (facilityType.getSwFacilitySubTypeId() != null) {
            SWFacilitySubType swFacilitySubType = new SWFacilitySubType();
            swFacilitySubType.setSwFacilitySubTypeId(facilityType.getSwFacilitySubTypeId());
            swFacilitySubType.setFacilitySubType(facilityType.getSubTypeDescription().concat(" (").concat(facilityType.getSubReg()).concat(")"));
            swFacilityType.getFacilitySubTypes().add(swFacilitySubType);
          }
        }
      });
      
      List<SWFacilityType> swFacilityTypeDetails = new ArrayList<>();
      List<SWFacilityType> swFacilityTypeWithSubTypeDetails = new ArrayList<>();
      swFacilityTypeMap.keySet().forEach(swFacilityTypeId -> {
        SWFacilityType swFacilityType = swFacilityTypeMap.get(swFacilityTypeId);
        if (CollectionUtils.isEmpty(swFacilityType.getFacilitySubTypes())) {
          swFacilityTypeDetails.add(swFacilityType);
        } else {
          swFacilityTypeWithSubTypeDetails.add(swFacilityType);
        }
      });
      
      List<SWFacilityType> sortedSWFacilityTypeWithSubTypeDetails = swFacilityTypeWithSubTypeDetails.stream().sorted(Comparator.comparing(
          SWFacilityType::getFacilityType)).collect(Collectors.toList());
      
      List<SWFacilityType> sortedSWwFacilityTypeDetails =  swFacilityTypeDetails.stream().sorted(Comparator.comparing(
          SWFacilityType::getFacilityType)).collect(Collectors.toList());
      
      sortedSWFacilityTypeWithSubTypeDetails.forEach(swFacilityType -> {
        sortedSWwFacilityTypeDetails.add(swFacilityType);
      });
      return sortedSWwFacilityTypeDetails;
//     for (Integer swFacilityTypeId : swFacilityTypeMap.keySet()) {
//        swFacilityTypes.add(swFacilityTypeMap.get(swFacilityTypeId));
//      }
//      swFacilityTypes = swFacilityTypes.stream().sorted(Comparator.comparing(
//          SWFacilityType::getFacilityType)).collect(Collectors.toList());
      
    }
    return swFacilityTypes;
  }

  @Override
  public Map<String, List<InvoiceFeesDetail>> getInvoiceFeesConfig(String userId, String contextId) {
    Map<String, List<InvoiceFeesDetail>> invoiceFeesDetails = new HashMap<>();
    invoiceFeeRepo.findByActiveInd(1).forEach(invoiceFeesEntity -> {
      InvoiceFeesDetail invoiceDetail = new InvoiceFeesDetail();
      invoiceDetail.setInvoiceFee(invoiceFeesEntity.getInvoiceFee());
      invoiceDetail.setInvoiceFeeDesc(invoiceFeesEntity.getInvoiceFeeDesc());
      invoiceDetail.setInvoiceFeeType(invoiceFeesEntity.getInvoiceFeeType());
      List<InvoiceFeesDetail> invoiceFeesListPerPermitType = invoiceFeesDetails.get(invoiceFeesEntity.getPermitTypeCode());
      if (CollectionUtils.isEmpty(invoiceFeesListPerPermitType)) {
        invoiceFeesListPerPermitType = new ArrayList<>();
        invoiceFeesListPerPermitType.add(invoiceDetail);
        invoiceFeesDetails.put(invoiceFeesEntity.getPermitTypeCode(), invoiceFeesListPerPermitType);
      } else {
        invoiceFeesListPerPermitType.add(invoiceDetail);
      }
    });
    return invoiceFeesDetails;
  }

  public Iterable<TransType> getTransTypes() {
    return transTypeRepo.findAll();
  }

  @Override
  public Map<String, List<SpatialInqCategory>> getSpatialInqCategories() {
    List<SpatialInqCategory> activeSpatialInquiryCategories = spatialCategoryRepo.findByActiveInd(1);
    Map<String, List<SpatialInqCategory>> categoryAvailabilityByUser = new HashMap<>();
    if (!CollectionUtils.isEmpty(activeSpatialInquiryCategories)) {
      activeSpatialInquiryCategories.forEach(activeSpatialInquiryCategory -> {
        if (CollectionUtils.isEmpty(categoryAvailabilityByUser.get(activeSpatialInquiryCategory.getCategoryAvailTo()))) {
          List<SpatialInqCategory> spatialInqCategories = new ArrayList<>();
          spatialInqCategories.add(activeSpatialInquiryCategory);
          categoryAvailabilityByUser.put(activeSpatialInquiryCategory.getCategoryAvailTo(), spatialInqCategories);
        } else {
          categoryAvailabilityByUser.get(activeSpatialInquiryCategory.getCategoryAvailTo()).add(activeSpatialInquiryCategory);
        }
      });
      categoryAvailabilityByUser.keySet().forEach(categoryAvailableTo -> {
        List<SpatialInqCategory> sortedSptialInquiryCategories =  categoryAvailabilityByUser.get(categoryAvailableTo).stream().sorted(
            Comparator.comparing(SpatialInqCategory::getDisplayOrder)).collect(Collectors.toList());
        categoryAvailabilityByUser.put(categoryAvailableTo, sortedSptialInquiryCategories);
      });
    }
    return categoryAvailabilityByUser;
  }

  @Override
  public Map<String, String> getSystemParameters() {
    Iterable<SystemParameterEntity> systemParameters = systemParameterRepo.findAll();
    Map<String, String> systemParameterMap = new HashMap<>();
    systemParameters.forEach(systemParameter -> {
      systemParameterMap.put(systemParameter.getUrlId(), systemParameter.getUrlLink());
    });
    return systemParameterMap;
  }

  @Override
  public Map<String, Object> getXtraIdProgIdAndSplAttneCodes() {
    return enterpriseConfigurationDAO.retriveXTRAProgIdsAndSpecialAttnCodes();
  }

  @Override
  @Cacheable(value = "gisLayers")
  public List<GISLayerConfigView> getGISLayers() {
    List<GISLayerConfig> gisConfigs = gisConfigRepo.findByActiveInd(1);
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
    return gisConfigViews;
  }

  @Override
  public Map<String, List<String>> retrievePermitsGroupBySubCategory() {
    List<String> constructionPermits = permitTypeRepo.findAllConstructionPermits();
    List<String> operatingPermits = permitTypeRepo.findAllOperatingPermits();
    List<String> generalPermits = permitTypeRepo.findAllGeneralPermits();
    Map<String, List<String>> renewedIndAndPermits = new HashMap<>();
    renewedIndAndPermits.put("C", constructionPermits);
    renewedIndAndPermits.put("O", operatingPermits);
    renewedIndAndPermits.put("GP", generalPermits);
    return renewedIndAndPermits;
  }

  // ETrackConfigServiceImpl.java

  @Override
  public Map<String, Map<String, List<PermitType>>> getActivePermitTypesByProjectId(
          final String userId, final String contextId, final Long projectId) {

    logger.info("Entering into getActivePermitTypesByProjectId(). "
            + "Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);

    Map<String, Map<String, List<PermitType>>> permitCategoryMap = new LinkedHashMap<>();

    // Rationale: This is the core fix for the data retrieval flaw. A new, hypothetical
    // repository method `findAllActivePermitTypesByProjectId` is called here. This method
    // would be implemented to explicitly filter for `active_ind = 1`, ensuring
    // that only active permits are retrieved from the database for the public-facing UI.
    // The previous method `findAllPermitTypesByProjectId` remains untouched.
    List<PermitType> permitTypes = permitTypeDao.findAllActivePermitTypesByProjectId(userId, contextId, projectId);

    if (CollectionUtils.isEmpty(permitTypes)) {
      return permitCategoryMap;
    }
    for (PermitType permitType : permitTypes) {
      Map<String, List<PermitType>> permitTypeMap =
              permitCategoryMap.get(permitType.getPermitCategoryDesc());
      if (permitTypeMap != null) {
        if (permitType.getGeneralPermitInd() == 1) {
          List<String> relatedRegularPermitTypes = permitTypeRepo.findRelatedRegularPermitsForTheGeneralPermit(permitType.getPermitTypeCode());
          if (!CollectionUtils.isEmpty(relatedRegularPermitTypes)) {
            List<String> permitTypeCode = new ArrayList<>();
            List<String> permitTypeDesc = new ArrayList<>();
            relatedRegularPermitTypes.forEach(relatedPermitType -> {
              String[] relatedPermit = relatedPermitType.split(",");
              permitTypeCode.add(relatedPermit[0]);
              permitTypeDesc.add(relatedPermit[1]);
            });
            permitType.setRelatedRegularPermitTypeDescForGp(String.join(",", permitTypeDesc));
            permitType.setRelatedRegularPermitTypeCodeForGp(String.join(",", permitTypeCode));
          }
          if (CollectionUtils.isEmpty(permitTypeMap.get(GENERAL_PERMIT))) {
            List<PermitType> generalPermits = new ArrayList<>();
            generalPermits.add(permitType);
            permitTypeMap.put(GENERAL_PERMIT, generalPermits);
          } else {
            permitTypeMap.get(GENERAL_PERMIT).add(permitType);
          }
        } else if (permitType.getGeneralPermitInd() == 0) {
          if (CollectionUtils.isEmpty(permitTypeMap.get(NON_GENERAL_PERMIT))) {
            List<PermitType> nonGeneralPermits = new ArrayList<>();
            nonGeneralPermits.add(permitType);
            permitTypeMap.put(NON_GENERAL_PERMIT, nonGeneralPermits);
          } else {
            permitTypeMap.get(NON_GENERAL_PERMIT).add(permitType);
          }
        }
      } else {
        permitTypeMap = new LinkedHashMap<>();
        List<PermitType> nonGeneralPermits = new ArrayList<>();
        List<PermitType> generalPermits = new ArrayList<>();
        if (permitType.getGeneralPermitInd() == 0) {
          nonGeneralPermits.add(permitType);
        } else if (permitType.getGeneralPermitInd() == 1) {
          generalPermits.add(permitType);
        }
        permitTypeMap.put(GENERAL_PERMIT, generalPermits);
        permitTypeMap.put(NON_GENERAL_PERMIT, nonGeneralPermits);
        permitCategoryMap.put(permitType.getPermitCategoryDesc(), permitTypeMap);
      }
    }

    Map<String, Map<String, List<PermitType>>> sortedCategoryPermitTypeMap = new LinkedHashMap<>();
    for (String category : permitCategoryMap.keySet()) {
      Map<String, List<PermitType>> sortedPermitTypeMap = new LinkedHashMap<>();
      Map<String, List<PermitType>> permitTypeMap = permitCategoryMap.get(category);
      for (String generalNonGeneral : permitTypeMap.keySet()) {
        List<PermitType> sorterdList = permitTypeMap.get(generalNonGeneral).stream()
                .sorted(
                        Comparator.comparing(PermitType::getPermitTypeDesc, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        sortedPermitTypeMap.put(generalNonGeneral, sorterdList);
      }
      sortedCategoryPermitTypeMap.put(category, sortedPermitTypeMap);
    }
    return sortedCategoryPermitTypeMap;
  }


}
