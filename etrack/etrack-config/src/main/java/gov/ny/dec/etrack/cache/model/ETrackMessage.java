package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public @Data class ETrackMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private String messageCode;
	private String messageDesc;
	private Integer messageTypeId;
	private String messageTypeDescription;
	private String createdById;
	private Date createDate;
	private String moifiedById;
	private Date modifiedDate;

}
