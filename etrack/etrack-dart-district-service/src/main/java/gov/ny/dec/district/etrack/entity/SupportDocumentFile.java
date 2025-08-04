package gov.ny.dec.district.etrack.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_SUPPORT_DOCUMENT_FILE", schema = "ETRACKOWNER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportDocumentFile implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name="DOCUMENT_FILE_ID")
  private Long documentFileId;
  @Column(name = "DOCUMENT_ID", insertable = false, updatable = false)
  private Long documentId;
  @Column(name = "FILE_NBR")
  private Integer fileNumber;
  @Column(name = "FILE_NM")
  private String fileName;
  @Temporal(TemporalType.DATE)
  @Column(name = "CREATE_DATE")
  private Date createdDate;
  @Temporal(TemporalType.DATE)
  @Column(name = "MODIFIED_DATE")
  private Date modifiedDate;
  @Temporal(TemporalType.DATE)
  @Column(name = "FILE_DATE")
  private Date fileDate;
  @Column(name = "CREATED_BY_ID")
  private String createdById;
  @Column(name = "MODIFIED_BY_ID")
  private String modifiedById;
  @ManyToOne
  @JoinColumn(name="DOCUMENT_ID", referencedColumnName = "DOCUMENT_ID")
  private SupportDocument supportDocument;
}
