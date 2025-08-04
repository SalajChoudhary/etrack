package gov.ny.dec.etrack.cache.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class ETrackDocType {
  private Integer docTypeId;
  private String docTypeDesc;
  private String docClassName;
  private Integer docClassId;
  private List<ETrackDocumentSubType> docSubTypes;
}
