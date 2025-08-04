package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.DynamicUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_SUBMITTED_DOCUMENT")
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8734627787075899390L;

  @Id
  @Column(name = "DOCUMENT_ID", updatable = false)
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "E_SUBMITTED_DOCUMENT_S")
  @SequenceGenerator(name = "E_SUBMITTED_DOCUMENT_S", sequenceName = "E_SUBMITTED_DOCUMENT_S", allocationSize = 1)
  private Long documentId;

  @Column(name = "EDB_DISTRICT_ID", updatable = false)
  private Long edbDistrictId;

  @Column(name = "ECMAAS_GUID", updatable = false)
  private String ecmaasGUID;
  
  @Column(name = "ACCESS_BY_DEP_ONLY_IND", updatable = true)
  private Integer accessByDEPOnlyInd;

  @Column(name = "DOC_RELEASABLE_CODE", updatable = true)
  private String docReleasableCode;

  @Column(name = "DOCUMENT_TYPE_ID", updatable = true)
  private Integer documentTypeId;

  @Column(name = "DOCUMENT_SUB_TYPE_ID", updatable = true)
  private Integer documentSubTypeId;

  @Column(name = "DOCUMENT_STATE_CODE", updatable = true)
  private String documentStateCode;

  @Column(name = "DOCUMENT_DESC", updatable = true)
  private String documentDesc;

  @Column(name = "DOCUMENT_NM", updatable = true)
  private String documentNm;

  @Column(name = "NON_ETRACK_IND", updatable = true)
  private Integer nonEtrackInd;

  @Column(name = "DOC_SUB_TYPE_OTHER_TXT", updatable = true)
  private String docSubTypeOtherTxt;

  @Column(name = "CREATED_BY_ID", updatable = false)
  private String createdById;
 
  @Column(name = "MODIFIED_BY_ID", updatable = true)
  private String modifiedById;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATE_DATE", updatable = false)
  private Date createDate;

  @Temporal(TemporalType.DATE)
  @Column(name = "MODIFIED_DATE", updatable = true)
  private Date modifiedDate;

  @Column(name = "TRACKED_APPLICATION_ID", updatable = true)
  private String trackedApplicationId;
  
  @OneToMany(mappedBy="submittedDoc",cascade=CascadeType.ALL)
  private List<DocumentFile> docFiles;
  
  @OneToMany(mappedBy="submittedDoc",cascade=CascadeType.ALL, orphanRemoval = true)
  private List<SubmittedDocNonRelReasonDetail> docNonRelReasons;
}
