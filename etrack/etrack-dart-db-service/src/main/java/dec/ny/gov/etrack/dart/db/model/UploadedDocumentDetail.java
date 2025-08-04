package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class UploadedDocumentDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String decId;
  private String facilityName;
  private Long projectId;
  private List<String> newPermits;
  private List<String> modificationPermits;
  private List<String> renewalPermits;
  private List<String> documentTitles;
  private List<String> refLocations;
}
