package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_KEYWORD_CATEGORY")
public @Data class KeywordCategoryEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_KEYWORD_CATEGORY_S")
  @SequenceGenerator(name = "E_KEYWORD_CATEGORY_S", sequenceName = "E_KEYWORD_CATEGORY_S", allocationSize = 1)
  private Long keywordCategoryId;
  private String keywordCategory;
  private Integer activeInd;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
