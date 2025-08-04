package dec.ny.gov.etrack.gis.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class JWTTokenConverterTest {
	
	@InjectMocks
	JWTTokenConverter jwtTokenConverter;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testConvertSuccessfully() {
		Map<String, Object> source = new HashMap<>();
		source.put("Hey", new Object());
		jwtTokenConverter.convert(source);
		
	}
}
