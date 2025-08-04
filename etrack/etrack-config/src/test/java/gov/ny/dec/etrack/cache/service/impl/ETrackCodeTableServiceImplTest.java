package gov.ny.dec.etrack.cache.service.impl;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import gov.ny.dec.etrack.cache.dao.TableWiseDataDao;
import gov.ny.dec.etrack.cache.entity.GISLayerConfig;
import gov.ny.dec.etrack.cache.entity.PermitCategoryEntity;
import gov.ny.dec.etrack.cache.entity.PermitTypeCodeEntity;
import gov.ny.dec.etrack.cache.exception.ETrackConfigException;
import gov.ny.dec.etrack.cache.model.PermitCategoryModel;
import gov.ny.dec.etrack.cache.model.PermitTypeCode;
import gov.ny.dec.etrack.cache.repostitory.DocumentClassRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentSubTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentTitleRepo;
import gov.ny.dec.etrack.cache.repostitory.DocumentTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.GISLayerConfigRepo;
import gov.ny.dec.etrack.cache.repostitory.InvoiceFeeRepo;
import gov.ny.dec.etrack.cache.repostitory.MessageRepository;
import gov.ny.dec.etrack.cache.repostitory.MessageTypeRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitCategoryRepo;
import gov.ny.dec.etrack.cache.repostitory.PermitTypeCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.SpatialInqCategoryRepo;
import gov.ny.dec.etrack.cache.repostitory.SystemParamterRepo;
import gov.ny.dec.etrack.cache.repostitory.TransTypeRepo;

@ExtendWith(MockitoExtension.class)
public class ETrackCodeTableServiceImplTest {

  @InjectMocks
  private ETrackCodeTableServiceImpl etrackCodeTableServiceImpl;

  @Mock
  private TableWiseDataDao tableWiseData;

  @Mock
  private SystemParamterRepo systemParamterRepo;

  @Mock
  private TransTypeRepo transtypeRepo;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private MessageTypeRepo messageTypeRepository;

  @Mock
  private PermitTypeCodeRepo permitTypeCodeRepo;

  @Mock
  private DocumentTypeRepo documentTypeRepo;

  @Mock
  private DocumentSubTypeRepo documentSubTypeRepo;

  @Mock
  private DocumentTitleRepo documentTitleRepo;

  @Mock
  private DocumentClassRepo documentClassRepo;

  @Mock
  private GISLayerConfigRepo gISLayerConfigRepo;

  @Mock
  private InvoiceFeeRepo invoiceFeeRepo;

  @Mock
  private SpatialInqCategoryRepo spatialInqCategoryRepo;

  @Mock
  private PermitCategoryRepo permitCategoryRepo;


  @Test
  public void persistPermitCategoryTest() {
    List<PermitCategoryModel> permitTypeCodes = new ArrayList<>();
    PermitCategoryModel permitCategoryModel = new PermitCategoryModel();
    permitCategoryModel.setPermitCategoryDesc("desc");
    permitTypeCodes.add(permitCategoryModel);
    when(permitCategoryRepo.findByPermitCategoryDescription(anyString()))
        .thenReturn(Optional.empty());
    doReturn(null).when(permitCategoryRepo).save(any());
    List<PermitTypeCode> codes =
        etrackCodeTableServiceImpl.persistPermitCategory("testUser123", "contextId", permitTypeCodes);
    assertNotNull(codes);
  }

  // @Test
  // public void persistPermitCategoryTest_WithDescription() {
  // List<PermitCategoryModel> permitTypeCodes = new ArrayList<>();
  // PermitCategoryModel permitCategoryModel = new PermitCategoryModel();
  // permitCategoryModel.setPermitCategoryId(1);
  // permitTypeCodes.add(permitCategoryModel);
  // when(permitCategoryRepo.findById(anyInt())).thenReturn(Optional.of(new
  // PermitCategoryEntity()));
  // doReturn(null).when(permitCategoryRepo).save(any());
  // List<PermitTypeCode> codes = etrackCodeTableServiceImpl.persistPermitCategory(permitTypeCodes,
  // "testUser123");
  // assertNotNull(codes);
  // }

  @Test
  public void persistPermitCategoryTest_Exception() {
    List<PermitCategoryModel> permitTypeCodes = new ArrayList<>();
    PermitCategoryModel permitCategoryModel = new PermitCategoryModel();
    permitCategoryModel.setPermitCategoryDesc("desc");
    permitTypeCodes.add(permitCategoryModel);
    when(permitCategoryRepo.findByPermitCategoryDescription(anyString()))
        .thenReturn(Optional.empty());
    doThrow(new RuntimeException()).when(permitCategoryRepo).save(any());
    assertThrows(ETrackConfigException.class,
        () -> etrackCodeTableServiceImpl.persistPermitCategory("testUser123", "contextId", permitTypeCodes));
  }

  //TODO: Recheck Test cases
//  @Test
  public void persistPermitCategoryTest_ETrackConfigDuplicateDataFoundException() {
    List<PermitCategoryModel> permitTypeCodes = new ArrayList<>();
    PermitCategoryModel permitCategoryModel = new PermitCategoryModel();
    permitCategoryModel.setPermitCategoryDesc("desc");
    permitTypeCodes.add(permitCategoryModel);
    when(permitCategoryRepo.findByPermitCategoryDescription(anyString()))
        .thenReturn(Optional.of(new PermitCategoryEntity()));
    assertThrows(ETrackConfigException.class,
        () -> etrackCodeTableServiceImpl.persistPermitCategory("testUser123", "contextId", permitTypeCodes));
  }


