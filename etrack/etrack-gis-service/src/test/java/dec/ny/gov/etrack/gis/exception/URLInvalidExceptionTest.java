package dec.ny.gov.etrack.gis.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class URLInvalidExceptionTest {

	
	@InjectMocks
	private URLInvalidException urlInvalidException;
	
	@Test
	public void testURLInvalidExceptionWithMsg() {
		URLInvalidException urlInvalidException = new URLInvalidException("msg");
	}
	
	@Test
	public void testURLInvalidExceptionWithThrowable() {
		URLInvalidException urlInvalidException = new URLInvalidException("msg", new Throwable());
	}
	
	@Test
	public void testURLInvalidException() {
		URLInvalidException urlInvalidException = new URLInvalidException();
	}
	
	
	
}
