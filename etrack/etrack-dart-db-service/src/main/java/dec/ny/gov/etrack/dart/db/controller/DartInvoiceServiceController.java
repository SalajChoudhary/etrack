package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.service.InvoiceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class DartInvoiceServiceController {

  @Autowired
  private InvoiceService invoiceService;
  
  private static final Logger logger = LoggerFactory.getLogger(DartInvoiceServiceController.class.getName());
  
  /**
   * This end point will return the invoice details for the input invoice.
   * 
   * @param userId - User who initiates this required.
   * @param projectId - Project Id.
   * @param fmisInvoice - Invoice number shared by FMIS system earlier.
   * 
   * @return - Invoice details.
   */
  @GetMapping("/invoice/{fmisInvoice}")
  @ApiOperation(value = "Retrieves the invoice details for the input invoice number.")
  public Object retrieveInvoiceDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "98234", value = "Invoice number") String fmisInvoice) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveInvoiceDetails. User Id {}, Context Id {}", userId,
        contextId);
    return invoiceService.retrieveInvoiceDetails(userId, contextId, projectId, fmisInvoice);
  }

  /**
   * This end point returns the list of invoice Fees for the list of applied permits which has fee.
   * 
   * @param userId - User who initiates this required.
   * @param projectId - Project Id.
   * 
   * @return - Fee eligible permits and Fee details.
   */
  @GetMapping("/invoice-fee")
  @ApiOperation(
      value = "Retrieve the Invoice Fee details for the list of fee eligible permits for the input project id.")
  public Object retrieveInvoiceFeeDetails(
      @RequestHeader @ApiParam(example = "shortname",
          value = "User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value = "Project Id") final Long projectId) {
    final String contextId = UUID.randomUUID().toString();
    logger.info("Entering into retrieveInvoiceFeeDetails. User Id {}, Context Id {}", userId,
        contextId);
    return invoiceService.retrieveInvoiceFeeDetails(userId, contextId, projectId);
  }
}
