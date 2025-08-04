package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.dao.SpatialInquiryDetailDAO;
import dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryNote;
import dec.ny.gov.etrack.dart.db.entity.ReviewDocument;
import dec.ny.gov.etrack.dart.db.entity.SpatialInqDocumentEntity;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDetail;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocument;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryDocumentReview;
import dec.ny.gov.etrack.dart.db.entity.SpatialInquiryReviewDetail;
import dec.ny.gov.etrack.dart.db.model.GIReviewerDashboardDetail;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryCategory;
import dec.ny.gov.etrack.dart.db.model.SpatialInquiryRequest;
import dec.ny.gov.etrack.dart.db.repo.GeographicalInquiryNoteRepo;
import dec.ny.gov.etrack.dart.db.repo.GeographicalInquiryResponseRepo;
import dec.ny.gov.etrack.dart.db.repo.ReviewDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInqDocumentRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInquiryDocumentReviewRepo;
import dec.ny.gov.etrack.dart.db.repo.SpatialInquiryRepo;
import dec.ny.gov.etrack.dart.db.service.TransformationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class SpatialInquiryServiceImplTest {

	  @Mock
	  private SpatialInquiryRepo spatialInquiryRepo;
	  @Mock
	  private TransformationService transformationService;
	  @Mock
	  private SpatialInquiryDetailDAO spatialInquiryDetailDAO;
	  @Mock
	  private GeographicalInquiryNoteRepo geographicalInquiryNoteRepo;
	  @Mock
	  private SpatialInqDocumentRepo spatialInqDocumentRepo;
	  @Mock
	  private SpatialInquiryDocumentReviewRepo spatialInquiryDocumentReviewRepo;
	  @Mock
	  private GeographicalInquiryResponseRepo geographicalInquiryResponseRepo;
	  @Mock
	  private ReviewDocumentRepo reviewDocumentRepo;

	  @InjectMocks
	  private SpatialInquiryServiceImpl spatialInquiryServiceImpl;
	  
		  private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		  private final SimpleDateFormat MM_DD_YYYY_AM_PM_FORMAT =
		      new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
		  private String SYSTEM_USER_ID = "SYSTEM";
		  private static Map<String, String> non_releasable_status = new HashMap<>();

		  static {
			    non_releasable_status.put("NODET", "No Determination");
			    non_releasable_status.put("NOREL", "Non - Releasable");
			    non_releasable_status.put("REL", "Releasable");
			  }

	  @Test 
	  void retrieveSpatialInquiryServiceByInquiryType() {
		  List<SpatialInquiryRequest> list = spatialInquiryServiceImpl.retrieveSpatialInquiryServiceByInquiryType("",
			      "", SpatialInquiryCategory.ENERGY_PROJ);
	  }
	  
	  @Test
	  public void retrieveSpatialInquiryServiceByInquiryTypeTest() {
		  String userId = "";
	      String contextId = ""; 
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
	      List<SpatialInquiryDetail> spatialInquiryDetails = new ArrayList();
	      spatialInquiryDetails.add(enquiry);
	      when(spatialInquiryRepo
	    		  .findByAssignedAnalystIdAndSpatialInqCategoryId(Mockito.any(), Mockito.any())).thenReturn(spatialInquiryDetails);
	      List<SpatialInquiryRequest> inquiryReq = spatialInquiryServiceImpl.retrieveSpatialInquiryServiceByInquiryType(userId, contextId, SpatialInquiryCategory.ENERGY_PROJ);
	      assertNotNull(inquiryReq);
	  }
	  
	  @Test
	  private void retrieveSpatialInquiryServiceByInquiryTypeNullTest() {
		  String userId = "";
	      String contextId = ""; 
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
	      enquiry.setInquiryId(1L);	      
	      List<SpatialInquiryDetail> spatialInquiryDetails = new ArrayList();
	      spatialInquiryDetails.add(enquiry);
	      when(spatialInquiryRepo
	    		  .findByAssignedAnalystIdAndSpatialInqCategoryId(userId, 2)).thenReturn(spatialInquiryDetails);
	      List<SpatialInquiryRequest> inquiryReq = spatialInquiryServiceImpl.retrieveSpatialInquiryServiceByInquiryType(userId, contextId, null);
	      assertNotNull(inquiryReq);
	  }
	  
	  @Test
	  public void retrieveRegionalSpatialInquiryServiceByInquiryTypeTest() {
		  SpatialInquiryCategory inquiryType = SpatialInquiryCategory.BOROUGH_DETERMINATION;
		  List<SpatialInquiryDetail> spatialInquiryDetails = new ArrayList<>();
		  SpatialInquiryDetail spatialInquiryDetail = new SpatialInquiryDetail();
		  spatialInquiryDetail.setSpatialInqCategoryId(0);
		  spatialInquiryDetails.add(spatialInquiryDetail);
		  //when(spatialInquiryRepo.findBySpatialInqCategoryId(2)).thenReturn(spatialInquiryDetails);
		  Mockito.lenient().when(spatialInquiryRepo.findBySpatialInqCategoryIdAndRegion(2,0)).thenReturn(spatialInquiryDetails);
		  List<SpatialInquiryRequest> spatialInquiryRequests = new ArrayList<>();
		  SpatialInquiryRequest spatialInquiryRequest = new SpatialInquiryRequest();
		  spatialInquiryRequest.setInquiryId(1L);
		  spatialInquiryRequests.add(spatialInquiryRequest);
		  Object obj = spatialInquiryServiceImpl.retrieveRegionalSpatialInquiryServiceByInquiryType("dx","",inquiryType,0);
	  }
	  
	  @Test
	  public void retrieveRegionalSpatialInquiryServiceByInquiryTypeOneTest() {
		  SpatialInquiryCategory inquiryType = SpatialInquiryCategory.BOROUGH_DETERMINATION;
		  List<SpatialInquiryDetail> spatialInquiryDetails = new ArrayList<>();
		  SpatialInquiryDetail spatialInquiryDetail = new SpatialInquiryDetail();
		  spatialInquiryDetail.setSpatialInqCategoryId(0);
		  spatialInquiryDetails.add(spatialInquiryDetail);
		  //when(spatialInquiryRepo.findBySpatialInqCategoryId(2)).thenReturn(spatialInquiryDetails);
		  Mockito.lenient().when(spatialInquiryRepo.findBySpatialInqCategoryId(Mockito.any())).thenReturn(spatialInquiryDetails);
		  List<SpatialInquiryRequest> spatialInquiryRequests = new ArrayList<>();
		  SpatialInquiryRequest spatialInquiryRequest = new SpatialInquiryRequest();
		  spatialInquiryRequest.setInquiryId(1L);
		  spatialInquiryRequests.add(spatialInquiryRequest);
		  Object obj = spatialInquiryServiceImpl.retrieveRegionalSpatialInquiryServiceByInquiryType("dx","",inquiryType,null);
	  }
	  
	  
	  @Test
	  public void  retrieveSpatialInqDetailTest() {
		  String userId = ""; String contextId = ""; Long inquiryId = 1l;
	      String requestorName = "";
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
	      Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses = new ArrayList();
	      dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse ge = new dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse();
          ge.setModifiedDate(new Date());
          ge.setInquiryId(1L);
	      geographicalInquiryResponses.add(ge);
	      when(geographicalInquiryResponseRepo.findByInquiryCompletedInd(1)).thenReturn(geographicalInquiryResponses);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialInqDetail(userId, contextId, inquiryId,
	    	       requestorName);
	      assertNull(ob);
	  }
	  
	  @Test
	  public void  retrieveSpatialInqDetailIdnullTest() {
		  String userId = "23"; String contextId = ""; Long inquiryId = null;
	      String requestorName = "";
	      List<SpatialInquiryDetail> ob = (List<SpatialInquiryDetail>) spatialInquiryServiceImpl.retrieveSpatialInqDetail(userId, contextId, inquiryId,
	    	       requestorName);
	      assertEquals(0, ob.size());
	  }

	  @Test
	  public void  retrieveSpatialInqDetailNamenullTest() {
		  String userId = ""; String contextId = ""; Long inquiryId = null;
	      String requestorName = "Test";
	      List<SpatialInquiryDetail> ob = (List<SpatialInquiryDetail>) spatialInquiryServiceImpl.retrieveSpatialInqDetail(userId, contextId, inquiryId,
	    	       requestorName);
	      assertEquals(0, ob.size());
	  }
	  
	  @Test
	  public void retrieveSpatialDocumentSummaryCategoryNotEqualTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryCategory inquiryType = SpatialInquiryCategory.SERP_CERT;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("C");
		  inqDoc.setDocumentTypeId(21);
		  inqDoc.setReqdDocumentInd(2);
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(2);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  
	  @Test
	  public void retrieveSpatialDocumentSummaryRefDocEqualTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryCategory inquiryType = SpatialInquiryCategory.SERP_CERT;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("A");
		  inqDoc.setDocumentTypeId(21);
		  inqDoc.setReqdDocumentInd(1);
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(5);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  
	  @Test
	  public void retrieveSpatialDocumentSummaryTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryCategory inquiryType = SpatialInquiryCategory.SERP_CERT;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("A");
		  inqDoc.setDocumentTypeId(5);
		  inqDoc.setReqdDocumentInd(2);
		  inqDoc.setUploadedInd(1);
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(5);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  @Test
	  public void retrieveSpatialDocumentSummaryOneTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("A");
		  inqDoc.setDocumentTypeId(5);
		  inqDoc.setReqdDocumentInd(21);
		  inqDoc.setRefDocument("SEQR");
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(5);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  
	  @Test
	  public void retrieveSpatialDocumentSummaryTwoTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("A");
		  inqDoc.setDocumentTypeId(5);
		  inqDoc.setReqdDocumentInd(23);
		  inqDoc.setRefDocument("SHPAdsds");
		  inqDoc.setReqdDocumentInd(1);
		  inqDoc.setDocumentTitle("test");
		  SpatialInquiryDocument inqDoc1 = new SpatialInquiryDocument();
		  inqDoc1.setDocumentStateCode("A");
		  inqDoc1.setDocumentTypeId(5);
		  inqDoc1.setReqdDocumentInd(23);
		  inqDoc1.setRefDocument("SHPAdsds");
		  inqDoc1.setReqdDocumentInd(21);
		  inqDoc1.setDocumentTitle("test");
		  SpatialInquiryDocument inqDoc2 = new SpatialInquiryDocument();
		  inqDoc2.setDocumentStateCode("A");
		  inqDoc2.setDocumentTypeId(5);
		  inqDoc2.setReqdDocumentInd(23);
		  inqDoc2.setRefDocument("SHPAdsds");
		  inqDoc2.setReqdDocumentInd(23);
		  inqDoc2.setDocumentTitle("test");
		  SpatialInquiryDocument inqDoc3 = new SpatialInquiryDocument();
		  inqDoc3.setDocumentStateCode("A");
		  inqDoc3.setDocumentTypeId(5);
		  inqDoc3.setReqdDocumentInd(23);
		  inqDoc3.setRefDocument("SHPAdsds");
		  inqDoc3.setReqdDocumentInd(25);
		  inqDoc3.setDocumentTitle("test");
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  spatialInquiryDocuments.add(inqDoc1);
		  spatialInquiryDocuments.add(inqDoc2);
		  spatialInquiryDocuments.add(inqDoc3);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(2);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  
	  @Test
	  public void retrieveSpatialDocumentSummaryThreeTest() {
		  String userId = ""; String contextId = "";  Long inquiryId = 1l;
		  SpatialInquiryDocument inqDoc = new SpatialInquiryDocument();
		  inqDoc.setDocumentStateCode("A");
		  inqDoc.setDocumentTypeId(5);
		  inqDoc.setReqdDocumentInd(23);
		  inqDoc.setRefDocument("SHPA");
		  List<SpatialInquiryDocument> spatialInquiryDocuments = new ArrayList();
		  spatialInquiryDocuments.add(inqDoc);
		  when(spatialInquiryDetailDAO.getSpatialInquiryDocument(userId, contextId, inquiryId)).thenReturn(spatialInquiryDocuments);
		  SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(5);
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      Object ob = spatialInquiryServiceImpl.retrieveSpatialDocumentSummary( userId, contextId,  inquiryId);
	      assertNotNull(ob);
	  }
	  
	  @Test
	  public void retrieveSpatialInquiryStatusTest() {
		  Map<String, Long> obj = spatialInquiryServiceImpl.retrieveSpatialInquiryStatus("", "", 1L);
	      assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveGeographicalInquiryForVWTest() {
		  String userId = "";  String contextId = "";
	      Long inquiryId = 1L;
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(2);
		  enquiry.setBorough("234");
		  enquiry.setBlock("234");
		  enquiry.setLot("234");
		  enquiry.setOriginalSubmittalDate(new Date());
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      List<String> geographicalInquiryCategories = new ArrayList();
	      geographicalInquiryCategories.add("1,2,3,4");
	      when(spatialInquiryRepo.findInquiryCategoryByInquiryId(inquiryId)).thenReturn(geographicalInquiryCategories);
	      GeographicalInquiryNote g = new GeographicalInquiryNote();
	      List<GeographicalInquiryNote> geographicalInquiryNotes = new ArrayList();
	      geographicalInquiryNotes.add(g);
	      when(geographicalInquiryNoteRepo.findNotesByInquiryId(inquiryId)).thenReturn(geographicalInquiryNotes);
	      SpatialInquiryDocumentReview review = new SpatialInquiryDocumentReview();
	      review.setDocReviewerName("TEST");
	      List<SpatialInquiryDocumentReview> reviewDocumentsList = new ArrayList();
	      reviewDocumentsList.add(review);
	      when(spatialInquiryDocumentReviewRepo.findAllReviewDocumentsByInquiryId(inquiryId)).thenReturn(reviewDocumentsList);
	      dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse gn = new dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse(); 
	      gn.setResponseSentDate(new Date());
	      gn.setResponseSentInd(1);
	      gn.setInquiryCompletedInd(1);
	      SpatialInqDocumentEntity spt = new SpatialInqDocumentEntity();
	      spt.setFileCount(2);
	      spt.setCreateDate(new Date());
	      List<SpatialInqDocumentEntity> spatialInquiryDocuments = new ArrayList();
	      spatialInquiryDocuments.add(spt);
	      when(spatialInqDocumentRepo.findAllUploadedSupportDocumentsByInquiryIdWithFilesCount(inquiryId)).thenReturn(spatialInquiryDocuments);
	      List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses = new ArrayList();
	      geographicalInquiryResponses.add(gn);
	      when(geographicalInquiryResponseRepo.findByInquiryId(inquiryId)).thenReturn(geographicalInquiryResponses);
	      Object obj =  spatialInquiryServiceImpl.retrieveGeographicalInquiryForVW( userId, contextId,
	    	      inquiryId);
	      assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveGeographicalInquiryForVWoneTest() {
		  String userId = "";  String contextId = "";
	      Long inquiryId = 1L;
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(2);
		  enquiry.setBorough("234");
		  enquiry.setBlock("234");
		  enquiry.setLot("234");
		  enquiry.setOriginalSubmittalDate(new Date());
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      List<String> geographicalInquiryCategories = new ArrayList();
	      geographicalInquiryCategories.add("6,2,3,4");
	      when(spatialInquiryRepo.findInquiryCategoryByInquiryId(inquiryId)).thenReturn(geographicalInquiryCategories);
	      GeographicalInquiryNote g = new GeographicalInquiryNote();
	      List<GeographicalInquiryNote> geographicalInquiryNotes = new ArrayList();
	      geographicalInquiryNotes.add(g);
	      when(geographicalInquiryNoteRepo.findNotesByInquiryId(inquiryId)).thenReturn(geographicalInquiryNotes);
	      SpatialInquiryDocumentReview review = new SpatialInquiryDocumentReview();
	      review.setDocReviewerName("TEST");
	      List<SpatialInquiryDocumentReview> reviewDocumentsList = new ArrayList();
	      reviewDocumentsList.add(review);
	      when(spatialInquiryDocumentReviewRepo.findAllReviewDocumentsByInquiryId(inquiryId)).thenReturn(reviewDocumentsList);
	      dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse gn = new dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse(); 
	      gn.setResponseSentDate(new Date());
	      gn.setResponseSentInd(1);
	      gn.setInquiryCompletedInd(1);
	      SpatialInqDocumentEntity spt = new SpatialInqDocumentEntity();
	      spt.setFileCount(2);
	      spt.setCreateDate(new Date());
	      List<SpatialInqDocumentEntity> spatialInquiryDocuments = new ArrayList();
	      spatialInquiryDocuments.add(spt);
	      when(spatialInqDocumentRepo.findAllUploadedSupportDocumentsByInquiryIdWithFilesCount(inquiryId)).thenReturn(spatialInquiryDocuments);
	      List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses = new ArrayList();
	      geographicalInquiryResponses.add(gn);
	      when(geographicalInquiryResponseRepo.findByInquiryId(inquiryId)).thenReturn(geographicalInquiryResponses);
	      Object obj =  spatialInquiryServiceImpl.retrieveGeographicalInquiryForVW( userId, contextId,
	    	      inquiryId);
	      assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveGeographicalInquiryForVWtwoTest() {
		  String userId = "";  String contextId = "";
	      Long inquiryId = 1L;
	      SpatialInquiryDetail enquiry = new SpatialInquiryDetail();
		  enquiry.setSpatialInqCategoryId(2);
		  enquiry.setBorough("234");
		  enquiry.setBlock("234");
		  enquiry.setLot("234");
		  enquiry.setOriginalSubmittalDate(new Date());
		  Optional<SpatialInquiryDetail> spatialInquiryDetailsAvail = Optional.of(enquiry);
	      when(spatialInquiryRepo.findById(inquiryId)).thenReturn(spatialInquiryDetailsAvail);
	      List<String> geographicalInquiryCategories = new ArrayList();
	      geographicalInquiryCategories.add("7,2,3,4");
	      when(spatialInquiryRepo.findInquiryCategoryByInquiryId(inquiryId)).thenReturn(geographicalInquiryCategories);
	      GeographicalInquiryNote g = new GeographicalInquiryNote();
	      List<GeographicalInquiryNote> geographicalInquiryNotes = new ArrayList();
	      geographicalInquiryNotes.add(g);
	      when(geographicalInquiryNoteRepo.findNotesByInquiryId(inquiryId)).thenReturn(geographicalInquiryNotes);
	      SpatialInquiryDocumentReview review = new SpatialInquiryDocumentReview();
	      review.setDocReviewerName("TEST");
	      List<SpatialInquiryDocumentReview> reviewDocumentsList = new ArrayList();
	      reviewDocumentsList.add(review);
	      when(spatialInquiryDocumentReviewRepo.findAllReviewDocumentsByInquiryId(inquiryId)).thenReturn(reviewDocumentsList);
	      dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse gn = new dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse(); 
	      gn.setResponseSentDate(new Date());
	      gn.setResponseSentInd(1);
	      gn.setInquiryCompletedInd(1);
	      SpatialInqDocumentEntity spt = new SpatialInqDocumentEntity();
	      spt.setFileCount(2);
	      spt.setCreateDate(new Date());
	      List<SpatialInqDocumentEntity> spatialInquiryDocuments = new ArrayList();
	      spatialInquiryDocuments.add(spt);
	      when(spatialInqDocumentRepo.findAllUploadedSupportDocumentsByInquiryIdWithFilesCount(inquiryId)).thenReturn(spatialInquiryDocuments);
	      List<dec.ny.gov.etrack.dart.db.entity.GeographicalInquiryResponse> geographicalInquiryResponses = new ArrayList();
	      geographicalInquiryResponses.add(gn);
	      when(geographicalInquiryResponseRepo.findByInquiryId(inquiryId)).thenReturn(geographicalInquiryResponses);
	      Object obj =  spatialInquiryServiceImpl.retrieveGeographicalInquiryForVW( userId, contextId,
	    	      inquiryId);
	      assertNotNull(obj);
	  }
	  
	  @Test
	  public void retrieveGeographicalNoteConfigTest() {
		  List<String> geographicalActiveNotes = new ArrayList();
		  geographicalActiveNotes.add("one,two,three");
		  when(geographicalInquiryNoteRepo.findAllActiveGeographicalNoteConfig()).thenReturn(geographicalActiveNotes);
	    Object obj = spatialInquiryServiceImpl.retrieveGeographicalNoteConfig("", "");
	    assertNotNull(obj);
	  }
	  
	  @Test
	  public void getNoteTest() {
		  String userId = ""; String contextId =""; Long inquiryId=1L; Long noteId=1L;
		  List<GeographicalInquiryNote> geographicalInquiryNotes = new ArrayList();
		  GeographicalInquiryNote gn = new GeographicalInquiryNote();
		  gn.setCreatedById("34");
		  gn.setModifiedDate(new Date());
		  geographicalInquiryNotes.add(gn);
		  when(geographicalInquiryNoteRepo.findNoteByInquiryIdAndNoteId(inquiryId, noteId)).thenReturn(geographicalInquiryNotes);
		  Object obj =  spatialInquiryServiceImpl.getNote(userId, contextId, inquiryId, noteId);  
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void getNoteNoIdTest() {
		  String userId = ""; String contextId =""; Long inquiryId=1L; Long noteId=1L;
		  List<GeographicalInquiryNote> geographicalInquiryNotes = new ArrayList();
		  GeographicalInquiryNote gn = new GeographicalInquiryNote();
//		  gn.setCreatedById("34");
		  gn.setModifiedDate(new Date());
		  geographicalInquiryNotes.add(gn);
		  when(geographicalInquiryNoteRepo.findNoteByInquiryIdAndNoteId(inquiryId, noteId)).thenReturn(geographicalInquiryNotes);
		  Object obj =  spatialInquiryServiceImpl.getNote(userId, contextId, inquiryId, noteId);  
		  assertNotNull(obj);
	  }
	  
	  @Test
	  public void getProgramReviewerDashboardDetailsTest() {
		  String userId = "";
	       String contextId = "";
	       List<SpatialInquiryReviewDetail> spatialInquiryReviewDetails = new ArrayList();
	       SpatialInquiryReviewDetail sid = new SpatialInquiryReviewDetail();
	       spatialInquiryReviewDetails.add(sid);
	       when(spatialInquiryDetailDAO.retrieveSpatialInquiryReviewDetails(userId, contextId)).thenReturn(spatialInquiryReviewDetails);
		  List<GIReviewerDashboardDetail> list = spatialInquiryServiceImpl.getProgramReviewerDashboardDetails(userId,
			      contextId);
		  assertNotNull(list);
	  }
	  
	  @Test
	  public void retrieveEligibleReviewDocumentsTest() {
		  List<ReviewDocument> obj =  (List<ReviewDocument>) spatialInquiryServiceImpl.retrieveEligibleReviewDocuments("","",1L);
	      assertEquals(0, obj.size());
	  }
	  
	  @Test
	  public void retrieveAllSpatialInquiryDetailsTest() {
		  List<SpatialInquiryDetail> spatialInquiryDetails = new ArrayList();
		  SpatialInquiryDetail spd = new SpatialInquiryDetail();
		  spatialInquiryDetails.add(spd);
		  when(spatialInquiryRepo.findBySpatialInquiriesByRegion(1)).thenReturn(spatialInquiryDetails);
		  Object ob = spatialInquiryServiceImpl.retrieveAllSpatialInquiryDetails("", "",
			      1);
		  assertNotNull(ob);
	  }
}
