package gov.ny.dec.etrack.cache.service;

import java.util.List;
import gov.ny.dec.etrack.cache.entity.DocumentSubTypeTitle;
import gov.ny.dec.etrack.cache.model.SupportDocumentMaintenance;
import gov.ny.dec.etrack.cache.model.TransactionTypeRule;
import gov.ny.dec.etrack.cache.model.ETrackCodeTable;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeeType;
import gov.ny.dec.etrack.cache.model.MaintanenceCodeTable;
import gov.ny.dec.etrack.cache.model.PermitCategoryModel;
import gov.ny.dec.etrack.cache.model.PermitTypeCode;
import gov.ny.dec.etrack.cache.model.SWFacilitySubType;
import gov.ny.dec.etrack.cache.model.SWFacilitySubTypeRequest;
import gov.ny.dec.etrack.cache.model.SWFacilityTypeRequest;
import gov.ny.dec.etrack.cache.model.UrlValues;

public interface ETrackCodeTableService<T> {

  /**
   * Retrieve all the eTrack Code Table Configs.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link Categories} .
   */
  public List<ETrackCodeTable> getCategoryTables();

  /**
   * Retrieve all the eTrack Code Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link DocTypeData} .
   */
  public List<UrlValues> getSelectedTableData(String tableName);

  /**
   * Persist the eTrack System Parameter Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link DocTypeData} .
   */
  public void updateSystemParameter(MaintanenceCodeTable maintanenceCodeTable);

  /**
   * Retrieve all eTrack Configuration Table datas.
   * 
   * @return - Returns the {@link List} .
   */
  public List<T> getConfigurationDataByRequestedTableName(String tableName);


  /**
   * Persist the eTrack Document Type Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link DocTypeData} .
   */
  public List<T> persistDocumentType(final String userId, final String contextId,
      List<T> documentTypeData);

  /**
   * Persist the eTrack Document Title Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link DocTypeData} .
   */
  public List<T> persistDocumentTitle(final String userId, final String contextId,
      List<T> documentTypeData);

  /**
   * Persist the eTrack Document Sub Type Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link DocTypeData} .
   */
  public List<T> persistDocumentSubType(final String userId, final String contextId,
      List<T> documentTypeDatad);

  /**
   * Persist the eTrack Permit Category Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link PermitCategoryModel} .
   */
  public List<T> persistPermitCategory(final String userId, final String contextId,
      List<T> permitCategoryModels);

  /**
   * Persist the eTrack Message Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link Message} .
   */
  public List<T> persistMessages(final String userId, final String contextId,
      List<T> documentTypeData);

  /**
   * Persist the eTrack Permit Type Table data.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns the {@link PermitTypeCode} .
   */
  List persistPermitTypeCode(final String userId, final String contextId, List permitTypes);

  /**
   * Update the Solid Waste Facility Sub Type with the requested details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param swFacilitSubType - List of {@link SWFacilitySubTypeRequest}
   */
  void updateSWFacilitySubType(final String userId, final String contextId,
      List<SWFacilitySubTypeRequest> swFacilitSubType);

  /**
   * Update the Solid Waste Facility Type with the requested details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param swFacilityType -  List of {@link SWFacilityTypeRequest}
   */
  void updateSWFacilityType(final String userId, final String contextId,
      List<SWFacilityTypeRequest> swFacilityType);

  /**
   * Store the requested GIS Layer details (could be new or amended).
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param gisLayerConfigs - List of GIS Layer configurations.
   * 
   */
  void persistGISLayerDetails(final String userId, final String contextId,
      final List<GISLayerConfigView> gisLayerConfigs);

  /**
   * Retrieve Document Sub type titles.
   * 
   * @param id - Document Sub Type Title id.
   * 
   * @return - {@link List}
   */
  List<T> retrieveDocumentSubTypeTitle(final Long id);

  /**
   * Retrieve all the Document Sub Type associated with the document Type id.
   * 
   * @param id - Document Type id.
   * 
   * @return - {@link List}
   */
  List<T> retrieveDocumentSubType(final Long id);

  /**
   * Store the Document Sub Type Title.
   * 
   * @param documentSubTypeTitles - {@link List}
   * @param userId - User who initiates this request.
   */
  void storeDocumentSubTypeTitles(final String userId, final String contextId,
      List<DocumentSubTypeTitle> documentSubTypeTitles);

  /**
   * Retrieve all the SWFacility Sub Type associated with the document Type id.
   * 
   * @param id - SWFacility Type id.
   * 
   * @return - {@link List}
   */
  List<SWFacilitySubType> retrieveSWFacilitySubTypeByTypeId(Integer swFacilityTypeId,
      String userId);

  /**
   * Persist the list of passed invoice fee types>
   * 
   * @param invoiceFeeType - List of {@link InvoiceFeeType}
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - List of Invoice Fee types.
   */
  List<InvoiceFeeType> persistInvoiceFeeType(List<InvoiceFeeType> invoiceFeeType, String userId,
      String contextId);

  /**
   * Retrieve the Support Document Maintenance table details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param tableName - Name of the table which maintenance details to be retrieved.
   * @param permitTypeCode - Permit Type code.
   * @param swFacTypeId - Solid Waste Facility Type Id.
   * @param swFacSubTypeId - Solid Waste Facility Sub Type Id.
   * 
   * @return - Maintenance table details.
   */
  Object retrieveSupportDocumentMaintenanceTableDetails(final String userId, final String contextId,
      final String tableName, final String permitTypeCode, final Integer swFacTypeId,
      final Integer swFacSubTypeId);

  /**
   * Retrieve all the Document title configured Solid Waste Facility Type and Sub Types.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Result of Solid Waste Facility Type and Sub Type.
   */
  Object retrieveDocumentTitleAssociatedSWFacTypesAndSubTypes(String userId, String contextId);

  /**
   * Persist the document configuration details for the requested table name.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param tableName - Name of the table which maintenance details to be retrieved.
   * @param supportDocumentMaintenanceDetails - Support Document details.
   * @return
   */
  void persistSupportDocumentMaintenanceTableDetails(String userId, String contextId,
      String tableName, List<SupportDocumentMaintenance> supportDocumentMaintenanceDetails);

  /**
   * Persist the transaction Type Rule configuration maintenance tables. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param tableName - Name of the table which maintenance details to be persisted.
   * @param transactionTypeRule - Transaction Type Rule details.
   */
  void persistTransactionTypeRuleConfigurationMaintenanceTableDetails(String userId,
      String contextId, String tableName, List<TransactionTypeRule> transactionTypeRule);

  /**
   * Retrieve the transaction Type Rule configuration maintenance tables. 
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param tableName - Name of the table which maintenance details to be retrieved.
   * @param permitSubCategory - Permit Sub Category C - Construction, O - Operating and GP - General Permit.
   * 
   * @return - List of Transaction Type Rules.
   */
  Object retrieveTransactionTypeRuleConfigurationMaintenanceTableDetails(String userId,
      String contextId, String tableName, String permitSubCategory);
}
