package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_SEARCH_QRY_CONDN")
public @Data class SearchQueryCondition  implements Serializable {
	
	 /**
	   * 
	   */
	  private static final long serialVersionUID = 1L;
	  
	  @Id
	  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SEARCH_QRY_CONDN_S")
	  @SequenceGenerator(name = "E_SEARCH_QRY_CONDN_S", sequenceName = "E_SEARCH_QRY_CONDN_S", allocationSize = 1)
	  @Column(name = "SEARCH_QRY_CONDN_ID")
	  private Long searchQueryConditionId;
	  @Column(name="CONDN_OPERATOR")
	  private String conditionOperator;
	  @Column(name="SEARCH_ATTRIBUTE_ID")
	  private Long searchAttributeId;	  
	  @Column(name="COMPARISON_OPERATOR")
	  private String comparisonOperator;
	  @Column(name="COMPARISON_VALUE")
	  private String comparisonValue;
	  @Column(name="SEARCH_ENTITY_CODE")
	  private Integer searchEntityCode;
	  @Column(name="CONDN_ORDER")
	  private Integer searchAttributeOrder;
	  @Column(name="CREATED_BY_ID")
	  private String createdById;
	  @Column(name="CREATE_DATE")
	  private Date createDate;
	  @Column(name="MODIFIED_BY_ID")
	  private String modifiedById;
	  @Column(name="MODIFIED_DATE")
	  private Date modifiedDate;
	  @ManyToOne(cascade = CascadeType.ALL)
	  @JoinColumn(name="SEARCH_QRY_ID")	  
	  private SearchQuery searchQuery;

}



