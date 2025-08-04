package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ProjectKeyword implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long keywordId;
  private String keywordText;
  private Long keywordCategoryId;
  private String keywordCategory;
  private Integer projectSelected;
  private Integer systemDetected;
}
