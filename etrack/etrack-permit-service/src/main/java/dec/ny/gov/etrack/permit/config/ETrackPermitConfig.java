package dec.ny.gov.etrack.permit.config;

import java.time.Duration;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableTransactionManagement
public class ETrackPermitConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;

  @Value("${etrack.facilities.package.name}")
  private String packageName;

  @Value("${etrack.populate.facility.proc.name}")
  private String populateFacilityProc;

  @Value("${etrack.amend.facility.proc.name}")
  private String updateFacilityProc;
  
  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schemaName;

  @Value("${etrack.facility.proc.name}")
  private String eTrackFacilityInfoProc;

  @Value("${etrack.delete.project.proc.name}")
  private String eTrackDeleteProjectProc;
  
  @Value("${etrack.populate.dart.public.info}")
  private String eTrackPopulatePublicInfo;
  
  @Value("${etrack.get.staff.details.info}")
  private String eTrackStafDetailsProc;

  @Value("${etrack.dart.load.public.history.data}")
  private String eTrackPopulatePublicHistProc;

  @Value("${etrack.dart.load.facility.history.data}")
  private String eTrackPopulateFacilityHistProc;

  @Value("${etrack.dart.user.details.proc}")
  private String eTrackGetUserDetailsProc;

  @Value("${etrack.update.original.submittal.proc}")
  private String eTrackUpdateOriginalSubmittalIndProc;

  @Value("${etrack.dart.permit.app.mapping.proc}")
  private String eTrackDARTPermitAndAppTypeValidProc;

  @Value("${etrack.dart.related.regular.mapping.proc}")
  private String eTrackDARTRelatedRegularMappingProc;
  
  @Autowired
  private DataSource eTrackDataSource;

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "populateFacilityProcCall")
  public SimpleJdbcCall populateFacilityProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(populateFacilityProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "updateFacilityProcCall")
  public SimpleJdbcCall updateFacilityProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(updateFacilityProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackFacilityInfoProc")
  public SimpleJdbcCall getETrackFacitlityInfoProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFacilityInfoProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackDeleteProjectCall")
  public SimpleJdbcCall getETrackDeleteProjectCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDeleteProjectProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackPopulatePublicInfoProc")
  public SimpleJdbcCall eTrackPopulatePublicInfoProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackPopulatePublicInfo)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackStaffDetailsProcCall")
  public SimpleJdbcCall eTrackStaffDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackStafDetailsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackPopulatePublicHistProcCall")
  public SimpleJdbcCall eTrackPopulatePublicHistProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackPopulatePublicHistProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackPopulateFacilityHistProcCall")
  public SimpleJdbcCall eTrackPopulateFacilityHistProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackPopulateFacilityHistProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackGetUserDetailsProcCall")
  public SimpleJdbcCall eTrackGetUserDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackGetUserDetailsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackUpdateOriginalSubmittalIndProcCall")
  public SimpleJdbcCall eTrackUpdateOriginalSubmittalIndProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackUpdateOriginalSubmittalIndProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackDARTPermitAndAppTypeValidProcCall")
  public SimpleJdbcCall eTrackDARTPermitAndAppTypeValidProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDARTPermitAndAppTypeValidProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Create an instance to execute a Procedure available in eTrack.
   * 
   * @return - instance of {@link SimpleJdbcCall}
   * 
   * @throws NamingException - Exception while preparing instance.
   */
  @Bean(name = "eTrackDARTRelatedRegularMappingProcCall")
  public SimpleJdbcCall eTrackDARTRelatedRegularMappingProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDARTRelatedRegularMappingProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  /**
   * Prepare an RestTemplate instance which can be used by the eTrack service to invoke Akana system.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder}
   * @param akanaDOSServiceURI - URI Path.
   * @param connectionTimeout - Connection time out value.
   * @param readTimeout - Read time out value.
   * 
   * @return - instance of {@link RestTemplate}
   */
  @Bean(name = "businessVerificationRestTemplate")
  public RestTemplate businessVerificationRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${etrack.akana.dos.verification.uri}") String akanaDOSServiceURI,
      @Value("${etrack.akana.dos.connection.timeout}") String connectionTimeout,
      @Value("${etrack.akana.dos.read.timeout}") String readTimeout) {

    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(akanaDOSServiceURI)
        .build();
  }

  /**
   * Prepare an RestTemplate instance which can be used by the eTrack service to invoke another eTrack service.
   * 
   * @param restTemplateBuilder - {@link RestTemplateBuilder}
   * @param eTrackServiceURL - URI Path.
   * @param connectionTimeout - Connection time out value.
   * @param readTimeout - Read time out value.
   * 
   * @return - instance of {@link RestTemplate}
   */
  @Bean(name = "eTrackOtherServiceRestTemplate")
  public RestTemplate eTrackOtherServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${etrack.other.service.uri}") String eTrackServiceURL,
      @Value("${etrack.akana.dos.connection.timeout}") String connectionTimeout,
      @Value("${etrack.akana.dos.read.timeout}") String readTimeout) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(eTrackServiceURL).build();
  }
  
  /**
   * Prepare the ObjectMapper instance to use in the service for Object Serialization and De-Serialization.
   * 
   * @return - Instance of {@link ObjectMapper}
   */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  /**
   * CORS enable configuration.
   * 
   * @return - instance of {@link WebMvcConfigurer}
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "PUT", "POST",
            "DELETE");
      }
    };
  }  
}
