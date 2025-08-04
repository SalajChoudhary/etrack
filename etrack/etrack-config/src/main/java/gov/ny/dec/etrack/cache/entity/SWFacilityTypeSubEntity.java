package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name = "E_SW_FACILITY_SUB_TYPE")
public @Data class SWFacilityTypeSubEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_sw_facility_sub_type_s")
	@SequenceGenerator(name = "e_sw_facility_sub_type_s", sequenceName = "e_sw_facility_sub_type_s", allocationSize = 1)
	@Column(name = "SW_FACILITY_SUB_TYPE_ID")
	private Integer swFacilitySubTypeId;
	
	@Column(name = "SW_FACILITY_TYPE_ID")
	private Integer swFacilityTypeId;
	
	@Transient
	private String facilityTypeDesc;
	
	@Transient
	private String ftReg;
	
	@Column(name = "SW_REGULATION_CODE")
	private String subReg;
	
	@Column(name = "SUB_TYPE_DESCRIPTION")
	private String subTypeDescription;

	@Column(name="CREATED_BY_ID")
	private String createdById;

	@Column(name="CREATE_DATE")
	private Date createDate;

	@Column(name="MODIFIED_BY_ID")
	private String moifiedById;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;
	
	private Integer activeInd;
}
