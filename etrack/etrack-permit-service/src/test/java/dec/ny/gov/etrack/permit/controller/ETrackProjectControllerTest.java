package dec.ny.gov.etrack.permit.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.AssignmentNote;
import dec.ny.gov.etrack.permit.model.ProjectDetail;
import dec.ny.gov.etrack.permit.service.impl.ProjectServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackProjectControllerTest {

	
	@InjectMocks
	private ETrackProjectController projectController;
	
	@Mock
	private ProjectServiceImpl projectService;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	
	
	private String userId = "jxpuvoge";
	private String contextId = "context1234";
	private String token = "123456";
	private Long projectId = 1002L;
	
	
		//Save Project Details test
		@Test
		public void testSaveProjectDetailsWithContextIdCallsService() {
			ProjectDetail projDetail = this.getProjectDetail();
			projectController.saveProjectDetails(userId, contextId, projDetail);
			 verify(projectService).saveProject(Mockito.anyString(),Mockito.anyString(), Mockito.any(ProjectDetail.class));
		}
		
		@Test
		public void testSaveProjectDetailsWithoutContextIdCallsService() {
			ProjectDetail projDetail = this.getProjectDetail();
			projectController.saveProjectDetails(userId, null, projDetail);
			verify(projectService,  times(1)).saveProject(Mockito.anyString(),Mockito.anyString(), Mockito.any(ProjectDetail.class));
		}
		
		
		//update Project Details tests
		
		@Test
		public void testUpdateProjectDetailThrowsBadRequestForNullProjectId(){
			ProjectDetail projectDetail = getProjectDetail();
			projectDetail.setProjectId(null);
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project Id is not passed");
			projectController.updateProjectDetails(userId, contextId, token, projectDetail);
		}
			
		
		@Test
		public void testUpdateProjectDetailThrowsBadRequestForNegativeProjectId(){
			ProjectDetail projectDetail = getProjectDetail();
			projectDetail.setProjectId(-10L);
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project Id is not passed");
			projectController.updateProjectDetails(userId, contextId, token, projectDetail);
			
		}
		
		@Test
		public void testUpdateProjectDetailWithNoContextID(){
			ProjectDetail projectDetail = getProjectDetail();
			projectDetail.setProjectId(1002L);
			projectController.updateProjectDetails(userId, null, token, projectDetail);
		}
		
		@Test
		public void testUpdateProjectDetailCallsServiceToUpdateDetails(){
			ProjectDetail projectDetail = getProjectDetail();
			projectDetail.setProjectId(1002L);
			projectController.updateProjectDetails(userId, contextId, token, projectDetail);
			verify(projectService).updateProject(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
			
		}
		
		
		//assign Project To Analyst tests
		
		
		@Test
		public void testAssignProjectToAnalystThrowsExceptionWithNoProjectId() {
			exceptionRule.expect(BadRequestException.class);
			AssignmentNote assignmentNote = getAssignmentNote();
			projectController.assignProjectToAnalyst(userId, null, assignmentNote);
		}
		
		@Test
		public void testAssignProjectToAnalystThrowsExceptionWithNegativeProjectId() {
			exceptionRule.expect(BadRequestException.class);
			AssignmentNote assignmentNote = getAssignmentNote();
			projectController.assignProjectToAnalyst(userId, -10L, assignmentNote);
		}
		
		@Test
		public void testAssignProjectToAnalystThrowsExceptionForNoAnalystName() {
			exceptionRule.expect(BadRequestException.class);
			AssignmentNote assignmentNote = getAssignmentNote();
			assignmentNote.setAnalystName(null);
			projectController.assignProjectToAnalyst(userId, projectId, assignmentNote);
		}
		
		@Test
		public void testAssignProjectToAnalystThrowsExceptionForNoAnalystId() {
			AssignmentNote assignmentNote = getAssignmentNote();
			assignmentNote.setAnalystId(null);
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("User Assigned details cannot be empty");
			projectController.assignProjectToAnalyst(userId, projectId, assignmentNote);
		}
		
		@Test
		public void testAssignProjectToAnalystSuccessfully() {
			AssignmentNote assignmentNote = getAssignmentNote();
			projectController.assignProjectToAnalyst(userId, projectId, assignmentNote);
		}
		
		
		
		// Get Project Details tests
		
		@Test
		public void testGetProjectDetailsCallsServiceToRetrieveDetails() {
			projectController.getProjectDetails(userId, contextId, projectId);
			verify(projectService).retrieveProjectDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
		}
		
		@Test
		public void testGetProjectDetailsThrowsExceptionForNullProjectId() {
			exceptionRule.expect(BadRequestException.class);
			projectController.getProjectDetails(userId, contextId, null);
		}
		
		@Test
		public void testGetProjectDetailsThrowsExceptionForNegativeProjectId() {
			exceptionRule.expect(BadRequestException.class);
			projectController.getProjectDetails(userId, contextId, -10L);
		}
		
		//Get Project Permit Status tests
		@Test
		public void testGetProjectPermitStatusCallsService() {
			projectController.getProjectPermitStatus(userId, projectId, 1);
			verify(projectService).getProjectPermitStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt());
		}
		
		//rejectProjectByValidator tests
		
		@Test 
		public void testRejectProjectbyValidatorThrowsBREForNullUserId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project and/or User id is blank or invalid value is passed");
			projectController.rejectProjectByValidator(null, projectId, "Reject");
		}
		
		@Test 
		public void testRejectProjectbyValidatorThrowsBREForInvalidReason() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Rejeect reason is not provided");
			projectController.rejectProjectByValidator(userId, projectId, "");
		}
		
		@Test 
		public void testRejectProjectbyValidatorCallsServiceToRejectProjValidation() {
			projectController.rejectProjectByValidator(userId, projectId, "Rejected");
			verify(projectService).rejectProjectValidation(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString());
		}
		
	
		
		//deletePrject tests
		@Test
		public void testDeleteProjectThrowsBREForInvalidUserId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
			projectController.deleteProject(null, token, projectId);
		}
		
		@Test
		public void testDeleteProjectThrowsBREForInvalidProjectId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project and/or User id is blank or invalid passed");
			projectController.deleteProject(userId, token, null);
		}
		
		@Test
		public void testDeleteProjectCallsProjectServiceToDeleteProject() {
			projectController.deleteProject(userId, token, projectId);
			verify(projectService, times(1)).deleteProject(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
		}
	
		//associateGeographicalInquiryToProject test cases
		@Test
		public void testAssociateGeographicalInquiryToProjectThrowsBREForInvalidInquiryAndProjectId() {
			exceptionRule.expect(BadRequestException.class);
			exceptionRule.expectMessage("Project Id and Inquiry id is Required to associate. Either one of them is missing.");
			projectController.associateGeographicalInquiryToProject(userId, null, null);
		}
		
		@Test
		public void testAssociateGeographicalInquiryToProjectCallsService() {
			projectController.associateGeographicalInquiryToProject(userId, projectId, 1L);
			verify(projectService).associateGeographicalInquiryToProject(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
		}


		private ProjectDetail getProjectDetail() {
			ProjectDetail projectDetail = new ProjectDetail();
			projectDetail.setProjectId(1002L);
			return projectDetail;
		}
	

		private AssignmentNote getAssignmentNote() {
			AssignmentNote note = new AssignmentNote();
			note.setAnalystId("Analyst1");
			note.setAnalystName("John Doe");
			note.setComments("comment");
			return note;
		}
}
