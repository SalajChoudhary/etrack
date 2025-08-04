package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MaintanenceCodeTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableName;
	private List<KeyValue> keyValues;
}
