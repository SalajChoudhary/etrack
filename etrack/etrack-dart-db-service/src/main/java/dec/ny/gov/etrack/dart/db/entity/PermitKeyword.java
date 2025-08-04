package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PermitKeyword implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Long permitKeywordId;
  private String permitTypeCode;
  private String permitTypeDesc;
  private Long keywordId;
  private String keywordText;
  private String startDate;
  private String endDate;
  private Long keywordCategoryId;
  private String keywordCategory;
}
