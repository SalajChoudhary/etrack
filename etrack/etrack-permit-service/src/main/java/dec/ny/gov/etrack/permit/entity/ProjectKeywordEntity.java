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
@Table(name="E_PROJECT_KEYWORD")
public @Data class ProjectKeywordEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_KEYWORD_S")
  @SequenceGenerator(name = "E_PROJECT_KEYWORD_S", sequenceName = "E_PROJECT_KEYWORD_S", allocationSize = 1)
  private Long projectKeywordId;
  private Long keywordId;
  private Long projectId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private Integer systemDetected;
}
