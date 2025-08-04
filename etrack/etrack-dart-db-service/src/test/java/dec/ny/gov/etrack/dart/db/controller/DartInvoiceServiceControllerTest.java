package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.entity.InvoiceFeeType;
import dec.ny.gov.etrack.dart.db.service.InvoiceService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartInvoiceServiceControllerTest {
	
	  @Mock
	  private InvoiceService invoiceService;
	  
	  @InjectMocks
	  private DartInvoiceServiceController dartInvoiceServiceController;

	@Test
	public void retrieveInvoiceDetailsTest() {
		List<dec.ny.gov.etrack.dart.db.model.InvoiceFeeType> invoiceDetail= new ArrayList<>();
		when(invoiceService.retrieveInvoiceDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString())).thenReturn(invoiceDetail);
		Object object = dartInvoiceServiceController.retrieveInvoiceDetails("dxdev", 1L,"");
		assertNotNull(object);
	}
	
	@Test
	public void retrieveInvoiceFeeDetailsTest() {
		List<InvoiceFeeType> invoiceDetail= new ArrayList<>();
		when(invoiceService.retrieveInvoiceFeeDetails(Mockito.anyString(), Mockito.anyString(), 
				Mockito.anyLong())).thenReturn(invoiceDetail);
		Object object = dartInvoiceServiceController.retrieveInvoiceFeeDetails("dxdev", 1L);
		assertNotNull(object);
	}

}
