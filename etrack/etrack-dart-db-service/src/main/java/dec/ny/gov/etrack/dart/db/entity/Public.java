package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
@Table(name = "E_PUBLIC")
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Public implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @JsonProperty("applicantId")
  private Long publicId;
  @JsonProperty("applicantType")
  private String publicTypeCode;
  private String displayName;
  private Long edbPublicId;
  private Long projectId;
//  private Integer selectedInEtrackInd;
//  private Integer onlineSubmitterInd;
}
