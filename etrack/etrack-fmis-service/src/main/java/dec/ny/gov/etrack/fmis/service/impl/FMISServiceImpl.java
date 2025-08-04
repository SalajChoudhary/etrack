package dec.ny.gov.etrack.fmis.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.management.timer.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.fmis.entity.Applicant;
import dec.ny.gov.etrack.fmis.entity.FMISInvoice;
import dec.ny.gov.etrack.fmis.entity.InvoiceFeeType;
import dec.ny.gov.etrack.fmis.entity.ProjectNote;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.exception.FMISException;
import dec.ny.gov.etrack.fmis.model.BillingInvoiceRequest;
import dec.ny.gov.etrack.fmis.model.ETRACKLoad;
import dec.ny.gov.etrack.fmis.model.ETRACKStatusLoad;
import dec.ny.gov.etrack.fmis.model.FMISHeader;
import dec.ny.gov.etrack.fmis.model.FMISRequest;
import dec.ny.gov.etrack.fmis.model.IngestionRequest;
import dec.ny.gov.etrack.fmis.model.Invoice;
import dec.ny.gov.etrack.fmis.model.InvoiceResponseBody;
import dec.ny.gov.etrack.fmis.model.InvoiceStatus;
import dec.ny.gov.etrack.fmis.model.ProjectType;
import dec.ny.gov.etrack.fmis.repo.FMISRepo;
import dec.ny.gov.etrack.fmis.repo.InvoiceFeeTypeRepo;
import dec.ny.gov.etrack.fmis.repo.InvoiceRepo;
import dec.ny.gov.etrack.fmis.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.fmis.service.FMISService;
import dec.ny.gov.etrack.fmis.util.FMISConstants;

@Service
public class FMISServiceImpl implements FMISService {

  private static final Logger logger = LoggerFactory.getLogger(FMISServiceImpl.class.getName());
  private final SimpleDateFormat mm_dd_yyyy_format = new SimpleDateFormat("MM/dd/yyyy");
  private final DateFormat yyyy_mm_dd_hh_mi_ss = new SimpleDateFormat("yyyyMMddHHmmss");
  
  @Value("${fmis.payload.uri}")
  private String fmisURL;
  @Value("${fmis.auth.info}")
  private String fmisBasicAuth;
  @Value("${etrack.program.identification}")
  private String programIdentification;
  @Value("${etrack.customer.type}")
  private String customerType;
  @Value("${fmis.xmlns.create.invoice}")
  private String fmisXMLNSInvoiceURI;
  @Value("${fmis.xmlns.get.invoice}")
  private String fmisXMLNSGetInvoiceURI;
  @Value("${fmis.xmlns.invoice.status}")
  private String fmisXMLNSGetInvoiceStatusURI;

  @Autowired
  private FMISHeader fmisHeader;

  @Autowired
  @Qualifier("fmisRestTemplate")
  private RestTemplate restTemplate;

  @Autowired
  private FMISRepo fmisRepo;

  @Autowired
  private InvoiceRepo invoiceRepo;

  @Autowired
  private TransformationService transformationService;

  @Autowired
  private InvoiceFeeTypeRepo invoiceFeeTypeRepo;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProjectNoteRepo projectNoteRepo;
  
  @Autowired
  @Qualifier("eTrackOtherServiceRestTemplate")
  private RestTemplate eTrackOtherServiceRestTemplate;
  
  private static final String INVOICE_REQUEST_URL_PATH = "/create_invoice_data/";
  private static final String INVOICE_NUM = "INVOICENUM";

