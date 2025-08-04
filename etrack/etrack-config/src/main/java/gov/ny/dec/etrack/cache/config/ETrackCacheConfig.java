package gov.ny.dec.etrack.cache.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableCaching
@EntityScan("gov.ny.dec.etrack.cache.entity")
@EnableJpaRepositories("gov.ny.dec.etrack.cache.repostitory")
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class ETrackCacheConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;
  
  @Value("${etrack.cache.config.schema.name}")
  private String schemaName;

  @Value("${etrack.cache.config.package.name}")
  private String packageName;

  @Value("${etrack.cache.config.message.proc.name}")
  private String messageProcName;
  
  @Value("${etrack.cache.config.doctype.subtype.proc.name}")
  private String docTypeSubTypeProcName;
  
  @Value("${etrack.cache.config.sw.facility.type.proc.name}")
  private String swFacilityTypeProcName;

  @Value("${etrack.permit.type.config.proc.name}")
  private String retrievePermitTypeProc;
  
  @Value("${etrack.xtra.progrm.id.spl.attn.proc.name}")
  private String retrieveXtraProgAndSplAttnProc;
  
  
//  private static final String MESSAGE_PROC_CURSOR_NAME = "CUR_MESSAGES";


  @Bean
  public DataSource dataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST");
      }
    };
  }
  
  @Bean(name = "messageJdbcCall")
  public SimpleJdbcCall messageJdbcCall() throws NamingException {
    return
        new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(messageProcName)
            .withSchemaName(schemaName).withCatalogName(packageName);
  }
  
  @Bean(name="docTyeAndSubTypeJdbcCall")
  public SimpleJdbcCall docTyeAndSubTypeJdbcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(docTypeSubTypeProcName)
    .withSchemaName(schemaName).withCatalogName(packageName);
  }
  
  @Bean(name="swFacilityTypeProcCall")
  public SimpleJdbcCall swFacilityTypeProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(swFacilityTypeProcName)
    .withSchemaName(schemaName).withCatalogName(packageName);
  }
  
  @Bean(name="eTrackPermitTypeProcCall")
  public SimpleJdbcCall eTrackPermitTypeProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(retrievePermitTypeProc)
    .withSchemaName(schemaName).withCatalogName("edb_facilities_pkg");
  }
 
  @Bean(name="eTrackXTRAIDandProgramIDandSplAttnProcCall")
  public SimpleJdbcCall eTrackXTRAIDandProgramIDandSplAttnProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(retrieveXtraProgAndSplAttnProc)
    .withSchemaName(schemaName).withCatalogName("edb_facilities_pkg");
  }
  

  
}
