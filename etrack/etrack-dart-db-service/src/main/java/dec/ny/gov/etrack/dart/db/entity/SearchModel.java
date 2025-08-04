package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dec.ny.gov.etrack.dart.db.model.SearchAttributeModel;
import lombok.Data;


public @Data class SearchModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer searchEntityCode;
	private String searchEntityDesc;
	List<SearchAttributeModel> attributeModels;
}
