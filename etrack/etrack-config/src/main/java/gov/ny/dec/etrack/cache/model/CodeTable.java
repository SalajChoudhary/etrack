package gov.ny.dec.etrack.cache.model;

import java.util.List;

import lombok.Data;

public @Data class CodeTable {

	private String categoryName;
	private List<String> tableNames; 
}
