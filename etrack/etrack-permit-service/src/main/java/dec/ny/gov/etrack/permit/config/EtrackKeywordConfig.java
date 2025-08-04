package dec.ny.gov.etrack.permit.config;

import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class EtrackKeywordConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;

  @Value("${etrack.keywords.package.name}")
  private String packageName;

  @Value("${etrack.system.keywords.retrieval.proc.name}")
  private String retrieveSystemDetectedKeywordsProc;

  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schemaName;

  @Autowired
  private DataSource eTrackDataSource;
  
  /**
   * Prepare instance of {@link SimpleJdbcCall} to invoke the procedure.
   * 
   * @return - Object of {@link SimpleJdbcCall}
   * @throws NamingException - if any error.
   */
  @Bean(name = "retrieveSystemDetectedProcCall")
  public SimpleJdbcCall retrieveSystemDetectedProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(retrieveSystemDetectedKeywordsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
}