package gov.ny.dec.district.etrack.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_DOCUMENT_FILE", schema="ETRACKOWNER")
@NoArgsConstructor
@Getter
@Setter
public  class ETrackDocumentFile implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "DOCUMENT_FILE_ID")
  private Long documentFileId;
  @Column(name = "DOCUMENT_ID", insertable=false, updatable=false)
  private Long documentId;
  @Column(name = "FILE_NBR")
  private Integer fileNbr;
  @Column(name = "FILE_NM")
  private String fileNm;
  @Column(name = "CREATE_DATE")
  private Timestamp createDate;
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
  @Column(name = "CREATED_BY_ID")
  private String createdById;
  @Column(name = "MODIFIED_BY_ID")
  private String modifiedById;
  @Column(name = "FILE_DATE")
  private Timestamp fileDate;
  @ManyToOne
  @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "DOCUMENT_ID", nullable=false)
  private SubmitDocument submitDocument;
}
