package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.InvoiceEntity;
import dec.ny.gov.etrack.dart.db.entity.InvoiceFeeType;
import dec.ny.gov.etrack.dart.db.model.Invoice;
import dec.ny.gov.etrack.dart.db.repo.InvoiceFeeTypeRepo;
import dec.ny.gov.etrack.dart.db.repo.InvoiceRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

	@Mock
	private InvoiceRepo invoiceRepo;
	@Mock
	private InvoiceFeeTypeRepo invoiceFeeTypeRepo;
	
	@InjectMocks
	private InvoiceServiceImpl invoiceServiceImpl;
	
	
	@Test
	public void retrieveInvoiceDetailsTest() {
		Long projectId =9905L;
		String fmisInvoice = "4440000000308";
		List<InvoiceEntity> invoices = new ArrayList<>();
		InvoiceEntity invoiceEntity = new InvoiceEntity();
		invoiceEntity.setFmisInvoiceNum("4440000000308");
		invoiceEntity.setInvoiceFeeType1("100");
		invoiceEntity.setInvoiceFeeTypeFee1(100);
		invoiceEntity.setInvoiceFeeTypeFee2(200);
		invoiceEntity.setInvoiceFeeTypeFee3(300);
		invoiceEntity.setCheckRcvdDate(new Date());
		invoiceEntity.setCreateDate(new Date());
		invoiceEntity.setInvoiceStatusCode("sa");
		invoiceEntity.setPaidAmt(200L);
		invoices.add(invoiceEntity);
		when(invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(projectId, fmisInvoice)).thenReturn(invoices);
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(fmisInvoice);
		invoice.setCheckNumber("100");
		invoice.setCheckRcvdDate("02/21/2024");
		invoice.setCheckAmt(100L);
		List<String> invoiceStatuses = new ArrayList<>();
		invoiceStatuses.add(invoice.toString());
		Mockito.lenient().when(invoiceRepo.findAllInvoiceStatus()).thenReturn(invoiceStatuses);
		Object obj = invoiceServiceImpl.retrieveInvoiceDetails("dxdevada","",projectId,fmisInvoice);
		assertNotNull(obj);
	}
	
	@Test
	public void retrieveInvoiceDetailsNullTest() {
		Long projectId =9905L;
		String fmisInvoice = "4440000000308";
		List<InvoiceEntity> invoices = new ArrayList<>();
		InvoiceEntity invoiceEntity = new InvoiceEntity();
		invoiceEntity.setFmisInvoiceNum("4440000000308");
		invoiceEntity.setInvoiceFeeTypeFee1(100);
		invoices.add(invoiceEntity);
		when(invoiceRepo.findAllByProjectIdAndFmisInvoiceNum(projectId, fmisInvoice)).thenReturn(invoices);
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(fmisInvoice);
		invoice.setCheckNumber("100");
		invoice.setCheckRcvdDate("02/21/2024");
		invoice.setCheckAmt(100L);
		List<String> invoiceStatuses = new ArrayList<>();
		invoiceStatuses.add(invoice.toString());
		Mockito.lenient().when(invoiceRepo.findAllInvoiceStatus()).thenReturn(invoiceStatuses);
		Object obj = invoiceServiceImpl.retrieveInvoiceDetails("dxdevada","",projectId,fmisInvoice);
		assertNotNull(obj);
	}
	@Test
	public void retrieveInvoiceFeeDetailsTest() {
		List<InvoiceFeeType> invoiceFee = new ArrayList<>();
		InvoiceFeeType invoiceFeeType = new InvoiceFeeType();
		invoiceFee.add(invoiceFeeType);
		Mockito.lenient().when(invoiceFeeTypeRepo.findAllInvoiceFeeTypesByProjectId(100L)).thenReturn(invoiceFee);
		List<InvoiceFeeType> inv = invoiceServiceImpl.retrieveInvoiceFeeDetails("", "", 1L);
		assertNotNull(inv);
	}

}
