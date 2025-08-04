package dec.ny.gov.etrack.gis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import dec.ny.gov.etrack.gis.util.JWTTokenConverter;
import dec.ny.gov.etrack.gis.util.MultitenantResolver;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  MultitenantResolver resolver;
  
  @Value("${okta.auth.url}")
  private String oktaAuthURL;


  /**
   * Create an instance of JwtDecoder.
   * 
   * @param properties - {@link OAuth2ResourceServerProperties}
   * 
   * @return - Instance of JwtDecoder.
   */
  @Bean
  public JwtDecoder customDecoder(OAuth2ResourceServerProperties properties) {
    NimbusJwtDecoder jwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();
    jwtDecoder.setClaimSetConverter(new JWTTokenConverter());
    return jwtDecoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().antMatcher(
        "/etrack-gis/ITSAddress,"
        + "/etrack-gis/EsriAddresses,"
        + "/etrack-gis/counties,"
        + "/etrack-gis/taxParcel,"
        + "/etrack-gis/municipalities,"
        + "/etrack-gis/DECPolygonByTaxId,"
        + "/etrack-gis/DECPolygonByAddress,"
        + "/etrack-gis/DECPolygonByDecId,"
        + "/etrack-gis/applicantPolygon*,"
        + "/etrack-gis/spatial-polygon*,"
        + "/etrack-gis/delete-spatial-polygon*,"
        + "/etrack-gis/save-spatial-inquiry,"
        + "/etrack-gis/spatial-inquiry*,"
        + "/etrack-gis/analystPolygon*,"
        + "/etrack-gis/submitedPolygon*,"
        + "/etrack-gis/applicantPolygon,"
        + "/etrack-gis/analystPolygon,"
        + "/etrack-gis/submitedPolygon,"
        + "/etrack-gis/facility*,"
        + "/etrack-gis/decId*,"
        + "/etrack-gis/deletePolygon*,"
        + "/etrack-gis/delete-analyst-polygon*,"
        + "/etrack-gis/delete-submittal-polygon*,"
        + "/etrack-gis/address,"
        + "/etrack-gis/upload,"
        + "/etrack-gis/workarea-polygon*,"
        + "/etrack-gis/delete-workarea-polygon*"
        + "").authorizeRequests().antMatchers("*")
    .hasAuthority("*").anyRequest().authenticated().and().oauth2ResourceServer().jwt();
  }
 
  /**
   * Prepare Authentication Manager details.
   * 
   */
  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    resolver.addTrustedIssuer(oktaAuthURL);
    auth.parentAuthenticationManager(resolver.resolve(oktaAuthURL));
  }

  /**
   * CORS configuration.
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
      web.ignoring().antMatchers(
              "/etrack-gis/upload-polygon-efind",
              "/v2/api-docs",
              "/configuration/ui",
              "/swagger-resources/**",
              "/configuration/security",
              "/swagger-ui.html",
              "/webjars/**");
  }

}

