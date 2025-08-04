package gov.ny.dec.district.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EntityScan("gov.ny.dec.district.dart.entity")
@EnableJpaRepositories(basePackages = "gov.ny.dec.district.etrack.repository",
    entityManagerFactoryRef = "eTrackDistrictEntityManager",
    transactionManagerRef = "eTrackDistrictTransactionManager")

@EnableTransactionManagement
@PropertySource(value = {"classpath:persistence-db.properties"})
public class ETrackDistrictServiceConfig {

  @Value("${spring.datasource.jndi-name}")
  private String eTrackJndiName;

  @Value("${etrack.district.decId.proc.name}")
  private String decIdSearchProcedureName;

  @Value("${etrack.district.facility.proc.name}")
  private String facilitySearchProcedureName;

  @Value("${etrack.district.schema.name}")
  private String eTrackSchemaName;

  @Value("${etrack.district.package.name}")
  private String eTrackPackageName;

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean eTrackDistrictEntityManager()
      throws NamingException {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(eTrackDataSource());
    em.setPackagesToScan("gov.ny.dec.district.etrack.entity");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    return em;
  }

  @Primary
  @Bean
  @ConfigurationProperties(prefix="spring.datasource")
  public DataSource eTrackDataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(eTrackJndiName);
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager eTrackDistrictTransactionManager() throws NamingException {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(eTrackDistrictEntityManager().getObject());
    return transactionManager;
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods(
            HttpMethod.GET.toString());
      }
    };
  }

  @Bean(name="decIDSearchJdbcCall")
  public SimpleJdbcCall decIDSearchJdbcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(decIdSearchProcedureName).withSchemaName(eTrackSchemaName)
        .withCatalogName(eTrackPackageName);
    return simpleJdbcCall;
  }

  @Bean(name="facilityNameSearchJdbcCall")
  public SimpleJdbcCall facilityNameSearchJdbcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(facilitySearchProcedureName).withSchemaName(eTrackSchemaName)
        .withCatalogName(eTrackPackageName);
    return simpleJdbcCall;
  }
}
