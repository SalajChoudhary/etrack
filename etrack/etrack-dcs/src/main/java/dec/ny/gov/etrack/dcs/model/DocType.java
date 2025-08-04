package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class DocType implements Serializable {
	private static final long serialVersionUID = 469L;
	
	private Integer docTypeId;
	private String docTypeDesc;
	private String docClassName;
	private Integer docClassId;
}
