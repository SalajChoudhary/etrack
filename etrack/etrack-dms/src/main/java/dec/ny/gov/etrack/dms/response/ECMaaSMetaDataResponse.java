package dec.ny.gov.etrack.dms.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class ECMaaSMetaDataResponse  extends DMSResponse {
  @JsonProperty("dim_MetadataProperties")
  private DimMetadataProperty dimMetadataProperties;
  @JsonProperty("ldm_MetadataProperties")
  private LdmMetadataProperty ldmMetadataProperties;
}
