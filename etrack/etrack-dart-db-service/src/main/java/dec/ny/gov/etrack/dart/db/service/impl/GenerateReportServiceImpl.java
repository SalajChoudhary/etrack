package dec.ny.gov.etrack.dart.db.service.impl;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.entity.PendingApplication;
import dec.ny.gov.etrack.dart.db.entity.Project;
import dec.ny.gov.etrack.dart.db.entity.PublicDetail;
import dec.ny.gov.etrack.dart.db.entity.SupportDocumentEntity;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.model.Document;
import dec.ny.gov.etrack.dart.db.model.DocumentItemDetail;
import dec.ny.gov.etrack.dart.db.model.SupportDocument;
import dec.ny.gov.etrack.dart.db.model.UploadedDocumentDetail;
import dec.ny.gov.etrack.dart.db.model.jasper.ApplicantReport;
import dec.ny.gov.etrack.dart.db.model.jasper.ApplicationContactReport;
import dec.ny.gov.etrack.dart.db.model.jasper.FacilityReport;
import dec.ny.gov.etrack.dart.db.model.jasper.InvoiceDto;
import dec.ny.gov.etrack.dart.db.model.jasper.PermitCoverSheetDto;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.ApplicationRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;
import dec.ny.gov.etrack.dart.db.repo.PendingAppRepo;
import dec.ny.gov.etrack.dart.db.repo.ProjectRepo;
import dec.ny.gov.etrack.dart.db.repo.SupportDocumentRepo;
import dec.ny.gov.etrack.dart.db.service.DartDbService;
import dec.ny.gov.etrack.dart.db.service.GenerateReportService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;

@Service
public class GenerateReportServiceImpl implements GenerateReportService {

  @Autowired
  private InvoiceRepo invoiceRepo;
  @Autowired
  private PendingAppRepo pendingAppRepo;
  @Autowired
  private SupportDocumentRepo supportDocumentRepo;
  @Autowired
  private DartDbDAO dartDbDao;
  @Autowired
  private DartDbService dartDBService;
  @Autowired
  private FacilityRepo facilityRepo;
  @Autowired
  private ApplicationRepo applicationRepo;
  @Autowired
  private ApplicantRepo applicantRepo;
  @Autowired
  private ProjectRepo projectRepo;


  private static final Logger logger = LoggerFactory.getLogger(GenerateReportServiceImpl.class);
  private SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");
  private DecimalFormat formatAmountWithputDecimal = new DecimalFormat("###,###,###.##");

