package dec.ny.gov.etrack.dcs.config;

import java.time.Duration;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



/*
 * @EnableJpaRepositories(basePackages = "dec.ny.gov.etrack.dcs.dao", entityManagerFactoryRef =
 * "dcsEntityManager", transactionManagerRef = "dcsTransactionManager")
 */
@Configuration
@EntityScan(basePackages = {"dec.ny.gov.etrack.dcs.model", "dec.ny.gov.etrack.dcs.entity"})
@EnableJpaRepositories(basePackages = {"dec.ny.gov.etrack.dcs.dao", "dec.ny.gov.etrack.dcs.repo"})
@PropertySource(value = {"classpath:application.properties"})
@EnableTransactionManagement
public class DCSConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;
  @Value("${dms.connection.timeout}")
  private String connectionTimeout;
  @Value("${dms.read.timeout}")
  private String readTimeout;
  @Value("${etrack.spatial.inquiry.package.name}")
  private String packageName;
  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schemaName;
  @Value("${etrack.si.reqd.documents.proc.name}")
  private String spatialInqDocumentRetrieveProc;

  /*@Value("${dcs.docclass.procedure.name}")
  private String dcsDocClassProcName;
  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schemaName;
  @Value("${dcs.dcoclass.package.name}")
  private String packageName;*/


  @Bean
  public DataSource dataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }
  
  @Bean(name = "spatialInqDocumentRetrieveProcCall")
  public SimpleJdbcCall spatialInqDocumentRetrieveProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    return 
        new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withProcedureName(spatialInqDocumentRetrieveProc)
            .withCatalogName(packageName);
  }
  
  /*@Bean(name="docClassJdbcCall")
  public SimpleJdbcCall docClassJdbcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withSchemaName(schemaName)
    .withCatalogName(packageName).withProcedureName(dcsDocClassProcName);
  }
  
  @Bean(name="jdbcTemplate")
  public JdbcTemplate jdbcTemplate(DataSource dataSource){
	  return new JdbcTemplate (dataSource);
  }*/
  
  /*
  @Bean
  public LocalContainerEntityManagerFactoryBean dcsEntityManager() throws NamingException {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("dec.ny.gov.etrack.dcs.model");
    
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(true);
    em.setJpaVendorAdapter(vendorAdapter);
    return em;
  }

  @Bean
  public PlatformTransactionManager dcsTransactionManager() throws NamingException {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(dcsEntityManager().getObject());
    transactionManager.setDefaultTimeout(6000);
    return transactionManager;
  }
  */
  
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${dms.uri}") String dmsURL) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(dmsURL).build();

  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "DELETE", "OPTIONS",
            "PUT", "POST");
      }
    };
  }
}
