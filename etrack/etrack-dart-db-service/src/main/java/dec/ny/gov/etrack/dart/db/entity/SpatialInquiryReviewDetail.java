package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

public @Data class SpatialInquiryReviewDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long reviewGroupId;
  private Long inquiryId;
  private String requestorName;
  private String projectName;
  private String mailingAddress;
  private String city;
  private String county;
  private String municipality;
  private String assignedAnalystName;
  private String reviewDueDate;
  private Long spatialInqCategoryId;
  private String spatialInqCategoryDesc;
  private Integer region;
  private String streetAddress;
  private String phoneNumber;
  private String email;
  @Column(name="mailing_address_street_1")
  private String mailingAddressStreet1;
  @Column(name="mailing_address_street_2")
  private String mailingAddressStreet2;
  private String mailingAddressZip;
  private String mailingAddressState;
  private String mailingAddressCity;
  private String extenderName;
  private String efcContact;
  private String dowContact;
  private String projectDescription;
  private String projectSponsor;
  private String leadAgencyName;
  private String leadAgencyContact;
  private String pscDocketNum;
  private String depProjectManager;
  private String developer;
  private String owner;
}
