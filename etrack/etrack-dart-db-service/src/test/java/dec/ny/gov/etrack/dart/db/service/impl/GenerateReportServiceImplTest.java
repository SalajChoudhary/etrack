package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentEntity;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class GenerateReportServiceImplTest {

	@Mock
    private InvoiceRepo invoiceRepo;
	@Mock
	private PendingAppRepo pendingAppRepo;
	@Mock
	private SupportDocumentRepo supportDocumentRepo;
	@Mock
	private DartDbDAO dartDbDao;
	@Mock
	private DartDbService dartDBService;
	@Mock
	private FacilityRepo facilityRepo;
	@Mock
	private ApplicationRepo applicationRepo;
	@Mock
	private ApplicantRepo applicantRepo;
	@Mock
	private ProjectRepo projectRepo;

    @InjectMocks
    private GenerateReportServiceImpl generateReportService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

   @Test
    public void testRetrieveInvoiceReport_WhenInvoiceNotFound() {
    	 List<InvoiceEntity> invoices = new ArrayList<>();
    	 InvoiceEntity invoiceEntity = new InvoiceEntity();
    	 invoiceEntity.setInvoiceId(123L);
    	 invoiceEntity.setInvoiceFeeType1("");
    	 invoiceEntity.setInvoiceFeeType2("2");
    	 invoiceEntity.setInvoiceFeeType3("3");
    	 invoiceEntity.setInvoiceFeeTypeFee1(100);
    	 invoiceEntity.setInvoiceFeeTypeFee2(200);
    	 invoiceEntity.setInvoiceFeeTypeFee3(300);
    	 invoiceEntity.setProjectId(12L);
    	 invoiceEntity.setCreateDate(new Date());
    	 invoices.add(invoiceEntity);
    	 Mockito.lenient().when(invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(9985L, "444000004433")).thenReturn(invoices);
    	 List<String> invoiceFeesList = new ArrayList();
    	 invoiceFeesList.add("one,two");
    	 Set<String> feeTypes = new HashSet<>();
    	 feeTypes.add("");
    	 feeTypes.add("2");
    	 feeTypes.add("3");
    	 Mockito.lenient().when(invoiceRepo.findAllFeeTypeDesc(feeTypes)).thenReturn(invoiceFeesList);
    	 List<PendingApplication> applications = new ArrayList();
    	 PendingApplication pa = new PendingApplication();
    	 pa.setCity("Test");
    	 pa.setState("Test");
    	 pa.setZip("532421");
    	 applications.add(pa);
    	 Mockito.lenient().when(pendingAppRepo.findApplicationDetailForTheProjectId(1L)).thenReturn(applications);
    	 assertThrows(DartDBException.class,()-> generateReportService.retrieveInvoiceReport("dxdevada", "", 9985L,"444000004433"));

    }
    
    @Test
    private void generateDocumentsUploadedReport() {
    	 String userId="",contextId="";
         Long projectId=1L;
         Facility facility = new Facility();
         facility.setProjectId(1L);
         Mockito.lenient().when(facilityRepo.findByProjectId(Mockito.any())).thenReturn(facility);
         byte[] service = generateReportService.generateDocumentsUploadedReport(userId,contextId,projectId);
    }

    @Test
    public void testRetrieveInvoiceReport_WhenInvoiceFound() {
    	
        List<InvoiceEntity> invoices = new ArrayList<>();
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoices.add(invoiceEntity);
        Mockito.lenient().when(invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(1L, "")).thenReturn(invoices);

        assertThrows(BadRequestException.class, ()->generateReportService.retrieveInvoiceReport("userId", "contextId", 1L, "invoiceNum"));

    }
	
	@Test
public void testRetrievePermitCoverSheetReport_whenFacilityNotFound() {
    
    when(facilityRepo.findByProjectId(1L)).thenReturn(null);

    assertThrows(DartDBException.class, () ->
    generateReportService.retrievePermitCoverSheetReport("userId", "contextId", 1L));
}

//@Test
public void testRetrievePermitCoverSheetReport_whenFacilityFound() {
    // Setup
    Facility facility = mock(Facility.class);
    when(facilityRepo.findByProjectId(1L)).thenReturn(facility);
    when(facility.getDecId()).thenReturn("12345678901");
    when(applicationRepo.findPermitTypesAndTransTypesByProjectId(1L)).thenReturn(new ArrayList<>());
//
//    setupJasperMocks();

    Project project =  new Project();
    when(projectRepo.findById(1l)).thenReturn(Optional.of(project));
    
    PendingApplication application= new PendingApplication();
    List<PendingApplication> applications = new ArrayList<>();
    applications.add(application);
    when(pendingAppRepo.findApplicationDetailForTheProjectId(1l)).thenReturn(applications);

    assertThrows(DartDBException.class, () ->
    generateReportService.retrievePermitCoverSheetReport("userId", "contextId", 1L));

}

private void setupJasperMocks() {
    // Mocking the static methods of JasperCompileManager, JasperFillManager, etc.
    // This requires PowerMockito to mock static methods and constructors.
}

@Test
public void testGenerateDocumentsUploadedReport_whenNoDocumentsFound() {
 
    Mockito.lenient().when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1L)).thenReturn(Collections.emptyList());

    assertThrows(DartDBException.class, () ->
    generateReportService.generateDocumentsUploadedReport("userId", "contextId", 1L));
}

@Test
public void testGenerateDocumentsUploadedReport_whenDocumentsFound() {
   
	java.util.List<SupportDocumentEntity> documents = new ArrayList<>();
    documents.add(new SupportDocumentEntity());
    Mockito.lenient().when(supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(1L)).thenReturn(documents);
    setupJasperMocks();
    assertThrows(DartDBException.class, ()-> generateReportService.generateDocumentsUploadedReport("userId", "contextId", 1L));

}
}
