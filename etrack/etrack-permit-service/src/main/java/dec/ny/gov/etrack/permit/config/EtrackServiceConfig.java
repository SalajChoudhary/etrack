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
public class EtrackServiceConfig {

  @Value("${etrack.dart.search.populate.table.proc.name}")
  private String retriveSearchTableProc;

  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schemaName;

  @Value("${etrack.keywords.package.name}")
  private String packageName;
  @Value("${etrack.dart.incremental.search.populate.proc.name}")
  private String etrackIncrementalPopulateSearchTableProc;

  @Autowired
  private DataSource eTrackDataSource;
  
  @Bean(name = "etrackPopulateSearchTableProcCall")
  public SimpleJdbcCall etrackPopulateSearchTableProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(eTrackDataSource))
        .withProcedureName(retriveSearchTableProc).withSchemaName(schemaName)
        .withCatalogName(packageName);
  }

  @Bean(name = "etrackIncrementalPopulateSearchTableProcCall")
  public SimpleJdbcCall etrackIncrementalPopulateSearchTableProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(eTrackDataSource))
        .withProcedureName(etrackIncrementalPopulateSearchTableProc).withSchemaName(schemaName)
        .withCatalogName(packageName);
  }
}
