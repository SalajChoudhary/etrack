package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.model.ProjectSubmittalReport;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;
import dec.ny.gov.etrack.dart.db.service.ETrackQueryReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackQueryReportControllerTest {
	
	@Mock
	private ETrackQueryReportService eTrackQueryReportService;
	
	@InjectMocks
	private ETrackQueryReportController eTrackQueryReportController;

	@Test
	public void generateProjectSubmittalReportTest() {
		ProjectSubmittalReport projectSubmittalReport= new ProjectSubmittalReport();
		ProjectSubmittedRetrievalCriteria projectSubmittedRetrievalCriteria=null ; 
		Mockito.lenient().when(eTrackQueryReportService.retrieveProjectSubmittalDetails("dxdevada","contextId",
				projectSubmittedRetrievalCriteria)).thenReturn(projectSubmittalReport);
		Object obj = eTrackQueryReportController.generateProjectSubmittalReport("dxdevada", 
				projectSubmittedRetrievalCriteria);
		assertEquals(null, obj);
	}
	
	@Test
	public void retrieveCandidateKeywordDetailsReportTest() {
		ProjectSubmittalReport projectSubmittalReport= new ProjectSubmittalReport();
		ProjectSubmittedRetrievalCriteria projectSubmittedRetrievalCriteria=null ; 
		Mockito.lenient().when(eTrackQueryReportService.retrieveCandidateKeywordDetailsReport("dxdevada","contextId"
				)).thenReturn(projectSubmittalReport);
		Object obj = eTrackQueryReportController.retrieveCandidateKeywordDetailsReport("dxdevada");
		assertEquals(null, obj);
	}

}
