package dec.ny.gov.etrack.permit.controller;

import static org.junit.Assert.assertNotNull;

import org.assertj.core.internal.ObjectArrayElementComparisonStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.UnexpectedRollbackException;

import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.exception.DataExistException;
import dec.ny.gov.etrack.permit.exception.DataNotFoundException;
import dec.ny.gov.etrack.permit.exception.ETrackPermitException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ETrackPermitExceptionControllerTest {
	

	@InjectMocks
	private ETrackPermitExceptionController eTrackPermitExceptionController;

	@Test
	public void testHandleBadRequestException() {
		assertNotNull(eTrackPermitExceptionController.handleBadRequestException(new BadRequestException(null, null, eTrackPermitExceptionController)));
	}
	
	@Test
	public void testHandleBadRequestExceptionWithErrorCode() {
		BadRequestException bre = new BadRequestException();
		bre.setErrorCode("400");
		bre.setErrorMessage("Bad Request");
		bre.setObject(new Object());
		Object request = bre.getObject();
		assertNotNull(eTrackPermitExceptionController.handleBadRequestException(new BadRequestException("400", "Invalid", request)));
	}
	
	@Test
	public void testHandleDataNotFoundException() {
		assertNotNull(eTrackPermitExceptionController.handleDataNotFoundException(new DataNotFoundException()));
	}
	
	@Test
	public void testHandleETrackPermitException() {
		assertNotNull(eTrackPermitExceptionController.handleETrackPermitException(new ETrackPermitException()));
	}
	
	@Test
	public void testHandleETrackPermitExceptionwithErrorCode() {
		assertNotNull(eTrackPermitExceptionController.handleETrackPermitException(new ETrackPermitException(HttpStatus.NO_CONTENT,"","")));
	}
		
	
	@Test
	public void testHandleDataExistException() {
		assertNotNull(eTrackPermitExceptionController.
				handleDataExistException(new DataExistException()));
	}
	

	@Test
	public void testHandleDBDataRollbackException() {
		assertNotNull(eTrackPermitExceptionController
				.handleDBDataRollbackException(new UnexpectedRollbackException(null)));
	}
}
