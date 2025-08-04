package dec.ny.gov.etrack.dms.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import dec.ny.gov.etrack.dms.util.EcMaaSInterceptor;

@Configuration
@EnableWebSecurity
@PropertySource(value = {"classpath:application.properties"})
public class DMSConfig {

  @Value("${eCMaaS.connection.timeout}")
  private String connectionTimeout;
  @Value("${eCMaaS.read.timeout}")
  private String readTimeout;
  @Value("${eCMaas.basic.auth}")
  private String basicAuthKey;

  /*
   * @Bean public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
   * 
   * @Value("${application-version}") String appVersion) {
   * 
   * return new OpenAPI().info(new Info().title("eTrack DMS API").version(appVersion)
   * .description(appDesciption).termsOfService("http://swagger.io/terms/") .license(new
   * License().name("Apache 2.0").url("http://springdoc.org"))); }
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${eCMaaS.interface.url}") String eCMaaSURL) {

    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(eCMaaSURL)
        .interceptors(new EcMaaSInterceptor(basicAuthKey)).build();
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }
}
