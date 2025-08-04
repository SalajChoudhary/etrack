package gov.ny.dec.etrack.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "gov.ny.dec.etrack.cache")
public class EtrackConfigApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(EtrackConfigApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(EtrackConfigApplication.class);
  }

}
