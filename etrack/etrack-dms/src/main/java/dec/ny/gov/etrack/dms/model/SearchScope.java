package dec.ny.gov.etrack.dms.model;

import lombok.Data;

public @Data class SearchScope {
  private Integer maximumNumberOfDocs;
  private Boolean searchAllVersions;
}
