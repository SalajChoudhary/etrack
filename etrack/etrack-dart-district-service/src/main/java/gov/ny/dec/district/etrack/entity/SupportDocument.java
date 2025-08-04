package gov.ny.dec.district.etrack.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="E_SUPPORT_DOCUMENT", schema = "ETRACKOWNER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportDocument implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name="DOCUMENT_ID")
  private Long documentId;
  @Column(name="ECMAAS_GUID")
  private String ecmaasGuid;
  @Column(name="ACCESS_BY_DEP_ONLY_IND")
  private Integer accessByDepOnlyInd;
  @Column(name="DOC_RELEASABLE_CODE")
  private String docReleasableCode;
  @Column(name="DOCUMENT_TYPE_ID")
  private Integer documentTypeId;
  @Column(name="DOCUMENT_SUB_TYPE_ID")
  private Integer documentSubTypeId;
  @Column(name="DOCUMENT_STATE_CODE")
  private String documentStateCode;
  @Column(name="DOCUMENT_DESC")
  private String documentDesc;
  @Column(name="DOCUMENT_SUB_TYPE_TITLE_ID")
  private Long documentSubTypeTitleId;
  @Column(name="DOC_SUB_TYPE_OTHER_TXT")
  private String docSubTypeOtherTxt;
  @Column(name="TRACKED_APPLICATION_ID")
  private String trackedApplicationId;
  @Column(name="CREATED_BY_ID")
  private String createdById;
  @Column(name="MODIFIED_BY_ID")
  private String modifiedById;
  @Column(name="CREATE_DATE")
  private Date createDate;
  @Column(name="MODIFIED_DATE")
  private Date modifiedDate;
  @Column(name="PROJECT_ID")
  private Long projectId;
  @Column(name="DOCUMENT_NM")
  private String documentNm;
  @Column(name="REF_DOCUMENT_ID")
  private Long refDocumentId;
  @Column(name="ADDL_DOC_IND")
  private Integer addlDocInd;
  @Column(name="DOC_CONF_IND")
  private Integer docConfInd;
  @Column(name="ARC_PRG_QUERY_RESULT_ID")
  private Long arcPrgQueryResultId;
  @Column(name="ARCHIVE_COMPLETED_IND")
  private Integer archiveCompletedInd;
  @OneToMany(mappedBy="supportDocument", fetch = FetchType.EAGER)
  private Set<SupportDocumentFile> docFiles;
  @OneToMany(mappedBy="supportDocument", fetch = FetchType.EAGER)
  private Set<SupportDocNonRelReasonDetail> docNonRelReasons;
}
