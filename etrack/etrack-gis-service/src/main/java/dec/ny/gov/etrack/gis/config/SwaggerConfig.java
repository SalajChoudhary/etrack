package dec.ny.gov.etrack.gis.config;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.val;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Autowired
  private BuildProperties buildProperties;

  @Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(
            new ApiInfoBuilder().title(buildProperties.getName())
            .version(buildProperties.getVersion()).build())
		    .forCodeGeneration(true).globalOperationParameters(globalParameterList()).select()
				.apis(RequestHandlerSelectors.basePackage("dec.ny.gov.etrack.gis.controller"))
				.paths(PathSelectors.any()).build();
	}
	
	private List<Parameter> globalParameterList() {
	    val authTokenHeader =
	        new ParameterBuilder()
	            .name("Authorization") // name of the header
	            .modelRef(new ModelRef("string")) // data-type of the header
	            .required(true) // required/optional
	            .parameterType("header") // for query-param, this value can be 'query'
	            .description("Basic Auth Token")
	            .build();

	    return Collections.singletonList(authTokenHeader);
	}
}
