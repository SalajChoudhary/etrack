package dec.ny.gov.etrack.gis.util;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.FacilityAddress;
import dec.ny.gov.etrack.gis.model.FacilityDetail;
import dec.ny.gov.etrack.gis.model.ProjectDetail;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidatorTest {
	
	private String userID = "Jxpuvoge";
	private String contextId ="1234";
	
	
	@Test(expected = BadRequestException.class)
	public void testIsProjectValidThrowsBadRequestForNoMailInd() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
		projectDetail.setMailInInd(null);
		Validator.isProjectValid(userID, contextId, projectDetail);
	}
	
	@Test(expected = BadRequestException.class)
	public void testIsProjectValidThrowsBadRequestForNullTypeCode() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
		projectDetail.setApplicantTypeCode(null);
		Validator.isProjectValid(userID, contextId, projectDetail);
	}
	
	@Test(expected = BadRequestException.class)
	public void testIsProjectValidThrowsBadRequestForZeroTypeCode() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
		projectDetail.setApplicantTypeCode(0);
		Validator.isProjectValid(userID, contextId, projectDetail);
	}
	
	@Test(expected = BadRequestException.class)
	public void testIsProjectValidThrowsBadRequestForNoFacility() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
		projectDetail.setFacility(null);
		Validator.isProjectValid(userID, contextId, projectDetail);
	}
	
	@Test(expected = BadRequestException.class)
	public void testIsProjectValidThrowsBadRequestForNoAddress() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
		projectDetail.getFacility().setAddress(null);
		Validator.isProjectValid(userID, contextId, projectDetail);
	}
	
	@Test
	public void testIsProjectValidSuccessfully() {
		ProjectDetail projectDetail = getValidProjectDetailObj();
	boolean isValid =	Validator.isProjectValid("Jxpuvoge", contextId, projectDetail);
	assertTrue(isValid);
	}
	

	
//	//private methods
//	
	private ProjectDetail getValidProjectDetailObj() {
		ProjectDetail projectDetail = new ProjectDetail();
		projectDetail.setMailInInd(10);
		projectDetail.setApplicantTypeCode(100);
		projectDetail.setFacility(getFacilityObj());
		projectDetail.setProjectId(300L);
		return projectDetail;
	}
//	
	private FacilityDetail getFacilityObj() {
		FacilityDetail facilityDetail = new FacilityDetail();
		facilityDetail.setAddress(getFacilityAddressObj());
		return facilityDetail;
	}
//	
	private FacilityAddress getFacilityAddressObj() {
	FacilityAddress facilityAddress = new FacilityAddress();
	facilityAddress.setCity("Albany");
	facilityAddress.setState("New York");
	return facilityAddress;
	
	}

}
