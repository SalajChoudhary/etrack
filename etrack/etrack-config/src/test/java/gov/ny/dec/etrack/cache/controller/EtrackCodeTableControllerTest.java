package gov.ny.dec.etrack.cache.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.etrack.cache.entity.GISLayerConfig;
import gov.ny.dec.etrack.cache.model.DocumentTypeData;
import gov.ny.dec.etrack.cache.model.ETrackCodeTable;
import gov.ny.dec.etrack.cache.model.GISLayerConfigView;
import gov.ny.dec.etrack.cache.model.InvoiceFeeType;
import gov.ny.dec.etrack.cache.model.KeyValue;
import gov.ny.dec.etrack.cache.model.PermitCategoryModel;
import gov.ny.dec.etrack.cache.model.UrlValues;
import gov.ny.dec.etrack.cache.service.impl.ETrackCodeTableServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class EtrackCodeTableControllerTest {

  @InjectMocks
  private EtrackCodeTableController etrackCodeTableController;

  @Mock
  private ETrackCodeTableServiceImpl eTrackCodeTableServiceImpl;

  private String userId = "1234";

  @Test
  public void getTableValuesTest() {
    List<ETrackCodeTable> codeTables = etrackCodeTableController.getTableValues();
    assertNotNull(codeTables);
  }

  @Test
  public void getTableDataTest() {
    List<UrlValues> codeTables = etrackCodeTableController.getTableData("table_name");
    assertNotNull(codeTables);
  }

  @Test
  public <T> void getDocumentTypeDateTest() {
    List<T> result = etrackCodeTableController.getDocumentTypeDate("table_name");
    assertNotNull(result);
  }
  //
  // @Test
  // public <T> void getPermitCategoryTest() {
  // List<T> result = etrackCodeTableController.getPermitCategory(userId);
  // assertNotNull(result);
  // }

  @Test
  public void updateSystemParameterTest() {
    List<KeyValue> keyValues = new ArrayList<>();
    etrackCodeTableController.updateSystemParameter(userId, keyValues);
    assertTrue(true);
  }

  @Test
  public <T> void persistDocumentTypeDataTest() {
    List<DocumentTypeData> documentTypeData = new ArrayList<>();
    List<T> result = etrackCodeTableController.persistDocumentTypeData(documentTypeData, userId);
    assertNotNull(result);
  }

  @Test
  public <T> void persistDocumentSubTypeDataTest() {
    List<DocumentTypeData> documentTypeData = new ArrayList<>();
    List<T> result = etrackCodeTableController.persistDocumentSubTypeData(documentTypeData, userId);
    assertNotNull(result);
  }

  @Test
  public <T> void persistDocumentTitleTest() {
    List<DocumentTypeData> documentTypeData = new ArrayList<>();
    List<T> result = etrackCodeTableController.persistDocumentTitle(documentTypeData, userId);
    assertNotNull(result);
  }

  // @Test
  // public void persistMessageTest() {
  // List<KeyValue> keyValues = new ArrayList<>();
  // etrackCodeTableController.persistMessage(keyValues);
  // }

  @Test
  public <T> void persistPermitCategoryTest() {
    List<PermitCategoryModel> permitCategoryModel = new ArrayList<>();
    List<T> result = etrackCodeTableController.persistPermitCategory(userId, permitCategoryModel);
    assertNotNull(result);
  }

  @Test
  public <T> void persistPermitTypeCodeTest() {
    List<PermitCategoryModel> permitCategoryModel = new ArrayList<>();
    Mockito.when(eTrackCodeTableServiceImpl.persistPermitTypeCode(Mockito.anyString(), anyString(), any()))
        .thenReturn(permitCategoryModel);
    List<T> result = etrackCodeTableController.persistPermitTypeCode(userId, permitCategoryModel);
    assertNotNull(result);
  }

  @Test
  public <T> void persistPermitTypeCodeTest_BadRequest() {
    Mockito.when(eTrackCodeTableServiceImpl.persistPermitTypeCode(Mockito.anyString(), anyString(), any()))
        .thenThrow(new RuntimeException());
    assertThrows(RuntimeException.class,
        () -> etrackCodeTableController.persistPermitTypeCode(userId, new ArrayList<>()));
  }


  @Test
  public <T> void persistGISLayerConfigurationTest() {
    List<GISLayerConfigView> gisLayerConfigs = new ArrayList<>();
    doNothing().when(eTrackCodeTableServiceImpl).persistGISLayerDetails(anyString(), anyString(), any());
    etrackCodeTableController.storeGISLayerConfigurationsRequestedByAdmin(userId, gisLayerConfigs);
  }
  
  @Test
  public void testRetrieveDocumentSubTypeTitleId() {
	  assertNotNull(etrackCodeTableController.retrieveDocumentSubTypeTitleId(1l));
  }
  
  @Test
  public void testretrieveDocumentSubType() {
	  assertNotNull(etrackCodeTableController.retrieveDocumentSubType(1l));
  }
  
  @Test
  public void testgetSWFacilitySubTypeByTypeId() {
	  assertNotNull(etrackCodeTableController.getSWFacilitySubTypeByTypeId(1, userId));
  }
  
  @Test
  public void testupdateSWFacilityType() {
	  etrackCodeTableController.updateSWFacilityType(userId, null);
	  assertTrue(true);
  }
  
  @Test
  public void testUpdateSWFacilitySubType() {
	  etrackCodeTableController.updateSWFacilitySubType(userId, null);
	  assertTrue(true);
  }
  
  @Test
  public void testPersistMessage() {
	  assertNotNull(etrackCodeTableController.persistMessage(null, userId));
  }
  
  @Test
  public void teststoreDocumentSubTypeTitles() {
	  etrackCodeTableController.storeDocumentSubTypeTitles(null, userId);
	  assertTrue(true);
  }
  
  @Test
  public void testpersistInvoiceFeeType() {
	  when(eTrackCodeTableServiceImpl.persistInvoiceFeeType(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(new InvoiceFeeType()));
	  assertNotNull(etrackCodeTableController.persistInvoiceFeeType(userId, null));
  }
  
  @Test
  public void testRetrieveDocumentAssociatedMaintenanceTableDetails() {
	  when(eTrackCodeTableServiceImpl.retrieveSupportDocumentMaintenanceTableDetails(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new Object());
	assertNotNull(etrackCodeTableController.retrieveDocumentAssociatedMaintenanceTableDetails("", userId, "", null, null));  
  }
  
  @Test
  public void testretrieveFacTypeAssociatedDetailsInDocumentMaintenance() {
	  when(eTrackCodeTableServiceImpl.retrieveDocumentTitleAssociatedSWFacTypesAndSubTypes(Mockito.any(), Mockito.any())).thenReturn(new Object());
	  assertNotNull(etrackCodeTableController.retrieveFacTypeAssociatedDetailsInDocumentMaintenance(userId));
  }
}
