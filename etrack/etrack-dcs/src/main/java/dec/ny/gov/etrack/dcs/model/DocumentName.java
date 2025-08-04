package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentName implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long documentId;
  private String displayName;
  private Long supportDocRefId;
  private Integer documentSubTypeId;
  private Integer documentTypeId;
}
