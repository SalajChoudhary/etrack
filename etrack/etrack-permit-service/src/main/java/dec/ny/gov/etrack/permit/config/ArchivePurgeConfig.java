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
public class ArchivePurgeConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;

  @Value("${etrack.purge.archive.package.name}")
  private String packageName;

  @Value("${etrack.purge.cleanup.proc.name}")
  private String purgeCleanUpProc;

  @Value("${etrack.purge.archive.review.proc}")
  private String purgeArchiveReviewProc;
  
  @Value("${etrack.purge.archive.document.proc}")
  private String purgeArchiveDocumentProc;

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
  @Bean(name = "purgeCleanUpProcCall")
  public SimpleJdbcCall purgeCleanUpProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(purgeCleanUpProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "purgeArchiveReviewProcCall")
  public SimpleJdbcCall purgeArchiveReviewProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(purgeArchiveReviewProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "purgeArchiveDcoumentProcCall")
  public SimpleJdbcCall purgeArchiveDcoumentProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(eTrackDataSource);
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(purgeArchiveDocumentProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
}
