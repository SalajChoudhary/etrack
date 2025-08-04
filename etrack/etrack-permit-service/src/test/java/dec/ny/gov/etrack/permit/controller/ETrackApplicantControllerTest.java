package dec.ny.gov.etrack.permit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.model.PublicCategory;
import dec.ny.gov.etrack.permit.service.ETrackApplicantService;
import dec.ny.gov.etrack.permit.util.Validator;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackApplicantControllerTest {

	
	@InjectMocks
	private ETrackApplicantController applicantController;
	
	@Mock
	private ETrackApplicantService eTrackApplicantService;
	
	@Mock
	private Validator validator;
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	

	private String userId = "jxpuvoge";
	private Long projectId = 1002L;
	
	
	// Save Applicant tests
	@Test
	public void testSaveApplicantUpdatesApplicantIfExisting() {
		applicantController.saveApplicant(userId, projectId, getApplicant(), PublicCategory.P);
		verify(eTrackApplicantService).updateApplicant(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.anyString());
	}
	
	@Test
	public void testSaveApplicantUpdatesApplicantIfNotExisting() {
		Applicant a = getApplicant();
		a.setApplicantId(-2L);
		applicantController.saveApplicant(userId, projectId, a, PublicCategory.P);
//		assertThrows(BadRequestException.class, ()->applicantController.saveApplicant(userId, projectId, a, PublicCategory.P));
//		verify(eTrackApplicantService).updateApplicant(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), new Applicant(), Mockito.anyString());
	}
	
	//updateApplicant test cases
	@Test
	public void updateApplicantThrowsBREForNullApplicantId() {
		Applicant applicant = getApplicant();
		applicant.setApplicantId(null);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no reference is passed (applicant id) in the input request");
		this.applicantController.updateApplicant(userId, projectId, applicant, PublicCategory.C);
	}
	
//	@Test
//	public void updateApplicantThrowsBREForApplicantId() {
//		Applicant applicant = null;
//		exceptionRule.expect(NullPointerException.class);
////		exceptionRule.expectMessage("There is no reference is passed (applicant id) in the input request");
//		this.applicantController.updateApplicant(userId, projectId, applicant, PublicCategory.C);
//	}
	
	
	//saveApplicantDetails tests
	@Test
	public void testSaveApplicantDetailsThrowsBREForNullApplicant() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is a reference is passed (applicant id) in the input request");
		this.applicantController.saveApplicantDetails(userId, projectId, null);
	}
	
	@Test
	public void testSaveApplicantDetailsThrowsBREForApplicant() {
		Applicant applicant = getApplicant();
		applicant.setApplicantId(null);
//		exceptionRule.expect(BadRequestException.class);
//		exceptionRule.expectMessage("There is a reference is passed (applicant id) in the input request");
		this.applicantController.saveApplicantDetails(userId, projectId, applicant);
	}
	
	
	@Test
	public void testUpdateApplicantDetailsThrowsBREForNullApplicantId(){
			Applicant applicant = getApplicant();
			applicant.setApplicantId(null);
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Input request is invalid");
			this.applicantController.updateApplicantDetails(userId, projectId, applicant);
	}
	
	@Test
	public void testUpdateApplicantDetailsThrowsBREForApplicantId(){
			Applicant applicant = getApplicant();
//			applicant.setApplicantId(null);
//			exceptionRule.expect(BadRequestException.class);
//			exceptionRule.expectMessage("Input request is invalid");
			this.applicantController.updateApplicantDetails(userId, projectId, applicant);
	}
	
	
	//deleteApplicantDetails tests
	
	
	@Test
	public void testDeleteApplicantDetailsThrowsBREForInvalidApplicantId(){
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Invalid Applicant Id is passed");
		this.applicantController.deleteApplicantDetails(userId, projectId, 10L, -100L, PublicCategory.C);
	}
	
	
	@Test
	public void testDeleteApplicantDetailsSuccessfully(){
		ResponseEntity entity = new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		entity =	this.applicantController.deleteApplicantDetails(userId, projectId, 10L, 100L, PublicCategory.O);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
 }
	//deleteContactAgentByIds
	@Test
	public void testDeleteContactAgentByIdsThrowsBREForNullContactIds() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("There is no Contact/Agents passed to delete");
		this.applicantController.deleteContactAgentByIds(userId, projectId, null);
	}
	
	
	@Test
	public void testDeleteContactAgentByIdsSuccessfully() {
		List<Long> contactIds = Arrays.asList(1L, 2L, 3L, 4L);
		this.applicantController.deleteContactAgentByIds(userId, projectId, contactIds);
		verify(eTrackApplicantService).deleteContacts(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
	}
	
	
	//verifyBusiness test cases
	
	@Test
	public void testVerifyBusinessThrowsBREForInvalidLegalName() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Legal name is not passed or Less than 3 characters Passed");
		this.applicantController.verifyBusiness(userId, projectId, "te");
	}
	
	@Test
	public void testVerifyBusinessCallsApplicantService() {
		this.applicantController.verifyBusiness(userId, projectId, "test");
		verify(eTrackApplicantService).getBusinessVerified(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	
	//saveAcknowledgedApplicants test cases
	@Test
	public void testSaveAcknowledgedApplicantsThrowsBREForInvalidUserId() {
		List<Long> applicants = Arrays.asList(1L, 2L);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.applicantController.saveAcknowledgedApplicants(null, projectId, applicants);
	}
	
	@Test
	public void testSaveAcknowledgedApplicantsThrowsBREForInvalidProjectId() {
		List<Long> applicants = Arrays.asList(1L, 2L);
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
		this.applicantController.saveAcknowledgedApplicants(userId, null, applicants);
	}
	
	@Test
	public void testSaveAcknowledgedApplicantsCallsPermitService() {
		List<Long> applicants = Arrays.asList(1L, 2L);
		this.applicantController.saveAcknowledgedApplicants(userId, projectId, applicants);
		verify(eTrackApplicantService).addAcknowledgedApplicants(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}
	
	//uploadOnlineSubmitter tests
	@Test
	public void testUploadOnlineSubmitterThrowsBREForInvalidUserAndProjectId() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Project and/or User id is blank or invalid value is passed");
		this.applicantController.uploadOnlineSubmitter(null, 10L, -100L, 5L);
	}
	
	@Test
	public void testUploadOnlineSubmitterCallsApplicantService() {
		this.applicantController.uploadOnlineSubmitter(userId, 10L, projectId, 5L);
		verify(eTrackApplicantService).updateOnlineSubmitter(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
	}
	
	
	
	//updatecontactAgentDetails test cases
	@Test
	public void updateContactAgentDetailsCallsServiceToUpdateContact() {
		Applicant applicant = getApplicant();
		applicant.setPropertyRelationships(null);
		this.applicantController.updatecontactAgentDetails(userId, projectId, applicant);
		verify(eTrackApplicantService).updateApplicant(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.anyString());
	}

	//saveContactAgentDetails
	
	@Test
	public void saveContactAgentDetailsCallsServiceToSaveDetails() {
		Applicant applicant = getApplicant();
		applicant.setPropertyRelationships(null);
		this.applicantController.savecontactAgentDetails(userId, projectId, applicant);
		verify(eTrackApplicantService).saveApplicant(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any());
	}
	
	
	//private methods
	
	private Applicant getApplicant() {
		Applicant applicant = new Applicant();
		applicant.setPublicTypeCode("Public code");
		applicant.setApplicantId(100L);
		applicant.setAddress(getApplicantAddressObj());
		applicant.setContact(getContactObj());
		applicant.setPropertyRelationships(Arrays.asList(1));
		return applicant;
		
	}
	
	private ApplicantAddress getApplicantAddressObj() {
		ApplicantAddress address = new ApplicantAddress();
		address.setAddressId(10L);
		address.setStreetAdr1("Pearl st");
		address.setAdrType(1);
		address.setCity("Albany");
		address.setZipCode("12207");
		address.setState("Nw York");
		address.setPostalCode("12222");
		return address;
	}
	
	private Contact getContactObj() {
		Contact contact = new Contact();
		contact.setCellNumber("5188941123");
		return contact;
	}
	
}
