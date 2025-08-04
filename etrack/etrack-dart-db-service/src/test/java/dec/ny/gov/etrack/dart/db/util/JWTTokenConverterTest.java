package dec.ny.gov.etrack.dart.db.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
public class JWTTokenConverterTest {
	
	@InjectMocks
	JWTTokenConverter jwt;
	
	@Test
	void convert() {
		Map<String, Object> source = new HashMap();
		Map<String, Object> cnv = jwt.convert(source);
	}

}
