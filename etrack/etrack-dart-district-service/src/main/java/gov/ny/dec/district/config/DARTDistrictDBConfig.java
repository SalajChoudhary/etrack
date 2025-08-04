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
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan("gov.ny.dec.district.etrack.entity")
@EnableJpaRepositories(basePackages="gov.ny.dec.district.dart.dao",
entityManagerFactoryRef="dartDistrictEntityManager",
transactionManagerRef="dartDistrictTransactionManager")


@EnableTransactionManagement
@PropertySource(value = {"classpath:persistence-db.properties"})
public class DARTDistrictDBConfig {
  
  @Value("${spring.dart-datasource.jndi-name}")
  private String jndiName;
 
//  @Value("${dart.district.schema.name}")
//  private String schemaName;
//
//  @Value("${etrack.district.package.name}")
//  private String packageName;

  @Value("${etrack.dart.infra.package.name}")
  private String eTrackDartInfraPackageName;
  
  @Value("${dart.appl.narrative.desc.proc.name}")
  private String applNarrativeDescProcedureName;
  
  @Value("${etrack.dart.infra.schema.name}")
  private String eTrackDartInfraSchemaName;

  @Value("${dart.upload.dart.proc.name}")
  private String dartUploadETrackProc;

  @Value("${dart.upload.dimsr.proc.name}")
  private String dartUploadDIMSRProc;

  @Value("${etrack.dart.milestone.refresh.package.name}")
  private String eTrackDartMilestoneRefreshPackageName;

  @Value("${etrack.dart.milestone.refresh.proc.name}")
  private String eTrackDartMilestoneRefreshProc;

  @Value("${etrack.dart.addl.permit.proc.name}")
  private String eTrackDartAddAdditionalPermitProc;

//
//  @Value("${dart.district.facility.proc.name}")
//  private String facilitySearchProcedureName;
  
  @Bean
  public LocalContainerEntityManagerFactoryBean dartDistrictEntityManager() throws NamingException {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dartDataSource());
    em.setPackagesToScan("gov.ny.dec.district.dart.entity");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    return em;
  }
  
  @Bean
  @ConfigurationProperties(prefix="spring.dart-datasource")
  public DataSource dartDataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }

  @Bean(name="eTrackDartPermitNarrativeDescProcCall")
  public SimpleJdbcCall eTrackDartPermitNarrativeDescProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dartDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(applNarrativeDescProcedureName).withSchemaName(eTrackDartInfraSchemaName)
        .withCatalogName(eTrackDartInfraPackageName);
    return simpleJdbcCall;
  }

  @Bean(name="eTrackUploadToDartProcCall")
  public SimpleJdbcCall eTrackUploadToDartProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dartDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(dartUploadETrackProc).withSchemaName(eTrackDartInfraSchemaName)
        .withCatalogName(eTrackDartInfraPackageName);
    return simpleJdbcCall;
  }

  @Bean(name="eTrackUploadDIMSRDetailProcCall")
  public SimpleJdbcCall eTrackUploadDIMSRDetailProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dartDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(dartUploadDIMSRProc).withSchemaName(eTrackDartInfraSchemaName)
        .withCatalogName(eTrackDartInfraPackageName);
    return simpleJdbcCall;
  }

  @Bean(name="eTrackDartMilestoneRefreshProcCall")
  public SimpleJdbcCall eTrackDartMilestoneRefreshProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dartDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(eTrackDartMilestoneRefreshProc).withSchemaName(eTrackDartInfraSchemaName)
        .withCatalogName(eTrackDartMilestoneRefreshPackageName);
    return simpleJdbcCall;
  }

  @Bean(name="eTrackDartAddAdditionalPermitProcCall")
  public SimpleJdbcCall eTrackDartAddAdditionalPermitProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dartDataSource());
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName(eTrackDartAddAdditionalPermitProc).withSchemaName(eTrackDartInfraSchemaName)
        .withCatalogName(eTrackDartInfraPackageName);
    return simpleJdbcCall;
  }
  
}
