package gov.ny.dec.etrack.cache.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeTitle;
import gov.ny.dec.etrack.cache.model.DocumentTypeData;
import gov.ny.dec.etrack.cache.model.ETrackCodeTable;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeeType;
import gov.ny.dec.etrack.cache.model.KeyValue;
import gov.ny.dec.etrack.cache.model.MaintanenceCodeTable;
import gov.ny.dec.etrack.cache.model.PermitCategoryModel;
import gov.ny.dec.etrack.cache.model.PermitTypeCode;
import gov.ny.dec.etrack.cache.model.SWFacilitySubTypeRequest;
import gov.ny.dec.etrack.cache.model.SWFacilityTypeRequest;
import gov.ny.dec.etrack.cache.model.SupportDocumentMaintenance;
import gov.ny.dec.etrack.cache.model.TransactionTypeRule;
import gov.ny.dec.etrack.cache.model.UrlValues;
import gov.ny.dec.etrack.cache.service.ETrackCodeTableService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/code-table")
public class EtrackCodeTableController<T> {

  private static Logger logger = LoggerFactory.getLogger(EtrackCodeTableController.class.getName());

  @Autowired
  private ETrackCodeTableService codeTableService;

  /**
   * Retrieve all the Categories.
   * 
   * @return - Returns {@ConfigType}
   */
  @GetMapping("/categories")
  @ApiOperation(value = "Gets list of Category values and Respective Table details")
  public List<ETrackCodeTable> getTableValues() {
    return codeTableService.getCategoryTables();
  }

  /**
   * Retrieve all the Table Data.
   * 
   * @return - Returns {@ConfigureCategory }
   */
  @GetMapping("/getTableDta")
  @ApiOperation(value = "Gets list of values to be shown")
  public List<UrlValues> getTableData(@RequestParam String tableName) {
    return codeTableService.getSelectedTableData(tableName);
  }

  /**
   * Retrieve Document Type Data, Sub Type and Document Title System parameter Permit Type and
   * Category Messages
   * 
   * @return - Returns {@DocumentTypeData}
   */
  @GetMapping("/view-table/{tableName}")
  @ApiOperation(value = "Gets list of Document Type, Sub Type, Document Title, "
      + "System parameter, Permit Type and Category, Messages ")
  public List<T> getDocumentTypeDate(@PathVariable(name = "tableName") String tableName) {
    return (List<T>) codeTableService.getConfigurationDataByRequestedTableName(tableName);
  }

  /**
   * Retrieve all the Document Sub Type Title details or for the requested Document Sub Type Title
   * Id.
   * 
   * @return - Returns {@link List}
   */
  @GetMapping({"/view-table/e_document_sub_type_title",
      "/view-table/e_document_sub_type_title/{id}"})
  @ApiOperation(
      value = "Retrieve all the Document Sub Type Title or for the requested Document Sub Type title id")
  public List<T> retrieveDocumentSubTypeTitleId(@PathVariable(required = false) Long id) {
    return (List<T>) codeTableService.retrieveDocumentSubTypeTitle(id);
  }

  /**
   * Retrieve all the Document Sub Type for the requested Document type id.
   * 
   * @return - Returns {@link List}
   */
  @GetMapping({"/view-table/e_document_sub_type/{id}"})
  @ApiOperation(
      value = "Retrieve all the Document Sub Type details for the requested Document Sub Type id")
  public List<T> retrieveDocumentSubType(@PathVariable Long id) {
    return (List<T>) codeTableService.retrieveDocumentSubType(id);
  }

  /**
   * Retrieve Document Type Data, Sub Type and Document Title System parameter Permit Type and
   * Category Messages
   * 
   * @return - Returns {@DocumentTypeData}
   */
  @GetMapping("/view-table/e_sw_facility_sub_type/{swFacilityTypeId}")
  @ApiOperation(value = "Gets list of Sw Facility Sub Type by Sw Facility Type ID")
  public List<T> getSWFacilitySubTypeByTypeId(
      @PathVariable(name = "swFacilityTypeId") Integer swFacilityTypeId,
      @RequestHeader final String userId) {
    return (List<T>) codeTableService.retrieveSWFacilitySubTypeByTypeId(swFacilityTypeId, userId);
  }

