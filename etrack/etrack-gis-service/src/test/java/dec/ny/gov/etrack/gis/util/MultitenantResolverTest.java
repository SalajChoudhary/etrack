package dec.ny.gov.etrack.gis.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class MultitenantResolverTest {

	
	@InjectMocks
	private MultitenantResolver multiTenantResolver;
	private String issuer = "https://etrackanalyst-dev.dec.ny.gov/dashboard";
	

	@Before
	public void setup() {
	    MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testResolveSuccessfully() {
	this.multiTenantResolver.resolve(issuer);
	}
	
//	@Test
//	public void testAddTrustedIssuer() {
//		this.multiTenantResolver.addTrustedIssuer(issuer);
//	}
//	
}
