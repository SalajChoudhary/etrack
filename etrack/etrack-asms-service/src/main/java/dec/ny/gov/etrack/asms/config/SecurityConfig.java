package dec.ny.gov.etrack.asms.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import dec.ny.gov.etrack.asms.util.JWTTokenConverter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public JwtDecoder customDecoder(OAuth2ResourceServerProperties properties) {
    NimbusJwtDecoder jwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();
    jwtDecoder.setClaimSetConverter(new JWTTokenConverter());
    return jwtDecoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().antMatcher("/**").authorizeRequests().antMatchers("*")
        .hasAuthority("*").anyRequest().authenticated().and().oauth2ResourceServer().jwt();
  }
  
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

}
