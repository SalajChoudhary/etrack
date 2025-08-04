package dec.ny.gov.etrack.gis.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class GISExceptionTest {

	@InjectMocks
	private GISException gisException;
	@Test
	public void testGISException() {
		GISException ex = new GISException("200", "Bad");
		
	}
	@Test
	public void testGISExceptionWithThrowable() {
		GISException ex = new GISException("200", "Bad", new Throwable());
		
	}
	
	@Test
	public void testGISExceptionGetErrorMsg() {
		this.gisException.getErrorMessage();		
	}
	
	@Test
	public void testGISExceptionGetErrorCode() {
		this.gisException.getErrorCode();
		
	}

}
