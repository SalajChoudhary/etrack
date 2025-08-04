package dec.ny.gov.etrack.dms.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class DMSDocumentResponse {

  private String resultCode;
  private String resultMessage;
  private Integer numDocumentsReturned;
  private Integer numDocumentsMatching;
  private Boolean hasMoreDocuments;
  private Integer numCEAttached;
  private List<DMSDocumentMetaData> documentsMetaData;

}
