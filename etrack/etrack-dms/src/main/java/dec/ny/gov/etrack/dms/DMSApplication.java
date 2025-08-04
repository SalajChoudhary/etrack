package dec.ny.gov.etrack.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dec.ny.gov.etrack.dms")
public class DMSApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(DMSApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(DMSApplication.class);
  }
}
