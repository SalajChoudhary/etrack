package dec.ny.gov.etrack.gis.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DataNotFoundExceptionTest {

	
	@InjectMocks
	private DataNotFoundException dataNotFoundException;
	
	@Test
	public void testDataNotFoundExceptionWithCodeAndMsg() {
		DataNotFoundException dataNotFoundException = new DataNotFoundException("code", "Msg");
	}
	
	@Test
	public void testDataNotFoundException() {
		DataNotFoundException dataNotFoundException = new DataNotFoundException();
	}
	
	@Test
	public void testDataNotFoundExceptionGetErrorCode() {
		this.dataNotFoundException.getErrorCode();
	}
	
	@Test
	public void testDataNotFoundExceptionGetErrorMsg() {
		this.dataNotFoundException.getErrorMessage();
	}
	

}
