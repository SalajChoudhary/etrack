package dec.ny.gov.etrack.fmis.service.impl;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.fmis.entity.FMISInvoice;
import dec.ny.gov.etrack.fmis.entity.ProjectNote;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.exception.VPSException;
import dec.ny.gov.etrack.fmis.model.OAuthToken;
import dec.ny.gov.etrack.fmis.model.VPSResponse;
import dec.ny.gov.etrack.fmis.model.VPSTransactionRequest;
import dec.ny.gov.etrack.fmis.repo.FMISRepo;
import dec.ny.gov.etrack.fmis.repo.InvoiceRepo;
import dec.ny.gov.etrack.fmis.repo.ProjectNoteRepo;
import dec.ny.gov.etrack.fmis.service.FMISService;
import dec.ny.gov.etrack.fmis.service.PaymentService;
import dec.ny.gov.etrack.fmis.util.FMISConstants;

/**
 * This class to support all the payment related activities
 * 
 * @author mxmahali
 *
 */
@Service
public class PaymentServiceImpl implements PaymentService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentService.class.getName());

  @Autowired
  private InvoiceRepo invoiceRepo;

  @Autowired
  @Qualifier("vpsTxnIdRestTemplate")
  private RestTemplate vpsTxnIdRestTemplate;

  @Autowired
  @Qualifier("vpsPaymentRestTemplate")
  private RestTemplate vpsPaymentRestTemplate;

  @Value("${vps.etrack.partner.id}")
  private String partnerId;
  @Value("${vps.etrack.partner.username}")
  private String userName;
  @Value("${vps.etrack.partner.password}")
  private String password;
  @Value("${vps.etrack.partner.name}")
  private String partnerName;
  @Autowired
  private FMISRepo fmisRepo;

  @Value("${vps.payment.receipt.callback.url}")
  private String paymentReceiptCallbackURL;
  @Value("${vps.payment.success.redirect.url}")
  private String paymentSuccessRedirectURL;

  @Autowired
  private FMISService fmisService;
  @Autowired
  private ProjectNoteRepo projectNoteRepo;

  @Override
  public Object requestTransactionId(final String userId, final String contextId,
      final Long projectId, final String invoiceNumber, final OAuthToken oauthToken) {
    
    logger.info("Entering into Request transaction Id method. User Id {}. Context Id {}", userId,
        contextId);

    List<FMISInvoice> existingInvoice = invoiceRepo.findByFmisInvoiceNumAndProjectId(invoiceNumber, projectId);
    
    if (CollectionUtils.isEmpty(existingInvoice)) {
      throw new BadRequestException("INVALID_REQ",
          "Invoice number is not appropriate for this project id " + projectId);
    }
    FMISInvoice invoice = existingInvoice.get(0);

    Integer invoiceStatus = invoice.getInvoiceStatusCode();
    if (invoiceStatus.equals(FMISConstants.INVOICE_CANCELLED)
        || invoiceStatus.equals(FMISConstants.PAYMENT_RECEIVED)) {
      throw new BadRequestException("INVALID_REQ",
          "Invoice number is not valid for an appropriate action for this project id " + projectId);
    }
    String vpsTransactionId = getVPSTransactionId(userId, contextId, invoice, oauthToken);
    if (StringUtils.isEmpty(vpsTransactionId)) {
      throw new VPSException("There is an error while invoking Generate Transaction Id");
    }

    fmisRepo.updateTransactionNumber(userId, invoiceNumber, vpsTransactionId, new Date());
    // Object response = makePayment(userId, contextId, vpsTransactionId);
    logger.info("Existing from Request transaction Id method. Context Id {}", userId, contextId);
    return vpsTransactionId;
  }

  /**
   * 
   * @param userId
   * @param contextId
   * @param existingInvoice
   * @return
   */
  private String getVPSTransactionId(String userId, String contextId, FMISInvoice invoice, OAuthToken oauthToken) {
    logger.info("Entering into getVPSTransactionId. User Id {},  Context id {}", userId, contextId);
    // Request for Transaction Id
    VPSTransactionRequest txnIdRequest = new VPSTransactionRequest();
    txnIdRequest.setRequestId(invoice.getFmisInvoiceNum());
    txnIdRequest.setPartnerId(partnerId);
    txnIdRequest.setUserName(userName);
    txnIdRequest.setPassword(password);
    txnIdRequest.setPartnerName(partnerName);
    txnIdRequest.setRedirectURL(paymentSuccessRedirectURL + "/" + invoice.getProjectId()) ;
    txnIdRequest.setConfirmationPostURL(paymentReceiptCallbackURL);
    txnIdRequest.setEchobackCustomData("1");
    Long totalAmount = 0L;
    if (invoice.getInvoiceFeeTypeFee1() != null) {
      totalAmount += invoice.getInvoiceFeeTypeFee1();
    }
    if (invoice.getInvoiceFeeTypeFee2() != null) {
      totalAmount += invoice.getInvoiceFeeTypeFee2();
    }
    if (invoice.getInvoiceFeeTypeFee3() != null) {
      totalAmount += invoice.getInvoiceFeeTypeFee3();
    }
    txnIdRequest.setAmount(String.valueOf(totalAmount).concat("00"));
    
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, "application/json");
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+oauthToken.getAccessToken());
    HttpEntity<Object> entity = new HttpEntity<>(txnIdRequest, headers);
    ResponseEntity<JsonNode> response =
        vpsTxnIdRestTemplate.postForEntity("/GenerateTid", entity, JsonNode.class);
    logger.info("Exiting from getVPSTransactionId Context id User Id {},  Context id {}", userId, contextId);
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      return response.getBody().get("TransactionID").asText();
    }
    return null;
  }

