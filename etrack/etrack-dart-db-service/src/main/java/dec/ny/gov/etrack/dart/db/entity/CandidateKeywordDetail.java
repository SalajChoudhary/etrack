package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class CandidateKeywordDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private String uniqueKeywordText;
  private String keywordText;
  private Integer region;
  private Integer count;
}
