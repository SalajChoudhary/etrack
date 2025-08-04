package dec.ny.gov.etrack.fmis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EtrackFmisServiceApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(EtrackFmisServiceApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(EtrackFmisServiceApplication.class);
  }

}
