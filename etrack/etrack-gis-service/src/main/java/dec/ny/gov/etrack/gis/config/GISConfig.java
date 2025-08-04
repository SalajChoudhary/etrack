package dec.ny.gov.etrack.gis.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application configuration.
 * 
 * @author mxmahali
 *
 */
@Configuration
public class GISConfig {

  @Value("${gis.connection.timeout}")
  private String connectionTimeout;
  @Value("${gis.read.timeout}")
  private String readTimeout;

  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param gisEFindServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "giseFindPolygonUploadServiceRestTemplate")
  public RestTemplate giseFindPolygonUploadServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${gis.efind.service.uri}") String gisEFindServiceURI) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(gisEFindServiceURI).build();
  }

  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param gisEFindServiceURI - URI Path.
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "gisEFindPolygonReadRestTemplate")
  public RestTemplate gisEFindPolygonReadRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${gis.efind.service.uri}") String gisEFindServiceURI) {
    
    RestTemplate restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(gisEFindServiceURI).build();
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
    messageConverters.add(converter);
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }
  
  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param gisServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "gisInternalServiceRestTemplate")
  public RestTemplate gisInternalServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${gis.internal.service.uri}") String gisServiceURI) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(gisServiceURI).build();
  }

  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param gisExternalServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "gisExternalServiceRestTemplate")
  public RestTemplate gisExternalServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${gis.external.service.uri}") String gisExternalServiceURI) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(gisExternalServiceURI).build();
  }
  
  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param gisITSServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "gisITSServiceRestTemplate")
  public RestTemplate gisITSServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${gis.its.service.uri}") String gisITSServiceURI) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(gisITSServiceURI).build();
  }
  
  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param geocodeServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "geoCodeServiceRestTemplate")
  public RestTemplate geoCodeServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${geocode.service.uri}") String geocodeServiceURI) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(geocodeServiceURI).build();
  }

  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param etrackOtherServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */
  @Bean(name = "eTrackOtherServiceRestTemplate")
  public RestTemplate eTrackPermitRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${etrack.other.service.uri}") String etrackOtherServiceURI) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(etrackOtherServiceURI).build();
  }

  /**
   * Prepare the  a bean instance for the resttemplate to make a call.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder} instance
   * @param arcGISServiceURI - URL String which needs to be used by this {@link RestTemplate}
   * 
   * @return - Instance of {@link RestTemplate} 
   */

  @Bean(name = "eTrackGISServiceRestTemplate")
  public RestTemplate eTrackGISServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${arcgis.service.url}") String arcGISServiceURI) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(arcGISServiceURI).build();
  }
  
  /**
   * CORS configuration.
   *  
   * @return - Instance of {@link WebMvcConfigurer} 
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
      }
    };
  }
}
