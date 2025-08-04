package dec.ny.gov.etrack.gis.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BadRequestExceptionTest {

	
	@InjectMocks
	private BadRequestException badRequestException;
	
	
	@Test
	public void testBadRequestExceptionGetErrorCode() {
		this.badRequestException.getErrorCode();
	}
	@Test
	public void testBadRequestExceptionGetErrorMsg() {
		this.badRequestException.getErrorMessage();
	}
	
	@Test
	public void testBadRequestExceptionGetErrorObj() {
		this.badRequestException.getObject();
	}
	
	@Test
	public void testBadRequestException() {
		BadRequestException ex = new BadRequestException("203", "Not Okay", new Throwable()); 
	}

}
