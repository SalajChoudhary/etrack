package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.Date;
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
@Table(name="E_SPATIAL_INQ_DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
public class SpatialInquiryDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "E_SPATIAL_INQ_DOCUMENT_S")
  @SequenceGenerator(name = "E_SPATIAL_INQ_DOCUMENT_S", sequenceName = "E_SPATIAL_INQ_DOCUMENT_S", allocationSize = 1)
  private Long documentId;
  private Long inquiryId;
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
  private String documentNm;
  private Long refDocumentId;
  private Integer addlDocInd;
  private Integer docConfInd;
  @OneToMany(mappedBy="spatialInqDocument", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<SpatialInquiryFile> spatialInquiryFiles;
  @JsonBackReference
  @OneToMany(mappedBy="spatialInqDocument", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<SpatialInqDocNonRelReasonDetail> spaInqDocNonRelReasonDetails;

}
