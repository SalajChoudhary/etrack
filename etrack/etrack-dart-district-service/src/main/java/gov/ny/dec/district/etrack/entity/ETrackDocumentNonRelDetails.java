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
@Table(name = "E_SUBMITTED_DOC_NON_REL_REASON", schema="ETRACKOWNER")
@NoArgsConstructor
@Getter
@Setter
public  class ETrackDocumentNonRelDetails implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "NON_REL_REASON_ID")
  private Integer nonRelReasonId;
  @Column(name = "DOCUMENT_ID", insertable=false, updatable=false)
  private Long documentId;
  @Column(name = "DOC_NON_REL_REASON_CODE")
  private String docNonRelReasonCode;
  @Column(name = "CREATED_BY_ID")
  private String createdById;
  @Column(name = "MODIFIED_BY_ID")
  private String modifiedById;
  @Column(name = "CREATE_DATE")
  private Timestamp createDate;
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
//@JsonManagedReference
  @ManyToOne
  @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "DOCUMENT_ID", nullable=false)
  private SubmitDocument submitDocument;
}
