package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import dec.ny.gov.etrack.dart.db.entity.SearchModel;
import lombok.Data;

public @Data class LastLoadDetails implements Serializable{
	
	private String pLastLoadDate;
	private List<SearchModel> searchModels;

}
