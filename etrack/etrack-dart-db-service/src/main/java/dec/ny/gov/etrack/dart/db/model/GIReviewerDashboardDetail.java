package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GIReviewerDashboardDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long giDocumentReviewId;
  private Long inquiryId;
  private String inquiryTypeDesc;
  private String municipality;
  private String county;
  private String analystName;
  private String dueDate;
  private String phoneNumber;
  private String email;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String zip;
  private String requestorName;
  private String extenderName;
  private String dowContact;
  private String efcContact;
  private String projectName;
  private String projectDescription;
  private String projectSponsor;
  private String leadAgencyName;
  private String leadAgencyContact;
  private String pscDocketNum;
  private String depProjectManager;
  private String developer;
  private String owner;
}
