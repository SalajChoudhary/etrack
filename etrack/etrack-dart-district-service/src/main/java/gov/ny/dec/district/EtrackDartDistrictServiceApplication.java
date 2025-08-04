package gov.ny.dec.district;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "gov.ny.dec.district")
public class EtrackDartDistrictServiceApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(EtrackDartDistrictServiceApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(EtrackDartDistrictServiceApplication.class);
  }
}
