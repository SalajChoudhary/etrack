package dec.ny.gov.etrack.dart.db.util;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
public class MultitenantResolveTest {

	@InjectMocks
	MultitenantResolver multitenantResolver;
	
	@Test
	void resolve() {
		AuthenticationManager am = multitenantResolver.resolve("issuer");
	}
	
//	@Test
//	void addTrustedIssuer() {
//		multitenantResolver.addTrustedIssuer("");
//	}
}
