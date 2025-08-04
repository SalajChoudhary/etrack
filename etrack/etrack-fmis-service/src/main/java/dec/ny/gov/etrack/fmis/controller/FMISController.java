package dec.ny.gov.etrack.fmis.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.net.HttpHeaders;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.model.BillingInvoiceRequest;
import dec.ny.gov.etrack.fmis.model.InvoiceResponseBody;
import dec.ny.gov.etrack.fmis.service.FMISService;
import dec.ny.gov.etrack.fmis.util.Validator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/invoice")
public class FMISController {

  private static Logger logger = LoggerFactory.getLogger(FMISController.class.getName());

  @Autowired
  private FMISService fmisService;
  
  private final SimpleDateFormat mm_dd_yyyy_format = new SimpleDateFormat("MM/dd/yyyy");
  
  /**
   * Create new invoice by invoking FMIS system for the input request.
   *
   * @param userId - User initiates this request.
   * @param projectId - project id.
   * @param billingInvoiceRequest - Billing invoice Request.
   * @param jwtToken - JWT Token.
   * 
   * @return - Returns the invoice status along with details
   */
  @PostMapping
  @ApiOperation(value="This service will request FMIS System to generate new invoice "
      + "and store the invoice details in eTrack and share with consumer.")
  public Object createInvoice(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody BillingInvoiceRequest billingInvoiceRequest, 
      @RequestHeader(HttpHeaders.AUTHORIZATION) final String jwtToken) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into createInvoice method User Id {}, Context Id {} ", userId, contextId);

    if (!StringUtils.hasLength(userId) || projectId == null || projectId <= 0
        || !StringUtils.isEmpty(billingInvoiceRequest.getInvoiceNum())) {
      throw new BadRequestException("USER_PROJECT_ID_NOT_PASSED", 
          "Either User Id/Project Id is empty or blank. Or Invoice number is passed");
    }
    
    if (StringUtils.hasLength(billingInvoiceRequest.getCheckNumber())) {
      if (!StringUtils.hasLength(billingInvoiceRequest.getCheckRcvdDate()) 
          || billingInvoiceRequest.getCheckAmt() == null 
          || billingInvoiceRequest.getCheckAmt() <= 0 
          || billingInvoiceRequest.getTotalCharge() == null 
          || billingInvoiceRequest.getTotalCharge() < 0) {
        logger.error ("One of the mandatory parameter is missing in the request body. User Id {}, Context Id {} ",userId, contextId);
        throw new BadRequestException("MANDATORY_PARAM_MISSING", "One of the mandatory parameter is missing in the request body");
      }
    }
    
    if (StringUtils.hasLength(billingInvoiceRequest.getCheckRcvdDate())) {
     try {
       mm_dd_yyyy_format.setLenient(false);
       Date formattedDate = mm_dd_yyyy_format.parse(billingInvoiceRequest.getCheckRcvdDate());
       logger.debug("Formatted Date {}. User Id {}, Context Id {}", mm_dd_yyyy_format.format(formattedDate), userId, contextId);
     } catch (ParseException e) {
       logger.error("Invalid Check Received Date is passed {}, User Id {}, Context Id {}", 
           billingInvoiceRequest.getCheckRcvdDate(), userId, contextId);
       throw new BadRequestException("INVALID_RCVD_DATE", 
           "Check received Date is invalid " + billingInvoiceRequest.getCheckRcvdDate());
     }
    }
    Validator.isValid(userId, contextId, billingInvoiceRequest);
    return fmisService.createInvoice(userId, contextId, projectId, billingInvoiceRequest, jwtToken);
  }

  /**
   * Add the user notes to the existing invoice number.
   *
   * @param userId - User initiates this request
   * @param projectId - project id
   * @param billingInvoiceRequest - request for this invoice
   * 
   * @return - Amended invoice details.
   */
  @PutMapping("/{invoiceNum}")
  @ApiOperation(value="Update the existing invoice details. "
      + "This service is mainly use to update the notes in eTrack and doesn't call FMIS System to update any existing invoice details.")
  public Object updateInvoiceDetail(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @RequestBody BillingInvoiceRequest billingInvoiceRequest) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into updateInvoiceDetail . User Id {}, Context Id {}", userId, contextId);
    return fmisService.updateInvoice(userId, contextId, projectId, billingInvoiceRequest);
  }

  /**
   * Cancel the existing invoice requested by the user.
   * 
   * @param userId - User who initiates this request
   * @param projectId - Project id associated with this invoice number
   * @param cancelInvoiceRequest - Cancel Invoice request.
   * 
   * @return - Status of the request.
   */
  @PostMapping("/cancel")
  @ApiOperation(value="This service will request FMIS system to delete this invoice and update the status as CANCELLED in eTrack.")
  public ResponseEntity<InvoiceResponseBody> cancelInvoice(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId, 
      @RequestBody final BillingInvoiceRequest cancelInvoiceRequest) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into delete invoice method User ID {}, Context Id{} ", userId, contextId);
    if (cancelInvoiceRequest == null 
        || !StringUtils.hasLength(cancelInvoiceRequest.getInvoiceNum()) 
            || !StringUtils.hasLength(cancelInvoiceRequest.getReason())
            || !StringUtils.hasLength(cancelInvoiceRequest.getCancelledUserName()))
      throw new BadRequestException("INVOIE_NUM_OR_REASON_MISSING", "Invoice request, number "
          + "or cancellation reason or  user is blank");
    
    return fmisService.cancelInvoice(userId, contextId, projectId, cancelInvoiceRequest);
  }
}
