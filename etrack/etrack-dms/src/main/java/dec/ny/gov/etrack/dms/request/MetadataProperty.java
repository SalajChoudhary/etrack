package dec.ny.gov.etrack.dms.request;

import lombok.Data;

public @Data class MetadataProperty {
  private String propertyDefinitionId;
  private String value;
}
