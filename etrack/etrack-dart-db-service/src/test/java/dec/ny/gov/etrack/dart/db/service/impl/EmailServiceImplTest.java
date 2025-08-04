package dec.ny.gov.etrack.dart.db.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.EmailCorrespondence;
import dec.ny.gov.etrack.dart.db.entity.Facility;
import dec.ny.gov.etrack.dart.db.model.DashboardEmailEnvelop;
import dec.ny.gov.etrack.dart.db.model.EmailContent;
import dec.ny.gov.etrack.dart.db.model.RegionUserEntity;
import dec.ny.gov.etrack.dart.db.model.VirtualDesktopEmailShortDesc;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.EmailCorrespondenceRepo;
import dec.ny.gov.etrack.dart.db.repo.FacilityRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
	 @Mock
	  private EmailCorrespondenceRepo emailCorrespondenceRepo;
	  
	 @Mock
	  private FacilityRepo facilityRepo;
	 @Mock
	  private ApplicantRepo applicantRepo;
	 @Mock
	  private DartDbDAO dartDbDAO;
	 
	 @InjectMocks
	 EmailServiceImpl emailService = new EmailServiceImpl();
	 
	 String userId = "aba"; 
	 String contextId = "";
	 Long projectId=1L;
	 Long correspondenceId=1L; 
	 boolean envelopRequestedInd=true;
	 String emailSendorId, emailReceiverId, correspondenceType = "";
	 @Test
	 public void retrieveEmailCorrespondenceTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecr = new EmailCorrespondence();
		 ecr.setCreateDate(new Date());
		 ecr.setEmailSubject("new email");
		 emailCorrespondences.add(ecr);
		 when(emailCorrespondenceRepo.findByCorrepondenceByUserIdAndProjectId(userId, projectId)).thenReturn(emailCorrespondences);
		 Object obj = emailService.retrieveEmailCorrespondence(userId,contextId,projectId);
	     assertNotNull(obj);
	 }
	
	 @Test
	 public void retrieveEmailCorrespondenceelseTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecr = new EmailCorrespondence();
		 ecr.setCreateDate(new Date());
		 ecr.setEmailSubject("new email");
		 emailCorrespondences.add(ecr);
		 when(emailCorrespondenceRepo.findByCorrepondenceByUserId(userId)).thenReturn(emailCorrespondences);