//  /**
//   * 
//   * @param userId
//   * @param contextId
//   * @return
//   */
//  private Object makePayment(final String userId, final String contextId,
//      final String transactionId) {
//    String url = UriComponentsBuilder.fromUriString("/" + transactionId).build().toString();
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XHTML_XML_VALUE);
//    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
//    HttpEntity<Object> entity = new HttpEntity<>(null, headers);
//    return vpsPaymentRestTemplate.postForObject(url, entity, Object.class);
//  }

  @Override
  public void updateConfirmationNumber(final String contextId, final VPSResponse receipt) {
    logger.info("Entering into update payment receipt details. Context id {}", contextId);

    logger.debug("Confirmation received in the VPS receipt call {}", receipt);
    String invoice = receipt.getRequestID();
    String transactionId = receipt.getTransactionID();
    String confirmationNumber = receipt.getConfirmationNumber();
    if (!CollectionUtils.isEmpty(receipt.getTransactions())) {
      String paidAmount = receipt.getTransactions().get(0).getPaidAmount();;
      String feeCharged = receipt.getTransactions().get(0).getFeeCharged();
      String approvalStatus = receipt.getTransactions().get(0).getApprovalStatus();
      logger.info("Paid Amount {} , Fees Charged {}, "
          + "Approval Status {}, Context Id {}", paidAmount, feeCharged, approvalStatus, contextId);
      
      List<FMISInvoice> invoices =
          invoiceRepo.findByFmisInvoiceNumAndTransactionId(invoice, transactionId);
      
      if (CollectionUtils.isEmpty(invoices)) {
        logger.error("There is no record exists for this invoice "
            + "{} and transaction id {}, Context id {}", invoice, transactionId, contextId);
        // thrown an error as there is no data associated with the input parameter(s).
      } else if (FMISConstants.PAYMENT_RECEIVED.equals(invoices.get(0).getInvoiceStatusCode())) {
        logger.error("Transaction is already marked as completed for this invoice "
            + "{} and transaction id {}, Context id {}", invoice, transactionId, contextId);
      } else {
        Integer status = FMISConstants.PAYMENT_RECEIVED;
        try {
          fmisService.updateConfirmationNumber(transactionId + " is passed as User Id", contextId,
                  invoices.get(0).getProjectId(), invoice, confirmationNumber);
          
          logger.info("Payment Received note is getting created. Project Id {} ", invoices.get(0).getProjectId());
          ProjectNote paymentReceivedNote = new ProjectNote();
          paymentReceivedNote.setActionDate(new Date());
          paymentReceivedNote.setActionNote(FMISConstants.PAYMENT_RECEIVED_NOTE + invoices.get(0).getProjectId());
          paymentReceivedNote.setCreatedById("SYSTEM");
          paymentReceivedNote.setCreateDate(new Date());
          paymentReceivedNote.setActionTypeCode(FMISConstants.PAYMENT_RECEIVED_ACTION_TYPE);
          paymentReceivedNote.setProjectId(invoices.get(0).getProjectId());
          projectNoteRepo.save(paymentReceivedNote);
        } catch (BadRequestException e) {
          logger.info("FMIS got the confirmation update but some failure in updating "
              + "the status. Transaction id {}, Context Id {}", transactionId, contextId);
          if (e.getErrorMessage().contains(confirmationNumber)) {
            status = FMISConstants.PAYMENT_RECEIVED;
          }
        } catch (Exception e) {
          status = FMISConstants.PAYMENT_PENDING;
          logger.error("Error while updating the confirmation number with FMIS. Context Id: {}",
              contextId, e);
        }
        fmisRepo.updateConfirmationNumber(invoice, transactionId, confirmationNumber, new Date(),
            status, Long.valueOf(paidAmount.substring(0, paidAmount.length()-2)));
      }
    }
  }
}
