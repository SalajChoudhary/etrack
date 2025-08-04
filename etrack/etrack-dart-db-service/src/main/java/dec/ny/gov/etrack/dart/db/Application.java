package dec.ny.gov.etrack.dart.db;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  private static Logger logger = LoggerFactory.getLogger(Application.class.getName());
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /*
   * @Override public void onStartup(ServletContext servletContext) throws ServletException {
   * logger.info("System environment variable {}", System.getenv("ITSENV"));
   * System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev");
   * super.onStartup(servletContext); }
   */
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }
}
