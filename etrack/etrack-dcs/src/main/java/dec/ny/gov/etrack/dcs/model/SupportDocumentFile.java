package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_SUPPORT_DOCUMENT_FILE")
@Getter
@Setter
@NoArgsConstructor
public class SupportDocumentFile implements Serializable {

 
  /**
   * 
   */
  private static final long serialVersionUID = 4699188979013984743L;
  @Id
  @Column(name = "DOCUMENT_FILE_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SUPPORT_DOCUMENT_FILE_S")
  @SequenceGenerator(name = "E_SUPPORT_DOCUMENT_FILE_S", sequenceName = "E_SUPPORT_DOCUMENT_FILE_S",
      allocationSize = 1)
  private Long documentFileId;
  @Column(name = "DOCUMENT_ID",insertable=false, updatable=false)
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
  @JoinColumn(name="DOCUMENT_ID")
  private SupportDocument supportDocument;
}
