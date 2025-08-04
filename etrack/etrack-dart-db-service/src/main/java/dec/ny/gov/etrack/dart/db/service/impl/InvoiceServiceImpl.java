package dec.ny.gov.etrack.dart.db.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.Invoice;
import dec.ny.gov.etrack.dart.db.model.InvoiceFeeType;
import dec.ny.gov.etrack.dart.db.repo.InvoiceFeeTypeRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;
import dec.ny.gov.etrack.dart.db.service.InvoiceService;

@Service
public class InvoiceServiceImpl implements InvoiceService {
  
  @Autowired
  private InvoiceRepo invoiceRepo;
  @Autowired
  private InvoiceFeeTypeRepo invoiceFeeTypeRepo;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
  private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class.getName());
  
  @Override
  public Object retrieveInvoiceDetails(final String userId, final String contextId, final Long projectId,
      final String fmisInvoice) {
    
    logger.info("Entering into Retrieve Invoice details. User Id {}, Context Id {}", userId, contextId);
    List<InvoiceEntity> invoices =
        invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(projectId, fmisInvoice);
    Invoice invoice = new Invoice();
    if (!CollectionUtils.isEmpty(invoices)) {
      List<String> invoiceStatuses = invoiceRepo.findAllInvoiceStatus();
      Map<String, String> invoiceStatusMap = new HashMap<>();
      if (CollectionUtils.isEmpty(invoiceStatuses)) {
        throw new BadRequestException("INVOICE_STATUS_NA", "Invoice Status is not available in the database", projectId);
      }
      invoiceStatuses.forEach(invoiceStatus -> {
        String[] invoiceStatusCodeAndDesc = invoiceStatus.split(",");
        invoiceStatusMap.put(invoiceStatusCodeAndDesc[0], invoiceStatusCodeAndDesc[1]);
      });

      invoices.forEach(invoiceEntity -> {
        List<InvoiceFeeType> type = new LinkedList<InvoiceFeeType>();
        invoice.setInvoiceId(invoiceEntity.getFmisInvoiceNum());
        invoice.setPayReference(invoiceEntity.getPaymentConfirmnId());
        if (invoiceEntity.getCreateDate() != null) {
          invoice.setInvoiceDate(dateFormat.format(invoiceEntity.getCreateDate()));
        }
        Long invoiceAmount = 0L;
        if (invoiceEntity.getInvoiceFeeType1() != null) {
          InvoiceFeeType projectTypeFee1 = new InvoiceFeeType();
          projectTypeFee1.setType(invoiceEntity.getInvoiceFeeType1());
          projectTypeFee1.setFee(invoiceEntity.getInvoiceFeeTypeFee1());
          type.add(projectTypeFee1);
          invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee1();
        }
        if (invoiceEntity.getInvoiceFeeTypeFee2() != null) {
          InvoiceFeeType projectTypeFee2 = new InvoiceFeeType();
          projectTypeFee2.setType(invoiceEntity.getInvoiceFeeType2());
          projectTypeFee2.setFee(invoiceEntity.getInvoiceFeeTypeFee2());
          type.add(projectTypeFee2);
          invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee2();
        }
        if (invoiceEntity.getInvoiceFeeTypeFee3() != null) {
          InvoiceFeeType projectTypeFee3 = new InvoiceFeeType();
          projectTypeFee3.setType(invoiceEntity.getInvoiceFeeType3());
          projectTypeFee3.setFee(invoiceEntity.getInvoiceFeeTypeFee3());
          type.add(projectTypeFee3);
          invoiceAmount += invoiceEntity.getInvoiceFeeTypeFee3();
        }
        invoice.setTypes(type);
        invoice.setStatus(invoiceStatusMap.get(invoiceEntity.getInvoiceStatusCode()));
        invoice.setCheckAmt(invoiceEntity.getCheckAmt());
        invoice.setCheckNumber(invoiceEntity.getCheckNumber());
        if (invoiceEntity.getCheckRcvdDate() != null) {
          invoice.setCheckRcvdDate(dateFormat.format(invoiceEntity.getCheckRcvdDate()));
        }
        if (StringUtils.hasLength(invoiceEntity.getInvoiceStatusCode())
            && invoiceEntity.getPaidAmt() != null) {
          invoice.setPaidAmount(invoiceEntity.getPaidAmt());
          invoice.setDueAmount(invoiceAmount - invoiceEntity.getPaidAmt());
        } else {
          invoice.setPaidAmount(0L);
          invoice.setDueAmount(invoiceAmount);
        }
        invoice.setNotes(invoiceEntity.getNotes());
        invoice.setReason(invoiceEntity.getCancelReason());
      });
    } else {
      throw new BadRequestException("INVOICE_NOT_AVAIL",
          "There is no invoice available for the invoice number " + fmisInvoice, projectId);
    }
    logger.info("Exiting from Retrieve Invoice details. User Id {}, Context Id {}", userId, contextId);
    return invoice;
  }


  @Override
  public List<dec.ny.gov.etrack.dart.db.entity.InvoiceFeeType> retrieveInvoiceFeeDetails(
      String userId, String contextId, Long projectId) {
    logger.info("Retrieve the Invoice fees and fee type for "
        + "the list of Fee eligible permits. User Id {}, Context id {}", userId, contextId);
    return invoiceFeeTypeRepo.findAllInvoiceFeeTypesByProjectId(projectId);
  }
}