//		 List<ApplicantDto> lrpsList = new ArrayList();
//		 ApplicantDto apd = new ApplicantDto();
//		 lrpsList.add(apd);
//		 when(applicantRepo.findLRPDetailsByProjectId(Mockito.any())).thenReturn(lrpsList);
		 dec.ny.gov.etrack.dart.db.entity.Facility facility = new dec.ny.gov.etrack.dart.db.entity.Facility();
		 facility.setDecId("dskfndslknf");
		 when(facilityRepo.findByProjectId(null)).thenReturn(facility);
		 Object obj = emailService.retrieveEmailCorrespondence(userId,contextId,null);
	     assertNotNull(obj);
	 }
	 
	 @Test
	 public void retrieveEmailCorrespondenceByCorrespondenceIdTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.findByCorrespondenceIdAndProjectId(correspondenceId, projectId)).thenReturn(emailCorrespondences);;
		 List<RegionUserEntity> regionUserEntity = new ArrayList();
		 RegionUserEntity rue = new RegionUserEntity();
		 rue.setEmailAddress("ruby@yopmail.com");
		
		 regionUserEntity.add(rue);
		 when(dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId)).thenReturn(regionUserEntity);
		 Facility facility = new Facility();
		 facility.setFacilityName("Test");
		 Mockito.lenient().when(facilityRepo.findByProjectId(projectId)).thenReturn(facility);   
		 EmailContent ec= emailService.retrieveEmailCorrespondenceByCorrespondenceId(userId, contextId,
			      projectId, correspondenceId, envelopRequestedInd);
		 assertNotNull(ec);
	 }
	 
	 @Test
	 public void retrieveEmailCorrespondenceByCorrespondenceIdOneTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.findByCorrespondenceIdAndProjectId(correspondenceId, projectId)).thenReturn(emailCorrespondences);;
		 List<RegionUserEntity> regionUserEntity = new ArrayList();
		 RegionUserEntity rue = new RegionUserEntity();
		 rue.setEmailAddress("ruby@yopmail.com");
		
		 regionUserEntity.add(rue);
		 when(dartDbDAO.retrieveStaffDetailsByUserId(userId, contextId)).thenReturn(regionUserEntity);
		 Facility facility = new Facility();
		 facility.setFacilityName("Test");
		 Mockito.lenient().when(facilityRepo.findByProjectId(projectId)).thenReturn(facility);   
		 EmailContent ec= emailService.retrieveEmailCorrespondenceByCorrespondenceId(userId, contextId,
			      projectId, correspondenceId, false);
		 assertNotNull(ec);
	 }
	 
	 @Test
	 public void retrieveEmailNotificationDetailsTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 ecs.setProjectId(1L);
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.retrieveEmailUnreadMessageCountByUserId(userId)).thenReturn(emailCorrespondences);
//		 List<String> projectIdsAndFacilityNameList = new ArrayList();
//		 projectIdsAndFacilityNameList.add("2,5");
//		  emailCorrespondenceRepo.retrieveFacilityNameByProjectIds( new LinkedHashMap<>().keySet());
		      
		 List<DashboardEmailEnvelop> dee =  emailService.retrieveEmailNotificationDetails(userId, contextId);
		 assertNotNull(dee);
	 }
	 
	 @Test
	 public void retrieveEmailNotificationsInVirtualDesktop() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 ecs.setProjectId(1L);
		 ecs.setEmailRcvdUserId("aba");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.findCorrespondenceByUserIdAndProjectId(userId, projectId)).thenReturn(emailCorrespondences);
		    
		 Map<String, List<VirtualDesktopEmailShortDesc>> list = emailService.retrieveEmailNotificationsInVirtualDesktop(userId, contextId,
			      projectId);
	     assertNotNull(list);
	 }
	 @Test
	 public void retrieveEmailNotificationsInVirtualDesktopOneTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 ecs.setProjectId(1L);
		 ecs.setEmailRcvdUserId("abas");
		 ecs.setEmailRqstdUserId("aba");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.findCorrespondenceByUserIdAndProjectId(userId, projectId)).thenReturn(emailCorrespondences);
		    
		 Map<String, List<VirtualDesktopEmailShortDesc>> list = emailService.retrieveEmailNotificationsInVirtualDesktop(userId, contextId,
			      projectId);
	     assertNotNull(list);
	 }
	 
	 @Test
	 public void retrieveCorrespondencesForTheRequestorTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 ecs.setProjectId(1L);
		 ecs.setEmailRcvdUserId("aba");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.retrieveEmailCorrespondencesBetweenUser(
	              emailReceiverId, emailSendorId, projectId)).thenReturn(emailCorrespondences);
		    
		 List<EmailCorrespondence> list = emailService.retrieveCorrespondencesForTheRequestor( userId, contextId, projectId,
			      emailSendorId, emailReceiverId, correspondenceType);
		 assertNotNull(list);
	 }
	 
	 String reviewerId="";
	 Long documentId = 1L;
	 List<Long> documentIds = new ArrayList();
	 @Test
	 public void retrieveEmailCorrespondenceByDocumentId() {
		 List<Long> correspondenceIds = new ArrayList();
		 correspondenceIds.add(1L);
		 when(emailCorrespondenceRepo.findCorrespondenceIdByUserIdAndDocumentId(reviewerId, documentId)).thenReturn(correspondenceIds);;

		 List<List<String>> list = emailService.retrieveEmailCorrespondenceByDocumentId(
			       userId, contextId, reviewerId,
			      projectId,documentId);
		 assertNotNull(list);
	 }
	 
	 @Test
	 public void retrieveCorrespondenceByReviewerAndDocumentIdsTest() {
		 List<EmailCorrespondence> emailCorrespondences = new ArrayList();
		 EmailCorrespondence ecs = new EmailCorrespondence();
		 ecs.setFromEmailAdr("ruby@yopmail.com");
		 ecs.setToEmailAdr("test@yopmail.com");
		 ecs.setCcEmailAdr("cop@yopmail.com");
		 ecs.setCorrespondenceId(1L);
		 ecs.setTopicId(1L);
		 ecs.setModifiedDate(new Date());
		 ecs.setEmailContent("test email content");
		 ecs.setProjectId(1L);
		 ecs.setEmailRcvdUserId("aba");
		 emailCorrespondences.add(ecs);
		 when(emailCorrespondenceRepo.retrieveEmailCorrespondencesByReviewerIdAndDocumentIds(reviewerId, projectId, documentIds)).thenReturn(emailCorrespondences);
		 List<EmailCorrespondence> list = emailService.retrieveCorrespondenceByReviewerAndDocumentIds( userId,  contextId, 
			      reviewerId,  projectId, documentIds);
		 assertNotNull(list);
	 }
}