  @Override
  public Object createInvoice(final String userId, final String contextId, final Long projectId,
      BillingInvoiceRequest invoice, final String token) {

    logger.info("Entering into create Invoice method User ID {}, Context Id {} ", userId,
        contextId);
    try {
      List<Applicant> fmisPrimaryLRPApplicant = fmisRepo.findApplicantDetails(projectId);
      logger.debug("Primary LRP applicant details {}", fmisPrimaryLRPApplicant);

      if (CollectionUtils.isEmpty(fmisPrimaryLRPApplicant)) {
        throw new BadRequestException("LRP_NOT_AVAIL", "Primary LRP is not available for this project "+ projectId);
      }
      String decId = fmisRepo.findDecIdByProjectId(projectId);
      if (!StringUtils.hasLength(decId)) {
        throw new BadRequestException("DEC_ID_REQD","DEC ID cannot be empty to create invoice. Please reach out Technical Support team.");
      }
      FMISRequest fmisInvoiceRequest = prepareInvoiceRequest(userId, contextId, projectId, invoice,
          fmisPrimaryLRPApplicant.get(0), FMISConstants.NEW_INVOICE, null, decId);

      logger.debug("Generate Invoice FMIS Request {}", new ObjectMapper()
          .writeValueAsString(fmisInvoiceRequest.getETrackLoad().getInputParameters()));
      ResponseEntity<InvoiceResponseBody> response =
          invoiceFMIS(fmisInvoiceRequest, INVOICE_REQUEST_URL_PATH);
      logger.debug("Generate Invoice FMIS Response {}",
          new ObjectMapper().writeValueAsString(response));
      InvoiceResponseBody output = response.getBody();
      invoice.setInvoiceNum(output.getOutputParameters().get(INVOICE_NUM));
      invoice.setTotalCharge(
          fmisInvoiceRequest.getETrackLoad().getInputParameters().getTotalCharges());

      invoiceRepo.save(transformationService.transformInvoiceRequestToDto(userId, contextId,
          projectId, fmisPrimaryLRPApplicant.get(0).getPublicId(), invoice,
          FMISConstants.NEW_INVOICE, invoice.getInvoiceNum(), null, decId));

      HttpHeaders headers = new HttpHeaders();
      headers.add("userId", userId);
      headers.add("projectId", projectId.toString());
      headers.add(HttpHeaders.AUTHORIZATION, token);
      HttpEntity<List<String>> requestEntity = new HttpEntity<>(headers);
      String uri = UriComponentsBuilder.newInstance()
          .pathSegment("/etrack-dart-db/report/invoice-report/" + invoice.getInvoiceNum()).build()
          .toString();
      ParameterizedTypeReference<byte[]> invoiceReport =
          new ParameterizedTypeReference<byte[]>() {};

      ResponseEntity<byte[]> invoiceReportResponseEntity = eTrackOtherServiceRestTemplate
          .exchange(uri, HttpMethod.GET, requestEntity, invoiceReport);
      uploadInvoiceReportDocumentToDMS(userId, contextId, projectId, 
          invoiceReportResponseEntity.getBody(), invoice.getInvoiceNum(), token);
      return new ResponseEntity<BillingInvoiceRequest>(invoice, HttpStatus.OK);
    } catch (HttpClientErrorException|HttpServerErrorException e) {
      logger.error("Error from the FMIS Server. User Id {}, Context Id {}", e);
      throw new FMISException("INVOICE_REPORT_UPLOAD_ERR", 
          e.getResponseBodyAsString(), e.getStatusCode());
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new FMISException("INVOICE_GEN_ERR", "Error while trying to create/update invoice", e);
    }
  }

