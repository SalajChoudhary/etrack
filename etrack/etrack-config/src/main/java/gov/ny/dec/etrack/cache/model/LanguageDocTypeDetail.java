package gov.ny.dec.etrack.cache.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class LanguageDocTypeDetail {
  private Map<String, List<ETrackDocType>> eTrackDocTypes;
}
