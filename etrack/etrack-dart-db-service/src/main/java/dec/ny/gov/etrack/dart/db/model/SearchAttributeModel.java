package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;


public @Data class SearchAttributeModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer searchAttributeId;

	private String  searchAttributeName;

	private Integer attributeDataType;

	private String  attributeDataName;
	
	private List<String> attributes = new ArrayList<>();

}
