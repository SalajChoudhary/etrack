package dec.ny.gov.etrack.permit.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
  
  @Value("${spring.datasource.jndi-name}")
  private String jndiName;

  @Bean(name="eTrackDataSource")
  public DataSource dataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }
  
}