  /**
   * Persist System Parameter data.
   * 
   * @return - Returns {@SystemParameter}
   */
  @PostMapping("/addUpdate/e_system_parameter")
  @ApiOperation(value = "Store the System Paramter  and retruns 200 status successfully")
  public void updateSystemParameter(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final List<KeyValue> systemParameters) {
    final String contextId = UUID.randomUUID().toString();
    MaintanenceCodeTable maintanenceCodeTable = new MaintanenceCodeTable();
    maintanenceCodeTable.setKeyValues(systemParameters);
    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId,
        contextId);
    codeTableService.updateSystemParameter(maintanenceCodeTable);
  }

  /**
   * Persist swFacilityType data.
   * 
   */
  @PostMapping("/addUpdate/e_sw_facility_type")
  @ApiOperation(value = "Store the swFacilityType  and retruns 200 status successfully")
  public void updateSWFacilityType(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final List<SWFacilityTypeRequest> swFacilityTypes) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the SWFacilityTypeRequest. User Id {}, Context Id {}", userId,
        contextId);
    codeTableService.updateSWFacilityType(userId, contextId, swFacilityTypes);
  }

  /**
   * Persist swFacilitySubType data.
   * 
   */
  @PostMapping("/addUpdate/e_sw_facility_sub_type")
  @ApiOperation(value = "Store the swFacilitySubType  and retruns 200 status successfully")
  public void updateSWFacilitySubType(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody final List<SWFacilitySubTypeRequest> swFacilitySubType) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the SWFacilitySubTypeRequest. User Id {}, Context Id {}",
        userId, contextId);
    codeTableService.updateSWFacilitySubType(userId, contextId, swFacilitySubType);
  }

  /**
   * Persist Document Type data.
   * 
   * @return - Returns {@DocumentTypeData}
   */
  @PostMapping("addUpdate/e_document_type")
  @ApiOperation(value = "Persist dcoument type table and return 200 status successfully")
  public List<T> persistDocumentTypeData(@RequestBody List<DocumentTypeData> documentTypeData,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the SWFacilitySubTypeRequest. User Id {}, Context Id {}",
        userId, contextId);
    return codeTableService.persistDocumentType(userId, contextId, documentTypeData);
  }

  /**
   * Persist Document Sub Type data.
   * 
   * @return - Returns {@DocumentSubTypeData}
   */
  @PostMapping("addUpdate/e_document_sub_type")
  @ApiOperation(value = "Persist dcoument Sub type table and return 200 status successfully")
  public List<T> persistDocumentSubTypeData(@RequestBody List<DocumentTypeData> documentTypeData,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the SWFacilitySubTypeRequest. User Id {}, Context Id {}",
        userId, contextId);
    return codeTableService.persistDocumentSubType(userId, contextId, documentTypeData);
  }

  /**
   * Persist Document Title data.
   * 
   * @return - Returns {@DocumentTitle}
   */
  @PostMapping("addUpdate/e_document_title")
  @ApiOperation(value = "Persist dcoument title table and return 200 status successfully")
  public List<T> persistDocumentTitle(@RequestBody List<DocumentTypeData> documentTypeData,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the SWFacilitySubTypeRequest. User Id {}, Context Id {}",
        userId, contextId);
    return codeTableService.persistDocumentTitle(userId, contextId, documentTypeData);
  }

  /**
   * Persist Message Code.
   * 
   * @return - Returns {@MessageEntity}
   */
  @PostMapping("addUpdate/e_message")
  @ApiOperation(value = "Persist message table and return 200 status successfully")
  public List<T> persistMessage(@RequestBody List<DocumentTypeData> documentTypeData,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Message Type and Desc. User Id {}, Context Id {}", userId,
        contextId);
    return codeTableService.persistMessages(userId, contextId, documentTypeData);
  }

  /**
   * Store the Requested Document Sub Type Title.
   * 
   * @return - Returns {@link List}
   */
  @PostMapping("addUpdate/e_document_sub_type_title")
  @ApiOperation(value = "Store the Document Sub Type Title")
  public void storeDocumentSubTypeTitles(
      @RequestBody List<DocumentSubTypeTitle> documentSubTypeTitles,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into storeDocumentSubTypeTitles. User Id {}, Context Id {}", userId,
        contextId);
    codeTableService.storeDocumentSubTypeTitles(userId, contextId, documentSubTypeTitles);
  }

  // /**
  // * Persist Trans Type Code data.
  // *
  // * @return - Returns {@TransTypeData}
  // */
  // @PostMapping("addUpdate/e_trans_type_code")
  // @ApiOperation(value = "Persist trans type code table and return 200 status
  // successfully")
  // public KeyValue persistTransTypeCode(@RequestBody KeyValue keyValue) {
  // return codeTableService.persistTransTypeCode(keyValue);
  // }

  /**
   * Persist Permit Category data.
   * 
   * @return - Returns {@PermitCategoryModel}
   */

  @PostMapping("/addUpdate/e_permit_category")
  @ApiOperation(value = "Persist permit category table and return 200 status successfully")
  public List<T> persistPermitCategory(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<PermitCategoryModel> permitCategoryModels) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId,
        contextId);
    return codeTableService.persistPermitCategory(userId, contextId, permitCategoryModels);
  }

  /**
   * Persist Permit Type data.
   * 
   * @return - Returns {@PermitTypeCode}
   */

  @PostMapping("/addUpdate/e_permit_type_code")
  @ApiOperation(value = "Persist permit type code table and return 200 status successfully")
  public List<T> persistPermitTypeCode(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<PermitTypeCode> permitTypes) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Store the Keyword Text. User Id {}, Context Id {}", userId,
        contextId);
    return codeTableService.persistPermitTypeCode(userId, contextId, permitTypes);
  }

  /**
   * Store the requested GIS Layer details.
   * 
   * @param userId - User who initiates this request.
   * @param gisLayerConfigs - GIS Layer configuration details {@link List}
   * 
   * @return - Updated GIS Layer configuration details {@link List}
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/addUpdate/e_gis_layer_config")
  @ApiOperation(value = "Persist permit type code table and return 200 status successfully")
  public void storeGISLayerConfigurationsRequestedByAdmin(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<GISLayerConfigView> gisLayerConfigs) {
    final String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into storeGISLayerConfigurationsRequestedByAdmin. User Id {}, Context Id {}",
        userId, contextId);
    codeTableService.persistGISLayerDetails(userId, contextId, gisLayerConfigs);
    logger.info(
        "Exiting from storeGISLayerConfigurationsRequestedByAdmin. User Id {}, Context Id {}",
        userId, contextId);
  }

  @SuppressWarnings("unchecked")
  @PostMapping("/addUpdate/e_invoice_fee_type")
  @ApiOperation(value = "Persist invoice fee type and return 200 status successfully")
  public List<InvoiceFeeType> persistInvoiceFeeType(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<InvoiceFeeType> invoiceFeeType) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into persist invoice fee type. User Id {}, Context Id {}", userId,
        contextId);
    return codeTableService.persistInvoiceFeeType(invoiceFeeType, userId, contextId);
  }

  /**
   * Retrieve all the Required document maintenance configuration details.
   * 
   * @param userId - User who initiates this request.
   * @param permitTypeCode - Permit Type Code.
   * @param swFacTypeId - Solid Waste Facility Type Id.
   * @param swFacSubTypeId - Solid Waste Facility Sub Type Id.
   * 
   * @return - Support Document Maintenance table details.
   */
  @GetMapping("/document-config/{tableName}")
  @ApiOperation("End Point retrieves the Document Titles associated for the requested maintenance table.")
  public Object retrieveDocumentAssociatedMaintenanceTableDetails(
      @PathVariable @ApiParam(example = "tableName",
          value = "Support document maintenance table") String tableName,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader(required = false) @ApiParam(example = "SW",
          value = "Permit Type Code") final String permitTypeCode,
      @RequestHeader(required = false) @ApiParam(example = "1",
          value = "Solid Waste Facility Type Id") final Integer swFacTypeId,
      @RequestHeader(required = false) @ApiParam(example = "3",
          value = "Solid Waste Facility Sub Type Id") final Integer swFacSubTypeId) {

    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieveDocumentAssociatedMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
    tableName = tableName.toLowerCase();
    return codeTableService.retrieveSupportDocumentMaintenanceTableDetails(userId, contextId,
        tableName, permitTypeCode, swFacTypeId, swFacSubTypeId);
  }


  /**
   * End point retrieves the list of Solid Waste Facility Type and Sub Types which are associated
   * with the Document Title.
   * 
   * @param userId - User who initiates this request.
   * 
   * @return - List of Solid Waste Facility Type and Sub Types.
   */
  @GetMapping("/sw-fac-type-associate")
  @ApiOperation("Retrieve all the Solid Waste Facility Types and associated Sub Types "
      + "associated with the document titles for the maintenance activity")
  public Object retrieveFacTypeAssociatedDetailsInDocumentMaintenance(@RequestHeader @ApiParam(
      example = "shortname", value = "User id of the logged in user") final String userId) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveMaintenanceTableDetails. User Id {}, Context Id {}", userId,
        contextId);
    return codeTableService.retrieveDocumentTitleAssociatedSWFacTypesAndSubTypes(userId, contextId);
  }

  /**
   * Persist the details to the requested table name.
   * 
   * @param userId - User who initiates this request.
   * @return - Support Document Maintenance table details.
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/document-config/{tableName}")
  @ApiOperation("End Point to Persit the Document configuration details for the requested maintenance table.")
  public void persistDocumentAssociatedMaintenanceTableDetails(
      @PathVariable @ApiParam(example = "tableName",
          value = "Support document maintenance table") String tableName,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<SupportDocumentMaintenance> supportDocumentMaintenanceDetails) {

    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into persistDocumentAssociatedMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
    tableName = tableName.toLowerCase();
    codeTableService.persistSupportDocumentMaintenanceTableDetails(userId, contextId, tableName,
        supportDocumentMaintenanceDetails);
    logger.info(
        "Exiting from persistDocumentAssociatedMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
  }

  /**
   * Retrieve the Transaction Type Rule Configuration.
   * 
   * @param tableName - Maintenance table detail.
   * @param userId - User who initiates this request.
   * @param transactionTypeRule - Transaction Type Configuration details.
   */
  @GetMapping("/transaction-rule/{tableName}")
  @ApiOperation("End Point to retrieve the Transaction Type Rule configuration details for the requested maintenance table.")
  public Object retrieveTransactionTypeRuleConfigurationMaintenanceTableDetails(
      @PathVariable @ApiParam(example = "tableName",
          value = "Transaction Type Rule Configuration maintenance table") String tableName,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader final String permitSubCategory) {

    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into retrieveTransactionTypeRuleConfigurationMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
    tableName = tableName.toLowerCase();
    return codeTableService.retrieveTransactionTypeRuleConfigurationMaintenanceTableDetails(
        userId, contextId, tableName, permitSubCategory);
  }

  /**
   * Persist the Transaction Type Rule configuration with the requested details.
   * 
   * @param tableName - Maintenance table detail.
   * @param userId - User who initiates this request.
   * @param transactionTypeRule - Transaction Type Rule Configuration details.
   */
  @SuppressWarnings("unchecked")
  @PostMapping("/transaction-rule/{tableName}")
  @ApiOperation("End Point to Persist the Transaction Type Rule configuration details for the requested maintenance table.")
  public void persistTransactionTypeRuleConfigurationMaintenanceTableDetails(
      @PathVariable @ApiParam(example = "tableName",
          value = "Transaction Type Rule Configuration maintenance table") String tableName,
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestBody List<TransactionTypeRule> transactionTypeRule) {

    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into persistTransactionTypeRuleConfigurationMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
    tableName = tableName.toLowerCase();
    codeTableService.persistTransactionTypeRuleConfigurationMaintenanceTableDetails(
        userId, contextId, tableName, transactionTypeRule);
    logger.info(
        "Exiting from persistTransactionTypeRuleConfigurationMaintenanceTableDetails. User Id {}, Context Id {}",
        userId, contextId);
  }
}
