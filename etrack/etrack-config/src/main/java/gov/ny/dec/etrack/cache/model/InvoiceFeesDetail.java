package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

public @Data class InvoiceFeesDetail implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String invoiceFeeType;
  private Long invoiceFee; 
  private String invoiceFeeDesc;

}
