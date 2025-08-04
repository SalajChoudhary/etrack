package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "E_SEARCH_ATTRIBUTE")
public @Data class SearchAttributeEntiry implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SEARCH_ATTRIBUTE_ID")
	private Integer searchAttributeId;

	@Column(name = "ATTRIBUTE_NAME")
	private String  searchAttributeName;

	@Column(name = "UI_DATA_TYPE")
	private Integer attributeDataType;
	
	@Column(name = "LOV_SQL")
	private String lovSql;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="SEARCH_ENTITY_CODE")	 
	private SearchEntity searchEntity = new SearchEntity();

}
