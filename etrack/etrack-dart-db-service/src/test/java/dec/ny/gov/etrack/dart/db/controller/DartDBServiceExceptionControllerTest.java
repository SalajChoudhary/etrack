package dec.ny.gov.etrack.dart.db.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.exception.DartDBException;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class DartDBServiceExceptionControllerTest {

	@InjectMocks
	private DartDBServiceExceptionController dartDBServiceExceptionController;
	

	@Test
	public void handleDataNotFoundExceptionTest() {
		assertNotNull(dartDBServiceExceptionController.handleDataNotFoundException(new NoDataFoundException()));
	}
	
	@Test
	public void handleBadRequestExceptionTest() {
		assertNotNull(dartDBServiceExceptionController.handleBadRequestException(new BadRequestException()));
	}
	
	@Test
	public void handleDartDBExceptionTest() {
		assertNotNull(dartDBServiceExceptionController.handleDartDBException(new DartDBException()));
	}
	
	@Test
	public void handleDartDBExceptionWithHttpStatusTest() {
		assertNotNull(dartDBServiceExceptionController.handleDartDBException(new DartDBException(HttpStatus.BAD_REQUEST, "", "", null)));
	}
}
