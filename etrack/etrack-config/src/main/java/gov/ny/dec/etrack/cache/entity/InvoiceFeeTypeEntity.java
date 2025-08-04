package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="E_INVOICE_FEE_TYPE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceFeeTypeEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String invoiceFeeType;
	private Long invoiceFee; 
	private String invoiceFeeDesc;
	private String permitTypeCode;

	@Column(name = "CREATED_BY_ID")
	private String createdById;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIED_BY_ID")
	private String moifiedById;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	private Integer activeInd;
}
