package dec.ny.gov.etrack.dart.db.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

@Configuration
public class SpatialInquiryConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;
  @Value("${etrack.facilities.package.name}")
  private String packageName;
  @Value("${etrack.dart.db.service.schema.name}")
  private String schemaName;
  @Value("${etrack.spatial.inq.doc.retrieval.proc.name}")
  private String spatialInqDocumentRetrieveProc;
  @Value("${etrack.spatial.inq.status.retrieval.proc.name}")
  private String spatialInqStatusRetrieveProc;
  @Value("${etrack.spatial.inq.review.retrieval.proc.name}")
  private String spatialInqReviewRetrieveProc;
  
  private DataSource dataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }

  /**
   * Configuration to retrieve the Spatial Inquiry document retrieval Process.
   * 
   * @return - SimpleJdbcCall instance.
   * 
   * @throws NamingException - if any error while building the instance.
   */
  @Bean(name = "spatialInqDocumentRetrieveProcCall")
  public SimpleJdbcCall spatialInqDocumentRetrieveProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName(packageName).withProcedureName(spatialInqDocumentRetrieveProc);
    return simpleJdbcCall;
  }

  /**
   * Configuration to retrieve the status of  Spatial Inquiry.
   * 
   * @return - SimpleJdbcCall instance.
   * 
   * @throws NamingException - if any error while building the instance.
   */
  @Bean(name = "spatialInqStatusRetrieveProcCall")
  public SimpleJdbcCall spatialInqStatusRetrieveProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName(packageName).withProcedureName(spatialInqStatusRetrieveProc);
    return simpleJdbcCall;
  }

  /**
   * Configuration to retrieve the review the Spatial Inquiry details.
   * 
   * @return - SimpleJdbcCall instance.
   * 
   * @throws NamingException - if any error while building the instance.
   */
  @Bean(name = "spatialInqReviewRetrieveProcCall")
  public SimpleJdbcCall spatialInqReviewRetrieveProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withSchemaName(schemaName)
        .withCatalogName(packageName).withProcedureName(spatialInqReviewRetrieveProc);
    return simpleJdbcCall;
  }
}
