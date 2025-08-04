package dec.ny.gov.etrack.dart.db.config;


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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import dec.ny.gov.etrack.dart.db.util.JWTTokenConverter;
import dec.ny.gov.etrack.dart.db.util.MultitenantResolver;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private MultitenantResolver resolver;
  
//  @Value("${okta.auth.url}")
//  private String oktaAuthURL;

  @Bean
  public JwtDecoder customDecoder(OAuth2ResourceServerProperties properties) {
    NimbusJwtDecoder jwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();
    jwtDecoder.setClaimSetConverter(new JWTTokenConverter());
    return jwtDecoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().antMatcher("/**").authorizeRequests().antMatchers("*").hasAuthority("*")
        .anyRequest().authenticated().and().oauth2ResourceServer().jwt();
  }
  
//  @Override
//  public void configure(AuthenticationManagerBuilder auth) throws Exception {
//    resolver.addTrustedIssuer(oktaAuthURL);
//    auth.parentAuthenticationManager(resolver.resolve(oktaAuthURL));
//  }

  @Override
  public void configure(WebSecurity web) throws Exception {
      // TODO Auto-generated method stub
      web.ignoring().antMatchers("/v2/api-docs",
              "/configuration/ui",
              "/swagger-resources/**",
              "/configuration/security",
              "/swagger-ui.html",
              "/webjars/**");
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST");
      }
    };
  }
}