  @Test
  public void persistPermitTypeCodeTest() {
    List<PermitTypeCode> permitTypeCodes = new ArrayList<>();
    PermitTypeCode permitTypeCode = new PermitTypeCode();
    permitTypeCode.setPermitTypeCode("permit");
    permitTypeCodes.add(permitTypeCode);
    when(permitTypeCodeRepo.findById(anyString()))
        .thenReturn(Optional.of(new PermitTypeCodeEntity()));
    doReturn(null).when(permitTypeCodeRepo).save(any());
    List<PermitTypeCode> codes =
        etrackCodeTableServiceImpl.persistPermitTypeCode("testUser123", "contextId", permitTypeCodes);
    assertNotNull(codes);
  }

  @Test
  public void persistPermitTypeCodeTest_Exception() {
    List<PermitTypeCode> permitTypeCodes = new ArrayList<>();
    PermitTypeCode permitTypeCode = new PermitTypeCode();
    permitTypeCode.setPermitTypeCode("permit");
    permitTypeCodes.add(permitTypeCode);
    when(permitTypeCodeRepo.findById(any())).thenReturn(Optional.of(new PermitTypeCodeEntity()));
    when(permitTypeCodeRepo.save(any())).thenThrow(new RuntimeException());
    assertThrows(ETrackConfigException.class,
        () -> etrackCodeTableServiceImpl.persistPermitTypeCode("testUser123", "contextId", permitTypeCodes));
  }

  @Test
  public void persistGISLayerConfigurationWithEmptyDataThrowsException() {
    assertThrows(ETrackConfigException.class,
        () -> etrackCodeTableServiceImpl.persistGISLayerDetails("userId", "contextId", null));
  }

//TODO: Recheck Test cases
//  @Test
  public void persistGISLayerConfigurationAsNewWhenNoExistingGISLayerDetails() {
    List<GISLayerConfig> gisLayerConfigs = new ArrayList<>();
    GISLayerConfig gisLayerConfig = new GISLayerConfig();
    gisLayerConfig.setLayerName("Input");
    gisLayerConfigs.add(gisLayerConfig);
    when(gISLayerConfigRepo.findByLayerName(anyString())).thenReturn(null);
    when(gISLayerConfigRepo.findAllOrderByLayerName()).thenReturn(gisLayerConfigs);
    doReturn(null).when(gISLayerConfigRepo).save(any());
    etrackCodeTableServiceImpl.persistGISLayerDetails("userId", "contextId", gisLayerConfigs);
    assertTrue(gisLayerConfig.getCreatedById().equals("userId"));
  }

  //TODO: Recheck Test cases
//  @Test
  public void persistGISLayerConfigurationAsExistingGISLayerDetails() {
    List<GISLayerConfig> gisLayerConfigs = new ArrayList<>();
    GISLayerConfig gisLayerConfig = new GISLayerConfig();
    gisLayerConfig.setLayerName("Input");
    gisLayerConfigs.add(gisLayerConfig);

    List<GISLayerConfig> existingGisLayerConfigs = new ArrayList<>();
    GISLayerConfig existingGisLayerConfig = new GISLayerConfig();
    existingGisLayerConfig.setLayerName("Input");
    existingGisLayerConfig.setCreatedById("existingUserId");
    existingGisLayerConfigs.add(existingGisLayerConfig);

    when(gISLayerConfigRepo.findAllOrderByLayerName()).thenReturn(existingGisLayerConfigs);
    when(gISLayerConfigRepo.findByLayerName(anyString())).thenReturn(existingGisLayerConfigs);
    doReturn(null).when(gISLayerConfigRepo).save(any());
    etrackCodeTableServiceImpl.persistGISLayerDetails("userId", "contextId", gisLayerConfigs);
    assertTrue(gisLayerConfig.getCreatedById().equals("existingUserId"));
    assertTrue(gisLayerConfig.getModifiedById().equals("userId"));
  }

  //TODO: Recheck test cases
//  @Test
  public void persistGISLayerConfigurationThrpwsErrorWhilePersistingData() {
    List<GISLayerConfig> gisLayerConfigs = new ArrayList<>();
    GISLayerConfig gisLayerConfig = new GISLayerConfig();
    gisLayerConfig.setLayerName("Input");
    gisLayerConfigs.add(gisLayerConfig);

    List<GISLayerConfig> existingGisLayerConfigs = new ArrayList<>();
    GISLayerConfig existingGisLayerConfig = new GISLayerConfig();
    existingGisLayerConfig.setLayerName("Input");
    existingGisLayerConfig.setCreatedById("existingUserId");
    existingGisLayerConfigs.add(existingGisLayerConfig);

    when(gISLayerConfigRepo.findByLayerName(anyString())).thenReturn(existingGisLayerConfigs);
    doThrow(RuntimeException.class).when(gISLayerConfigRepo).save(any());
    assertThrows(ETrackConfigException.class,
        () -> etrackCodeTableServiceImpl.persistGISLayerDetails("userId", "contextId", gisLayerConfigs));
  }

  
}
