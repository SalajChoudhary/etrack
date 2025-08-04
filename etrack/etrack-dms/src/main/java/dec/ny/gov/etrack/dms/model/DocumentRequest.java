package dec.ny.gov.etrack.dms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class DocumentRequest {
  private String author;
  private String docCreator;
  private String docCategory;
  private String docSubCategory;
  private String documentTitle;
  private String indexDate;
  private String docLastModifier;
  private String source;
  private String projectId;
  private String eTrackDocumentID;
  private String historic;
  private String foilStatus;
  private String deleteFlag;
  private String applicationID;
  private String description;
  private String docCreationType;
  private String renewalNumber;
  private String modificationNumber;
  private String expirationDate;
  private String permitType;
  private String sentTo;
  private String receivedFrom;
  private String emailSubject;
  private String sentDate;
  private String receivedDate;
}
