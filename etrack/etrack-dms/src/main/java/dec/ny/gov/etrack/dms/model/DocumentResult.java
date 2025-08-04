package dec.ny.gov.etrack.dms.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class DocumentResult {
  private String resultCode;
  private String resultMessage;
  private int numDocumentsReturned;
  private int numDocumentsMatching;
  private Boolean hasMoreDocuments;
  private int numCEAttached;
  private List<ECMaaSDocumentMetaData> documentsMetaData;
}
