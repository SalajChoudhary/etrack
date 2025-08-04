package dec.ny.gov.etrack.gis.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Web Configuration class.
 * 
 * @author mxmahali
 *
 */
@Configuration
public class WebConfig {

  /**
   * Prepare the customized of {@link MappingJackson2HttpMessageConverter}
   * 
   * @return - instance of {@link MappingJackson2HttpMessageConverter}
   */
  @Bean
  public MappingJackson2HttpMessageConverter customizedJacksonMessageConverter() {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
      converter.setSupportedMediaTypes(
              Arrays.asList(
                      MediaType.APPLICATION_JSON,
                      new MediaType("application", "*+json"),
                      MediaType.APPLICATION_OCTET_STREAM));
      return converter;
  }
}
