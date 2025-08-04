package dec.ny.gov.etrack.asms.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class ETrackASMSConfig {

  @Value("${asms.connection.timeout}")
  private String connectionTimeout;
  @Value("${asms.read.timeout}")
  private String readTimeout;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${asms.interface.url}") String asmsURL) {
    RestTemplate restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(asmsURL)
        .build();
    DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
    defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
    restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
    return restTemplate;
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
