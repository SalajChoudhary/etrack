package dec.ny.gov.etrack.fmis.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VPSConfig {

  @Bean(name="vpsTxnIdRestTemplate")
  public RestTemplate eTrackVPSTxnIdRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${vps.generate.txn.id.url}") String etrackVPSTxnIdURI,
      @Value("${connection.timeout}") String connectionTimeout,
      @Value("${read.timeout}") String readTimeout) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(etrackVPSTxnIdURI).build();
  }

  @Bean(name = "akanaOAuthRestTemplate")
  public RestTemplate akanaOAuthRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${okta.oauth.token.url}") String akanaAuthTokenURL,
      @Value("${connection.timeout}") String connectionTimeout,
      @Value("${read.timeout}") String readTimeout) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(akanaAuthTokenURL)
        .build();
  }

  @Bean(name="vpsPaymentRestTemplate")
  public RestTemplate eTrackVPSPaymentRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${vps.payment.request.url}") String etrackVPSTxnIdURI,
      @Value("${connection.timeout}") String connectionTimeout,
      @Value("${read.timeout}") String readTimeout) {
    
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(etrackVPSTxnIdURI).build();
  }

}
