package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="E_SUPPORT_DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
public class SupportDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "E_SUPPORT_DOCUMENT_S")
  @SequenceGenerator(name = "E_SUPPORT_DOCUMENT_S", sequenceName = "E_SUPPORT_DOCUMENT_S", allocationSize = 1)
  private Long documentId;
  private String ecmaasGuid;
  private Integer accessByDepOnlyInd;
  private String docReleasableCode;
  private Integer documentTypeId;
  private Integer documentSubTypeId;
  private String documentStateCode;
  private String refDocumentDesc;
  private String documentDesc;
  private Integer documentSubTypeTitleId;
  private String docSubTypeOtherTxt;
  private String createdById;
  private String modifiedById;
  private Date createDate;
  private Date modifiedDate;
  private Long projectId;
  private String documentNm;
  private Long refDocumentId;
  private Integer addlDocInd;
  private Integer docConfInd;
  private String trackedApplicationId;
  private Integer supportDocCategoryCode;
  @OneToMany(mappedBy="supportDocument", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<SupportDocumentFile> docFiles = new HashSet<>();
  @JsonBackReference
  @OneToMany(mappedBy="supportDocument", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<SupportDocNonRelReasonDetail> docNonRelReasons = new HashSet<>();
  
  public void setDocNonRelReasons(Set<SupportDocNonRelReasonDetail> docNonRelReasons) {
    this.docNonRelReasons.addAll(docNonRelReasons);
  }

  public void setDocFiles(Set<SupportDocumentFile> docFiles) {
    this.docFiles.addAll(docFiles);
  }
  
}