  @Override
  public byte[] retrieveInvoiceReport(final String userId, final String contextId,
      final Long projectId, final String invoiceNum) {

    logger.info(
        "Entering into Generate Invoice Report. Invoice number {} , " + "User Id {}, Context Id {}",
        invoiceNum, userId, contextId);
    List<InvoiceEntity> invoices =
        invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(projectId, invoiceNum);
    if (CollectionUtils.isEmpty(invoices)) {
      throw new BadRequestException("INVOICE_NOT_FOUND",
          "There is no invoice associated with this Invoice Number " + invoiceNum, projectId);
    }

    Set<String> feeTypes = new HashSet<>();
    try {
      InvoiceEntity invoiceEntity = invoices.get(0);
      feeTypes.add(invoiceEntity.getInvoiceFeeType1());
      if (StringUtils.hasLength(invoiceEntity.getInvoiceFeeType2())) {
        feeTypes.add(invoiceEntity.getInvoiceFeeType2());
      }
      if (StringUtils.hasLength(invoiceEntity.getInvoiceFeeType3())) {
        feeTypes.add(invoiceEntity.getInvoiceFeeType3());
      }
      List<String> invoiceFeesList = invoiceRepo.findAllFeeTypeDesc(feeTypes);
      logger.info("Fee Type Description details {}", invoiceFeesList);
      Map<String, String> invoiceFeeTypeMap = new HashMap<>();
      invoiceFeesList.forEach(invoiceFeeType -> {
        String[] invoiceFeeTypeDesc = invoiceFeeType.split(",");
        invoiceFeeTypeMap.put(invoiceFeeTypeDesc[0], invoiceFeeTypeDesc[1]);
      });

      InvoiceDto invoiceDto = new InvoiceDto();
      invoiceDto.setDecUrl("www.dec.ny.gov");
      invoiceDto.setBillingSite(String.valueOf(projectId));
      invoiceDto.setInvoiceDate(mmDDYYYFormat.format(invoiceEntity.getCreateDate()));
      Integer totalAmount = 0;
      invoiceDto.setInvoiceNum(invoiceEntity.getFmisInvoiceNum());
      if (StringUtils.hasLength(invoiceEntity.getInvoiceFeeType1())) {
        invoiceDto.setPermitType1(invoiceFeeTypeMap.get(invoiceEntity.getInvoiceFeeType1()));
        totalAmount += invoiceEntity.getInvoiceFeeTypeFee1();
        invoiceDto.setPermitTypeFee1("$" + 
            formatAmountWithputDecimal.format(invoiceEntity.getInvoiceFeeTypeFee1()));
      }

      if (StringUtils.hasLength(invoiceEntity.getInvoiceFeeType2())) {
        invoiceDto.setPermitType2(invoiceFeeTypeMap.get(invoiceEntity.getInvoiceFeeType2()));
        totalAmount += invoiceEntity.getInvoiceFeeTypeFee2();
        invoiceDto.setPermitTypeFee2("$" + 
            formatAmountWithputDecimal.format(invoiceEntity.getInvoiceFeeTypeFee2()));
      }

      if (StringUtils.hasLength(invoiceEntity.getInvoiceFeeType3())) {
        invoiceDto.setPermitType3(invoiceFeeTypeMap.get(invoiceEntity.getInvoiceFeeType3()));
        totalAmount += invoiceEntity.getInvoiceFeeTypeFee3();
        invoiceDto.setPermitTypeFee3("$" + 
            formatAmountWithputDecimal.format(invoiceEntity.getInvoiceFeeTypeFee3()));
      }

      invoiceDto.setTotalAmount("$" + formatAmountWithputDecimal.format(totalAmount));
      List<PendingApplication> applications =
          pendingAppRepo.findApplicationDetailForTheProjectId(projectId);

      PendingApplication invoiceApplicationDetail = applications.get(0);
      List<ApplicantDto> lrpDetails = applicantRepo.findLRPDetailsByProjectId(projectId);
      ApplicantDto primaryLRPDto = lrpDetails.get(lrpDetails.size() - 1);
      Map<String, Object> applicantDetails = dartDbDao.getApplicantDetails(userId, contextId, projectId, primaryLRPDto.getPublicId());
      PublicDetail primaryLRPDetail = (PublicDetail) ((ArrayList<PublicDetail>) applicantDetails.get("p_public_cur")).get(0);
      String facilityName = invoiceApplicationDetail.getFacilityName();
      if(StringUtils.hasLength(invoiceApplicationDetail.getDecId())) {
    	  String formattedDecId = invoiceApplicationDetail.getDecId().substring(0, 1) + '-'
      			+ invoiceApplicationDetail.getDecId().substring(1, 5) + '-'
      			+ invoiceApplicationDetail.getDecId().substring(5, 10);
    	  facilityName += ' ';
    	  facilityName += formattedDecId;
      }
      invoiceDto.setFacilityName(facilityName);
      invoiceDto.setName(primaryLRPDto.getDisplayName());
      invoiceDto.setStreet1(primaryLRPDetail.getStreet1());
      if(StringUtils.hasLength(primaryLRPDetail.getStreet2())) {
    	  invoiceDto.setStreet2(primaryLRPDetail.getStreet2());
      }
      if(StringUtils.hasLength(primaryLRPDetail.getCountry())) {
    	  invoiceDto.setCountryCode(primaryLRPDetail.getCountry());
      }
      /* Invoice Detail needs to be revisited for Online Portal.
       * if (projectId % 2 == 0) { invoiceDto.setPayonlinedetail(
       * "To pay online with a credit card, please go to your Project Workspace, ");
       * invoiceDto.setPayonlinedetail1("accessed from the ");
       * invoiceDto.setOnlinesubmitterurl("Online Submitter Portal.");
       * invoiceDto.setOnlineremitto("or remit to:"); } else {
       * invoiceDto.setAnalystremitto("Please remit to: "); }
       */
      invoiceDto.setAnalystremitto("Please remit to: ");
      invoiceDto.setRevenueurl("revenue@dec.ny.gov");

      StringBuilder cityStateZip = new StringBuilder();
      cityStateZip.append(primaryLRPDetail.getCity());
      if (StringUtils.hasLength(primaryLRPDetail.getState())) {
        cityStateZip.append(", ").append(primaryLRPDetail.getState());
      }
      if (StringUtils.hasLength(primaryLRPDetail.getZip())) {
        cityStateZip.append(", ").append(primaryLRPDetail.getZip());
      }

      invoiceDto.setCitystatezip(cityStateZip.toString());
      List<InvoiceDto> invoiceList = new ArrayList<>();
      invoiceList.add(invoiceDto);
      JRBeanCollectionDataSource beanCollectionDataSource =
          new JRBeanCollectionDataSource(invoiceList);
      InputStream invoiceReportStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("etrack_invoice_data_bean_report.jrxml");
//          getClass().getClassLoader().getResourceAsStream("etrack_invoice_data_bean_report.jrxml");
      JasperReport invoiceJasperReport = JasperCompileManager.compileReport(invoiceReportStream);
      JRSaver.saveObject(invoiceJasperReport, "etrack_invoice_report.jasper");
      Map<String, Object> params = new HashMap<>();
      JasperPrint jasperPrint =
          JasperFillManager.fillReport(invoiceJasperReport, params, beanCollectionDataSource);
      logger.info("Preparing the report file to be generated");
      byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
      logger.info(
          "Invoice Report generated successfully "
              + "for the invoice nunber {}, User Id {}, Context Id {}",
          invoiceNum, userId, contextId);
      return report;
    } catch (Exception e) {
      logger.error("Error while generating invoice report ", e);
      throw new DartDBException("INVOICE_GENERATE_REPORT_ERR",
          "Error while generating the invoice Report for the invoice number " + invoiceNum, e);
    }
  }
  
