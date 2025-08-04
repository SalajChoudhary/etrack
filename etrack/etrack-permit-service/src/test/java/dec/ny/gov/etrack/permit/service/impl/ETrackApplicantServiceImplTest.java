package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import dec.ny.gov.etrack.permit.dao.ETrackPermitDAO;
import dec.ny.gov.etrack.permit.entity.Address;
import dec.ny.gov.etrack.permit.entity.ProjectActivity;
import dec.ny.gov.etrack.permit.entity.Public;
import dec.ny.gov.etrack.permit.entity.Role;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.BusinessInformation;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameInfo;
import dec.ny.gov.etrack.permit.model.BusinessLegalNameResponse;
import dec.ny.gov.etrack.permit.model.BusinessVerificationResponse;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.repo.AddressRepo;
import dec.ny.gov.etrack.permit.repo.ApplicationRepo;
import dec.ny.gov.etrack.permit.repo.ProjectActivityRepo;
import dec.ny.gov.etrack.permit.repo.PublicRepo;
import dec.ny.gov.etrack.permit.repo.RoleRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackApplicantServiceImplTest {
	
	@InjectMocks
	private ETrackApplicantServiceImpl applicantService;
	
	@Mock
	private TransformationService transformationService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Mock
	private PublicRepo publicRepo;
	
	
	@Mock
	private AddressRepo addressRepo;
	
	@Mock
	private ApplicationRepo applicationRepo;
	
	@Mock
	private RoleRepo roleRepo;
	
	@Mock
	private ProjectActivityRepo projectActivityRepo;
	
	@Mock
	private ETrackPermitDAO eTrackPermitDAO;
	
	@Mock
	private RestTemplate businessVerificationRestTemplate;
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private Long projectId = 1002L;
	private String legalName = "John Doe";
	private String category = "Cat";


	
	//deleteApplicant Test cases:
	@Test
	public void testDeleteApplicantThrowsBREForInvalidPublicId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no public associated with the public id");
		when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "cat");
	}
	
	@Test
	public void testDeleteApplicantLogicallyDeletesPublic() {
		when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getPublicsList());
		applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "P");
		verify(publicRepo, times(1)).updateSeletedInETrackInd(Mockito.anyLong(), Mockito.anyList());
	}
	
	@Test
	public void testDeleteApplicantDeletesPublic() {
		//remove edbPublicId to physically delete public
		 List<Public> publics = new ArrayList<>();
		   Public public1 = new Public();
		   public1.setDisplayName("Name");
		   public1.setEdbPublicId(null);
//		   public1.setRoles(getRolesList());
		   public1.setPublicId(10L);
		   publics.add(public1);
		when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(publics);
		applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "P");
		verify(publicRepo).delete(Mockito.any());
	}
	
	@Test
	public void testDeleteApplicantDeletesApplicants() {
		 List<Public> publics = new ArrayList<>();
		   Public public1 = new Public();
		   public1.setDisplayName("Name");
		   public1.setEdbPublicId(null);
		   List<Role> rolesList = new ArrayList<>();
		   Role role2 = new Role();
		   role2.setRoleTypeId(6);
		   role2.setBeginDate(new Date());
		   role2.setAddressId(10L);
		   role2.setCreatedById(userId);
		   rolesList.add(role2);
//		   public1.setRoles(rolesList);
		   public1.setPublicId(10L);
		   publics.add(public1);
		when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(publics);
		applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "P");
		verify(publicRepo).delete(Mockito.any());
	}
	
	@Test
	public void testDeleteApplicantDeletesPublicsRole() {
		 List<Public> publics = new ArrayList<>();
		   Public public1 = new Public();
		   public1.setDisplayName("Name");
		   public1.setEdbPublicId(null);
		   List<Role> rolesList = new ArrayList<>();
		   Role role1 = new Role();
		   role1.setRoleTypeId(7);
		   role1.setRoleId(2L);
		   role1.setBeginDate(new Date());
		   role1.setAddressId(10L);
		   role1.setCreatedById(userId);
		   rolesList.add(role1);
		   Role role2 = new Role();
		   role2.setRoleTypeId(6);
		   role2.setRoleId(3L);
		   role2.setBeginDate(new Date());
		   role2.setAddressId(10L);
		   role2.setCreatedById(userId);
		   rolesList.add(role2);
//		   public1.setRoles(rolesList);
		   public1.setPublicId(10L);
		   publics.add(public1);
		when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(publics);
		applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "P");
		verify(roleRepo).deleteRoleById(Mockito.anyLong());
	}
	
	@Test
	public void testDeleteApplicantDeletesOwner() {
		 List<Public> publics = new ArrayList<>();
		   Public public1 = new Public();
		   public1.setDisplayName("Name");
		   public1.setEdbPublicId(null);
		   List<Role> rolesList = new ArrayList<>();
		   Role role1 = new Role();
		   role1.setRoleTypeId(7);
		   role1.setRoleId(2L);
		   role1.setBeginDate(new Date());
		   role1.setAddressId(10L);
		   role1.setCreatedById(userId);
		   rolesList.add(role1);
		   Role role2 = new Role();
		   role2.setRoleTypeId(6);
		   role2.setRoleId(3L);
		   role2.setBeginDate(new Date());
		   role2.setAddressId(10L);
		   role2.setCreatedById(userId);
		   rolesList.add(role2);
//		   public1.setRoles(rolesList);
		   public1.setPublicId(10L);
		   publics.add(public1);
		   when(publicRepo.findByPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(publics);
		   applicantService.deleteApplicant(userId, contextId, projectId, 1L, 2L, "O");
		   verify(publicRepo).delete(Mockito.any());
	}
	
	//deleteContacts test cases
	@Test
	public void testDeleteContactsThrowsBREForInvalidContactIds() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Contacts/Agents associated with these input contact Ids");
		when(publicRepo.findAllPublicByIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(null);
		applicantService.deleteContacts(userId, contextId, projectId, getContactIdList());
	}
	
	
	@Test
	public void testDeleteContactsThrowsBREForInvalidNumberOfContactIds() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("One or More Contact/Agent is not avaialble for the input contact Ids");
		List<Long> ids = getContactIdList();
		ids.add(9L);
		when(publicRepo.findAllPublicByIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(getPublicsList());
		applicantService.deleteContacts(userId, contextId, projectId, ids);
	}
	
	@Test
	public void testDeleteContactsLogicallyDeletesContact() {
		List<Long> contactIdList = Arrays.asList(1L);
		List<Public> publics = getPublicsList();
//		publics.get(0).getRoles().get(0).setRoleId(100L);
		when(publicRepo.findAllPublicByIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(publics);
		applicantService.deleteContacts(userId, contextId, projectId, contactIdList);
		verify(publicRepo).updateSeletedInETrackInd(Mockito.anyLong(), Mockito.anyList());
	}
	
	@Test
	public void testDeleteContactsDeletesContact() {
		List<Long> contactIdList = Arrays.asList(1L);
		List<Public> publics = getPublicsList();
		Public public1 = publics.get(0);
		public1.setEdbPublicId(null);
//		public1.getRoles().get(0).setRoleId(100L);
		when(publicRepo.findAllPublicByIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(publics);
		applicantService.deleteContacts(userId, contextId, projectId, contactIdList);
		verify(publicRepo).delete(Mockito.any());
	}
	
	//addApplicant test cases
	@Test
	public void testAddApplicantThrowsBREForExistingPublic() {
		expectedException.expect(DataExistException.class);
		expectedException.expectMessage("Public name is already exist");
		Applicant applicant = getApplicantObj();
		List<Public> publics = getPublicsList();
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(publics);
		this.applicantService.addApplicant(userId, contextId, projectId, applicant, category);
	}
	
	@Test
	public void testAddApplicantForContactAgent() {
		Applicant applicant = getApplicantObj();
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		Applicant result = applicantService.addApplicant(userId, contextId, projectId, applicant, "C");
		assertEquals(123L, result.getEdbApplicantId());
		assertEquals("1234567", result.getContact().getCellNumber());
		assertEquals(1L, result.getAddress().getAddressId());
	}
	
	@Test
	public void testAddApplicantForOwner() {
		Applicant applicant = getApplicantObj();
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		Applicant result = applicantService.addApplicant(userId, contextId, projectId, applicant, "O");
		assertEquals(123L, result.getEdbApplicantId());
		assertEquals("1234567", result.getContact().getCellNumber());
		assertEquals(1L, result.getAddress().getAddressId());
	}
	
	@Test
	public void testAddApplicantForP() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(0L);
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		assertThrows(ETrackPermitException.class,()->applicantService.addApplicant(userId, contextId, projectId, applicant, "P"));
	}
	
	@Test
	public void testAddApplicantForBrequest() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(0L);
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		assertThrows(BadRequestException.class,()->applicantService.addApplicant(userId, contextId, projectId, applicant, "p"));
//		assertEquals(123L, result.getEdbApplicantId());
//		assertEquals("1234567", result.getContact().getCellNumber());
//		assertEquals(1L, result.getAddress().getAddressId());

	}
	
	@Test
	public void testAddApplicantForC() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(0L);
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		assertThrows(ETrackPermitException.class,()->applicantService.addApplicant(userId, contextId, projectId, applicant, "C"));

	}
	
	@Test
	public void testAddApplicantForEmptyList() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(0L);
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		when(publicRepo.findExistingContacts(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(1L));
		assertThrows(DataExistException.class,()->applicantService.addApplicant(userId, contextId, projectId, applicant, "C"));

	}
	
	@Test
	public void testAddApplicantForList() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(0L);
		when(publicRepo.findByEdbPublicIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		when(addressRepo.findByPublicId(Mockito.anyLong())).thenReturn(1L);
		when(eTrackPermitDAO.populateApplicantDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(2L);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForContact(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
		when(publicRepo.findExistingContacts(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(Arrays.asList());
		assertThrows(ETrackPermitException.class,()->applicantService.addApplicant(userId, contextId, projectId, applicant, "C"));

	}
	
	
	
	
	
	@Test
	public void testAddApplicantAddsNewApplicant() {
		Applicant applicant = getApplicantObj();
		applicant.setEdbApplicantId(null);
		Address address = getAddressObj();
		Public public1 = getPublicObj();
		public1.setPublicId(100L);
		when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(address);
		when(addressRepo.save(Mockito.any())).thenReturn(address);
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		when(publicRepo.save(Mockito.any())).thenReturn(public1);
		Applicant result = applicantService.addApplicant(userId, contextId, projectId, applicant, "O");
		assertEquals(100L, result.getApplicantId());
		assertNull(result.getEdbApplicantId());
	
	}
	
	
	
	
	//updateApplicant test cases:
	@Test
	public void testUpdateApplicantThrowsDNFEForNoPublicFound() {
		expectedException.expect(DataNotFoundException.class);
		expectedException.expectMessage("No public is available for the Public Id");
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		applicantService.updateApplicant(userId, contextId, projectId, getApplicantObj(), "P");
	}
	
	@Test
	public void testUpdateApplicantThrowsBREForExistingAddress() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Input request is invalid");
		Public public1 = new Public();
		public1.setBusinessValidatedInd(1);
		public1.setCreatedById(userId);
		public1.setLastName("Smith");
		Applicant applicant = getApplicantObj();
		applicant.setApplicantId(20L);
		applicant.setPublicTypeCode("Type");
		applicant.getAddress().setAddressId(null);
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(public1));
		when(addressRepo.findAddressExistsForPublic(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		applicantService.updateApplicant(userId, contextId, projectId, applicant, "P");
	}

	
	@Test
	public void testUpdateApplicant() {
		Applicant applicant = getApplicantObj();
		applicant.setApplicantId(1L);
		applicant.getAddress().setAddressId(null);
		applicant.setPublicTypeCode("code");
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(getPublicObj()));
		when(addressRepo.findAddressExistsForPublic(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList());
		when(addressRepo.save(Mockito.any())).thenReturn(getAddressObj());
		when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getPublicObj());
		when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
		Applicant result =	applicantService.updateApplicant(userId, contextId, projectId, applicant, "P");
		assertEquals(1, result.getApplicantId());
		assertEquals(123, result.getEdbApplicantId());
		assertEquals(10, result.getAddress().getEdbAddressId());
	
	}
	
	@Test
	public void testUpdateApplicantThrowsBREForInvalidAddressId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("There is no Address existing for the input Address id");
		Public public1 = new Public();
		public1.setBusinessValidatedInd(1);
		public1.setCreatedById(userId);
		public1.setLastName("Smith");
		Applicant applicant = getApplicantObj();
		applicant.setApplicantId(20L);
		applicant.setPublicTypeCode("Type");
		when(publicRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(public1));
		when(addressRepo.findAddressExistsForPublic(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.empty());
		applicantService.updateApplicant(userId, contextId, projectId, applicant, "P");
	}
	
	@Test
	public void testUpdateApplicantThrowsBREForInvalidApplicantId() {
		expectedException.expect(BadRequestException.class);
		expectedException.expectMessage("Input request is invalid");
		Public public1 = new Public();
		public1.setBusinessValidatedInd(1);
		public1.setCreatedById(userId);
		public1.setLastName("Smith");
		Applicant applicant = getApplicantObj();
		applicant.setApplicantId(20L);
		applicant.setPublicTypeCode("Type");
		doReturn(Optional.of(public1)).doReturn(Optional.empty()).when(publicRepo).findById(Mockito.anyLong());
		when(addressRepo.findAddressExistsForPublic(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Arrays.asList(1L));
		when(addressRepo.findByAddressId(Mockito.anyLong())).thenReturn(Optional.of(getAddressObj()));
		applicantService.updateApplicant(userId, contextId, projectId, applicant, "P");
	}


	//getBusinessVerified tests
	

		@Test
		public void testGetBusinessVerifiedReturnsCorrectNameAndOkStatus() {
			BusinessLegalNameResponse responseBody = getBusinessLegalNameResponseObj();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.OK);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenReturn(bizResponseEntity);
			ReflectionTestUtils.setField(applicantService, "akanaAuthToken", "test");
			ResponseEntity result =	(ResponseEntity) applicantService.getBusinessVerified(userId, contextId, legalName);
			assertEquals(Arrays.asList(legalName), result.getBody());
			assertEquals(HttpStatus.OK, result.getStatusCode());
		}
		
		@Test
		public void testGetBusinessVerifiedReturnsNoContentStatus() {
			BusinessLegalNameResponse responseBody = getBusinessLegalNameResponseObj();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.NO_CONTENT);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenReturn(bizResponseEntity);
			ReflectionTestUtils.setField(applicantService, "akanaAuthToken", "test");
			ResponseEntity result =	(ResponseEntity) applicantService.getBusinessVerified(userId, contextId, legalName);
			assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
		}
		
		@Test
		public void testGetBusinessVerifiedReturnsNoContentIfNoNamesReturned() {
			BusinessLegalNameResponse responseBody = new BusinessLegalNameResponse();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.OK);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenReturn(bizResponseEntity);
			ReflectionTestUtils.setField(applicantService, "akanaAuthToken", "test");
			ResponseEntity result =	(ResponseEntity) applicantService.getBusinessVerified(userId, contextId, legalName);
			assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
		}
		
		@Test
		public void testGetBusinessVerifiedReturnsAcceptedIfNamesDoNotMatch() {
			BusinessLegalNameResponse responseBody = getBusinessLegalNameResponseObj();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.OK);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenReturn(bizResponseEntity);
			ReflectionTestUtils.setField(applicantService, "akanaAuthToken", "test");
			ResponseEntity result =	(ResponseEntity) applicantService.getBusinessVerified(userId, contextId, "Steve");
			assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
		}
		
		@Test
		public void testGetBusinessVerifiedReturnsInternalServerErrorForUnauthorizedRequest() {
			BusinessLegalNameResponse responseBody = getBusinessLegalNameResponseObj();
			ResponseEntity<BusinessLegalNameResponse> bizResponseEntity = new ResponseEntity<BusinessLegalNameResponse>(responseBody, HttpStatus.OK);
			when(businessVerificationRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), ArgumentMatchers.<Class<BusinessLegalNameResponse>>any())).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
			ReflectionTestUtils.setField(applicantService, "akanaAuthToken", "test");
			ResponseEntity result =	(ResponseEntity) applicantService.getBusinessVerified(userId, contextId, "Steve");
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}

			
		
		//addAcknowledgedApplicants test cases
		@Test
		public void testAddAcknowledgedApplicantsWithNoSignedApplicantsThrowsBREForMissingPublic() {
			expectedException.expect(BadRequestException.class);
			expectedException.expectMessage("There is no Publics to acknowledge or one or more Public is missing");
			when(publicRepo.findAllExcludedPublicIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1l));
			when(publicRepo.updateAllAcknowledgedApplicants(Mockito.anyString(), Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt())).thenReturn(1);
			applicantService.addAcknowledgedApplicants(userId, contextId, projectId, Arrays.asList(1l,2l));
		}
		
		@Test
		public void testAddAcknowledgedApplicantsWithNoSignedApplicantsThrowsBREForIncompleteProject() {
			expectedException.expect(BadRequestException.class);
			expectedException.expectMessage("This project is incomplete or not available");
			when(publicRepo.findAllExcludedPublicIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1l));
			when(publicRepo.updateAllAcknowledgedApplicants(Mockito.anyString(), Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt())).thenReturn(1);
			when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
			when(projectActivityRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(getProjectActivityList());
			applicantService.addAcknowledgedApplicants(userId, contextId, projectId, Arrays.asList());
		}
		
		@Test
		public void testAddAcknowledgedApplicantsWithNoSignedApplicantsSavesProjectActivity() {
			List<ProjectActivity> projectActivities = getProjectActivityList();
			ProjectActivity activity = new ProjectActivity();
			ProjectActivity activity2 = new ProjectActivity();
			ProjectActivity activity3 = new ProjectActivity();
			activity.setCreatedById(userId);
			activity2.setCreatedById(userId);
			activity3.setCreatedById(userId);	
			projectActivities.add(activity);
			projectActivities.add(activity2);
			projectActivities.add(activity3);
			when(publicRepo.findAllExcludedPublicIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1l));
			when(publicRepo.updateAllAcknowledgedApplicants(Mockito.anyString(), Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt())).thenReturn(1);
			when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(Arrays.asList());
			when(projectActivityRepo.findAllByProjectId(Mockito.anyLong())).thenReturn(projectActivities);
			applicantService.addAcknowledgedApplicants(userId, contextId, projectId, Arrays.asList());
			verify(projectActivityRepo).save(Mockito.any());
		}
		
	
		@Test
		public void testAddAcknowledgedApplicantsWithSignedApplicants() {
			when(publicRepo.findAllExcludedPublicIds(Mockito.anyLong(), Mockito.anyList())).thenReturn(Arrays.asList(1l));
			when(publicRepo.updateAllAcknowledgedApplicants(Mockito.anyString(), Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt())).thenReturn(1);
			when(projectActivityRepo.findAllByProjectIdAndActivityStatusId(Mockito.anyLong(), Mockito.anyInt())).thenReturn(getProjectActivityList());
			applicantService.addAcknowledgedApplicants(userId, contextId, projectId, Arrays.asList(1l));
		}
		
		//saveApplicant test cases:
		
		@Test
		public void  testSaveApplicant() {
			Applicant applicant = getApplicantObj();
			applicant.setApplicantId(10L);
			applicant.setDisplayName("John");
			Address address = getAddressObj();
			Public public1 = getPublicObj();
			public1.setPublicId(502L);
			when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(public1);
			when(publicRepo.findByDisplayNameAndProjectId(Mockito.anyString(), Mockito.anyLong())).thenReturn(null);
			when(transformationService.transformInputAddressToEntityAddress(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(getAddressObj());
			when(transformationService.transformPropertyRelationshipToRoleEntity(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(getRolesList());
			when(addressRepo.save(Mockito.any())).thenReturn(address);
			when(publicRepo.save(Mockito.any())).thenReturn(public1);
			Applicant result =	applicantService.saveApplicant(userId, contextId, projectId, applicant);
			assertEquals("John", result.getDisplayName());
			assertEquals(502L, result.getApplicantId());
			assertEquals(123L, result.getEdbApplicantId());
			assertEquals("j@yahoo.com", result.getContact().getEmailAddress());
		}
		
		@Test
		public void  testSaveApplicantThrowsBREForExistingPublicName() {
			Applicant applicant = getApplicantObj();
			applicant.setApplicantId(10L);
			Public public1 = getPublicObj();
			public1.setFirstName("John");
			public1.setPublicId(10L);
			public1.setDisplayName("John");
			expectedException.expect(DataExistException.class);
			expectedException.expectMessage("Public name is already exist");
			List<Public> publics = new ArrayList<>();
			publics.add(public1);
			
			when(transformationService.transformApplicantToPublicEntity(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(public1);
			when(publicRepo.findByDisplayNameAndProjectId(Mockito.anyString(), Mockito.anyLong())).thenReturn(publics);			
			applicantService.saveApplicant(userId, contextId, projectId, applicant);
			
		}	
	
	private Address getAddressObj() {
		Address address = new Address();
		address.setAddressId(1L);
		address.setAttentionName("ATTENTION");
		return address;
	}
	
	
	private BusinessLegalNameResponse getBusinessLegalNameResponseObj() {
		BusinessLegalNameResponse responseBody = new BusinessLegalNameResponse();
		responseBody.setLegalNameInformation(getBizLegalNameInfoObj());
		responseBody.setResponseInfo(getBizVerificationResponseObj());
		return responseBody;
	}
	
	private BusinessVerificationResponse getBizVerificationResponseObj() {
		BusinessVerificationResponse response = new BusinessVerificationResponse();
		response.setCustTransactionId("ID");
		response.setErrorMessage("ERROR");
		response.setResponseMessage("Success");
		return response;
	}
	
	
	private BusinessLegalNameInfo getBizLegalNameInfoObj(){
		BusinessLegalNameInfo info = new BusinessLegalNameInfo();
		info.setBusinessInformation(getBusinessInformationList());
		return info;
	}
	
	private List<BusinessInformation> getBusinessInformationList(){
		BusinessInformation information = new BusinessInformation();
		information.setCounty("Albany");
		information.setDosId("1234");
		information.setLegalName(legalName);
		information.setFilingDate("12/23/2023");
		return Arrays.asList(information);
	}
	
	
	private List<ProjectActivity> getProjectActivityList(){
		List<ProjectActivity> activities = new ArrayList<>();
		ProjectActivity activity = new ProjectActivity();
		activities.add(activity);
		return activities;
	}

	
	
	
	//private methods
	private Applicant getApplicantObj() {
	Applicant applicant = new Applicant();
	applicant.setAddress(getApplicantAddressObj());
	applicant.setContact(getContactObj());
	applicant.setEdbApplicantId(123L);
	return applicant;
}

private ApplicantAddress getApplicantAddressObj() {
	ApplicantAddress applicantAddress = new ApplicantAddress();
	applicantAddress.setState("New York");
	applicantAddress.setStreetAdr1("PEarl St");
	applicantAddress.setStreetAdr2("State St");
	applicantAddress.setAddressId(10L);
	applicantAddress.setAddressId(1L);
	applicantAddress.setEdbAddressId(10L);
	return applicantAddress;
	
}

	private Contact getContactObj() {
		Contact contact = new Contact();
		contact.setCellNumber("1234567");
		contact.setEmailAddress("j@yahoo.com");
		contact.setHomePhoneNumber("56678899");
		contact.setWorkPhoneNumber("987654");
		return contact;
	}
	
	private List<Public> getPublicsList(){
		   List<Public> publics = new ArrayList<>();
		   Public public1 = new Public();
		   public1.setDisplayName("Name");
		   public1.setEdbPublicId(120L);
//		   public1.setRoles(getRolesList());
		   public1.setPublicId(10L);
		   publics.add(public1);
		   return publics;
	} 
	private Public getPublicObj() {
			Public publicObj = new Public();
			return publicObj;
	}

	private List<Role> getRolesList(){
		   List<Role> roles = new ArrayList<>();
		   roles.add(getRoleObj());
		   return roles;
	}
	
	private Role getRoleObj() {
		   Role role = new Role();
		   role.setRoleTypeId(10);
		   role.setAddressId(100L);
		   return role;
	}
	private List<Long> getContactIdList(){
		List<Long> ids = new ArrayList<>();
		ids.add(1L);
		return ids;
	}
}
