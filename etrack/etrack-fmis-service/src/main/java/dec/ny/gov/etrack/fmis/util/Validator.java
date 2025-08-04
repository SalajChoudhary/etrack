package dec.ny.gov.etrack.fmis.util;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.model.BillingInvoiceRequest;
import dec.ny.gov.etrack.fmis.model.ProjectType;

public class Validator {

  private static final Logger logger = LoggerFactory.getLogger(Validator.class.getName());
  
  /**
   * Validate the request parameters passed by the user as part of the invoice request.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param invoiceBillingRequest - Invoice Billing request.
   */
  public static void isValid(final String userId, final String contextId,
      final BillingInvoiceRequest invoiceBillingRequest) {
    logger.info("Entering into Validate the input request User id {}, Context Id {}", userId,
        contextId);

    if (invoiceBillingRequest == null) {
      throw new BadRequestException("NO_INVOICE_REQ", "There is no invoice request is passed.");
    }
    List<ProjectType> billingAccounts = invoiceBillingRequest.getTypes();

    if (CollectionUtils.isEmpty(billingAccounts)) {
      throw new BadRequestException("NO_INVOICE_BILLG_REQ", "There is no Billing Account details passed.");
    }
    
    billingAccounts.forEach(account -> {
      if (StringUtils.isEmpty(account.getType()) || account.getFee() == null
          || account.getFee() <= 0) {
        throw new BadRequestException("FEE_TYPE_OR_FEE_MISSING", "Fee Type and/or Fee is missing");
      }
    });
    logger.info("Exiting from Validate the input request User id {}, Context Id {}", userId,
        contextId);
  }
}
