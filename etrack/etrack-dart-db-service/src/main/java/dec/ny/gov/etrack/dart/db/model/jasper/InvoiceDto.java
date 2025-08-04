package dec.ny.gov.etrack.dart.db.model.jasper;

import java.io.Serializable;
import lombok.Data;

public @Data class InvoiceDto implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String invoiceNum;
  private String invoiceDate;
  private String billingSite;
  private String facilityName;
  private String name;
  private String street1;
  private String street2;
  private String citystatezip;
  private String countryCode;
  private String permitType1;
  private String permitTypeFee1;
  private String permitType2;
  private String permitTypeFee2;
  private String permitType3;
  private String permitTypeFee3;  
  private String totalAmount;
  private String decUrl;
  private String payonlinedetail;
  private String payonlinedetail1;
  private String onlinesubmitterurl;
  private String onlineremitto;
  private String analystremitto;
  private String revenueurl;
}
