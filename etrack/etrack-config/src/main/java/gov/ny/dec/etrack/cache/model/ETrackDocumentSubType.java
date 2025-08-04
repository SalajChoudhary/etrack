package gov.ny.dec.etrack.cache.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class ETrackDocumentSubType {
  private Integer subTypeId;
  private String subTypeDesc;
}
