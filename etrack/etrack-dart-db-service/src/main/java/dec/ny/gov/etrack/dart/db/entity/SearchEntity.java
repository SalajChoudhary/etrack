package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "E_SEARCH_ENTITY_CODE")
public @Data class SearchEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "SEARCH_ENTITY_CODE")
	private Integer searchEntityCode;
	
	@Column(name = "SEARCH_ENTITY_DESC")
	private String searchEntityDesc;
	
	 @Column(name="CREATED_BY_ID")
	  private String createdById;
	  @Column(name="CREATE_DATE")
	  private Date createDate;
	  @Column(name="MODIFIED_BY_ID")
	  private String modifiedById;
	  @Column(name="MODIFIED_DATE")
	  private Date modifiedDate;
	  
	  @OneToMany(mappedBy = "searchEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	  private List<SearchAttributeEntiry> attributeEntiries;

}
