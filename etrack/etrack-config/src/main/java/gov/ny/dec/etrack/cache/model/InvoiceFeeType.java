package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvoiceFeeType implements Serializable {

	private static final long serialVersionUID = -9170791040428513524L;

	private String invoiceFeeType;
	private Long invoiceFee; 
	private String invoiceFeeDesc;
	private String permitTypeCode;
//	private boolean newInvoiceFee;
	private Integer activeInd;
}
