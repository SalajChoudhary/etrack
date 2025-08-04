package gov.ny.dec.etrack.cache.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_DOCUMENT_SUB_TYPE_TITLE")
public @Data class DocumentSubTypeTitle {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_DOCUMENT_SUB_TYPE_TITLE_S")
  @SequenceGenerator(name = "E_DOCUMENT_SUB_TYPE_TITLE_S", sequenceName = "E_DOCUMENT_SUB_TYPE_TITLE_S", allocationSize = 1)
  private Long documentSubTypeTitleId;
  private Long documentSubTypeId;
  private Long documentTitleId;
  private Integer activeInd;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
