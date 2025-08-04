package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

public @Data class PurgeArchiveResultDocuments implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<PurgeArchiveResultDocument> documents;
}
