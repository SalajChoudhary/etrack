package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.awt.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.service.GenerateReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackReportGenerateControllerTest {
	
	  @Mock
	  private GenerateReportService generateReportService;
	  
	  @InjectMocks
	  private ETrackReportGenerateController eTrackReportGenerateController;

	  String userId = "dxdev";
	  String contextId ="con";
	  Long projectId = 1l;
	  String invoice ="44400000333";
	  
//	@Test
//	public void generateInvoiceReportWithBeanCollectionTest() {
//		ResponseEntity response = new ResponseEntity<>(HttpStatus.OK);
//		byte[] report = new ArrayList<>();
//		Mockito.lenient().when(generateReportService.retrieveInvoiceReport(userId, contextId, 
//				projectId, invoice)).thenReturn(response);
//	}

}
