package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import dec.ny.gov.etrack.dart.db.dao.ETrackQueryReportDAO;
import dec.ny.gov.etrack.dart.db.entity.CandidateKeywordDetail;
import dec.ny.gov.etrack.dart.db.entity.SubmittedProjectDetail;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittalReport;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;
import dec.ny.gov.etrack.dart.db.repo.ETrackQueryReportRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackQueryReportServiceImplTest {

	  @Mock
	  private ETrackQueryReportDAO eTrackQueryReportDAO;
	  
	  @Mock
	  private ETrackQueryReportRepo eTrackQueryReportRepo;
	  
	  @InjectMocks
	  ETrackQueryReportServiceImpl eTrackQueryReportServiceImpl;
	  
	  String userId ="";
	  String contextId="";
	  
	  @Test
	  public void retrieveProjectSubmittalDetailsMailTest() {
		  
		  ProjectSubmittedRetrievalCriteria projectSubmittalReport = new ProjectSubmittedRetrievalCriteria();		  
		  projectSubmittalReport.setStartDate("02/20/2022");
		  projectSubmittalReport.setEndDate("02/20/2024");
		  List<SubmittedProjectDetail> submittedProjectDetails = new ArrayList<>();		  
		  SubmittedProjectDetail submittedProjectDetail = new SubmittedProjectDetail();
		  submittedProjectDetail.setMailInInd(1);
		  submittedProjectDetails.add(submittedProjectDetail);
		  submittedProjectDetail.setTotal(new BigDecimal(2));
		  Mockito.lenient().when(eTrackQueryReportDAO.retrieveSubmittedProjectDetails(
				  userId, contextId, projectSubmittalReport)).thenReturn(submittedProjectDetails);		  
		  Object obj = eTrackQueryReportServiceImpl.retrieveProjectSubmittalDetails(userId, contextId, projectSubmittalReport);		  
		  assertNotEquals(obj, null);  
	  }
	  
	  @Test
	  public void retrieveProjectSubmittalDetailsPaperTest() {
		  
		  ProjectSubmittedRetrievalCriteria projectSubmittalReport = new ProjectSubmittedRetrievalCriteria();		  
		  projectSubmittalReport.setStartDate("02/20/2022");
		  projectSubmittalReport.setEndDate("02/20/2024");
		  List<SubmittedProjectDetail> submittedProjectDetails = new ArrayList<>();		  
		  SubmittedProjectDetail submittedProjectDetail = new SubmittedProjectDetail();
		  submittedProjectDetail.setMailInInd(0);
		  submittedProjectDetails.add(submittedProjectDetail);
		  submittedProjectDetail.setTotal(new BigDecimal(2));
		  Mockito.lenient().when(eTrackQueryReportDAO.retrieveSubmittedProjectDetails(
				  userId, contextId, projectSubmittalReport)).thenReturn(submittedProjectDetails);		  
		  Object obj = eTrackQueryReportServiceImpl.retrieveProjectSubmittalDetails(userId, contextId, projectSubmittalReport);		  
		  assertNotEquals(obj, null);  
	  }
	  	  
	  @Test
	  public void retrieveCandidateKeywordDetailsReport() {
		  CandidateKeywordDetail ckd = new CandidateKeywordDetail();
		  ckd.setRegion(1);
		  ckd.setKeywordText("Test");
		  ckd.setCount(3);
		  List<CandidateKeywordDetail> candidateKeywordDetails = new ArrayList();
		  candidateKeywordDetails.add(ckd);
		  when(eTrackQueryReportRepo.retrieveCandidateKeywordDetails()).thenReturn(candidateKeywordDetails);
		  Object obj =  eTrackQueryReportServiceImpl.retrieveCandidateKeywordDetailsReport(userId, contextId);
		  assertNotEquals(obj, null);
	  }
}
