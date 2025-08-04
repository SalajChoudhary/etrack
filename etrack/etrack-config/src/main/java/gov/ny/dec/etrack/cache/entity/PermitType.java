package gov.ny.dec.etrack.cache.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
//@Table(name = "E_PERMIT_TYPE_CODE")
public @Data class PermitType {
	@Id
	private String permitTypeCode;
	private String permitCategoryDesc;
	private String permitTypeDesc;
	private Integer permitCategoryId;
	private Integer generalPermitInd;
	private String relatedRegularPermitTypeDescForGp;
	private String relatedRegularPermitTypeCodeForGp;
}
