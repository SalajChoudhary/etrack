package dec.ny.gov.etrack.fmis.config;

import java.time.Duration;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.fmis.model.FMISHeader;

@Configuration
public class FMISConfig {


  @Bean
  public MappingJackson2HttpMessageConverter customizedJacksonMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter
        .setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
            new MediaType("application", "*+json"), MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_FORM_URLENCODED));
    return converter;
  }


  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
      }
    };
  }

  @Bean(name = "fmisRestTemplate")
  public RestTemplate eTrackFMISRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${fmis.payload.uri}") String etrackFMISServiceURI,
      @Value("${connection.timeout}") String connectionTimeout,
      @Value("${read.timeout}") String readTimeout) {

    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(etrackFMISServiceURI)
        .build();
  }

  @Bean
  public FMISHeader getFMISHeader(@Value("${fmis.system.user.name}") final String userName, 
      @Value("${fmis.system.user.credential}") final String credential) {
    FMISHeader fmisHeader = new FMISHeader();
    fmisHeader.setXmlns("http://xmlns.oracle.com/apps/fnd/rest/header");
    fmisHeader.setUserName(userName);
    fmisHeader.setPassword(credential);
    fmisHeader.setResponsibility("FND_REP_APP");
    fmisHeader.setRespApplication("FND");
    fmisHeader.setSecurityGroup("STANDARD");
    return fmisHeader;
  }

  @Bean
  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
    return objectMapper;
  }
  
  @Bean(name = "eTrackOtherServiceRestTemplate")
  public RestTemplate eTrackOtherServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${etrack.other.service.uri}") String eTrackServiceURL,
      @Value("${connection.timeout}") String connectionTimeout,
      @Value("${read.timeout}") String readTimeout) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(eTrackServiceURL).build();
  }
}
