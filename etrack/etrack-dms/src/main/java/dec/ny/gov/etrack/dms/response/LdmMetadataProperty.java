package dec.ny.gov.etrack.dms.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class LdmMetadataProperty {
  private String propertyDefinitionId;
  private String value;
  private List<PropertyString> propertyString;
  private Integer count;
}
