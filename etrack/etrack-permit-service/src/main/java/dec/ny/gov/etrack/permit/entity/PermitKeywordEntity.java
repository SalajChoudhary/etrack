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

@Data
@Entity
@Table(name = "E_PERMIT_KEYWORD")
public class PermitKeywordEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PERMIT_KEYWORD_S")
  @SequenceGenerator(name = "E_PERMIT_KEYWORD_S", sequenceName = "E_PERMIT_KEYWORD_S", allocationSize = 1)
  private Long permitKeywordId;
  private Long keywordId;
  private Integer activeInd;
  private Date startDate;
  private Date endDate;
  private String permitTypeCode;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;

}
