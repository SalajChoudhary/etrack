package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
public @Data class ReviewDocument implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long documentId;
  @JsonProperty("documentName")
  private String documentNm;
  @JsonProperty("description")
  private String documentDesc;
  @JsonProperty("documentType")
  private String documentTypeDesc;
  @JsonProperty("subType")
  private String documentSubTypeDesc; 
  @JsonProperty("files")
  private String fileCount;
  private String uploadDate;
}