  private void uploadInvoiceReportDocumentToDMS(final String userId, final String contextId, final Long projectId,
       final byte[] content, final String invoiceNumber, final String token) {

    IngestionRequest ingestionRequest = new IngestionRequest();
    ingestionRequest.setAttachmentFilesCount(1);
    Map<String, String> filesDate = new HashMap<>();
    String currentDate = yyyy_mm_dd_hh_mi_ss.format(new Date());
    filesDate.put("0", currentDate);
    ingestionRequest.setFileDates(filesDate);
    Map<String, Object> metadataProperties = new HashMap<>();
    metadataProperties.put("Description", "Project invoice report for the invoice number " + invoiceNumber);
    metadataProperties.put("docCategory", "30");
    metadataProperties.put("docSubCategory", "97");
    metadataProperties.put("docCreationType", "TEXT");
    String documentName = "Project_" + projectId + "_Invoice_" +invoiceNumber; 
    metadataProperties.put("DocumentTitle", documentName);
    metadataProperties.put("historic", "0");
    metadataProperties.put("docCreator", userId);
    metadataProperties.put("indexDate", currentDate);
    metadataProperties.put("docLastModifier", userId);
    metadataProperties.put("source", "ETRACK");
    metadataProperties.put("projectID", projectId);
    metadataProperties.put("applicationID", invoiceNumber);
    metadataProperties.put("foilStatus", "NODET");
    metadataProperties.put("deleteFlag", "F");
    metadataProperties.put("renewalNumber", "0");
    metadataProperties.put("modificationNumber", "0");
    metadataProperties.put("trackedAppId", "");
    metadataProperties.put("access", "0");
    metadataProperties.put("nonRelReasonCodes", "");
    metadataProperties.put("receivedDate", currentDate);
    metadataProperties.put("permitType", "");
    ingestionRequest.setMetadataProperties(metadataProperties);

    ByteArrayResource byteArrayResource = new ByteArrayResource(content) {
      @Override
      public String getFilename() {
        return documentName + ".pdf";
      }
    };
    MultiValueMap<String, Object> filesAndMetadataMap = new LinkedMultiValueMap<String, Object>();
    try {
      HttpHeaders attachFile = new HttpHeaders();
      attachFile.setContentType(MediaType.TEXT_PLAIN);
      HttpEntity<ByteArrayResource> attachment = new HttpEntity<>(byteArrayResource, attachFile);
      filesAndMetadataMap.add("uploadFiles", attachment);
      HttpHeaders fileMetadataHeaders = new HttpHeaders();
      fileMetadataHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IngestionRequest> metaDataEntity =
          new HttpEntity<>(ingestionRequest, fileMetadataHeaders);
      filesAndMetadataMap.add("ingestionMetaData", metaDataEntity);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
      httpHeaders.add("userId", userId);
      StringBuilder clientId = new StringBuilder();
      clientId.append("DEC_").append("SUPPORTINGDOCUMENTS").append("_P8");
      httpHeaders.add("projectId", String.valueOf(projectId));
      httpHeaders.add("clientId", clientId.toString());
      httpHeaders.add("contextId", contextId);
      httpHeaders.add("docClassification", String.valueOf(2));;
      httpHeaders.add(HttpHeaders.AUTHORIZATION, token);

      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(filesAndMetadataMap, httpHeaders);
      String uri = UriComponentsBuilder.newInstance().path("/etrack-dcs/support-document/upload")
          .build().toUriString();
      logger.info("Making call to DMS to upload invoice id: {} report. User id: {} context Id: {}",
          invoiceNumber, userId, contextId);
      eTrackOtherServiceRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);
      logger.info("Upload the email Correspondence successful. User id: {} context id: {}", userId,
          contextId);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      logger.error("Error while requesting etrack-dcs service to upload the invoice report. "
          + "User Id {}, Context Id {}. Error details ", userId, contextId, e);
      throw new FMISException("INVOICE_REPORT_DOC_UPLOAD_ERROR", e.getResponseBodyAsString(), e.getStatusCode());
    } catch (Exception e) {
      throw new FMISException("INVOICE_REPORT_DOC_UPLOAD_ERROR",
          "Error while preparing the invoice report and upload into DMS for the invoice number "
              + invoiceNumber, e);
    }
  }
  
  @Override
  public ResponseEntity<InvoiceResponseBody> cancelInvoice(
      final String userId, final String contextId, final Long projectId,
      final BillingInvoiceRequest cancelInvoiceRequest) {
    
    logger.info("Entering into Cancel Invoice method User Id {}, Context Id {} ", userId,
        contextId);
    try {
      String decId = fmisRepo.findDecIdByProjectId(projectId);
      String invoiceNum = cancelInvoiceRequest.getInvoiceNum();
      List<Applicant> fmisPrimaryLRPApplicant = fmisRepo.findApplicantDetails(projectId);
      logger.debug("Primary LRP applicant details {}", fmisPrimaryLRPApplicant);

      if (CollectionUtils.isEmpty(fmisPrimaryLRPApplicant)) {
        throw new BadRequestException("LRP_NOT_AVAIL", "Primary LRP is not available for this project "+ projectId);
      }

      List<FMISInvoice> existingInvoices =
          invoiceRepo.findActiveByFmisInvoiceNumAndProjectId(invoiceNum, projectId);
      if (CollectionUtils.isEmpty(existingInvoices)
          || !projectId.equals(existingInvoices.get(0).getProjectId())) {
        throw new BadRequestException("INVOICE_NOT_MATCH",
            "Invoice number is not appropriate for this project id " + projectId);
      }

      FMISInvoice invoice = existingInvoices.get(0);
      if (FMISConstants.INVOICE_CANCELLED.equals(invoice.getInvoiceStatusCode())
          || FMISConstants.PAYMENT_RECEIVED.equals(invoice.getInvoiceStatusCode())) {

        throw new BadRequestException("INVOICE_CANCEL_INVALID",
            "Invoice is already cancelled or Payment is received. So, the invoice cancellation request cannot be processed : "
                + invoiceNum);
      }
      
      FMISRequest fmisInvoiceRequest = prepareInvoiceRequest(userId, contextId, projectId, null,
          fmisPrimaryLRPApplicant.get(0), FMISConstants.CANCEL_INVOICE, invoice, decId);

      logger.debug("FMIS Request {}", new ObjectMapper()
          .writeValueAsString(fmisInvoiceRequest.getETrackLoad().getInputParameters()));
      
      ResponseEntity<InvoiceResponseBody> response =
          invoiceFMIS(fmisInvoiceRequest, INVOICE_REQUEST_URL_PATH);

      InvoiceResponseBody output = response.getBody();
      invoiceRepo.save(transformationService.transformInvoiceRequestToDto(userId, contextId,
          projectId, fmisPrimaryLRPApplicant.get(0).getPublicId(), cancelInvoiceRequest,
          FMISConstants.CANCEL_INVOICE, invoiceNum, invoice, decId));

      logger.debug("Creating the cancellation action note for the invoice {}, "
          + "User Id {}, Context Id {}", invoiceNum, userId, contextId);
      ProjectNote cancellationNote = new ProjectNote();
      cancellationNote.setProjectId(projectId);
      StringBuilder invoiceActionNoteAndCancelReason = new StringBuilder();
      invoiceActionNoteAndCancelReason.append("Invoice ").append(invoiceNum)
        .append(" for Project ID: ").append(projectId)
        .append(" has been cancelled|").append(cancelInvoiceRequest.getReason());
      cancellationNote.setActionNote(invoiceActionNoteAndCancelReason.toString());
      cancellationNote.setComments(cancelInvoiceRequest.getNotes());
      cancellationNote.setCreateDate(new Date());
      cancellationNote.setActionDate(new Date());
      cancellationNote.setCreatedById("SYSTEM");
      cancellationNote.setActionTypeCode(FMISConstants.INVOICE_CANCELLATION_ACTION_TYPE);
      cancellationNote.setCancelUserId(userId);
      cancellationNote.setCancelUserNm(cancelInvoiceRequest.getCancelledUserName());
      projectNoteRepo.save(cancellationNote);
      return new ResponseEntity<InvoiceResponseBody>(output, HttpStatus.OK);
    } catch (BadRequestException bre) {
      throw bre;
    } catch (Exception e) {
      throw new FMISException("INVOICE_CANCEL_ERR", 
          "Error while trying to cancel invoice for the invoice " + cancelInvoiceRequest.getInvoiceNum(), e);
    }
  }

  /**
   * Call the FMIS system to perform appropriate actions.
   * 
   * @param fmisRequest - FMIS request payload.
   * @param pathVariable - FMIS URI to invoke.
   * 
   * @return - Response received from FMIS system.
   */
  private ResponseEntity<InvoiceResponseBody> invoiceFMIS(final Object fmisRequest,
      final String pathVariable) {
    String url = UriComponentsBuilder.fromUriString(pathVariable).build().toString();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(fmisBasicAuth);
    headers.add(HttpHeaders.ACCEPT, "application/json");
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    HttpEntity<Object> entity = new HttpEntity<>(fmisRequest, headers);
    ResponseEntity<InvoiceResponseBody> response =
        restTemplate.postForEntity(url, entity, InvoiceResponseBody.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      InvoiceResponseBody output = response.getBody();
      logger.debug("Response message from FMIS {}", output);
      if (!CollectionUtils.isEmpty(output.getOutputParameters())) {
        String responseMessage = output.getOutputParameters().get("RESPONSEMSG");
        if (StringUtils.hasLength(responseMessage)
            && !"Success".equalsIgnoreCase(responseMessage)) {
          throw new BadRequestException("FMIS_ERR_DETAIL", responseMessage);
        }
      }
    }
    return response;
  }
 
  private String paddingWithZeros(String input, int length) {
    StringBuilder sb = new StringBuilder();
    int noOfPaddingZeros = length - input.length();
    for (int i = 0; i < noOfPaddingZeros; i++) {
      sb.append("0");
    }
    return sb.append(input).toString();
  }

  /**
   * Prepare the invoice request payload which can be accepted by FMIS system.
   * 
   * @param userId - User initiates the request
   * @param contextId - UUID to track the transaction
   * @param projectId - Project Id.
   * @param billingInvoice - Billing Invoice details.
   * @param primaryApplicant - Primary LRP for the project id.
   * @param transactiontype - Transaction Type . New or Delete.
   * @param fmisInvoice - FMIS invoice details.
   * @param decId - DEC Id.
   * 
   * @return - Transformed the FMIS request.
   */
  private FMISRequest prepareInvoiceRequest(final String userId, final String contextId,
      final Long projectId, BillingInvoiceRequest billingInvoice, final Applicant primaryApplicant,
      final String transactiontype, FMISInvoice fmisInvoice, final String decId) {
    logger.info("Entering into prepare invoice request User id {} , Context Id {}", userId,
        contextId);
    ETRACKLoad eTrackLoad = new ETRACKLoad();
    eTrackLoad.setXmlnsURL(fmisXMLNSInvoiceURI);
    eTrackLoad.setFmisHeader(fmisHeader);
    Invoice fmisPayload = new Invoice();
    fmisPayload.setProgramIdentification(programIdentification);
    fmisPayload.setCustomerUniqueIdentifier(
        paddingWithZeros(String.valueOf(primaryApplicant.getPublicId()), 10));
    fmisPayload.setCustomerType(customerType);
    fmisPayload.setTransactionType(transactiontype);
    fmisPayload.setCustomerName(primaryApplicant.getDisplayName());
    fmisPayload.setCustomerAddressline1(primaryApplicant.getStreet1());
    fmisPayload.setCustomerAddressline2(primaryApplicant.getStreet2());
    fmisPayload.setCity(primaryApplicant.getCity());
    fmisPayload.setState(primaryApplicant.getState());
    fmisPayload.setZip(primaryApplicant.getZip());
    fmisPayload.setCountryCode(
        primaryApplicant.getCountry() != null ? primaryApplicant.getCountry().substring(0, 2) : "");
    fmisPayload.setBillingSiteUniqueId(paddingWithZeros(String.valueOf(projectId), 10));
    fmisPayload.setBillingAddressline1(primaryApplicant.getStreet1());
    fmisPayload.setBillingAddressline2(primaryApplicant.getStreet2());
    fmisPayload.setBillingCity(primaryApplicant.getCity());
    fmisPayload.setBillingState(primaryApplicant.getState());
    fmisPayload.setBillingZip(primaryApplicant.getZip());
    fmisPayload.setPhoneNumber(getPhoneNumber(primaryApplicant.getCellPhoneNumber(),
        primaryApplicant.getHomePhoneNumber(), primaryApplicant.getBusinessPhoneNumber()));
    fmisPayload.setBillingCountrycode(
        primaryApplicant.getCountry() != null ? primaryApplicant.getCountry().substring(0, 2) : "");
    fmisPayload.setBillingFirstname(primaryApplicant.getFirstName());
    fmisPayload.setBillingLastname(primaryApplicant.getLastName());
    fmisPayload.setBillingEmail(primaryApplicant.getEmail());
    if (FMISConstants.NEW_INVOICE.equals(transactiontype)) {
      fmisPayload.setDecId(decId);
      if (StringUtils.hasLength(billingInvoice.getCheckNumber())) {
        fmisPayload.setCheckReceived("Y");
        fmisPayload.setCheckNumber(billingInvoice.getCheckNumber());
        fmisPayload.setCheckAmount(String.valueOf(billingInvoice.getCheckAmt()));
        fmisPayload.setCheckReceivedDate(billingInvoice.getCheckRcvdDate());
      } else {
        fmisPayload.setCheckReceived("N");
      }      
      prepareRevAcctForNewInvoice(contextId, billingInvoice, fmisPayload);
    } else if (FMISConstants.CANCEL_INVOICE.equals(transactiontype)) {
      if (!StringUtils.hasLength(decId)) {
        throw new BadRequestException("DEC_ID_NOT_FOUND", "Invoice generated for this Project doesn't seems have DEC ID");
      }
      fmisPayload.setDecId(decId);
      prepareRevAcctForCancelInvoice(fmisInvoice, fmisPayload);
    } else {
      throw new BadRequestException("INVALID_TXN_REQ", "Invalid Transaction type is passed for the project id "+ projectId);
    }
    eTrackLoad.setInputParameters(fmisPayload);
    FMISRequest fmisRequest = new FMISRequest();
    fmisRequest.setETrackLoad(eTrackLoad);
    logger.info("Exiting from prepare invoice request User id {} , Context Id {}", userId,
        contextId);
    return fmisRequest;
  }

  private void prepareRevAcctForNewInvoice(final String contextId,
      BillingInvoiceRequest billingInvoice, Invoice fmisPayload) {
    Long totalCharges = 0L;
    List<ProjectType> revenueAccts = billingInvoice.getTypes();
    if (revenueAccts.size() < 4) {
      int index = 1;
      for (ProjectType projectType : revenueAccts) {
        Long revAmount = projectType.getFee();
        if (StringUtils.isEmpty(projectType.getType()) || revAmount == null || revAmount < 0) {
          logger.error("Revenue Account is empty {} or amount {} is not valid Context Id {}",
              projectType.getType(), revAmount, contextId);
          throw new BadRequestException("REVENUE_ACCT_EMPTY", "Revenue Account/Amount is blank or empty");
        }
        Optional<InvoiceFeeType> feeTypeAvailability =
            invoiceFeeTypeRepo.findById(projectType.getType());

        if (!feeTypeAvailability.isPresent()) {
          logger.error("Revenue Account {} is not valid Context Id {}", projectType.getType(),
              contextId);
          throw new BadRequestException("FEE_TYPE_EMPTY", 
              "There is no invoice fee type in the E_INVOICE_FEE_TYPE "+ projectType.getType());
        }
        
        InvoiceFeeType feeType = feeTypeAvailability.get();
        if (!revAmount.equals(feeType.getInvoiceFee())) {
          logger.error("Fee is not matching with configured fee Revenue Account {}. Context Id {}",
              projectType.getType(), contextId);
          throw new BadRequestException("FEE_EMPTY", "There is no fee for the input Revenue Account Type " + projectType.getType());
        }
        revAmount = feeType.getInvoiceFee();
        String revAccount = feeType.getInvoiceFeeType();
        totalCharges += revAmount;
        switch (index) {
          case 1:
            fmisPayload.setRevAcct1(revAccount);
            fmisPayload.setRevAcct1Amt(revAmount);
            break;
          case 2:
            fmisPayload.setRevAcct2(revAccount);
            fmisPayload.setRevAcct2Amt(revAmount);
            break;
          case 3:
            fmisPayload.setRevAcct3(revAccount);
            fmisPayload.setRevAcct3Amt(revAmount);
            break;
        }
        ++index;
      }
    } else {
      throw new BadRequestException("REV_ACCT_EXCEEDED", "Revenue accounts are more than 4 which is invalid");
    }
    fmisPayload.setTotalCharges(totalCharges);
  }

  private void prepareRevAcctForCancelInvoice(FMISInvoice fmisInvoice, Invoice fmisPayload) {
    Long totalCharges = 0L;
    fmisPayload.setRevAcct1(fmisInvoice.getInvoiceFeeType1());
    fmisPayload.setRevAcct1Amt(fmisInvoice.getInvoiceFeeTypeFee1());
    if (fmisInvoice.getInvoiceFeeTypeFee1() != null && fmisInvoice.getInvoiceFeeTypeFee1() > 0)
      totalCharges += fmisInvoice.getInvoiceFeeTypeFee1();
    fmisPayload.setRevAcct1(fmisInvoice.getInvoiceFeeType2());
    fmisPayload.setRevAcct1Amt(fmisInvoice.getInvoiceFeeTypeFee2());
    if (fmisInvoice.getInvoiceFeeTypeFee2() != null && fmisInvoice.getInvoiceFeeTypeFee2() > 0)
      totalCharges += fmisInvoice.getInvoiceFeeTypeFee2();
    fmisPayload.setRevAcct1(fmisInvoice.getInvoiceFeeType3());
    fmisPayload.setRevAcct1Amt(fmisInvoice.getInvoiceFeeTypeFee3());
    if (fmisInvoice.getInvoiceFeeTypeFee3() != null && fmisInvoice.getInvoiceFeeTypeFee3() > 0)
      totalCharges += fmisInvoice.getInvoiceFeeTypeFee3();
    fmisPayload.setTotalCharges(totalCharges);
    fmisPayload.setOriginalFmisInvnum(fmisInvoice.getFmisInvoiceNum());
    if (StringUtils.hasLength(fmisInvoice.getCheckNumber())) {
      fmisPayload.setCheckReceived("Y");
      fmisPayload.setCheckNumber(fmisInvoice.getCheckNumber());
      fmisPayload.setCheckAmount(String.valueOf(fmisInvoice.getCheckAmt()));
      fmisPayload.setCheckReceivedDate(
          mm_dd_yyyy_format.format(fmisInvoice.getCheckRcvdDate()));
    } else {
      fmisPayload.setCheckReceived("N");
    }
  }

  private String getPhoneNumber(String... phoneNumbers) {
    for (String phoneNumber : phoneNumbers) {
      if (!StringUtils.isEmpty(phoneNumber)) {
        return phoneNumber;
      }
    }
    return null;
  }

  @Override
  public ResponseEntity<InvoiceResponseBody> updateConfirmationNumber(final String transactionId,
      final String contextId, final Long projectId, final String invoiceNumber,
      final String confirmationNumber) {

    logger.info("Entering into update confirmation number method Transaction ID {}, Context Id {} ",
        transactionId, contextId);
    List<FMISInvoice> invoices =
        invoiceRepo.findActiveByFmisInvoiceNumAndProjectId(invoiceNumber, projectId);
    if (CollectionUtils.isEmpty(invoices)) {
      throw new BadRequestException("INVALID_INVOICE_PASSED",
          "Invoice number is not appropriate for this project id " + projectId);
    }
    ETRACKLoad eTrackLoad = new ETRACKLoad();
    eTrackLoad.setXmlnsURL(fmisXMLNSGetInvoiceURI);
    eTrackLoad.setFmisHeader(fmisHeader);
    FMISRequest fmisRequest = new FMISRequest();
    Invoice inputParameters = new Invoice();
    inputParameters.setInvoiceNum(invoiceNumber);
    inputParameters.setConfNum(confirmationNumber);
    eTrackLoad.setInputParameters(inputParameters);
    fmisRequest.setETrackLoad(eTrackLoad);
    try {
      logger.debug("Update VPS Receipt confirmation request body {}",
          new ObjectMapper().writeValueAsString(fmisRequest));
      
      ResponseEntity<InvoiceResponseBody> response = invoiceFMIS(fmisRequest, "/get_conf_num/");
      logger.info("Exiting from get Confirmation details invoice request Transaction id {} , Context Id {}",
          transactionId, contextId);
      // return response.getBody().getOutputParameters().get("RESPONSEMSG");
      return response;
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new FMISException("UPDATE_CONF_ERR", "Error while updating the confirmation number", e);
    }
  }

  /**
   * 
   */
  @Scheduled(fixedDelay = Timer.ONE_HOUR)
  @Override
  public void getInvoiceStatus() {
    logger.info("Entering into getInvoiceStatus");
    ETRACKStatusLoad eTrackStatusLoad = new ETRACKStatusLoad();
    eTrackStatusLoad.setXmlnsURL(fmisXMLNSGetInvoiceStatusURI);
    eTrackStatusLoad.setFmisHeader(fmisHeader);
    Map<String, Object> fmisRequest = new HashMap<>();
    Map<String, String> inputParameter = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
    try {
      Calendar startDate = Calendar.getInstance();
      startDate.add(Calendar.DATE, -30);
      Calendar now = Calendar.getInstance();
      String fromDate = sdf.format(startDate.getTime());
      String toDate = sdf.format(now.getTime());
      logger.info("Getting the invoice status from {} to {}", fromDate, toDate);
      inputParameter.put(FMISConstants.DATE_FROM.toUpperCase(), fromDate);
      inputParameter.put(FMISConstants.DATE_TO.toUpperCase(), toDate);
      eTrackStatusLoad.setInputParameters(inputParameter);
      fmisRequest.put("ETRACKLoad", eTrackStatusLoad);
      logger.debug("Get Invoice Request {}", objectMapper.writeValueAsString(fmisRequest));
      ResponseEntity<InvoiceResponseBody> response =
          invoiceFMIS(fmisRequest, "/get_invoice_status/");
      String responseObject = response.getBody().getOutputParameters().get("OUTPUT");
      try {
        TypeReference<LinkedHashMap<String, List<InvoiceStatus>>> typeRef =
            new TypeReference<LinkedHashMap<String, List<InvoiceStatus>>>() {};
        Map<String, List<InvoiceStatus>> invoiceStatusResponse =
            objectMapper.readValue(responseObject, typeRef);
        if (!CollectionUtils.isEmpty(invoiceStatusResponse)) {
          invoiceStatusResponse.keySet().forEach(key -> {
            List<InvoiceStatus> invoiceResponses = invoiceStatusResponse.get(key);
            if (!CollectionUtils.isEmpty(invoiceResponses)) {
              invoiceResponses.forEach(invoice -> {
                List<FMISInvoice> fmisInvoice = invoiceRepo.findByFmisInvoiceNum(invoice.getInvoiceNumber());
                if (!CollectionUtils.isEmpty(fmisInvoice)) {
                  if (StringUtils.hasLength(invoice.getInvoiceStatus())) {
                      if (invoice.getInvoiceStatus().equals("PAID")) {
                        int noOfRecordsUpdated = fmisRepo.updateFMISReceiptForPaidInvoice(invoice.getInvoiceNumber(),
                            invoice.getReceiptNumber(), Integer.parseInt(invoice.getPaidAmount()), 2);
                        logger.info("Payment Received note is getting created. Project Id {} Records updated {}",
                            fmisInvoice.get(0).getProjectId(), noOfRecordsUpdated);                        
                        if (noOfRecordsUpdated > 0) {
                          ProjectNote paymentReceivedNote = new ProjectNote();
                          paymentReceivedNote.setActionDate(new Date());
                          paymentReceivedNote.setActionNote(
                              FMISConstants.PAYMENT_RECEIVED_NOTE + fmisInvoice.get(0).getProjectId());
                          paymentReceivedNote.setCreatedById("SYSTEM");
                          paymentReceivedNote.setCreateDate(new Date());
                          paymentReceivedNote
                              .setActionTypeCode(FMISConstants.PAYMENT_RECEIVED_ACTION_TYPE);
                          paymentReceivedNote.setProjectId(fmisInvoice.get(0).getProjectId());
                          projectNoteRepo.save(paymentReceivedNote);                        
                      }
                    } else if (invoice.getInvoiceStatus().equals("PARTIAL")){
                      fmisRepo.updateFMISReceiptForPaidInvoice(invoice.getInvoiceNumber(),
                          invoice.getReceiptNumber(), Integer.parseInt(invoice.getPaidAmount()), 1);
                    }
                  }
                }
              });
            }
          });
        }
        logger.debug("Invoice Status List Response {}",
            objectMapper.writeValueAsString(invoiceStatusResponse));
      } catch (JsonMappingException jse) {
        TypeReference<LinkedHashMap<String, LinkedHashMap<String, InvoiceStatus>>> typeRef =
            new TypeReference<LinkedHashMap<String, LinkedHashMap<String, InvoiceStatus>>>() {};
        LinkedHashMap<String, LinkedHashMap<String, InvoiceStatus>> invoiceStatusResponse =
            objectMapper.readValue(responseObject, typeRef);
        if (!CollectionUtils.isEmpty(invoiceStatusResponse)) {
          invoiceStatusResponse.keySet().forEach(key -> {
            LinkedHashMap<String, InvoiceStatus> invoiceStatusMap = invoiceStatusResponse.get(key);
            if (!CollectionUtils.isEmpty(invoiceStatusMap)) {
              invoiceStatusMap.keySet().forEach(invoiceKey -> {
                InvoiceStatus invoiceStatus = invoiceStatusMap.get(invoiceKey);
                if (StringUtils.hasLength(invoiceStatus.getInvoiceStatus())
                    && invoiceStatus.getInvoiceStatus().equals("PAID")) {
                  fmisRepo.updateFMISReceiptForPaidInvoice(invoiceStatus.getInvoiceNumber(),
                      invoiceStatus.getReceiptNumber(),
                      Integer.parseInt(invoiceStatus.getPaidAmount()),2);
                }
              });
            }
          });
        }
        logger.debug("Invoice Status Response {}",
            objectMapper.writeValueAsString(invoiceStatusResponse));
      }
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new FMISException("INVOICE_STATUS_ERR", "Error while retrieving invoice status from FMIS", e);
    }
  }

  @Override
  public Object updateInvoice(String userId, String contextId, Long projectId,
      BillingInvoiceRequest billingInvoiceRequest) {
    
    logger.info("Entering into update the Invoice details. User Id {}, Context Id {}", userId, contextId);
    
    if (StringUtils.hasLength(billingInvoiceRequest.getInvoiceNum())) {
      List<FMISInvoice> fmisInvoiceList = invoiceRepo.findByFmisInvoiceNumAndProjectId(
          billingInvoiceRequest.getInvoiceNum(), projectId);
      if (CollectionUtils.isEmpty(fmisInvoiceList)) {
        throw new BadRequestException("NO_INVOICE_EXIST", "There is no existing invoice with invoice number:  "
            + billingInvoiceRequest.getInvoiceNum() + " for the project Id " + projectId + " to update the notes");
      }
      fmisInvoiceList.get(0).setNotes(billingInvoiceRequest.getNotes());
      fmisInvoiceList.get(0).setModifiedById(userId);
      fmisInvoiceList.get(0).setModifiedDate(new Date());
      invoiceRepo.save(fmisInvoiceList.get(0));
    } else {
      throw new BadRequestException("INVOICE_NUM_MISSING", "Invoice number or Notes Missing");
    }
    logger.info("Existing from update the Invoice details. User Id {}, Context Id {}", userId, contextId);
    return billingInvoiceRequest;
  }
}