  @Override
  public byte[] generateDocumentsUploadedReport(final String userId, final String contextId,
      final Long projectId) {

    logger.info("Entering into Generate Documents Uploadee Report. Project Id {} , "
        + "User Id {}, Context Id {}", projectId, userId, contextId);
    try {
      Facility facility = facilityRepo.findByProjectId(projectId);
      if (facility == null) {
        throw new BadRequestException("NO_FACILITY_FOUND",
            "There is no facility found for this project " + projectId, projectId);
      }

      List<SupportDocumentEntity> uploadedDocuments =
          supportDocumentRepo.findAllUploadedSupportDocumentsByProjectId(projectId);
      Map<Long, SupportDocumentEntity> uploadedDocumentsMap = new HashMap<>();
      if (!CollectionUtils.isEmpty(uploadedDocuments)) {
        uploadedDocuments.forEach(uploadedDocument -> {
          uploadedDocumentsMap.put(uploadedDocument.getDocumentSubTypeTitleId(), uploadedDocument);
        });
      }
      
      UploadedDocumentDetail uploadedDocumentDetail = new UploadedDocumentDetail();
      uploadedDocumentDetail.setProjectId(projectId);
      if (StringUtils.hasLength(facility.getDecId())) {
    	String formattedDecId = facility.getDecId().substring(0, 1) + '-'
    			+ facility.getDecId().substring(1, 5) + '-'
    			+ facility.getDecId().substring(5, 10);
        uploadedDocumentDetail.setDecId(formattedDecId);
      } else {
        uploadedDocumentDetail.setDecId("NEW");
      }
      uploadedDocumentDetail.setFacilityName(facility.getFacilityName());
      List<String> permitAndTransTypesList =
          applicationRepo.findPermitTypesAndTransTypesByProjectId(projectId);
      List<String> newPermits = new ArrayList<>();
      List<String> modificationPermits = new ArrayList<>();
      List<String> renewalPermits = new ArrayList<>();
      if (!CollectionUtils.isEmpty(permitAndTransTypesList)) {
        permitAndTransTypesList.forEach(permitAndTransType -> {
          String[] permitAndTransTypes = permitAndTransType.split(",");

          if (permitAndTransTypes[1].equals("NEW")) {
            newPermits.add(permitAndTransTypes[0]);
          } else if (permitAndTransTypes[1].startsWith("M")) {
            modificationPermits.add(permitAndTransTypes[0]);
          } else if (permitAndTransTypes[1].startsWith("R")) {
            renewalPermits.add(permitAndTransTypes[0]);
          }
        });
      }
      uploadedDocumentDetail.setNewPermits(newPermits);
      uploadedDocumentDetail.setModificationPermits(modificationPermits);
      uploadedDocumentDetail.setRenewalPermits(renewalPermits);
      
      List<DocumentItemDetail> requiredDocumentItemDetailsList = new ArrayList<>();
      List<DocumentItemDetail> relatedDocumentItemDetailsList = new ArrayList<>();
      List<DocumentItemDetail> seqrDocumentItemDetailsList = new ArrayList<>();
      List<DocumentItemDetail> shpaDocumentItemDetailsList = new ArrayList<>();
      Object supportDocSummaryObj = dartDBService.retrieveSupportDocumentSummary(userId, contextId, projectId);
      List<Document> requiredDocs = new ArrayList<>();
      List<Document> relatedDocs = new ArrayList<>();
      List<Document> seqrDocs = new ArrayList<>();
      List<Document> shpaDocs = new ArrayList<>();
      
      if(supportDocSummaryObj != null) {
    	  SupportDocument supportDocSummary = (SupportDocument) supportDocSummaryObj;
          requiredDocs = supportDocSummary.getRequiredDoc();
          relatedDocs = supportDocSummary.getRelatedDoc();
          seqrDocs = supportDocSummary.getSeqrDoc();
          shpaDocs = supportDocSummary.getShpaDoc();
      }
      
      if (!CollectionUtils.isEmpty(requiredDocs)) {
        requiredDocs.forEach(doc -> {
          DocumentItemDetail documentItemDetail = new DocumentItemDetail();      
          documentItemDetail.setDocumentTitle(doc.getDocumentTitle());
          if ("Y".equals(doc.getUploadInd())) {
            
            if(StringUtils.isEmpty(doc.getRefDocumentDesc())) {
            	documentItemDetail.setRefLocation("Received");
            }
            
            else {
            	documentItemDetail.setRefLocation(doc.getRefDocumentDesc());
            }          
          } else {
            documentItemDetail.setRefLocation("Not Received");
          }
          requiredDocumentItemDetailsList.add(documentItemDetail);
        });
      }
      if (!CollectionUtils.isEmpty(relatedDocs)) {
          relatedDocs.forEach(doc -> {
            if ("Y".equals(doc.getUploadInd())) {
            	DocumentItemDetail documentItemDetail = new DocumentItemDetail();
            	documentItemDetail.setDocumentTitle(doc.getDocumentTitle());
              
            	if(StringUtils.isEmpty(doc.getRefDocumentDesc())) {
            		documentItemDetail.setRefLocation("Received");
            	}	
              
            	else {
            		documentItemDetail.setRefLocation(doc.getRefDocumentDesc());
            	}          
            	relatedDocumentItemDetailsList.add(documentItemDetail);
            }
          });
      }
      if (!CollectionUtils.isEmpty(seqrDocs)) {
          seqrDocs.forEach(doc -> {
            if ("Y".equals(doc.getUploadInd())) {
                DocumentItemDetail documentItemDetail = new DocumentItemDetail();    
            	documentItemDetail.setDocumentTitle(doc.getDocumentTitle());
              
            	if(StringUtils.isEmpty(doc.getRefDocumentDesc())) {
              	documentItemDetail.setRefLocation("Received");
            	}
              
            	else {
            		documentItemDetail.setRefLocation(doc.getRefDocumentDesc());
            	}          
            	seqrDocumentItemDetailsList.add(documentItemDetail);
            } 
          });
      }
      
      if (!CollectionUtils.isEmpty(shpaDocs)) {
          shpaDocs.forEach(doc -> {
            if ("Y".equals(doc.getUploadInd())) {            
            	DocumentItemDetail documentItemDetail = new DocumentItemDetail();     
            	documentItemDetail.setDocumentTitle(doc.getDocumentTitle());
            	if(StringUtils.isEmpty(doc.getRefDocumentDesc())) {
            		documentItemDetail.setRefLocation("Received");
            	}
              
            	else {
            		documentItemDetail.setRefLocation(doc.getRefDocumentDesc());
            	}          
            	shpaDocumentItemDetailsList.add(documentItemDetail);
            }
          });
      }

      List<UploadedDocumentDetail> uploadedDocumentDetailsList = new ArrayList<>();
      uploadedDocumentDetailsList.add(uploadedDocumentDetail);
      
      List<dec.ny.gov.etrack.dart.db.model.Public> applicantsList = new ArrayList<>();
      List<DocumentItemDetail> missingSignaturesList = new ArrayList<>();
      Object reqdApplicants = dartDBService.retrieveRequiredApplicantsToSign(userId, contextId, projectId);
      Map<String, Object> reqdApplicantsMap = new HashMap<>();
      if(!(reqdApplicants instanceof Collection<?> ) &&
    	(reqdApplicants instanceof Map<?, ?>)) {
    	  reqdApplicantsMap = (Map<String, Object>) reqdApplicants;
    	  Object reqdSignedDoc = reqdApplicantsMap.get("reqdsigneddoc");
    	  applicantsList = (List<dec.ny.gov.etrack.dart.db.model.Public>) reqdSignedDoc;
      }
      
      if(!CollectionUtils.isEmpty(applicantsList)) {
    	  applicantsList.forEach(applicant -> {   		  
    		  if("N".equals(applicant.getAcknowledgeInd()) || 
    				  !StringUtils.hasLength(applicant.getAcknowledgeInd())) {
    			  DocumentItemDetail missingSignature = new DocumentItemDetail();
        		  missingSignature.setDocumentTitle(applicant.getDisplayName());
        		  missingSignature.setRefLocation(applicant.getRole());
    			  missingSignaturesList.add(missingSignature);
    		  }
    	  });
      }
      String requiredInd = CollectionUtils.isEmpty(requiredDocumentItemDetailsList) ? 
    		  "0" : "1";
      String relatedInd = CollectionUtils.isEmpty(relatedDocumentItemDetailsList) ? 
    		  "0" : "1";
      String shpaInd = CollectionUtils.isEmpty(shpaDocumentItemDetailsList) ? 
    		  "0" : "1";
      String seqrInd = CollectionUtils.isEmpty(seqrDocumentItemDetailsList) ? 
    		  "0" : "1";
      String missingSignaturesInd = CollectionUtils.isEmpty(missingSignaturesList) ? 
    		  "0" : "1";
      
      JRBeanCollectionDataSource requiredDocumentItemListDataSource = 
    	  new JRBeanCollectionDataSource(requiredDocumentItemDetailsList); 
      JRBeanCollectionDataSource relatedDocumentItemListDataSource = 
        	  new JRBeanCollectionDataSource(relatedDocumentItemDetailsList); 
      JRBeanCollectionDataSource shpaDocumentItemListDataSource = 
        	  new JRBeanCollectionDataSource(shpaDocumentItemDetailsList); 
      JRBeanCollectionDataSource seqrDocumentItemListDataSource = 
        	  new JRBeanCollectionDataSource(seqrDocumentItemDetailsList); 
      JRBeanCollectionDataSource beanCollectionDataSource =
          new JRBeanCollectionDataSource(uploadedDocumentDetailsList);
      JRBeanCollectionDataSource missingSignaturesDataSource = 
    		  new JRBeanCollectionDataSource(missingSignaturesList);
      InputStream uploadedDocumentsReportStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("document_detail_report.jrxml");
      InputStream requiredDocumentItemsReportStream =
              Thread.currentThread().getContextClassLoader().getResourceAsStream("document_item_detail_report.jrxml");
      InputStream relatedDocumentItemsReportStream =
              Thread.currentThread().getContextClassLoader().getResourceAsStream("document_item_detail_report.jrxml");
      InputStream shpaDocumentItemsReportStream =
              Thread.currentThread().getContextClassLoader().getResourceAsStream("document_item_detail_report.jrxml");
      InputStream seqrDocumentItemsReportStream =
              Thread.currentThread().getContextClassLoader().getResourceAsStream("document_item_detail_report.jrxml");
      InputStream missingSignaturesReportStream =
              Thread.currentThread().getContextClassLoader().getResourceAsStream("document_item_detail_report.jrxml");
      JasperReport uploadedDocumentsJasperReport =
          JasperCompileManager.compileReport(uploadedDocumentsReportStream);
      JasperReport requiredDocumentItemsJasperReport =
              JasperCompileManager.compileReport(requiredDocumentItemsReportStream);
      JasperReport relatedDocumentItemsJasperReport =
              JasperCompileManager.compileReport(relatedDocumentItemsReportStream);
      JasperReport shpaDocumentItemsJasperReport =
              JasperCompileManager.compileReport(shpaDocumentItemsReportStream);
      JasperReport seqrDocumentItemsJasperReport =
              JasperCompileManager.compileReport(seqrDocumentItemsReportStream);
      JasperReport missingSignaturesJasperReport = 
    		  JasperCompileManager.compileReport(missingSignaturesReportStream);
      JRSaver.saveObject(uploadedDocumentsJasperReport, "document_detail_report.jasper");
      
      Map<String, Object> params = new HashMap<>();
      params.put("requiredItemsDataSource", requiredDocumentItemListDataSource);
      params.put("relatedItemsDataSource", relatedDocumentItemListDataSource);
      params.put("shpaItemsDataSource", shpaDocumentItemListDataSource);
      params.put("seqrItemsDataSource", seqrDocumentItemListDataSource);
      params.put("missingSignaturesDataSource", missingSignaturesDataSource);
      params.put("requiredItemsReport", requiredDocumentItemsJasperReport);
      params.put("relatedItemsReport", relatedDocumentItemsJasperReport);
      params.put("seqrItemsReport", seqrDocumentItemsJasperReport);
      params.put("shpaItemsReport", shpaDocumentItemsJasperReport);
      params.put("missingSignaturesReport", missingSignaturesJasperReport);
      params.put("requiredInd", requiredInd);
      params.put("relatedInd", relatedInd);
      params.put("shpaInd", shpaInd);
      params.put("seqrInd", seqrInd);
      params.put("missingSignaturesInd", missingSignaturesInd);

      JasperPrint jasperPrint = JasperFillManager.fillReport(uploadedDocumentsJasperReport, params,
          beanCollectionDataSource);
      logger.info("Preparing the Uploaded Documents report file to be generated");
      byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
      logger.info("Uploaded document Report generated successfully "
          + "for the Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);
      return report;
    } catch (Exception e) {
      logger.error("Error while generating Uploaded Documents report ", e);
      throw new DartDBException("UPLOADED_DOC_GENERATE_REPORT_ERR",
          "Error while generating the Upoaded Document details Report for the Project Id "
              + projectId,
          e);
    }
  }

  @Override
  public byte[] retrievePermitCoverSheetReport(String userId, String contextId, Long projectId) {
    Map<String, Object> params = new HashMap<>();
    Integer associatedInd = 1;
    String formattedDecId = "NEW";
    try {
    	
    	Facility facility = facilityRepo.findByProjectId(projectId);
        if (facility == null) {
          throw new BadRequestException("NO_FACILITY_FOUND",
              "There is no facility found for this project " + projectId, projectId);
        }       
        
        if (StringUtils.hasLength(facility.getDecId())) {
        	formattedDecId = facility.getDecId().substring(0, 1) + '-'
        			+ facility.getDecId().substring(1, 5) + '-'
        			+ facility.getDecId().substring(5, 10);
        }
        
        Project project = projectRepo.findById(projectId).get();
        
      InputStream permitCoverSheetReportStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("etrack_permit_cover_sheet_report.jrxml");
//          getClass().getClassLoader().getResourceAsStream("etrack_permit_cover_sheet_report.jrxml");

      JasperReport permitCoverSheetJasperReport =
          JasperCompileManager.compileReport(permitCoverSheetReportStream);
      JRSaver.saveObject(permitCoverSheetJasperReport, "etrack_permit_cover_sheet_report.jasper");

      InputStream facilityReportStream = 
          Thread.currentThread().getContextClassLoader().getResourceAsStream("facility_details.jrxml");
//          getClass().getClassLoader().getResourceAsStream("facility_details.jrxml");
      JasperReport facilityJasperReport = JasperCompileManager.compileReport(facilityReportStream);
      JRSaver.saveObject(facilityJasperReport, "facility_details.jasper");

      InputStream applicantReportStream = 
          Thread.currentThread().getContextClassLoader().getResourceAsStream("applicant_details.jrxml");
//          getClass().getClassLoader().getResourceAsStream("applicant_details.jrxml");
      JasperReport applicantJasperReport =
          JasperCompileManager.compileReport(applicantReportStream);
      JRSaver.saveObject(applicantJasperReport, "applicant_details.jasper");

      InputStream contactReportStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("application_contacts_details.jrxml");
//          getClass().getClassLoader().getResourceAsStream("application_contacts_details.jrxml");
      JasperReport contactJasperReport = JasperCompileManager.compileReport(contactReportStream);
      JRSaver.saveObject(contactJasperReport, "application_contacts_details.jasper");
      
      List<String> permitAndTransTypesList =
              applicationRepo.findPermitTypesAndTransTypesByProjectId(projectId);
      List<String> newPermits = new ArrayList<>();
      List<String> modificationPermits = new ArrayList<>();
      List<String> renewalPermits = new ArrayList<>();
      List<String> contactPermits = new ArrayList<>();
      if (!CollectionUtils.isEmpty(permitAndTransTypesList)) {
        permitAndTransTypesList.forEach(permitAndTransType -> {
          String[] permitAndTransTypes = permitAndTransType.split(",");
          if (permitAndTransTypes[1].equals("NEW")) {
            newPermits.add(permitAndTransTypes[0]);
            contactPermits.add(permitAndTransTypes[0] + " (New)");
          } else if (permitAndTransTypes[1].startsWith("M")) {
              modificationPermits.add(permitAndTransTypes[0]);
              contactPermits.add(permitAndTransTypes[0] + " (Modification)");
            } else if (permitAndTransTypes[1].startsWith("R")) {
              renewalPermits.add(permitAndTransTypes[0]);
              contactPermits.add(permitAndTransTypes[0] + " (Renewal)");
            }
        });
      }  
      
      List<PendingApplication> applications = 
    		  pendingAppRepo.findApplicationDetailForTheProjectId(projectId);
      PendingApplication invoiceApplicationDetail = applications.get(0);
      
      List<ApplicantDto> applicants = applicantRepo.findAllPublicsByAssociatedInd(projectId, associatedInd);
      List<ApplicantDto> contacts = applicantRepo.findAllContactsByAssociatedInd(projectId, associatedInd);
      
      PermitCoverSheetDto permitCoverSheetDto = new PermitCoverSheetDto();           
      permitCoverSheetDto.setNewPermits(newPermits);
      permitCoverSheetDto.setModificationPermits(modificationPermits);
      permitCoverSheetDto.setRenewalPermits(renewalPermits);  
      permitCoverSheetDto.setContactPermits(contactPermits);
      permitCoverSheetDto.setDecId(formattedDecId);
      permitCoverSheetDto.setFacilityName(invoiceApplicationDetail.getFacilityName());
      permitCoverSheetDto.setProjectId(String.valueOf(projectId));
      permitCoverSheetDto.setProjectDescription(project.getProjectDesc());
      if(project.getProposedStartDate() != null) {
    	  permitCoverSheetDto.setProposedStartDate(mmDDYYYFormat.format(project.getProposedStartDate()));
      }
      if(project.getEstmtdCompletionDate() != null) {
    	  permitCoverSheetDto.setProposedEndDate(mmDDYYYFormat.format(project.getEstmtdCompletionDate()));
      }
      permitCoverSheetDto.setConstructionInd("0");
      if(project.getConstrnType() != null) {
    	  permitCoverSheetDto.setConstructionInd("1");
      }
      permitCoverSheetDto.setProposedUse(project.getProposedUseCode());
  
      StringBuilder cityStateZip = new StringBuilder();
      cityStateZip.append(invoiceApplicationDetail.getCity());
      if (StringUtils.hasLength(invoiceApplicationDetail.getState())) {
        cityStateZip.append(", ").append(invoiceApplicationDetail.getState());
      }
      if (StringUtils.hasLength(invoiceApplicationDetail.getZip())) {
        cityStateZip.append(", ").append(invoiceApplicationDetail.getZip());
      }
      
      FacilityReport facilityDto = new FacilityReport();
      facilityDto.setFacility(invoiceApplicationDetail.getFacilityName() + ", " + 
    		  formattedDecId);
      facilityDto.setCityStateZip(cityStateZip.toString());
      facilityDto.setFacilityAddress(invoiceApplicationDetail.getLocationDirections());
      facilityDto.setStreamWaterbodyName(project.getStrWaterbodyName());
      facilityDto.setTaxMapNumber("<TAX MAP IDs>");
      facilityDto.setDirections(invoiceApplicationDetail.getStreet2());
      
      List<FacilityReport> facilitiesList = new ArrayList<>();
      facilitiesList.add(facilityDto);
      permitCoverSheetDto.setFacilityReport(facilitiesList);

      List<ApplicantReport> applicantReports = new ArrayList<>();   
      if (!CollectionUtils.isEmpty(applicants)) {
          applicants.forEach(applicant -> {
            Map<String, Object> result = dartDbDao.getApplicantDetails
            		(userId, contextId, projectId, applicant.getPublicId());
            String phoneNumber;
            ApplicantReport applicantReport = new ApplicantReport();
            List<dec.ny.gov.etrack.dart.db.entity.PublicDetail> applicantList = 
            		(List<PublicDetail>) result.get("p_public_cur");
            StringBuilder applicantCityStateZip = new StringBuilder();
            applicantCityStateZip.append(applicantList.get(0).getCity());
            if (StringUtils.hasLength(applicantList.get(0).getState())) {
              applicantCityStateZip.append(", ").append(applicantList.get(0).getState());
            }
            if (StringUtils.hasLength(applicantList.get(0).getZip())) {
              applicantCityStateZip.append(", ").append(applicantList.get(0).getZip());
            }
            
            StringBuilder taxPayerId = new StringBuilder();
            if(StringUtils.hasLength(applicantList.get(0).getTaxpayerId())) {
            	taxPayerId.append(applicantList.get(0).getTaxpayerId());
            }
            if(StringUtils.hasLength(taxPayerId.toString()) && 
            		applicantList.get(0).getIncorpInd() != null) {
            	if(applicantList.get(0).getIncorpInd() == 1) {
            		taxPayerId.append("; verified");
            	}
            	else {
            		taxPayerId.append("; unverified");
            	}
            }

            applicantReport.setTaxpayerId(taxPayerId.toString());      
            applicantReport.setAddress(applicantList.get(0).getStreet1());
            applicantReport.setApplicantName(applicantList.get(0).getDisplayName());
            applicantReport.setCityStateZip(applicantCityStateZip.toString());
            applicantReport.setEmailAddress(applicantList.get(0).getEmailAddress());
            applicantReport.setTelephone(applicantList.get(0).getCellPhoneNumber());
                       
            phoneNumber = applicantList.get(0).getBusinessPhoneNumber();          
            if(!StringUtils.hasLength(phoneNumber)) {
            	phoneNumber = applicantList.get(0).getHomePhoneNumber();
            }
            if(!StringUtils.hasLength(phoneNumber)) {
            	phoneNumber = applicantList.get(0).getCellPhoneNumber();
            }
            if(!StringUtils.hasLength(phoneNumber)) {
            	applicantReport.setTelephone(phoneNumber);
            }
            else {
            	applicantReport.setTelephone("(" + phoneNumber.substring(0, 3)
      	      + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10));
            }
            applicantReport.setOperatorInd("0");
            applicantReport.setOwnerInd("0");
            applicantReport.setLesseeInd("0");
            applicantList.forEach(applicantRole -> {
            	if(applicantRole.getRoleTypeId().equals("6")) {
            		applicantReport.setOwnerInd("1");
            	}
            	if(applicantRole.getRoleTypeId().equals("1")) {
            		if("2".equals(applicantRole.getLegallyResponsibleTypeCode())) {
            			applicantReport.setOperatorInd("1");
            		}
            		else {
            			applicantReport.setLesseeInd("1");
            		}
            	}
            });
            applicantReports.add(applicantReport);
          });
        }  
      
      permitCoverSheetDto.setApplicantReport(applicantReports);

      List<ApplicationContactReport> applicationContactReports = new ArrayList<>();
      
      if(!CollectionUtils.isEmpty(contacts)) {
    	  contacts.forEach(contact -> {
    		  ApplicationContactReport applicationContactReport = new ApplicationContactReport();
    		  String phoneNumber;
    		  List<PublicDetail> contactDetail = (List<PublicDetail>) dartDbDao.getApplicantDetails
              		(userId, contextId, projectId, contact.getPublicId()).get("p_public_cur");
    		  StringBuilder contactCityStateZip = new StringBuilder();
              contactCityStateZip.append(contactDetail.get(0).getCity());
              if (StringUtils.hasLength(contactDetail.get(0).getState())) {
                contactCityStateZip.append(", ").append(contactDetail.get(0).getState());
              }
              if (StringUtils.hasLength(contactDetail.get(0).getZip())) {
                contactCityStateZip.append(", ").append(contactDetail.get(0).getZip());
              }
    		  applicationContactReport.setContactName(contact.getDisplayName());
    		  applicationContactReport.setContactAddress(contactDetail.get(0).getStreet1());
    	      applicationContactReport.setContactCityStateZip(contactCityStateZip.toString());
    	      applicationContactReport.setContactEmail(contactDetail.get(0).getEmailAddress());
    	      
    	      phoneNumber = contactDetail.get(0).getBusinessPhoneNumber();
    	      if(!StringUtils.hasLength(phoneNumber)) {
    	    	  phoneNumber = contactDetail.get(0).getHomePhoneNumber();
    	      }
    	      if(!StringUtils.hasLength(phoneNumber)) {
    	    	  phoneNumber = contactDetail.get(0).getCellPhoneNumber();
    	      }
    	      if(!StringUtils.hasLength(phoneNumber)) {
    	    	  applicationContactReport.setContactPhone(phoneNumber);
    	      }
    	      else {
    	    	  applicationContactReport.setContactPhone("(" + phoneNumber.substring(0, 3)
        	      + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10));
    	      }
    	   
    	      applicationContactReports.add(applicationContactReport);
    	  });
      }
      permitCoverSheetDto.setContactReport(applicationContactReports);

      List<PermitCoverSheetDto> permitCoversheetList = new ArrayList<>();
      permitCoversheetList.add(permitCoverSheetDto);
      
      JRBeanCollectionDataSource beanCollectionDataSource =
          new JRBeanCollectionDataSource(permitCoversheetList);
      params.put("facilityReport", facilityJasperReport);
      params.put("applicantReport", applicantJasperReport);
      params.put("contactReport", contactJasperReport);

      logger.info("Permit Cover sheet Report preparing fill report "
          + "for the Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);

      JasperPrint jasperPrint = JasperFillManager.fillReport(permitCoverSheetJasperReport, params,
          beanCollectionDataSource);
      logger.info("Permit Cover sheet Report preparing the export pdf "
          + "for the Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);

      byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);
      logger.info("Permit Cover sheet Report generated successfully "
          + "for the Project Id {}, User Id {}, Context Id {}", projectId, userId, contextId);
      return report;
    } catch (Exception e) {
    	logger.error("Error while generating invoice report ", e);
      throw new DartDBException("PERMIT_COVER_SHEET_GENERATE_ERR", "Error while generating the "
          + "Permit Cover sheet Report for the Project Id " + projectId, e);
    }
  }
}
