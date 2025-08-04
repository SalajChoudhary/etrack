package dec.ny.gov.etrack.dcs.config;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.service.Parameter;
import lombok.val;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.ModelRef;
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
        .forCodeGeneration(true)
        .globalOperationParameters(globalParameterList()).select()
        .apis(RequestHandlerSelectors.basePackage("dec.ny.gov.etrack.dcs.controller"))
        .paths(PathSelectors.any()).build();
  }

  @Bean
  public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
    return new AlternateTypeRuleConvention() {

      @Override
      public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
      }

      @Override
      public List<AlternateTypeRule> rules() {
        return Arrays.asList(new AlternateTypeRule(resolver.resolve(Pageable.class),
            resolver.resolve(pageableMixin())));
      }
    };
  }

  private Type pageableMixin() {
    return new AlternateTypeBuilder()
        .fullyQualifiedClassName(String.format("%s.generated.%s",
            Pageable.class.getPackage().getName(), Pageable.class.getSimpleName()))
        .withProperties(Arrays.asList(property(Integer.class, "page"),
            property(Integer.class, "size"), property(String.class, "sort")))
        .build();
  }

  private AlternateTypePropertyBuilder property(Class<?> type, String name) {
    return new AlternateTypePropertyBuilder().withName(name).withType(type).withCanRead(true)
        .withCanWrite(true);
  }


  private List<Parameter> globalParameterList() {
    val authTokenHeader = new ParameterBuilder().name("Authorization") // name of the header
        .modelRef(new ModelRef("string")) // data-type of the header
        .required(true) // required/optional
        .parameterType("header") // for query-param, this value can be 'query'
        .description("Basic Auth Token").build();

    return Collections.singletonList(authTokenHeader);
  }
}
