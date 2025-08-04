package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_SEARCH_QRY")
public @Data class SearchQuery implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SEARCH_QRY_S")
  @SequenceGenerator(name = "E_SEARCH_QRY_S", sequenceName = "E_SEARCH_QRY_S", allocationSize = 1)
  @Column(name = "SEARCH_QRY_ID")
  private Long queryId;
  @Column(name="QRY_NAME")
  private String queryName;
  @Column(name="QRY_OWNER")
  private String queryOwner;
  @Column(name="CREATED_BY_ID")
  private String createdById;
  @Column(name="CREATE_DATE")
  private Date createDate;
  @Column(name="MODIFIED_BY_ID")
  private String modifiedById;
  @Column(name="MODIFIED_DATE")
  private Date modifiedDate;
  @Column(name="QRY_TARGET_TYPE")
  private String resultDetails;
  @Column(name="DOCUMENT_SEARCH_TYPE")
  private String documentSearchType;
  @Column(name="COMMENTS")
  private String comments;
  @Column(name="QUERY_STORED_DATA_IND")
  private String persistenceDataType;
  @OneToMany(mappedBy = "searchQuery", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<SearchQueryCondition> searchQueryCondition;
}
