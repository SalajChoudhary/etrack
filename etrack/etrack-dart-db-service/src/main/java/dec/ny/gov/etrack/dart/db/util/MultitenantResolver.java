package dec.ny.gov.etrack.dart.db.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class MultitenantResolver implements AuthenticationManagerResolver<String> {

  private Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(MultitenantResolver.class.getName());

  @Override
  public AuthenticationManager resolve(String issuer) {
    return this.authenticationManagers.get(issuer);
  }

  public void addTrustedIssuer(String issuer) {
    JwtAuthenticationProvider authenticationProvider =
        new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuer));
    authenticationManagers.put(issuer, authenticationProvider::authenticate);
    logger.info("Authentication provider is setup");
  }

}
