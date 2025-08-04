package gov.ny.dec.district.etrack.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "E_SUBMITTED_DOCUMENT", schema = "ETRACKOWNER")
@NoArgsConstructor
@Getter
@Setter
public class SubmitDocument  implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "DOCUMENT_ID")
  private Long documentId;
  @Column(name = "EDB_DISTRICT_ID")
  private Long edbDistrictId;
  @Column(name = "ECMAAS_GUID")
  private String ecMaaSGuid;
  @Column(name = "ACCESS_BY_DEP_ONLY_IND")
  private String accessByDepOnlyInd;
  @Column(name = "DOC_RELEASABLE_CODE")
  private String docReleasableCode;
  @Column(name = "DOCUMENT_TYPE_ID")
  private Integer documentTypeId;
  @Column(name = "DOCUMENT_SUB_TYPE_ID")
  private Integer documentSubTypeId;
  @Column(name = "DOCUMENT_STATE_CODE")
  private String documentStateCode;
  @Column(name = "DOCUMENT_DESC")
  private String documentDesc;
  @Column(name = "DOCUMENT_NM")
  private String documentNm;
  @Column(name = "NON_ETRACK_IND")
  private Integer nonEtrackInd;
  @Column(name = "DOC_SUB_TYPE_OTHER_TXT")
  private String docSubTypeOtherTxt;
  @Column(name = "CREATED_BY_ID")
  private String createdById;
  @Column(name = "MODIFIED_BY_ID")
  private String modifiedById;
  @Column(name = "CREATE_DATE")
  private Timestamp createDate;
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
  @Column(name = "TRACKED_APPLICATION_ID")
  private String trackedApplicationId;
  @OneToMany(mappedBy="submitDocument", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//  @JsonBackReference
  private Set<ETrackDocumentFile> eTrackDocumentFile;
  @JsonBackReference
  @OneToMany(mappedBy="submitDocument", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<ETrackDocumentNonRelDetails> eTrackDocumentNonRelDetails;
}
