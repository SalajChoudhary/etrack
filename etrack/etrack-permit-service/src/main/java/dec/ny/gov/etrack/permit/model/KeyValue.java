package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class KeyValue implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uniquekey;
	private String value;

}
